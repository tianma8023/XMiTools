package com.tianma.tweaks.miui.utils

import android.content.Context
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import android.widget.Toast
import com.tianma.tweaks.miui.R
import java.lang.Exception

/**
 * Other Utils
 */
object Utils {
    fun showWebPage(context: Context?, url: String?) {
        try {
            val cti = CustomTabsIntent.Builder().build()
            cti.launchUrl(context!!, Uri.parse(url))
        } catch (e: Exception) {
            Toast.makeText(context, R.string.browser_install_or_enable_prompt, Toast.LENGTH_SHORT)
                .show()
        }
    }
}