package com.tianma.tweaks.miui.app;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.material.tabs.TabLayout;
import com.tianma.tweaks.miui.R;
import com.tianma.tweaks.miui.app.base.BaseActivity;
import com.tianma.tweaks.miui.app.base.BasePreferenceFragment;
import com.tianma.tweaks.miui.utils.PackageUtils;
import com.tianma.tweaks.miui.utils.RootUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setupToolbar();

        ViewPager viewPager = findViewById(R.id.view_pager);
        TabLayout tabLayout = findViewById(R.id.tab_layout);

        List<BasePreferenceFragment> fragments = new ArrayList<>();
        fragments.add(new GeneralSettingsFragment(getString(R.string.pref_general_title)));
        fragments.add(new StatusBarSettingsFragment(getString(R.string.pref_status_bar_title)));
        fragments.add(new DropDownStatusBarSettingsFragment(getString(R.string.pref_dropdown_status_bar_title)));
        fragments.add(new KeyguardSettingsFragment(getString(R.string.pref_keyguard_title)));
        fragments.add(new AboutSettingsFragment(getString(R.string.pref_about_title)));

        final SettingsFragmentPagerAdapter pagerAdapter = new SettingsFragmentPagerAdapter(getSupportFragmentManager(), fragments);

        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupToolbar() {
        setSupportActionBar(mToolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
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
        new MaterialDialog.Builder(this)
                .title(R.string.action_reboot_system)
                .content(R.string.prompt_reboot_system_message)
                .positiveText(R.string.confirm)
                .onPositive(((dialog, which) -> RootUtils.reboot()))
                .negativeText(R.string.cancel)
                .show();
    }

    private void preformSoftRebootSystem() {
        new MaterialDialog.Builder(this)
                .title(R.string.action_soft_reboot_system)
                .content(R.string.prompt_soft_reboot_message)
                .positiveText(R.string.confirm)
                .onPositive(((dialog, which) -> RootUtils.softReboot()))
                .negativeText(R.string.cancel)
                .show();
    }

    private void performRestartHostApps() {
        new MaterialDialog.Builder(this)
                .title(R.string.action_restart_host_apps)
                .content(R.string.prompt_restart_host_apps_message)
                .positiveText(R.string.confirm)
                .onPositive((dialog, which) -> {
                    RootUtils.restartSystemUI();
                })
                .negativeText(R.string.cancel)
                .show();
    }

    private void showTaiChiUsersNotice() {
        new MaterialDialog.Builder(this)
                .title(R.string.action_taichi_users_notice)
                .content(R.string.prompt_taichi_users_notice_message)
                .positiveText(R.string.check_module)
                .onPositive((dialog, which) -> PackageUtils.startCheckModuleInTaiChi(this))
                .negativeText(R.string.add_applications)
                .onNegative((dialog, which) -> PackageUtils.startAddAppsInTaiChi(this))
                .show();
    }
}
