package com.tianma.tweaks.miui.xp.hook.launcher;

import static com.tianma.tweaks.miui.xp.wrapper.XposedWrapper.findAndHookMethod;

import com.tianma.tweaks.miui.data.sp.XPrefContainer;
import com.tianma.tweaks.miui.utils.XLog;
import com.tianma.tweaks.miui.xp.hook.BaseSubHook;
import com.tianma.tweaks.miui.xp.wrapper.MethodHookWrapper;

import de.robv.android.xposed.XSharedPreferences;

public class WorkSpaceHook extends BaseSubHook {

    private static final String CLASS_WORK_SPACE = "com.miui.home.launcher.Workspace";

    private boolean mAlwaysShowStatusBarClock;

    public WorkSpaceHook(ClassLoader classLoader, XSharedPreferences xsp) {
        super(classLoader, xsp);

        // mAlwaysShowStatusBarClock = XSPUtils.alwaysShowStatusBarClock(xsp);
        mAlwaysShowStatusBarClock = XPrefContainer.getAlwaysShowStatusBarClock();
    }

    @Override
    public void startHook() {
        try {
            XLog.d("Hooking WorkSpace...");
            if (mAlwaysShowStatusBarClock) {
                hookIsScreenHasClockGadgets();
            }
        } catch (Throwable t) {
            XLog.e("Error occurs when hook WorkSpace", t);
        }
    }

    // #isScreenHasClockGadget()
    private void hookIsScreenHasClockGadgets() {
        findAndHookMethod(CLASS_WORK_SPACE,
                mClassLoader,
                "isScreenHasClockGadget",
                long.class,
                new MethodHookWrapper() {
                    @Override
                    protected void before(MethodHookParam param) {
                        param.setResult(false);
                    }
                });
    }
}
