package com.tianma.tweaks.miui.xp.hook.systemui;


import com.tianma.tweaks.miui.BuildConfig;
import com.tianma.tweaks.miui.utils.XLog;
import com.tianma.tweaks.miui.utils.XSPUtils;
import com.tianma.tweaks.miui.xp.hook.BaseHook;

import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class SystemUIHook extends BaseHook {

    public static final String PACKAGE_NAME = "com.android.systemui";

    public SystemUIHook() {
    }

    @Override
    public void onLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (PACKAGE_NAME.equals(lpparam.packageName)) {
            XLog.d("Hooking SystemUI...");

            XSharedPreferences xsp = new XSharedPreferences(BuildConfig.APPLICATION_ID);
            try {
                xsp.makeWorldReadable();
            } catch (Throwable t) {
                XLog.e("", t);
            }

            if (XSPUtils.isMainSwitchEnabled(xsp)) {
                new StatusBarClockHook(lpparam.classLoader, xsp).startHook();
                new MiuiKeyguardClockHook(lpparam.classLoader, xsp).startHook();
                new PhoneStatusBarViewHook(lpparam.classLoader, xsp).startHook();
            }
        }
    }
}
