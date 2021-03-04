package com.tianma.tweaks.miui.xp.hook.self;


import com.tianma.tweaks.miui.BuildConfig;
import com.tianma.tweaks.miui.utils.ModuleUtils;
import com.tianma.tweaks.miui.utils.XLog;
import com.tianma.tweaks.miui.xp.hook.BaseHook;

import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static com.tianma.tweaks.miui.xp.wrapper.XposedWrapper.findAndHookMethod;

/**
 * Hook class ModuleUtils
 */
public class ModuleUtilsHook extends BaseHook {

    private static final String MI_TWEAKS_PACKAGE = BuildConfig.APPLICATION_ID;
    private static final int MODULE_VERSION = BuildConfig.MODULE_VERSION;

    @Override
    public void onLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (MI_TWEAKS_PACKAGE.equals(lpparam.packageName)) {
            try {
                XLog.i("Hooking current Xposed module status...");
                hookModuleUtils(lpparam);
            } catch (Throwable e) {
                XLog.e("Failed to hook current Xposed module status.");
            }
        }

    }

    private void hookModuleUtils(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        String className = ModuleUtils.class.getName();

        findAndHookMethod(className, lpparam.classLoader,
                "getModuleVersion",
                XC_MethodReplacement.returnConstant(MODULE_VERSION));
    }

}
