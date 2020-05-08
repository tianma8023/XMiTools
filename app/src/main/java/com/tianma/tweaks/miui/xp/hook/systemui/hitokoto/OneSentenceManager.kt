package com.tianma.tweaks.miui.xp.hook.systemui.hitokoto

import android.content.Context
import android.os.SystemClock
import com.tianma.tweaks.miui.cons.PrefConst
import com.tianma.tweaks.miui.data.http.repository.DataRepository
import com.tianma.tweaks.miui.utils.SPUtils
import com.tianma.tweaks.miui.utils.XLog
import com.tianma.tweaks.miui.utils.XSPUtils
import de.robv.android.xposed.XSharedPreferences
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

    fun loadOneSentence(modContext: Context, xsp: XSharedPreferences, oneSentenceLoadListener: OneSentenceLoadListener?) {
        try {
            val apiSources = XSPUtils.getOneSentenceApiSources(xsp)
            if (apiSources == null || apiSources.isEmpty()) {
                XLog.e("No OneSentence API chosen")
                return
            }

            // 判断是否满足刷新频率
            val duration = XSPUtils.getOneSentenceRefreshRate(xsp) * 60 * 1000
            if (duration > 0) {
                // 因为是 modContext, 所以上次刷新时间数据的存取都是在 com.android.systemui 的 shared_prefs 文件中
                val lastTime = SPUtils.getOneSentenceLastRefreshTime(modContext)
                val curTime = SystemClock.elapsedRealtime()
                if (curTime - lastTime < duration) {
                    XLog.d("Cannot fetch new data due to refresh rate")
                    return
                }
                SPUtils.setOneSentenceLastRefreshTime(modContext, curTime)
            }

            val randIdx = Random().nextInt(apiSources.size)
            when (val apiSource = ArrayList(apiSources)[randIdx]) {
                PrefConst.API_SOURCE_HITOKOTO -> {
                    loadHitokoto(xsp, oneSentenceLoadListener)
                }
                PrefConst.API_SOURCE_ONE_POEM -> {
                    loadOnePoem(xsp, oneSentenceLoadListener)
                }
                else -> {
                    XLog.e("Unknown API source: $apiSource")
                }
            }
        } catch (e: Exception) {
            XLog.e("Error occurs when load OneSentence", e)
        }
    }

    private fun loadHitokoto(xsp: XSharedPreferences, oneSentenceLoadListener: OneSentenceLoadListener?) {
        try {
            val hitokotoCategories = XSPUtils.getHitokotoCategories(xsp)
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

            val showHitokotoSource = XSPUtils.getShowHitokotoSource(xsp)
            val disposable = DataRepository.getHitokoto(params)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ hitokoto ->
                        hitokoto?.let {
                            XLog.d(hitokoto.toString())
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
            XLog.e("Error occurs when load Hitokoto", e)
        }
    }

    private fun loadOnePoem(xsp: XSharedPreferences, oneSentenceLoadListener: OneSentenceLoadListener?) {
        try {
            val onePoemCategories = XSPUtils.getOnePoemCategories(xsp)
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

            val showPoemAuthor = XSPUtils.getShowPoemAuthor(xsp)
            val disposable = DataRepository.getPoem(onePoemCategory)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ poem ->
                        poem?.let {
                            XLog.d(poem.toString())
                            val content = if (poem.content == null) "" else poem.content
                            val oneSentence: String
                            oneSentence = if (showPoemAuthor) {
                                val author = if (poem.author == null) "" else poem.author
                                String.format("%s  %s", content, author)
                            } else {
                                content
                            }
                            oneSentenceLoadListener?.onSuccess(oneSentence)
                        }
                    }, { throwable -> XLog.e("Error occurs", throwable) })
            compositeDisposable.add(disposable)
        } catch (e: Throwable) {
            XLog.e("Error occurs when load OnePoem", e)
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