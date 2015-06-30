package com.artemzin.android.wail.ui.fragment.dialogs;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.DialogInterface;

public class DialogDecorator extends DialogFragment {
    protected Activity activity;
    private Callback listener;

    public void setListener(Callback listener) {
        this.listener = listener;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (listener != null) {
            listener.onDismiss();
        }
    }

    public interface Callback {
        void onDismiss();
    }
}
