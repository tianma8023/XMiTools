package com.tianma.tweaks.miui.xp.hook;

import com.tianma.tweaks.miui.utils.rom.MiuiVersion;
import com.tianma.tweaks.miui.xp.utils.appinfo.AppInfo;

public abstract class BaseSubHook {

    protected ClassLoader mClassLoader;
    protected MiuiVersion mMiuiVersion;
    protected AppInfo mAppInfo;

    public BaseSubHook(ClassLoader classLoader) {
        this(classLoader, null, null);
    }

    public BaseSubHook(ClassLoader classLoader, MiuiVersion miuiVersion) {
        this(classLoader, null, miuiVersion);
    }

    public BaseSubHook(ClassLoader classLoader, AppInfo appInfo) {
        this(classLoader, appInfo, null);
    }

    public BaseSubHook(ClassLoader classLoader, AppInfo appInfo, MiuiVersion miuiVersion) {
        mClassLoader = classLoader;
        mAppInfo = appInfo;
        mMiuiVersion = miuiVersion;
    }

    public abstract void startHook();

}
