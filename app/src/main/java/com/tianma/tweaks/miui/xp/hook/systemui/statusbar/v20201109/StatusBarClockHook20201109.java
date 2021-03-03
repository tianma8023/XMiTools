package com.tianma.tweaks.miui.xp.hook.systemui.statusbar.v20201109;

import android.content.Context;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.UserHandle;
import android.widget.TextView;

import com.tianma.tweaks.miui.utils.XLog;
import com.tianma.tweaks.miui.utils.XSPUtils;
import com.tianma.tweaks.miui.xp.hook.BaseSubHook;
import com.tianma.tweaks.miui.xp.hook.systemui.helper.ResHelpers;
import com.tianma.tweaks.miui.xp.hook.systemui.screen.ScreenBroadcastManager;
import com.tianma.tweaks.miui.xp.hook.systemui.screen.ScreenListener;
import com.tianma.tweaks.miui.xp.hook.systemui.screen.SimpleScreenListener;
import com.tianma.tweaks.miui.xp.hook.systemui.tick.TickObserver;
import com.tianma.tweaks.miui.xp.hook.systemui.tick.TimeTicker;
import com.tianma.tweaks.miui.xp.utils.appinfo.AppInfo;
import com.tianma.tweaks.miui.xp.wrapper.MethodHookWrapper;
import com.tianma.tweaks.miui.xp.wrapper.XposedWrapper;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

import static com.tianma.tweaks.miui.xp.wrapper.XposedWrapper.hookAllConstructors;

/**
 * 状态栏时钟 Hook
 * 适用版本 9.4.x+
 */
public class StatusBarClockHook20201109 extends BaseSubHook implements TickObserver {

    private static final String PKG_STATUS_BAR = "com.android.systemui.statusbar";
    private static final String CLASS_MIUI_CLOCK = PKG_STATUS_BAR + ".policy.MiuiClock";
    private static final String CLASS_STATUS_BAR = PKG_STATUS_BAR + ".phone.StatusBar";
    private static final String CLASS_DEPENDENCY = "com.android.systemui.Dependency";
    private static final String CLASS_REGISTER_STATUS_BAR_RESULT = "com.android.internal.statusbar.RegisterStatusBarResult";

    private Class<?> mMiuiClockClass;

    private final Set<Object> mClockViewSet = new HashSet<>();

    /**
     * 状态栏是否显示秒数
     */
    private final boolean mShowSecInStatusBar;
    /**
     * 下拉状态栏是否显示秒数
     */
    private final boolean mShowSecInDropdownStatusBar;
    /**
     * 是否自定义状态栏时间格式
     */
    private final boolean mStatusBarClockFormatEnabled;
    private SimpleDateFormat mStatusBarClockFormat;

    /**
     * 是否自定义状态栏时间颜色
     */
    private final boolean mStatusBarClockColorEnabled;
    /**
     * 状态栏时间颜色
     */
    private int mStatusBarClockColor;
    /**
     * 是否自定义下拉状态栏时钟颜色
     */
    private final boolean mDropdownStatusBarClockColorEnabled;
    /**
     * 下拉状态栏时间颜色
     */
    private int mDropdownStatusBarClockColor;
    /**
     * 下拉状态栏日期颜色
     */
    private int mDropdownStatusBarDateColor;

    // 是否拦截系统的 TIME_TICK Action
    private final boolean mBlockSystemTimeTick;

    public StatusBarClockHook20201109(ClassLoader classLoader, XSharedPreferences xsp, AppInfo appInfo) {
        super(classLoader, xsp, appInfo);

        mShowSecInStatusBar = XSPUtils.showSecInStatusBar(xsp);
        mStatusBarClockFormatEnabled = XSPUtils.isStatusBarClockFormatEnabled(xsp);
        mShowSecInDropdownStatusBar = XSPUtils.showSecInDropdownStatusBar(xsp);

        if (mStatusBarClockFormatEnabled) {
            String timeFormat = XSPUtils.getStatusBarClockFormat(xsp);
            mStatusBarClockFormat = new SimpleDateFormat(timeFormat, Locale.getDefault());
        }

        mStatusBarClockColorEnabled = XSPUtils.isStatusBarClockColorEnabled(xsp);
        if (mStatusBarClockColorEnabled) {
            mStatusBarClockColor = XSPUtils.getStatusBarClockColor(xsp);
        }

        mDropdownStatusBarClockColorEnabled = XSPUtils.isDropdownStatusBarClockColorEnabled(xsp);
        if (mDropdownStatusBarClockColorEnabled) {
            mDropdownStatusBarClockColor = XSPUtils.getDropdownStatusBarClockColor(xsp);
            mDropdownStatusBarDateColor = XSPUtils.getDropdownStatusBarDateColor(xsp);
        }

        mBlockSystemTimeTick = mShowSecInStatusBar || mShowSecInDropdownStatusBar;
    }

    @Override
    public void startHook() {
        try {
            XLog.d("Hooking StatusBar Clock...");
            mMiuiClockClass = XposedHelpers.findClass(CLASS_MIUI_CLOCK, mClassLoader);

            hookClockConstructor();

            if (mShowSecInStatusBar || mStatusBarClockFormatEnabled || mShowSecInDropdownStatusBar) {
                if (mBlockSystemTimeTick) {
                    hookStatusBar();
                    hookOnAttachedToWindow();
                }
                hookClockUpdateTime();
            }

            if (mStatusBarClockColorEnabled) {
                hookOnDarkChanged();
            }
        } catch (Throwable t) {
            XLog.e("Error occurs when hook StatusBar Clock", t);
        }
    }

    // com.android.systemui.statusbar.policy.MiuiClock#updateTime
    private void hookClockUpdateTime() {
        XposedWrapper.findAndHookMethod(mMiuiClockClass,
                "updateTime",
                new MethodHookWrapper() {
                    @Override
                    protected void after(MethodHookParam param) {
                        TextView clockView = (TextView) param.thisObject;
                        Resources res = clockView.getResources();
                        int id = clockView.getId();
                        String timeStr = clockView.getText().toString();
                        if (id == ResHelpers.getId(res, "clock")) {
                            // 状态栏时钟，其id是 clock
                            if (mStatusBarClockFormatEnabled) {
                                timeStr = getCustomFormatTime();
                            } else if (mShowSecInStatusBar) {
                                timeStr = addInSecond(timeStr);
                            } else {
                                return;
                            }
                        } else if (id == ResHelpers.getId(res, "big_time")
                                || id == ResHelpers.getId(res, "date_time")) {
                            if (mShowSecInDropdownStatusBar) {
                                timeStr = addInSecond(timeStr);
                            } else {
                                return;
                            }
                        } else {
                            return;
                        }
                        clockView.setText(timeStr);
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

    // com.android.systemui.statusbar.policy.MiuiClock#constructor()
    private void hookClockConstructor() {
        hookAllConstructors(mMiuiClockClass,
                new MethodHookWrapper() {
                    @Override
                    protected void after(MethodHookParam param) {
                        TextView clock = (TextView) param.thisObject;
                        Resources res = clock.getResources();
                        int id = clock.getId();
                        if (id == ResHelpers.getId(res, "clock")) {
                            // 状态栏时钟，其id是 clock
                            if (mBlockSystemTimeTick) {
                                addClock(clock);
                            }
                            if (mStatusBarClockColorEnabled) {
                                clock.setTextColor(mStatusBarClockColor);
                            }
                        } else if (id == ResHelpers.getId(res, "big_time")) {
                            if (mBlockSystemTimeTick) {
                                addClock(clock);
                            }
                            if (mDropdownStatusBarClockColorEnabled) {
                                clock.setTextColor(mDropdownStatusBarClockColor);
                            }
                        } else if (id == ResHelpers.getId(res, "date_time")) {
                            if (mBlockSystemTimeTick) {
                                addClock(clock);
                            }
                            if (mDropdownStatusBarClockColorEnabled) {
                                clock.setTextColor(mDropdownStatusBarDateColor);
                            }
                        }
                    }
                });
    }

    private void hookOnDarkChanged() {
        if (mStatusBarClockColorEnabled) {
            String methodName = "onDarkChanged";
            Method targetMethod = XposedWrapper.findMethodByNameIfExists(mMiuiClockClass, methodName);

            if (targetMethod == null) {
                XLog.e("method %s#%s not found", CLASS_MIUI_CLOCK, methodName);
                return;
            }

            XposedBridge.hookMethod(targetMethod,
                    new XC_MethodReplacement() {
                        @Override
                        protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                            return null;
                        }
                    });
        }
    }

    private void hookStatusBar() {
        hookMakeStatusBarView();
    }

    // com.android.systemui.statusbar.phone.StatusBar#makeStatusBarView()
    private void hookMakeStatusBarView() {
        Class<?> statusBarClass = XposedHelpers.findClass(CLASS_STATUS_BAR, mClassLoader);
        // Android 11 起，出现 RegisterStatusBarResult 类
        Class<?> registerStatusBarResultClass = XposedHelpers.findClassIfExists(CLASS_REGISTER_STATUS_BAR_RESULT, mClassLoader);

        Method targetMethod = XposedHelpers.findMethodBestMatch(
                statusBarClass,
                "makeStatusBarView",
                registerStatusBarResultClass
        );

        XposedBridge.hookMethod(targetMethod, new MethodHookWrapper() {
            @Override
            protected void after(MethodHookParam param) {
                Object statusBar = param.thisObject;
                Context context = (Context) XposedHelpers.getObjectField(statusBar, "mContext");

                // register screen on/off listener
                ScreenBroadcastManager.getInstance(context).registerListener(mScreenListener);
            }
        });
    }

    // Hook onAttachedToWindow()
    private void hookOnAttachedToWindow() {
        Method targetMethod = XposedWrapper.findMethodByNameIfExists(mMiuiClockClass, "onAttachedToWindow");
        XposedBridge.hookMethod(targetMethod, new MethodHookWrapper() {
            @Override
            protected void after(MethodHookParam param) {
                Object thisObject = param.thisObject;

                Object mIntentReceiver = XposedHelpers.getObjectField(thisObject, "mIntentReceiver");
                Object mBroadcastDispatcher = XposedHelpers.getObjectField(thisObject, "mBroadcastDispatcher");
                // 先取消注册原 Receiver (带了 TIME_TICK 广播)
                XposedHelpers.callMethod(mBroadcastDispatcher, "unregisterReceiver", mIntentReceiver);

                // 不再接收 TIME_TICK 广播
                IntentFilter intentFilter = new IntentFilter();
                // intentFilter.addAction("android.intent.action.TIME_TICK");
                intentFilter.addAction("android.intent.action.TIME_SET");
                intentFilter.addAction("android.intent.action.TIMEZONE_CHANGED");
                intentFilter.addAction("android.intent.action.CONFIGURATION_CHANGED");
                intentFilter.addAction("android.intent.action.USER_SWITCHED");
                Object USER_HANDLE_ALL = XposedHelpers.getStaticObjectField(UserHandle.class, "ALL");
                Class<?> dependencyClass = XposedHelpers.findClass(CLASS_DEPENDENCY, mClassLoader);
                Object TIME_TICK_HANDLER = XposedHelpers.getStaticObjectField(dependencyClass, "TIME_TICK_HANDLER");
                Object handler = XposedHelpers.callStaticMethod(dependencyClass, "get", TIME_TICK_HANDLER);
                // this.mBroadcastDispatcher.registerReceiverWithHandler(this.mIntentReceiver, intentFilter,
                //  (Handler) Dependency.get(Dependency.TIME_TICK_HANDLER), UserHandle.ALL);
                XposedHelpers.callMethod(mBroadcastDispatcher,
                        "registerReceiverWithHandler",
                        mIntentReceiver,
                        intentFilter,
                        handler,
                        USER_HANDLE_ALL);
            }
        });
    }

    private synchronized void addClock(Object clock) {
        if (mClockViewSet.isEmpty()) {
            TimeTicker.get().registerObserver(StatusBarClockHook20201109.this);
        }

        mClockViewSet.add(clock);
    }

    @Override
    public void onTimeTick() {
        for (Object clockView : mClockViewSet) {
            if (clockView != null) {
                XposedHelpers.callMethod(clockView, "updateClock");
            }
        }
    }

    private final ScreenListener mScreenListener = new SimpleScreenListener() {
        @Override
        public void onScreenOn() {
            TimeTicker.get().registerObserver(StatusBarClockHook20201109.this);
        }

        @Override
        public void onScreenOff() {
            TimeTicker.get().unregisterObserver(StatusBarClockHook20201109.this);
        }
    };
}
