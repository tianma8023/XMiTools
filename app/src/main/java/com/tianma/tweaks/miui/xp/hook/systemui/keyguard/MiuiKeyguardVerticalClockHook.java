package com.tianma.tweaks.miui.xp.hook.systemui.keyguard;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tianma.tweaks.miui.utils.XLog;
import com.tianma.tweaks.miui.utils.XSPUtils;
import com.tianma.tweaks.miui.xp.hook.BaseSubHook;
import com.tianma.tweaks.miui.xp.hook.systemui.tick.TickObserver;
import com.tianma.tweaks.miui.xp.hook.systemui.tick.TimeTicker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedHelpers;

import static de.robv.android.xposed.XposedBridge.hookAllConstructors;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.getAdditionalInstanceField;
import static de.robv.android.xposed.XposedHelpers.getBooleanField;
import static de.robv.android.xposed.XposedHelpers.getFloatField;
import static de.robv.android.xposed.XposedHelpers.getObjectField;
import static de.robv.android.xposed.XposedHelpers.setAdditionalInstanceField;

/**
 * 锁屏界面
 * 适用版本 9.5.7+
 * 锁屏居中时钟 Hook
 */
public class MiuiKeyguardVerticalClockHook extends BaseSubHook implements TickObserver {

    private static final String CLASS_MIUI_KEYGUARD_VERTICAL_CLOCK = "com.android.keyguard.MiuiKeyguardVerticalClock";

    private Class<?> mMiuiKeyguardVerticalClockCls;

    private final boolean mShowVerticalSec;
    private final boolean mShowHorizontalSec;

    private List<View> mKeyguardClockList = new ArrayList<>();

    private static final String M_HORIZONTAL_TIME_LAYOUT = "mHorizontalTimeLayout";
    private static final String M_HORIZONTAL_DOT = "mHorizontalDot";
    private static final String M_HORIZONTAL_DOT_2 = "mHorizontalDot2";
    private static final String M_HORIZONTAL_MIN = "mHorizontalMin";
    private static final String M_HORIZONTAL_SEC = "mHorizontalSec";

    private static final String M_VERTICAL_TIME_LAYOUT = "mVerticalTimeLayout";
    private static final String M_VERTICAL_MIN = "mVerticalMin";
    private static final String M_VERTICAL_SEC = "mVerticalSec";

    private static final String M_VERTICAL_TO_HORIZONTAL_ANIM_2 = "mVerticalToHorizontalAnim2";
    private static final String M_HORIZONTAL_TO_VERTICAL_ANIM_2 = "mHorizontalToVerticalAnim2";

    public MiuiKeyguardVerticalClockHook(ClassLoader classLoader, XSharedPreferences xsp) {
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
            XLog.d("Hooking MiuiKeyguardVerticalClock...");
            mMiuiKeyguardVerticalClockCls = XposedHelpers
                    .findClass(CLASS_MIUI_KEYGUARD_VERTICAL_CLOCK, mClassLoader);
            hookOnFinishInflate();
            hookUpdateViewTextSize();
            hookUpdateTime();
            hookConstructor();
            hookShowHorizontalTime();
            hookShowVerticalTime();
            hookPlayVerticalToHorizontalAnim();
            hookPlayHorizontalToVerticalAnim();
            hookClearAnim();
            hookSetDarkMode();
        } catch (Throwable t) {
            XLog.e("Error occurs when hook MiuiKeyguardVerticalClock", t);
        }
    }

    // com.android.keyguard.MiuiKeyguardVerticalClock#onFinishInflate()
    private void hookOnFinishInflate() {
        findAndHookMethod(mMiuiKeyguardVerticalClockCls,
                "onFinishInflate",
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        try {
                            LinearLayout miuiKeyguardClock = (LinearLayout) param.thisObject;

                            if (mShowHorizontalSec) {
                                // horizontal layout
                                LinearLayout mHorizontalTimeLayout = (LinearLayout) getObjectField(miuiKeyguardClock, M_HORIZONTAL_TIME_LAYOUT);

                                TextView mHorizontalDot = (TextView) getObjectField(miuiKeyguardClock, M_HORIZONTAL_DOT);
                                TextView mHorizontalDot2 = createTextViewByCopyAttributes(mHorizontalDot);
                                mHorizontalTimeLayout.addView(mHorizontalDot2);
                                setAdditionalInstanceField(miuiKeyguardClock, M_HORIZONTAL_DOT_2, mHorizontalDot2);

                                TextView mHorizontalMin = (TextView) getObjectField(miuiKeyguardClock, M_HORIZONTAL_MIN);
                                TextView mHorizontalSec = createTextViewByCopyAttributes(mHorizontalMin);
                                mHorizontalTimeLayout.addView(mHorizontalSec);
                                setAdditionalInstanceField(miuiKeyguardClock, M_HORIZONTAL_SEC, mHorizontalSec);
                            }

                            if (mShowVerticalSec) {
                                // vertical layout
                                LinearLayout mVerticalTimeLayout = (LinearLayout) getObjectField(miuiKeyguardClock, M_VERTICAL_TIME_LAYOUT);

                                TextView mVerticalMin = (TextView) getObjectField(miuiKeyguardClock, M_VERTICAL_MIN);
                                TextView mVerticalSec = createTextViewByCopyAttributes(mVerticalMin);
                                mVerticalTimeLayout.addView(mVerticalSec);
                                setAdditionalInstanceField(miuiKeyguardClock, M_VERTICAL_SEC, mVerticalSec);
                            }
                        } catch (Throwable t) {
                            XLog.e("", t);
                        }
                    }

                    private TextView createTextViewByCopyAttributes(TextView origin) {
                        TextView dest = new TextView(origin.getContext());
                        LinearLayout.LayoutParams originLP = (LinearLayout.LayoutParams) origin.getLayoutParams();
                        LinearLayout.LayoutParams destLP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        destLP.topMargin = originLP.topMargin;
                        dest.setLayoutParams(destLP);
                        dest.setTextSize(TypedValue.COMPLEX_UNIT_PX, origin.getTextSize());
                        dest.setTextColor(origin.getCurrentTextColor());
                        dest.setGravity(origin.getGravity());
                        dest.setTypeface(origin.getTypeface());
                        dest.setLetterSpacing(origin.getLetterSpacing());
                        dest.setText(origin.getText());
                        return dest;
                    }
                });
    }

    // com.android.keyguard.MiuiKeyguardVerticalClock#updateViewTextSize()
    private void hookUpdateViewTextSize() {
        findAndHookMethod(mMiuiKeyguardVerticalClockCls,
                "updateViewsTextSize",
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        try {
                            Object keyguardClock = param.thisObject;

                            TextView mHorizontalSec = (TextView) getAdditionalInstanceField(keyguardClock, M_HORIZONTAL_SEC);
                            if (mHorizontalSec != null) {
                                TextView mHorizontalMin = (TextView) getObjectField(keyguardClock, M_HORIZONTAL_MIN);
                                mHorizontalSec.setTextSize(TypedValue.COMPLEX_UNIT_PX, mHorizontalMin.getTextSize());
                            }

                            TextView mHorizontalDot2 = (TextView) getAdditionalInstanceField(keyguardClock, M_HORIZONTAL_DOT_2);
                            if (mHorizontalDot2 != null) {
                                TextView mHorizontalDot = (TextView) getObjectField(keyguardClock, M_HORIZONTAL_DOT);
                                mHorizontalDot2.setTextSize(TypedValue.COMPLEX_UNIT_PX, mHorizontalDot.getTextSize());
                            }

                            TextView mVerticalSec = (TextView) getAdditionalInstanceField(keyguardClock, M_VERTICAL_SEC);
                            if (mVerticalSec != null) {
                                TextView mVerticalMin = (TextView) getObjectField(keyguardClock, M_VERTICAL_MIN);
                                mVerticalSec.setTextSize(TypedValue.COMPLEX_UNIT_PX, mVerticalMin.getTextSize());
                            }
                        } catch (Throwable t) {
                            XLog.e("", t);
                        }
                    }
                });
    }

    // com.android.keyguard.MiuiKeyguardVerticalClock#updateTime()
    private void hookUpdateTime() {
        findAndHookMethod(mMiuiKeyguardVerticalClockCls,
                "updateTime",
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        try {
                            Object keyguardClock = param.thisObject;

                            int sec = Calendar.getInstance().get(Calendar.SECOND);
                            String secStr = String.format(Locale.getDefault(), "%02d", sec);

                            TextView mHorizontalSec = (TextView) getAdditionalInstanceField(keyguardClock, M_HORIZONTAL_SEC);
                            if (mHorizontalSec != null) {
                                mHorizontalSec.setText(secStr);
                            }

                            TextView mVerticalSec = (TextView) getAdditionalInstanceField(keyguardClock, M_VERTICAL_SEC);
                            if (mVerticalSec != null) {
                                mVerticalSec.setText(secStr);
                            }
                        } catch (Throwable t) {
                            XLog.e("", t);
                        }
                    }
                });
    }

    // com.android.keyguard.MiuiKeyguardVerticalClock#access()
    private void hookConstructor() {
        hookAllConstructors(mMiuiKeyguardVerticalClockCls,
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        try {
                            final LinearLayout keyguardClock = (LinearLayout) param.thisObject;

                            if (mShowVerticalSec) {
                                AnimatorSet mVerticalToHorizontalAnim2 = new AnimatorSet();
                                setAdditionalInstanceField(keyguardClock, M_VERTICAL_TO_HORIZONTAL_ANIM_2, mVerticalToHorizontalAnim2);

                                AnimatorSet mHorizontalToVerticalAnim2 = new AnimatorSet();
                                setAdditionalInstanceField(keyguardClock, M_HORIZONTAL_TO_VERTICAL_ANIM_2, mHorizontalToVerticalAnim2);
                            }

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

                            // register receiver
                            IntentFilter filter = new IntentFilter();
                            filter.addAction(Intent.ACTION_SCREEN_ON);
                            filter.addAction(Intent.ACTION_USER_PRESENT);
                            filter.addAction(Intent.ACTION_SCREEN_OFF);
                            filter.addAction(IntentAction.KEYGUARD_STOP_TIME_TICK);

                            keyguardClock.getContext().registerReceiver(mScreenReceiver, filter);
                        } catch (Throwable e) {
                            XLog.e("", e);
                        }
                    }
                });
    }

    private synchronized void addClock(View clock) {
        if (!mKeyguardClockList.contains(clock)) {
            mKeyguardClockList.add(clock);

            int size = mKeyguardClockList.size();
            int limitedSize = 2;
            if (size > limitedSize) {
                for (int i = 0; i < size - limitedSize; i ++) {
                    View item = mKeyguardClockList.get(i);
                    mKeyguardClockList.remove(item);
                }
            }
        }

        if (!mKeyguardClockList.isEmpty()) {
            TimeTicker.get().registerObserver(MiuiKeyguardVerticalClockHook.this);
        }
    }

    private synchronized void removeClock(View clock) {
        mKeyguardClockList.remove(clock);

        if (mKeyguardClockList.isEmpty()) {
            TimeTicker.get().unregisterObserver(MiuiKeyguardVerticalClockHook.this);
        }
    }

    @Override
    public void onTimeTick() {
        for (View keyguardClock : mKeyguardClockList) {
            if (keyguardClock != null) {
                XposedHelpers.callMethod(keyguardClock, "updateTime");
            }
        }
    }

    private final BroadcastReceiver mScreenReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Intent.ACTION_SCREEN_ON.equals(action)) {
                TimeTicker.get().registerObserver(MiuiKeyguardVerticalClockHook.this);
            } else if (Intent.ACTION_USER_PRESENT.equals(action)
                    || Intent.ACTION_SCREEN_OFF.equals(action)) {
                TimeTicker.get().unregisterObserver(MiuiKeyguardVerticalClockHook.this);
            } else if (IntentAction.KEYGUARD_STOP_TIME_TICK.equals(action)) {
                TimeTicker.get().unregisterObserver(MiuiKeyguardVerticalClockHook.this);
            }
        }
    };

    // com.android.keyguard.MiuiKeyguardClock#showHorizontalTime()
    private void hookShowHorizontalTime() {
        findAndHookMethod(mMiuiKeyguardVerticalClockCls,
                "showHorizontalTime",
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        try {
                            Object keyguardClock = param.thisObject;

                            TextView mVerticalSec = (TextView) getAdditionalInstanceField(keyguardClock, M_VERTICAL_SEC);
                            if (mVerticalSec != null) {
                                boolean mShowHorizontalTime = getBooleanField(param.thisObject, "mShowHorizontalTime");
                                if (mShowHorizontalTime) {
                                    mVerticalSec.setAlpha(0.0f);
                                }
                            }
                        } catch (Throwable t) {
                            XLog.e("", t);
                        }
                    }
                });
    }

    // com.android.keyguard.MiuiKeyguardVerticalClock#showVerticalTime()
    private void hookShowVerticalTime() {
        findAndHookMethod(mMiuiKeyguardVerticalClockCls,
                "showVerticalTime",
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        try {
                            Object keyguardClock = param.thisObject;

                            TextView mVerticalSec = (TextView) getAdditionalInstanceField(keyguardClock, M_VERTICAL_SEC);
                            if (mVerticalSec != null) {
                                boolean mShowHorizontalTime = getBooleanField(param.thisObject, "mShowHorizontalTime");
                                if (!mShowHorizontalTime) {
                                    mVerticalSec.setAlpha(1.0f);
                                    mVerticalSec.setTranslationY(0.0f);
                                }
                            }
                        } catch (Throwable t) {
                            XLog.e("", t);
                        }
                    }
                });
    }

    // com.android.keyguard.MiuiKeyguardVerticalClock#playVerticalToHorizontalAnim()
    private void hookPlayVerticalToHorizontalAnim() {
        findAndHookMethod(mMiuiKeyguardVerticalClockCls,
                "playVerticalToHorizontalAnim",
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        Object keyguardClock = param.thisObject;
                        AnimatorSet mVerticalToHorizontalAnim2 = (AnimatorSet) getAdditionalInstanceField(keyguardClock, M_VERTICAL_TO_HORIZONTAL_ANIM_2);
                        TextView mVerticalSec = (TextView) getAdditionalInstanceField(keyguardClock, M_VERTICAL_SEC);
                        if (mVerticalToHorizontalAnim2 == null || mVerticalSec == null) {
                            return;
                        }
                        try {
                            float mVerticalTimeLayoutHeight = getFloatField(keyguardClock, "mVerticalTimeLayoutHeight");
                            float mVerticalTimePaddingTop = getFloatField(keyguardClock, "mVerticalTimePaddingTop");
                            float mHorizontalTimeLayoutHeight = getFloatField(keyguardClock, "mHorizontalTimeLayoutHeight");
                            float mHorizontalTimePaddingTop = getFloatField(keyguardClock, "mHorizontalTimePaddingTop");

                            float[] f = new float[]{0.0f, -((((mVerticalTimeLayoutHeight - mVerticalTimePaddingTop) / 2.0f) + mVerticalTimePaddingTop) - (((mHorizontalTimeLayoutHeight - mHorizontalTimePaddingTop) / 2.0f) + mHorizontalTimePaddingTop))};
                            ObjectAnimator translationYAnim = ObjectAnimator.ofFloat(mVerticalSec, "translationY", f);
                            translationYAnim.setDuration(425);
                            translationYAnim.setInterpolator(Ease.Cubic.easeInOut);

                            float[] f2 = new float[]{1.0f, 0.0f};
                            ObjectAnimator alphaAnim = ObjectAnimator.ofFloat(mVerticalSec, "alpha", f2);
                            alphaAnim.setDuration(425);
                            alphaAnim.setInterpolator(Ease.Sine.easeInOut);

                            mVerticalToHorizontalAnim2.play(translationYAnim).with(alphaAnim);
                            mVerticalToHorizontalAnim2.start();
                        } catch (Throwable t) {
                            XLog.e("", t);
                        }
                    }
                });
    }

    // com.android.keyguard.MiuiKeyguardVerticalClock#playHorizontalToVerticalAnim()
    private void hookPlayHorizontalToVerticalAnim() {
        findAndHookMethod(mMiuiKeyguardVerticalClockCls,
                "playHorizontalToVerticalAnim",
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        Object keyguardClock = param.thisObject;
                        AnimatorSet mHorizontalToVerticalAnim2 = (AnimatorSet) getAdditionalInstanceField(keyguardClock, M_HORIZONTAL_TO_VERTICAL_ANIM_2);
                        TextView mVerticalSec = (TextView) getAdditionalInstanceField(keyguardClock, M_VERTICAL_SEC);
                        if (mHorizontalToVerticalAnim2 == null || mVerticalSec == null) {
                            return;
                        }
                        try {
                            float mVerticalTimeLayoutHeight = getFloatField(keyguardClock, "mVerticalTimeLayoutHeight");
                            float mVerticalTimePaddingTop = getFloatField(keyguardClock, "mVerticalTimePaddingTop");
                            float mHorizontalTimeLayoutHeight = getFloatField(keyguardClock, "mHorizontalTimeLayoutHeight");
                            float mHorizontalTimePaddingTop = getFloatField(keyguardClock, "mHorizontalTimePaddingTop");


                            float[] f1 = new float[]{-((((mVerticalTimeLayoutHeight - mVerticalTimePaddingTop) / 2.0f) + mVerticalTimePaddingTop) - (((mHorizontalTimeLayoutHeight - mHorizontalTimePaddingTop) / 2.0f) + mHorizontalTimePaddingTop)), 0.0f};
                            ObjectAnimator translationYAnim = ObjectAnimator.ofFloat(mVerticalSec, "translationY", f1);
                            translationYAnim.setDuration(425);
                            translationYAnim.setInterpolator(Ease.Cubic.easeOut);

                            float[] f2 = new float[]{0.0f, 1.0f};
                            ObjectAnimator alphaAnim = ObjectAnimator.ofFloat(mVerticalSec, "alpha", f2);
                            alphaAnim.setDuration(425);
                            alphaAnim.setInterpolator(Ease.Sine.easeInOut);


                            mHorizontalToVerticalAnim2.play(translationYAnim).with(alphaAnim);
                            mHorizontalToVerticalAnim2.start();
                        } catch (Throwable t) {
                            XLog.e("", t);
                        }
                    }
                });
    }

    // com.android.keyguard.MiuiKeyguardVerticalClock#clearAnim()
    private void hookClearAnim() {
        findAndHookMethod(mMiuiKeyguardVerticalClockCls,
                "clearAnim",
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        Object keyguardClock = param.thisObject;
                        AnimatorSet mHorizontalToVerticalAnim2 = (AnimatorSet) getAdditionalInstanceField(keyguardClock, M_HORIZONTAL_TO_VERTICAL_ANIM_2);
                        if (mHorizontalToVerticalAnim2 != null) {
                            mHorizontalToVerticalAnim2.cancel();
                        }

                        AnimatorSet mVerticalToHorizontalAnim2 = (AnimatorSet) getAdditionalInstanceField(keyguardClock, M_VERTICAL_TO_HORIZONTAL_ANIM_2);
                        if (mVerticalToHorizontalAnim2 != null) {
                            mVerticalToHorizontalAnim2.cancel();
                        }

                        TextView mVerticalSec = (TextView) getAdditionalInstanceField(keyguardClock, M_VERTICAL_SEC);
                        if (mVerticalSec != null) {
                            mVerticalSec.clearAnimation();
                        }
                    }
                });
    }

    // com.android.keyguard.MiuiKeyguardVerticalClock#clearAnim()
    private void hookSetDarkMode() {
        findAndHookMethod(mMiuiKeyguardVerticalClockCls,
                "setDarkMode",
                boolean.class,
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        Object keyguardClock = param.thisObject;

                        TextView mHorizontalSec = (TextView) getAdditionalInstanceField(keyguardClock, M_HORIZONTAL_SEC);
                        if (mHorizontalSec != null) {
                            TextView mHorizontalMin = (TextView) getObjectField(keyguardClock, M_HORIZONTAL_MIN);
                            mHorizontalSec.setTextColor(mHorizontalMin.getTextColors());
                        }

                        TextView mHorizontalDot2 = (TextView) getAdditionalInstanceField(keyguardClock, M_HORIZONTAL_DOT_2);
                        if (mHorizontalDot2 != null) {
                            TextView mHorizontalDot = (TextView) getObjectField(keyguardClock, M_HORIZONTAL_DOT);
                            mHorizontalDot2.setTextColor(mHorizontalDot.getTextColors());
                        }

                        TextView mVerticalSec = (TextView) getAdditionalInstanceField(keyguardClock, M_VERTICAL_SEC);
                        if (mVerticalSec != null) {
                            TextView mVerticalMin = (TextView) getObjectField(keyguardClock, M_VERTICAL_MIN);
                            mVerticalSec.setTextColor(mVerticalMin.getTextColors());
                        }
                    }
                });
    }
}
