package com.tianma.tweaks.miui.xp.hook

import kotlin.Throws
import de.robv.android.xposed.IXposedHookZygoteInit.StartupParam
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam

interface IHook {
    @Throws(Throwable::class)
    fun initZygote(startupParam: StartupParam)

    @Throws(Throwable::class)
    fun onLoadPackage(lpparam: LoadPackageParam)
}