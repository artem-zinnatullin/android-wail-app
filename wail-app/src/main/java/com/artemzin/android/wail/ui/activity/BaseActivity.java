package com.artemzin.android.wail.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.artemzin.android.wail.R;
import com.artemzin.android.wail.WAILApp;
import com.artemzin.android.wail.storage.WAILSettings;
import com.artemzin.android.wail.storage.db.AppDBManager;
import com.artemzin.android.wail.util.LocaleUtil;
import com.google.analytics.tracking.android.EasyTracker;

public abstract class BaseActivity extends AppCompatActivity {

    public static final String ACTION_INVALID_SESSION_KEY = "ACTION_INVALID_SESSION_KEY";

    private final BroadcastReceiver invalidSessionKeyReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            WAILSettings.clearAllSettings(BaseActivity.this);
            AppDBManager.getInstance(BaseActivity.this).clearAll();
            LocaleUtil.updateLanguage(BaseActivity.this, null);
            startActivity(new Intent(BaseActivity.this, MainActivity.class));
            BaseActivity.this.finish();
        }
    };

    protected boolean doFinishOnHomeAsUpButton() {
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme();
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            onCreteWithNullState();
        }

        setupUI(savedInstanceState);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(invalidSessionKeyReceiver);
        WAILApp.activityPaused();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(invalidSessionKeyReceiver, new IntentFilter(ACTION_INVALID_SESSION_KEY));
        WAILApp.activityResumed();
    }

    /**
     * Will be called only once through the activity lifecycle
     * When savedInstanceState bundle in onCreate is null, that method will be called
     */
    protected void onCreteWithNullState() {

    }

    protected void setupUI(Bundle savedInstanceState) {

    }

    @Override
    public void onStart() {
        super.onStart();
        EasyTracker.getInstance(this).activityStart(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            if (doFinishOnHomeAsUpButton()) {
                finish();
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EasyTracker.getInstance(this).activityStop(this);
    }

    public void setTheme() {
        if (WAILSettings.getTheme(getBaseContext()) == WAILSettings.Theme.DARK) {
            if (this instanceof MainActivity) {
                setTheme(R.style.AppTheme_Dark_NoActionBar);
            } else {
                setTheme(R.style.AppTheme_Dark);
            }
        } else {
            if (this instanceof MainActivity) {
                setTheme(R.style.AppTheme_Light_NoActionBar);
            } else {
                setTheme(R.style.AppTheme_Light);
            }
        }
    }

    public void restart() {
        finish();
        startActivity(getIntent());
    }
}
