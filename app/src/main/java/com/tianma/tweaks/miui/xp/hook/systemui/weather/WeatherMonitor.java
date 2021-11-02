package com.tianma.tweaks.miui.xp.hook.systemui.weather;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import com.tianma.tweaks.miui.R;
import com.tianma.tweaks.miui.cons.AppConst;
import com.tianma.tweaks.miui.utils.PackageUtils;
import com.tianma.tweaks.miui.utils.XLogKt;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WeatherMonitor {

    private static WeatherMonitor sWeatherMonitor;

    private Context mAppContext;
    private Handler mHandler;

    private List<WeatherObserver> mObserverList;

    private ContentResolver mContentResolver;
    private WeatherContentObserver mContentObserver;
    private Uri mContentUri;

    private ExecutorService mThreadPool;

    private static final String COL_CITY_NAME = "city_name";
    private static final String COL_DESCRIPTION = "description";
    private static final String COL_TEMPERATURE = "temperature";

    private static final int MSG_WEATHER_INFO = 0xf0;

    private WeatherMonitor(Context appContext) {
        mAppContext = appContext;
        mObserverList = new ArrayList<>();
        mHandler = new WeatherHandler(Looper.getMainLooper());
        mContentResolver = mAppContext.getContentResolver();
        mContentObserver = new WeatherContentObserver(mHandler);
        mContentUri = Uri.parse("content://weather/weather");
        mThreadPool = Executors.newCachedThreadPool();
    }

    public static WeatherMonitor get(Context appContext) {
        if (sWeatherMonitor == null) {
            synchronized (WeatherMonitor.class) {
                if (sWeatherMonitor == null) {
                    sWeatherMonitor = new WeatherMonitor(appContext);
                }
            }
        }
        return sWeatherMonitor;
    }

    private class WeatherContentObserver extends ContentObserver {

        public WeatherContentObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            mThreadPool.submit(new DataParser());
        }
    }

    private class DataParser implements Runnable {

        @Override
        public void run() {
            parseData();
        }

        private void parseData() {
            Cursor c = null;
            String weatherInfo = "";
            try {
                c = mContentResolver.query(mContentUri, null, null, null, null);
                if (c != null) {
                    if (c.moveToNext()) {
                        String cityName = c.getString(c.getColumnIndex(COL_CITY_NAME));
                        String description = c.getString(c.getColumnIndex(COL_DESCRIPTION));
                        String temperature = c.getString(c.getColumnIndex(COL_TEMPERATURE));
                        weatherInfo = cityName + " "
                                + description + " "
                                + temperature;
                    }
                }
            } catch (Throwable t) {
                XLogKt.logE("", t);
            } finally {
                if (c != null) {
                    c.close();
                }
            }
            weatherInfo = handleWeatherInfo(weatherInfo);
            Message msg = Message.obtain(mHandler, MSG_WEATHER_INFO, weatherInfo);
            mHandler.sendMessage(msg);
        }

        private String handleWeatherInfo(String weatherInfo) {
            if (TextUtils.isEmpty(weatherInfo)) {
                int state = PackageUtils.checkPackageState(mAppContext, AppConst.MIUI_WEATHER_PACKAGE);
                if (state == PackageUtils.PACKAGE_NOT_INSTALLED) {
                    weatherInfo = mAppContext.getString(R.string.miui_weather_install_prompt);
                } else if (state == PackageUtils.PACKAGE_DISABLED) {
                    weatherInfo = mAppContext.getString(R.string.miui_weather_enable_prompt);
                } else {
                    weatherInfo = mAppContext.getString(R.string.no_weather_info);
                }
            }
            return weatherInfo;
        }
    }

    private class WeatherHandler extends Handler {

        private WeatherHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MSG_WEATHER_INFO) {
                notifyObservers((String) msg.obj);
            }
        }
    }

    private void notifyObservers(String weatherInfo) {
        for (WeatherObserver observer : mObserverList) {
            observer.onWeatherChanged(weatherInfo);
        }
    }

    public synchronized void registerObserver(WeatherObserver observer) {
        if (mObserverList.isEmpty()) {
            startMonitor();
        }

        if (!mObserverList.contains(observer)) {
            mObserverList.add(observer);
        }
    }

    public synchronized void unregisterObserver(WeatherObserver observer) {
        mObserverList.remove(observer);

        if (mObserverList.isEmpty()) {
            stopMonitor();
        }
    }

    private void startMonitor() {
        mContentResolver.registerContentObserver(mContentUri, true, mContentObserver);
        mContentObserver.onChange(true);
    }

    private void stopMonitor() {
        mContentResolver.unregisterContentObserver(mContentObserver);
    }
}
