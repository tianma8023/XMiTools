package com.tianma.tweaks.miui.xp.hook.systemui.keyguard;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.os.UserHandle;
import android.view.ViewParent;
import android.widget.FrameLayout;

import com.tianma.tweaks.miui.utils.XLog;
import com.tianma.tweaks.miui.utils.XSPUtils;
import com.tianma.tweaks.miui.xp.hook.BaseSubHook;
import com.tianma.tweaks.miui.xp.utils.appinfo.AppInfo;
import com.tianma.tweaks.miui.xp.utils.appinfo.AppVersionConst;
import com.tianma.tweaks.miui.xp.wrapper.MethodHookWrapper;

import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedHelpers;

import static com.tianma.tweaks.miui.xp.wrapper.XposedWrapper.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.findClass;
import static de.robv.android.xposed.XposedHelpers.getObjectField;
import static de.robv.android.xposed.XposedHelpers.getStaticObjectField;

/**
 * 锁屏时钟容器 Hook
 * 适用版本 9.4.x
 */
public class KeyguardClockContainerHook extends BaseSubHook {

    private static final String CLASS_KEYGUARD_CLOCK_CONTAINER_OLD = "com.android.keyguard.KeyguardClockContainer";
    private static final String CLASS_KEYGUARD_CLOCK_CONTAINER_NEW = "com.android.keyguard.clock.KeyguardClockContainer";
    private static final String CLASS_DEPENDENCY = "com.android.systemui.Dependency";

    private boolean mShowHorizontalSec;
    private boolean mShowVerticalSec;

    private Class<?> mKeyguardClockContainerClass;

    public KeyguardClockContainerHook(ClassLoader classLoader, XSharedPreferences xsp, AppInfo appInfo) {
        super(classLoader, xsp, appInfo);

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
            if (mAppInfo.getVersionCode() >= AppVersionConst.SYSTEM_UI_V201912130) {
                mKeyguardClockContainerClass = findClass(CLASS_KEYGUARD_CLOCK_CONTAINER_NEW, mClassLoader);
            } else {
                mKeyguardClockContainerClass = findClass(CLASS_KEYGUARD_CLOCK_CONTAINER_OLD, mClassLoader);
            }

            hookOnAttachedToWindow();
        } catch (Throwable t) {
            XLog.e("Error occurs when hook KeyguardClockContainerHook", t);
        }
    }

    // com.android.keyguard.KeyguardClockContainer#onAttachedToWindow()
    private void hookOnAttachedToWindow() {
        findAndHookMethod(mKeyguardClockContainerClass,
                "onAttachedToWindow",
                new MethodHookWrapper() {
                    @Override
                    protected void before(MethodHookParam param) {
                        FrameLayout clockContainer = (FrameLayout) param.thisObject;
                        ViewParent parent = clockContainer.getParent();
                        XposedHelpers.callMethod(parent, "onAttachedToWindow");

                        IntentFilter filter = new IntentFilter();
                        // filter.addAction("android.intent.action.TIME_TICK");
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
                    }
                });
    }

}
