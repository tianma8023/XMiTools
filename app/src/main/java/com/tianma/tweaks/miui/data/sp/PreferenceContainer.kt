package com.tianma.tweaks.miui.data.sp

import com.tianma.tweaks.miui.cons.AppConst
import com.tianma.tweaks.miui.cons.PrefConst
import com.tianma.tweaks.miui.utils.prefs.PreferenceDelegate

/**
 * desc: App Preference Container
 * date: 2021/10/11
 */
object PreferenceContainer {

    /**
     * 一言 API 源
     */
    var oneSentenceApiSources by pref(PrefConst.ONE_SENTENCE_API_SOURCES, setOf<String>())

    /**
     * 一言 Hitokoto 类别
     */
    var hitokotoCategories by pref(PrefConst.HITOKOTO_CATEGORIES, setOf<String>())

    /**
     * 今日诗词类别
     */
    var onePoemCategories by pref(PrefConst.ONE_POEM_CATEGORIES, setOf<String>())

    /**
     * 是否显示一言 Hitokoto 来源
     */
    var showHitokotoSource by pref(PrefConst.SHOW_HITOKOTO_SOURCE, false)

    /**
     * 是否显示今日诗词作者
     */
    var showOnePoemAuthor by pref(PrefConst.SHOW_POEM_AUTHOR, false)

    private fun <T> pref(key: String, defaultValue: T): PreferenceDelegate<T> {
        return PreferenceDelegate(
            key,
            defaultValue,
            AppConst.XMI_TOOLS_PREFS_NAME
        )
    }

}
