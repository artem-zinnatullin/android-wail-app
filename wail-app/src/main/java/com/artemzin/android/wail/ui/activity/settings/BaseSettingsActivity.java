package com.artemzin.android.wail.ui.activity.settings;

import android.os.Bundle;

import com.artemzin.android.wail.R;
import com.artemzin.android.wail.ui.activity.BaseActivity;

/**
 * @author Artem Zinnatullin [artem.zinnatullin@gmail.com]
 */
public class BaseSettingsActivity extends BaseActivity {

    @Override
    protected void setupUI(Bundle savedInstanceState) {
        super.setupUI(savedInstanceState);
        overridePendingTransition(R.anim.activity_pull_from_right_to_left, R.anim.activity_pull_from_current_to_left);
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.activity_pull_from_left_out_to_screen, R.anim.activity_pull_from_center_to_out_right);
    }

}
