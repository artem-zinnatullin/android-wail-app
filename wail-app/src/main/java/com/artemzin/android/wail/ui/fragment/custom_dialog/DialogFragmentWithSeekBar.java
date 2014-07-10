package com.artemzin.android.wail.ui.fragment.custom_dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.artemzin.android.wail.R;

public class DialogFragmentWithSeekBar extends CustomDialogFragment {

    private static final String ARGS_START_PROGRESS_VALUE = "ARGS_START_PROGRESS_VALUE";

    private int startProgressValue;

    private SeekBar seekBar;
    private TextView bottomTextView;

    private SeekBar.OnSeekBarChangeListener onSeekBarChangeListener;

    public static DialogFragmentWithSeekBar newInstance(String title, String description, int startProgressValue) {
        Bundle args = getDefaultArguments(title, description);
        args.putInt(ARGS_START_PROGRESS_VALUE, startProgressValue);

        final DialogFragmentWithSeekBar dialog = new DialogFragmentWithSeekBar();
        dialog.setArguments(args);

        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startProgressValue = getArguments().getInt(ARGS_START_PROGRESS_VALUE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_with_seek_bar_fragment, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        seekBar        = (SeekBar) view.findViewById(R.id.dialog_with_seek_bar_seek_bar);
        bottomTextView = (TextView) view.findViewById(R.id.dialog_with_seek_bar_bottom_text);

        if (onSeekBarChangeListener != null) {
            seekBar.setOnSeekBarChangeListener(onSeekBarChangeListener);
        }

        seekBar.setProgress(startProgressValue);
    }

    public void setOnSeekBarChangeListener(SeekBar.OnSeekBarChangeListener listener) {
        this.onSeekBarChangeListener = listener;
    }

    public void setBottomText(String text) {
        bottomTextView.setText(text);
    }

    public SeekBar getSeekBar() {
        return seekBar;
    }
}
