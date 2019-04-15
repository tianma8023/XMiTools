package com.tianma.tweaks.miui.xp.hook.systemui;

import android.content.Context;
import android.content.Intent;

public interface BroadcastSubReceiver {

    void onBroadcastReceived(Context context, Intent intent);

}
