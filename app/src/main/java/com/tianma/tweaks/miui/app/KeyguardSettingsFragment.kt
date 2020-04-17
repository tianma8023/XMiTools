package com.tianma.tweaks.miui.app

import android.os.Bundle
import androidx.preference.Preference
import com.tianma.tweaks.miui.R
import com.tianma.tweaks.miui.app.base.BasePreferenceFragment
import com.tianma.tweaks.miui.app.widget.dialog.OneSentenceSettingsDialogWrapper
import com.tianma.tweaks.miui.cons.PrefConst

/**
 * Settings fragment for LockScreen
 */
class KeyguardSettingsFragment: BasePreferenceFragment, Preference.OnPreferenceClickListener {
    constructor() : super()
    constructor(title: CharSequence?) : super(title)


    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        super.onCreatePreferences(savedInstanceState, rootKey)

        addPreferencesFromResource(R.xml.keyguard_settings)

        findPreference<Preference>(PrefConst.ONE_SENTENCE_SETTINGS).onPreferenceClickListener = this
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

    private fun onOneSentenceSettingsClicked() {
        if (context != null) {
            OneSentenceSettingsDialogWrapper(context!!).show()
        }
    }

}