package com.artemzin.android.wail.ui.activity;

import android.os.Bundle;

import com.artemzin.android.wail.R;
import com.artemzin.android.wail.ui.fragment.NonAuthorizedMainFragment;

public class NonAuthorizedActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_non_authorized);

        if (savedInstanceState == null) {
            getFragmentManager()
                    .beginTransaction()
                    .add(R.id.non_authorized_frame, new NonAuthorizedMainFragment())
                    .commit();
        }

        setResult(RESULT_CANCELED);
    }
}
