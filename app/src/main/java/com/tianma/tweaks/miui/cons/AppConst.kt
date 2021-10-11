package com.tianma.tweaks.miui.cons

import com.tianma.tweaks.miui.BuildConfig

object AppConst {
    // MiTweaks begin
    const val MAIN_ACTIVITY_ALIAS = BuildConfig.APPLICATION_ID + ".app.MainActivityAlias"
    const val PROJECT_SOURCE_CODE_URL = "https://github.com/tianma8023/XMiTools"
    const val XMI_TOOLS_PREFS_NAME = BuildConfig.APPLICATION_ID + "_preferences"
    // MiTweaks end

    // Alipay begin
    const val ALIPAY_PACKAGE_NAME = "com.eg.android.AlipayGphone"
    const val ALIPAY_QRCODE_URI_PREFIX = "alipayqr://platformapi/startapp?saId=10000007&qrcode="
    const val ALIPAY_QRCODE_URL = "HTTPS://QR.ALIPAY.COM/FKX074142EKXD0OIMV8B60"
    // Alipay end

    // QQ begin
    const val QQ_GROUP_KEY = "bJYxW0EzBLB-3NeX1DBuOBxq9sSXnxN4"
    // QQ end

    // Taichi begin
    const val TAICHI_PACKAGE_NAME = "me.weishu.exp"
    const val TAICHI_MAIN_PAGE = "me.weishu.exp.ui.MainActivity"
    // Taichi end

    // Xposed Installer begin
    const val XPOSED_PACKAGE = "de.robv.android.xposed.installer"

    // Old Xposed installer
    const val XPOSED_OPEN_SECTION_ACTION = "$XPOSED_PACKAGE.OPEN_SECTION"
    const val XPOSED_EXTRA_SECTION = "section"

    // New Xposed installer
    const val XPOSED_ACTIVITY = "$XPOSED_PACKAGE.WelcomeActivity"
    const val XPOSED_EXTRA_FRAGMENT = "fragment"
    // Xposed Installer end

    // MIUI Weather begin
    const val MIUI_WEATHER_PACKAGE = "com.miui.weather2"
    // MIUI Weather end
}