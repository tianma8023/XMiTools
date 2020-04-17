package com.tianma.tweaks.miui.data.http.repository;

import com.tianma.tweaks.miui.data.http.APIConst;
import com.tianma.tweaks.miui.data.http.entity.Hitokoto;
import com.tianma.tweaks.miui.data.http.entity.Poem;
import com.tianma.tweaks.miui.data.http.service.HitokotoService;
import com.tianma.tweaks.miui.data.http.service.PoemService;
import com.tianma.tweaks.miui.data.http.service.ServiceGenerator;

import java.util.List;

import io.reactivex.Observable;

public class DataRepository {

    private DataRepository() {
    }

    public static Observable<Hitokoto> getHitokoto(List<String> categories) {
        HitokotoService hitokotoService = ServiceGenerator.getInstance()
                .createService(APIConst.HITOKOTO_BASE_URL, HitokotoService.class);
        return hitokotoService.getHitokoto(categories);
    }

    public static Observable<Poem> getPoem(String category) {
        PoemService poemService = ServiceGenerator.getInstance()
                .createService(APIConst.POEM_BASE_URL, PoemService.class);

        return poemService.getPoem(category);
    }

}
