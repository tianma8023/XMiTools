package com.tianma.tweaks.miui.cons;

import com.tianma.tweaks.miui.BuildConfig;

public interface AppConst {

    /* MiTweaks begin */
    String MAIN_ACTIVITY_ALIAS = BuildConfig.APPLICATION_ID + ".app.MainActivityAlias";
    String PROJECT_SOURCE_CODE_URL = "https://github.com/tianma8023/MiTweaks";
    /* MiTweaks end */


    /* Alipay begin */
    String ALIPAY_PACKAGE_NAME = "com.eg.android.AlipayGphone";
    String ALIPAY_QRCODE_URI_PREFIX = "alipayqr://platformapi/startapp?saId=10000007&qrcode=";
    String ALIPAY_QRCODE_URL = "HTTPS://QR.ALIPAY.COM/FKX074142EKXD0OIMV8B60";
    /* Alipay end */

    /* Taichi begin */
    String TAICHI_PACKAGE_NAME = "me.weishu.exp";
    String TAICHI_MAIN_PAGE = "me.weishu.exp.ui.MainActivity";
    /* Taichi end */

    /* Xposed Installer begin */
    String XPOSED_PACKAGE = "de.robv.android.xposed.installer";
    // Old Xposed installer
    String XPOSED_OPEN_SECTION_ACTION = XPOSED_PACKAGE + ".OPEN_SECTION";
    String XPOSED_EXTRA_SECTION = "section";
    // New Xposed installer
    String XPOSED_ACTIVITY = XPOSED_PACKAGE + ".WelcomeActivity";
    String XPOSED_EXTRA_FRAGMENT = "fragment";
    /* Xposed Installer end */

}
