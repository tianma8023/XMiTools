package com.tianma.tweaks.miui.utils

import com.jaredrummler.android.shell.Shell
import com.tianma.tweaks.miui.utils.RootUtils
import com.tianma.tweaks.miui.xp.hook.systemui.SystemUIHook
import com.tianma.tweaks.miui.xp.hook.launcher.MiuiLauncherHook

/**
 * Utils for root action
 */
object RootUtils {
    /**
     * Restart SystemUI
     */
    fun restartSystemUI() {
        killAll(SystemUIHook.PACKAGE_NAME)
    }

    /**
     * Kill Miui Launcher
     */
    fun killAllMiuiLauncher() {
        killAll(MiuiLauncherHook.PACKAGE_NAME)
    }

    /**
     * killall <process name>
     *
     * @param processName process name
    </process> */
    private fun killAll(processName: String) {
        val cmd = String.format("killall %s", processName)
        Shell.SU.run(cmd)
    }

    /**
     * Reboot
     */
    fun reboot() {
        Shell.SU.run("reboot")
    }

    /**
     * Soft Reboot
     */
    fun softReboot() {
        Shell.SU.run("setprop ctl.restart surfaceflinger; setprop ctl.restart zygote")
    }
}