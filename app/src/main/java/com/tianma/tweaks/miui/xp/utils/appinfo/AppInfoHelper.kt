package com.tianma.tweaks.miui.xp.utils.appinfo

import com.tianma.tweaks.miui.utils.XLog
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage
import java.io.File

object AppInfoHelper {

    @JvmStatic
    fun getAppInfo(lpparam: XC_LoadPackage.LoadPackageParam?): AppInfo {
        var versionCode = 0
        var versionName = "0"
        var packageName = ""

        if (lpparam != null) {
            try {
                val packageParserCls = XposedHelpers.findClass("android.content.pm.PackageParser", lpparam.classLoader)
                val packageParser = packageParserCls.newInstance()
                val apkPath = File(lpparam.appInfo.sourceDir)
                val pkg = XposedHelpers.callMethod(packageParser, "parsePackage", apkPath, 0)
                versionCode = XposedHelpers.getIntField(pkg, "mVersionCode")
                versionName = XposedHelpers.getObjectField(pkg, "mVersionName") as String
                packageName = lpparam.packageName
            } catch (throwable: Throwable) {
                XLog.e("Parse package info failed", throwable)
            }
        }

        return AppInfo(packageName, versionCode, versionName)
    }
}