package com.artemzin.android.wail.ui.fragment.custom_dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;

import com.artemzin.android.wail.R;

public class DialogFragmentWithNumberPicker extends CustomDialogFragment {

    private static final String ARGS_MIN_VALUE  = "ARGS_MIN_VALUE";
    private static final String ARGS_MAX_VALUE  = "ARGS_MAX_VALUE";
    private static final String ARGS_INIT_VALUE = "ARGS_INIT_VALUE";

    private int minValue, maxValue, initValue;

    private NumberPicker numberPicker;
    private NumberPicker.OnValueChangeListener onValueChangeListener;

    public NumberPicker getNumberPicker() {
        return numberPicker;
    }

    public void setOnValueChangeListener(NumberPicker.OnValueChangeListener listener) {
        this.onValueChangeListener = listener;
    }

    public static DialogFragmentWithNumberPicker newInstance(String title, String description, int minValue, int maxValue, int initValue) {
        Bundle args = getDefaultArguments(title, description);
        args.putInt(ARGS_MIN_VALUE, minValue);
        args.putInt(ARGS_MAX_VALUE, maxValue);
        args.putInt(ARGS_INIT_VALUE, initValue);

        final DialogFragmentWithNumberPicker dialogFragmentWithNumberPicker = new DialogFragmentWithNumberPicker();
        dialogFragmentWithNumberPicker.setArguments(args);

        return dialogFragmentWithNumberPicker;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        minValue  = getArguments().getInt(ARGS_MIN_VALUE);
        maxValue  = getArguments().getInt(ARGS_MAX_VALUE);
        initValue = getArguments().getInt(ARGS_INIT_VALUE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_with_number_picker_fragment, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        numberPicker = (NumberPicker) view.findViewById(R.id.dialog_with_number_picker_picker);

        numberPicker.setMinValue(minValue);
        numberPicker.setMaxValue(maxValue);
        numberPicker.setValue(initValue);
        numberPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        if (onValueChangeListener != null) {
            numberPicker.setOnValueChangedListener(onValueChangeListener);
        }
    }
}
