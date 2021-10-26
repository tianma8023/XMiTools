package com.tianma.tweaks.miui.xp.hook.systemui.keyguard.v20190507;

import static com.tianma.tweaks.miui.xp.wrapper.XposedWrapper.findAndHookMethod;
import static com.tianma.tweaks.miui.xp.wrapper.XposedWrapper.hookAllConstructors;

import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tianma.tweaks.miui.R;
import com.tianma.tweaks.miui.data.sp.XPrefContainer;
import com.tianma.tweaks.miui.utils.ResolutionUtils;
import com.tianma.tweaks.miui.utils.XLogKt;
import com.tianma.tweaks.miui.xp.hook.BaseSubHook;
import com.tianma.tweaks.miui.xp.hook.systemui.screen.ScreenBroadcastManager;
import com.tianma.tweaks.miui.xp.hook.systemui.screen.SimpleScreenListener;
import com.tianma.tweaks.miui.xp.hook.systemui.tick.TickObserver;
import com.tianma.tweaks.miui.xp.hook.systemui.tick.TimeTicker;
import com.tianma.tweaks.miui.xp.wrapper.MethodHookWrapper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import de.robv.android.xposed.XposedHelpers;

/**
 * 锁屏界面
 * 适用版本 9.5.7+
 * 锁屏左上角时钟 Hook
 */
public class MiuiKeyguardLeftTopClockHook extends BaseSubHook implements TickObserver {

    private static final String CLASS_MIUI_KEYGUARD_LEFT_TOP_CLOCK = "com.android.keyguard.MiuiKeyguardLeftTopClock";

    private Class<?> mMiuiKeyguardLeftTopClockCls;

    private boolean mShowHorizontalSec;
    private boolean mOneSentenceEnabled;

    private List<View> mKeyguardClockList = new ArrayList<>();

    public MiuiKeyguardLeftTopClockHook(ClassLoader classLoader) {
        super(classLoader);
        // mShowHorizontalSec = XSPUtils.showSecInKeyguardHorizontal(xsp);
        mShowHorizontalSec = XPrefContainer.getShowSecInKeyguardHorizontal();
        // mOneSentenceEnabled = XSPUtils.oneSentenceEnabled(xsp);
        mOneSentenceEnabled = XPrefContainer.getOneSentenceEnabled();
    }

    @Override
    public void startHook() {
        try {
            XLogKt.logD("Hooking MiuiKeyguardLeftTopClock...");
            mMiuiKeyguardLeftTopClockCls = XposedHelpers.findClass(CLASS_MIUI_KEYGUARD_LEFT_TOP_CLOCK, getMClassLoader());

            if (mShowHorizontalSec) {
                hookConstructor();
                hookUpdateTime();
            }

            if (mOneSentenceEnabled) {
                hookOnFinishInflate();
            }
        } catch (Throwable t) {
            XLogKt.logE("Error occurs when hook MiuiKeyguardLeftTopClock", t);
        }
    }

    // com.android.keyguard.MiuiKeyguardLeftTopClock#updateTime()
    private void hookUpdateTime() {
        findAndHookMethod(mMiuiKeyguardLeftTopClockCls,
                "updateTime",
                new MethodHookWrapper() {
                    @Override
                    protected void after(MethodHookParam param) {
                        TextView mTimeText = (TextView) XposedHelpers.getObjectField(param.thisObject, "mTimeText");
                        if (mTimeText != null) {
                            String originalTimeStr = mTimeText.getText().toString();
                            mTimeText.setText(addInSecond(originalTimeStr));
                        }
                    }
                });
    }

    private String addInSecond(String originalTimeStr) {
        int sec = Calendar.getInstance().get(Calendar.SECOND);
        String secStr = String.format(Locale.getDefault(), "%02d", sec);
        return originalTimeStr.replaceAll("(\\d+:\\d+)(:\\d+)?", "$1:" + secStr);
    }

    // com.android.keyguard.MiuiKeyguardLeftTopClock#access()
    private void hookConstructor() {
        hookAllConstructors(mMiuiKeyguardLeftTopClockCls,
                new MethodHookWrapper() {
                    @Override
                    protected void after(MethodHookParam param) {
                        final View keyguardClock = (View) param.thisObject;
                        keyguardClock.getViewTreeObserver().addOnWindowAttachListener(new ViewTreeObserver.OnWindowAttachListener() {
                            @Override
                            public void onWindowAttached() {
                                addClock(keyguardClock);
                            }

                            @Override
                            public void onWindowDetached() {
                                removeClock(keyguardClock);
                            }
                        });
                        addClock(keyguardClock);

                        ScreenBroadcastManager.getInstance(keyguardClock.getContext()).registerListener(screenListener);
                    }
                });
    }

    private synchronized void addClock(View clock) {
        if (!mKeyguardClockList.contains(clock)) {
            mKeyguardClockList.add(clock);

            int size = mKeyguardClockList.size();
            int limitedSize = 2;
            if (size > limitedSize) {
                for (int i = 0; i < size - limitedSize; i++) {
                    View item = mKeyguardClockList.get(i);
                    mKeyguardClockList.remove(item);
                }
            }
        }

        if (!mKeyguardClockList.isEmpty()) {
            TimeTicker.get().registerObserver(MiuiKeyguardLeftTopClockHook.this);
        }
    }

    private synchronized void removeClock(View clock) {
        mKeyguardClockList.remove(clock);

        if (mKeyguardClockList.isEmpty()) {
            TimeTicker.get().unregisterObserver(MiuiKeyguardLeftTopClockHook.this);
        }
    }

    private final SimpleScreenListener screenListener = new SimpleScreenListener() {
        @Override
        public void onScreenOn() {
            TimeTicker.get().registerObserver(MiuiKeyguardLeftTopClockHook.this);
        }

        @Override
        public void onScreenOff() {
            TimeTicker.get().unregisterObserver(MiuiKeyguardLeftTopClockHook.this);
        }

        @Override
        public void onUserPresent() {
            TimeTicker.get().unregisterObserver(MiuiKeyguardLeftTopClockHook.this);
        }

        @Override
        public void onStopTimeTick() {
            TimeTicker.get().unregisterObserver(MiuiKeyguardLeftTopClockHook.this);
        }
    };

    @Override
    public void onTimeTick() {
        for (View keyguardClock : mKeyguardClockList) {
            if (keyguardClock != null) {
                XposedHelpers.callMethod(keyguardClock, "updateTime");
            }
        }
    }

    // com.android.keyguard.MiuiKeyguardLeftTopClock#onFinishInflate()
    private void hookOnFinishInflate() {
        findAndHookMethod(mMiuiKeyguardLeftTopClockCls,
                "onFinishInflate",
                new MethodHookWrapper() {
                    @Override
                    protected void after(MethodHookParam param) {
                        LinearLayout keyguardClock = (LinearLayout) param.thisObject;

                        try {
                            // 校正 HitokotoTextView 位置
                            TextView hitokotoInfo = keyguardClock.findViewById(R.id.hitokoto_info_text_view);
                            if (hitokotoInfo != null) {
                                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) hitokotoInfo.getLayoutParams();
                                layoutParams.gravity = Gravity.START;

                                layoutParams.leftMargin = (int) ResolutionUtils.dp2px(keyguardClock.getContext(), 5);
                            }
                        } catch (Throwable t) {
                            // ignore
                            XLogKt.logE("", t);
                        }
                    }
                });
    }
}
