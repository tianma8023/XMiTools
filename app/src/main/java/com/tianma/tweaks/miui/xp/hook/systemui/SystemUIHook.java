package com.tianma.tweaks.miui.xp.hook.systemui;


import com.tianma.tweaks.miui.utils.XLog;
import com.tianma.tweaks.miui.utils.XSPUtils;
import com.tianma.tweaks.miui.utils.rom.MiuiUtils;
import com.tianma.tweaks.miui.utils.rom.MiuiVersion;
import com.tianma.tweaks.miui.xp.hook.BaseHook;
import com.tianma.tweaks.miui.xp.hook.systemui.keyguard.ChooseKeyguardClockActivityHook;
import com.tianma.tweaks.miui.xp.hook.systemui.keyguard.KeyguardClockContainerHook;
import com.tianma.tweaks.miui.xp.hook.systemui.keyguard.MiuiKeyguardClockHook;
import com.tianma.tweaks.miui.xp.hook.systemui.keyguard.MiuiKeyguardLeftTopClockHook;
import com.tianma.tweaks.miui.xp.hook.systemui.keyguard.MiuiKeyguardVerticalClockHook;
import com.tianma.tweaks.miui.xp.hook.systemui.statusbar.PhoneStatusBarViewHook;
import com.tianma.tweaks.miui.xp.hook.systemui.statusbar.StatusBarClockHook;

import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class SystemUIHook extends BaseHook {

    public static final String PACKAGE_NAME = "com.android.systemui";

    public SystemUIHook() {
    }

    @Override
    public void onLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (PACKAGE_NAME.equals(lpparam.packageName)) {
            XLog.i("Hooking SystemUI...");

            XSharedPreferences xsp = XSPUtils.getXSharedPreferences();

            ClassLoader classLoader = lpparam.classLoader;
            if (XSPUtils.isMainSwitchEnabled(xsp)) {
                if(!MiuiUtils.isMiui()) {
                    return;
                }
                MiuiVersion miuiVersion = MiuiUtils.getMiuiVersion();
                if (miuiVersion.getTime() >= MiuiVersion.VERSION_19_5_7.getTime()) {
                    new MiuiKeyguardVerticalClockHook(classLoader, xsp).startHook();
                    new MiuiKeyguardLeftTopClockHook(classLoader, xsp).startHook();
                    new ChooseKeyguardClockActivityHook(classLoader, xsp).startHook();
                } else {
                    new MiuiKeyguardClockHook(classLoader, xsp).startHook();
                }
                new PhoneStatusBarViewHook(classLoader, xsp).startHook();
                new StatusBarClockHook(classLoader, xsp).startHook();
                new KeyguardClockContainerHook(classLoader, xsp).startHook();
            }
        }
    }
}
