package com.tianma.tweaks.miui.app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.preference.Preference;

import com.tianma.tweaks.miui.R;
import com.tianma.tweaks.miui.app.base.BasePreferenceFragment;
import com.tianma.tweaks.miui.cons.AppConst;
import com.tianma.tweaks.miui.cons.PrefConst;
import com.tianma.tweaks.miui.utils.ContextUtils;
import com.tianma.tweaks.miui.utils.ModuleUtils;
import com.tianma.tweaks.miui.utils.StorageUtils;

import java.io.File;

public class GeneralSettingsFragment extends BasePreferenceFragment implements Preference.OnPreferenceChangeListener {

    private Activity mActivity;

    public GeneralSettingsFragment() {
    }

    public GeneralSettingsFragment(CharSequence title) {
        super(title);
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        super.onCreatePreferences(savedInstanceState, rootKey);
        addPreferencesFromResource(R.xml.main_settings);

        findPreference(PrefConst.HIDE_LAUNCHER_ICON).setOnPreferenceChangeListener(this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mActivity = requireActivity();
    }

    @Override
    public void onResume() {
        super.onResume();

        showModuleStatus();
    }

    @Override
    public void onPause() {
        super.onPause();
        setPreferenceWorldWritable();
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        String key = preference.getKey();
        if (PrefConst.HIDE_LAUNCHER_ICON.equals(key)) {
            hideOrShowLauncherIcon((Boolean) newValue);
        } else {
            return false;
        }
        return true;
    }

    private void showModuleStatus() {
        Preference preference = findPreference(PrefConst.MODULE_STATUS);
        if (ModuleUtils.isModuleActive()) {
            preference.setSummary(R.string.module_status_active);
        } else {
            preference.setSummary(R.string.module_status_inactive);
        }
    }

    private void hideOrShowLauncherIcon(boolean hide) {
        PackageManager pm = mActivity.getPackageManager();
        ComponentName launcherCN = new ComponentName(mActivity, AppConst.MAIN_ACTIVITY_ALIAS);
        int state = hide ? PackageManager.COMPONENT_ENABLED_STATE_DISABLED : PackageManager.COMPONENT_ENABLED_STATE_ENABLED;
        if (pm.getComponentEnabledSetting(launcherCN) != state) {
            pm.setComponentEnabledSetting(launcherCN, state, PackageManager.DONT_KILL_APP);
        }
    }

    @SuppressLint({"SetWorldReadable", "SetWorldWritable"})
    private void setPreferenceWorldWritable() {
        Context context;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // API >= 24 (Android 7.0+)
            // dataDir: /data/user_de/0/<package_name>/
            // spDir: /data/user_de/0/<package_name>/shared_prefs/
            // spFile: /data/user_de/0/<package_name>/shared_prefs/<preferences_name>.xml
            context = ContextUtils.getProtectedContext(mActivity.getApplicationContext());
        } else {
            // API < 24, there is no data encrypt.
            // dataDir: /data/data/<package_name>/
            context = mActivity.getApplicationContext();
        }
        File prefsFile = StorageUtils.getSharedPreferencesFile(context, AppConst.X_MIUI_CLOCK_PREFS_NAME);
        StorageUtils.setFileWorldWritable(prefsFile, 2);
    }
}
