package com.tianma.tweaks.miui.app.fragment

import android.os.Bundle
import androidx.preference.Preference
import com.tianma.tweaks.miui.R
import com.tianma.tweaks.miui.cons.PrefConst

/**
 * Settings fragment for System StatusBar
 */
class StatusBarSettingsFragment(title: CharSequence? = "") : BaseSettingsFragment(title), Preference.OnPreferenceChangeListener {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        super.onCreatePreferences(savedInstanceState, rootKey)
        addPreferencesFromResource(R.xml.statusbar_settings)

        findPreference<Preference>(PrefConst.STATUS_BAR_CLOCK_FORMAT)?.onPreferenceChangeListener = this
        findPreference<Preference>(PrefConst.CUSTOM_MOBILE_NETWORK_TYPE)?.onPreferenceChangeListener = this
    }

    override fun onResume() {
        super.onResume()

        val sp = preferenceManager.sharedPreferences

        val timeFormatPref = findPreference<Preference>(PrefConst.STATUS_BAR_CLOCK_FORMAT)
        timeFormatPref?.let {
            val timeFormat = sp.getString(PrefConst.STATUS_BAR_CLOCK_FORMAT, PrefConst.STATUS_BAR_CLOCK_FORMAT_DEFAULT)
            showStatusBarClockFormat(timeFormatPref, timeFormat)
        }

        val networkTypePref = findPreference<Preference>(PrefConst.CUSTOM_MOBILE_NETWORK_TYPE)
        networkTypePref?.let {
            val networkType = sp.getString(PrefConst.CUSTOM_MOBILE_NETWORK_TYPE, PrefConst.CUSTOM_MOBILE_NETWORK_TYPE_DEFAULT)
            showCustomMobileNetworkType(networkTypePref, networkType)
        }
    }

    override fun onPreferenceChange(preference: Preference?, newValue: Any?): Boolean {
        val key = preference?.key
        when {
            PrefConst.STATUS_BAR_CLOCK_FORMAT == key -> {
                showStatusBarClockFormat(preference, newValue as String?)
            }
            PrefConst.CUSTOM_MOBILE_NETWORK_TYPE == key -> {
                showCustomMobileNetworkType(preference, newValue as String?)
            }
            else -> {
                return false
            }
        }
        return true
    }

    private fun showStatusBarClockFormat(preference: Preference, newValue: String?) {
        preference.summary = newValue
    }

    private fun showCustomMobileNetworkType(preference: Preference, newValue: String?) {
        preference.summary = newValue
    }

}