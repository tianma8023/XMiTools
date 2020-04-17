package com.tianma.tweaks.miui.data.http.service;

import com.tianma.tweaks.miui.data.http.entity.Poem;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface PoemService {

    @GET("/{category}")
    Observable<Poem> getPoem(@Path("category") String category);

}
