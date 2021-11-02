package com.tianma.tweaks.miui.app.base

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.tianma.tweaks.miui.cons.AppConst

abstract class BasePreferenceFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        val pm = preferenceManager
        pm.sharedPreferencesName = AppConst.XMI_TOOLS_PREFS_NAME
    }

}