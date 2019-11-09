package com.tianma.tweaks.miui.xp.hook.systemui.statusbar;

import android.content.Context;
import android.content.pm.PackageManager;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tianma.tweaks.miui.BuildConfig;
import com.tianma.tweaks.miui.utils.XLog;
import com.tianma.tweaks.miui.utils.XSPUtils;
import com.tianma.tweaks.miui.utils.rom.MiuiVersion;
import com.tianma.tweaks.miui.xp.hook.BaseSubHook;
import com.tianma.tweaks.miui.xp.hook.systemui.helper.ResHelpers;
import com.tianma.tweaks.miui.xp.hook.systemui.weather.WeatherMonitor;
import com.tianma.tweaks.miui.xp.hook.systemui.weather.WeatherObserver;
import com.tianma.tweaks.miui.xp.wrapper.MethodHookWrapper;
import com.tianma.tweaks.miui.xp.wrapper.XposedWrapper;

import de.robv.android.xposed.XSharedPreferences;

import static com.tianma.tweaks.miui.xp.wrapper.XposedWrapper.findAndHookMethod;
import static com.tianma.tweaks.miui.xp.wrapper.XposedWrapper.findClass;

/**
 * 下拉状态栏头部View Hook（下拉状态栏显示天气等）
 */
public class HeaderViewHook extends BaseSubHook implements WeatherObserver {

    private static final String CLASS_HEADER_VIEW = "com.android.systemui.statusbar.HeaderView";

    private Class<?> mHeaderViewClass;

    private Context mModContext;
    private Context mAppContext;

    private TextView mWeatherInfoTv;

    private boolean mWeatherEnabled;
    private int mWeatherTextColor;
    private float mWeatherTextSize;

    public HeaderViewHook(ClassLoader classLoader, XSharedPreferences xsp, MiuiVersion miuiVersion) {
        super(classLoader, xsp, miuiVersion);

        mWeatherEnabled = XSPUtils.isDropdownStatusBarWeatherEnabled(xsp);
        if (mWeatherEnabled) {
            mWeatherTextColor = XSPUtils.getDropdownStatusBarWeatherTextColor(xsp);
            mWeatherTextSize = XSPUtils.getDropdownStatusBarWeatherTextSize(xsp);
        }
    }

    @Override
    public void startHook() {
        XLog.d("Hooking HeaderView...");
        if (mWeatherEnabled) {
            mHeaderViewClass = findClass(CLASS_HEADER_VIEW, mClassLoader);
            hookConstructor();
            hookOnFinishInflate();
            hookOnAttachedToWindow();
            hookOnDetachedFromWindow();
        }
    }

    // HeaderView#constructor()
    private void hookConstructor() {
        XposedWrapper.hookAllConstructors(mHeaderViewClass,
                new MethodHookWrapper() {
                    @Override
                    protected void after(MethodHookParam param) {
                        if (mAppContext == null) {
                            View headerView = (View) param.thisObject;
                            mModContext = headerView.getContext().getApplicationContext();
                            try {
                                mAppContext = mModContext
                                        .createPackageContext(BuildConfig.APPLICATION_ID, Context.CONTEXT_IGNORE_SECURITY);
                            } catch (PackageManager.NameNotFoundException e) {
                                // ignore
                            }
                        }
                    }
                });
    }

    // HeaderView#onFinishInflate()
    private void hookOnFinishInflate() {
        findAndHookMethod(mHeaderViewClass,
                "onFinishInflate",
                new MethodHookWrapper() {
                    @Override
                    protected void after(MethodHookParam param) {
                        ViewGroup headerView = (ViewGroup) param.thisObject;
                        LinearLayout weatherContainer = (LinearLayout) headerView.getChildAt(headerView.getChildCount() - 1);
                        weatherContainer.setGravity(Gravity.CENTER_VERTICAL);

                        mWeatherInfoTv = new TextView(headerView.getContext());
                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        mWeatherInfoTv.setLayoutParams(lp);
                        mWeatherInfoTv.setTextColor(mWeatherTextColor);
                        mWeatherInfoTv.setTextSize(mWeatherTextSize);

                        weatherContainer.addView(mWeatherInfoTv, 0);

                        // 右上角齿轮快捷按钮
                        View shortcutView = weatherContainer.findViewById(ResHelpers.getId(headerView.getResources(), "notification_shade_shortcut"));
                        if (shortcutView == null) {
                            shortcutView = weatherContainer.getChildAt(weatherContainer.getChildCount() - 1);
                        }
                        LinearLayout.LayoutParams shortcutLP = (LinearLayout.LayoutParams) shortcutView.getLayoutParams();
                        if (shortcutLP.bottomMargin > 0) {
                            shortcutLP.bottomMargin = 0;
                        }
                    }
                });
    }

    // HeaderView#onAttachedToWindow()
    private void hookOnAttachedToWindow() {
        findAndHookMethod(mHeaderViewClass,
                "onAttachedToWindow",
                new MethodHookWrapper() {
                    @Override
                    protected void after(MethodHookParam param) {
                        WeatherMonitor.get(mAppContext).registerObserver(HeaderViewHook.this);
                    }
                });
    }

    // HeaderView#onDetachedFromWindow()
    private void hookOnDetachedFromWindow() {
        findAndHookMethod(mHeaderViewClass,
                "onDetachedFromWindow",
                new MethodHookWrapper() {
                    @Override
                    protected void after(MethodHookParam param) {
                        WeatherMonitor.get(mAppContext).unregisterObserver(HeaderViewHook.this);
                    }
                });
    }

    @Override
    public void onWeatherChanged(String newWeatherInfo) {
        if (mWeatherInfoTv != null) {
            mWeatherInfoTv.setText(newWeatherInfo);
        }
    }
}
