package com.artemzin.android.wail.ui.fragment.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;

import com.artemzin.android.wail.R;
import com.artemzin.android.wail.storage.WAILSettings;
import com.artemzin.android.wail.ui.fragment.main.SettingsFragment;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;

public class DialogFragmentWithNumberPicker extends DialogDecorator {
    private int minValue, maxValue, initValue;
    private NumberPicker numberPicker;

    public static DialogFragmentWithNumberPicker newInstance(int minValue, int maxValue, int initValue) {
        final DialogFragmentWithNumberPicker dialogFragmentWithNumberPicker = new DialogFragmentWithNumberPicker();
        dialogFragmentWithNumberPicker.minValue = minValue;
        dialogFragmentWithNumberPicker.maxValue = maxValue;
        dialogFragmentWithNumberPicker.initValue = initValue;

        return dialogFragmentWithNumberPicker;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_with_number_picker_fragment, null);

        builder.setView(view)
                .setCustomTitle(inflater.inflate(R.layout.dialog_with_number_picker_fragment_title, null))
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        WAILSettings.setMinTrackDurationInSeconds(
                                getActivity(),
                                numberPicker.getValue()
                        );

                        EasyTracker.getInstance(getActivity()).send(
                                MapBuilder.createEvent(SettingsFragment.GA_EVENT_SETTINGS_FRAGMENT,
                                        "changed min track duration in seconds to: " +
                                                WAILSettings.getMinTrackDurationInSeconds(getActivity()) +
                                                " seconds",
                                        null,
                                        1L)
                                        .build()
                        );
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        DialogFragmentWithNumberPicker.this.getDialog().cancel();
                    }
                });


        numberPicker = (NumberPicker) view.findViewById(R.id.dialog_with_number_picker_picker);

        numberPicker.setMinValue(minValue);
        numberPicker.setMaxValue(maxValue);
        numberPicker.setValue(initValue);
        numberPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        return builder.create();
    }
}
