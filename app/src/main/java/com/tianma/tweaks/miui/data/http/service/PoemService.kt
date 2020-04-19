package com.tianma.tweaks.miui.data.http.service

import com.tianma.tweaks.miui.data.http.entity.Poem
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path

interface PoemService {
    @GET("/{category}")
    fun getPoem(@Path("category") category: String): Observable<Poem?>
}