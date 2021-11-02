package com.tianma.tweaks.miui.utils

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.annotation.IntDef
import com.tianma.tweaks.miui.BuildConfig
import com.tianma.tweaks.miui.R
import com.tianma.tweaks.miui.cons.AppConst
import com.tianma.tweaks.miui.xp.hook.systemui.SystemUIHook

/**
 * 包相关工具类
 */
object PackageUtils {
    /**
     * not installed
     */
    const val PACKAGE_NOT_INSTALLED = 0

    /**
     * installed & disabled
     */
    const val PACKAGE_DISABLED = 1

    /**
     * installed & enabled
     */
    const val PACKAGE_ENABLED = 2

    @IntDef(PACKAGE_NOT_INSTALLED, PACKAGE_DISABLED, PACKAGE_ENABLED)
    annotation class PackageState

    @JvmStatic
    @PackageState
    fun checkPackageState(context: Context, packageName: String?): Int {
        return if (isPackageEnabled(context, packageName)) {
            // installed & enabled
            PACKAGE_ENABLED
        } else {
            if (isPackageInstalled(context, packageName)) {
                // installed & disabled
                PACKAGE_DISABLED
            } else {
                // not installed
                PACKAGE_NOT_INSTALLED
            }
        }
    }

    /**
     * 指定的包名对应的App是否已安装
     */
    fun isPackageInstalled(context: Context, packageName: String?): Boolean {
        val pm = context.packageManager
        try {
            val packageInfo = pm.getPackageInfo(packageName, 0)
            return packageInfo != null
        } catch (e: PackageManager.NameNotFoundException) {
            // ignore
        }
        return false
    }

    /**
     * 对应包名的应用是否已启用
     */
    fun isPackageEnabled(context: Context, packageName: String?): Boolean {
        val pm = context.packageManager
        try {
            val appInfo = pm.getApplicationInfo(packageName, 0)
            return appInfo != null && appInfo.enabled
        } catch (e: PackageManager.NameNotFoundException) {
            // ignore
        }
        return false
    }

    private fun checkAlipayExists(context: Context): Boolean {
        when (checkPackageState(context, AppConst.ALIPAY_PACKAGE_NAME)) {
            PACKAGE_ENABLED -> {
                return true
            }
            PACKAGE_DISABLED -> {
                Toast.makeText(context, R.string.alipay_enable_prompt, Toast.LENGTH_SHORT).show()
            }
            PACKAGE_NOT_INSTALLED -> {
                Toast.makeText(context, R.string.alipay_install_prompt, Toast.LENGTH_SHORT).show()
            }
        }
        return false
    }

    /**
     * 打开支付宝捐赠页
     */
    fun startAlipayDonatePage(context: Context?) {
        context ?: return

        if (checkAlipayExists(context)) {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data =
                Uri.parse(AppConst.ALIPAY_QRCODE_URI_PREFIX + AppConst.ALIPAY_QRCODE_URL)
            context.startActivity(intent)
        }
    }

    private fun checkTaiChiExists(context: Context): Boolean {
        when (checkPackageState(context, AppConst.TAICHI_PACKAGE_NAME)) {
            PACKAGE_ENABLED -> {
                // installed & enabled
                return true
            }
            PACKAGE_NOT_INSTALLED -> {
                Toast.makeText(context, R.string.taichi_install_prompt, Toast.LENGTH_SHORT).show()
            }
            PACKAGE_DISABLED -> {
                Toast.makeText(context, R.string.taichi_enable_prompt, Toast.LENGTH_SHORT).show()
            }
        }
        return false
    }

    /**
     * 请求太极勾选本模块
     */
    fun startCheckModuleInTaiChi(context: Context) {
        if (checkTaiChiExists(context)) {
            val intent = Intent("me.weishu.exp.ACTION_MODULE_MANAGE")
            intent.data = Uri.parse("package:" + BuildConfig.APPLICATION_ID)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }

    /**
     * 在太极中勾选本模块相关的应用
     */
    fun startAddAppsInTaiChi(context: Context) {
        if (checkTaiChiExists(context)) {
            val intent = Intent("me.weishu.exp.ACTION_ADD_APP")
            val uriStr = "package:" + SystemUIHook.PACKAGE_NAME
            // "|" + MiuiLauncherHook.PACKAGE_NAME;
            intent.data = Uri.parse(uriStr)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }

    /**
     * Join QQ group
     */
    fun joinQQGroup(context: Context?) {
        context ?: return

        val key = AppConst.QQ_GROUP_KEY
        val intent = Intent()
        intent.data =
            Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26k%3D$key")
        // 此Flag可根据具体产品需要自定义，如设置，则在加群界面按返回，返回手Q主界面，不设置，按返回会返回到呼起产品界面
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            // 未安装手Q或安装的版本不支持
            Toast.makeText(context, R.string.prompt_join_qq_group_failed, Toast.LENGTH_SHORT).show()
        }
    }
}