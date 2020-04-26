package com.tianma.tweaks.miui.app.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.preference.Preference
import com.tianma.tweaks.miui.BuildConfig
import com.tianma.tweaks.miui.R
import com.tianma.tweaks.miui.app.base.BasePreferenceFragment
import com.tianma.tweaks.miui.cons.AppConst
import com.tianma.tweaks.miui.cons.PrefConst
import com.tianma.tweaks.miui.utils.ContextUtils
import com.tianma.tweaks.miui.utils.PackageUtils
import com.tianma.tweaks.miui.utils.StorageUtils
import com.tianma.tweaks.miui.utils.Utils

class GeneralSettingsFragment(title: CharSequence? = "") : BasePreferenceFragment(title), Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener {

    private lateinit var mActivity: Activity

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        super.onCreatePreferences(savedInstanceState, rootKey)
        addPreferencesFromResource(R.xml.main_settings)

        findPreference<Preference>(PrefConst.HIDE_LAUNCHER_ICON)?.onPreferenceChangeListener = this

        findPreference<Preference>(PrefConst.SOURCE_CODE)?.onPreferenceClickListener = this
        findPreference<Preference>(PrefConst.KEY_JOIN_QQ_GROUP)?.onPreferenceClickListener = this
        findPreference<Preference>(PrefConst.DONATE_BY_ALIPAY)?.onPreferenceClickListener = this
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mActivity = requireActivity()
    }

    override fun onPause() {
        super.onPause()
        setPreferenceWorldWritable()
    }

    override fun onResume() {
        super.onResume()

        showVersionInfo()
    }

    override fun onPreferenceClick(preference: Preference?): Boolean {
        when (preference?.key) {
            PrefConst.SOURCE_CODE -> {
                showSourceCode()
            }
            PrefConst.KEY_JOIN_QQ_GROUP -> {
                joinQQGroup()
            }
            PrefConst.DONATE_BY_ALIPAY -> {
                donateByAlipay()
            }
            else -> {
                return false
            }
        }
        return true
    }

    override fun onPreferenceChange(preference: Preference?, newValue: Any): Boolean {
        val key = preference?.key
        if (PrefConst.HIDE_LAUNCHER_ICON == key) {
            hideOrShowLauncherIcon(newValue as Boolean)
        } else {
            return false
        }
        return true
    }

    private fun hideOrShowLauncherIcon(hide: Boolean) {
        val pm = mActivity.packageManager
        val launcherCN = ComponentName(mActivity, AppConst.MAIN_ACTIVITY_ALIAS)
        val state = if (hide) PackageManager.COMPONENT_ENABLED_STATE_DISABLED else PackageManager.COMPONENT_ENABLED_STATE_ENABLED
        if (pm.getComponentEnabledSetting(launcherCN) != state) {
            pm.setComponentEnabledSetting(launcherCN, state, PackageManager.DONT_KILL_APP)
        }
    }

    @SuppressLint("SetWorldReadable", "SetWorldWritable")
    private fun setPreferenceWorldWritable() {
        val context: Context = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // API >= 24 (Android 7.0+)
            // dataDir: /data/user_de/0/<package_name>/
            // spDir: /data/user_de/0/<package_name>/shared_prefs/
            // spFile: /data/user_de/0/<package_name>/shared_prefs/<preferences_name>.xml
            ContextUtils.getProtectedContext(mActivity.applicationContext)
        } else {
            // API < 24, there is no data encrypt.
            // dataDir: /data/data/<package_name>/
            mActivity.applicationContext
        }
        val prefsFile = StorageUtils.getSharedPreferencesFile(context, AppConst.X_MIUI_CLOCK_PREFS_NAME)
        StorageUtils.setFileWorldWritable(prefsFile, 2)
    }

    private fun showVersionInfo() {
        findPreference<Preference>(PrefConst.APP_VERSION)?.summary = BuildConfig.VERSION_NAME
    }

    private fun showSourceCode() {
        Utils.showWebPage(activity, AppConst.PROJECT_SOURCE_CODE_URL)
    }

    private fun joinQQGroup() {
        PackageUtils.joinQQGroup(context)
    }

    private fun donateByAlipay() {
        PackageUtils.startAlipayDonatePage(context)
    }
}