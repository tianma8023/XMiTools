package com.tianma.tweaks.miui.xp.hook.launcher

import com.tianma.tweaks.miui.data.sp.XPrefContainer.mainSwitchEnable
import com.tianma.tweaks.miui.utils.logI
import com.tianma.tweaks.miui.utils.rom.MiuiUtils
import com.tianma.tweaks.miui.xp.hook.BaseHook
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam

class MiuiLauncherHook : BaseHook() {

    companion object {
        const val PACKAGE_NAME = "com.miui.home"
    }

    @Throws(Throwable::class)
    override fun onLoadPackage(lpparam: LoadPackageParam) {
        if (PACKAGE_NAME == lpparam.packageName) {
            logI("Hooking MIUI Launcher...")
            val classLoader = lpparam.classLoader
            if (mainSwitchEnable) {
                if (!MiuiUtils.isMiui()) {
                    return
                }
                WorkSpaceHook(classLoader).startHook()
            }
        }
    }
}