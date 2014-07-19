package com.artemzin.android.wail.ui.fragment;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.artemzin.android.wail.R;

public class ProgressDialogFragment extends DialogFragment {

    private static final String ARGS_PROGRESS_TEXT_ID = "ARGS_PROGRESS_TEXT_ID";

    private String progressMessage;

    public static ProgressDialogFragment newInstance(String progressMessage) {
        Bundle args = new Bundle();
        args.putString(ARGS_PROGRESS_TEXT_ID, progressMessage);

        ProgressDialogFragment progressDialogFragment = new ProgressDialogFragment();
        progressDialogFragment.setArguments(args);

        return progressDialogFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        progressMessage = getArguments().getString(ARGS_PROGRESS_TEXT_ID);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE); // hiding title from dialog window
        return inflater.inflate(R.layout.fragment_progress_dialog, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ((TextView) view.findViewById(R.id.progress_dialog_message)).setText(progressMessage);
    }

    @Override
    public void onDestroyView() {
        if (getDialog() != null && getRetainInstance()) {
            getDialog().setDismissMessage(null);
        }

        super.onDestroyView();
    }

    @Override
    public void dismiss() {
        try {
            super.dismiss();
        } catch (Exception e) {
        }
    }

    @Override
    public void dismissAllowingStateLoss() {
        try {
            super.dismissAllowingStateLoss();
        } catch (Exception e) {

        }
    }
}
