package com.artemzin.android.wail.ui.fragment.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.widget.NumberPicker;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
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

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                .customView(R.layout.dialog_with_number_picker_fragment, false)
                .theme(Theme.DARK)
                .title(title)
                .positiveText(R.string.dialog_save)
                .negativeText(R.string.dialog_cancel)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
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

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        dialog.dismiss();
                    }
                }).build();


        numberPicker = (NumberPicker) dialog.getCustomView()
                .findViewById(R.id.dialog_with_number_picker_picker);

        numberPicker.setMinValue(minValue);
        numberPicker.setMaxValue(maxValue);
        numberPicker.setValue(initValue);
        numberPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        return dialog;
    }
}
