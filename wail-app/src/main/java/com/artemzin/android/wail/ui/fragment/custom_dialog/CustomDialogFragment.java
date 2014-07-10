package com.artemzin.android.wail.ui.fragment.custom_dialog;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.artemzin.android.wail.R;

public class CustomDialogFragment extends DialogFragment {

    private static final String ARGS_TITLE = "ARGS_TITLE";
    private static final String ARGS_DESCRIPTION = "ARGS_DESCRIPTION";

    private String title;
    private String description;

    private TextView descriptionTextView;
    private Button leftButton, rightButton;

    private View.OnClickListener leftButtonOnClickListener, rightButtonOnClickListener;

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    protected View.OnClickListener getLeftButtonOnClickListener() {
        return leftButtonOnClickListener;
    }

    public void setLeftButtonOnClickListener(View.OnClickListener leftButtonOnClickListener) {
        this.leftButtonOnClickListener = leftButtonOnClickListener;
    }

    protected View.OnClickListener getRightButtonOnClickListener() {
        return rightButtonOnClickListener;
    }

    public void setRightButtonOnClickListener(View.OnClickListener rightButtonOnClickListener) {
        this.rightButtonOnClickListener = rightButtonOnClickListener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        title = getArguments().getString(ARGS_TITLE);
        description = getArguments().getString(ARGS_DESCRIPTION);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getDialog().setTitle(getTitle());

        ((TextView) view.findViewById(R.id.custom_dialog_description)).setText(getDescription());

        descriptionTextView = (TextView) view.findViewById(R.id.custom_dialog_description);

        leftButton     = (Button) view.findViewById(R.id.custom_dialog_left_button);
        rightButton    = (Button) view.findViewById(R.id.custom_dialog_right_button);

        if (getLeftButtonOnClickListener() != null) {
            leftButton.setOnClickListener(leftButtonOnClickListener);
        }

        if (getRightButtonOnClickListener() != null) {
            rightButton.setOnClickListener(rightButtonOnClickListener);
        }
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

    protected static Bundle getDefaultArguments(String title, String description) {
        final Bundle args = new Bundle();

        args.putString(ARGS_TITLE, title);
        args.putString(ARGS_DESCRIPTION, description);

        return args;
    }
}
