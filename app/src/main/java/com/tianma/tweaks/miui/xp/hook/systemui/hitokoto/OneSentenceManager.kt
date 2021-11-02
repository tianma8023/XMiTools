package com.tianma.tweaks.miui.xp.hook.systemui.hitokoto

import android.content.Context
import android.os.SystemClock
import com.tianma.tweaks.miui.cons.PrefConst
import com.tianma.tweaks.miui.data.http.repository.DataRepository
import com.tianma.tweaks.miui.data.sp.XPrefContainer
import com.tianma.tweaks.miui.utils.SPUtils
import com.tianma.tweaks.miui.utils.logD
import com.tianma.tweaks.miui.utils.logE
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.*

/**
 * 一言加载工具
 */
class OneSentenceManager private constructor(){

    companion object {
        @Volatile
        private var instance: OneSentenceManager? = null

        @JvmStatic
        fun getInstance() : OneSentenceManager {
            return instance ?: synchronized(this) {
                instance ?: OneSentenceManager().also { instance = it }
            }
        }
    }

    private val compositeDisposable = CompositeDisposable()

    fun loadOneSentence(modContext: Context, oneSentenceLoadListener: OneSentenceLoadListener?) {
        try {
            // val apiSources = XSPUtils.getOneSentenceApiSources(xsp)
            val apiSources = XPrefContainer.oneSentenceApiSources
            if (apiSources.isEmpty()) {
                logE("No OneSentence API chosen")
                return
            }

            // 判断是否满足刷新频率
            // val duration = XSPUtils.getOneSentenceRefreshRate(xsp) * 60 * 1000
            val duration = XPrefContainer.getOneSentenceRefreshRate() * 60 * 1000
            if (duration > 0) {
                // 因为是 modContext, 所以上次刷新时间数据的存取都是在 com.android.systemui 的 shared_prefs 文件中
                val lastTime = SPUtils.getOneSentenceLastRefreshTime(modContext)
                val curTime = SystemClock.elapsedRealtime()
                if (curTime - lastTime < duration) {
                    logD("Cannot fetch new data due to refresh rate")
                    return
                }
                SPUtils.setOneSentenceLastRefreshTime(modContext, curTime)
            }

            val randIdx = Random().nextInt(apiSources.size)
            when (val apiSource = ArrayList(apiSources)[randIdx]) {
                PrefConst.API_SOURCE_HITOKOTO -> {
                    loadHitokoto(oneSentenceLoadListener)
                }
                PrefConst.API_SOURCE_ONE_POEM -> {
                    loadOnePoem(oneSentenceLoadListener)
                }
                else -> {
                    logE("Unknown API source: $apiSource")
                }
            }
        } catch (e: Exception) {
            logE("Error occurs when load OneSentence", e)
        }
    }

    private fun loadHitokoto(oneSentenceLoadListener: OneSentenceLoadListener?) {
        try {
            // val hitokotoCategories = XSPUtils.getHitokotoCategories(xsp)
            val hitokotoCategories = XPrefContainer.hitokotoCategories
            val params = mutableListOf<String>()
            if (hitokotoCategories.isNullOrEmpty()) {
                params.add("")
            } else {
                if (hitokotoCategories.contains(PrefConst.ONE_POEM_CATEGORY_ALL)) {
                    params.add("")
                } else {
                    params.addAll(hitokotoCategories)
                }
            }

            // val showHitokotoSource = XSPUtils.getShowHitokotoSource(xsp)
            val showHitokotoSource = XPrefContainer.showHitokotoSource
            val disposable = DataRepository.getHitokoto(params)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ hitokoto ->
                        hitokoto?.let {
                            logD(hitokoto.toString())
                            val content = hitokoto.content ?: ""
                            val oneSentence = if (showHitokotoSource) {
                                val source = hitokoto.from ?: ""
                                String.format("%s <%s>", content, source)
                            } else {
                                content
                            }
                            oneSentenceLoadListener?.onSuccess(oneSentence)
                        }
                    }, { throwable -> oneSentenceLoadListener?.onFailed(throwable) })
            compositeDisposable.add(disposable)
        } catch (e: Throwable) {
            logE("Error occurs when load Hitokoto", e)
        }
    }

    private fun loadOnePoem(oneSentenceLoadListener: OneSentenceLoadListener?) {
        try {
            // val onePoemCategories = XSPUtils.getOnePoemCategories(xsp)
            val onePoemCategories = XPrefContainer.onePoemCategories
            val onePoemCategory = if (onePoemCategories.isNullOrEmpty()) {
                PrefConst.ONE_POEM_CATEGORY_ALL
            } else {
                if (onePoemCategories.contains(PrefConst.ONE_POEM_CATEGORY_ALL)) {
                    PrefConst.ONE_POEM_CATEGORY_ALL
                } else {
                    val randIdx = Random().nextInt(onePoemCategories.size)
                    ArrayList(onePoemCategories)[randIdx]
                }
            }

            // val showPoemAuthor = XSPUtils.getShowPoemAuthor(xsp)
            val showPoemAuthor = XPrefContainer.showPoemAuthor
            val disposable = DataRepository.getPoem(onePoemCategory)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ poem ->
                        poem?.let {
                            logD(poem.toString())
                            val content = poem.content ?: ""
                            val oneSentence: String = if (showPoemAuthor) {
                                val author = poem.author ?: ""
                                String.format("%s  %s", content, author)
                            } else {
                                content
                            }
                            oneSentenceLoadListener?.onSuccess(oneSentence)
                        }
                    }, { throwable -> logE("Error occurs", throwable) })
            compositeDisposable.add(disposable)
        } catch (e: Throwable) {
            logE("Error occurs when load OnePoem", e)
        }
    }

    fun cancelLoadOneSentence() {
        if (compositeDisposable.size() > 0) {
            compositeDisposable.clear()
        }
    }

    interface OneSentenceLoadListener {
        fun onSuccess(oneSentence: String)
        fun onFailed(throwable: Throwable)
    }


}