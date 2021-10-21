package com.tianma.tweaks.miui.xp.hook.systemui;


import com.tianma.tweaks.miui.data.sp.XPrefContainer;
import com.tianma.tweaks.miui.utils.XLogKt;
import com.tianma.tweaks.miui.utils.XLogKt;
import com.tianma.tweaks.miui.utils.XSPUtils;
import com.tianma.tweaks.miui.utils.rom.MiuiUtils;
import com.tianma.tweaks.miui.utils.rom.MiuiVersion;
import com.tianma.tweaks.miui.xp.hook.BaseHook;
import com.tianma.tweaks.miui.xp.hook.systemui.keyguard.ChooseKeyguardClockActivityHook;
import com.tianma.tweaks.miui.xp.hook.systemui.keyguard.KeyguardClockContainerHook;
import com.tianma.tweaks.miui.xp.hook.systemui.keyguard.MiuiBaseClockHook;
import com.tianma.tweaks.miui.xp.hook.systemui.keyguard.MiuiCenterHorizontalClockHook;
import com.tianma.tweaks.miui.xp.hook.systemui.keyguard.MiuiKeyguardBaseClockHook;
import com.tianma.tweaks.miui.xp.hook.systemui.keyguard.MiuiKeyguardClockHook;
import com.tianma.tweaks.miui.xp.hook.systemui.keyguard.MiuiKeyguardLeftTopClockHook;
import com.tianma.tweaks.miui.xp.hook.systemui.keyguard.MiuiKeyguardVerticalClockHook;
import com.tianma.tweaks.miui.xp.hook.systemui.keyguard.MiuiLeftToplClockHook;
import com.tianma.tweaks.miui.xp.hook.systemui.keyguard.MiuiLeftToplLargeClockHook;
import com.tianma.tweaks.miui.xp.hook.systemui.keyguard.MiuiVerticalClockHook;
import com.tianma.tweaks.miui.xp.hook.systemui.statusbar.def.BatteryMeterViewHook;
import com.tianma.tweaks.miui.xp.hook.systemui.statusbar.def.CollapsedStatusBarFragmentHook;
import com.tianma.tweaks.miui.xp.hook.systemui.statusbar.def.HeaderViewHook;
import com.tianma.tweaks.miui.xp.hook.systemui.statusbar.def.PhoneStatusBarViewHook;
import com.tianma.tweaks.miui.xp.hook.systemui.statusbar.def.SignalClusterViewHook;
import com.tianma.tweaks.miui.xp.hook.systemui.statusbar.def.StatusBarClockHook;
import com.tianma.tweaks.miui.xp.hook.systemui.statusbar.v20201109.CollapsedStatusBarFragmentHook20201109;
import com.tianma.tweaks.miui.xp.hook.systemui.statusbar.v20201109.MiuiQSHeaderViewHook20201109;
import com.tianma.tweaks.miui.xp.hook.systemui.statusbar.v20201109.StatusBarClockHook20201109;
import com.tianma.tweaks.miui.xp.hook.systemui.statusbar.v20201109.StatusBarMobileViewHook20201109;
import com.tianma.tweaks.miui.xp.hook.systemui.statusbar.v20201109.StatusBarSignalPolicyHook20201109;
import com.tianma.tweaks.miui.xp.utils.appinfo.AppInfo;
import com.tianma.tweaks.miui.xp.utils.appinfo.AppInfoHelper;
import com.tianma.tweaks.miui.xp.utils.appinfo.AppVersionConst;

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
            XLogKt.logI("Hooking SystemUI...");

            XSharedPreferences xsp = XSPUtils.getXSharedPreferences();

            ClassLoader classLoader = lpparam.classLoader;

            if (XPrefContainer.getMainSwitchEnable()) {
                if (!MiuiUtils.isMiui()) {
                    XLogKt.logE("Only support MIUI");
                    return;
                }

                MiuiVersion miuiVersion = MiuiUtils.getMiuiVersion();
                XLogKt.logI(miuiVersion.toString());

                AppInfo appInfo = AppInfoHelper.getAppInfo(lpparam);
                XLogKt.logI(appInfo.toString());
                if (appInfo.getVersionCode() >= AppVersionConst.SYSTEM_UI_V202011090) {
                    hookAfter20201109(classLoader, xsp, appInfo);
                } else if (appInfo.getVersionCode() >= AppVersionConst.SYSTEM_UI_V201912130) {
                    hookAfter201912130(classLoader, xsp, miuiVersion, appInfo);
                } else if (miuiVersion.getTime() >= MiuiVersion.V_19_5_7.getTime()) {
                    hookAfter190507(classLoader, xsp, miuiVersion, appInfo);
                } else {
                    hookByDefault(classLoader, xsp, miuiVersion, appInfo);
                }
            }
        }
    }

    // 默认 Hook 逻辑（兜底）
    private void hookByDefault(ClassLoader classLoader,
                               XSharedPreferences xsp,
                               MiuiVersion miuiVersion,
                               AppInfo appInfo) {
        XLogKt.logD("hook by default");
        new MiuiKeyguardClockHook(classLoader, xsp).startHook();

        new PhoneStatusBarViewHook(classLoader, xsp, appInfo).startHook();
        new StatusBarClockHook(classLoader, xsp).startHook();
        new KeyguardClockContainerHook(classLoader, xsp, appInfo).startHook();

        new CollapsedStatusBarFragmentHook(classLoader, xsp, miuiVersion).startHook();
        new SignalClusterViewHook(classLoader, xsp, miuiVersion).startHook();

        new HeaderViewHook(classLoader, xsp, miuiVersion).startHook();
        new BatteryMeterViewHook(classLoader, xsp, miuiVersion).startHook();
    }

    private void hookAfter190507(ClassLoader classLoader,
                                 XSharedPreferences xsp,
                                 MiuiVersion miuiVersion,
                                 AppInfo appInfo) {
        XLogKt.logD("hook after MIUI 19.05.07");
        new MiuiKeyguardVerticalClockHook(classLoader, xsp).startHook();
        new MiuiKeyguardLeftTopClockHook(classLoader, xsp).startHook();
        new ChooseKeyguardClockActivityHook(classLoader, xsp, appInfo).startHook();
        new MiuiKeyguardBaseClockHook(classLoader, xsp).startHook();

        new PhoneStatusBarViewHook(classLoader, xsp, appInfo).startHook();
        new StatusBarClockHook(classLoader, xsp).startHook();
        new KeyguardClockContainerHook(classLoader, xsp, appInfo).startHook();

        new CollapsedStatusBarFragmentHook(classLoader, xsp, miuiVersion).startHook();
        new SignalClusterViewHook(classLoader, xsp, miuiVersion).startHook();

        new HeaderViewHook(classLoader, xsp, miuiVersion).startHook();
        new BatteryMeterViewHook(classLoader, xsp, miuiVersion).startHook();
    }

    private void hookAfter201912130(ClassLoader classLoader,
                                    XSharedPreferences xsp,
                                    MiuiVersion miuiVersion,
                                    AppInfo appInfo) {
        XLogKt.logD("hook after v201912130");
        new MiuiCenterHorizontalClockHook(classLoader, xsp, appInfo).startHook();
        new MiuiVerticalClockHook(classLoader, xsp, appInfo).startHook();
        new MiuiLeftToplClockHook(classLoader, xsp, appInfo).startHook();
        new MiuiLeftToplLargeClockHook(classLoader, xsp, appInfo).startHook();
        new ChooseKeyguardClockActivityHook(classLoader, xsp, appInfo).startHook();
        new MiuiBaseClockHook(classLoader, xsp, appInfo).startHook();

        new PhoneStatusBarViewHook(classLoader, xsp, appInfo).startHook();
        new StatusBarClockHook(classLoader, xsp).startHook();
        new KeyguardClockContainerHook(classLoader, xsp, appInfo).startHook();

        new CollapsedStatusBarFragmentHook(classLoader, xsp, miuiVersion).startHook();
        new SignalClusterViewHook(classLoader, xsp, miuiVersion).startHook();

        new HeaderViewHook(classLoader, xsp, miuiVersion).startHook();
        new BatteryMeterViewHook(classLoader, xsp, miuiVersion).startHook();
    }

    private void hookAfter20201109(ClassLoader classLoader,
                                   XSharedPreferences xsp,
                                   AppInfo appInfo) {
        XLogKt.logD("hook after v20201109");
        // 锁屏
        new MiuiCenterHorizontalClockHook(classLoader, xsp, appInfo).startHook();
        new MiuiVerticalClockHook(classLoader, xsp, appInfo).startHook();
        new MiuiLeftToplClockHook(classLoader, xsp, appInfo).startHook();
        new MiuiLeftToplLargeClockHook(classLoader, xsp, appInfo).startHook();
        new ChooseKeyguardClockActivityHook(classLoader, xsp, appInfo).startHook();
        new MiuiBaseClockHook(classLoader, xsp, appInfo).startHook();
        new KeyguardClockContainerHook(classLoader, xsp, appInfo).startHook();

        // 状态栏 + 下拉状态栏
        new PhoneStatusBarViewHook(classLoader, xsp, appInfo).startHook();
        new StatusBarClockHook20201109(classLoader, xsp, appInfo).startHook();

        new CollapsedStatusBarFragmentHook20201109(classLoader, xsp, appInfo).startHook();
        new StatusBarSignalPolicyHook20201109(classLoader, xsp, appInfo).startHook();
        new StatusBarMobileViewHook20201109(classLoader, xsp, appInfo).startHook();

        new MiuiQSHeaderViewHook20201109(classLoader, xsp, appInfo).startHook();
    }
}
