package com.tianma.tweaks.miui.xp.hook.systemui.keyguard

import android.view.View
import android.view.ViewTreeObserver.OnWindowAttachListener
import android.widget.TextView
import com.tianma.tweaks.miui.utils.XLog
import com.tianma.tweaks.miui.utils.XSPUtils
import com.tianma.tweaks.miui.xp.hook.BaseSubHook
import com.tianma.tweaks.miui.xp.hook.systemui.screen.ScreenBroadcastManager
import com.tianma.tweaks.miui.xp.hook.systemui.screen.SimpleScreenListener
import com.tianma.tweaks.miui.xp.hook.systemui.tick.TickObserver
import com.tianma.tweaks.miui.xp.hook.systemui.tick.TimeTicker
import com.tianma.tweaks.miui.xp.utils.appinfo.AppInfo
import com.tianma.tweaks.miui.xp.wrapper.MethodHookWrapper
import com.tianma.tweaks.miui.xp.wrapper.XposedWrapper
import de.robv.android.xposed.XSharedPreferences
import de.robv.android.xposed.XposedHelpers
import java.util.*

/**
 * 锁屏居中水平时钟
 * 适用版本 20.4.27+
 */
class MiuiCenterHorizontalClockHook(classLoader: ClassLoader?, xsp: XSharedPreferences?, appInfo: AppInfo?) : BaseSubHook(classLoader, xsp, appInfo), TickObserver {

    companion object {
        const val CLASS_MIUI_CENTER_HORIZONTAL_CLOCK = "miui.keyguard.clock.MiuiCenterHorizontalClock"
    }

    private var centerHorizontalClockClass: Class<*>? = null

    private val clockList = mutableListOf<View?>()

    private val showHorizontalSec = XSPUtils.showSecInKeyguardHorizontal(xsp)

    override fun startHook() {
        if (showHorizontalSec) {
            XLog.d("Hooking MiuiCenterHorizontalClock...")
            centerHorizontalClockClass = XposedWrapper.findClass(CLASS_MIUI_CENTER_HORIZONTAL_CLOCK, mClassLoader)

            centerHorizontalClockClass?.let {
                hookConstructor()
                hookUpdateTime()
            }
        }
    }

    private fun hookConstructor() {
        XposedWrapper.hookAllConstructors(centerHorizontalClockClass,
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
            TimeTicker.get().registerObserver(this@MiuiCenterHorizontalClockHook)
        }

        override fun onScreenOff() {
            TimeTicker.get().unregisterObserver(this@MiuiCenterHorizontalClockHook)
        }

        override fun onUserPresent() {
            TimeTicker.get().unregisterObserver(this@MiuiCenterHorizontalClockHook)
        }

        override fun onStopTimeTick() {
            TimeTicker.get().unregisterObserver(this@MiuiCenterHorizontalClockHook)
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
        XposedWrapper.findAndHookMethod(centerHorizontalClockClass,
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