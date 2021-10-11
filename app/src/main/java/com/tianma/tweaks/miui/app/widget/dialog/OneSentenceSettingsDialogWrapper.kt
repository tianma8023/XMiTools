package com.tianma.tweaks.miui.app.widget.dialog

import android.content.Context
import android.view.View
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.google.android.flexbox.FlexboxLayoutManager
import com.tianma.tweaks.miui.R
import com.tianma.tweaks.miui.app.widget.tag.ItemClickCallback
import com.tianma.tweaks.miui.app.widget.tag.TagAdapter
import com.tianma.tweaks.miui.app.widget.tag.TagBean
import com.tianma.tweaks.miui.cons.PrefConst
import com.tianma.tweaks.miui.data.sp.PreferenceContainer

/**
 * OneSentence Settings Dialog
 */
class OneSentenceSettingsDialogWrapper(private val context: Context) {

    private val apiSourceKeys = arrayOf(
            PrefConst.API_SOURCE_HITOKOTO,
            PrefConst.API_SOURCE_ONE_POEM
    )

    private val hitokotoCategoryKeys = arrayOf(
            PrefConst.HITOKOTO_CATEGORY_ALL,  // 全部
            "a", // 动画
            "b", // 漫画
            "c", // 游戏
            "d", // 文学
            "e", // 原创
            "f", // 来自网络
            "g", // 其他
            "h", // 影视
            "i", // 诗词
            "j", // 网易云
            "k", // 哲学
            "l"  // 抖机灵
    )

    private val onePoemCategoryKeys = arrayOf(
            PrefConst.ONE_POEM_CATEGORY_ALL,      // 全部
            "shuqing",  // 抒情
            "siji",     // 四季
            "shanshui", // 山水
            "tianqi",   // 天气
            "renwu",    // 人物
            "rensheng", // 人生
            "shenghuo", // 生活
            "jieri",    // 节日
            "dongwu",   // 动物
            "zhiwu",    // 植物
            "shiwu"     // 食物
    )

    private var delegateDialog: MaterialDialog? = null

    private var apiSourceAdapter: TagAdapter? = null
    private var hitokotoTagAdapter: TagAdapter? = null
    private var onePoemTagAdapter: TagAdapter? = null

    private var hitokotoSourceCheckBox: AppCompatCheckBox? = null
    private var onePoemAuthorCheckBox: AppCompatCheckBox? = null

    // 初始化 Dialog
    private fun initDialog(): MaterialDialog {
        val view = View.inflate(context, R.layout.dialog_hitokoto_settings, null)

        // API 源
        view.findViewById<RecyclerView>(R.id.apiSourcesRecyclerView).apply {
            layoutManager = FlexboxLayoutManager(context)
            adapter = TagAdapter(context, mutableListOf()).apply {
                itemClickCallback = TagClickCallback(this)
                apiSourceAdapter = this
            }
        }

        // 一言
        view.findViewById<RecyclerView>(R.id.hitokotoCategoryRecyclerView).apply {
            layoutManager = FlexboxLayoutManager(context)
            adapter = TagAdapter(context, mutableListOf()).apply {
                itemClickCallback = TagClickCallback(this)
                hitokotoTagAdapter = this
            }
        }

        hitokotoSourceCheckBox = view.findViewById(R.id.hitokotoSourceCheckBox)

        // 今日诗词
        view.findViewById<RecyclerView>(R.id.onePoemCategoryRecyclerView).apply {
            layoutManager = FlexboxLayoutManager(context)
            adapter = TagAdapter(context, mutableListOf()).apply {
                itemClickCallback = TagClickCallback(this)
                onePoemTagAdapter = this
            }
        }

        onePoemAuthorCheckBox = view.findViewById(R.id.onePoemAuthorCheckBox)

        return MaterialDialog(context)
                .title(R.string.pref_one_sentence_settings_title)
                .customView(view = view, scrollable = true, horizontalPadding = true)
                .positiveButton(R.string.confirm) {
                    onDialogConfirmed()
                }
                .negativeButton(R.string.cancel)
    }

    // 初始化 Dialog 中的数据
    private fun initData() {
        val apiSources = getApiSourceTagList()
        apiSourceAdapter?.setDataList(apiSources)

        val hitokotoTagList = getHitokotoTagList()
        hitokotoTagAdapter?.setDataList(hitokotoTagList)

        // hitokotoSourceCheckBox?.isChecked = SPUtils.getShowHitokotoSource(context)
        hitokotoSourceCheckBox?.isChecked = PreferenceContainer.showHitokotoSource

        val onePoemTagList = getOnePoemTagList()
        onePoemTagAdapter?.setDataList(onePoemTagList)

        // onePoemAuthorCheckBox?.isChecked = SPUtils.getShowPoemAuthor(context)
        onePoemAuthorCheckBox?.isChecked = PreferenceContainer.showOnePoemAuthor

        for (tagBean in apiSources) {
            onApiSourceChanged(tagBean)
        }
    }

    fun show() {
        if (delegateDialog == null) {
            delegateDialog = initDialog()
            initData()
        }
        delegateDialog!!.show()
    }

    fun cancel() {
        if (delegateDialog != null && delegateDialog!!.isShowing) {
            delegateDialog!!.cancel()
        }
    }

    fun dismiss() {
        if (delegateDialog != null && delegateDialog!!.isShowing) {
            delegateDialog!!.dismiss()
        }
    }

    private fun getApiSourceTagList(): MutableList<TagBean> {
        val cateValueArray = context.resources.getStringArray(R.array.api_source_value_array)

        val list = mutableListOf<TagBean>()

        for (i in cateValueArray.indices) {
            list.add(TagBean(apiSourceKeys[i], cateValueArray[i]))
        }

        // val selectedSet = SPUtils.getOneSentenceApiSources(context)
        val selectedSet = PreferenceContainer.oneSentenceApiSources
        if (!selectedSet.isNullOrEmpty()) {
            for (key in selectedSet) {
                for (tagBean in list) {
                    if (tagBean.key == key) {
                        tagBean.isSelected = true
                        continue
                    }
                }
            }
        }

        return list
    }

    private fun getHitokotoTagList(): MutableList<TagBean> {
        val cateValueArray = context.resources.getStringArray(R.array.hitokoto_type_category_value_array)

        val list = mutableListOf<TagBean>()

        for (i in cateValueArray.indices) {
            list.add(TagBean(hitokotoCategoryKeys[i], cateValueArray[i]))
        }

        // val selectedSet = SPUtils.getHitokotoCategories(context)
        val selectedSet = PreferenceContainer.hitokotoCategories
        if (!selectedSet.isNullOrEmpty()) {
            for (key in selectedSet) {
                for (tagBean in list) {
                    if (tagBean.key == key) {
                        tagBean.isSelected = true
                        continue
                    }
                }
            }
        }

        return list
    }


    private fun getOnePoemTagList(): MutableList<TagBean> {
        val cateValueArray = context.resources.getStringArray(R.array.one_poem_category_value_array)

        val list = mutableListOf<TagBean>()

        for (i in cateValueArray.indices) {
            list.add(TagBean(onePoemCategoryKeys[i], cateValueArray[i]))
        }

        // val selectedSet = SPUtils.getOnePoemCategories(context)
        val selectedSet = PreferenceContainer.onePoemCategories
        if (!selectedSet.isNullOrEmpty()) {
            for (key in selectedSet) {
                for (tagBean in list) {
                    if (tagBean.key == key) {
                        tagBean.isSelected = true
                        continue
                    }
                }
            }
        }

        return list
    }

    inner class TagClickCallback(private var tagAdapter: TagAdapter?) : ItemClickCallback<TagBean> {
        override fun onItemClicked(itemView: View?, item: TagBean?, position: Int) {
            handleItemClicked(itemView, item, position)
        }

        override fun onItemLongClicked(itemView: View?, item: TagBean?, position: Int): Boolean {
            handleItemClicked(itemView, item, position)
            return true
        }

        private fun handleItemClicked(itemView: View?, item: TagBean?, position: Int) {
            item?.let {
                val isSelected = it.isSelected
                if (it.key == PrefConst.HITOKOTO_CATEGORY_ALL ||
                        it.key == PrefConst.ONE_POEM_CATEGORY_ALL) {
                    tagAdapter?.setAllSelected(!isSelected)
                } else {
                    tagAdapter?.setItemSelected(!isSelected, position)
                }


                onApiSourceChanged(it)
            }
        }
    }

    private fun onApiSourceChanged(apiSourceTagBean: TagBean) {
        apiSourceTagBean.let {
            val enabled = it.isSelected
            when (it.key) {
                PrefConst.API_SOURCE_HITOKOTO -> {
                    hitokotoTagAdapter?.setAllEnabled(enabled)
                    hitokotoSourceCheckBox?.isEnabled = enabled
                }
                PrefConst.API_SOURCE_ONE_POEM -> {
                    onePoemTagAdapter?.setAllEnabled(enabled)
                    onePoemAuthorCheckBox?.isEnabled = enabled
                }
                else -> {
                    // DO NOTHING
                }
            }
        }
    }

    private fun onDialogConfirmed() {
        saveApiSources()
        saveAboutHitokoto()
        saveAboutOnePoem()
    }

    private fun saveApiSources() {
        apiSourceAdapter?.let {
            // SPUtils.setOneSentenceApiSources(context, getSelectedTagsFromTagAdapter(it))
            PreferenceContainer.oneSentenceApiSources = getSelectedTagsFromTagAdapter(it)
        }
    }

    private fun saveAboutHitokoto() {
        hitokotoTagAdapter?.let {
            // SPUtils.setHitokotoCategories(context, getSelectedTagsFromTagAdapter(it))
            PreferenceContainer.hitokotoCategories = getSelectedTagsFromTagAdapter(it)
        }

        hitokotoSourceCheckBox?.let {
            // SPUtils.setShowHitokotoSource(context, it.isChecked)
            PreferenceContainer.showHitokotoSource = it.isChecked
        }
    }

    private fun saveAboutOnePoem() {
        onePoemTagAdapter?.let {
            // SPUtils.setOnePoemCategories(context, getSelectedTagsFromTagAdapter(it))
            PreferenceContainer.onePoemCategories = getSelectedTagsFromTagAdapter(it)
        }

        onePoemAuthorCheckBox?.let {
            // SPUtils.setShowOnePoemAuthor(context, it.isChecked)
            PreferenceContainer.showOnePoemAuthor = it.isChecked
        }
    }

    private fun getSelectedTagsFromTagAdapter(tagAdapter: TagAdapter): Set<String> {
        val selectedSet = mutableSetOf<String>()

        for (item in tagAdapter.getDataList()) {
            if (item.isSelected) {
                selectedSet.add(item.key)
            }
        }

        return selectedSet
    }

}