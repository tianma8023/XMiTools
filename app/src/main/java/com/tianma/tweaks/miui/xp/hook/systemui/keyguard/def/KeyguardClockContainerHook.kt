package com.tianma.tweaks.miui.xp.hook.systemui.keyguard.def

import android.content.BroadcastReceiver
import android.content.IntentFilter
import android.os.UserHandle
import android.widget.FrameLayout
import com.tianma.tweaks.miui.data.sp.XPrefContainer
import com.tianma.tweaks.miui.utils.logD
import com.tianma.tweaks.miui.utils.logE
import com.tianma.tweaks.miui.xp.hook.BaseSubHook
import com.tianma.tweaks.miui.xp.utils.appinfo.AppInfo
import com.tianma.tweaks.miui.xp.utils.appinfo.AppVersionConst
import com.tianma.tweaks.miui.xp.wrapper.MethodHookWrapper
import com.tianma.tweaks.miui.xp.wrapper.XposedWrapper
import de.robv.android.xposed.XposedHelpers

/**
 * 锁屏时钟容器 Hook
 * 适用版本 9.4.x
 */
class KeyguardClockContainerHook(classLoader: ClassLoader?, appInfo: AppInfo?) :
    BaseSubHook(classLoader, appInfo) {

    companion object {
        private const val CLASS_KEYGUARD_CLOCK_CONTAINER_OLD =
            "com.android.keyguard.KeyguardClockContainer"

        private const val CLASS_KEYGUARD_CLOCK_CONTAINER_NEW =
            "com.android.keyguard.clock.KeyguardClockContainer"

        private const val CLASS_DEPENDENCY = "com.android.systemui.Dependency"
    }

    private val mShowHorizontalSec: Boolean = XPrefContainer.showSecInKeyguardHorizontal
    private val mShowVerticalSec: Boolean = XPrefContainer.showSecInKeyguardVertical

    private var mKeyguardClockContainerClass: Class<*>? = null

    override fun startHook() {
        if (!mShowHorizontalSec && !mShowVerticalSec) {
            return
        }
        try {
            logD("Hooking KeyguardClockContainerHook...")
            mKeyguardClockContainerClass =
                if (mAppInfo!!.versionCode >= AppVersionConst.SYSTEM_UI_V201912130) {
                    XposedHelpers.findClass(CLASS_KEYGUARD_CLOCK_CONTAINER_NEW, mClassLoader)
                } else {
                    XposedHelpers.findClass(CLASS_KEYGUARD_CLOCK_CONTAINER_OLD, mClassLoader)
                }
            hookOnAttachedToWindow()
        } catch (t: Throwable) {
            logE("Error occurs when hook KeyguardClockContainerHook", t)
        }
    }

    // com.android.keyguard.KeyguardClockContainer#onAttachedToWindow()
    private fun hookOnAttachedToWindow() {
        XposedWrapper.findAndHookMethod(mKeyguardClockContainerClass,
            "onAttachedToWindow",
            object : MethodHookWrapper() {
                override fun before(param: MethodHookParam) {
                    val clockContainer = param.thisObject as FrameLayout
                    val parent = clockContainer.parent
                    XposedHelpers.callMethod(parent, "onAttachedToWindow")

                    val filter = IntentFilter()
                    // 目的: 取消注册 TIME_TICK 事件
                    // filter.addAction("android.intent.action.TIME_TICK");
                    filter.addAction("android.intent.action.TIME_SET")
                    filter.addAction("android.intent.action.TIMEZONE_CHANGED")

                    val mIntentReceiver = XposedHelpers.getObjectField(
                        clockContainer,
                        "mIntentReceiver"
                    ) as BroadcastReceiver

                    val userHandleAll = XposedHelpers.getStaticObjectField(UserHandle::class.java, "ALL")
                    val dependencyClass = XposedHelpers.findClass(CLASS_DEPENDENCY, mClassLoader)
                    val timeTickHandler = XposedHelpers.getStaticObjectField(dependencyClass, "TIME_TICK_HANDLER")
                    val handler =
                        XposedHelpers.callStaticMethod(dependencyClass, "get", timeTickHandler)

                    XposedHelpers.callMethod(
                        clockContainer.context,
                        "registerReceiverAsUser",
                        mIntentReceiver,
                        userHandleAll,
                        filter,
                        null,
                        handler
                    )

                    XposedHelpers.callMethod(clockContainer, "registerDualClockObserver")
                    XposedHelpers.callMethod(clockContainer, "registerClockPositionObserver")

                    param.result = null
                }
            })
    }
}