package com.tianma.tweaks.miui.xp.hook.systemui.statusbar;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.tianma.tweaks.miui.utils.XLog;
import com.tianma.tweaks.miui.utils.XSPUtils;
import com.tianma.tweaks.miui.xp.hook.BaseSubHook;
import com.tianma.tweaks.miui.xp.hook.systemui.helper.ResHelpers;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedHelpers;

public class CollapsedStatusBarFragmentHook extends BaseSubHook {

    private static final String CLASS_STATUS_BAR_FRAGMENT = "com.android.systemui.statusbar.phone.CollapsedStatusBarFragment";

    private boolean mSignalAlignLeft;
    private boolean mAlwaysShowStatusBarClock;

    public CollapsedStatusBarFragmentHook(ClassLoader classLoader, XSharedPreferences xsp) {
        super(classLoader, xsp);

        mSignalAlignLeft = XSPUtils.isSignalAlignLeft(xsp);
        mAlwaysShowStatusBarClock = XSPUtils.alwaysShowStatusBarClock(xsp);
    }

    @Override
    public void startHook() {
        try {
            XLog.d("Hooking CollapsedStatusBarFragment... ");
            if (mSignalAlignLeft) {
                hookCollapsedStatusBarFragment();
            }

            if (mAlwaysShowStatusBarClock) {
                hookClockVisibleAnimate();
            }
        } catch (Throwable t) {
            XLog.e("Error occurs when hook CollapsedStatusBarFragment", t);
        }
    }

    // CollapsedStatusBarFragment#onViewCreated()
    private void hookCollapsedStatusBarFragment() {
        XposedHelpers.findAndHookMethod(CLASS_STATUS_BAR_FRAGMENT,
                mClassLoader,
                "onViewCreated",
                View.class,
                Bundle.class,
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        try {
                            ViewGroup phoneStatusBarView = (ViewGroup) XposedHelpers.getObjectField(param.thisObject, "mStatusBar");
                            Resources res = phoneStatusBarView.getResources();

                            View signalClusterView = phoneStatusBarView
                                    .findViewById(ResHelpers.getId(res, "signal_cluster"));
                            ((ViewGroup) signalClusterView.getParent()).removeView(signalClusterView);

                            ViewGroup contentsContainer = phoneStatusBarView
                                    .findViewById(ResHelpers.getId(res, "phone_status_bar_contents_container"));
                            contentsContainer.addView(signalClusterView, 0);
                        } catch (Throwable t) {
                            XLog.e("", t);
                        }
                    }
                });
    }

    private void hookClockVisibleAnimate() {
        XposedHelpers.findAndHookMethod(CLASS_STATUS_BAR_FRAGMENT,
                mClassLoader,
                "clockVisibleAnimate",
                boolean.class,
                boolean.class,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        View mStatusClock = (View) XposedHelpers.getObjectField(param.thisObject, "mStatusClock");
                        mStatusClock.setVisibility(View.VISIBLE);
                        param.setResult(null);
                    }
                });
    }

}
