package com.tianma.tweaks.miui.cons;

import android.graphics.Color;

public interface PrefConst {

    String MODULE_STATUS = "module_status";
    String MAIN_SWITCH = "main_switch";
    boolean MAIN_SWITCH_DEFAULT = true;

    String SHOW_SEC_IN_STATUS_BAR= "show_sec_in_status_bar";
    boolean SHOW_SEC_IN_STATUS_BAR_DEFAULT = false;
    String STATUS_BAR_CLOCK_ALIGNMENT = "status_bar_clock_alignment";
    String ALIGNMENT_LEFT = "left";
    String ALIGNMENT_CENTER = "center";
    String ALIGNMENT_RIGHT = "right";
    String STATUS_BAR_CLOCK_COLOR_ENABLE = "status_bar_clock_color_enable";
    boolean STATUS_BAR_CLOCK_COLOR_ENABLE_DEFAULT = false;
    String STATUS_BAR_CLOCK_COLOR = "status_bar_clock_color";
    int STATUS_BAR_CLOCK_COLOR_DEFAULT = Color.WHITE;
    String STATUS_BAR_CLOCK_FORMAT_ENABLE = "status_bar_clock_format_enable";
    boolean STATUS_BAR_CLOCK_FORMAT_ENABLE_DEFAULT = false;
    String STATUS_BAR_CLOCK_FORMAT = "status_bar_clock_format";
    String STATUS_BAR_CLOCK_FORMAT_DEFAULT = "HH:mm:ss";

    String SHOW_SEC_IN_DROPDOWN_STATUS_BAR = "show_sec_in_dropdown_status_bar";
    boolean SHOW_SEC_IN_DROPDOWN_STATUS_BAR_DEFAULT = false;
    String DROPDOWN_STATUS_BAR_CLOCK_COLOR_ENABLE = "dropdown_status_bar_clock_color_enable";
    boolean DROPDOWN_STATUS_BAR_CLOCK_COLOR_ENABLE_DEFAULT = false;
    String DROPDOWN_STATUS_BAR_CLOCK_COLOR = "dropdown_status_bar_clock_color";
    int DROPDOWN_STATUS_BAR_CLOCK_COLOR_DEFAULT = Color.WHITE;
    String DROPDOWN_STATUS_BAR_DATE_COLOR = "dropdown_status_bar_date_color";
    int DROPDOWN_STATUS_BAR_DATE_COLOR_DEFAULT = Color.WHITE;

    String SHOW_SEC_IN_KEYGUARD_HORIZONTAL = "show_sec_in_keyguard_horizontal";
    boolean SHOW_SEC_IN_KEYGUARD_HORIZONTAL_DEFAULT = false;
    String SHOW_SEC_IN_KEYGUARD_VERTICAL = "show_sec_in_keyguard_horizontal";
    boolean SHOW_SEC_IN_KEYGUARD_VERTICAL_DEFAULT = false;
    String KEYGUARD_CLOCK_COLOR = "keyguard_clock_color";
    int KEYGUARD_CLOCK_COLOR_DEFAULT = Color.WHITE;

    String HIDE_LAUNCHER_ICON = "hide_launcher_icon";

    String APP_VERSION = "app_version";
    String SOURCE_CODE = "source_code";
    String DONATE_BY_ALIPAY = "donate_by_alipay";
}
