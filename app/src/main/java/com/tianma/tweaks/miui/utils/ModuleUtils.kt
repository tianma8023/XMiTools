package com.tianma.tweaks.miui.utils

/**
 * 当前Xposed模块相关工具类
 */
object ModuleUtils {
    /**
     * 当前模块是否在Xposed Installer中被启用
     */
    @JvmStatic
    fun isModuleActive(): Boolean = getModuleVersion() > -1

    /**
     * 返回模块版本 <br></br>
     * 注意：该方法被本模块Hook住，返回的值是 BuildConfig.MODULE_VERSION，如果没被Hook则返回-1
     */
    @JvmStatic
    fun getModuleVersion(): Int {
        XLog.d("getModuleVersion()")
        return -1
    }
}