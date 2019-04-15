package com.tianma.tweaks.miui.utils;

import android.util.Log;

/**
 * 当前Xposed模块相关工具类
 */
public class ModuleUtils {

    private ModuleUtils() {
    }

    /**
     * 当前模块是否在XposedInstaller中被启用
     */
    public static boolean isModuleActive() {
        Log.d("Something", "Not important");
        return false;
    }
}
