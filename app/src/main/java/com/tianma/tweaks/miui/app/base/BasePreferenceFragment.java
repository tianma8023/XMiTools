package com.tianma.tweaks.miui.app.base;

import android.os.Bundle;

import com.tianma.tweaks.miui.cons.AppConst;

import androidx.annotation.NonNull;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

public abstract class BasePreferenceFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        PreferenceManager pm = getPreferenceManager();
        pm.setSharedPreferencesName(AppConst.X_MIUI_CLOCK_PREFS_NAME);
    }

    @NonNull
    @Override
    public <T extends Preference> T findPreference(@NonNull CharSequence key) {
        PreferenceManager pm = getPreferenceManager();
        return pm.findPreference(key);
    }
}
