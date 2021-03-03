package com.tianma.tweaks.miui.xp.wrapper;

import androidx.annotation.NonNull;

import com.tianma.tweaks.miui.utils.XLog;

import de.robv.android.xposed.XC_MethodHook;

public abstract class MethodHookWrapper extends XC_MethodHook {

    @Override
    final protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
        try {
            before(param);
        } catch (Throwable t) {
            XLog.d("Error in hook %s", param.method.getName(), t);
        }
    }

    protected void before(MethodHookParam param) {
    }

    @Override
    final protected void afterHookedMethod(MethodHookParam param) throws Throwable {
        try {
            after(param);
        } catch (Throwable t) {
            XLog.e("Error in hook %s", param.method.getName(), t);
        }
    }

    protected void after(MethodHookParam param) {
    }
}
