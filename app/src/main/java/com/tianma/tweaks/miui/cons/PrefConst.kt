package com.tianma.tweaks.miui.cons

import com.tianma.tweaks.miui.BuildConfig

object PrefConst {
    const val SHARED_PREFS_NAME = BuildConfig.APPLICATION_ID + "_preferences"

    // General Settings
    const val MODULE_STATUS = "module_status"
    const val MAIN_SWITCH = "main_switch"
    const val HIDE_LAUNCHER_ICON = "hide_launcher_icon"
    // General Settings End

    // StatusBar Settings
    const val SHOW_SEC_IN_STATUS_BAR = "show_sec_in_status_bar"
    const val STATUS_BAR_CLOCK_ALIGNMENT = "status_bar_clock_alignment"
    const val ALIGNMENT_LEFT = "left"
    const val ALIGNMENT_CENTER = "center"
    const val ALIGNMENT_RIGHT = "right"
    const val STATUS_BAR_CLOCK_COLOR_ENABLE = "status_bar_clock_color_enable"
    const val STATUS_BAR_CLOCK_COLOR = "status_bar_clock_color"
    const val STATUS_BAR_CLOCK_FORMAT_ENABLE = "status_bar_clock_format_enable"
    const val STATUS_BAR_CLOCK_FORMAT = "status_bar_clock_format"
    const val STATUS_BAR_CLOCK_FORMAT_DEFAULT = "HH:mm:ss"
    const val STATUS_BAR_SIGNAL_ALIGN_LEFT = "status_bar_signal_align_left"
    const val STATUS_BAR_DUAL_MOBILE_SIGNAL = "status_bar_dual_mobile_signal"
    const val STATUS_BAR_HIDE_VPN_ICON = "status_bar_hide_vpn_icon"
    const val STATUS_BAR_HIDE_HD_ICON = "status_bar_hide_hd_icon"
    const val STATUS_BAR_SHOW_SMALL_BATTERY_PERCENT_SIGN = "status_bar_show_small_battery_percent_sign"
    const val CUSTOM_MOBILE_NETWORK_TYPE_ENABLE = "custom_mobile_network_type_enable"
    const val CUSTOM_MOBILE_NETWORK_TYPE = "custom_mobile_network_type"
    const val CUSTOM_MOBILE_NETWORK_TYPE_DEFAULT = "5G"
    const val ALWAYS_SHOW_STATUS_BAR_CLOCK = "always_show_status_bar_clock"
    // StatusBar Settings End

    // Dropdown StatusBar Settings
    const val SHOW_SEC_IN_DROPDOWN_STATUS_BAR = "show_sec_in_dropdown_status_bar"
    const val DROPDOWN_STATUS_BAR_CLOCK_COLOR_ENABLE = "dropdown_status_bar_clock_color_enable"
    const val DROPDOWN_STATUS_BAR_CLOCK_COLOR = "dropdown_status_bar_clock_color"
    const val DROPDOWN_STATUS_BAR_DATE_COLOR = "dropdown_status_bar_date_color"
    const val DROPDOWN_STATUS_BAR_WEATHER_ENABLE = "dropdown_status_bar_weather_enable"
    const val DROPDOWN_STATUS_BAR_WEATHER_TEXT_COLOR = "dropdown_status_bar_weather_text_color"
    const val DROPDOWN_STATUS_BAR_WEATHER_TEXT_SIZE = "dropdown_status_bar_weather_text_size"
    const val DROPDOWN_STATUS_BAR_WEATHER_TEXT_SIZE_DEFAULT = "14"
    // Dropdown StatusBar Settings End

    // Keyguard Settings
    const val SHOW_SEC_IN_KEYGUARD_HORIZONTAL = "show_sec_in_keyguard_horizontal"
    const val SHOW_SEC_IN_KEYGUARD_VERTICAL = "show_sec_in_keyguard_vertical"
    const val KEYGUARD_CLOCK_COLOR = "keyguard_clock_color"
    // Keyguard Settings end

    // About Settings
    const val APP_VERSION = "app_version"
    const val SOURCE_CODE = "source_code"
    const val KEY_JOIN_QQ_GROUP = "join_qq_group"
    const val DONATE_BY_ALIPAY = "donate_by_alipay"
    // About Settings End

    // 一言
    const val ONE_SENTENCE_ENABLE = "one_sentence_enable"
    const val ONE_SENTENCE_SETTINGS = "one_sentence_settings"
    const val ONE_SENTENCE_API_SOURCES = "one_sentence_api_sources"
    const val API_SOURCE_HITOKOTO = "source_hitokoto"
    const val API_SOURCE_ONE_POEM = "source_one_poem"
    const val HITOKOTO_CATEGORIES = "hitokoto_categories"
    const val HITOKOTO_CATEGORY_ALL = "all"
    const val SHOW_HITOKOTO_SOURCE = "show_hitokoto_source"
    const val ONE_POEM_CATEGORIES = "one_poem_categories"
    const val ONE_POEM_CATEGORY_ALL = "all"
    const val SHOW_POEM_AUTHOR = "show_poem_author"
    const val ONE_SENTENCE_REFRESH_RATE = "one_sentence_refresh_rate"
    const val ONE_SENTENCE_REFRESH_RATE_DEFAULT = "30"
    // 一言 End
}