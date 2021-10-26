package com.tianma.tweaks.miui.xp.hook.systemui.keyguard.def;

import static com.tianma.tweaks.miui.xp.wrapper.XposedWrapper.findAndHookMethod;
import static com.tianma.tweaks.miui.xp.wrapper.XposedWrapper.hookAllConstructors;
import static de.robv.android.xposed.XposedHelpers.getAdditionalInstanceField;
import static de.robv.android.xposed.XposedHelpers.getBooleanField;
import static de.robv.android.xposed.XposedHelpers.getFloatField;
import static de.robv.android.xposed.XposedHelpers.getObjectField;
import static de.robv.android.xposed.XposedHelpers.setAdditionalInstanceField;

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

import com.tianma.tweaks.miui.data.sp.XPrefContainer;
import com.tianma.tweaks.miui.utils.XLogKt;
import com.tianma.tweaks.miui.xp.hook.BaseSubHook;
import com.tianma.tweaks.miui.xp.hook.systemui.tick.TickObserver;
import com.tianma.tweaks.miui.xp.hook.systemui.tick.TimeTicker;
import com.tianma.tweaks.miui.xp.wrapper.MethodHookWrapper;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import de.robv.android.xposed.XposedHelpers;

/**
 * 锁屏界面
 * 适用版本 < 9.5.7
 * 锁屏时钟 Hook
 */
public class MiuiKeyguardClockHook extends BaseSubHook implements TickObserver {

    private static final String CLASS_MIUI_KEYGUARD_CLOCK = "com.android.keyguard.MiuiKeyguardClock";

    private Class<?> mMiuiKeyguardClockCls;

    private boolean mShowVerticalSec;
    private boolean mShowHorizontalSec;

    private Set<Object> mKeyguardClockSet = new HashSet<>();

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

    public MiuiKeyguardClockHook(ClassLoader classLoader) {
        super(classLoader);

        // mShowHorizontalSec = XSPUtils.showSecInKeyguardHorizontal(xsp);
        mShowHorizontalSec = XPrefContainer.getShowSecInKeyguardHorizontal();
        // mShowVerticalSec = XSPUtils.showSecInKeyguardVertical(xsp);
        mShowVerticalSec = XPrefContainer.getShowSecInKeyguardVertical();
    }

    @Override
    public void startHook() {
        if (!mShowHorizontalSec && !mShowVerticalSec) {
            return;
        }
        try {
            XLogKt.logD("Hooking MiuiKeyguardClock...");
            mMiuiKeyguardClockCls = XposedHelpers
                    .findClass(CLASS_MIUI_KEYGUARD_CLOCK, getMClassLoader());
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
            XLogKt.logE("Error occurs when hook MiuiKeyguardClock", t);
        }
    }

    // com.android.keyguard.MiuiKeyguardClock#onFinishInflate()
    private void hookOnFinishInflate() {
        findAndHookMethod(mMiuiKeyguardClockCls,
                "onFinishInflate",
                new MethodHookWrapper() {
                    @Override
                    protected void after(MethodHookParam param) {
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

    // com.android.keyguard.MiuiKeyguardClock#updateViewTextSize()
    private void hookUpdateViewTextSize() {
        findAndHookMethod(mMiuiKeyguardClockCls,
                "updateViewsTextSize",
                new MethodHookWrapper() {
                    @Override
                    protected void after(MethodHookParam param) {
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
                    }
                });
    }

    // com.android.keyguard.MiuiKeyguardClock#updateTime()
    private void hookUpdateTime() {
        findAndHookMethod(mMiuiKeyguardClockCls,
                "updateTime",
                new MethodHookWrapper() {
                    @Override
                    protected void after(MethodHookParam param) {
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
                    }
                });
    }

    // com.android.keyguard.MiuiKeyguardClock#access()
    private void hookConstructor() {
        hookAllConstructors(mMiuiKeyguardClockCls,
                new MethodHookWrapper() {
                    @Override
                    protected void after(MethodHookParam param) {
                        LinearLayout keyguardClock = (LinearLayout) param.thisObject;

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

                        // register receiver
                        IntentFilter filter = new IntentFilter();
                        filter.addAction(Intent.ACTION_SCREEN_ON);
                        filter.addAction(Intent.ACTION_USER_PRESENT);
                        filter.addAction(Intent.ACTION_SCREEN_OFF);

                        keyguardClock.getContext().registerReceiver(mScreenReceiver, filter);
                    }
                });
    }

    private synchronized void addClock(View clock) {
        mKeyguardClockSet.add(clock);

        if (!mKeyguardClockSet.isEmpty()) {
            TimeTicker.get().registerObserver(MiuiKeyguardClockHook.this);
        }
    }

    private synchronized void removeClock(View clock) {
        mKeyguardClockSet.remove(clock);

        if (mKeyguardClockSet.isEmpty()) {
            TimeTicker.get().unregisterObserver(MiuiKeyguardClockHook.this);
        }
    }

    @Override
    public void onTimeTick() {
        for (Object keyguardClock : mKeyguardClockSet) {
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
                TimeTicker.get().registerObserver(MiuiKeyguardClockHook.this);
            } else if (Intent.ACTION_USER_PRESENT.equals(action)
                    || Intent.ACTION_SCREEN_OFF.equals(action)) {
                TimeTicker.get().unregisterObserver(MiuiKeyguardClockHook.this);
            }
        }
    };

    // com.android.keyguard.MiuiKeyguardClock#showHorizontalTime()
    private void hookShowHorizontalTime() {
        findAndHookMethod(mMiuiKeyguardClockCls,
                "showHorizontalTime",
                new MethodHookWrapper() {
                    @Override
                    protected void after(MethodHookParam param) {
                        Object keyguardClock = param.thisObject;

                        TextView mVerticalSec = (TextView) getAdditionalInstanceField(keyguardClock, M_VERTICAL_SEC);
                        if (mVerticalSec != null) {
                            boolean mShowHorizontalTime = getBooleanField(param.thisObject, "mShowHorizontalTime");
                            if (mShowHorizontalTime) {
                                mVerticalSec.setAlpha(0.0f);
                            }
                        }
                    }
                });
    }

    // com.android.keyguard.MiuiKeyguardClock#showVerticalTime()
    private void hookShowVerticalTime() {
        findAndHookMethod(mMiuiKeyguardClockCls,
                "showVerticalTime",
                new MethodHookWrapper() {
                    @Override
                    protected void after(MethodHookParam param) {
                        Object keyguardClock = param.thisObject;

                        TextView mVerticalSec = (TextView) getAdditionalInstanceField(keyguardClock, M_VERTICAL_SEC);
                        if (mVerticalSec != null) {
                            boolean mShowHorizontalTime = getBooleanField(param.thisObject, "mShowHorizontalTime");
                            if (!mShowHorizontalTime) {
                                mVerticalSec.setAlpha(1.0f);
                                mVerticalSec.setTranslationY(0.0f);
                            }
                        }
                    }
                });
    }

    // com.android.keyguard.MiuiKeyguardClock#playVerticalToHorizontalAnim()
    private void hookPlayVerticalToHorizontalAnim() {
        findAndHookMethod(mMiuiKeyguardClockCls,
                "playVerticalToHorizontalAnim",
                new MethodHookWrapper() {
                    @Override
                    protected void after(MethodHookParam param) {
                        Object keyguardClock = param.thisObject;
                        AnimatorSet mVerticalToHorizontalAnim2 = (AnimatorSet) getAdditionalInstanceField(keyguardClock, M_VERTICAL_TO_HORIZONTAL_ANIM_2);
                        TextView mVerticalSec = (TextView) getAdditionalInstanceField(keyguardClock, M_VERTICAL_SEC);
                        if (mVerticalToHorizontalAnim2 == null || mVerticalSec == null) {
                            return;
                        }

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
                    }
                });
    }

    // com.android.keyguard.MiuiKeyguardClock#playHorizontalToVerticalAnim()
    private void hookPlayHorizontalToVerticalAnim() {
        findAndHookMethod(mMiuiKeyguardClockCls,
                "playHorizontalToVerticalAnim",
                new MethodHookWrapper() {
                    @Override
                    protected void after(MethodHookParam param) {
                        Object keyguardClock = param.thisObject;
                        AnimatorSet mHorizontalToVerticalAnim2 = (AnimatorSet) getAdditionalInstanceField(keyguardClock, M_HORIZONTAL_TO_VERTICAL_ANIM_2);
                        TextView mVerticalSec = (TextView) getAdditionalInstanceField(keyguardClock, M_VERTICAL_SEC);
                        if (mHorizontalToVerticalAnim2 == null || mVerticalSec == null) {
                            return;
                        }

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
                    }
                });
    }

    // com.android.keyguard.MiuiKeyguardClock#clearAnim()
    private void hookClearAnim() {
        findAndHookMethod(mMiuiKeyguardClockCls,
                "clearAnim",
                new MethodHookWrapper() {
                    @Override
                    protected void after(MethodHookParam param) {
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

    // com.android.keyguard.MiuiKeyguardClock#clearAnim()
    private void hookSetDarkMode() {
        findAndHookMethod(mMiuiKeyguardClockCls,
                "setDarkMode",
                boolean.class,
                new MethodHookWrapper() {
                    @Override
                    protected void after(MethodHookParam param) {
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
