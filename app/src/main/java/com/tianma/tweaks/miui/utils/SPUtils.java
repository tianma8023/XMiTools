package com.tianma.tweaks.miui.utils;

import android.content.Context;

import com.tianma.tweaks.miui.cons.PrefConst;

import java.util.HashSet;
import java.util.Set;

import retrofit2.http.PUT;

public class SPUtils {

    private SPUtils() {

    }

    // 设置一言 API 源
    public static void setOneSentenceApiSources(Context context, Set<String> apiSources) {
        PreferencesUtils.putStringSet(context, PrefConst.ONE_SENTENCE_API_SOURCES, apiSources);
    }

    // 获取一言 API 源
    public static Set<String> getOneSentenceApiSources(Context context) {
        return PreferencesUtils.getStringSet(context, PrefConst.ONE_SENTENCE_API_SOURCES, new HashSet<>());
    }

    // 设置一言 Hitokoto 类别
    public static void setHitokotoCategories(Context context, Set<String> hitokotoCategories) {
        PreferencesUtils.putStringSet(context, PrefConst.HITOKOTO_CATEGORIES, hitokotoCategories);
    }

    // 获取一言 Hitokoto 类别
    public static Set<String> getHitokotoCategories(Context context) {
        return PreferencesUtils.getStringSet(context, PrefConst.HITOKOTO_CATEGORIES, new HashSet<>());
    }

    // 设置今日诗词类别
    public static void setOnePoemCategories(Context context, Set<String> onePoemCategories) {
        PreferencesUtils.putStringSet(context, PrefConst.ONE_POEM_CATEGORIES, onePoemCategories);
    }

    // 获取今日诗词类别
    public static Set<String> getOnePoemCategories(Context context) {
        return PreferencesUtils.getStringSet(context, PrefConst.ONE_POEM_CATEGORIES, new HashSet<>());
    }

    // 设置是否显示一言 Hitokoto 来源
    public static void setShowHitokotoSource(Context context, boolean showHitokotoSource) {
        PreferencesUtils.putBoolean(context, PrefConst.SHOW_HITOKOTO_SOURCE, showHitokotoSource);
    }

    // 获取是否显示一言 Hitokoto 来源
    public static boolean getShowHitokotoSource(Context context) {
        return PreferencesUtils.getBoolean(context, PrefConst.SHOW_HITOKOTO_SOURCE, false);
    }

    // 设置是否显示今日诗词作者
    public static void setShowOnePoemAuthor(Context context, boolean showPoemAuthor) {
        PreferencesUtils.putBoolean(context, PrefConst.SHOW_POEM_AUTHOR, showPoemAuthor);
    }

    // 获取是否显示今日诗词作者
    public static boolean getShowPoemAuthor(Context context) {
        return PreferencesUtils.getBoolean(context, PrefConst.SHOW_POEM_AUTHOR, false);
    }

    private static final String ONE_SENTENCE_LAST_REFRESH_TIME = "one_sentence_last_refresh_time";

    // 存储上次刷新时间
    public static void setOneSentenceLastRefreshTime(Context context, long timestamp) {
        PreferencesUtils.putLong(context, ONE_SENTENCE_LAST_REFRESH_TIME, timestamp);
    }

    // 获取上次刷新时间
    public static long getOneSentenceLastRefreshTime(Context context) {
        return PreferencesUtils.getLong(context, ONE_SENTENCE_LAST_REFRESH_TIME, 0L);
    }

}
