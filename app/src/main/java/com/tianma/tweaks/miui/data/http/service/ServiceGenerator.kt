package com.tianma.tweaks.miui.data.http.service

import android.util.ArrayMap
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

/**
 * Retrofit Service Generator
 */
class ServiceGenerator private constructor() {
    private val mRetrofitMap: ArrayMap<String, Retrofit?> = ArrayMap()
    private val mOkHttpClient: OkHttpClient = OkHttpClient()

    private object InstanceHolder {
        val INSTANCE = ServiceGenerator()
    }

    fun <T> createService(baseUrl: String, serviceClass: Class<T>): T {
        var retrofit = mRetrofitMap[baseUrl]
        if (retrofit == null) {
            retrofit = Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .client(mOkHttpClient)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build()
            mRetrofitMap[baseUrl] = retrofit
        }
        return retrofit!!.create(serviceClass)
    }

    companion object {
        val instance: ServiceGenerator
            get() = InstanceHolder.INSTANCE
    }

}