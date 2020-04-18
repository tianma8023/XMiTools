package com.tianma.tweaks.miui.cons;

import com.tianma.tweaks.miui.BuildConfig;

public interface PrefConst {

    String SHARED_PREFS_NAME = BuildConfig.APPLICATION_ID + "_preferences";

    String MODULE_STATUS = "module_status";
    String MAIN_SWITCH = "main_switch";
    String HIDE_LAUNCHER_ICON = "hide_launcher_icon";

    String SHOW_SEC_IN_STATUS_BAR = "show_sec_in_status_bar";
    String STATUS_BAR_CLOCK_ALIGNMENT = "status_bar_clock_alignment";
    String ALIGNMENT_LEFT = "left";
    String ALIGNMENT_CENTER = "center";
    String ALIGNMENT_RIGHT = "right";
    String STATUS_BAR_CLOCK_COLOR_ENABLE = "status_bar_clock_color_enable";
    String STATUS_BAR_CLOCK_COLOR = "status_bar_clock_color";
    String STATUS_BAR_CLOCK_FORMAT_ENABLE = "status_bar_clock_format_enable";
    String STATUS_BAR_CLOCK_FORMAT = "status_bar_clock_format";
    String STATUS_BAR_CLOCK_FORMAT_DEFAULT = "HH:mm:ss";
    String STATUS_BAR_SIGNAL_ALIGN_LEFT = "status_bar_signal_align_left";
    String STATUS_BAR_DUAL_MOBILE_SIGNAL = "status_bar_dual_mobile_signal";
    String STATUS_BAR_HIDE_VPN_ICON = "status_bar_hide_vpn_icon";
    String STATUS_BAR_HIDE_HD_ICON = "status_bar_hide_hd_icon";
    String STATUS_BAR_SHOW_SMALL_BATTERY_PERCENT_SIGN = "status_bar_show_small_battery_percent_sign";
    String CUSTOM_MOBILE_NETWORK_TYPE_ENABLE = "custom_mobile_network_type_enable";
    String CUSTOM_MOBILE_NETWORK_TYPE = "custom_mobile_network_type";
    String CUSTOM_MOBILE_NETWORK_TYPE_DEFAULT = "5G";
    String ALWAYS_SHOW_STATUS_BAR_CLOCK = "always_show_status_bar_clock";

    String SHOW_SEC_IN_DROPDOWN_STATUS_BAR = "show_sec_in_dropdown_status_bar";
    String DROPDOWN_STATUS_BAR_CLOCK_COLOR_ENABLE = "dropdown_status_bar_clock_color_enable";
    String DROPDOWN_STATUS_BAR_CLOCK_COLOR = "dropdown_status_bar_clock_color";
    String DROPDOWN_STATUS_BAR_DATE_COLOR = "dropdown_status_bar_date_color";
    String DROPDOWN_STATUS_BAR_WEATHER_ENABLE = "dropdown_status_bar_weather_enable";
    String DROPDOWN_STATUS_BAR_WEATHER_TEXT_COLOR = "dropdown_status_bar_weather_text_color";
    String DROPDOWN_STATUS_BAR_WEATHER_TEXT_SIZE = "dropdown_status_bar_weather_text_size";
    String DROPDOWN_STATUS_BAR_WEATHER_TEXT_SIZE_DEFAULT = "14";

    String SHOW_SEC_IN_KEYGUARD_HORIZONTAL = "show_sec_in_keyguard_horizontal";
    String SHOW_SEC_IN_KEYGUARD_VERTICAL = "show_sec_in_keyguard_vertical";
    String KEYGUARD_CLOCK_COLOR = "keyguard_clock_color";

    String APP_VERSION = "app_version";
    String SOURCE_CODE = "source_code";
    String KEY_JOIN_QQ_GROUP = "join_qq_group";
    String DONATE_BY_ALIPAY = "donate_by_alipay";

    // 一言
    String ONE_SENTENCE_ENABLE = "one_sentence_enable";
    String ONE_SENTENCE_SETTINGS = "one_sentence_settings";
    String ONE_SENTENCE_API_SOURCES = "one_sentence_api_sources";
    String API_SOURCE_HITOKOTO = "source_hitokoto";
    String API_SOURCE_ONE_POEM = "source_one_poem";

    String HITOKOTO_CATEGORIES = "hitokoto_categories";
    String HITOKOTO_CATEGORY_ALL = "all";
    String SHOW_HITOKOTO_SOURCE = "show_hitokoto_source";

    String ONE_POEM_CATEGORIES = "one_poem_categories";
    String ONE_POEM_CATEGORY_ALL = "all";
    String SHOW_POEM_AUTHOR = "show_poem_author";

    String ONE_SENTENCE_REFRESH_RATE = "one_sentence_refresh_rate";
    String ONE_SENTENCE_REFRESH_RATE_DEFAULT = "30";

}
