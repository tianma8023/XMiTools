package com.tianma.tweaks.miui.app

import android.os.Bundle
import com.tianma.tweaks.miui.R
import com.tianma.tweaks.miui.app.base.BasePreferenceFragment

/**
 * Settings fragment for LockScreen
 */
class KeyguardSettingsFragment: BasePreferenceFragment{
    constructor() : super()
    constructor(title: CharSequence?) : super(title)


    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        super.onCreatePreferences(savedInstanceState, rootKey)

        addPreferencesFromResource(R.xml.keyguard_settings)
    }

}