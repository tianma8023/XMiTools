package com.tianma.tweaks.miui.utils

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi

object ContextUtils {

    @RequiresApi(api = Build.VERSION_CODES.N)
    private fun getProtectedContext(context: Context): Context {
        return if (context.isDeviceProtectedStorage) {
            context
        } else {
            context.createDeviceProtectedStorageContext()
        }
    }

    fun getProtectedContextIfNecessary(context: Context): Context {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // API >= 24 (Android 7.0+)
            // dataDir: /data/user_de/0/<package_name>/
            // spDir: /data/user_de/0/<package_name>/shared_prefs/
            // spFile: /data/user_de/0/<package_name>/shared_prefs/<preferences_name>.xml
            getProtectedContext(context)
        } else {
            // API < 24, there is no data encrypt.
            // dataDir: /data/data/<package_name>/
            context
        }
    }
}