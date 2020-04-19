package com.tianma.tweaks.miui.data.http.service

import com.tianma.tweaks.miui.data.http.entity.Hitokoto
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * 一言API
 */
interface HitokotoService {
    @GET("/")
    fun getHitokoto(@Query("c") categories: List<String>): Observable<Hitokoto?>
}