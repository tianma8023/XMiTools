package com.tianma.tweaks.miui.app.fragment

import android.os.Bundle
import android.text.InputType
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import com.tianma.tweaks.miui.R
import com.tianma.tweaks.miui.app.base.BasePreferenceFragment
import com.tianma.tweaks.miui.app.widget.dialog.OneSentenceSettingsDialogWrapper
import com.tianma.tweaks.miui.cons.PrefConst

/**
 * Settings fragment for LockScreen
 */
class KeyguardSettingsFragment: BasePreferenceFragment, Preference.OnPreferenceClickListener, Preference.OnPreferenceChangeListener {
    constructor() : super()
    constructor(title: CharSequence?) : super(title)


    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        super.onCreatePreferences(savedInstanceState, rootKey)

        addPreferencesFromResource(R.xml.keyguard_settings)

        findPreference<Preference>(PrefConst.ONE_SENTENCE_SETTINGS).onPreferenceClickListener = this

        val oneSentencePref = findPreference<EditTextPreference>(PrefConst.ONE_SENTENCE_TEXT_SIZE)
        oneSentencePref.setOnBindEditTextListener { editText ->
            editText.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
            editText.setSelection(editText.text.length)
        }
        oneSentencePref.onPreferenceChangeListener = this
    }

    override fun onResume() {
        super.onResume()

        val oneSentencePref = findPreference<EditTextPreference>(PrefConst.ONE_SENTENCE_TEXT_SIZE)
        showOneSentenceTextSize(oneSentencePref, oneSentencePref.text)
    }

    override fun onPreferenceClick(preference: Preference?): Boolean {
        val key = preference?.key
        if (key == PrefConst.ONE_SENTENCE_SETTINGS) {
            onOneSentenceSettingsClicked()
        } else {
            return false
        }
        return true
    }

    override fun onPreferenceChange(preference: Preference?, newValue: Any?): Boolean {
        val key = preference?.key
        if (PrefConst.ONE_SENTENCE_TEXT_SIZE == key) {
            val value = newValue as String?
            if (value.isNullOrEmpty()) {
                return false
            } else {
                showOneSentenceTextSize(preference, value)
            }
        }
        return true
    }

    private fun onOneSentenceSettingsClicked() {
        context?.let {
            OneSentenceSettingsDialogWrapper(it).show()
        }
    }

    private fun showOneSentenceTextSize(preference: Preference, newValue: String) {
        preference.summary = newValue
    }

}