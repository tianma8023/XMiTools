package com.tianma.tweaks.miui.xp.hook.launcher;

import com.tianma.tweaks.miui.data.sp.XPrefContainer;
import com.tianma.tweaks.miui.utils.XLogKt;
import com.tianma.tweaks.miui.utils.rom.MiuiUtils;
import com.tianma.tweaks.miui.xp.hook.BaseHook;

import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class MiuiLauncherHook extends BaseHook {

    public static final String PACKAGE_NAME = "com.miui.home";

    public MiuiLauncherHook() {
    }

    @Override
    public void onLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (PACKAGE_NAME.equals(lpparam.packageName)) {
            XLogKt.logI("Hooking MIUI Launcher...");

            ClassLoader classLoader = lpparam.classLoader;
            if (XPrefContainer.getMainSwitchEnable()) {
                if(!MiuiUtils.isMiui()) {
                    return;
                }
                new WorkSpaceHook(classLoader).startHook();
            }

        }
    }
}
