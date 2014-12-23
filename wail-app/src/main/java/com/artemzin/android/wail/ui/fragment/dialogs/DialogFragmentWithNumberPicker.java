package com.artemzin.android.wail.ui.fragment.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.artemzin.android.wail.R;
import com.artemzin.android.wail.storage.WAILSettings;
import com.artemzin.android.wail.ui.fragment.main.SettingsFragment;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;

public class DialogFragmentWithNumberPicker extends DialogDecorator {
    private int minValue, maxValue, initValue;
    private NumberPicker numberPicker;
    private String title;

    public static DialogFragmentWithNumberPicker newInstance(String title, int minValue, int maxValue, int initValue) {
        final DialogFragmentWithNumberPicker dialog = new DialogFragmentWithNumberPicker();
        dialog.title = title;
        dialog.minValue = minValue;
        dialog.maxValue = maxValue;
        dialog.initValue = initValue;

        return dialog;
    }

    public int getPickerValue() {
        return numberPicker.getValue();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_with_number_picker_fragment, (ViewGroup) getView());
        View titleView = inflater.inflate(R.layout.dialog_fragment_title, ((ViewGroup) getView()));
        ((TextView) titleView.findViewById(R.id.dialog_fragment_title_text_view)).setText(title);

        builder.setView(view)
                .setCustomTitle(titleView)
                .setPositiveButton(getString(R.string.dialog_save), new DialogInterface.OnClickListener() {
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
                .setNegativeButton(getString(R.string.dialog_cancel), new DialogInterface.OnClickListener() {
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
