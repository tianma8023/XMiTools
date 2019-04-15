package com.tianma.tweaks.miui.xp.hook;

import de.robv.android.xposed.XSharedPreferences;

public abstract class BaseSubHook {

    protected ClassLoader mClassLoader;
    protected XSharedPreferences xsp;

    public BaseSubHook(ClassLoader classLoader, XSharedPreferences xsp) {
        mClassLoader = classLoader;
        this.xsp = xsp;
    }

    public abstract void startHook();

}
