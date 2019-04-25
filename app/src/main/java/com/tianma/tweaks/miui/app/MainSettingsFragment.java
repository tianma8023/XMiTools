package com.tianma.tweaks.miui.app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.afollestad.materialdialogs.MaterialDialog;
import com.tianma.tweaks.miui.BuildConfig;
import com.tianma.tweaks.miui.R;
import com.tianma.tweaks.miui.cons.AppConst;
import com.tianma.tweaks.miui.cons.PrefConst;
import com.tianma.tweaks.miui.utils.ModuleUtils;
import com.tianma.tweaks.miui.utils.PackageUtils;
import com.tianma.tweaks.miui.utils.RootUtils;
import com.tianma.tweaks.miui.utils.StorageUtils;
import com.tianma.tweaks.miui.utils.Utils;

import java.io.File;

import androidx.annotation.Nullable;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

public class MainSettingsFragment extends PreferenceFragmentCompat
        implements Preference.OnPreferenceClickListener, Preference.OnPreferenceChangeListener {

    private Activity mActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.main_settings);

        findPreference(PrefConst.HIDE_LAUNCHER_ICON).setOnPreferenceChangeListener(this);

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
        showStatusBarClockFormat();
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

    private void showStatusBarClockFormat() {
        Preference preference = findPreference(PrefConst.STATUS_BAR_CLOCK_FORMAT);
        SharedPreferences sp = getPreferenceManager().getSharedPreferences();
        String timeFormat = sp.getString(PrefConst.STATUS_BAR_CLOCK_FORMAT, PrefConst.STATUS_BAR_CLOCK_FORMAT_DEFAULT);
        preference.setSummary(timeFormat);
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
            case R.id.action_restart_system_ui:
                performRestartSystemUI();
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

    private void performRestartSystemUI() {
        new MaterialDialog.Builder(mActivity)
                .title(R.string.action_restart_system_ui)
                .content(R.string.prompt_restart_system_ui_message)
                .positiveText(R.string.confirm)
                .onPositive((dialog, which) -> RootUtils.restartSystemUI())
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
        // dataDir: /data/data/<package_name>/
        // spDir: /data/data/<package_name>/shared_prefs/
        // spFile: /data/data/<package_name>/shared_prefs/<preferences_name>.xml
        String preferencesName = getPreferenceManager().getSharedPreferencesName();
        File prefsFile = StorageUtils.getSharedPreferencesFile(mActivity, preferencesName);
        StorageUtils.setFileWorldWritable(prefsFile, 2);
    }
}
