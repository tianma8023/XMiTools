package com.tianma.tweaks.miui.app.fragment

import android.os.Bundle
import androidx.preference.Preference
import com.tianma.tweaks.miui.BuildConfig
import com.tianma.tweaks.miui.R
import com.tianma.tweaks.miui.app.base.BasePreferenceFragment
import com.tianma.tweaks.miui.cons.AppConst
import com.tianma.tweaks.miui.cons.PrefConst
import com.tianma.tweaks.miui.utils.PackageUtils
import com.tianma.tweaks.miui.utils.Utils

class AboutSettingsFragment: BasePreferenceFragment, Preference.OnPreferenceClickListener {

    constructor() : super()
    constructor(title: CharSequence?) : super(title)


    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        super.onCreatePreferences(savedInstanceState, rootKey)
        addPreferencesFromResource(R.xml.about_settings)

        findPreference<Preference>(PrefConst.SOURCE_CODE).onPreferenceClickListener = this
        findPreference<Preference>(PrefConst.KEY_JOIN_QQ_GROUP).onPreferenceClickListener = this
        findPreference<Preference>(PrefConst.DONATE_BY_ALIPAY).onPreferenceClickListener = this
    }

    override fun onResume() {
        super.onResume()

        showVersionInfo()
    }

    override fun onPreferenceClick(preference: Preference?): Boolean {
        val key = preference!!.key
        when {
            PrefConst.SOURCE_CODE == key -> {
                showSourceCode()
            }
            PrefConst.KEY_JOIN_QQ_GROUP == key -> {
                joinQQGroup()
            }
            PrefConst.DONATE_BY_ALIPAY == key -> {
                donateByAlipay()
            }
            else -> {
                return false
            }
        }
        return true
    }

    private fun showVersionInfo() {
        findPreference<Preference>(PrefConst.APP_VERSION).summary = BuildConfig.VERSION_NAME
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