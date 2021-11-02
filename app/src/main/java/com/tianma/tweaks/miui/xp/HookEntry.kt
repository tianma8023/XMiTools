package com.tianma.tweaks.miui.xp

import com.tianma.tweaks.miui.xp.hook.BaseHook
import com.tianma.tweaks.miui.xp.hook.self.ModuleUtilsHook
import com.tianma.tweaks.miui.xp.hook.systemui.SystemUIHook
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.IXposedHookZygoteInit
import de.robv.android.xposed.IXposedHookZygoteInit.StartupParam
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam

class HookEntry : IXposedHookLoadPackage, IXposedHookZygoteInit {

    private val hookList: List<BaseHook> by lazy {
        mutableListOf<BaseHook>().apply {
            add(ModuleUtilsHook()) // Self Hook
            add(SystemUIHook()) // SystemUI Hook
            // add(new MiuiLauncherHook()); // Miui Launcher Hook
        }
    }

    @Throws(Throwable::class)
    override fun initZygote(startupParam: StartupParam?) {
        startupParam ?: return
        for (hook in hookList) {
            if (hook.shouldHookInitZygote()) {
                hook.initZygote(startupParam)
            }
        }
    }

    @Throws(Throwable::class)
    override fun handleLoadPackage(lpparam: LoadPackageParam?) {
        lpparam ?: return
        for (hook in hookList) {
            if (hook.shouldHookOnLoadPackage()) {
                hook.onLoadPackage(lpparam)
            }
        }
    }
}