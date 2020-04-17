package com.tianma.tweaks.miui.data.http.service;

import android.util.ArrayMap;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Retrofit Service Generator
 */
public class ServiceGenerator {

    private ArrayMap<String, Retrofit> mRetrofitMap;

    private OkHttpClient mOkHttpClient;

    private static class InstanceHolder {
        private static final ServiceGenerator INSTANCE = new ServiceGenerator();
    }

    private ServiceGenerator() {
        mRetrofitMap = new ArrayMap<>();
        mOkHttpClient = new OkHttpClient();
    }

    public static ServiceGenerator getInstance() {
        return InstanceHolder.INSTANCE;
    }

    public <T> T createService(String baseUrl, Class<T> serviceClass) {
        Retrofit retrofit = mRetrofitMap.get(baseUrl);
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .client(mOkHttpClient)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();
            mRetrofitMap.put(baseUrl, retrofit);
        }
        return retrofit.create(serviceClass);
    }

}
