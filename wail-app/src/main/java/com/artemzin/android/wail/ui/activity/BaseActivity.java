package com.artemzin.android.wail.ui.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;

import com.artemzin.android.wail.R;
import com.artemzin.android.wail.WAILApp;
import com.artemzin.android.wail.storage.WAILSettings;
import com.google.analytics.tracking.android.EasyTracker;

public abstract class BaseActivity extends ActionBarActivity {

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
        WAILApp.activityPaused();
    }

    @Override
    protected void onResume() {
        super.onResume();
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
            setTheme(R.style.AppTheme_Dark);
        } else {
            setTheme(R.style.AppTheme_Light);
        }
    }

    public void restart() {
        finish();
        startActivity(getIntent());
    }
}
