package com.tianma.tweaks.miui.app

import android.app.Application

/**
 * desc: Application instance
 * date: 2021/10/7
 */
class App : Application() {

    companion object {
        lateinit var appContext: Application
            private set
    }

    override fun onCreate() {
        super.onCreate()

        appContext = this
    }

}