package com.artemzin.android.wail.ui.fragment.dialogs;

import android.app.DialogFragment;
import android.content.DialogInterface;

public class DialogDecorator extends DialogFragment {
    private Callback listener;

    public void setListener(Callback listener) {
        this.listener = listener;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        listener.onDismiss();
    }

    public interface Callback {
        void onDismiss();
    }
}
