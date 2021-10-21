package com.tianma.tweaks.miui.xp.hook.systemui.statusbar.v20201109;

import static com.tianma.tweaks.miui.xp.wrapper.XposedWrapper.findAndHookMethod;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.tianma.tweaks.miui.data.sp.XPrefContainer;
import com.tianma.tweaks.miui.utils.XLogKt;
import com.tianma.tweaks.miui.xp.hook.BaseSubHook;
import com.tianma.tweaks.miui.xp.utils.appinfo.AppInfo;
import com.tianma.tweaks.miui.xp.wrapper.MethodHookWrapper;

import de.robv.android.xposed.XposedHelpers;

public class CollapsedStatusBarFragmentHook20201109 extends BaseSubHook {

    private static final String CLASS_STATUS_BAR_FRAGMENT = "com.android.systemui.statusbar.phone.CollapsedStatusBarFragment";

    private boolean mSignalAlignLeft;
    private boolean mAlwaysShowStatusBarClock;

    public CollapsedStatusBarFragmentHook20201109(ClassLoader classLoader, AppInfo appInfo) {
        super(classLoader, appInfo);

        // mSignalAlignLeft = XSPUtils.isSignalAlignLeft(xsp);
        mSignalAlignLeft = XPrefContainer.isSignalAlignLeft();
        // mAlwaysShowStatusBarClock = XSPUtils.alwaysShowStatusBarClock(xsp);
        mAlwaysShowStatusBarClock = XPrefContainer.getAlwaysShowStatusBarClock();
    }

    @Override
    public void startHook() {
        try {
            XLogKt.logD("Hooking CollapsedStatusBarFragment... ");
            if (mSignalAlignLeft) {
                hookOnViewCreated();
            }

            if (mAlwaysShowStatusBarClock) {
                hookClockVisibleAnimate();
            }
        } catch (Throwable t) {
            XLogKt.logE("Error occurs when hook CollapsedStatusBarFragment", t);
        }
    }

    // CollapsedStatusBarFragment#onViewCreated()
    private void hookOnViewCreated() {
        findAndHookMethod(CLASS_STATUS_BAR_FRAGMENT,
                mClassLoader,
                "onViewCreated",
                View.class,
                Bundle.class,
                new MethodHookWrapper() {
                    @Override
                    protected void after(MethodHookParam param) {
                        ViewGroup phoneStatusBarView = (ViewGroup) XposedHelpers.getObjectField(param.thisObject, "mStatusBar");
                        // XLogKt.logE("PhoneStatusBarView:");
                        // DebugHelper.printViewTree(phoneStatusBarView);

                        Resources res = phoneStatusBarView.getResources();

                        // TODO
//                        View headsUpStatusBarView = phoneStatusBarView.findViewById(ResHelpers.getId(res, "heads_up_status_bar_view"));
//                        headsUpStatusBarView.setBackgroundColor(Color.parseColor("#FF0000"));
//
//                        View statusBarLeftSide = phoneStatusBarView.findViewById(
//                                ResHelpers.getId(res, "status_bar_left_side")
//                        );
//                        statusBarLeftSide.setBackgroundColor(Color.parseColor("#00FF00"));
//
//                        View systemIconArea = phoneStatusBarView.findViewById(
//                                ResHelpers.getId(res, "system_icon_area")
//                        );
//                        systemIconArea.setBackgroundColor(Color.parseColor("#0000FF"));
//
//                        // drip_network_speed_view
//                        View dripNetworkSpeedView = phoneStatusBarView.findViewById(
//                                ResHelpers.getId(res, "drip_network_speed_view")
//                        );
//                        dripNetworkSpeedView.setBackgroundColor(Color.parseColor("#FFFF00"));
//
//                        // fullscreen_network_speed_view
//                        View fullScreenNetworkSpeedView = phoneStatusBarView.findViewById(
//                                ResHelpers.getId(res, "fullscreen_network_speed_view")
//                        );
//                        fullScreenNetworkSpeedView.setBackgroundColor(Color.parseColor("#00FFFF"));
                        // TODO
//                        View signalClusterViewContainer = phoneStatusBarView
//                                .findViewById(ResHelpers.getId(res, "signal_cluster_view"));
//                        ((ViewGroup) signalClusterViewContainer.getParent()).removeView(signalClusterViewContainer);
//
//                        if (mMiuiVersion.getTime() >= MiuiVersion.V_19_5_7.getTime()) {
//                            try {
//                                LinearLayout contentsContainer = phoneStatusBarView
//                                        .findViewById(ResHelpers.getId(res, "phone_status_bar_contents_container"));
//                                contentsContainer.setGravity(Gravity.CENTER_VERTICAL);
//                                contentsContainer.addView(signalClusterViewContainer, 0);
//                            } catch (Throwable t) {
//                                LinearLayout statusBarContents = phoneStatusBarView
//                                        .findViewById(ResHelpers.getId(res, "status_bar_contents"));
//                                statusBarContents.setGravity(Gravity.CENTER_VERTICAL);
//                                statusBarContents.addView(signalClusterViewContainer, 0);
//                            }
//                        } else {
//                            LinearLayout statusBarContents = phoneStatusBarView
//                                    .findViewById(ResHelpers.getId(res, "status_bar_contents"));
//                            statusBarContents.setGravity(Gravity.CENTER_VERTICAL);
//                            statusBarContents.addView(signalClusterViewContainer, 0);
//                        }
                    }
                });
    }

    private void hookClockVisibleAnimate() {
        // TODO
        findAndHookMethod(CLASS_STATUS_BAR_FRAGMENT,
                mClassLoader,
                "clockVisibleAnimate",
                boolean.class,
                boolean.class,
                new MethodHookWrapper() {
                    @Override
                    protected void before(MethodHookParam param) {
                        View mStatusClock = (View) XposedHelpers.getObjectField(param.thisObject, "mStatusClock");
                        mStatusClock.setVisibility(View.VISIBLE);
                        param.setResult(null);
                    }
                });
    }

}
