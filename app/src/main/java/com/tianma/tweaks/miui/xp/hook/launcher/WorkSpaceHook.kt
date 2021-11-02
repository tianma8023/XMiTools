package com.tianma.tweaks.miui.xp.hook.launcher

import com.tianma.tweaks.miui.data.sp.XPrefContainer
import com.tianma.tweaks.miui.utils.logD
import com.tianma.tweaks.miui.utils.logE
import com.tianma.tweaks.miui.xp.hook.BaseSubHook
import com.tianma.tweaks.miui.xp.wrapper.MethodHookWrapper
import com.tianma.tweaks.miui.xp.wrapper.XposedWrapper

class WorkSpaceHook(classLoader: ClassLoader?) : BaseSubHook(classLoader) {

    companion object {
        private const val CLASS_WORK_SPACE = "com.miui.home.launcher.Workspace"
    }

    private val mAlwaysShowStatusBarClock: Boolean = XPrefContainer.alwaysShowStatusBarClock

    override fun startHook() {
        try {
            logD("Hooking WorkSpace...")
            if (mAlwaysShowStatusBarClock) {
                hookIsScreenHasClockGadgets()
            }
        } catch (t: Throwable) {
            logE("Error occurs when hook WorkSpace", t)
        }
    }

    // #isScreenHasClockGadget()
    private fun hookIsScreenHasClockGadgets() {
        XposedWrapper.findAndHookMethod(
            CLASS_WORK_SPACE,
            mClassLoader,
            "isScreenHasClockGadget",
            Long::class.javaPrimitiveType,
            object : MethodHookWrapper() {
                override fun before(param: MethodHookParam) {
                    param.result = false
                }
            })
    }
}