package com.tianma.tweaks.miui.xp.hook.systemui.keyguard;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.tianma.tweaks.miui.utils.XLog;
import com.tianma.tweaks.miui.utils.XSPUtils;
import com.tianma.tweaks.miui.xp.hook.BaseSubHook;
import com.tianma.tweaks.miui.xp.hook.systemui.tick.TickObserver;
import com.tianma.tweaks.miui.xp.hook.systemui.tick.TimeTicker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedHelpers;

import static de.robv.android.xposed.XposedBridge.hookAllConstructors;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

/**
 * 锁屏界面
 * 适用版本 9.5.7+
 * 锁屏左上角时钟 Hook
 */
public class MiuiKeyguardLeftTopClockHook extends BaseSubHook implements TickObserver {

    private static final String CLASS_MIUI_KEYGUARD_LEFT_TOP_CLOCK = "com.android.keyguard.MiuiKeyguardLeftTopClock";

    private Class<?> mMiuiKeyguardLeftTopClockCls;

    private boolean mShowHorizontalSec;

    private List<View> mKeyguardClockList = new ArrayList<>();

    public MiuiKeyguardLeftTopClockHook(ClassLoader classLoader, XSharedPreferences xsp) {
        super(classLoader, xsp);
        mShowHorizontalSec = XSPUtils.showSecInKeyguardHorizontal(xsp);
    }

    @Override
    public void startHook() {
        if (!mShowHorizontalSec) {
            return;
        }
        try {
            XLog.d("Hooking MiuiKeyguardLeftTopClock...");
            mMiuiKeyguardLeftTopClockCls = XposedHelpers
                    .findClass(CLASS_MIUI_KEYGUARD_LEFT_TOP_CLOCK, mClassLoader);
            hookConstructor();
            hookUpdateTime();
        } catch (Throwable t) {
            XLog.e("Error occurs when hook MiuiKeyguardLeftTopClock", t);
        }
    }

    // com.android.keyguard.MiuiKeyguardLeftTopClock#updateTime()
    private void hookUpdateTime() {
        findAndHookMethod(mMiuiKeyguardLeftTopClockCls,
                "updateTime",
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        try {
                            TextView mTimeText = (TextView) XposedHelpers.getObjectField(param.thisObject, "mTimeText");
                            if (mTimeText != null) {
                                String originalTimeStr = mTimeText.getText().toString();
                                mTimeText.setText(addInSecond(originalTimeStr));
                            }
                        } catch (Throwable t) {
                            XLog.e("", t);
                        }
                    }
                });
    }

    private String addInSecond(String originalTimeStr) {
        int sec = Calendar.getInstance().get(Calendar.SECOND);
        String secStr = String.format(Locale.getDefault(), "%02d", sec);
        return originalTimeStr.replaceAll("(\\d+:\\d+)(:\\d+)?", "$1:" + secStr);
    }

    // com.android.keyguard.MiuiKeyguardLeftTopClock#access()
    private void hookConstructor() {
        hookAllConstructors(mMiuiKeyguardLeftTopClockCls,
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        try {
                            final View keyguardClock = (View) param.thisObject;
                            keyguardClock.getViewTreeObserver().addOnWindowAttachListener(new ViewTreeObserver.OnWindowAttachListener() {
                                @Override
                                public void onWindowAttached() {
                                    addClock(keyguardClock);
                                }

                                @Override
                                public void onWindowDetached() {
                                    removeClock(keyguardClock);
                                }
                            });
                            addClock(keyguardClock);

                            // register receiver
                            IntentFilter filter = new IntentFilter();
                            filter.addAction(Intent.ACTION_SCREEN_ON);
                            filter.addAction(Intent.ACTION_USER_PRESENT);
                            filter.addAction(Intent.ACTION_SCREEN_OFF);
                            filter.addAction(IntentAction.KEYGUARD_STOP_TIME_TICK);

                            keyguardClock.getContext().registerReceiver(mScreenReceiver, filter);
                        } catch (Throwable e) {
                            XLog.e("", e);
                        }
                    }
                });
    }

    private synchronized void addClock(View clock) {
        if (!mKeyguardClockList.contains(clock)) {
            mKeyguardClockList.add(clock);

            int size = mKeyguardClockList.size();
            int limitedSize = 2;
            if (size > limitedSize) {
                for (int i = 0; i < size - limitedSize; i ++) {
                    View item = mKeyguardClockList.get(i);
                    mKeyguardClockList.remove(item);
                }
            }
        }

        if (!mKeyguardClockList.isEmpty()) {
            TimeTicker.get().registerObserver(MiuiKeyguardLeftTopClockHook.this);
        }
    }

    private synchronized void removeClock(View clock) {
        mKeyguardClockList.remove(clock);

        if (mKeyguardClockList.isEmpty()) {
            TimeTicker.get().unregisterObserver(MiuiKeyguardLeftTopClockHook.this);
        }
    }

    private final BroadcastReceiver mScreenReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Intent.ACTION_SCREEN_ON.equals(action)) {
                TimeTicker.get().registerObserver(MiuiKeyguardLeftTopClockHook.this);
            } else if (Intent.ACTION_USER_PRESENT.equals(action)
                    || Intent.ACTION_SCREEN_OFF.equals(action)) {
                TimeTicker.get().unregisterObserver(MiuiKeyguardLeftTopClockHook.this);
            } else if (IntentAction.KEYGUARD_STOP_TIME_TICK.equals(action)){
                TimeTicker.get().unregisterObserver(MiuiKeyguardLeftTopClockHook.this);
            }
        }
    };

    @Override
    public void onTimeTick() {
        for (View keyguardClock : mKeyguardClockList) {
            if (keyguardClock != null) {
                XposedHelpers.callMethod(keyguardClock, "updateTime");
            }
        }
    }
}
