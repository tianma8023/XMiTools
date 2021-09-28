package com.tianma.tweaks.miui.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import androidx.annotation.NonNull;

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

    public static int getScreenWidth(@NonNull Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        if (wm != null) {
            wm.getDefaultDisplay().getMetrics(outMetrics);
            return outMetrics.widthPixels;
        } else {
            return 0;
        }
    }

    public static int getScreenHeight(@NonNull Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        if (wm != null) {
            wm.getDefaultDisplay().getMetrics(outMetrics);
            return outMetrics.heightPixels;
        } else {
            return 0;
        }
    }

}
