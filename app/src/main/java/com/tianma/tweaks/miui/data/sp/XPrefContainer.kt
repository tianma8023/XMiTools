package com.tianma.tweaks.miui.data.sp

import android.graphics.Color
import com.tianma.tweaks.miui.BuildConfig
import com.tianma.tweaks.miui.cons.AppConst
import com.tianma.tweaks.miui.cons.PrefConst
import com.tianma.tweaks.miui.utils.prefs.XPreferenceDelegate

/**
 * desc: XSharedPreference Container. Used in Host App
 * date: 2021/10/12
 */
object XPrefContainer {

    /**
     * 是否打开总开关
     */
    @JvmStatic
    val mainSwitchEnable by xPref(
        PrefConst.MAIN_SWITCH,
        false
    )

    /**
     * 状态栏是否显示秒数
     */
    @JvmStatic
    val showSecInStatusBar by xPref(
        PrefConst.SHOW_SEC_IN_STATUS_BAR,
        false
    )

    /**
     * 状态栏时钟对齐方式
     */
    @JvmStatic
    val statusBarClockAlignment by xPref(
        PrefConst.STATUS_BAR_CLOCK_ALIGNMENT,
        PrefConst.ALIGNMENT_LEFT
    )

    /**
     * 是否自定义状态栏时钟颜色
     */
    @JvmStatic
    val statusBarClockColorEnabled by xPref(
        PrefConst.STATUS_BAR_CLOCK_COLOR_ENABLE,
        false
    )

    /**
     * 状态栏时钟颜色
     */
    @JvmStatic
    val statusBarClockColor by xPref(
        PrefConst.STATUS_BAR_CLOCK_COLOR,
        Color.WHITE
    )

    /**
     * 是否自定义状态栏时间格式
     */
    @JvmStatic
    val statusBarClockFormatEnabled by xPref(
        PrefConst.STATUS_BAR_CLOCK_FORMAT_ENABLE,
        false
    )

    /**
     * 自定义的状态栏时间格式
     */
    @JvmStatic
    val statusBarClockFormat by xPref(
        PrefConst.STATUS_BAR_CLOCK_FORMAT,
        PrefConst.STATUS_BAR_CLOCK_FORMAT_DEFAULT
    )

    /**
     * 下拉状态栏是否显示秒数
     */
    @JvmStatic
    val showSecInDropdownStatusBar by xPref(
        PrefConst.SHOW_SEC_IN_DROPDOWN_STATUS_BAR,
        false
    )

    /**
     * 是否自定义下拉状态栏时钟颜色
     */
    @JvmStatic
    val dropdownStatusBarClockColorEnabled by xPref(
        PrefConst.DROPDOWN_STATUS_BAR_CLOCK_COLOR_ENABLE,
        false
    )

    /**
     * 下拉状态栏时钟颜色
     */
    @JvmStatic
    val dropdownStatusBarClockColor by xPref(
        PrefConst.DROPDOWN_STATUS_BAR_CLOCK_COLOR,
        Color.WHITE
    )

    /**
     * 下拉状态栏日期颜色
     */
    @JvmStatic
    val dropdownStatusBarDateColor by xPref(
        PrefConst.DROPDOWN_STATUS_BAR_DATE_COLOR,
        Color.WHITE
    )

    /**
     * 锁屏界面水平时钟是否显示秒数
     */
    @JvmStatic
    val showSecInKeyguardHorizontal by xPref(
        PrefConst.SHOW_SEC_IN_KEYGUARD_HORIZONTAL,
        false
    )

    /**
     * 锁屏界面垂直时钟是否显示秒数
     */
    @JvmStatic
    val showSecInKeyguardVertical by xPref(
        PrefConst.SHOW_SEC_IN_KEYGUARD_VERTICAL,
        false
    )

    /**
     * 获取锁屏时钟颜色
     */
    @JvmStatic
    val keyguardClockColor by xPref(
        PrefConst.KEYGUARD_CLOCK_COLOR,
        Color.WHITE
    )

    /**
     * 是否在有系统时钟widget的桌面中显示状态栏时间
     */
    @JvmStatic
    val alwaysShowStatusBarClock by xPref(
        PrefConst.ALWAYS_SHOW_STATUS_BAR_CLOCK,
        false
    )

    /**
     * 信号是否左对齐
     */
    @JvmStatic
    val isSignalAlignLeft by xPref(
        PrefConst.STATUS_BAR_SIGNAL_ALIGN_LEFT,
        false
    )

    /**
     * 是否显示双层信号
     */
    @JvmStatic
    val isDualMobileSignal by xPref(
        PrefConst.STATUS_BAR_DUAL_MOBILE_SIGNAL,
        false
    )

    /**
     * 是否隐藏VPN图标
     */
    @JvmStatic
    val isHideVpnIcon by xPref(
        PrefConst.STATUS_BAR_HIDE_VPN_ICON,
        false
    )

    /**
     * 是否隐藏 HD 图标
     */
    @JvmStatic
    val isHideHDIcon by xPref(
        PrefConst.STATUS_BAR_HIDE_HD_ICON,
        false
    )

    /**
     * 电量是否显示小的百分号
     */
    @JvmStatic
    val showSmallBatteryPercentSign by xPref(
        PrefConst.STATUS_BAR_SHOW_SMALL_BATTERY_PERCENT_SIGN,
        false
    )

    /**
     * 状态栏是否自定义显示的移动网络类型
     */
    @JvmStatic
    val isCustomMobileNetworkEnabled by xPref(
        PrefConst.CUSTOM_MOBILE_NETWORK_TYPE_ENABLE,
        false
    )

    /**
     * 状态栏自定义显示的移动网络类型
     */
    @JvmStatic
    val customMobileNetwork by xPref(
        PrefConst.CUSTOM_MOBILE_NETWORK_TYPE,
        PrefConst.CUSTOM_MOBILE_NETWORK_TYPE_DEFAULT
    )

    /**
     * 下拉状态栏是否显示天气信息
     */
    @JvmStatic
    val isDropdownStatusBarWeatherEnabled by xPref(
        PrefConst.DROPDOWN_STATUS_BAR_WEATHER_ENABLE,
        false
    )

    /**
     * 下拉状态栏天气字体颜色
     */
    @JvmStatic
    val dropdownStatusBarWeatherTextColor by xPref(
        PrefConst.DROPDOWN_STATUS_BAR_WEATHER_TEXT_COLOR,
        Color.WHITE
    )

//    /**
//     * 下拉状态栏天气字体大小
//     */
//    private val dropdownStatusBarWeatherTextSizeStr by xPref(
//        PrefConst.DROPDOWN_STATUS_BAR_WEATHER_TEXT_SIZE,
//        PrefConst.DROPDOWN_STATUS_BAR_WEATHER_TEXT_SIZE_DEFAULT
//    )

    /**
     * 下拉状态栏天气字体大小
     */
    @JvmStatic
    fun getDropdownStatusBarWeatherTextSize(): Float {
        val dropdownStatusBarWeatherTextSizeStr by xPref(
            PrefConst.DROPDOWN_STATUS_BAR_WEATHER_TEXT_SIZE,
            PrefConst.DROPDOWN_STATUS_BAR_WEATHER_TEXT_SIZE_DEFAULT
        )
        val size: Float = try {
            dropdownStatusBarWeatherTextSizeStr.toFloat()
        } catch (t: Throwable) {
            PrefConst.DROPDOWN_STATUS_BAR_WEATHER_TEXT_SIZE_DEFAULT.toFloat()
        }
        return size
    }

    /**
     * 是否启用一言
     */
    @JvmStatic
    val oneSentenceEnabled by xPref(
        PrefConst.ONE_SENTENCE_ENABLE,
        false
    )

    /**
     * 一言API源
     */
    @JvmStatic
    val oneSentenceApiSources by xPref(
        PrefConst.ONE_SENTENCE_API_SOURCES,
        setOf<String>()
    )

    /**
     * Hitokoto种类
     */
    @JvmStatic
    val hitokotoCategories by xPref(
        PrefConst.HITOKOTO_CATEGORIES,
        setOf<String>()
    )

    /**
     * 今日诗词种类
     */
    @JvmStatic
    val onePoemCategories by xPref(
        PrefConst.ONE_POEM_CATEGORIES,
        setOf<String>()
    )

    /**
     * 是否显示一言 Hitokoto 来源
     */
    @JvmStatic
    val showHitokotoSource by xPref(
        PrefConst.SHOW_HITOKOTO_SOURCE,
        false
    )

    /**
     * 是否显示今日诗词作者
     */
    @JvmStatic
    val showPoemAuthor by xPref(
        PrefConst.SHOW_POEM_AUTHOR,
        false
    )

    /**
     * 一言刷新时间间隔，单位为min
     */
    @JvmStatic
    fun getOneSentenceRefreshRate(): Long {
        val oneSentenceRefreshRateStr by xPref(
            PrefConst.ONE_SENTENCE_REFRESH_RATE,
            PrefConst.ONE_SENTENCE_REFRESH_RATE_DEFAULT
        )
        val refreshRate: Long = try {
            oneSentenceRefreshRateStr.toLong()
        } catch (t: Throwable) {
            PrefConst.ONE_SENTENCE_REFRESH_RATE_DEFAULT.toLong()
        }
        return refreshRate
    }

    /**
     * 锁屏一言文字颜色
     */
    @JvmStatic
    val oneSentenceColor by xPref(PrefConst.ONE_SENTENCE_COLOR, Color.WHITE)

    /**
     * 锁屏一言文字大小
     */
    @JvmStatic
    fun getOneSentenceTextSize(): Float {
        val textSizeStr: String by xPref(
            PrefConst.ONE_SENTENCE_TEXT_SIZE,
            PrefConst.ONE_SENTENCE_TEXT_SIZE_DEFAULT
        )
        return try {
            textSizeStr.toFloat()
        } catch (t: Throwable) {
            PrefConst.ONE_SENTENCE_TEXT_SIZE_DEFAULT.toFloat()
        }
    }

    private fun <T> xPref(key: String, defaultValue: T): XPreferenceDelegate<T> {
        return XPreferenceDelegate(
            key,
            defaultValue,
            BuildConfig.APPLICATION_ID,
            AppConst.XMI_TOOLS_PREFS_NAME
        )
    }

}