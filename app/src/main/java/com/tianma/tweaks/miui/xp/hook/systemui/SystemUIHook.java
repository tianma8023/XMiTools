package com.tianma.tweaks.miui.xp.hook.systemui;


import com.tianma.tweaks.miui.data.sp.XPrefContainer;
import com.tianma.tweaks.miui.utils.XLogKt;
import com.tianma.tweaks.miui.utils.rom.MiuiUtils;
import com.tianma.tweaks.miui.utils.rom.MiuiVersion;
import com.tianma.tweaks.miui.xp.hook.BaseHook;
import com.tianma.tweaks.miui.xp.hook.systemui.keyguard.v20190507.ChooseKeyguardClockActivityHook;
import com.tianma.tweaks.miui.xp.hook.systemui.keyguard.def.KeyguardClockContainerHook;
import com.tianma.tweaks.miui.xp.hook.systemui.keyguard.v20191213.MiuiBaseClockHook;
import com.tianma.tweaks.miui.xp.hook.systemui.keyguard.v20191213.MiuiCenterHorizontalClockHook;
import com.tianma.tweaks.miui.xp.hook.systemui.keyguard.v20190507.MiuiKeyguardBaseClockHook;
import com.tianma.tweaks.miui.xp.hook.systemui.keyguard.def.MiuiKeyguardClockHook;
import com.tianma.tweaks.miui.xp.hook.systemui.keyguard.v20190507.MiuiKeyguardLeftTopClockHook;
import com.tianma.tweaks.miui.xp.hook.systemui.keyguard.v20190507.MiuiKeyguardVerticalClockHook;
import com.tianma.tweaks.miui.xp.hook.systemui.keyguard.v20191213.MiuiLeftTopClockHook;
import com.tianma.tweaks.miui.xp.hook.systemui.keyguard.v20191213.MiuiLeftTopLargeClockHook;
import com.tianma.tweaks.miui.xp.hook.systemui.keyguard.v20191213.MiuiVerticalClockHook;
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
                    hookAfter20201109(classLoader, appInfo);
                } else if (appInfo.getVersionCode() >= AppVersionConst.SYSTEM_UI_V201912130) {
                    hookAfter201912130(classLoader, miuiVersion, appInfo);
                } else if (miuiVersion.getTime() >= MiuiVersion.V_19_5_7.getTime()) {
                    hookAfter190507(classLoader, miuiVersion, appInfo);
                } else {
                    hookByDefault(classLoader, miuiVersion, appInfo);
                }
            }
        }
    }

    // 默认 Hook 逻辑（兜底）
    private void hookByDefault(ClassLoader classLoader,
                               MiuiVersion miuiVersion,
                               AppInfo appInfo) {
        XLogKt.logD("hook by default");
        new MiuiKeyguardClockHook(classLoader).startHook();

        new PhoneStatusBarViewHook(classLoader, appInfo).startHook();
        new StatusBarClockHook(classLoader).startHook();
        new KeyguardClockContainerHook(classLoader, appInfo).startHook();

        new CollapsedStatusBarFragmentHook(classLoader, miuiVersion).startHook();
        new SignalClusterViewHook(classLoader, miuiVersion).startHook();

        new HeaderViewHook(classLoader, miuiVersion).startHook();
        new BatteryMeterViewHook(classLoader, miuiVersion).startHook();
    }

    private void hookAfter190507(ClassLoader classLoader,
                                 MiuiVersion miuiVersion,
                                 AppInfo appInfo) {
        XLogKt.logD("hook after MIUI 19.05.07");
        new MiuiKeyguardVerticalClockHook(classLoader).startHook();
        new MiuiKeyguardLeftTopClockHook(classLoader).startHook();
        new ChooseKeyguardClockActivityHook(classLoader, appInfo).startHook();
        new MiuiKeyguardBaseClockHook(classLoader).startHook();

        new PhoneStatusBarViewHook(classLoader, appInfo).startHook();
        new StatusBarClockHook(classLoader).startHook();
        new KeyguardClockContainerHook(classLoader, appInfo).startHook();

        new CollapsedStatusBarFragmentHook(classLoader, miuiVersion).startHook();
        new SignalClusterViewHook(classLoader, miuiVersion).startHook();

        new HeaderViewHook(classLoader, miuiVersion).startHook();
        new BatteryMeterViewHook(classLoader, miuiVersion).startHook();
    }

    private void hookAfter201912130(ClassLoader classLoader,
                                    MiuiVersion miuiVersion,
                                    AppInfo appInfo) {
        XLogKt.logD("hook after v201912130");
        // 锁屏
        new MiuiCenterHorizontalClockHook(classLoader, appInfo).startHook();
        new MiuiVerticalClockHook(classLoader, appInfo).startHook();
        new MiuiLeftTopClockHook(classLoader, appInfo).startHook();
        new MiuiLeftTopLargeClockHook(classLoader, appInfo).startHook();
        new ChooseKeyguardClockActivityHook(classLoader, appInfo).startHook();
        new MiuiBaseClockHook(classLoader, appInfo).startHook();
        new KeyguardClockContainerHook(classLoader, appInfo).startHook();

        // 状态栏 + 下拉状态栏
        new PhoneStatusBarViewHook(classLoader, appInfo).startHook();
        new StatusBarClockHook(classLoader).startHook();

        new CollapsedStatusBarFragmentHook(classLoader, miuiVersion).startHook();
        new SignalClusterViewHook(classLoader, miuiVersion).startHook();

        new HeaderViewHook(classLoader, miuiVersion).startHook();
        new BatteryMeterViewHook(classLoader, miuiVersion).startHook();
    }

    private void hookAfter20201109(ClassLoader classLoader, AppInfo appInfo) {
        XLogKt.logD("hook after v20201109");
        // 锁屏
        new MiuiCenterHorizontalClockHook(classLoader, appInfo).startHook();
        new MiuiVerticalClockHook(classLoader, appInfo).startHook();
        new MiuiLeftTopClockHook(classLoader, appInfo).startHook();
        new MiuiLeftTopLargeClockHook(classLoader, appInfo).startHook();
        new ChooseKeyguardClockActivityHook(classLoader, appInfo).startHook();
        new MiuiBaseClockHook(classLoader, appInfo).startHook();
        new KeyguardClockContainerHook(classLoader, appInfo).startHook();

        // 状态栏 + 下拉状态栏
        new PhoneStatusBarViewHook(classLoader, appInfo).startHook();
        new StatusBarClockHook20201109(classLoader, appInfo).startHook();

        new CollapsedStatusBarFragmentHook20201109(classLoader, appInfo).startHook();
        new StatusBarSignalPolicyHook20201109(classLoader, appInfo).startHook();
        new StatusBarMobileViewHook20201109(classLoader, appInfo).startHook();

        new MiuiQSHeaderViewHook20201109(classLoader, appInfo).startHook();
    }
}
