package com.tianma.tweaks.miui.app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.afollestad.materialdialogs.MaterialDialog;
import com.tianma.tweaks.miui.BuildConfig;
import com.tianma.tweaks.miui.R;
import com.tianma.tweaks.miui.app.base.BasePreferenceFragment;
import com.tianma.tweaks.miui.cons.AppConst;
import com.tianma.tweaks.miui.cons.PrefConst;
import com.tianma.tweaks.miui.utils.ContextUtils;
import com.tianma.tweaks.miui.utils.ModuleUtils;
import com.tianma.tweaks.miui.utils.PackageUtils;
import com.tianma.tweaks.miui.utils.RootUtils;
import com.tianma.tweaks.miui.utils.StorageUtils;
import com.tianma.tweaks.miui.utils.Utils;

import java.io.File;

import androidx.annotation.Nullable;
import androidx.preference.Preference;

public class MainSettingsFragment extends BasePreferenceFragment
        implements Preference.OnPreferenceClickListener, Preference.OnPreferenceChangeListener {

    private Activity mActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        super.onCreatePreferences(savedInstanceState, rootKey);
        addPreferencesFromResource(R.xml.main_settings);

        findPreference(PrefConst.HIDE_LAUNCHER_ICON).setOnPreferenceChangeListener(this);
        findPreference(PrefConst.STATUS_BAR_CLOCK_FORMAT).setOnPreferenceChangeListener(this);
        findPreference(PrefConst.CUSTOM_MOBILE_NETWORK_TYPE).setOnPreferenceChangeListener(this);

        findPreference(PrefConst.SOURCE_CODE).setOnPreferenceClickListener(this);
        findPreference(PrefConst.DONATE_BY_ALIPAY).setOnPreferenceClickListener(this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mActivity = requireActivity();
    }

    @Override
    public void onResume() {
        super.onResume();

        showVersionInfo();
        showModuleStatus();

        Preference timeFormatPref = findPreference(PrefConst.STATUS_BAR_CLOCK_FORMAT);
        SharedPreferences sp = getPreferenceManager().getSharedPreferences();
        String timeFormat = sp.getString(PrefConst.STATUS_BAR_CLOCK_FORMAT, PrefConst.STATUS_BAR_CLOCK_FORMAT_DEFAULT);
        showStatusBarClockFormat(timeFormatPref, timeFormat);

        Preference networkTypePref = findPreference(PrefConst.CUSTOM_MOBILE_NETWORK_TYPE);
        String networkType = sp.getString(PrefConst.CUSTOM_MOBILE_NETWORK_TYPE, PrefConst.CUSTOM_MOBILE_NETWORK_TYPE_DEFAULT);
        showCustomMobileNetworkType(networkTypePref, networkType);
    }

    @Override
    public void onPause() {
        super.onPause();
        setPreferenceWorldWritable();
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        String key = preference.getKey();
        if (PrefConst.SOURCE_CODE.equals(key)) {
            showSourceCode();
        } else if (PrefConst.DONATE_BY_ALIPAY.equals(key)) {
            donateByAlipay();
        } else {
            return false;
        }
        return true;
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        String key = preference.getKey();
        if (PrefConst.HIDE_LAUNCHER_ICON.equals(key)) {
            hideOrShowLauncherIcon((Boolean) newValue);
        } else if (PrefConst.STATUS_BAR_CLOCK_FORMAT.equals(key)) {
            showStatusBarClockFormat(preference, (String) newValue);
        } else if (PrefConst.CUSTOM_MOBILE_NETWORK_TYPE.equals(key)) {
            showCustomMobileNetworkType(preference, (String) newValue);
        } else {
            return false;
        }
        return true;
    }

    private void showVersionInfo() {
        findPreference(PrefConst.APP_VERSION).setSummary(BuildConfig.VERSION_NAME);
    }

    private void showSourceCode() {
        Utils.showWebPage(getActivity(), AppConst.PROJECT_SOURCE_CODE_URL);
    }

    private void showModuleStatus() {
        Preference preference = findPreference(PrefConst.MODULE_STATUS);
        if (ModuleUtils.isModuleActive()) {
            preference.setSummary(R.string.module_status_active);
        } else {
            preference.setSummary(R.string.module_status_inactive);
        }
    }

    private void showStatusBarClockFormat(Preference preference, String newValue) {
        preference.setSummary(newValue);
    }

    private void showCustomMobileNetworkType(Preference preference, String newValue) {
        preference.setSummary(newValue);
    }

    private void hideOrShowLauncherIcon(boolean hide) {
        PackageManager pm = mActivity.getPackageManager();
        ComponentName launcherCN = new ComponentName(mActivity, AppConst.MAIN_ACTIVITY_ALIAS);
        int state = hide ? PackageManager.COMPONENT_ENABLED_STATE_DISABLED : PackageManager.COMPONENT_ENABLED_STATE_ENABLED;
        if (pm.getComponentEnabledSetting(launcherCN) != state) {
            pm.setComponentEnabledSetting(launcherCN, state, PackageManager.DONT_KILL_APP);
        }
    }

    private void donateByAlipay() {
        PackageUtils.startAlipayDonatePage(mActivity);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_reboot_system:
                performRebootSystem();
                break;
            case R.id.action_soft_reboot_system:
                preformSoftRebootSystem();
                break;
            case R.id.action_restart_host_apps:
                performRestartHostApps();
                break;
            case R.id.action_taichi_users_notice:
                showTaiChiUsersNotice();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void performRebootSystem() {
        new MaterialDialog.Builder(mActivity)
                .title(R.string.action_reboot_system)
                .content(R.string.prompt_reboot_system_message)
                .positiveText(R.string.confirm)
                .onPositive(((dialog, which) -> RootUtils.reboot()))
                .negativeText(R.string.cancel)
                .show();
    }

    private void preformSoftRebootSystem() {
        new MaterialDialog.Builder(mActivity)
                .title(R.string.action_soft_reboot_system)
                .content(R.string.prompt_soft_reboot_message)
                .positiveText(R.string.confirm)
                .onPositive(((dialog, which) -> RootUtils.softReboot()))
                .negativeText(R.string.cancel)
                .show();
    }

    private void performRestartHostApps() {
        new MaterialDialog.Builder(mActivity)
                .title(R.string.action_restart_host_apps)
                .content(R.string.prompt_restart_host_apps_message)
                .positiveText(R.string.confirm)
                .onPositive((dialog, which) -> {
                    RootUtils.restartSystemUI();
                    RootUtils.killAllMiuiLauncher();
                })
                .negativeText(R.string.cancel)
                .show();
    }

    private void showTaiChiUsersNotice() {
        new MaterialDialog.Builder(mActivity)
                .title(R.string.action_taichi_users_notice)
                .content(R.string.prompt_taichi_users_notice_message)
                .positiveText(R.string.check_module)
                .onPositive((dialog, which) -> PackageUtils.startCheckModuleInTaiChi(mActivity))
                .negativeText(R.string.add_applications)
                .onNegative((dialog, which) -> PackageUtils.startAddAppsInTaiChi(mActivity))
                .show();
    }

    @SuppressLint({"SetWorldReadable", "SetWorldWritable"})
    private void setPreferenceWorldWritable() {
        Context context;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
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
