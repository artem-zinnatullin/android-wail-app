package com.artemzin.android.wail.ui.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.View;

public abstract class BaseFragment extends Fragment {

    private Bundle savedInstanceState;
    private boolean wasStarted;

    protected boolean isRetainInstance() {
        return true;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.savedInstanceState = savedInstanceState;

        if (isRetainInstance()) {
            setRetainInstance(true);
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();

        if (!wasStarted) {
            wasStarted = true;
            onFirstStart(savedInstanceState);
        }
    }

    protected void onFirstStart(Bundle savedInstanceState) {}
}
