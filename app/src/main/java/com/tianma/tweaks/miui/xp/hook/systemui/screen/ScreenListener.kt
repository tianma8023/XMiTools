package com.tianma.tweaks.miui.xp.hook.systemui.screen

/**
 * Screen Listener
 */
interface ScreenListener {
    /**
     * called when screen on
     */
    fun onScreenOn()

    /**
     * called when screen off
     */
    fun onScreenOff()

    /**
     * called when user present
     */
    fun onUserPresent()

    /**
     * called when stop time tick
     */
    fun onStopTimeTick()
}