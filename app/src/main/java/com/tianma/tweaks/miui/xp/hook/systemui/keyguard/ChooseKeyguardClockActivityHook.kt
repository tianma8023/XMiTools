package com.tianma.tweaks.miui.xp.hook.systemui.keyguard;

import static com.tianma.tweaks.miui.xp.wrapper.XposedWrapper.findAndHookMethod;

import android.content.Context;
import android.content.Intent;

import com.tianma.tweaks.miui.data.sp.XPrefContainer;
import com.tianma.tweaks.miui.utils.XLogKt;
import com.tianma.tweaks.miui.xp.hook.BaseSubHook;
import com.tianma.tweaks.miui.xp.utils.appinfo.AppInfo;
import com.tianma.tweaks.miui.xp.wrapper.MethodHookWrapper;

/**
 * MIUI设置页面 - 选择锁屏时钟界面 Hook
 * 适用版本 9.5.7+
 */
public class ChooseKeyguardClockActivityHook extends BaseSubHook {

    private static final String CLASS_CHOOSE_KEYGUARD_CLOCK_ACTIVITY = "com.android.keyguard.settings.ChooseKeyguardClockActivity";

    private boolean mShowVerticalSec;
    private boolean mShowHorizontalSec;

    public ChooseKeyguardClockActivityHook(ClassLoader classLoader, AppInfo appInfo) {
        super(classLoader, appInfo);

        // mShowHorizontalSec = XSPUtils.showSecInKeyguardHorizontal(xsp);
        mShowHorizontalSec = XPrefContainer.getShowSecInKeyguardHorizontal();
        // mShowVerticalSec = XSPUtils.showSecInKeyguardVertical(xsp);
        mShowVerticalSec = XPrefContainer.getShowSecInKeyguardVertical();
    }

    @Override
    public void startHook() {

        if (!mShowHorizontalSec && !mShowVerticalSec) {
            return;
        }
        try {
            XLogKt.logD("Hooking ChooseKeyguardClockActivity...");
            hookOnStop();
        } catch (Throwable t) {
            XLogKt.logE("Error occurs when hook ChooseKeyguardClockActivity", t);
        }
    }

    // com.android.keyguard.setting.ChooseKeyguardClockActivity#onStop()
    private void hookOnStop() {
        findAndHookMethod(CLASS_CHOOSE_KEYGUARD_CLOCK_ACTIVITY,
                getMClassLoader(),
                "onStop",
                new MethodHookWrapper() {
                    @Override
                    protected void before(MethodHookParam param) {
                        Context context = (Context) param.thisObject;
                        Intent intent = new Intent(IntentAction.KEYGUARD_STOP_TIME_TICK);
                        context.sendBroadcast(intent);
                    }
                });
    }
}
