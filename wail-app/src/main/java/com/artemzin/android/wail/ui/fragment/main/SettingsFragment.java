package com.artemzin.android.wail.ui.fragment.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.artemzin.android.bytes.ui.DisplayUnitsConverter;
import com.artemzin.android.bytes.ui.ViewUtil;
import com.artemzin.android.wail.R;
import com.artemzin.android.wail.storage.WAILSettings;
import com.artemzin.android.wail.ui.activity.settings.SettingsSelectLanguageActivity;
import com.artemzin.android.wail.ui.activity.settings.SettingsSoundNotificationsActivity;
import com.artemzin.android.wail.ui.activity.settings.SettingsStatusBarNotificationsActivity;
import com.artemzin.android.wail.ui.fragment.BaseFragment;
import com.artemzin.android.wail.ui.fragment.custom_dialog.DialogFragmentWithNumberPicker;
import com.artemzin.android.wail.ui.fragment.custom_dialog.DialogFragmentWithSeekBar;
import com.artemzin.android.wail.util.WordFormUtil;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;

public class SettingsFragment extends BaseFragment implements View.OnClickListener {

    private final String GA_EVENT_SETTINGS_FRAGMENT = "SettingsFragment";

    private TextView buildVersionDescTextView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        getActivity().getActionBar().setTitle(R.string.settings_actionbar_title);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main_settings, container, false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main_settings, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        Switch isWailEnabledSwitch = (Switch) menu.getItem(0).getActionView();
        isWailEnabledSwitch.setChecked(WAILSettings.isEnabled(getActivity()));
        isWailEnabledSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                WAILSettings.setEnabled(getActivity(), isChecked);

                final Toast toast = Toast.makeText(getActivity(), "", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.TOP|Gravity.RIGHT, 0 , (int) DisplayUnitsConverter.dpToPx(getActivity(), 60));

                if (isChecked) {
                    setUIStateWailEnabled();
                    toast.setText(R.string.settings_wail_enabled_toast);
                } else {
                    setUIStateWailDisabled();
                    toast.setText(R.string.settings_wail_disabled_toast);
                }

                toast.show();

                EasyTracker.getInstance(getActivity()).send(MapBuilder.createEvent(GA_EVENT_SETTINGS_FRAGMENT, "isWailEnabled changed, enabled: " + isChecked, null, isChecked ? 1L : 0L).build());
            }
        });
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (WAILSettings.isEnabled(getActivity())) {
            setUIStateWailEnabled();
        } else {
            setUIStateWailDisabled();
        }

        view.findViewById(R.id.settings_select_language_menu_item).setOnClickListener(this);
        TextView languageMenuItemDescription = (TextView) view.findViewById(R.id.settings_select_language_description);
        languageMenuItemDescription.setText(WAILSettings.getLanguage(getActivity()));

        view.findViewById(R.id.settings_min_track_duration_in_percents).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMinTrackDurationInPercentsEditDialog();
            }
        });

        view.findViewById(R.id.settings_min_track_duration_in_seconds).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMinTrackDurationInSecondsEditDialog();
            }
        });

        refreshMinTrackDurationInPercents();
        refreshMinTrackDurationInSeconds();

        Switch isScrobblingOverMobileNetworkDisabledSwitch = (Switch) view.findViewById(R.id.settings_disable_scrobbling_over_mobile_network_switch);
        isScrobblingOverMobileNetworkDisabledSwitch.setChecked(WAILSettings.isDisableScrobblingOverMobileNetwork(getActivity()));
        isScrobblingOverMobileNetworkDisabledSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                WAILSettings.setDisableScrobblingOverMobileNetwork(getActivity(), b);
            }
        });

        Switch isLastfmUpdateNowplayingEnabledSwitch = (Switch) view.findViewById(R.id.settings_lastfm_update_nowplaying_switch);
        isLastfmUpdateNowplayingEnabledSwitch.setChecked(WAILSettings.isLastfmNowplayingUpdateEnabled(getActivity()));

        isLastfmUpdateNowplayingEnabledSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                WAILSettings.setLastfmNowplayingUpdateEnabled(getActivity(), isChecked);

                final String toast = isChecked ? getString(R.string.settings_lastfm_update_nowplaying_enabled_toast)
                        : getString(R.string.settings_lastfm_update_nowplaying_disabled_toast);

                Toast.makeText(getActivity(), toast, Toast.LENGTH_SHORT).show();

                EasyTracker.getInstance(getActivity()).send(
                        MapBuilder.createEvent(GA_EVENT_SETTINGS_FRAGMENT,
                                "lastFmUpdateNowPlaying enabled: " + isChecked,
                                null,
                                isChecked ? 1L : 0L
                        ).build()
                );
            }
        });

        view.findViewById(R.id.settings_sound_notifications).setOnClickListener(this);

        view.findViewById(R.id.settings_status_bar_notifications).setOnClickListener(this);

        view.findViewById(R.id.settings_email_to_the_developer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emailToTheDeveloper();
            }
        });

        buildVersionDescTextView = (TextView) view.findViewById(R.id.settings_build_version_desc);

        try {
            buildVersionDescTextView.setText(getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionName);
        } catch (Exception e) {
            buildVersionDescTextView.setText("unknown");
            EasyTracker.getInstance(getActivity()).send(MapBuilder.createException("Can not set build version in settings: " + e.getMessage(), false).build());
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        EasyTracker.getInstance(getActivity()).send(MapBuilder.createEvent(GA_EVENT_SETTINGS_FRAGMENT, "started", null, 1L).build());
    }

    @Override
    public void onStop() {
        super.onStop();
        EasyTracker.getInstance(getActivity()).send(MapBuilder.createEvent(GA_EVENT_SETTINGS_FRAGMENT, "stopped", null, 0L).build());
    }

    void setUIStateWailEnabled() {
        ViewUtil.setEnabledForAllChildrenRecursively((ViewGroup) getView().findViewById(R.id.settings_container), true);
    }

    void setUIStateWailDisabled() {
        ViewUtil.setEnabledForAllChildrenRecursively((ViewGroup) getView().findViewById(R.id.settings_container), false);
    }

    void showMinTrackDurationInPercentsEditDialog() {
        final DialogFragmentWithSeekBar dialogFragmentWithSeekBar = DialogFragmentWithSeekBar.newInstance(
                getString(R.string.settings_min_track_elapsed_time_in_percent_dialog_title),
                getString(R.string.settings_min_track_elapsed_time_in_percent_dialog_description),
                WAILSettings.getMinTrackDurationInPercents(getActivity())
        );

        dialogFragmentWithSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress < 50) {
                    seekBar.setProgress(50);
                }

                dialogFragmentWithSeekBar.setBottomText(getString(
                        R.string.settings_min_track_elapsed_time_in_percent_dialog_bottom_text,
                        seekBar.getProgress())
                );
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        dialogFragmentWithSeekBar.setLeftButtonOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogFragmentWithSeekBar.dismiss();
            }
        });

        dialogFragmentWithSeekBar.setRightButtonOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    WAILSettings.setMinTrackDurationInPercents(getActivity(), dialogFragmentWithSeekBar.getSeekBar().getProgress());
                    dialogFragmentWithSeekBar.dismiss();
                    refreshMinTrackDurationInPercents();

                    EasyTracker.getInstance(getActivity()).send(
                            MapBuilder.createEvent(GA_EVENT_SETTINGS_FRAGMENT,
                                    "changed min track duration in percents to: " + WAILSettings.getMinTrackDurationInPercents(getActivity()) + "%",
                                    null,
                                    1L)
                            .build()
                    );
                } catch (Exception e) {
                    // do nothing
                }
            }
        });

        dialogFragmentWithSeekBar.show(getFragmentManager(), "minTrackDurationInPercentsDialog");
    }

    private void refreshMinTrackDurationInPercents() {
        ((TextView) getView().findViewById(R.id.settings_min_track_duration_in_percents_desc)).setText(
                getString(
                        R.string.settings_min_track_elapsed_time_in_percent_desc,
                        WAILSettings.getMinTrackDurationInPercents(getActivity())
                )
        );
    }

    void showMinTrackDurationInSecondsEditDialog() {
        final DialogFragmentWithNumberPicker minTrackDurationInSecondsDialog = DialogFragmentWithNumberPicker.newInstance(
                getString(R.string.settings_min_track_elapsed_time_in_seconds_dialog_title),
                null,
                30,
                600,
                WAILSettings.getMinTrackDurationInSeconds(getActivity())
        );

        minTrackDurationInSecondsDialog.setLeftButtonOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                minTrackDurationInSecondsDialog.dismiss();
            }
        });

        minTrackDurationInSecondsDialog.setRightButtonOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WAILSettings.setMinTrackDurationInSeconds(
                        getActivity(),
                        minTrackDurationInSecondsDialog.getNumberPicker().getValue()
                );

                refreshMinTrackDurationInSeconds();
                minTrackDurationInSecondsDialog.dismiss();

                EasyTracker.getInstance(getActivity()).send(
                        MapBuilder.createEvent(GA_EVENT_SETTINGS_FRAGMENT,
                                "changed min track duration in seconds to: " + WAILSettings.getMinTrackDurationInSeconds(getActivity()) + " seconds",
                                null,
                                1L)
                        .build()
                );
            }
        });

        minTrackDurationInSecondsDialog.show(getFragmentManager(), "minTrackDurationInSecondsDialog");
    }

    private void refreshMinTrackDurationInSeconds() {
        final int minTrackDurationInSeconds = WAILSettings.getMinTrackDurationInSeconds(getActivity());

        ((TextView) getView().findViewById(R.id.settings_min_track_duration_in_seconds_desc)).setText(
                getString(
                        R.string.settings_min_track_elapsed_time_in_seconds_desc,
                         minTrackDurationInSeconds + " " + WordFormUtil.getWordForm(minTrackDurationInSeconds, getResources().getStringArray(R.array.word_forms_second))
                )
        );
    }

    private void emailToTheDeveloper() {
        try {
            final Intent emailIntent = new Intent(Intent.ACTION_SEND);

            emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] { getString(R.string.settings_my_email) });
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.settings_email_to_the_developer_subj) + " " + buildVersionDescTextView.getText().toString());
            emailIntent.setType("message/rfc822");

            startActivity(Intent.createChooser(emailIntent, getString(R.string.settings_email_dialog_title)));

            EasyTracker.getInstance(getActivity())
                    .send(MapBuilder.createEvent(GA_EVENT_SETTINGS_FRAGMENT,
                            "emailToTheDeveloperClicked",
                            null,
                            1L).build());
        } catch (Exception e) {
            EasyTracker.getInstance(getActivity())
                    .send(MapBuilder.createException("Can not send email to the developer: " + e, false).build());
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.settings_sound_notifications) {
            startActivity(new Intent(getActivity(), SettingsSoundNotificationsActivity.class));
        } else if (v.getId() == R.id.settings_status_bar_notifications) {
            startActivity(new Intent(getActivity(), SettingsStatusBarNotificationsActivity.class));
        } else if (v.getId() == R.id.settings_select_language_menu_item) {
            startActivity(new Intent(getActivity(), SettingsSelectLanguageActivity.class));
        }
    }
}
