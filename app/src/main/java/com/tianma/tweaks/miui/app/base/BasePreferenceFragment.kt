package com.tianma.tweaks.miui.app.base

import kotlin.jvm.JvmOverloads
import androidx.preference.PreferenceFragmentCompat
import android.os.Bundle
import com.tianma.tweaks.miui.cons.AppConst

abstract class BasePreferenceFragment @JvmOverloads constructor(val title: CharSequence? = "") :
    PreferenceFragmentCompat() {
    // var title: CharSequence? = null

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        val pm = preferenceManager
        pm.sharedPreferencesName = AppConst.XMI_TOOLS_PREFS_NAME
    }

//    init {
//        title = title
//    }
}