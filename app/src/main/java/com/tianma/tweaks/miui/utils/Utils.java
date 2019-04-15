package com.tianma.tweaks.miui.utils;

import android.content.Context;
import android.net.Uri;
import android.widget.Toast;

import com.tianma.tweaks.miui.R;

import androidx.browser.customtabs.CustomTabsIntent;

/**
 * Other Utils
 */
public class Utils {

    private Utils() {
    }

    public static void showWebPage(Context context, String url) {
        try {
            CustomTabsIntent cti = new CustomTabsIntent.Builder().build();
            cti.launchUrl(context, Uri.parse(url));
        } catch (Exception e) {
            Toast.makeText(context, R.string.browser_install_or_enable_prompt, Toast.LENGTH_SHORT).show();
        }
    }
}
