package com.tianma.tweaks.miui.data.http.service;


import com.tianma.tweaks.miui.data.http.entity.Hitokoto;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * 一言API
 */
public interface HitokotoService {

    @GET("/")
    Observable<Hitokoto> getHitokoto(@Query("c") List<String> categories);

}
