package com.tianma.tweaks.miui.xp.hook.systemui.statusbar.v20201109

import android.widget.TextView
import com.tianma.tweaks.miui.utils.XLog
import com.tianma.tweaks.miui.utils.XSPUtils
import com.tianma.tweaks.miui.xp.hook.BaseSubHook
import com.tianma.tweaks.miui.xp.utils.appinfo.AppInfo
import com.tianma.tweaks.miui.xp.wrapper.MethodHookWrapper
import com.tianma.tweaks.miui.xp.wrapper.XposedWrapper
import de.robv.android.xposed.XSharedPreferences
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers

/**
 * StatusBarMobileView Hook, 用于 hook 自定义网络类型
 * 适用版本 MIUISystemUI(versionCode >= 202011090)
 */
class StatusBarMobileViewHook20201109(
        classLoader: ClassLoader,
        xsp: XSharedPreferences,
        appInfo: AppInfo
) : BaseSubHook(classLoader, xsp, appInfo) {

    companion object {
        private const val CLASS_STATUS_BAR_MOBILE_VIEW = "com.android.systemui.statusbar.StatusBarMobileView"
    }

    private val isCustomNetworkTypeEnabled = XSPUtils.isCustomMobileNetworkEnabled(xsp)
    private var customNetworkType: String = ""

    init {
        if (isCustomNetworkTypeEnabled) {
            customNetworkType = XSPUtils.customMobileNetwork(xsp)
        }
    }

    override fun startHook() {
        try {
            XLog.i("Hooking StatusBarMobileView...")

            if (isCustomNetworkTypeEnabled) {
                hookUpdateState()
            }

        }catch (t: Throwable) {
            XLog.e("Error occurs when hook StatusBarMobileView", t)
        }
    }

    private fun hookUpdateState() {
        val targetMethod = XposedWrapper.findMethodByNameIfExists(
                CLASS_STATUS_BAR_MOBILE_VIEW,
                mClassLoader,
                "updateState"
        )

        targetMethod?.also {
            XposedBridge.hookMethod(
                    targetMethod,
                    object: MethodHookWrapper() {
                        override fun after(param: MethodHookParam?) {
                            param?.also {
                                val thisObj = param.thisObject
                                val mMobileType = XposedHelpers.getObjectField(thisObj, "mMobileType") as TextView
                                mMobileType.text = customNetworkType
                            }
                        }
                    }
            )
        }
    }


}