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
import com.tianma.tweaks.miui.xp.hook.systemui.statusbar.BatteryMeterViewHook;
import com.tianma.tweaks.miui.xp.hook.systemui.statusbar.CollapsedStatusBarFragmentHook;
import com.tianma.tweaks.miui.xp.hook.systemui.statusbar.HeaderViewHook;
import com.tianma.tweaks.miui.xp.hook.systemui.statusbar.PhoneStatusBarViewHook;
import com.tianma.tweaks.miui.xp.hook.systemui.statusbar.SignalClusterViewHook;
import com.tianma.tweaks.miui.xp.hook.systemui.statusbar.StatusBarClockHook;

import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * MIUI 系统界面 App Hook
 */
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
                if (miuiVersion.getTime() >= MiuiVersion.V_19_5_7.getTime()) {
                    new MiuiKeyguardVerticalClockHook(classLoader, xsp).startHook();
                    new MiuiKeyguardLeftTopClockHook(classLoader, xsp).startHook();
                    new ChooseKeyguardClockActivityHook(classLoader, xsp).startHook();
                } else {
                    new MiuiKeyguardClockHook(classLoader, xsp).startHook();
                }
                new PhoneStatusBarViewHook(classLoader, xsp).startHook();
                new StatusBarClockHook(classLoader, xsp).startHook();
                new KeyguardClockContainerHook(classLoader, xsp).startHook();

                new CollapsedStatusBarFragmentHook(classLoader, xsp, miuiVersion).startHook();
                new SignalClusterViewHook(classLoader, xsp, miuiVersion).startHook();

                new HeaderViewHook(classLoader, xsp, miuiVersion).startHook();
                new BatteryMeterViewHook(classLoader, xsp, miuiVersion).startHook();
            }
        }
    }
}
