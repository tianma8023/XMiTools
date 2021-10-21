package com.tianma.tweaks.miui.xp.hook.systemui.statusbar.def;

import static com.tianma.tweaks.miui.xp.wrapper.XposedWrapper.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.getObjectField;
import static de.robv.android.xposed.XposedHelpers.getSurroundingThis;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.tianma.tweaks.miui.data.sp.XPrefContainer;
import com.tianma.tweaks.miui.utils.XLogKt;
import com.tianma.tweaks.miui.utils.rom.MiuiVersion;
import com.tianma.tweaks.miui.xp.hook.BaseSubHook;
import com.tianma.tweaks.miui.xp.wrapper.MethodHookWrapper;

import java.util.List;

import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedHelpers;

public class SignalClusterViewHook extends BaseSubHook {

    private static final String PKG_STATUS_BAR = "com.android.systemui.statusbar";
    private static final String CLASS_SIGNAL_CLUSTER_VIEW = PKG_STATUS_BAR + ".SignalClusterView";
    private static final String CLASS_PHONE_STATE = CLASS_SIGNAL_CLUSTER_VIEW + "$PhoneState";

    private boolean mDualMobileSignal;
    private boolean mHideVpnIcon;
    private boolean mHideHDIcon;

    private boolean mCustomMobileNetworkTypeEnabled;
    private String mCustomMobileNetworkType = "";

    public SignalClusterViewHook(ClassLoader classLoader, XSharedPreferences xsp, MiuiVersion miuiVersion) {
        super(classLoader, xsp, miuiVersion);

        // mDualMobileSignal = XSPUtils.isDualMobileSignal(xsp);
        mDualMobileSignal = XPrefContainer.isDualMobileSignal();
        // mHideVpnIcon = XSPUtils.isHideVpnIcon(xsp);
        mHideVpnIcon = XPrefContainer.isHideVpnIcon();
        // mHideHDIcon = XSPUtils.isHideHDIcon(xsp);
        mHideHDIcon = XPrefContainer.isHideHDIcon();

        // mCustomMobileNetworkTypeEnabled = XSPUtils.isCustomMobileNetworkEnabled(xsp);
        mCustomMobileNetworkTypeEnabled = XPrefContainer.isCustomMobileNetworkEnabled();
        if (mCustomMobileNetworkTypeEnabled) {
            // mCustomMobileNetworkType = XSPUtils.customMobileNetwork(xsp);
            mCustomMobileNetworkType = XPrefContainer.getCustomMobileNetwork();
        }
    }

    @Override
    public void startHook() {
        try {
            XLogKt.logD("Hooking SignalClusterView... ");
            if (mDualMobileSignal) {
                hookSetSubs();
            }

            if (mHideVpnIcon) {
                hookApply();
            }

            if (mCustomMobileNetworkTypeEnabled) {
                hookPhoneStateApply();
            }

            if (mHideHDIcon) {
                hookIsImsRegisted();
            }
        } catch (Throwable t) {
            XLogKt.logE("Error occurs when hook SignalClusterView", t);
        }
    }

    // SignalClusterView#hookSetSubs()
    private void hookSetSubs() {
        findAndHookMethod(CLASS_SIGNAL_CLUSTER_VIEW,
                mClassLoader,
                "setSubs",
                List.class,
                new MethodHookWrapper() {
                    @Override
                    protected void before(MethodHookParam param) {
                        XposedHelpers.setBooleanField(param.thisObject, "mNotchEar", true);
                        if (mMiuiVersion.getTime() >= MiuiVersion.V_19_5_7.getTime()) {
                            XposedHelpers.setBooleanField(param.thisObject, "mNotchEarDualEnable", true);
                        }
                    }
                });
    }

    // SignalClusterView#apply()
    private void hookApply() {
        findAndHookMethod(CLASS_SIGNAL_CLUSTER_VIEW,
                mClassLoader,
                "apply",
                new MethodHookWrapper() {
                    @Override
                    protected void after(MethodHookParam param) {
                        Object signalClusterView = param.thisObject;
                        View mVpn = (View) getObjectField(signalClusterView, "mVpn");
                        if (mVpn != null && mVpn.getVisibility() != View.GONE) {
                            mVpn.setVisibility(View.GONE);
                        }
                    }
                });
    }


    // SignalClusterView$PhoneState#apply()
    private void hookPhoneStateApply() {
        findAndHookMethod(CLASS_PHONE_STATE,
                mClassLoader,
                "apply",
                boolean.class,
                new MethodHookWrapper() {
                    @Override
                    protected void after(MethodHookParam param) {
                        Object phoneState = param.thisObject;
                        Object signalClusterView = getSurroundingThis(phoneState);

                        TextView mMobileType = (TextView) getObjectField(phoneState, "mMobileType");
                        TextView mSignalDualNotchMobileType = (TextView) getObjectField(signalClusterView, "mSignalDualNotchMobileType");

                        mMobileType.setText(mCustomMobileNetworkType);
                        mSignalDualNotchMobileType.setText(mCustomMobileNetworkType);
                    }
                });
    }

    // SignalClusterView$PhoneState#isImdRegisted()
    private void hookIsImsRegisted() {
        findAndHookMethod(CLASS_PHONE_STATE,
                mClassLoader,
                "setIsImsRegisted",
                boolean.class,
                new MethodHookWrapper() {
                    @Override
                    protected void after(MethodHookParam param) {
                        Object phoneState = param.thisObject;

                        ImageView mVolte = (ImageView) getObjectField(phoneState, "mVolte");

                        if (mVolte.getVisibility() != View.GONE) {
                            mVolte.setVisibility(View.GONE);
                        }
                    }
                });
    }
}
