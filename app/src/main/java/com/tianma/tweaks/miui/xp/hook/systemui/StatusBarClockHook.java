package com.tianma.tweaks.miui.xp.hook.systemui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Handler;
import android.os.SystemClock;
import android.widget.TextView;

import com.tianma.tweaks.miui.utils.XLog;
import com.tianma.tweaks.miui.utils.XSPUtils;
import com.tianma.tweaks.miui.xp.hook.BaseSubHook;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

/**
 * StatusBar Clock hook. 状态栏显秒
 */
public class StatusBarClockHook extends BaseSubHook {

    private static final String CLASS_CLOCK = "com.android.systemui.statusbar.policy.Clock";

    private static final String CLASS_STATUS_BAR = "com.android.systemui.statusbar.phone.StatusBar";

    private Class<?> mClockClass;

    private List<BroadcastSubReceiver> mSubReceivers = new ArrayList<>();

    private final BroadcastReceiver mScreenReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            for (BroadcastSubReceiver subReceiver : mSubReceivers) {
                if (subReceiver != null) {
                    subReceiver.onBroadcastReceived(context, intent);
                }
            }
        }
    };

    public StatusBarClockHook(ClassLoader classLoader, XSharedPreferences xsp) {
        super(classLoader, xsp);
    }

    @Override
    public void startHook() {
        if (XSPUtils.showSecInStatusBar(xsp)) {
            try {
                XLog.d("Hooking StatusBar Clock...");
                mClockClass = XposedHelpers.findClass(CLASS_CLOCK, mClassLoader);
                hookStatusBar();
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
                            TextView clock = (TextView) param.thisObject;
                            Resources res = clock.getResources();
                            int id = clock.getId();
                            if (id == getId(res, "clock") || id == getId(res, "big_time")) {
                                StatusBarClock statusBarClock = new StatusBarClock(clock);
                                if (!mSubReceivers.contains(statusBarClock)) {
                                    statusBarClock.showSeconds();
                                    mSubReceivers.add(statusBarClock);
                                }
                            }
                        } catch (Throwable e) {
                            XLog.e("", e);
                        }

                    }

                    private int getId(Resources res, String name) {
                        return res.getIdentifier(name, "id", SystemUIHook.PACKAGE_NAME);
                    }
                });
    }

    private class StatusBarClock implements BroadcastSubReceiver {

        private Handler mSecondsHandler;
        private TextView mClock;

        StatusBarClock(TextView clockView) {
            mClock = clockView;
            mSecondsHandler = new Handler(clockView.getContext().getMainLooper());
        }

        private void showSeconds() {
            mSecondsHandler.post(mSecondsTicker);
        }

        private final Runnable mSecondsTicker = new Runnable() {

            @Override
            public void run() {
                long now = SystemClock.uptimeMillis();
                long next = now + (1000 - now % 1000);
                mSecondsHandler.postAtTime(this, next);
                XposedHelpers.callMethod(mClock, "updateClock");
            }
        };

        @Override
        public void onBroadcastReceived(Context context, Intent intent) {
            String action = intent.getAction();
            if (Intent.ACTION_SCREEN_OFF.equals(action)) {
                mSecondsHandler.removeCallbacks(mSecondsTicker);
            } else if (Intent.ACTION_SCREEN_ON.equals(action)) {
                mSecondsHandler.postAtTime(mSecondsTicker,
                        SystemClock.uptimeMillis() / 1000 * 1000 + 1000);
            }
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof StatusBarClock)) return false;
            StatusBarClock that = (StatusBarClock) o;
            return Objects.equals(mClock, that.mClock);
        }

        @Override
        public int hashCode() {
            return Objects.hash(mClock);
        }
    }

    private void hookStatusBar() {
        hookMakeStatusBarView();
    }

    // com.android.systemui.statusbar.phone.StatusBar#makeStatusBarView()
    private void hookMakeStatusBarView() {
        XposedHelpers.findAndHookMethod(CLASS_STATUS_BAR,
                mClassLoader,
                "makeStatusBarView",
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        try {
                            Object statusBar = param.thisObject;
                            Context context = (Context) XposedHelpers.getObjectField(statusBar, "mContext");

                            IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
                            filter.addAction(Intent.ACTION_SCREEN_ON);
                            context.registerReceiver(mScreenReceiver, filter);
                        } catch (Throwable t) {
                            XLog.e("", t);
                        }
                    }
                });
    }

}
