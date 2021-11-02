package com.tianma.tweaks.miui.xp.hook.systemui.keyguard.v20191213

import android.view.View
import android.view.ViewTreeObserver.OnWindowAttachListener
import android.widget.TextView
import com.tianma.tweaks.miui.data.sp.XPrefContainer
import com.tianma.tweaks.miui.utils.logD
import com.tianma.tweaks.miui.xp.hook.BaseSubHook
import com.tianma.tweaks.miui.xp.hook.systemui.screen.ScreenBroadcastManager
import com.tianma.tweaks.miui.xp.hook.systemui.screen.SimpleScreenListener
import com.tianma.tweaks.miui.xp.hook.systemui.tick.TickObserver
import com.tianma.tweaks.miui.xp.hook.systemui.tick.TimeTicker
import com.tianma.tweaks.miui.xp.utils.appinfo.AppInfo
import com.tianma.tweaks.miui.xp.wrapper.MethodHookWrapper
import com.tianma.tweaks.miui.xp.wrapper.XposedWrapper
import de.robv.android.xposed.XposedHelpers
import java.util.*

/**
 * 锁屏左上角大时钟
 * 适用版本 20.4.27+
 */
class MiuiLeftTopLargeClockHook(classLoader: ClassLoader?, appInfo: AppInfo?) : BaseSubHook(classLoader, appInfo), TickObserver {

    companion object {
        const val CLASS_MIUI_LEFT_TOP_LARGE_CLOCK = "miui.keyguard.clock.MiuiLeftTopLargeClock"
    }

    private var leftTopLargeClockClass: Class<*>? = null

    private val clockList = mutableListOf<View?>()

    // private val showHorizontalSec = XSPUtils.showSecInKeyguardHorizontal(xsp)
    private val showHorizontalSec = XPrefContainer.showSecInKeyguardHorizontal

    override fun startHook() {
        if (showHorizontalSec) {
            logD("Hooking MiuiLeftTopLargeClock...")
            leftTopLargeClockClass = XposedWrapper.findClass(CLASS_MIUI_LEFT_TOP_LARGE_CLOCK, mClassLoader)

            leftTopLargeClockClass?.let {
                hookConstructor()
                hookUpdateTime()
            }
        }
    }

    private fun hookConstructor() {
        XposedWrapper.hookAllConstructors(leftTopLargeClockClass,
                object : MethodHookWrapper() {
                    override fun after(param: MethodHookParam?) {
                        param?.let {
                            val miuiBaseClock = it.thisObject as View
                            miuiBaseClock.viewTreeObserver.addOnWindowAttachListener(object : OnWindowAttachListener {
                                override fun onWindowAttached() {
                                    addClock(miuiBaseClock)
                                }

                                override fun onWindowDetached() {
                                    removeClock(miuiBaseClock)
                                }
                            })

                            addClock(miuiBaseClock)

                            ScreenBroadcastManager.getInstance(miuiBaseClock.context).registerListener(screenListener)
                        }
                    }
                })
    }

    @Synchronized
    private fun addClock(clock: View) {
        if (!clockList.contains(clock)) {
            clockList.add(clock)
            val size: Int = clockList.size
            val limitedSize = 2
            if (size > limitedSize) {
                for (i in 0 until size - limitedSize) {
                    val item: View? = clockList[i]
                    clockList.remove(item)
                }
            }
        }
        if (clockList.isNotEmpty()) {
            TimeTicker.get().registerObserver(this)
        }
    }

    @Synchronized
    private fun removeClock(clock: View) {
        clockList.remove(clock)
        if (clockList.isEmpty()) {
            TimeTicker.get().unregisterObserver(this)
        }
    }

    private val screenListener: SimpleScreenListener = object : SimpleScreenListener() {
        override fun onScreenOn() {
            TimeTicker.get().registerObserver(this@MiuiLeftTopLargeClockHook)
        }

        override fun onScreenOff() {
            TimeTicker.get().unregisterObserver(this@MiuiLeftTopLargeClockHook)
        }

        override fun onUserPresent() {
            TimeTicker.get().unregisterObserver(this@MiuiLeftTopLargeClockHook)
        }

        override fun onStopTimeTick() {
            TimeTicker.get().unregisterObserver(this@MiuiLeftTopLargeClockHook)
        }
    }

    override fun onTimeTick() {
        for (keyguardClock in clockList) {
            if (keyguardClock != null) {
                XposedHelpers.callMethod(keyguardClock, "updateTime")
            }
        }
    }

    private fun hookUpdateTime() {
        XposedWrapper.findAndHookMethod(leftTopLargeClockClass,
                "updateTime",
                object : MethodHookWrapper() {
                    override fun after(param: MethodHookParam?) {
                        param?.let {
                            val mTimeText = XposedHelpers.getObjectField(param.thisObject, "mTimeText") as TextView
                            val originalTimeStr = mTimeText.text.toString()
                            mTimeText.text = addInSecond(originalTimeStr)
                        }
                    }
                })
    }

    private fun addInSecond(originalTimeStr: String): String? {
        val sec = Calendar.getInstance()[Calendar.SECOND]
        val secStr = String.format(Locale.getDefault(), "%02d", sec)
        return originalTimeStr.replace("(\\d+:\\d+)(:\\d+)?".toRegex(), "$1:$secStr")
    }

}