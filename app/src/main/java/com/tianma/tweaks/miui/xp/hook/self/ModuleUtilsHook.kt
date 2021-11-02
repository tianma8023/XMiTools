package com.tianma.tweaks.miui.xp.hook.self

import com.tianma.tweaks.miui.BuildConfig
import com.tianma.tweaks.miui.utils.ModuleUtils
import com.tianma.tweaks.miui.utils.logE
import com.tianma.tweaks.miui.utils.logI
import com.tianma.tweaks.miui.xp.hook.BaseHook
import com.tianma.tweaks.miui.xp.wrapper.XposedWrapper
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam

/**
 * Hook class ModuleUtils
 */
class ModuleUtilsHook : BaseHook() {

    companion object {
        private const val MI_TWEAKS_PACKAGE = BuildConfig.APPLICATION_ID
        private const val MODULE_VERSION = BuildConfig.MODULE_VERSION
    }

    @Throws(Throwable::class)
    override fun onLoadPackage(lpparam: LoadPackageParam) {
        if (MI_TWEAKS_PACKAGE == lpparam.packageName) {
            try {
                logI("Hooking current Xposed module status...")
                hookModuleUtils(lpparam)
            } catch (e: Throwable) {
                logE("Failed to hook current Xposed module status.")
            }
        }
    }

    @Throws(Throwable::class)
    private fun hookModuleUtils(lpparam: LoadPackageParam) {
        val className = ModuleUtils::class.java.name
        XposedWrapper.findAndHookMethod(
            className, lpparam.classLoader,
            "getModuleVersion",
            XC_MethodReplacement.returnConstant(MODULE_VERSION)
        )
    }
}