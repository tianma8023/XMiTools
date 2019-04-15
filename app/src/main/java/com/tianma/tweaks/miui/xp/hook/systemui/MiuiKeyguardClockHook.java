package com.tianma.tweaks.miui.xp.hook.systemui;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.TypedValue;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tianma.tweaks.miui.utils.XLog;
import com.tianma.tweaks.miui.utils.XSPUtils;
import com.tianma.tweaks.miui.xp.hook.BaseSubHook;

import java.util.Calendar;
import java.util.Locale;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedHelpers;

import static de.robv.android.xposed.XposedBridge.hookAllConstructors;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.getBooleanField;
import static de.robv.android.xposed.XposedHelpers.getFloatField;
import static de.robv.android.xposed.XposedHelpers.getObjectField;

/**
 * 锁屏界面
 */
public class MiuiKeyguardClockHook extends BaseSubHook {

    private static final String MIUI_KEYGUARD_CLOCK_CLASS_NAME = "com.android.keyguard.MiuiKeyguardClock";

    private Class<?> mMiuiKeyguardClockCls;

    private boolean mShowVerticalSec;
    private boolean mShowHorizontalSec;

    public MiuiKeyguardClockHook(ClassLoader classLoader, XSharedPreferences xsp) {
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
            XLog.d("Hooking MiuiKeyguardClock...");
            mMiuiKeyguardClockCls = XposedHelpers
                    .findClass(MIUI_KEYGUARD_CLOCK_CLASS_NAME, mClassLoader);
            hookOnFinishInflate();
            hookUpdateViewTextSize();
            hookUpdateTime();
            hookConstructor();
            hookShowHorizontalTime();
            hookShowVerticalTime();
            hookPlayVerticalToHorizontalAnim();
            hookPlayHorizontalToVerticalAnim();
            hookClearAnim();
        } catch (Throwable t) {
            XLog.e("Error occurs when hook MiuiKeyguardClock", t);
        }
    }

    // 水平时间 TextView
    private TextView mHorizontalMin;
    private TextView mHorizontalSec;
    private TextView mHorizontalDot;
    private TextView mHorizontalDot2;

    // 垂直时间 TextView
    private TextView mVerticalMin;
    private TextView mVerticalSec;

    // 动画 AnimatorSet
    private AnimatorSet mVerticalToHorizontalAnim2;
    private AnimatorSet mHorizontalToVerticalAnim2;

    // com.android.keyguard.MiuiKeyguardClock#onFinishInflate()
    private void hookOnFinishInflate() {
        findAndHookMethod(mMiuiKeyguardClockCls,
                "onFinishInflate",
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        try {
                            LinearLayout miuiKeyguardClock = (LinearLayout) param.thisObject;

                            if (mShowHorizontalSec) {
                                // horizontal layout
                                LinearLayout mHorizontalTimeLayout = (LinearLayout) getObjectField(miuiKeyguardClock, "mHorizontalTimeLayout");

                                mHorizontalDot = (TextView) getObjectField(miuiKeyguardClock, "mHorizontalDot");
                                mHorizontalDot2 = createTextViewByCopyAttributes(mHorizontalDot);
                                mHorizontalTimeLayout.addView(mHorizontalDot2);
//                            setAdditionalInstanceField(miuiKeyguardClock, "mHorizontalDot2", mHorizontalDot2);

                                mHorizontalMin = (TextView) getObjectField(miuiKeyguardClock, "mHorizontalMin");
                                mHorizontalSec = createTextViewByCopyAttributes(mHorizontalMin);
                                mHorizontalTimeLayout.addView(mHorizontalSec);
//                            setAdditionalInstanceField(miuiKeyguardClock, "mHorizontalSec", mHorizontalSec);
                            }

                            if (mShowVerticalSec) {
                                // vertical layout
                                LinearLayout mVerticalTimeLayout = (LinearLayout) getObjectField(miuiKeyguardClock, "mVerticalTimeLayout");

                                mVerticalMin = (TextView) getObjectField(miuiKeyguardClock, "mVerticalMin");
                                mVerticalSec = createTextViewByCopyAttributes(mVerticalMin);
                                mVerticalTimeLayout.addView(mVerticalSec);
//                            setAdditionalInstanceField(miuiKeyguardClock, "mVerticalSec", mVerticalSec);
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

    // com.android.keyguard.MiuiKeyguardClock#updateViewTextSize()
    private void hookUpdateViewTextSize() {
        findAndHookMethod(mMiuiKeyguardClockCls,
                "updateViewsTextSize",
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        try {
//                            LinearLayout miuiKeyguardClock = (LinearLayout) param.thisObject;

//                            TextView mHorizontalSec = (TextView) getAdditionalInstanceField(miuiKeyguardClock, "mHorizontalSec");
                            if (mHorizontalSec != null) {
//                                TextView mHorizontalMin = (TextView) getObjectField(miuiKeyguardClock, "mHorizontalMin");
                                mHorizontalSec.setTextSize(TypedValue.COMPLEX_UNIT_PX, mHorizontalMin.getTextSize());
                            }

//                            TextView mHorizontalDot2 = (TextView) getAdditionalInstanceField(miuiKeyguardClock, "mHorizontalDot");
                            if (mHorizontalDot2 != null) {
//                                TextView mHorizontalDot = (TextView) getObjectField(miuiKeyguardClock, "mHorizontalDot");
                                mHorizontalDot2.setTextSize(TypedValue.COMPLEX_UNIT_PX, mHorizontalDot.getTextSize());
                            }

//                            TextView mVerticalSec = (TextView) getAdditionalInstanceField(miuiKeyguardClock, "mVerticalSec");
                            if (mVerticalSec != null) {
//                                mVerticalMin = (TextView) getObjectField(miuiKeyguardClock, "mVerticalMin");
                                mVerticalSec.setTextSize(TypedValue.COMPLEX_UNIT_PX, mVerticalMin.getTextSize());
                            }
                        } catch (Throwable t) {
                            XLog.e("", t);
                        }
                    }
                });
    }

    // com.android.keyguard.MiuiKeyguardClock#updateTime()
    private void hookUpdateTime() {
        findAndHookMethod(mMiuiKeyguardClockCls,
                "updateTime",
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        try {
                            int sec = Calendar.getInstance().get(Calendar.SECOND);
                            String secStr = String.format(Locale.getDefault(), "%02d", sec);

//                            TextView mHorizontalSec = (TextView) getAdditionalInstanceField(miuiKeyguardClock, "mHorizontalSec");
                            if (mHorizontalSec != null) {
                                mHorizontalSec.setText(secStr);
                            }

//                            TextView mVerticalSec = (TextView) getAdditionalInstanceField(miuiKeyguardClock, "mVerticalSec");
                            if (mVerticalSec != null) {
                                mVerticalSec.setText(secStr);
                            }
                        } catch (Throwable t) {
                            XLog.e("", t);
                        }
                    }
                });
    }

    // com.android.keyguard.MiuiKeyguardClock#access()
    private void hookConstructor() {
        hookAllConstructors(mMiuiKeyguardClockCls,
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        try {
                            mSecondsHandler = new SecondsHandler(Looper.getMainLooper(), param.thisObject);
//                            mSecondsHandler.sendEmptyMessage(SecondsHandler.MSG_UPDATE_TIME);
                            mSecondsHandler.post(mSecondsTicker);

                            if (mShowVerticalSec) {
                                mVerticalToHorizontalAnim2 = new AnimatorSet();
                                mHorizontalToVerticalAnim2 = new AnimatorSet();
                            }

                            // register receiver
                            LinearLayout keyguardClock = (LinearLayout) param.thisObject;

                            IntentFilter filter = new IntentFilter();
                            filter.addAction(Intent.ACTION_SCREEN_ON);
                            filter.addAction(Intent.ACTION_USER_PRESENT);
                            filter.addAction(Intent.ACTION_SCREEN_OFF);

                            keyguardClock.getContext().registerReceiver(mScreenReceiver, filter);
                        } catch (Throwable e) {
                            XLog.e("", e);
                        }
                    }
                });
    }

    private SecondsHandler mSecondsHandler;

    private final Runnable mSecondsTicker = new Runnable() {

        @Override
        public void run() {
            long now = SystemClock.uptimeMillis();
            long next = now + (1000 - now % 1000);
            mSecondsHandler.postAtTime(this, next);
            XposedHelpers.callMethod(mSecondsHandler.mKeyguardClockObj, "updateTime");
        }
    };

    private static class SecondsHandler extends Handler {
        private Object mKeyguardClockObj;

        private SecondsHandler(Looper looper, Object keyguardClockObj) {
            super(looper);
            this.mKeyguardClockObj = keyguardClockObj;
        }
    }

    // com.android.keyguard.MiuiKeyguardClock#showHorizontalTime()
    private void hookShowHorizontalTime() {
        findAndHookMethod(mMiuiKeyguardClockCls,
                "showHorizontalTime",
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
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
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
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
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        if (mVerticalToHorizontalAnim2 == null) {
                            return;
                        }
                        try {
                            Object keyguardClock = param.thisObject;

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

    // com.android.keyguard.MiuiKeyguardClock#playHorizontalToVerticalAnim()
    private void hookPlayHorizontalToVerticalAnim() {
        findAndHookMethod(mMiuiKeyguardClockCls,
                "playHorizontalToVerticalAnim",
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        if (mHorizontalToVerticalAnim2 == null) {
                            return;
                        }
                        try {
                            Object keyguardClock = param.thisObject;

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
                            XLog.d("", t);
                        }
                    }
                });
    }

    // com.android.keyguard.MiuiKeyguardClock#clearAnim()
    private void hookClearAnim() {
        findAndHookMethod(mMiuiKeyguardClockCls,
                "clearAnim",
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        if (mVerticalToHorizontalAnim2 != null) {
                            mVerticalToHorizontalAnim2.cancel();
                            if (mVerticalSec != null) {
                                mVerticalSec.clearAnimation();
                            }
                        }
                    }
                });
    }

    private final BroadcastReceiver mScreenReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Intent.ACTION_SCREEN_ON.equals(action)) {
                if (mSecondsHandler != null) {
                    mSecondsHandler.post(mSecondsTicker);
                }
            } else if (Intent.ACTION_USER_PRESENT.equals(action)
                    || Intent.ACTION_SCREEN_OFF.equals(action)) {
                if (mSecondsHandler != null) {
                    mSecondsHandler.removeCallbacks(mSecondsTicker);
                }
            }
        }
    };

}
