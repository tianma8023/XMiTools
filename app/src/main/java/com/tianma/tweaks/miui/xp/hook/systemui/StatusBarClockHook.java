package com.tianma.tweaks.miui.xp.hook.systemui;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.widget.TextView;

import com.tianma.tweaks.miui.utils.ReflectionUtils;
import com.tianma.tweaks.miui.utils.XLog;
import com.tianma.tweaks.miui.utils.XSPUtils;
import com.tianma.tweaks.miui.xp.hook.BaseSubHook;

import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.Locale;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

/**
 * StatusBar Clock hook. 状态栏显秒
 */
public class StatusBarClockHook extends BaseSubHook {

    private static final String CLOCK_CLASS_NAME = "com.android.systemui.statusbar.policy.Clock";

    private Class<?> mClockClass;

    public StatusBarClockHook(ClassLoader classLoader, XSharedPreferences xsp) {
        super(classLoader, xsp);
    }

    @Override
    public void startHook() {
        if (XSPUtils.showSecInStatusBar(xsp)) {
            try {
                XLog.d("Hooking StatusBar Clock...");
                mClockClass = XposedHelpers.findClass(CLOCK_CLASS_NAME, mClassLoader);
                hookClockUpdateClock();
                hookClockConstructor();
            } catch (Throwable t) {
                XLog.e("Error occurs when hook StatusBar Clock", t);
            }
        }
    }

    private void hookClockUpdateClock() {
        XposedHelpers.findAndHookMethod(mClockClass,
                "updateClock",
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        try {
                            TextView clockView = (TextView) param.thisObject;
                            clockView.setText(addInSecond(clockView.getText().toString()));
                        } catch (Throwable t) {
                            XLog.e("", t);
                        }
                    }
                });
    }

    private String addInSecond(String timeStr) {
        int sec = Calendar.getInstance().get(Calendar.SECOND);
        String secStr = String.format(Locale.getDefault(), "%02d", sec);
        return timeStr.replaceAll("\\d+:\\d+", "$0:" + secStr);
    }

    private void hookClockConstructor() {
        XposedBridge.hookAllConstructors(mClockClass,
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        try {
                            SecondsHandler handler = new SecondsHandler(Looper.getMainLooper(), param.thisObject);
                            handler.sendEmptyMessage(SecondsHandler.MSG_UPDATE_CLOCK);
                        } catch (Throwable e) {
                            XLog.e("", e);
                        }

                    }
                });
    }


    private static class SecondsHandler extends Handler {
        static final int MSG_UPDATE_CLOCK = 0x12;

        private Object clockObject;
        private Method updateClockMethod;

        private long mLastUpdate = 0L;
        private long mCurrentTime;

        private SecondsHandler(Looper looper, Object clockObject) {
            super(looper);
            this.clockObject = clockObject;
            updateClockMethod = ReflectionUtils.getDeclaredMethod(clockObject.getClass(), "updateClock");
        }

        @Override
        public void handleMessage(Message msg) {
            sendEmptyMessageDelayed(MSG_UPDATE_CLOCK, 499L);
            mCurrentTime = SystemClock.elapsedRealtime();
            if (mCurrentTime - mLastUpdate >= 998L) {
                ReflectionUtils.invoke(updateClockMethod, clockObject);
                mLastUpdate = mCurrentTime;
            }
        }
    }

}
