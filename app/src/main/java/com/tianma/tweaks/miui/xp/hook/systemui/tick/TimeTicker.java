package com.tianma.tweaks.miui.xp.hook.systemui.tick;

import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;

import java.util.ArrayList;
import java.util.List;

public class TimeTicker {

    private List<TickObserver> mObserverList;

    private volatile static TimeTicker sTimeTicker;

    private Handler mSecondsHandler = new Handler(Looper.getMainLooper());

    private TimeTicker() {
        mObserverList = new ArrayList<>();
    }

    private Runnable mSecondsTicker = new Runnable() {

        @Override
        public void run() {
            long now = SystemClock.uptimeMillis();
            long next = now + (1000 - now % 1000);
            mSecondsHandler.postAtTime(this, next);
            for (TickObserver observer : mObserverList) {
                if (observer != null) {
                    observer.onTimeTick();
                }
            }
        }
    };

    public static TimeTicker get() {
        if (sTimeTicker == null) {
            synchronized (TimeTicker.class) {
                if (sTimeTicker == null) {
                    sTimeTicker = new TimeTicker();
                }
            }
        }
        return sTimeTicker;
    }

    public synchronized void registerObserver(TickObserver observer) {
        if (mObserverList.isEmpty()) {
            startTick();
        }

        if (!mObserverList.contains(observer)) {
            mObserverList.add(observer);
        }
    }

    public synchronized void unregisterObserver(TickObserver observer) {
        mObserverList.remove(observer);

        if (mObserverList.isEmpty()) {
            stopTick();
        }
    }

    private void startTick() {
        mSecondsHandler.post(mSecondsTicker);
    }

    private void stopTick() {
        mSecondsHandler.removeCallbacks(mSecondsTicker);
    }

}
