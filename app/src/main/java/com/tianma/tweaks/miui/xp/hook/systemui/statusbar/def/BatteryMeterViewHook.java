package com.tianma.tweaks.miui.xp.hook.systemui.statusbar.def;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.widget.TextView;

import com.tianma.tweaks.miui.data.sp.XPrefContainer;
import com.tianma.tweaks.miui.utils.XLogKt;
import com.tianma.tweaks.miui.utils.rom.MiuiVersion;
import com.tianma.tweaks.miui.xp.hook.BaseSubHook;
import com.tianma.tweaks.miui.xp.wrapper.MethodHookWrapper;
import com.tianma.tweaks.miui.xp.wrapper.XposedWrapper;

import de.robv.android.xposed.XposedHelpers;

public class BatteryMeterViewHook extends BaseSubHook {

    private static final String CLASS_BATTERY_VIEW = "com.android.systemui.BatteryMeterView";

    private boolean mShowSmallPercentSign;

    public BatteryMeterViewHook(ClassLoader classLoader, MiuiVersion miuiVersion) {
        super(classLoader, null, miuiVersion);

        // mShowSmallPercentSign = XSPUtils.showSmallBatteryPercentSign(xsp);
        mShowSmallPercentSign = XPrefContainer.getShowSmallBatteryPercentSign();
    }

    @Override
    public void startHook() {
        XLogKt.logD("Hooking BatteryMeterView...");
        if (mShowSmallPercentSign) {
            hookUpdateShowPercent();
        }
    }

    private void hookUpdateShowPercent() {
        XposedWrapper.findAndHookMethod(CLASS_BATTERY_VIEW,
                getMClassLoader(),
                "updateShowPercent",
                new MethodHookWrapper() {
                    @Override
                    protected void after(MethodHookParam param) {

                        TextView mBatteryPercentView = (TextView) XposedHelpers
                                .getObjectField(param.thisObject, "mBatteryPercentView");
                        if (mBatteryPercentView != null) {
                            CharSequence cs = mBatteryPercentView.getText();
                            if (cs == null) {
                                return;
                            }
                            String text = cs.toString();
                            int percentSignIdx = text.indexOf('%');
                            if (percentSignIdx != -1) {
                                SpannableString ss = new SpannableString(text);
                                float originSize = mBatteryPercentView.getTextSize();
                                ss.setSpan(new AbsoluteSizeSpan((int) (originSize * 3 / 4)), percentSignIdx, percentSignIdx + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                mBatteryPercentView.setText(ss);
                            }
                        }
                    }
                });
    }
}
