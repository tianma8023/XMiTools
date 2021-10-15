package com.tianma.tweaks.miui.utils;

/**
 * 当前Xposed模块相关工具类
 */
public class ModuleUtils {

    private ModuleUtils() {
    }

    /**
     * 当前模块是否在Xposed Installer中被启用
     */
    public static boolean isModuleActive() {
        return getModuleVersion() > -1;
    }

    /**
     * 返回模块版本 <br/>
     * 注意：该方法被本模块Hook住，返回的值是 BuildConfig.MODULE_VERSION，如果没被Hook则返回-1
     */
    public static int getModuleVersion() {
        XLog.d("getModuleVersion()");
        return -1;
    }
}
