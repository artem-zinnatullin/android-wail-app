package com.artemzin.android.wail.ui.fragment.main;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.artemzin.android.bytes.ui.ViewUtil;
import com.artemzin.android.wail.R;
import com.artemzin.android.wail.api.lastfm.LFApiException;
import com.artemzin.android.wail.api.lastfm.LFUserApi;
import com.artemzin.android.wail.api.lastfm.model.response.LFUserResponseModel;
import com.artemzin.android.wail.api.network.NetworkException;
import com.artemzin.android.wail.service.WAILService;
import com.artemzin.android.wail.storage.WAILSettings;
import com.artemzin.android.wail.storage.db.IgnoredPlayersDBHelper;
import com.artemzin.android.wail.storage.db.TracksDBHelper;
import com.artemzin.android.wail.storage.model.Track;
import com.artemzin.android.wail.ui.fragment.BaseFragment;
import com.artemzin.android.wail.util.AsyncTaskExecutor;
import com.artemzin.android.wail.util.Loggi;
import com.artemzin.android.wail.util.ThreadUtil;
import com.artemzin.android.wail.util.WordFormUtil;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;
import com.melnykov.fab.FloatingActionButton;
import com.melnykov.fab.ObservableScrollView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class MainFragment extends BaseFragment {

    private static final String GA_EVENT_MAIN_FRAGMENT = "MainFragment";

    private IgnoredPlayersDBHelper dbHelper;

    private final BroadcastReceiver tracksChangedBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateLocalInfo();
        }
    };

    @InjectView(R.id.main_scroll_view)
    public ObservableScrollView scrollView;

    @InjectView(R.id.main_pull_to_refresh_layout)
    public SwipeRefreshLayout pullToRefreshLayout;

    @InjectView(R.id.main_tracks_today_count_text_view)
    public TextView tracksTodayCountTextView;

    @InjectView(R.id.main_tracks_today_count_label_text_view)
    public TextView tracksTodayCountLabelTextView;

    @InjectView(R.id.main_now_scrobbling_track_text_view)
    public TextView nowScrobblingTrackTextView;

    @InjectView(R.id.main_now_scrobbling_player_text_view)
    public TextView nowScrobblingPlayerTextView;

    @InjectView(R.id.main_tracks_total_count_text_view)
    public TextView tracksTotalCountOnLastfmTextView;

    @InjectView(R.id.main_tracks_total_count_label_text_view)
    public TextView tracksTotalCountOnLastfmLabelTextView;

    @InjectView(R.id.main_tracks_total_count_unknown_text)
    public TextView tracksTotalCountOnLastfmLabelUnknownTextView;

    @InjectView(R.id.main_last_fm_user_info_update_time)
    public TextView lastfmUserInfoUpdateTimeTextView;

    @InjectView(R.id.main_ignore_player_button)
    public TextView ignorePlayerButton;

    @InjectView(R.id.main_love_current_track_button)
    public FloatingActionButton loveCurrentTrackButton;

    @InjectView(R.id.main_feedback_please)
    public View feedbackPleaseView;

    private String[] trackWordForms;

    @OnClick(R.id.main_tracks_today_view)
    public void onTracksTodayViewClick() {
        Toast.makeText(getActivity(), getString(R.string.main_pull_down_to_refresh_toast), Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.main_feedback_please)
    public void onFeedbackPleaseClick() {
        final Activity activity = getActivity();

        WAILSettings.setShowFeedbackRequest(activity, false);
        ViewUtil.setVisibility(feedbackPleaseView, false);

        Toast.makeText(activity, getString(R.string.main_feedback_please_happy_toast), Toast.LENGTH_LONG)
                .show();

        final String appPackageName = activity.getPackageName();

        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
            EasyTracker.getInstance(activity).send(MapBuilder.createEvent(
                    GA_EVENT_MAIN_FRAGMENT,
                    "feedback_please_click",
                    "Google Play opened",
                    1L
            ).build());
        } catch (Exception e) {
            // will open browser if failed with Google Play app
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName)));
            EasyTracker.getInstance(activity).send(MapBuilder.createEvent(
                    GA_EVENT_MAIN_FRAGMENT,
                    "feedback_please_click",
                    "Browser opened",
                    1L
            ).build());
        }
    }

    @OnClick(R.id.main_love_current_track_button)
    public void onLoveCurrentTrackButtonClick() {
        Track track = WAILSettings.getNowScrobblingTrack(getActivity());
        if (track != null) {
            Toast.makeText(getActivity(), getString(R.string.main_track_loved), Toast.LENGTH_SHORT).show();
            loveCurrentTrackButton.hide();
            getActivity().startService(
                    new Intent(getActivity(), WAILService.class)
                            .setAction(WAILService.INTENT_ACTION_HANDLE_LOVED_TRACK)
            );
        }
    }

    @OnClick(R.id.main_ignore_player_button)
    public void onIgnoreScrobblingPlayerClick() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View titleView = inflater.inflate(R.layout.dialog_fragment_title, null);
        String label = WAILSettings.getNowScrobblingPlayerLabel(getActivity());
        final String packageName = WAILSettings.getNowScrobblingPlayerPackageName(getActivity());
        final String nowScrobblingPlayer = label != null ? label : packageName;

        ((TextView) titleView.findViewById(R.id.dialog_fragment_title_text_view))
                .setText(String.format(
                                getString(R.string.main_confirm_ignoring_player),
                                nowScrobblingPlayer)
                );

        builder.setCustomTitle(titleView)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dbHelper.add(packageName);
                        WAILSettings.setNowScrobblingTrack(getActivity(), null);
                        WAILSettings.setNowScrobblingPlayerPackageName(getActivity(), null);
                        WAILSettings.setNowScrobblingPlayerLabel(getActivity(), null);
                        WAILSettings.setLastCapturedTrackInfo(getActivity(), null);
                        updateLocalInfo();
                    }
                })
                .setNegativeButton(getString(R.string.dialog_cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                }).show();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((ActionBarActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.main_ab_title));
        loadTrackWordForms();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dbHelper = IgnoredPlayersDBHelper.getInstance(getActivity());

        ButterKnife.inject(this, view);

        final Activity activity = getActivity();

        pullToRefreshLayout.setColorSchemeResources(R.color.primary);
        pullToRefreshLayout.setOnRefreshListener(new PullToRefreshListener());

        if (WAILSettings.isShowFeedbackRequest(activity)) {
            ViewUtil.setVisibility(feedbackPleaseView, true);
        }

        loveCurrentTrackButton.attachToScrollView(scrollView);
        loveCurrentTrackButton.show(false);
    }

    @Override
    public void onStart() {
        super.onStart();

        EasyTracker.getInstance(getActivity()).send(
                MapBuilder.createEvent(GA_EVENT_MAIN_FRAGMENT,
                        "started",
                        null,
                        1L)
                        .build()
        );
    }

    @Override
    public void onResume() {
        super.onResume();

        try {
            if (!WAILSettings.isAuthorized(getActivity())) return;
        } catch (Exception e) {

        }

        updateLocalInfo();

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(
                tracksChangedBroadcastReceiver,
                new IntentFilter(TracksDBHelper.INTENT_TRACKS_CHANGED)
        );

        AsyncTaskExecutor.executeConcurrently(new AsyncTask<Object, Object, Object>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                updateTracksCountFromLastfm();
            }

            @Override
            protected Object doInBackground(Object... params) {
                ThreadUtil.sleepIfRequired(SystemClock.elapsedRealtime(), 650);
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);

                try {
                    final Context context = getActivity();

                    if (isRemoving() || isDetached() || context.isRestricted()) return;

                    final long updateTimeStamp = WAILSettings.getLastfmUserInfoUpdateTimestamp(context);

                    if (!pullToRefreshLayout.isRefreshing() && System.currentTimeMillis() - updateTimeStamp > 180000) {
                        refreshDataFromLastfm();
                    }
                } catch (Exception e) {
                    Loggi.e("Exception in delayed refresh in MainFragment: " + e.getMessage());
                }
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(tracksChangedBroadcastReceiver);
    }

    @Override
    public void onStop() {
        super.onStop();
        EasyTracker.getInstance(getActivity()).send(
                MapBuilder.createEvent(GA_EVENT_MAIN_FRAGMENT,
                        "stopped",
                        null,
                        0L)
                        .build()
        );
    }

    private void refreshDataFromLastfm() {
        AsyncTaskExecutor.executeConcurrently(new AsyncTask<Object, Object, LFUserResponseModel>() {
            NetworkException networkException;
            LFApiException lfApiException;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                try {
                    pullToRefreshLayout.setRefreshing(true);
                } catch (Exception e) {

                }
            }

            @Override
            protected LFUserResponseModel doInBackground(Object... params) {
                final long startTime = SystemClock.elapsedRealtime();

                LFUserResponseModel userModel = null;

                try {
                    final String response = LFUserApi.getInfo(
                            WAILSettings.getLastfmSessionKey(getActivity()),
                            WAILSettings.getLastfmApiKey(),
                            WAILSettings.getLastfmSecret(),
                            null
                    );

                    userModel = LFUserResponseModel.parseFromJSON(response);
                    WAILSettings.setLastfmUserInfo(getActivity(), response);
                    WAILSettings.setLastfmUserName(getActivity(), userModel.getName());
                    WAILSettings.setLastfmUserInfoUpdateTimestamp(getActivity(), System.currentTimeMillis());
                } catch (Exception e) {
                    EasyTracker.getInstance(getActivity()).send(MapBuilder.createEvent(
                            GA_EVENT_MAIN_FRAGMENT,
                            "refreshDataFromLastfm",
                            "failed: " + e.getMessage(),
                            0L).build());

                    if (e instanceof NetworkException) {
                        networkException = (NetworkException) e;
                    } else if (e instanceof LFApiException) {
                        lfApiException = (LFApiException) e;
                    }
                }

                ThreadUtil.sleepIfRequired(startTime, 1900);

                return userModel;
            }

            @Override
            protected void onPostExecute(LFUserResponseModel userModel) {
                super.onPostExecute(userModel);

                try {
                    pullToRefreshLayout.setRefreshing(false);
                    updateTracksCountFromLastfm();

                    String toast = null;

                    if (networkException != null) {
                        toast = getString(R.string.main_refresh_info_from_lastfm_network_error);
                    } else if (lfApiException != null) {
                        toast = getString(R.string.main_refresh_info_from_lastfm_api_error, lfApiException.getMessage());
                        EasyTracker.getInstance(getActivity()).send(MapBuilder.createEvent(GA_EVENT_MAIN_FRAGMENT,
                                "refreshDataFromLastfm",
                                "failed with LFApiException: " + lfApiException.getMessage(),
                                0L)
                                .build());
                    }

                    if (toast != null) {
                        Toast.makeText(getActivity(), toast, Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    try {
                        final Context context = getActivity();
                        if (isDetached() || isRemoving() || context == null) return;

                        Toast.makeText(context, R.string.main_refresh_info_from_lastfm_unknown_error, Toast.LENGTH_LONG).show();

                        EasyTracker.getInstance(getActivity()).send(MapBuilder.createEvent(GA_EVENT_MAIN_FRAGMENT,
                                "refreshDataFromLastfm",
                                "failed with unknown error",
                                0L)
                                .build());
                    } catch (Exception e1) {
                        // do nothing
                    }
                }

                redrawLastUpdateTime();
            }
        });
    }

    private void loadTrackWordForms() {
        trackWordForms = getResources().getStringArray(R.array.word_form_track);
    }

    private void updateLocalInfo() {
        updateTracksTodayCount();
        redrawLastUpdateTime();
        updateNowScrobblingTrack();
    }

    private void updateTracksTodayCount() {
        AsyncTaskExecutor.executeConcurrently(new AsyncTask<Void, Void, Integer>() {
            @Override
            protected Integer doInBackground(Void... params) {
                try {
                    final Calendar currentDate = Calendar.getInstance();

                    int tracksTodayCount = 0;

                    Cursor tracksCursor = TracksDBHelper.getInstance(getActivity().getApplicationContext())
                            .getAllDesc();

                    if (tracksCursor.moveToFirst()) {
                        do {
                            Track track = TracksDBHelper.parseFromCursor(tracksCursor);
                            final Calendar trackCaptureDate = Calendar.getInstance();
                            trackCaptureDate.setTimeInMillis(track.getTimestamp());

                            if (trackCaptureDate.get(Calendar.YEAR) == currentDate.get(Calendar.YEAR)
                                    && trackCaptureDate.get(Calendar.MONTH) == currentDate.get(Calendar.MONTH)
                                    && trackCaptureDate.get(Calendar.DAY_OF_MONTH) == currentDate.get(Calendar.DAY_OF_MONTH)) {
                                tracksTodayCount++;
                            }
                        } while (tracksCursor.moveToNext());
                    }

                    tracksCursor.close();

                    return tracksTodayCount;
                } catch (Exception e) {
                    return -1;
                }
            }

            @Override
            protected void onPostExecute(Integer tracksTodayCount) {
                if (!isDetached()) {
                    try {
                        tracksTodayCountTextView.setText(String.valueOf(tracksTodayCount));
                        tracksTodayCountLabelTextView.setText(
                                WordFormUtil.getWordForm(tracksTodayCount, trackWordForms) + " " + getString(R.string.main_today)
                        );
                    } catch (Exception e) {
                        Loggi.e("MainFragment updateTracksTodayCount() exception: " + e.getMessage());
                    }
                }

            }
        });
    }

    private void updateTracksCountFromLastfm() {
        final LFUserResponseModel userModel = WAILSettings.getLastfmUserInfo(getActivity());

        if (userModel == null || userModel.getPlayCount() == -1) {
            tracksTotalCountOnLastfmTextView.setVisibility(View.GONE);
            tracksTotalCountOnLastfmLabelTextView.setVisibility(View.GONE);
            tracksTotalCountOnLastfmLabelUnknownTextView.setVisibility(View.VISIBLE);
            tracksTotalCountOnLastfmLabelUnknownTextView.setText(R.string.main_tracks_on_last_fm_unknown);
        } else {
            tracksTotalCountOnLastfmTextView.setVisibility(View.VISIBLE);
            tracksTotalCountOnLastfmLabelTextView.setVisibility(View.VISIBLE);
            tracksTotalCountOnLastfmLabelUnknownTextView.setVisibility(View.GONE);

            tracksTotalCountOnLastfmTextView.setText(String.valueOf(userModel.getPlayCount()));
            tracksTotalCountOnLastfmLabelTextView.setText(
                    WordFormUtil.getWordForm(userModel.getPlayCount(), trackWordForms) + " " + getString(R.string.main_tracks_on_last_fm)
            );
        }
    }

    private void updateNowScrobblingTrack() {
        Track nowScrobblingTrack = WAILSettings.getNowScrobblingTrack(getActivity());
        String label = WAILSettings.getNowScrobblingPlayerLabel(getActivity());
        String packageName = WAILSettings.getNowScrobblingPlayerPackageName(getActivity());
        final String nowScrobblingPlayer = label != null ? label : packageName;

        if (nowScrobblingTrack != null) {
            nowScrobblingTrackTextView.setText(getString(
                            R.string.main_now_scrobbling_label,
                            nowScrobblingTrack.getArtist() + " - " + nowScrobblingTrack.getTrack())
            );
            nowScrobblingPlayerTextView.setText(String.format(getString(R.string.main_scrobbling_from_player_label), nowScrobblingPlayer));
            if (loveCurrentTrackButton.getVisibility() != View.VISIBLE) {
                loveCurrentTrackButton.setVisibility(View.VISIBLE);
            }
            loveCurrentTrackButton.show();
            ignorePlayerButton.setVisibility(View.VISIBLE);
        } else {
            nowScrobblingTrackTextView.setText(getString(R.string.main_now_scrobbling_label, getString(R.string.main_now_scrobbling_nothing)));
            nowScrobblingPlayerTextView.setText("");
            loveCurrentTrackButton.hide();
            ignorePlayerButton.setVisibility(View.GONE);
        }
    }

    private void redrawLastUpdateTime() {
        try {
            final long lastUpdateTime = WAILSettings.getLastfmUserInfoUpdateTimestamp(getActivity());

            if (lastUpdateTime == -1) {
                lastfmUserInfoUpdateTimeTextView.setText("");
                return;
            }

            final Calendar lastUpdateDate = Calendar.getInstance();
            lastUpdateDate.setTimeInMillis(lastUpdateTime);

            final String text;

            final long timeDiff = System.currentTimeMillis() - lastUpdateTime;

            if (timeDiff < 86400000) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
                text = getString(R.string.main_updated_today_at, dateFormat.format(lastUpdateDate.getTime()));
            } else if (timeDiff >= 86400000 && timeDiff <= 172800000) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
                text = getString(R.string.main_updated_yesterday_at, dateFormat.format(lastUpdateDate.getTime()));
            } else {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd at HH:mm", Locale.getDefault());
                text = getString(R.string.main_updated_common, dateFormat.format(lastUpdateDate.getTime()));
            }

            lastfmUserInfoUpdateTimeTextView.setText(text);
        } catch (Exception e) {
            lastfmUserInfoUpdateTimeTextView.setText("");
        }
    }

    private class PullToRefreshListener implements SwipeRefreshLayout.OnRefreshListener {

        @Override
        public void onRefresh() {
            Toast.makeText(getActivity(), getString(R.string.main_refreshing), Toast.LENGTH_SHORT).show();
            refreshDataFromLastfm();
            EasyTracker.getInstance(getActivity()).send(MapBuilder.createEvent(
                    GA_EVENT_MAIN_FRAGMENT,
                    "pullToRefresh",
                    null,
                    1L).build());
        }
    }
}
