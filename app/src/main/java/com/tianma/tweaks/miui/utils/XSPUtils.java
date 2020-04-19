package com.tianma.tweaks.miui.utils;

import android.graphics.Color;

import com.tianma.tweaks.miui.BuildConfig;
import com.tianma.tweaks.miui.cons.AppConst;
import com.tianma.tweaks.miui.cons.PrefConst;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import de.robv.android.xposed.XSharedPreferences;

import static com.tianma.tweaks.miui.cons.PrefConst.ALIGNMENT_LEFT;
import static com.tianma.tweaks.miui.cons.PrefConst.ALWAYS_SHOW_STATUS_BAR_CLOCK;
import static com.tianma.tweaks.miui.cons.PrefConst.CUSTOM_MOBILE_NETWORK_TYPE;
import static com.tianma.tweaks.miui.cons.PrefConst.CUSTOM_MOBILE_NETWORK_TYPE_DEFAULT;
import static com.tianma.tweaks.miui.cons.PrefConst.CUSTOM_MOBILE_NETWORK_TYPE_ENABLE;
import static com.tianma.tweaks.miui.cons.PrefConst.DROPDOWN_STATUS_BAR_CLOCK_COLOR;
import static com.tianma.tweaks.miui.cons.PrefConst.DROPDOWN_STATUS_BAR_CLOCK_COLOR_ENABLE;
import static com.tianma.tweaks.miui.cons.PrefConst.DROPDOWN_STATUS_BAR_DATE_COLOR;
import static com.tianma.tweaks.miui.cons.PrefConst.DROPDOWN_STATUS_BAR_WEATHER_ENABLE;
import static com.tianma.tweaks.miui.cons.PrefConst.DROPDOWN_STATUS_BAR_WEATHER_TEXT_COLOR;
import static com.tianma.tweaks.miui.cons.PrefConst.DROPDOWN_STATUS_BAR_WEATHER_TEXT_SIZE;
import static com.tianma.tweaks.miui.cons.PrefConst.DROPDOWN_STATUS_BAR_WEATHER_TEXT_SIZE_DEFAULT;
import static com.tianma.tweaks.miui.cons.PrefConst.KEYGUARD_CLOCK_COLOR;
import static com.tianma.tweaks.miui.cons.PrefConst.MAIN_SWITCH;
import static com.tianma.tweaks.miui.cons.PrefConst.SHOW_SEC_IN_DROPDOWN_STATUS_BAR;
import static com.tianma.tweaks.miui.cons.PrefConst.SHOW_SEC_IN_KEYGUARD_HORIZONTAL;
import static com.tianma.tweaks.miui.cons.PrefConst.SHOW_SEC_IN_KEYGUARD_VERTICAL;
import static com.tianma.tweaks.miui.cons.PrefConst.SHOW_SEC_IN_STATUS_BAR;
import static com.tianma.tweaks.miui.cons.PrefConst.STATUS_BAR_CLOCK_ALIGNMENT;
import static com.tianma.tweaks.miui.cons.PrefConst.STATUS_BAR_CLOCK_COLOR;
import static com.tianma.tweaks.miui.cons.PrefConst.STATUS_BAR_CLOCK_COLOR_ENABLE;
import static com.tianma.tweaks.miui.cons.PrefConst.STATUS_BAR_CLOCK_FORMAT;
import static com.tianma.tweaks.miui.cons.PrefConst.STATUS_BAR_CLOCK_FORMAT_DEFAULT;
import static com.tianma.tweaks.miui.cons.PrefConst.STATUS_BAR_CLOCK_FORMAT_ENABLE;
import static com.tianma.tweaks.miui.cons.PrefConst.STATUS_BAR_DUAL_MOBILE_SIGNAL;
import static com.tianma.tweaks.miui.cons.PrefConst.STATUS_BAR_HIDE_HD_ICON;
import static com.tianma.tweaks.miui.cons.PrefConst.STATUS_BAR_HIDE_VPN_ICON;
import static com.tianma.tweaks.miui.cons.PrefConst.STATUS_BAR_SHOW_SMALL_BATTERY_PERCENT_SIGN;
import static com.tianma.tweaks.miui.cons.PrefConst.STATUS_BAR_SIGNAL_ALIGN_LEFT;

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
        return xsp.getBoolean(MAIN_SWITCH, true);
    }

    /**
     * 状态栏是否显示秒数
     */
    public static boolean showSecInStatusBar(XSharedPreferences xsp) {
        return xsp.getBoolean(SHOW_SEC_IN_STATUS_BAR, false);
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
        return xsp.getBoolean(STATUS_BAR_CLOCK_COLOR_ENABLE, false);
    }

    /**
     * 获取状态栏时钟颜色
     */
    public static int getStatusBarClockColor(XSharedPreferences xsp) {
        return xsp.getInt(STATUS_BAR_CLOCK_COLOR, Color.WHITE);
    }

    /**
     * 是否自定义状态栏时间格式
     */
    public static boolean isStatusBarClockFormatEnabled(XSharedPreferences xsp) {
        return xsp.getBoolean(STATUS_BAR_CLOCK_FORMAT_ENABLE, false);
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
        return xsp.getBoolean(SHOW_SEC_IN_DROPDOWN_STATUS_BAR, false);
    }

    /**
     * 是否自定义下拉状态栏时钟颜色
     */
    public static boolean isDropdownStatusBarClockColorEnabled(XSharedPreferences xsp) {
        return xsp.getBoolean(DROPDOWN_STATUS_BAR_CLOCK_COLOR_ENABLE, false);
    }

    /**
     * 下拉状态栏时钟颜色
     */
    public static int getDropdownStatusBarClockColor(XSharedPreferences xsp) {
        return xsp.getInt(DROPDOWN_STATUS_BAR_CLOCK_COLOR, Color.WHITE);
    }

    /**
     * 下拉状态栏日期颜色
     */
    public static int getDropdownStatusBarDateColor(XSharedPreferences xsp) {
        return xsp.getInt(DROPDOWN_STATUS_BAR_DATE_COLOR, Color.WHITE);
    }

    /**
     * 锁屏界面水平时钟是否显示秒数
     */
    public static boolean showSecInKeyguardHorizontal(XSharedPreferences xsp) {
        return xsp.getBoolean(SHOW_SEC_IN_KEYGUARD_HORIZONTAL, false);
    }

    /**
     * 锁屏界面垂直时钟是否显示秒数
     */
    public static boolean showSecInKeyguardVertical(XSharedPreferences xsp) {
        return xsp.getBoolean(SHOW_SEC_IN_KEYGUARD_VERTICAL, false);
    }

    /**
     * 获取锁屏时钟颜色
     */
    public static int getKeyguardClockColor(XSharedPreferences xsp) {
        return xsp.getInt(KEYGUARD_CLOCK_COLOR, Color.WHITE);
    }

    /**
     * 是否在有系统时钟widget的桌面中显示状态栏时间
     */
    public static boolean alwaysShowStatusBarClock(XSharedPreferences xsp) {
        return xsp.getBoolean(ALWAYS_SHOW_STATUS_BAR_CLOCK, false);
    }

    /**
     * 信号是否左对齐
     */
    public static boolean isSignalAlignLeft(XSharedPreferences xsp) {
        return xsp.getBoolean(STATUS_BAR_SIGNAL_ALIGN_LEFT, false);
    }

    /**
     * 是否显示双层信号
     */
    public static boolean isDualMobileSignal(XSharedPreferences xsp) {
        return xsp.getBoolean(STATUS_BAR_DUAL_MOBILE_SIGNAL, false);
    }

    /**
     * 是否隐藏VPN图标
     */
    public static boolean isHideVpnIcon(XSharedPreferences xsp) {
        return xsp.getBoolean(STATUS_BAR_HIDE_VPN_ICON, false);
    }

    /**
     * 是否隐藏 HD 图标
     */
    public static boolean isHideHDIcon(XSharedPreferences xsp) {
        return xsp.getBoolean(STATUS_BAR_HIDE_HD_ICON, false);
    }

    /**
     * 电量是否显示小的百分号
     */
    public static boolean showSmallBatteryPercentSign(XSharedPreferences xsp) {
        return xsp.getBoolean(STATUS_BAR_SHOW_SMALL_BATTERY_PERCENT_SIGN, false);
    }

    /**
     * 状态栏是否自定义显示的移动网络类型
     */
    public static boolean isCustomMobileNetworkEnabled(XSharedPreferences xsp) {
        return xsp.getBoolean(CUSTOM_MOBILE_NETWORK_TYPE_ENABLE, false);
    }

    /**
     * 状态栏自定义显示的移动网络类型
     */
    public static String customMobileNetwork(XSharedPreferences xsp) {
        return xsp.getString(CUSTOM_MOBILE_NETWORK_TYPE, CUSTOM_MOBILE_NETWORK_TYPE_DEFAULT);
    }

    /**
     * 下拉状态栏是否显示天气信息
     */
    public static boolean isDropdownStatusBarWeatherEnabled(XSharedPreferences xsp) {
        return xsp.getBoolean(DROPDOWN_STATUS_BAR_WEATHER_ENABLE, false);
    }

    /**
     * 下拉状态栏天气字体颜色
     */
    public static int getDropdownStatusBarWeatherTextColor(XSharedPreferences xsp) {
        return xsp.getInt(DROPDOWN_STATUS_BAR_WEATHER_TEXT_COLOR, Color.WHITE);
    }

    /**
     * 下拉状态栏天气字体大小
     */
    public static float getDropdownStatusBarWeatherTextSize(XSharedPreferences xsp) {
        String text = xsp.getString(DROPDOWN_STATUS_BAR_WEATHER_TEXT_SIZE, DROPDOWN_STATUS_BAR_WEATHER_TEXT_SIZE_DEFAULT);
        float size;
        try {
            size = Float.parseFloat(text);
        } catch (Throwable t) {
            size = Float.parseFloat(DROPDOWN_STATUS_BAR_WEATHER_TEXT_SIZE_DEFAULT);
        }
        return size;
    }

    /**
     * 是否启用一言
     */
    public static boolean oneSentenceEnabled(XSharedPreferences xsp) {
        return xsp.getBoolean(PrefConst.ONE_SENTENCE_ENABLE, false);
    }

    /**
     * 获取一言API源
     */
    public static Set<String> getOneSentenceApiSources(XSharedPreferences xsp) {
        return xsp.getStringSet(PrefConst.ONE_SENTENCE_API_SOURCES, new HashSet<>());
    }

    /**
     * 获取Hitokoto种类
     */
    public static Set<String> getHitokotoCategories(XSharedPreferences xsp) {
        return xsp.getStringSet(PrefConst.HITOKOTO_CATEGORIES, new HashSet<>());
    }

    /**
     * 获取今日诗词种类
     */
    public static Set<String> getOnePoemCategories(XSharedPreferences xsp) {
        return xsp.getStringSet(PrefConst.ONE_POEM_CATEGORIES, new HashSet<>());
    }

    /**
     * 是否显示一言 Hitokoto 来源
     */
    public static boolean getShowHitokotoSource(XSharedPreferences xsp) {
        return xsp.getBoolean(PrefConst.SHOW_HITOKOTO_SOURCE, false);
    }

    /**
     * 是否显示今日诗词作者
     */
    public static boolean getShowPoemAuthor(XSharedPreferences xsp) {
        return xsp.getBoolean(PrefConst.SHOW_POEM_AUTHOR, false);
    }

    /**
     * 获取一言刷新时间间隔，单位为min
     */
    public static long getOneSentenceRefreshRate(XSharedPreferences xsp) {
        String text = xsp.getString(PrefConst.ONE_SENTENCE_REFRESH_RATE, PrefConst.ONE_SENTENCE_REFRESH_RATE_DEFAULT);
        long refreshRate;
        try {
            refreshRate = Long.parseLong(text);
        } catch (Throwable t) {
            refreshRate = Long.parseLong(PrefConst.ONE_SENTENCE_REFRESH_RATE_DEFAULT);
        }
        return refreshRate;
    }

    /**
     * 下拉状态栏日期颜色
     */
    public static int getOneSentenceColor(XSharedPreferences xsp) {
        return xsp.getInt(PrefConst.ONE_SENTENCE_COLOR, Color.WHITE);
    }

}
