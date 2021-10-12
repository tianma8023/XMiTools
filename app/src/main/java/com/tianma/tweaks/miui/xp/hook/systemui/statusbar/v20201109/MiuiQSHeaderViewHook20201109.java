package com.tianma.tweaks.miui.xp.hook.systemui.statusbar.v20201109;

import android.content.Context;
import android.content.pm.PackageManager;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tianma.tweaks.miui.BuildConfig;
import com.tianma.tweaks.miui.data.sp.XPrefContainer;
import com.tianma.tweaks.miui.utils.ResolutionUtils;
import com.tianma.tweaks.miui.utils.XLog;
import com.tianma.tweaks.miui.xp.hook.BaseSubHook;
import com.tianma.tweaks.miui.xp.hook.systemui.weather.WeatherMonitor;
import com.tianma.tweaks.miui.xp.hook.systemui.weather.WeatherObserver;
import com.tianma.tweaks.miui.xp.utils.appinfo.AppInfo;
import com.tianma.tweaks.miui.xp.wrapper.MethodHookWrapper;
import com.tianma.tweaks.miui.xp.wrapper.XposedWrapper;

import de.robv.android.xposed.XSharedPreferences;

/**
 * 下拉状态栏头部View Hook（下拉状态栏显示天气等)，适用版本 MiuiSystemUI(versionCode>=202011090)
 */
public class MiuiQSHeaderViewHook20201109 extends BaseSubHook implements WeatherObserver {

    private static final String CLASS_HEADER_VIEW = "com.android.systemui.qs.MiuiQSHeaderView";

    private Class<?> mHeaderViewClass;

    private Context mModContext;
    private Context mAppContext;

    private TextView mWeatherInfoTv;

    private final boolean mWeatherEnabled;
    private int mWeatherTextColor;
    private float mWeatherTextSize;

    public MiuiQSHeaderViewHook20201109(ClassLoader classLoader, XSharedPreferences xsp, AppInfo appInfo) {
        super(classLoader, xsp, appInfo);

        // mWeatherEnabled = XSPUtils.isDropdownStatusBarWeatherEnabled(xsp);
        mWeatherEnabled = XPrefContainer.isDropdownStatusBarWeatherEnabled();
        if (mWeatherEnabled) {
            // mWeatherTextColor = XSPUtils.getDropdownStatusBarWeatherTextColor(xsp);
            mWeatherTextColor = XPrefContainer.getDropdownStatusBarWeatherTextColor();
            // mWeatherTextSize = XSPUtils.getDropdownStatusBarWeatherTextSize(xsp);
            mWeatherTextSize = XPrefContainer.getDropdownStatusBarWeatherTextSize();
        }
    }

    @Override
    public void startHook() {
        XLog.d("Hooking MiuiQSHeaderView...");
        if (mWeatherEnabled) {
            mHeaderViewClass = XposedWrapper.findClass(CLASS_HEADER_VIEW, mClassLoader);
            if (mHeaderViewClass != null) {
                hookConstructor();
                hookOnFinishInflate();
                hookOnAttachedToWindow();
                hookOnDetachedFromWindow();
            }
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
        XposedWrapper.findAndHookMethod(mHeaderViewClass,
                "onFinishInflate",
                new MethodHookWrapper() {
                    @Override
                    protected void after(MethodHookParam param) {
                        // MiuiQSHeaderView extends RelativeLayout
                        ViewGroup miuiQSHeaderView = (ViewGroup) param.thisObject;

                        // 右上角齿轮快捷按钮
                        ImageView shortcutView = (ImageView) miuiQSHeaderView.getChildAt(miuiQSHeaderView.getChildCount() - 1);

                        mWeatherInfoTv = new TextView(miuiQSHeaderView.getContext());
                        RelativeLayout.LayoutParams weatherInfoLp = new RelativeLayout.LayoutParams(
                                RelativeLayout.LayoutParams.WRAP_CONTENT,
                                RelativeLayout.LayoutParams.WRAP_CONTENT
                        );
                        weatherInfoLp.addRule(RelativeLayout.START_OF, shortcutView.getId());
                        weatherInfoLp.addRule(RelativeLayout.ALIGN_TOP, shortcutView.getId());
                        weatherInfoLp.addRule(RelativeLayout.ALIGN_BOTTOM, shortcutView.getId());
                        weatherInfoLp.setMarginEnd((int) ResolutionUtils.dp2px(miuiQSHeaderView.getContext(), 3));
                        mWeatherInfoTv.setLayoutParams(weatherInfoLp);

                        mWeatherInfoTv.setIncludeFontPadding(false);
                        mWeatherInfoTv.setGravity(Gravity.CENTER);

                        mWeatherInfoTv.setTextColor(mWeatherTextColor);
                        mWeatherInfoTv.setTextSize(mWeatherTextSize);

                        miuiQSHeaderView.addView(mWeatherInfoTv);
                    }
                });
    }

    // HeaderView#onAttachedToWindow()
    private void hookOnAttachedToWindow() {
        XposedWrapper.findAndHookMethod(mHeaderViewClass,
                "onAttachedToWindow",
                new MethodHookWrapper() {
                    @Override
                    protected void after(MethodHookParam param) {
                        WeatherMonitor.get(mAppContext).registerObserver(MiuiQSHeaderViewHook20201109.this);
                    }
                });
    }

    // HeaderView#onDetachedFromWindow()
    private void hookOnDetachedFromWindow() {
        XposedWrapper.findAndHookMethod(mHeaderViewClass,
                "onDetachedFromWindow",
                new MethodHookWrapper() {
                    @Override
                    protected void after(MethodHookParam param) {
                        WeatherMonitor.get(mAppContext).unregisterObserver(MiuiQSHeaderViewHook20201109.this);
                    }
                });
    }

    @Override
    public void onWeatherChanged(String newWeatherInfo) {
        XLog.d("onWeatherChanged: %s", newWeatherInfo);
        if (mWeatherInfoTv != null) {
            mWeatherInfoTv.setText(newWeatherInfo);
        }
    }
}
