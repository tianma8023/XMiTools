package com.tianma.tweaks.miui.utils;

import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;

public class ContextUtils {

    private ContextUtils() {
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static Context getProtectedContext(Context context) {
        return context.isDeviceProtectedStorage() ? context
                : context.createDeviceProtectedStorageContext();
    }

}
