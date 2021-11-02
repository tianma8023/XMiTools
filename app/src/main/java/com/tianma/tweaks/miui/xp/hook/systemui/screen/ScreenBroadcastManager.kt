package com.tianma.tweaks.miui.xp.hook.systemui.screen

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.tianma.tweaks.miui.xp.hook.systemui.tick.TimeTicker
import java.lang.ref.WeakReference
import java.util.*

/**
 * Screen Broadcast Manager
 */
class ScreenBroadcastManager private constructor(context: Context) {

    private val listeners: MutableList<ScreenListener> = ArrayList()
    private val contextWeakReference: WeakReference<Context> =
        WeakReference(context.applicationContext)

    companion object {
        @Volatile
        private var sInstance: ScreenBroadcastManager? = null

        @JvmStatic
        fun getInstance(modContext: Context): ScreenBroadcastManager {
            return sInstance ?: synchronized(TimeTicker::class.java) {
                sInstance ?: ScreenBroadcastManager(modContext).also {
                    sInstance = it
                }
            }
        }
    }

    @Synchronized
    fun registerListener(screenListener: ScreenListener) {
        if (listeners.isEmpty()) {
            registerBroadcastReceiver()
        }
        if (!listeners.contains(screenListener)) {
            listeners.add(screenListener)
        }
    }

    @Synchronized
    fun unregisterListener(screenListener: ScreenListener) {
        listeners.remove(screenListener)
        if (listeners.isEmpty()) {
            unregisterBroadcastReceiver()
        }
    }

    // register broadcast receiver for screen on, off, user-present, stop-time-tick
    private fun registerBroadcastReceiver() {
        val context = contextWeakReference.get()
        if (context != null) {
            // register receiver
            val filter = IntentFilter()
            filter.addAction(Intent.ACTION_SCREEN_ON)
            filter.addAction(Intent.ACTION_USER_PRESENT)
            filter.addAction(Intent.ACTION_SCREEN_OFF)
            filter.addAction(IntentAction.KEYGUARD_STOP_TIME_TICK)
            context.registerReceiver(screenReceiver, filter)
        }
    }

    // unregister broadcast receiver
    private fun unregisterBroadcastReceiver() {
        val context = contextWeakReference.get()
        context?.unregisterReceiver(screenReceiver)
    }

    // screen receiver
    private val screenReceiver: BroadcastReceiver by lazy {
        object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val action = intent.action
                notifyListeners(action)
            }
        }
    }

    // notify screen event to all listeners (ScreenListener)
    private fun notifyListeners(action: String?) {
        val screenOn = Intent.ACTION_SCREEN_ON == action
        val userPresent = Intent.ACTION_USER_PRESENT == action
        val screenOff = Intent.ACTION_SCREEN_OFF == action
        val stopTimeTick = IntentAction.KEYGUARD_STOP_TIME_TICK == action

        for (listener in listeners) {
            when {
                screenOn -> {
                    listener.onScreenOn()
                }
                userPresent -> {
                    listener.onUserPresent()
                }
                screenOff -> {
                    listener.onScreenOff()
                }
                stopTimeTick -> {
                    listener.onStopTimeTick()
                }
            }
        }
    }

}