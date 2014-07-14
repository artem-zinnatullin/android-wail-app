package com.artemzin.android.wail.ui.activity;

import android.os.Bundle;

import com.artemzin.android.wail.R;

public class LastfmLoginActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.activity_pull_from_bottom, R.anim.activity_alpha_down);

        setContentView(R.layout.activity_lastfm_login);
        setResult(RESULT_CANCELED);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.activity_apha_up, R.anim.activity_push_to_bottom);
    }
}
