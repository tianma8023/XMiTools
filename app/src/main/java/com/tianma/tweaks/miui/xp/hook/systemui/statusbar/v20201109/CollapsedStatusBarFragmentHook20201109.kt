package com.tianma.tweaks.miui.xp.hook.systemui.statusbar.v20201109

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import com.tianma.tweaks.miui.data.sp.XPrefContainer
import com.tianma.tweaks.miui.utils.callMethod
import com.tianma.tweaks.miui.utils.logD
import com.tianma.tweaks.miui.utils.logE
import com.tianma.tweaks.miui.xp.hook.BaseSubHook
import com.tianma.tweaks.miui.xp.hook.systemui.screen.ScreenBroadcastManager
import com.tianma.tweaks.miui.xp.hook.systemui.screen.SimpleScreenListener
import com.tianma.tweaks.miui.xp.utils.appinfo.AppInfo
import com.tianma.tweaks.miui.xp.wrapper.MethodHookWrapper
import com.tianma.tweaks.miui.xp.wrapper.XposedWrapper
import de.robv.android.xposed.XposedHelpers

class CollapsedStatusBarFragmentHook20201109(classLoader: ClassLoader?, appInfo: AppInfo?) :
    BaseSubHook(classLoader, appInfo) {

    companion object {
        private const val CLASS_STATUS_BAR_FRAGMENT =
            "com.android.systemui.statusbar.phone.CollapsedStatusBarFragment"
    }

    private val mSignalAlignLeft: Boolean = XPrefContainer.isSignalAlignLeft
    private val mAlwaysShowStatusBarClock: Boolean = XPrefContainer.alwaysShowStatusBarClock

    // 是否可以展示状态栏时钟 (锁屏状态下不展示，梁平状态下有可能展示)
    private var canShowStatusBarClock = false

    override fun startHook() {
        try {
            logD("Hooking CollapsedStatusBarFragment... ")
            if (mSignalAlignLeft) {
                // nothing
            }
            if (mAlwaysShowStatusBarClock) {
                hookOnViewCreated()
                hookHideClock()
            }
        } catch (t: Throwable) {
            logE("Error occurs when hook CollapsedStatusBarFragment", t)
        }
    }

    // CollapsedStatusBarFragment#onViewCreated()
    private fun hookOnViewCreated() {
        XposedWrapper.findAndHookMethod(
            CLASS_STATUS_BAR_FRAGMENT,
            mClassLoader,
            "onViewCreated",
            View::class.java,
            Bundle::class.java,
            object : MethodHookWrapper() {
                override fun after(param: MethodHookParam) {
                    val phoneStatusBarView =
                        XposedHelpers.getObjectField(param.thisObject, "mStatusBar") as ViewGroup

                    val context = phoneStatusBarView.context
                    val statusBarFragment = param.thisObject

                    val screenListener = object : SimpleScreenListener() {
                        override fun onUserPresent() {
                            // 屏幕解锁时，可以展示状态栏时钟
                            canShowStatusBarClock = true

                            // 展示时钟
                            statusBarFragment.callMethod(
                                "showClock",
                                arrayOf(Boolean::class.java),
                                arrayOf(true)
                            )
                        }

                        override fun onScreenOff() {
                            // 屏幕锁定时，不可以展示状态栏时钟
                            canShowStatusBarClock = false

                            statusBarFragment.callMethod(
                                "hideClock",
                                arrayOf(Boolean::class.java),
                                arrayOf(true)
                            )
                        }
                    }
                    ScreenBroadcastManager.getInstance(context).registerListener(screenListener)
                }
            })
    }

    private fun hookHideClock() {
        XposedWrapper.findAndHookMethod(
            CLASS_STATUS_BAR_FRAGMENT,
            mClassLoader,
            "hideClock",
            Boolean::class.java,
            object : MethodHookWrapper() {
                override fun before(param: MethodHookParam?) {
                    param ?: return
                    // 拦截 hideClock()
                    if (canShowStatusBarClock) {
                        param.result = null
                    }
                }
            }
        )
    }

}