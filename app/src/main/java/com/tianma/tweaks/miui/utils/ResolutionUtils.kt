package com.tianma.tweaks.miui.utils

import android.content.Context
import android.util.DisplayMetrics
import com.tianma.tweaks.miui.utils.ResolutionUtils
import android.view.WindowManager

object ResolutionUtils {
    private fun getDisplayMetrics(context: Context): DisplayMetrics {
        return context.resources.displayMetrics
    }

    @JvmStatic
    fun dp2px(context: Context, dp: Float): Float {
        return dp * getDisplayMetrics(context).density
    }

    fun px2dp(context: Context, px: Float): Float {
        return px / getDisplayMetrics(context).density
    }

    fun getScreenWidth(context: Context): Int {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val outMetrics = DisplayMetrics()
        return if (wm != null) {
            wm.defaultDisplay.getMetrics(outMetrics)
            outMetrics.widthPixels
        } else {
            0
        }
    }

    fun getScreenHeight(context: Context): Int {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val outMetrics = DisplayMetrics()
        return if (wm != null) {
            wm.defaultDisplay.getMetrics(outMetrics)
            outMetrics.heightPixels
        } else {
            0
        }
    }
}