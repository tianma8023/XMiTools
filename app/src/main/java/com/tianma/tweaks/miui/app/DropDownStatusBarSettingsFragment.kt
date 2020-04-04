package com.tianma.tweaks.miui.app

import android.os.Bundle
import android.text.InputType
import android.text.TextUtils
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import com.tianma.tweaks.miui.R
import com.tianma.tweaks.miui.app.base.BasePreferenceFragment
import com.tianma.tweaks.miui.cons.PrefConst

/**
 * dSettings fragment for System DropDown StatusBar
 */
class DropDownStatusBarSettingsFragment: BasePreferenceFragment, Preference.OnPreferenceChangeListener {

    constructor() : super()
    constructor(title: CharSequence?) : super(title)


    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        super.onCreatePreferences(savedInstanceState, rootKey)
        addPreferencesFromResource(R.xml.dropdown_statusbar_settings)

        val weatherTextSizePref = findPreference<EditTextPreference>(PrefConst.DROPDOWN_STATUS_BAR_WEATHER_TEXT_SIZE)
        weatherTextSizePref.setOnBindEditTextListener { editText ->
            editText.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
            editText.setSelection(editText.text.length)
        }
        weatherTextSizePref.onPreferenceChangeListener = this
    }

    override fun onResume() {
        super.onResume()

        val weatherTextSizePref = findPreference<EditTextPreference>(PrefConst.DROPDOWN_STATUS_BAR_WEATHER_TEXT_SIZE)
        showWeatherTextSize(weatherTextSizePref, weatherTextSizePref.text)
    }

    private fun showWeatherTextSize(preference: EditTextPreference, newValue: String) {
        preference.summary = newValue
    }

    override fun onPreferenceChange(preference: Preference?, newValue: Any?): Boolean {
        val key = preference?.key
        if (PrefConst.DROPDOWN_STATUS_BAR_WEATHER_TEXT_SIZE == key) {
            val value = newValue as String
            if (TextUtils.isEmpty(value)) {
                return false
            }
            showWeatherTextSize((preference as EditTextPreference?)!!, (newValue as String?)!!)
        } else {
            return false
        }
        return true
    }

}