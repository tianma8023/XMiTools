package com.tianma.tweaks.miui.xp.hook.systemui.keyguard;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tianma.tweaks.miui.utils.XLog;
import com.tianma.tweaks.miui.utils.XSPUtils;
import com.tianma.tweaks.miui.xp.hook.BaseSubHook;

import java.util.Calendar;
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
public class MiuiKeyguardLeftTopClockHook extends BaseSubHook {

    private static final String CLASS_MIUI_KEYGUARD_VERTICAL_CLOCK = "com.android.keyguard.MiuiKeyguardLeftTopClock";

    private Class<?> mMiuiKeyguardLeftTopClockCls;

    private boolean mShowHorizontalSec;

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
                    .findClass(CLASS_MIUI_KEYGUARD_VERTICAL_CLOCK, mClassLoader);
            hookOnFinishInflate();
            hookConstructor();
            hookUpdateTime();
        } catch (Throwable t) {
            XLog.e("Error occurs when hook MiuiKeyguardLeftTopClock", t);
        }
    }

    // 显示时间的View
    private TextView mTimeText;

    // com.android.keyguard.MiuiKeyguardLeftTopClock#onFinishInflate
    private void hookOnFinishInflate() {
        findAndHookMethod(mMiuiKeyguardLeftTopClockCls,
                "onFinishInflate",
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        try {
                            mTimeText = (TextView) XposedHelpers.getObjectField(param.thisObject, "mTimeText");
                        } catch (Throwable t) {
                            XLog.e("", t);
                        }
                    }
                });
    }

    // com.android.keyguard.MiuiKeyguardLeftTopClock#updateTime()
    private void hookUpdateTime() {
        findAndHookMethod(mMiuiKeyguardLeftTopClockCls,
                "updateTime",
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        try {
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
                            if (mSecondsHandler == null) {
                                mSecondsHandler = new SecondsHandler(Looper.getMainLooper(), param.thisObject);
                            }
                            mSecondsHandler.post(mSecondsTicker);

                            // register receiver
                            LinearLayout keyguardClock = (LinearLayout) param.thisObject;

                            IntentFilter filter = new IntentFilter();
                            filter.addAction(Intent.ACTION_SCREEN_ON);
                            filter.addAction(Intent.ACTION_USER_PRESENT);
                            filter.addAction(Intent.ACTION_SCREEN_OFF);
                            filter.addAction(IntentAction.STOP_TIME_TICK);

                            keyguardClock.getContext().registerReceiver(mScreenReceiver, filter);
                        } catch (Throwable e) {
                            XLog.e("", e);
                        }
                    }
                });
    }

    private SecondsHandler mSecondsHandler;

    private final Runnable mSecondsTicker = new Runnable() {

        @Override
        public void run() {
            long now = SystemClock.uptimeMillis();
            long next = now + (1000 - now % 1000);
            mSecondsHandler.postAtTime(this, next);
            XposedHelpers.callMethod(mSecondsHandler.mKeyguardClockObj, "updateTime");
        }
    };

    private static class SecondsHandler extends Handler {
        private Object mKeyguardClockObj;

        private SecondsHandler(Looper looper, Object keyguardClockObj) {
            super(looper);
            this.mKeyguardClockObj = keyguardClockObj;
        }
    }

    private final BroadcastReceiver mScreenReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Intent.ACTION_SCREEN_ON.equals(action)) {
                if (mSecondsHandler != null) {
                    mSecondsHandler.post(mSecondsTicker);
                }
            } else if (Intent.ACTION_USER_PRESENT.equals(action)
                    || Intent.ACTION_SCREEN_OFF.equals(action)) {
                if (mSecondsHandler != null) {
                    mSecondsHandler.removeCallbacks(mSecondsTicker);
                }
            } else if (IntentAction.STOP_TIME_TICK.equals(action)){
                if (mSecondsHandler != null) {
                    mSecondsHandler.removeCallbacks(mSecondsTicker);
                }
            }
        }
    };

}
