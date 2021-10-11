package com.tianma.tweaks.miui.app

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Menu
import android.view.MenuItem
import com.afollestad.materialdialogs.MaterialDialog
import com.tianma.tweaks.miui.R
import com.tianma.tweaks.miui.app.base.BaseActivity
import com.tianma.tweaks.miui.app.base.BasePreferenceFragment
import com.tianma.tweaks.miui.app.fragment.*
import com.tianma.tweaks.miui.data.sp.AppPreferenceSettings
import com.tianma.tweaks.miui.utils.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.toolbar.*
import kotlin.math.max

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
            R.id.action_edxposed_users_notice -> showEdXposedUsersNotice()
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    private fun performRebootSystem() {
        MaterialDialog(this).show {
            title(R.string.action_reboot_system)
            message(R.string.prompt_reboot_system_message)
            positiveButton(R.string.confirm) {
                RootUtils.reboot()
            }
            negativeButton(R.string.cancel)
        }
    }

    private fun preformSoftRebootSystem() {
        MaterialDialog(this).show {
            title(R.string.action_soft_reboot_system)
            message(R.string.prompt_soft_reboot_message)
            positiveButton(R.string.confirm) {
                RootUtils.softReboot()
            }
            negativeButton(R.string.cancel)
        }
    }

    private fun performRestartHostApps() {
        MaterialDialog(this).show {
            title(R.string.action_restart_host_apps)
            message(R.string.prompt_restart_host_apps_message)
            positiveButton(R.string.confirm) {
                RootUtils.restartSystemUI()
            }
            negativeButton(R.string.cancel)
        }
    }

    private fun showTaiChiUsersNotice() {
        MaterialDialog(this).show {
            title(R.string.action_taichi_users_notice)
            message(R.string.prompt_taichi_users_notice_message)
            positiveButton(R.string.check_module) {
                PackageUtils.startCheckModuleInTaiChi(this@MainActivity)
            }
            negativeButton(R.string.add_applications) {
                PackageUtils.startAddAppsInTaiChi(this@MainActivity)
            }
        }
    }

    private fun showEdXposedUsersNotice() {
        MaterialDialog(this).show {
            title(R.string.action_edxposed_users_notice)
            message(R.string.edxposed_users_notice_content)
            positiveButton(R.string.confirm)
        }
    }

    private fun showModuleStatus() {
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            if (isFinishing) {
                return@postDelayed
            }
            val format = "%s (%s)"
            val appName = getString(R.string.app_name)
            val appTitle = if (ModuleUtils.isModuleActive()) {
                String.format(format, appName, getString(R.string.module_status_active))
            } else {
                String.format(format, appName, getString(R.string.module_status_inactive))
            }
            title = appTitle
        }, 1000L)
    }
}