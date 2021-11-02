package com.tianma.tweaks.miui.xp.hook.systemui.statusbar.v20201109

import com.tianma.tweaks.miui.data.sp.XPrefContainer
import com.tianma.tweaks.miui.utils.logE
import com.tianma.tweaks.miui.utils.logI
import com.tianma.tweaks.miui.xp.hook.BaseSubHook
import com.tianma.tweaks.miui.xp.utils.appinfo.AppInfo
import com.tianma.tweaks.miui.xp.wrapper.MethodHookWrapper
import com.tianma.tweaks.miui.xp.wrapper.XposedWrapper
import de.robv.android.xposed.XposedHelpers

/**
 * StatusBarSignalPolicy Hook, 用于处理 隐藏 VPN 图标
 * 适用版本 MIUISystemUI(versionCode >= 202011090)
 */
class StatusBarSignalPolicyHook20201109(
    classLoader: ClassLoader,
    appInfo: AppInfo
) : BaseSubHook(classLoader, appInfo) {

    companion object {
        private const val CLASS_STATUS_BAR_POLICY = "com.android.systemui.statusbar.phone.StatusBarSignalPolicy"
    }

    // private val mHideVpnIcon: Boolean = XSPUtils.isHideVpnIcon(xsp)
    private val mHideVpnIcon: Boolean = XPrefContainer.isHideVpnIcon

    override fun startHook() {

        try {
            logI("Hooking StatusBarSignalPolicy...")

            if (mHideVpnIcon) {
                hookUpdateVpn()
            }

        } catch (t: Throwable) {
            logE("Error occurs when hook StatusBarSignalPolicy", t)
        }

    }

    private fun hookUpdateVpn() {
        XposedWrapper.findAndHookMethod(
                CLASS_STATUS_BAR_POLICY,
                mClassLoader,
                "updateVpn",
                object : MethodHookWrapper() {
                    override fun after(param: MethodHookParam?) {
                        param?.also {
                            val thisObj = param.thisObject
                            val mSlotVpn = XposedHelpers.getObjectField(thisObj, "mSlotVpn")
                            val mIconController = XposedHelpers.getObjectField(thisObj, "mIconController")
                            XposedHelpers.callMethod(mIconController, "setIconVisibility", mSlotVpn, false)
                        }
                    }
                }
        )
    }

}