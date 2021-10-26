package com.tianma.tweaks.miui.xp.hook.systemui.keyguard.v20190507

import android.content.Context
import android.content.Intent
import com.tianma.tweaks.miui.data.sp.XPrefContainer
import com.tianma.tweaks.miui.utils.logD
import com.tianma.tweaks.miui.utils.logE
import com.tianma.tweaks.miui.xp.hook.BaseSubHook
import com.tianma.tweaks.miui.xp.hook.systemui.screen.IntentAction
import com.tianma.tweaks.miui.xp.utils.appinfo.AppInfo
import com.tianma.tweaks.miui.xp.wrapper.MethodHookWrapper
import com.tianma.tweaks.miui.xp.wrapper.XposedWrapper

/**
 * MIUI设置页面 - 选择锁屏时钟界面 Hook
 * 适用版本 9.5.7+
 */
class ChooseKeyguardClockActivityHook(classLoader: ClassLoader?, appInfo: AppInfo?) :
    BaseSubHook(classLoader, appInfo) {

    companion object {
        private const val CLASS_CHOOSE_KEYGUARD_CLOCK_ACTIVITY =
            "com.android.keyguard.settings.ChooseKeyguardClockActivity"
    }

    private val mShowVerticalSec: Boolean = XPrefContainer.showSecInKeyguardVertical
    private val mShowHorizontalSec: Boolean = XPrefContainer.showSecInKeyguardHorizontal

    override fun startHook() {
        if (!mShowHorizontalSec && !mShowVerticalSec) {
            return
        }
        try {
            logD("Hooking ChooseKeyguardClockActivity...")
            hookOnStop()
        } catch (t: Throwable) {
            logE("Error occurs when hook ChooseKeyguardClockActivity", t)
        }
    }

    // com.android.keyguard.setting.ChooseKeyguardClockActivity#onStop()
    private fun hookOnStop() {
        XposedWrapper.findAndHookMethod(
            CLASS_CHOOSE_KEYGUARD_CLOCK_ACTIVITY,
            mClassLoader,
            "onStop",
            object : MethodHookWrapper() {
                override fun before(param: MethodHookParam) {
                    val context = param.thisObject as Context
                    val intent = Intent(IntentAction.KEYGUARD_STOP_TIME_TICK)
                    context.sendBroadcast(intent)
                }
            })
    }
}