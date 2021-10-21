package com.tianma.tweaks.miui.xp.hook

import kotlin.jvm.JvmOverloads
import com.tianma.tweaks.miui.utils.rom.MiuiVersion
import com.tianma.tweaks.miui.xp.utils.appinfo.AppInfo

abstract class BaseSubHook @JvmOverloads constructor(
    val mClassLoader: ClassLoader?,
    val mAppInfo: AppInfo? = null,
    val mMiuiVersion: MiuiVersion? = null
) {

    abstract fun startHook()

}