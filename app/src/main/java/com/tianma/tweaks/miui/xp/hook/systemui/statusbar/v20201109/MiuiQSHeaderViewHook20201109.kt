package com.tianma.tweaks.miui.xp.hook.systemui.statusbar.v20201109

import android.content.Context
import android.content.pm.PackageManager
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import com.tianma.tweaks.miui.BuildConfig
import com.tianma.tweaks.miui.data.sp.XPrefContainer
import com.tianma.tweaks.miui.utils.ResolutionUtils
import com.tianma.tweaks.miui.utils.logD
import com.tianma.tweaks.miui.xp.hook.BaseSubHook
import com.tianma.tweaks.miui.xp.hook.systemui.helper.ResHelpers
import com.tianma.tweaks.miui.xp.hook.systemui.weather.WeatherMonitor
import com.tianma.tweaks.miui.xp.hook.systemui.weather.WeatherObserver
import com.tianma.tweaks.miui.xp.utils.appinfo.AppInfo
import com.tianma.tweaks.miui.xp.wrapper.MethodHookWrapper
import com.tianma.tweaks.miui.xp.wrapper.XposedWrapper

/**
 * 下拉状态栏头部View Hook（下拉状态栏显示天气等)，适用版本 MiuiSystemUI(versionCode>=202011090)
 */
class MiuiQSHeaderViewHook20201109(classLoader: ClassLoader?, appInfo: AppInfo?) :
    BaseSubHook(classLoader, appInfo), WeatherObserver {

    private var mHeaderViewClass: Class<*>? = null

    private var mModContext: Context? = null
    private var mAppContext: Context? = null

    private var mWeatherInfoTv: TextView? = null

    private val mWeatherEnabled: Boolean = XPrefContainer.isDropdownStatusBarWeatherEnabled
    private var mWeatherTextColor = 0
    private var mWeatherTextSize = 0f

    companion object {
        private const val CLASS_HEADER_VIEW = "com.android.systemui.qs.MiuiQSHeaderView"
    }

    init {
        if (mWeatherEnabled) {
            mWeatherTextColor = XPrefContainer.dropdownStatusBarWeatherTextColor
            mWeatherTextSize = XPrefContainer.getDropdownStatusBarWeatherTextSize()
        }
    }

    override fun startHook() {
        logD("Hooking MiuiQSHeaderView...")
        if (mWeatherEnabled) {
            mHeaderViewClass = XposedWrapper.findClass(CLASS_HEADER_VIEW, mClassLoader)
            if (mHeaderViewClass != null) {
                hookConstructor()
                hookOnFinishInflate()
                hookOnAttachedToWindow()
                hookOnDetachedFromWindow()
            }
        }
    }

    // HeaderView#constructor()
    private fun hookConstructor() {
        XposedWrapper.hookAllConstructors(mHeaderViewClass,
            object : MethodHookWrapper() {
                override fun after(param: MethodHookParam) {
                    if (mAppContext == null) {
                        val headerView = param.thisObject as View
                        mModContext = headerView.context.applicationContext
                        try {
                            mAppContext = mModContext?.createPackageContext(
                                BuildConfig.APPLICATION_ID,
                                Context.CONTEXT_IGNORE_SECURITY
                            )
                        } catch (e: PackageManager.NameNotFoundException) {
                            // ignore
                        }
                    }
                }
            })
    }

    // HeaderView#onFinishInflate()
    private fun hookOnFinishInflate() {
        XposedWrapper.findAndHookMethod(mHeaderViewClass,
            "onFinishInflate",
            object : MethodHookWrapper() {
                override fun after(param: MethodHookParam) {
                    // MiuiQSHeaderView extends RelativeLayout
                    val miuiQSHeaderView = param.thisObject as ViewGroup

                    // 右上角齿轮快捷按钮
                    val context = miuiQSHeaderView.context
                    val shortcutViewId =
                        ResHelpers.getId(context.resources, "notification_shade_shortcut")
                    val shortcutView = miuiQSHeaderView.findViewById<View>(shortcutViewId)

                    val weatherInfoLp = RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT
                    ).also {
                        it.addRule(RelativeLayout.START_OF, shortcutView.id)
                        it.addRule(RelativeLayout.ALIGN_TOP, shortcutView.id)
                        it.addRule(RelativeLayout.ALIGN_BOTTOM, shortcutView.id)
                        it.marginEnd =
                            ResolutionUtils.dp2px(miuiQSHeaderView.context, 3f).toInt()
                    }

                    mWeatherInfoTv = TextView(miuiQSHeaderView.context).also {
                        it.includeFontPadding = false
                        it.gravity = Gravity.CENTER
                        it.setTextColor(mWeatherTextColor)
                        it.textSize = mWeatherTextSize
                        it.layoutParams = weatherInfoLp
                    }

                    miuiQSHeaderView.addView(mWeatherInfoTv)
                }
            })
    }

    // HeaderView#onAttachedToWindow()
    private fun hookOnAttachedToWindow() {
        XposedWrapper.findAndHookMethod(mHeaderViewClass,
            "onAttachedToWindow",
            object : MethodHookWrapper() {
                override fun after(param: MethodHookParam) {
                    val context = mAppContext ?: return
                    WeatherMonitor.get(context)
                        .registerObserver(this@MiuiQSHeaderViewHook20201109)
                }
            })
    }

    // HeaderView#onDetachedFromWindow()
    private fun hookOnDetachedFromWindow() {
        XposedWrapper.findAndHookMethod(mHeaderViewClass,
            "onDetachedFromWindow",
            object : MethodHookWrapper() {
                override fun after(param: MethodHookParam) {
                    val context = mAppContext ?: return
                    WeatherMonitor.get(context)
                        .unregisterObserver(this@MiuiQSHeaderViewHook20201109)
                }
            })
    }

    override fun onWeatherChanged(newWeatherInfo: String) {
        logD("onWeatherChanged: %s", newWeatherInfo)
        mWeatherInfoTv?.text = newWeatherInfo

    }
}