package com.tianma.tweaks.miui.xp.hook.systemui.statusbar;

import android.view.View;
import android.widget.TextView;

import com.tianma.tweaks.miui.utils.XLog;
import com.tianma.tweaks.miui.utils.XSPUtils;
import com.tianma.tweaks.miui.xp.hook.BaseSubHook;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.getObjectField;
import static de.robv.android.xposed.XposedHelpers.getSurroundingThis;
import static de.robv.android.xposed.XposedHelpers.setBooleanField;

public class SignalClusterViewHook extends BaseSubHook {

    private static final String PKG_STATUS_BAR = "com.android.systemui.statusbar";
    private static final String CLASS_SIGNAL_CLUSTER_VIEW = PKG_STATUS_BAR + ".SignalClusterView";
    private static final String CLASS_PHONE_STATE = CLASS_SIGNAL_CLUSTER_VIEW + "$PhoneState";

    private boolean mDualMobileSignal;
    private boolean mHideVpnIcon;

    private boolean mCustomMobileNetworkTypeEnabled;
    private String mCustomMobileNetworkType = "";

    public SignalClusterViewHook(ClassLoader classLoader, XSharedPreferences xsp) {
        super(classLoader, xsp);

        mDualMobileSignal = XSPUtils.isDualMobileSignal(xsp);
        mHideVpnIcon = XSPUtils.isHideVpnIcon(xsp);

        mCustomMobileNetworkTypeEnabled = XSPUtils.customMobileNetworkEnabled(xsp);
        if (mCustomMobileNetworkTypeEnabled) {
            mCustomMobileNetworkType = XSPUtils.customMobileNetwork(xsp);
        }
    }

    @Override
    public void startHook() {
        try {
            XLog.d("Hooking SignalClusterView... ");
            if (mDualMobileSignal) {
                hookUpdateSwitches();
                hookUpdateNotchEar();
            }

            if (mHideVpnIcon) {
                hookApply();
            }

            if (mCustomMobileNetworkTypeEnabled) {
                hookPhoneStateApply();
            }
        } catch (Throwable t) {
            XLog.e("Error occurs when hook SignalClusterView", t);
        }
    }

    // SignalClusterView#updateSwitches()
    private void hookUpdateSwitches() {
        findAndHookMethod(CLASS_SIGNAL_CLUSTER_VIEW,
                mClassLoader,
                "updateSwitches",
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        try {
                            Object signalClusterView = param.thisObject;
                            setBooleanField(signalClusterView, "mNotchEarDualEnable", true);
                        } catch (Throwable t) {
                            XLog.e("", t);
                        }
                    }
                });
    }

    // SignalClusterView#updateNotchEar()
    private void hookUpdateNotchEar() {
        findAndHookMethod(CLASS_SIGNAL_CLUSTER_VIEW,
                mClassLoader,
                "updateNotchEar",
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        try {
                            Object signalClusterView = param.thisObject;
                            setBooleanField(signalClusterView, "mNotchEar", true);
                        } catch (Throwable t) {
                            XLog.e("", t);
                        }
                    }
                });
    }

    // SignalClusterView#apply()
    private void hookApply() {
        findAndHookMethod(CLASS_SIGNAL_CLUSTER_VIEW,
                mClassLoader,
                "apply",
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        try {
                            Object signalClusterView = param.thisObject;
                            View mVpn = (View) getObjectField(signalClusterView, "mVpn");
                            if (mVpn != null && mVpn.getVisibility() != View.GONE) {
                                mVpn.setVisibility(View.GONE);
                            }
                        } catch (Throwable t) {
                            XLog.e("", t);
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
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        try {
                            Object phoneState = param.thisObject;
                            Object signalClusterView = getSurroundingThis(phoneState);

                            TextView mMobileType = (TextView) getObjectField(phoneState, "mMobileType");
                            TextView mSignalDualNotchMobileType = (TextView) getObjectField(signalClusterView, "mSignalDualNotchMobileType");

                            mMobileType.setText(mCustomMobileNetworkType);
                            mSignalDualNotchMobileType.setText(mCustomMobileNetworkType);
                        } catch (Throwable t) {
                            XLog.e("", t);
                        }
                    }
                });
    }
}
