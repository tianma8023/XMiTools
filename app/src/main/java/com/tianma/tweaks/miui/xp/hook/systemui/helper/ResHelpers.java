package com.tianma.tweaks.miui.xp.hook.systemui.helper;

import android.content.res.Resources;
import android.util.ArrayMap;

import com.tianma.tweaks.miui.xp.hook.systemui.SystemUIHook;

public class ResHelpers {

    private static ArrayMap<String, Integer> sNameIdMap;

    static {
        sNameIdMap = new ArrayMap<>();
    }

    public static Integer getId(Resources res, String name) {
        if (!sNameIdMap.containsKey(name)) {
            int id = res.getIdentifier(name, "id", SystemUIHook.PACKAGE_NAME);
            sNameIdMap.put(name, id);
        }
        return sNameIdMap.get(name);
    }

}
