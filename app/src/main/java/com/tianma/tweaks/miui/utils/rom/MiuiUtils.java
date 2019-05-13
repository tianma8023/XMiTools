package com.tianma.tweaks.miui.utils.rom;

import android.os.Build;
import android.text.TextUtils;

import com.tianma.tweaks.miui.utils.XLog;

/**
 * MIUI Rom 相关工具类
 */
public class MiuiUtils {

    private static final String KEY_MIUI_VERSION_CODE = "ro.miui.ui.version.code";
    private static final String KEY_MIUI_VERSION_NAME = "ro.miui.ui.version.name";
    private static final String KEY_VERSION_CODE_TIME = "ro.miui.version.code_time";

    private MiuiUtils() {
    }

    /**
     * 判断当前Rom是否是MIUI
     */
    public static boolean isMiui() {
        return !TextUtils.isEmpty(getMiuiVersionName()) || getMiuiVersionCode() != -1;
    }

    /**
     * 获取MIUI版本名（v10, v9 之类）
     */
    public static String getMiuiVersionName() {
        return RomUtils.getSystemProperty(KEY_MIUI_VERSION_NAME);
    }

    /**
     * 获取 MIUI 大版本，MIUI v10 返回 10, MIUI v9 返回 9, ro.miui.ui.version.code 键不存在，否则返回 -1
     */
    public static int getMiuiVersionCode() {
        String versionCode = RomUtils.getSystemProperty(KEY_MIUI_VERSION_CODE);
        if (!TextUtils.isEmpty(versionCode)) {
            try {
                return Integer.parseInt(versionCode) + 2;
            } catch (Exception e) {
                XLog.e("get MIUI version code failed: %s", versionCode, e);
            }
        }
        return -1;
    }

    /**
     * 获取 MIUI 小版本，比如 9.5.9, 8.12.27 之类的，代表构建日期
     * @return
     */
    public static String getMiuiVersionIncremental() {
        return Build.VERSION.INCREMENTAL;
    }

    /**
     * 获取当前MIUI版本构建时间（单位ms）
     * @return
     */
    private static long getMiuiVersionCodeTime() {
        String versionCodeTime = RomUtils.getSystemProperty(KEY_VERSION_CODE_TIME);
        if (!TextUtils.isEmpty(versionCodeTime)) {
            try {
                return Long.parseLong(versionCodeTime) * 1000;
            } catch (Exception e) {
                XLog.e("get MIUI version code time failed: %s", versionCodeTime, e);
            }
        }
        return 0L;
    }

    public static MiuiVersion getMiuiVersion() {
        return new MiuiVersion(getMiuiVersionCodeTime());
    }

}
