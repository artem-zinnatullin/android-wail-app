package com.artemzin.android.wail.ui.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;

import com.google.analytics.tracking.android.EasyTracker;

public abstract class BaseActivity extends ActionBarActivity {

    protected boolean doFinishOnHomeAsUpButton() {
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            onCreteWithNullState();
        }

        setupUI(savedInstanceState);
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
}
