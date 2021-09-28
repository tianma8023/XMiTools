package com.tianma.tweaks.miui.utils

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi

object ContextUtils {
    @RequiresApi(api = Build.VERSION_CODES.N)
    fun getProtectedContext(context: Context): Context {
        return if (context.isDeviceProtectedStorage) {
            context
        } else {
            context.createDeviceProtectedStorageContext()
        }
    }
}