package com.artemzin.android.wail.ui.fragment.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.artemzin.android.wail.R;
import com.artemzin.android.wail.storage.WAILSettings;
import com.artemzin.android.wail.ui.fragment.main.SettingsFragment;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;

public class DialogFragmentWithSeekBar extends DialogDecorator {
    private String title;
    private String description;

    private int startProgressValue;
    private SeekBar seekBar;

    public static DialogFragmentWithSeekBar newInstance(String title, String description, int startProgressValue) {
        final DialogFragmentWithSeekBar dialog = new DialogFragmentWithSeekBar();
        dialog.description = description;
        dialog.startProgressValue = startProgressValue;
        dialog.title = title;
        return dialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                .customView(R.layout.dialog_with_seek_bar_fragment, false)
                .theme(Theme.DARK)
                .title(title)
                .positiveText(R.string.dialog_save)
                .negativeText(R.string.dialog_cancel)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        WAILSettings.setMinTrackDurationInPercents(
                                getActivity(),
                                seekBar.getProgress()
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

        seekBar = (SeekBar) dialog.getCustomView().findViewById(R.id.dialog_with_seek_bar_seek_bar);
        seekBar.setProgress(startProgressValue);
        final TextView label = (TextView) dialog.getCustomView().findViewById(R.id.dialog_with_seek_bar_bottom_text);
        label.setText(getString(
                R.string.settings_min_track_elapsed_time_in_percent_dialog_bottom_text,
                seekBar.getProgress()));

        ((TextView) dialog.getCustomView().findViewById(R.id.custom_dialog_description)).setText(description);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress < 50) {
                    seekBar.setProgress(50);
                }
                label.setText(getString(
                        R.string.settings_min_track_elapsed_time_in_percent_dialog_bottom_text,
                        seekBar.getProgress()));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        return dialog;
    }
}
