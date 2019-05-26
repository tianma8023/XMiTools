package com.tianma.tweaks.miui.utils;

import com.tianma.tweaks.miui.BuildConfig;
import com.tianma.tweaks.miui.cons.AppConst;

import java.io.File;

import de.robv.android.xposed.XSharedPreferences;

import static com.tianma.tweaks.miui.cons.PrefConst.ALIGNMENT_LEFT;
import static com.tianma.tweaks.miui.cons.PrefConst.ALWAYS_SHOW_STATUS_BAR_CLOCK;
import static com.tianma.tweaks.miui.cons.PrefConst.ALWAYS_SHOW_STATUS_BAR_CLOCK_DEFAULT;
import static com.tianma.tweaks.miui.cons.PrefConst.CUSTOM_MOBILE_NETWORK_TYPE;
import static com.tianma.tweaks.miui.cons.PrefConst.CUSTOM_MOBILE_NETWORK_TYPE_DEFAULT;
import static com.tianma.tweaks.miui.cons.PrefConst.CUSTOM_MOBILE_NETWORK_TYPE_ENABLE;
import static com.tianma.tweaks.miui.cons.PrefConst.CUSTOM_MOBILE_NETWORK_TYPE_ENABLE_DEFAULT;
import static com.tianma.tweaks.miui.cons.PrefConst.DROPDOWN_STATUS_BAR_CLOCK_COLOR;
import static com.tianma.tweaks.miui.cons.PrefConst.DROPDOWN_STATUS_BAR_CLOCK_COLOR_DEFAULT;
import static com.tianma.tweaks.miui.cons.PrefConst.DROPDOWN_STATUS_BAR_CLOCK_COLOR_ENABLE;
import static com.tianma.tweaks.miui.cons.PrefConst.DROPDOWN_STATUS_BAR_CLOCK_COLOR_ENABLE_DEFAULT;
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
import static com.tianma.tweaks.miui.cons.PrefConst.STATUS_BAR_DUAL_MOBILE_SIGNAL;
import static com.tianma.tweaks.miui.cons.PrefConst.STATUS_BAR_DUAL_MOBILE_SIGNAL_DEFAULT;
import static com.tianma.tweaks.miui.cons.PrefConst.STATUS_BAR_HIDE_VPN_ICON;
import static com.tianma.tweaks.miui.cons.PrefConst.STATUS_BAR_HIDE_VPN_ICON_DEFAULT;
import static com.tianma.tweaks.miui.cons.PrefConst.STATUS_BAR_SIGNAL_ALIGN_LEFT;
import static com.tianma.tweaks.miui.cons.PrefConst.STATUS_BAR_CLOCK_ALIGNMENT;
import static com.tianma.tweaks.miui.cons.PrefConst.STATUS_BAR_CLOCK_COLOR;
import static com.tianma.tweaks.miui.cons.PrefConst.STATUS_BAR_CLOCK_COLOR_DEFAULT;
import static com.tianma.tweaks.miui.cons.PrefConst.STATUS_BAR_CLOCK_COLOR_ENABLE;
import static com.tianma.tweaks.miui.cons.PrefConst.STATUS_BAR_CLOCK_COLOR_ENABLE_DEFAULT;
import static com.tianma.tweaks.miui.cons.PrefConst.STATUS_BAR_CLOCK_FORMAT;
import static com.tianma.tweaks.miui.cons.PrefConst.STATUS_BAR_CLOCK_FORMAT_DEFAULT;
import static com.tianma.tweaks.miui.cons.PrefConst.STATUS_BAR_CLOCK_FORMAT_ENABLE;
import static com.tianma.tweaks.miui.cons.PrefConst.STATUS_BAR_CLOCK_FORMAT_ENABLE_DEFAULT;
import static com.tianma.tweaks.miui.cons.PrefConst.STATUS_BAR_SIGNAL_ALIGN_LEFT_DEFAULT;

public class XSPUtils {

    private XSPUtils() {
    }

    /**
     * 获取XSharedPreferences
     */
    public static XSharedPreferences getXSharedPreferences() {
        File prefsFile = new File("/data/user_de/0/" + BuildConfig.APPLICATION_ID + "/shared_prefs/" + AppConst.X_MIUI_CLOCK_PREFS_NAME + ".xml");
        XSharedPreferences xsp;
        if (prefsFile.exists()) { // Android 7.0+
            xsp = new XSharedPreferences(prefsFile);
        } else { // below Android 7.0
            xsp = new XSharedPreferences(BuildConfig.APPLICATION_ID);
        }
        try {
            xsp.makeWorldReadable();
        } catch (Throwable t) {
            XLog.e("", t);
        }
        return xsp;
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
     * 是否自定义下拉状态栏时钟颜色
     */
    public static boolean isDropdownStatusBarClockColorEnabled(XSharedPreferences xsp) {
        return xsp.getBoolean(DROPDOWN_STATUS_BAR_CLOCK_COLOR_ENABLE, DROPDOWN_STATUS_BAR_CLOCK_COLOR_ENABLE_DEFAULT);
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

    /**
     * 是否在有系统时钟widget的桌面中显示状态栏时间
     */
    public static boolean alwaysShowStatusBarClock(XSharedPreferences xsp) {
        return xsp.getBoolean(ALWAYS_SHOW_STATUS_BAR_CLOCK, ALWAYS_SHOW_STATUS_BAR_CLOCK_DEFAULT);
    }

    /**
     * 信号是否左对齐
     */
    public static boolean isSignalAlignLeft(XSharedPreferences xsp) {
        return xsp.getBoolean(STATUS_BAR_SIGNAL_ALIGN_LEFT, STATUS_BAR_SIGNAL_ALIGN_LEFT_DEFAULT);
    }

    /**
     * 是否显示双层信号
     */
    public static boolean isDualMobileSignal(XSharedPreferences xsp) {
        return xsp.getBoolean(STATUS_BAR_DUAL_MOBILE_SIGNAL, STATUS_BAR_DUAL_MOBILE_SIGNAL_DEFAULT);
    }

    /**
     * 是否隐藏VPN图标
     */
    public static boolean isHideVpnIcon(XSharedPreferences xsp) {
        return xsp.getBoolean(STATUS_BAR_HIDE_VPN_ICON, STATUS_BAR_HIDE_VPN_ICON_DEFAULT);
    }

    /**
     * 状态栏是否自定义显示的移动网络类型
     */
    public static boolean customMobileNetworkEnabled(XSharedPreferences xsp) {
        return xsp.getBoolean(CUSTOM_MOBILE_NETWORK_TYPE_ENABLE, CUSTOM_MOBILE_NETWORK_TYPE_ENABLE_DEFAULT);
    }

    /**
     * 状态栏自定义显示的移动网络类型
     */
    public static String customMobileNetwork(XSharedPreferences xsp) {
        return xsp.getString(CUSTOM_MOBILE_NETWORK_TYPE, CUSTOM_MOBILE_NETWORK_TYPE_DEFAULT);
    }
}
