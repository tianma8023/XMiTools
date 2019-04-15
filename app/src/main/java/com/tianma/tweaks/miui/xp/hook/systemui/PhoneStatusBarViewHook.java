package com.tianma.tweaks.miui.xp.hook.systemui;

import android.content.Context;
import android.content.res.Resources;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tianma.tweaks.miui.utils.XLog;
import com.tianma.tweaks.miui.utils.XSPUtils;
import com.tianma.tweaks.miui.xp.hook.BaseSubHook;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedHelpers;

/**
 * 状态栏时钟居中显示
 */
public class PhoneStatusBarViewHook extends BaseSubHook {

    private static final String PACKAGE_NAME = SystemUIHook.PACKAGE_NAME;

    private static final String CLASS_PHONE_STATUS_BAR_VIEW = "com.android.systemui.statusbar.phone.PhoneStatusBarView";
    private static final String CLASS_STATUS_BAR = "com.android.systemui.statusbar.phone.StatusBar";

    private static final String CLASS_NOTIFICATION_ICON_CONTAINER = "com.android.systemui.statusbar.phone.NotificationIconContainer";

    private LinearLayout mCenterLayout;

    PhoneStatusBarViewHook(ClassLoader classLoader, XSharedPreferences xsp) {
        super(classLoader, xsp);
    }

    public void startHook() {
        if (XSPUtils.showStatusBarClockInCenter(xsp)) {
            try {
                XLog.d("Hooking PhoneStatusBarView...");
                hookSetBar();
                hookGetActualWidth();
            } catch (Throwable t) {
                XLog.d("Error occurs when hook PhoneStatusBarView", t);
            }
        }
    }

    private void hookSetBar() {
        XposedHelpers.findAndHookMethod(CLASS_PHONE_STATUS_BAR_VIEW,
                mClassLoader,
                "setBar",
                CLASS_STATUS_BAR,
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);

                        prepareLayoutStatusBar(param);
                    }
                });
    }

    private void prepareLayoutStatusBar(XC_MethodHook.MethodHookParam param) {
        // FrameLayout
        ViewGroup phoneStatusBarView = (ViewGroup) param.thisObject;

        Context context = phoneStatusBarView.getContext();
        Resources res = context.getResources();

        LinearLayout statusBarContents = phoneStatusBarView.findViewById(
                res.getIdentifier("status_bar_contents", "id", PACKAGE_NAME));

        // 注入新的居中的layout 到 phoneStatusBarView 中去
        mCenterLayout = new LinearLayout(context);
        LinearLayout.LayoutParams lp =
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        mCenterLayout.setLayoutParams(lp);
        mCenterLayout.setGravity(Gravity.CENTER);
        phoneStatusBarView.addView(mCenterLayout);

        TextView clock = statusBarContents.findViewById(
                res.getIdentifier("clock", "id", PACKAGE_NAME));

        ((ViewGroup) clock.getParent()).removeView(clock);
        clock.setPaddingRelative(2, 0, 2, 0);
        clock.setGravity(Gravity.CENTER);
        mCenterLayout.addView(clock);
    }

    private void hookGetActualWidth() {
        XposedHelpers.findAndHookMethod(CLASS_NOTIFICATION_ICON_CONTAINER,
                mClassLoader,
                "getActualWidth",
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        if (mCenterLayout == null)
                            return;
                        if (mCenterLayout.getChildCount() == 0)
                            return;
                        View clock = mCenterLayout.getChildAt(0);
                        int width = Math.round(mCenterLayout.getWidth() / 2f - clock.getWidth() / 2f) - 8;
                        param.setResult(width);
                    }
                });
    }

}
