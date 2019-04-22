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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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

    private List<TextView> mClockViews = new ArrayList<>();

    /**
     * 状态栏是否显示秒数
     */
    private boolean mShowSecInStatusBar;
    /**
     * 下拉状态栏是否显示秒数
     */
    private boolean mShowSecInDropdownStatusBar;
    /**
     * 是否自定义状态栏时间格式
     */
    private boolean mStatusBarClockFormatEnabled;
    private SimpleDateFormat mStatusBarClockFormat;

    public StatusBarClockHook(ClassLoader classLoader, XSharedPreferences xsp) {
        super(classLoader, xsp);
    }

    @Override
    public void startHook() {
        try {
            XLog.d("Hooking StatusBar Clock...");
            mShowSecInStatusBar = XSPUtils.showSecInStatusBar(xsp);
            mStatusBarClockFormatEnabled = XSPUtils.isStatusBarClockFormatEnabled(xsp);
            mShowSecInDropdownStatusBar = XSPUtils.showSecInDropdownStatusBar(xsp);
            if (mStatusBarClockFormatEnabled) {
                String timeFormat = XSPUtils.getStatusBarClockFormat(xsp);
                mStatusBarClockFormat = new SimpleDateFormat(timeFormat, Locale.getDefault());
            }
            if (mShowSecInStatusBar || mStatusBarClockFormatEnabled || mShowSecInDropdownStatusBar) {
                mClockClass = XposedHelpers.findClass(CLASS_CLOCK, mClassLoader);
                if (mShowSecInStatusBar || mShowSecInDropdownStatusBar) {
                    hookStatusBar();
                    hookClockConstructor();
                }
                hookClockUpdateClock();
            }
        } catch (Throwable t) {
            XLog.e("Error occurs when hook StatusBar Clock", t);
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
                            Resources res = clockView.getResources();
                            int id = clockView.getId();
                            String timeStr = clockView.getText().toString();
                            if (id == getId(res, "clock")) {
                                if (mStatusBarClockFormatEnabled) {
                                    timeStr = getCustomFormatTime();
                                } else if (mShowSecInStatusBar) {
                                    timeStr = addInSecond(timeStr);
                                } else {
                                    return;
                                }
                            } else if (id == getId(res, "big_time")
                                    || id == getId(res, "date_time")) {
                                if (mShowSecInDropdownStatusBar) {
                                    timeStr = addInSecond(timeStr);
                                } else {
                                    return;
                                }
                            } else {
                                return;
                            }
                            clockView.setText(timeStr);
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

    private String getCustomFormatTime() {
        return mStatusBarClockFormat.format(new Date());
    }

    private int getId(Resources res, String name) {
        return res.getIdentifier(name, "id", SystemUIHook.PACKAGE_NAME);
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
                            if (id == getId(res, "clock") && mShowSecInStatusBar) {
                                if (!mClockViews.contains(clock)) {
                                    mClockViews.add(clock);
                                }
                            } else if ((id == getId(res, "big_time")
                                    || id == getId(res, "date_time"))
                                    && mShowSecInDropdownStatusBar) {
                                if (!mClockViews.contains(clock)) {
                                    mClockViews.add(clock);
                                }
                            }
                        } catch (Throwable e) {
                            XLog.e("", e);
                        }
                    }
                });
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

                            mSecondsHandler = new Handler(context.getMainLooper());
                            mSecondsHandler.post(mSecondsTicker);
                        } catch (Throwable t) {
                            XLog.e("", t);
                        }
                    }
                });
    }

    private Handler mSecondsHandler;

    private final Runnable mSecondsTicker = new Runnable() {

        @Override
        public void run() {
            long now = SystemClock.uptimeMillis();
            long next = now + (1000 - now % 1000);
            mSecondsHandler.postAtTime(this, next);
            for (TextView clockView : mClockViews) {
                if (clockView != null) {
                    XposedHelpers.callMethod(clockView, "updateClock");
                }
            }
        }
    };

    private final BroadcastReceiver mScreenReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Intent.ACTION_SCREEN_OFF.equals(action)) {
                mSecondsHandler.removeCallbacks(mSecondsTicker);
            } else if (Intent.ACTION_SCREEN_ON.equals(action)) {
                mSecondsHandler.postAtTime(mSecondsTicker,
                        SystemClock.uptimeMillis() / 1000 * 1000 + 1000);
            }
        }
    };

}
