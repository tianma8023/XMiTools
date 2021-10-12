package com.tianma.tweaks.miui.xp.hook.systemui.keyguard

import android.content.Context
import android.view.View
import android.view.ViewTreeObserver.OnWindowAttachListener
import android.widget.LinearLayout
import android.widget.TextView
import com.tianma.tweaks.miui.R
import com.tianma.tweaks.miui.data.sp.XPrefContainer
import com.tianma.tweaks.miui.utils.ResolutionUtils
import com.tianma.tweaks.miui.utils.XLog
import com.tianma.tweaks.miui.xp.hook.BaseSubHook
import com.tianma.tweaks.miui.xp.hook.systemui.hitokoto.OneSentenceManager
import com.tianma.tweaks.miui.xp.hook.systemui.screen.ScreenBroadcastManager
import com.tianma.tweaks.miui.xp.hook.systemui.screen.SimpleScreenListener
import com.tianma.tweaks.miui.xp.utils.appinfo.AppInfo
import com.tianma.tweaks.miui.xp.wrapper.MethodHookWrapper
import com.tianma.tweaks.miui.xp.wrapper.XposedWrapper
import de.robv.android.xposed.XSharedPreferences
import de.robv.android.xposed.XposedHelpers

/**
 * 锁屏时钟基类 Hook
 * 适用版本 20.4.27+
 */
class MiuiBaseClockHook(classLoader: ClassLoader?, xsp: XSharedPreferences?, appInfo: AppInfo?) : BaseSubHook(classLoader, xsp, appInfo) {

    private var miuiBaseClockClass: Class<*>? = null
    private val clockList = mutableListOf<View>()

    private var modContext: Context? = null

    // private val oneSentenceEnabled = XSPUtils.oneSentenceEnabled(xsp)
    private val oneSentenceEnabled = XPrefContainer.oneSentenceEnabled
    // private val oneSentenceColor = XSPUtils.getOneSentenceColor(xsp)
    private val oneSentenceColor = XPrefContainer.oneSentenceColor
    // private val oneSentenceTextSize = XSPUtils.getOneSentenceTextSize(xsp)
    private val oneSentenceTextSize = XPrefContainer.getOneSentenceTextSize()

    companion object {
        private const val CLASS_MIUI_BASE_CLOCK = "miui.keyguard.clock.MiuiBaseClock"
    }

    override fun startHook() {
        miuiBaseClockClass = XposedWrapper.findClass(CLASS_MIUI_BASE_CLOCK, mClassLoader)
        if (oneSentenceEnabled) {
            XLog.d("OneSentence enabled, hooking MiuiBaseClock...")
            hookOnFinishInflate()
            hookOnAttachedToWindow()
        }
    }

    private fun hookOnFinishInflate() {
        XposedWrapper.findAndHookMethod(miuiBaseClockClass,
                "onFinishInflate",
                object : MethodHookWrapper() {
                    override fun after(param: MethodHookParam) {
                        val miuiBaseClock = param.thisObject as LinearLayout
                        val context = miuiBaseClock.context

                        val mOwnerInfo = XposedHelpers.getObjectField(miuiBaseClock, "mOwnerInfo") as TextView
                        val mOwnerInfoIndex = miuiBaseClock.indexOfChild(mOwnerInfo)

                        val hitokotoTextView = TextView(context)
                        hitokotoTextView.textSize = oneSentenceTextSize
                        hitokotoTextView.setTextColor(oneSentenceColor)
                        hitokotoTextView.id = R.id.hitokoto_info_text_view

                        val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                        hitokotoTextView.layoutParams = layoutParams
                        // params.gravity = Gravity.CENTER
                        layoutParams.topMargin = ResolutionUtils.dp2px(context, 5.5f).toInt()

                        when (val className = param.thisObject.javaClass.name) {
                            MiuiCenterHorizontalClockHook.CLASS_MIUI_CENTER_HORIZONTAL_CLOCK -> {
                                layoutParams.leftMargin = ResolutionUtils.dp2px(context, 10f).toInt()
                                layoutParams.rightMargin = ResolutionUtils.dp2px(context, 10f).toInt()
                            }
                            MiuiVerticalClockHook.CLASS_MIUI_VERTICAL_CLOCK -> {
                                layoutParams.leftMargin = ResolutionUtils.dp2px(context, 10f).toInt()
                                layoutParams.rightMargin = ResolutionUtils.dp2px(context, 10f).toInt()
                            }
                            MiuiLeftToplClockHook.CLASS_MIUI_LEFT_TOP_CLOCK -> {
                                layoutParams.leftMargin = ResolutionUtils.dp2px(context, 2f).toInt()
                                layoutParams.rightMargin = ResolutionUtils.dp2px(context, 20f).toInt()
                            }
                            MiuiLeftToplLargeClockHook.CLASS_MIUI_LEFT_TOP_LARGE_CLOCK -> {
                                layoutParams.leftMargin = ResolutionUtils.dp2px(context, 2f).toInt()
                                layoutParams.rightMargin = ResolutionUtils.dp2px(context, 20f).toInt()
                            }
                            else -> {
                                XLog.d("Unknown subclass of MiuiBaseClock: $className")
                            }
                        }

                        miuiBaseClock.addView(hitokotoTextView, mOwnerInfoIndex + 1)
                    }
                })
    }

    private fun hookOnAttachedToWindow() {
        XposedWrapper.findAndHookMethod(miuiBaseClockClass,
                "onAttachedToWindow",
                object : MethodHookWrapper() {
                    override fun after(param: MethodHookParam) {
                        val keyguardClock = param.thisObject as LinearLayout
                        modContext = keyguardClock.context.applicationContext
                        keyguardClock.viewTreeObserver.addOnWindowAttachListener(object : OnWindowAttachListener {
                            override fun onWindowAttached() {
                                addClock(keyguardClock)
                            }

                            override fun onWindowDetached() {
                                removeClock(keyguardClock)
                            }
                        })
                        addClock(keyguardClock)
                        ScreenBroadcastManager.getInstance(keyguardClock.context).registerListener(screenListener)
                    }
                })
    }

    @Synchronized
    private fun addClock(clock: View) {
        if (!clockList.contains(clock)) {
            clockList.add(clock)
            val size = clockList.size
            val limitedSize = 2
            if (size > limitedSize) {
                for (i in 0 until size - limitedSize) {
                    val item = clockList[i]
                    clockList.remove(item)
                }
            }
        }
    }

    @Synchronized
    private fun removeClock(clock: View) {
        clockList.remove(clock)
    }

    private val screenListener: SimpleScreenListener = object : SimpleScreenListener() {
        override fun onScreenOn() {
            loadOneSentence()
        }

        override fun onScreenOff() {
            cancelLoadOneSentence()
        }

        override fun onUserPresent() {
            cancelLoadOneSentence()
        }
    }

    private fun loadOneSentence() {
        modContext?.let {
            OneSentenceManager.getInstance().loadOneSentence(it, xsp, object : OneSentenceManager.OneSentenceLoadListener {
                override fun onSuccess(oneSentence: String) {
                    showOneSentence(oneSentence)
                }

                override fun onFailed(throwable: Throwable) {
                    XLog.e("Error occurs when load OneSentence", throwable)
                }
            })
        }
    }


    private fun showOneSentence(oneSentence: String) {
        for (keyguardClock in clockList) {
            try {
                val hitokotoInfo = keyguardClock.findViewById<TextView>(R.id.hitokoto_info_text_view)
                hitokotoInfo.text = oneSentence
            } catch (t: Throwable) {
                // ignore
                XLog.e("", t)
            }
        }
    }

    private fun cancelLoadOneSentence() {
        OneSentenceManager.getInstance().cancelLoadOneSentence()
    }
}