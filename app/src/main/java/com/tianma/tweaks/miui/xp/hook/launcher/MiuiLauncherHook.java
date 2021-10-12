package com.tianma.tweaks.miui.xp.hook.launcher;

import com.tianma.tweaks.miui.data.sp.XPrefContainer;
import com.tianma.tweaks.miui.utils.XLog;
import com.tianma.tweaks.miui.utils.XSPUtils;
import com.tianma.tweaks.miui.utils.rom.MiuiUtils;
import com.tianma.tweaks.miui.xp.hook.BaseHook;

import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class MiuiLauncherHook extends BaseHook {

    public static final String PACKAGE_NAME = "com.miui.home";

    public MiuiLauncherHook() {
    }

    @Override
    public void onLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (PACKAGE_NAME.equals(lpparam.packageName)) {
            XLog.i("Hooking MIUI Launcher...");

            XSharedPreferences xsp = XSPUtils.getXSharedPreferences();

            ClassLoader classLoader = lpparam.classLoader;
            if (XPrefContainer.getMainSwitchEnable()) {
                if(!MiuiUtils.isMiui()) {
                    return;
                }
                new WorkSpaceHook(classLoader, xsp).startHook();
            }

        }
    }
}
