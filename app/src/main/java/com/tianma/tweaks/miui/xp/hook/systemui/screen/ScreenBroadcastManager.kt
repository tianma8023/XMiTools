package com.tianma.tweaks.miui.xp.hook.systemui.screen;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.tianma.tweaks.miui.xp.hook.systemui.keyguard.IntentAction;
import com.tianma.tweaks.miui.xp.hook.systemui.tick.TimeTicker;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class ScreenBroadcastManager {

    private volatile static ScreenBroadcastManager sInstance;

    private List<ScreenListener> listeners = new ArrayList<>();

    private WeakReference<Context> contextWeakReference;

    private ScreenBroadcastManager(Context context) {
        contextWeakReference = new WeakReference<>(context.getApplicationContext());
    }

    public static ScreenBroadcastManager getInstance(Context modContext) {
        if (sInstance == null) {
            synchronized (TimeTicker.class) {
                if (sInstance == null) {
                    sInstance = new ScreenBroadcastManager(modContext);
                }
            }
        }
        return sInstance;
    }

    public synchronized void registerListener(ScreenListener screenListener) {
        if (listeners.isEmpty()) {
            registerBroadcastReceiver();
        }

        if (!listeners.contains(screenListener)) {
            listeners.add(screenListener);
        }
    }

    public synchronized void unregisterListener(ScreenListener screenListener) {
        listeners.remove(screenListener);

        if (listeners.isEmpty()) {
            unregisterBroadcastReceiver();
        }
    }

    private void registerBroadcastReceiver() {
        Context context = contextWeakReference.get();
        if (context != null) {
            // register receiver
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_SCREEN_ON);
            filter.addAction(Intent.ACTION_USER_PRESENT);
            filter.addAction(Intent.ACTION_SCREEN_OFF);
            filter.addAction(IntentAction.KEYGUARD_STOP_TIME_TICK);

            context.registerReceiver(screenReceiver, filter);
        }
    }

    private void unregisterBroadcastReceiver() {
        Context context = contextWeakReference.get();
        if (context != null) {
            context.unregisterReceiver(screenReceiver);
        }
    }

    private final BroadcastReceiver screenReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            notifyListeners(action);
        }
    };

    private void notifyListeners(String action) {
        boolean screenOn = Intent.ACTION_SCREEN_ON.equals(action);
        boolean userPresent = Intent.ACTION_USER_PRESENT.equals(action);
        boolean screenOff = Intent.ACTION_SCREEN_OFF.equals(action);
        boolean stopTimeTick = IntentAction.KEYGUARD_STOP_TIME_TICK.equals(action);

        for (ScreenListener listener : listeners) {
            if (screenOn) {
                listener.onScreenOn();
            } else if (userPresent) {
                listener.onUserPresent();
            } else if (screenOff) {
                listener.onScreenOff();
            } else if (stopTimeTick) {
                listener.onStopTimeTick();
            }
        }
    }
}
