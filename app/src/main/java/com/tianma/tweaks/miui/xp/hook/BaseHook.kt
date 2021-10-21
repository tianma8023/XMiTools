package com.tianma.tweaks.miui.xp.hook

import de.robv.android.xposed.IXposedHookZygoteInit.StartupParam
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam

open class BaseHook : IHook {
    @Throws(Throwable::class)
    override fun initZygote(startupParam: StartupParam?) {
    }

    open fun shouldHookInitZygote(): Boolean {
        return false
    }

    @Throws(Throwable::class)
    override fun onLoadPackage(lpparam: LoadPackageParam?) {
    }

    open fun shouldHookOnLoadPackage(): Boolean {
        return true
    }
}