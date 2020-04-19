package com.tianma.tweaks.miui.app

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.afollestad.materialdialogs.MaterialDialog
import com.tianma.tweaks.miui.R
import com.tianma.tweaks.miui.app.base.BaseActivity
import com.tianma.tweaks.miui.app.base.BasePreferenceFragment
import com.tianma.tweaks.miui.app.fragment.*
import com.tianma.tweaks.miui.utils.ModuleUtils
import com.tianma.tweaks.miui.utils.PackageUtils
import com.tianma.tweaks.miui.utils.RootUtils
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.toolbar.*

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupToolbar()
        initFragments()
        showModuleStatus()
    }

    private fun setupToolbar() {
        setSupportActionBar(toolbar)
    }

    private fun initFragments() {
        val fragments = mutableListOf<BasePreferenceFragment>().apply {
            add(GeneralSettingsFragment(getString(R.string.pref_general_title)))
            add(StatusBarSettingsFragment(getString(R.string.pref_status_bar_title)))
            add(DropDownStatusBarSettingsFragment(getString(R.string.pref_dropdown_status_bar_title)))
            add(KeyguardSettingsFragment(getString(R.string.pref_keyguard_title)))
            add(AboutSettingsFragment(getString(R.string.pref_about_title)))
        }

        viewPager.adapter = SettingsFragmentPagerAdapter(supportFragmentManager, fragments)
        tabLayout.setupWithViewPager(viewPager)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_reboot_system -> performRebootSystem()
            R.id.action_soft_reboot_system -> preformSoftRebootSystem()
            R.id.action_restart_host_apps -> performRestartHostApps()
            R.id.action_taichi_users_notice -> showTaiChiUsersNotice()
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    private fun performRebootSystem() {
        MaterialDialog.Builder(this)
                .title(R.string.action_reboot_system)
                .content(R.string.prompt_reboot_system_message)
                .positiveText(R.string.confirm)
                .onPositive { _, _ -> RootUtils.reboot() }
                .negativeText(R.string.cancel)
                .show()
    }

    private fun preformSoftRebootSystem() {
        MaterialDialog.Builder(this)
                .title(R.string.action_soft_reboot_system)
                .content(R.string.prompt_soft_reboot_message)
                .positiveText(R.string.confirm)
                .onPositive { _, _ -> RootUtils.softReboot() }
                .negativeText(R.string.cancel)
                .show()
    }

    private fun performRestartHostApps() {
        MaterialDialog.Builder(this)
                .title(R.string.action_restart_host_apps)
                .content(R.string.prompt_restart_host_apps_message)
                .positiveText(R.string.confirm)
                .onPositive { _, _ -> RootUtils.restartSystemUI() }
                .negativeText(R.string.cancel)
                .show()
    }

    private fun showTaiChiUsersNotice() {
        MaterialDialog.Builder(this)
                .title(R.string.action_taichi_users_notice)
                .content(R.string.prompt_taichi_users_notice_message)
                .positiveText(R.string.check_module)
                .onPositive { _, _ -> PackageUtils.startCheckModuleInTaiChi(this) }
                .negativeText(R.string.add_applications)
                .onNegative { _, _ -> PackageUtils.startAddAppsInTaiChi(this) }
                .show()
    }

    private fun showModuleStatus() {
        val format = "%s (%s)"
        val appName = getString(R.string.app_name)
        val appTitle = if (ModuleUtils.isModuleActive()) {
            String.format(format, appName, getString(R.string.module_status_active))
        } else {
            String.format(format, appName, getString(R.string.module_status_inactive))
        }
        title = appTitle
    }
}