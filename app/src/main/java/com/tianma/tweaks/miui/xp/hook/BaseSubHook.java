package com.tianma.tweaks.miui.xp.hook;

import com.tianma.tweaks.miui.utils.rom.MiuiVersion;
import com.tianma.tweaks.miui.xp.utils.appinfo.AppInfo;

import de.robv.android.xposed.XSharedPreferences;

public abstract class BaseSubHook {

    protected ClassLoader mClassLoader;
    protected XSharedPreferences xsp;
    protected MiuiVersion mMiuiVersion;
    protected AppInfo mAppInfo;

    public BaseSubHook(ClassLoader classLoader, XSharedPreferences xsp) {
        this(classLoader, xsp, null, null);
    }

    public BaseSubHook(ClassLoader classLoader, XSharedPreferences xsp, MiuiVersion miuiVersion) {
        this(classLoader, xsp, null, miuiVersion);
    }

    public BaseSubHook(ClassLoader classLoader, XSharedPreferences xsp, AppInfo appInfo) {
        this(classLoader, xsp, appInfo, null);
    }

    public BaseSubHook(ClassLoader classLoader, XSharedPreferences xsp, AppInfo appInfo, MiuiVersion miuiVersion) {
        mClassLoader = classLoader;
        this.xsp = xsp;
        mAppInfo = appInfo;
        mMiuiVersion = miuiVersion;
    }

    public abstract void startHook();

}
