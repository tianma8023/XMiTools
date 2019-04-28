package com.tianma.tweaks.miui.utils;

import de.robv.android.xposed.XSharedPreferences;

import static com.tianma.tweaks.miui.cons.PrefConst.ALIGNMENT_LEFT;
import static com.tianma.tweaks.miui.cons.PrefConst.STATUS_BAR_CLOCK_COLOR_ENABLE;
import static com.tianma.tweaks.miui.cons.PrefConst.STATUS_BAR_CLOCK_COLOR_ENABLE_DEFAULT;
import static com.tianma.tweaks.miui.cons.PrefConst.DROPDOWN_STATUS_BAR_CLOCK_COLOR;
import static com.tianma.tweaks.miui.cons.PrefConst.DROPDOWN_STATUS_BAR_CLOCK_COLOR_DEFAULT;
import static com.tianma.tweaks.miui.cons.PrefConst.DROPDOWN_STATUS_BAR_DATE_COLOR;
import static com.tianma.tweaks.miui.cons.PrefConst.DROPDOWN_STATUS_BAR_DATE_COLOR_DEFAULT;
import static com.tianma.tweaks.miui.cons.PrefConst.KEYGUARD_CLOCK_COLOR;
import static com.tianma.tweaks.miui.cons.PrefConst.KEYGUARD_CLOCK_COLOR_DEFAULT;
import static com.tianma.tweaks.miui.cons.PrefConst.MAIN_SWITCH;
import static com.tianma.tweaks.miui.cons.PrefConst.MAIN_SWITCH_DEFAULT;
import static com.tianma.tweaks.miui.cons.PrefConst.SHOW_SEC_IN_DROPDOWN_STATUS_BAR;
import static com.tianma.tweaks.miui.cons.PrefConst.SHOW_SEC_IN_DROPDOWN_STATUS_BAR_DEFAULT;
import static com.tianma.tweaks.miui.cons.PrefConst.SHOW_SEC_IN_KEYGUARD_HORIZONTAL;
import static com.tianma.tweaks.miui.cons.PrefConst.SHOW_SEC_IN_KEYGUARD_HORIZONTAL_DEFAULT;
import static com.tianma.tweaks.miui.cons.PrefConst.SHOW_SEC_IN_KEYGUARD_VERTICAL;
import static com.tianma.tweaks.miui.cons.PrefConst.SHOW_SEC_IN_KEYGUARD_VERTICAL_DEFAULT;
import static com.tianma.tweaks.miui.cons.PrefConst.SHOW_SEC_IN_STATUS_BAR;
import static com.tianma.tweaks.miui.cons.PrefConst.SHOW_SEC_IN_STATUS_BAR_DEFAULT;
import static com.tianma.tweaks.miui.cons.PrefConst.STATUS_BAR_CLOCK_ALIGNMENT;
import static com.tianma.tweaks.miui.cons.PrefConst.STATUS_BAR_CLOCK_COLOR;
import static com.tianma.tweaks.miui.cons.PrefConst.STATUS_BAR_CLOCK_COLOR_DEFAULT;
import static com.tianma.tweaks.miui.cons.PrefConst.STATUS_BAR_CLOCK_FORMAT;
import static com.tianma.tweaks.miui.cons.PrefConst.STATUS_BAR_CLOCK_FORMAT_DEFAULT;
import static com.tianma.tweaks.miui.cons.PrefConst.STATUS_BAR_CLOCK_FORMAT_ENABLE;
import static com.tianma.tweaks.miui.cons.PrefConst.STATUS_BAR_CLOCK_FORMAT_ENABLE_DEFAULT;

public class XSPUtils {

    private XSPUtils() {
    }

    /**
     * 是否打开总开关
     */
    public static boolean isMainSwitchEnabled(XSharedPreferences xsp) {
        return xsp.getBoolean(MAIN_SWITCH, MAIN_SWITCH_DEFAULT);
    }

    /**
     * 状态栏是否显示秒数
     */
    public static boolean showSecInStatusBar(XSharedPreferences xsp) {
        return xsp.getBoolean(SHOW_SEC_IN_STATUS_BAR, SHOW_SEC_IN_STATUS_BAR_DEFAULT);
    }

    /**
     * 获取状态栏时钟对齐方式
     */
    public static String getStatusBarClockAlignment(XSharedPreferences xsp) {
        return xsp.getString(STATUS_BAR_CLOCK_ALIGNMENT, ALIGNMENT_LEFT);
    }

    /**
     * 是否自定义状态栏时钟颜色
     */
    public static boolean isStatusBarClockColorEnabled(XSharedPreferences xsp) {
        return xsp.getBoolean(STATUS_BAR_CLOCK_COLOR_ENABLE,
                STATUS_BAR_CLOCK_COLOR_ENABLE_DEFAULT);
    }

    /**
     * 获取状态栏时钟颜色
     */
    public static int getStatusBarClockColor(XSharedPreferences xsp) {
        return xsp.getInt(STATUS_BAR_CLOCK_COLOR, STATUS_BAR_CLOCK_COLOR_DEFAULT);
    }

    /**
     * 是否自定义状态栏时间格式
     */
    public static boolean isStatusBarClockFormatEnabled(XSharedPreferences xsp) {
        return xsp.getBoolean(STATUS_BAR_CLOCK_FORMAT_ENABLE, STATUS_BAR_CLOCK_FORMAT_ENABLE_DEFAULT);
    }

    /**
     * 获取自定义的状态栏时间格式
     */
    public static String getStatusBarClockFormat(XSharedPreferences xsp) {
        return xsp.getString(STATUS_BAR_CLOCK_FORMAT, STATUS_BAR_CLOCK_FORMAT_DEFAULT);
    }

    /**
     * 下拉状态栏是否显示秒数
     */
    public static boolean showSecInDropdownStatusBar(XSharedPreferences xsp) {
        return xsp.getBoolean(SHOW_SEC_IN_DROPDOWN_STATUS_BAR, SHOW_SEC_IN_DROPDOWN_STATUS_BAR_DEFAULT);
    }

    /**
     * 下拉状态栏时钟颜色
     */
    public static int getDropdownStatusBarClockColor(XSharedPreferences xsp) {
        return xsp.getInt(DROPDOWN_STATUS_BAR_CLOCK_COLOR, DROPDOWN_STATUS_BAR_CLOCK_COLOR_DEFAULT);
    }

    /**
     * 下拉状态栏日期颜色
     */
    public static int getDropdownStatusBarDateColor(XSharedPreferences xsp) {
        return xsp.getInt(DROPDOWN_STATUS_BAR_DATE_COLOR, DROPDOWN_STATUS_BAR_DATE_COLOR_DEFAULT);
    }

    /**
     * 锁屏界面水平时钟是否显示秒数
     */
    public static boolean showSecInKeyguardHorizontal(XSharedPreferences xsp) {
        return xsp.getBoolean(SHOW_SEC_IN_KEYGUARD_HORIZONTAL, SHOW_SEC_IN_KEYGUARD_HORIZONTAL_DEFAULT);
    }

    /**
     * 锁屏界面垂直时钟是否显示秒数
     */
    public static boolean showSecInKeyguardVertical(XSharedPreferences xsp) {
        return xsp.getBoolean(SHOW_SEC_IN_KEYGUARD_VERTICAL, SHOW_SEC_IN_KEYGUARD_VERTICAL_DEFAULT);
    }

    /**
     * 获取锁屏时钟颜色
     */
    public static int getKeyguardClockColor(XSharedPreferences xsp) {
        return xsp.getInt(KEYGUARD_CLOCK_COLOR, KEYGUARD_CLOCK_COLOR_DEFAULT);
    }
}
