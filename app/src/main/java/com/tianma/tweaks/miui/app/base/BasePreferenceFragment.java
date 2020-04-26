package com.tianma.tweaks.miui.app.base;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.tianma.tweaks.miui.cons.AppConst;

public abstract class BasePreferenceFragment extends PreferenceFragmentCompat {

    private CharSequence title;

    public BasePreferenceFragment() {
        this("");
    }

    public BasePreferenceFragment(CharSequence title) {
        setTitle(title);
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        PreferenceManager pm = getPreferenceManager();
        pm.setSharedPreferencesName(AppConst.X_MIUI_CLOCK_PREFS_NAME);
    }

    public void setTitle(CharSequence title) {
        this.title = title;
    }

    final public CharSequence getTitle() {
        return title;
    }
}
