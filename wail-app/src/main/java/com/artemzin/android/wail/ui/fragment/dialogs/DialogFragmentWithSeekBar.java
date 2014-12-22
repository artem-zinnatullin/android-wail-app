package com.artemzin.android.wail.ui.fragment.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.artemzin.android.wail.R;
import com.artemzin.android.wail.storage.WAILSettings;
import com.artemzin.android.wail.ui.fragment.main.SettingsFragment;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;

public class DialogFragmentWithSeekBar extends DialogDecorator {
    private String description;
    private int startProgressValue;

    private SeekBar seekBar;

    public static DialogFragmentWithSeekBar newInstance(String description, int startProgressValue) {
        final DialogFragmentWithSeekBar dialog = new DialogFragmentWithSeekBar();
        dialog.description = description;
        dialog.startProgressValue = startProgressValue;
        return dialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_with_seek_bar_fragment, null);

        builder.setView(view)
                .setCustomTitle(inflater.inflate(R.layout.dialog_with_seek_bar_fragment_title, null))
                .setPositiveButton(getString(R.string.dialog_save), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
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
                })
                .setNegativeButton(getString(R.string.dialog_cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        DialogFragmentWithSeekBar.this.getDialog().cancel();
                    }
                });

        seekBar = (SeekBar) view.findViewById(R.id.dialog_with_seek_bar_seek_bar);
        seekBar.setProgress(startProgressValue);
        final TextView label = (TextView) view.findViewById(R.id.dialog_with_seek_bar_bottom_text);
        label.setText(getString(
                R.string.settings_min_track_elapsed_time_in_percent_dialog_bottom_text,
                seekBar.getProgress()));

        ((TextView) view.findViewById(R.id.custom_dialog_description)).setText(description);

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

        return builder.create();
    }
}
