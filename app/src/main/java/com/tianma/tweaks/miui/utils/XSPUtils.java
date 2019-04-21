package com.tianma.tweaks.miui.utils;

import de.robv.android.xposed.XSharedPreferences;

import static com.tianma.tweaks.miui.cons.PrefConst.ALIGNMENT_LEFT;
import static com.tianma.tweaks.miui.cons.PrefConst.SHOW_SEC_IN_KEYGUARD_HORIZONTAL;
import static com.tianma.tweaks.miui.cons.PrefConst.SHOW_SEC_IN_KEYGUARD_HORIZONTAL_DEFAULT;
import static com.tianma.tweaks.miui.cons.PrefConst.SHOW_SEC_IN_KEYGUARD_VERTICAL;
import static com.tianma.tweaks.miui.cons.PrefConst.SHOW_SEC_IN_KEYGUARD_VERTICAL_DEFAULT;
import static com.tianma.tweaks.miui.cons.PrefConst.SHOW_SEC_IN_STATUS_BAR;
import static com.tianma.tweaks.miui.cons.PrefConst.SHOW_SEC_IN_STATUS_BAR_DEFAULT;
import static com.tianma.tweaks.miui.cons.PrefConst.STATUS_BAR_CLOCK_ALIGNMENT;

public class XSPUtils {

    private XSPUtils() {
    }

    public static boolean showSecInStatusBar(XSharedPreferences xsp) {
        return xsp.getBoolean(SHOW_SEC_IN_STATUS_BAR, SHOW_SEC_IN_STATUS_BAR_DEFAULT);
    }

    public static String getStatusBarClockAlignment(XSharedPreferences xsp) {
        return xsp.getString(STATUS_BAR_CLOCK_ALIGNMENT, ALIGNMENT_LEFT);
    }

    public static boolean showSecInKeyguardHorizontal(XSharedPreferences xsp) {
        return xsp.getBoolean(SHOW_SEC_IN_KEYGUARD_HORIZONTAL, SHOW_SEC_IN_KEYGUARD_HORIZONTAL_DEFAULT);
    }

    public static boolean showSecInKeyguardVertical(XSharedPreferences xsp) {
        return xsp.getBoolean(SHOW_SEC_IN_KEYGUARD_VERTICAL, SHOW_SEC_IN_KEYGUARD_VERTICAL_DEFAULT);
    }
}
