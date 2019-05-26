package com.tianma.tweaks.miui.xp;


import com.tianma.tweaks.miui.xp.hook.BaseHook;
import com.tianma.tweaks.miui.xp.hook.ModuleUtilsHook;
import com.tianma.tweaks.miui.xp.hook.systemui.SystemUIHook;

import java.util.ArrayList;
import java.util.List;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class HookEntry implements IXposedHookLoadPackage, IXposedHookZygoteInit {

    private List<BaseHook> mHookList;

    {
        mHookList = new ArrayList<>();
        mHookList.add(new ModuleUtilsHook()); // MiuiTweaks Hook
        mHookList.add(new SystemUIHook()); // SystemUI Hook
        // mHookList.add(new MiuiLauncherHook()); // Miui Launcher Hook
    }

    @Override
    public void initZygote(StartupParam startupParam) throws Throwable {
        for (BaseHook hook : mHookList) {
            if (hook.hookInitZygote()) {
                hook.initZygote(startupParam);
            }
        }
    }

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        for (BaseHook hook : mHookList) {
            if (hook.hookOnLoadPackage()) {
                hook.onLoadPackage(lpparam);
            }
        }
    }
}
