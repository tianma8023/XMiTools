package com.tianma.tweaks.miui.app.base;

import android.content.Context;
import android.os.Build;
import android.view.MenuItem;

import com.tianma.tweaks.miui.utils.ContextUtils;

import androidx.appcompat.app.AppCompatActivity;

public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void attachBaseContext(Context newBase) {
        Context context = newBase;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            context = ContextUtils.getProtectedContext(newBase);
        }
        super.attachBaseContext(context);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
