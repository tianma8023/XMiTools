package com.tianma.tweaks.miui.utils;

import com.jaredrummler.android.shell.Shell;
import com.tianma.tweaks.miui.xp.hook.launcher.MiuiLauncherHook;
import com.tianma.tweaks.miui.xp.hook.systemui.SystemUIHook;

/**
 * Utils for root action
 */
public class RootUtils {

    private RootUtils() {

    }

    /**
     * Restart SystemUI
     */
    public static void restartSystemUI() {
        killAll(SystemUIHook.PACKAGE_NAME);
    }

    /**
     * Kill Miui Launcher
     */
    public static void killAllMiuiLauncher() {
        killAll(MiuiLauncherHook.PACKAGE_NAME);
    }

    /**
     * killall <process name>
     *
     * @param processName process name
     */
    private static void killAll(String processName) {
        String cmd = String.format("killall %s", processName);
        Shell.SU.run(cmd);
    }

    /**
     * Reboot
     */
    public static void reboot() {
        Shell.SU.run("reboot");
    }

    /**
     * Soft Reboot
     */
    public static void softReboot() {
        Shell.SU.run("setprop ctl.restart surfaceflinger; setprop ctl.restart zygote");
    }

}
