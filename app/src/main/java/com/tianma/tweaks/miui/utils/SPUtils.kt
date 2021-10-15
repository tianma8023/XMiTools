package com.tianma.tweaks.miui.utils

import android.content.Context
import com.tianma.tweaks.miui.utils.PreferencesUtils.getLong
import com.tianma.tweaks.miui.utils.PreferencesUtils.putLong

object SPUtils {
    private const val ONE_SENTENCE_LAST_REFRESH_TIME = "one_sentence_last_refresh_time"

    // 存储上次刷新时间
    @JvmStatic
    fun setOneSentenceLastRefreshTime(context: Context, timestamp: Long) {
        putLong(context, ONE_SENTENCE_LAST_REFRESH_TIME, timestamp)
    }

    // 获取上次刷新时间
    @JvmStatic
    fun getOneSentenceLastRefreshTime(context: Context): Long {
        return getLong(context, ONE_SENTENCE_LAST_REFRESH_TIME, 0L)
    }
}