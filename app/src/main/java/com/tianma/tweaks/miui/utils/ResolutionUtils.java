package com.tianma.tweaks.miui.utils;

import android.content.Context;
import android.util.DisplayMetrics;

public class ResolutionUtils {

    private ResolutionUtils() {

    }

    private static DisplayMetrics getDisplayMetrics(Context context) {
        return context.getResources().getDisplayMetrics();
    }

    public static float dp2px(Context context, float dp) {
        return dp * getDisplayMetrics(context).density;
    }

    public static float px2dp(Context context, float px) {
        return px / getDisplayMetrics(context).density;
    }

}
