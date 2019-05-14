package com.tianma.tweaks.miui.xp.hook.systemui.keyguard;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.os.UserHandle;
import android.view.ViewParent;
import android.widget.FrameLayout;

import com.tianma.tweaks.miui.utils.XLog;
import com.tianma.tweaks.miui.utils.XSPUtils;
import com.tianma.tweaks.miui.xp.hook.BaseSubHook;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedHelpers;

import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.findClass;
import static de.robv.android.xposed.XposedHelpers.getObjectField;
import static de.robv.android.xposed.XposedHelpers.getStaticObjectField;

/**
 * 锁屏时钟容器 Hook
 * 适用版本 9.4.x
 */
public class KeyguardClockContainerHook extends BaseSubHook {

    private static final String CLASS_KEYGUARD_CLOCK_CONTAINER = "com.android.keyguard.KeyguardClockContainer";
    private static final String CLASS_DEPENDENCY = "com.android.systemui.Dependency";

    private boolean mShowHorizontalSec;
    private boolean mShowVerticalSec;

    private Class<?> mKeyguardClockContainerClass;

    public KeyguardClockContainerHook(ClassLoader classLoader, XSharedPreferences xsp) {
        super(classLoader, xsp);

        mShowHorizontalSec = XSPUtils.showSecInKeyguardHorizontal(xsp);
        mShowVerticalSec = XSPUtils.showSecInKeyguardVertical(xsp);
    }

    @Override
    public void startHook() {
        if (!mShowHorizontalSec && !mShowVerticalSec) {
            return;
        }
        try {
            XLog.d("Hooking KeyguardClockContainerHook...");
            mKeyguardClockContainerClass = findClass(CLASS_KEYGUARD_CLOCK_CONTAINER, mClassLoader);

            hookOnAttachedToWindow();
        } catch (Throwable t) {
            XLog.e("Error occurs when hook KeyguardClockContainerHook", t);
        }
    }

    // com.android.keyguard.KeyguardClockContainer#onAttachedToWindow()
    private void hookOnAttachedToWindow() {
        findAndHookMethod(mKeyguardClockContainerClass,
                "onAttachedToWindow",
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        try {
                            FrameLayout clockContainer = (FrameLayout) param.thisObject;
                            ViewParent parent = clockContainer.getParent();
                            XposedHelpers.callMethod(parent, "onAttachedToWindow");

                            IntentFilter filter = new IntentFilter();
//                            filter.addAction("android.intent.action.TIME_TICK");
                            filter.addAction("android.intent.action.TIME_SET");
                            filter.addAction("android.intent.action.TIMEZONE_CHANGED");

                            BroadcastReceiver mIntentReceiver = (BroadcastReceiver) getObjectField(clockContainer, "mIntentReceiver");
                            Object USER_HANDLE_ALL = getStaticObjectField(UserHandle.class, "ALL");
                            Class<?> dependencyClass = findClass(CLASS_DEPENDENCY, mClassLoader);
                            Object TIME_TICK_HANDLER = getStaticObjectField(dependencyClass, "TIME_TICK_HANDLER");
                            Object handler = XposedHelpers.callStaticMethod(dependencyClass, "get", TIME_TICK_HANDLER);

                            callMethod(clockContainer.getContext(),
                                    "registerReceiverAsUser",
                                    mIntentReceiver,
                                    USER_HANDLE_ALL,
                                    filter,
                                    null,
                                    handler);

                            callMethod(clockContainer, "registerDualClockObserver");
                            callMethod(clockContainer, "registerClockPositionObserver");

                            param.setResult(null);
                        } catch (Throwable t) {
                            XLog.e("", t);
                        }
                    }
                });
    }

}
