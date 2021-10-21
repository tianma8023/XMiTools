package com.tianma.tweaks.miui.xp.hook.systemui.statusbar.def;

import static com.tianma.tweaks.miui.xp.wrapper.XposedWrapper.findAndHookMethod;

import android.content.Context;
import android.content.res.Resources;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tianma.tweaks.miui.cons.PrefConst;
import com.tianma.tweaks.miui.data.sp.XPrefContainer;
import com.tianma.tweaks.miui.utils.XLogKt;
import com.tianma.tweaks.miui.xp.hook.BaseSubHook;
import com.tianma.tweaks.miui.xp.hook.systemui.SystemUIHook;
import com.tianma.tweaks.miui.xp.utils.appinfo.AppInfo;
import com.tianma.tweaks.miui.xp.wrapper.MethodHookWrapper;

import de.robv.android.xposed.XC_MethodHook;

/**
 * 状态栏时钟居中显示
 * 适用版本 9.4.x+
 */
public class PhoneStatusBarViewHook extends BaseSubHook {

    private static final String PACKAGE_NAME = SystemUIHook.PACKAGE_NAME;

    private static final String CLASS_PHONE_STATUS_BAR_VIEW = "com.android.systemui.statusbar.phone.PhoneStatusBarView";
    private static final String CLASS_STATUS_BAR = "com.android.systemui.statusbar.phone.StatusBar";

    private static final String CLASS_NOTIFICATION_ICON_CONTAINER = "com.android.systemui.statusbar.phone.NotificationIconContainer";

    private LinearLayout mCenterLayout;

    private boolean mAlignmentCenter = false;
    private boolean mAlignmentRight = false;

    public PhoneStatusBarViewHook(ClassLoader classLoader, AppInfo appInfo) {
        super(classLoader, appInfo);
        // String alignment = XSPUtils.getStatusBarClockAlignment(xsp);
        String alignment = XPrefContainer.getStatusBarClockAlignment();
        if (PrefConst.ALIGNMENT_CENTER.equals(alignment)) {
            mAlignmentCenter = true;
            mAlignmentRight = false;
        } else if (PrefConst.ALIGNMENT_RIGHT.equals(alignment)) {
            mAlignmentCenter = false;
            mAlignmentRight = true;
        }
    }

    public void startHook() {
        if (mAlignmentCenter || mAlignmentRight) {
            try {
                XLogKt.logD("Hooking PhoneStatusBarView...");
                hookSetBar();
                if (mAlignmentCenter) {
                    hookGetActualWidth();
                }
            } catch (Throwable t) {
                XLogKt.logE("Error occurs when hook PhoneStatusBarView", t);
            }
        }
    }

    private void hookSetBar() {
        findAndHookMethod(CLASS_PHONE_STATUS_BAR_VIEW,
                mClassLoader,
                "setBar",
                CLASS_STATUS_BAR,
                new MethodHookWrapper() {
                    @Override
                    protected void after(MethodHookParam param) {
                        prepareLayoutStatusBar(param);
                    }
                });
    }

    private void prepareLayoutStatusBar(XC_MethodHook.MethodHookParam param) {
        // FrameLayout
        ViewGroup phoneStatusBarView = (ViewGroup) param.thisObject;

        Context context = phoneStatusBarView.getContext();
        Resources res = context.getResources();

        //        LinearLayout statusBarContents = phoneStatusBarView.findViewById(
        //                res.getIdentifier("status_bar_contents", "id", PACKAGE_NAME));

        TextView clock = phoneStatusBarView.findViewById(
                res.getIdentifier("clock", "id", PACKAGE_NAME));
        ((ViewGroup) clock.getParent()).removeView(clock);

        if (mAlignmentCenter) {
            // 注入新的居中的layout 到 phoneStatusBarView 中去
            mCenterLayout = new LinearLayout(context);
            LinearLayout.LayoutParams lp =
                    new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            mCenterLayout.setLayoutParams(lp);
            mCenterLayout.setGravity(Gravity.CENTER);
            phoneStatusBarView.addView(mCenterLayout);

            clock.setPaddingRelative(2, 0, 2, 0);
            clock.setGravity(Gravity.CENTER);
            mCenterLayout.addView(clock);
        } else if (mAlignmentRight) { // 居右对齐
            LinearLayout rightAreaLayout = phoneStatusBarView.findViewById(
                    res.getIdentifier("system_icons", "id", PACKAGE_NAME));
            clock.setGravity(Gravity.END | Gravity.CENTER_VERTICAL);
            rightAreaLayout.addView(clock);
        }
    }

    private void hookGetActualWidth() {
        findAndHookMethod(CLASS_NOTIFICATION_ICON_CONTAINER,
                mClassLoader,
                "getActualWidth",
                new MethodHookWrapper() {
                    @Override
                    protected void after(MethodHookParam param) {
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
