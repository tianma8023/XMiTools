package com.tianma.tweaks.miui.app.base

import android.content.Context
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.tianma.tweaks.miui.utils.ContextUtils

abstract class BaseActivity : AppCompatActivity() {

    override fun attachBaseContext(newBase: Context?) {
        val context = if (newBase != null) {
            ContextUtils.getProtectedContextIfNecessary(newBase)
        } else {
            newBase
        }
        super.attachBaseContext(context)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val itemId = item.itemId
        if (itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}