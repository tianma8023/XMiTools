package com.tianma.tweaks.miui.xp.hook.systemui.keyguard.v20190507;

import static com.tianma.tweaks.miui.xp.wrapper.XposedWrapper.hookAllConstructors;

import android.content.Context;
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
import com.tianma.tweaks.miui.xp.hook.systemui.hitokoto.OneSentenceManager;
import com.tianma.tweaks.miui.xp.hook.systemui.screen.ScreenBroadcastManager;
import com.tianma.tweaks.miui.xp.hook.systemui.screen.SimpleScreenListener;
import com.tianma.tweaks.miui.xp.wrapper.MethodHookWrapper;
import com.tianma.tweaks.miui.xp.wrapper.XposedWrapper;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import de.robv.android.xposed.XposedHelpers;
import io.reactivex.disposables.CompositeDisposable;

/**
 * MIUI 锁屏界面 Hook
 * com.android.keyguard.MiuiKeyguardBaseClock
 */
public class MiuiKeyguardBaseClockHook extends BaseSubHook {

    private static final String CLASS_MIUI_KEYGUARD_BASE_CLOCK = "com.android.keyguard.MiuiKeyguardBaseClock";

    private Class<?> mKeyguardBaseClockClass;

    private List<View> mKeyguardClockList = new ArrayList<>();

    private CompositeDisposable mCompositeDisposable;

    private Context modContext;

    private boolean oneSentenceEnabled;
    private int oneSentenceColor;
    private float oneSentenceTextSize;

    public MiuiKeyguardBaseClockHook(ClassLoader classLoader) {
        super(classLoader);
        mCompositeDisposable = new CompositeDisposable();
        oneSentenceEnabled = XPrefContainer.getOneSentenceEnabled();
        oneSentenceColor = XPrefContainer.getOneSentenceColor();
        oneSentenceTextSize = XPrefContainer.getOneSentenceTextSize();
    }

    @Override
    public void startHook() {
        try {
            mKeyguardBaseClockClass = XposedWrapper.findClass(CLASS_MIUI_KEYGUARD_BASE_CLOCK, getMClassLoader());
            if (oneSentenceEnabled) {
                XLogKt.logD("OneSentence enabled, hooking MiuiKeyguardBaseClock...");

                hookOnFinishInflate();
                hookHandleNotificationChange();
                hookConstructor();
            }
        } catch (Throwable t) {
            XLogKt.logE("Error occurs when hook MiuiKeyguardBaseClock", t);
        }
    }

    private void hookOnFinishInflate() {
        XposedWrapper.findAndHookMethod(mKeyguardBaseClockClass,
                "onFinishInflate",
                new MethodHookWrapper() {
                    @Override
                    protected void after(MethodHookParam param) {
                        LinearLayout miuiKeyguardBaseClock = (LinearLayout) param.thisObject;
                        Context context = miuiKeyguardBaseClock.getContext();

                        TextView mOwnerInfo = (TextView) XposedHelpers.getObjectField(miuiKeyguardBaseClock, "mOwnerInfo");
                        int mOwnerInfoIndex = miuiKeyguardBaseClock.indexOfChild(mOwnerInfo);

                        TextView hitokotoTextView = new TextView(context);
                        hitokotoTextView.setTextSize(oneSentenceTextSize);
                        hitokotoTextView.setTextColor(oneSentenceColor);
                        hitokotoTextView.setId(R.id.hitokoto_info_text_view);

                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        hitokotoTextView.setLayoutParams(params);
                        params.gravity = Gravity.CENTER;
                        params.topMargin = (int) ResolutionUtils.dp2px(miuiKeyguardBaseClock.getContext(), 8.95f);

                        miuiKeyguardBaseClock.addView(hitokotoTextView, mOwnerInfoIndex + 1);
                    }
                });
    }

    private void hookHandleNotificationChange() {
        XposedWrapper.findAndHookMethod(mKeyguardBaseClockClass,
                "handleNotificationChange",
                new MethodHookWrapper() {
                    @Override
                    protected void after(MethodHookParam param) {
                        View keyguardClock = (View) param.thisObject;

                        boolean mHasNotification = XposedHelpers.getBooleanField(keyguardClock, "mHasNotification");

                        try {
                            TextView hitokotoInfo = keyguardClock.findViewById(R.id.hitokoto_info_text_view);
                            if (hitokotoInfo != null) {
                                hitokotoInfo.setVisibility(mHasNotification ? View.GONE : View.VISIBLE);
                            }
                        } catch (Throwable t) {
                            // ignore
                            XLogKt.logE("", t);
                        }
                    }
                });
    }

    // com.android.keyguard.MiuiKeyguardBaseClock#constructor()
    private void hookConstructor() {
        hookAllConstructors(mKeyguardBaseClockClass,
                new MethodHookWrapper() {
                    @Override
                    protected void after(MethodHookParam param) {
                        final LinearLayout keyguardClock = (LinearLayout) param.thisObject;
                        modContext = keyguardClock.getContext().getApplicationContext();

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
    }

    private synchronized void removeClock(View clock) {
        mKeyguardClockList.remove(clock);
    }

    private final SimpleScreenListener screenListener = new SimpleScreenListener() {
        @Override
        public void onScreenOn() {
            loadOneSentence();
        }

        @Override
        public void onUserPresent() {
            cancelLoadOneSentence();
        }

        @Override
        public void onScreenOff() {
            cancelLoadOneSentence();
        }
    };

    private void loadOneSentence() {
        if (modContext != null) {
            OneSentenceManager.getInstance().loadOneSentence(modContext, new OneSentenceManager.OneSentenceLoadListener() {
                @Override
                public void onSuccess(@NotNull String oneSentence) {
                    showOneSentence(oneSentence);
                }

                @Override
                public void onFailed(@NotNull Throwable throwable) {
                    XLogKt.logE("Error occurs when load OneSentence", throwable);
                }
            });
        }
    }

    private void showOneSentence(String oneSentence) {
        for (View keyguardClock : mKeyguardClockList) {
            try {
                TextView hitokotoInfo = keyguardClock.findViewById(R.id.hitokoto_info_text_view);
                hitokotoInfo.setText(oneSentence);
            } catch (Throwable t) {
                // ignore
                XLogKt.logE("", t);
            }
        }
    }

    private void cancelLoadOneSentence() {
        if (mCompositeDisposable.size() > 0) {
            mCompositeDisposable.clear();
        }
        OneSentenceManager.getInstance().cancelLoadOneSentence();
    }
}
