package com.tianma.tweaks.miui.app.fragment

import android.annotation.SuppressLint
import com.tianma.tweaks.miui.app.base.BasePreferenceFragment
import com.tianma.tweaks.miui.cons.AppConst
import com.tianma.tweaks.miui.utils.ContextUtils
import com.tianma.tweaks.miui.utils.StorageUtils

/**
 * Base Fragment for settings.
 */
abstract class BaseSettingsFragment @JvmOverloads constructor(val title: CharSequence? = "") :
    BasePreferenceFragment() {

    override fun onPause() {
        super.onPause()
        setPreferenceWorldWritable()
    }

    @SuppressLint("SetWorldReadable", "SetWorldWritable")
    private fun setPreferenceWorldWritable() {
        val activity = activity ?: return
        val context = ContextUtils.getProtectedContextIfNecessary(activity.applicationContext)

        val prefsFile = StorageUtils.getSharedPreferencesFile(context, AppConst.XMI_TOOLS_PREFS_NAME)
        StorageUtils.setFileWorldWritable(prefsFile, 2)
    }

}