package com.artemzin.android.wail.ui.fragment.main;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.artemzin.android.bytes.ui.DisplayUnitsConverter;
import com.artemzin.android.bytes.ui.ViewUtil;
import com.artemzin.android.wail.R;
import com.artemzin.android.wail.api.lastfm.LFApiException;
import com.artemzin.android.wail.api.lastfm.LFUserApi;
import com.artemzin.android.wail.api.lastfm.model.response.LFUserResponseModel;
import com.artemzin.android.wail.api.network.NetworkException;
import com.artemzin.android.wail.storage.db.TracksDBHelper;
import com.artemzin.android.wail.storage.WAILSettings;
import com.artemzin.android.wail.storage.model.Track;
import com.artemzin.android.wail.ui.fragment.BaseFragment;
import com.artemzin.android.wail.util.AsyncTaskExecutor;
import com.artemzin.android.wail.util.Loggi;
import com.artemzin.android.wail.util.ThreadUtil;
import com.artemzin.android.wail.util.WordFormUtil;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.DefaultHeaderTransformer;
import uk.co.senab.actionbarpulltorefresh.library.Options;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

public class MainFragment extends BaseFragment implements View.OnClickListener {

    private static final String GA_EVENT_MAIN_FRAGMENT = "MainFragment";

    private PullToRefreshLayout pullToRefreshLayout;
    private TextView tracksTodayCountTextView,
            tracksTodayCountLabelTextView,
            nowScrobblingTrackTextView,
            tracksTotalCountOnLastfmTextView,
            tracksTotalCountOnLastfmLabelTextView,
            lastfmUserInfoUpdateTimeTextView;
    private View feedbackPleaseView;
    private String[] trackWordForms;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().getActionBar().setTitle(getString(R.string.main_ab_title));
        loadTrackWordForms();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final Activity activity = getActivity();

        pullToRefreshLayout = (PullToRefreshLayout) view.findViewById(R.id.main_pull_to_refresh_layout);
        ActionBarPullToRefresh.from(activity)
                .allChildrenArePullable()
                .listener(new PullToRefreshListener())
                .options(Options.create()
                            .headerTransformer(new PullToRefreshHeaderTransformer())
                            .build())
                .setup(pullToRefreshLayout);

        view.findViewById(R.id.main_tracks_today_view).setOnClickListener(this);

        tracksTodayCountTextView      = (TextView) view.findViewById(R.id.main_tracks_today_count_text_view);
        tracksTodayCountLabelTextView = (TextView) view.findViewById(R.id.main_tracks_today_count_label_text_view);

        nowScrobblingTrackTextView = (TextView) view.findViewById(R.id.main_now_scrobbling_track_text_view);

        tracksTotalCountOnLastfmTextView      = (TextView) view.findViewById(R.id.main_tracks_total_count_text_view);
        tracksTotalCountOnLastfmLabelTextView = (TextView) view.findViewById(R.id.main_tracks_total_count_label_text_view);

        lastfmUserInfoUpdateTimeTextView      = (TextView) view.findViewById(R.id.main_last_fm_user_info_update_time);

        feedbackPleaseView = view.findViewById(R.id.main_feedback_please);

        if (WAILSettings.isShowFeedbackRequest(activity)) {
            ViewUtil.setVisibility(feedbackPleaseView, true);
        }

        feedbackPleaseView.setOnClickListener(this);
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
                    pullToRefreshLayout.setRefreshComplete();
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
            tracksTotalCountOnLastfmTextView.setText("");
            tracksTotalCountOnLastfmLabelTextView.setText(R.string.main_tracks_on_last_fm_unknown);
        } else {
            tracksTotalCountOnLastfmTextView.setText(String.valueOf(userModel.getPlayCount()));
            tracksTotalCountOnLastfmLabelTextView.setText(
                    WordFormUtil.getWordForm(userModel.getPlayCount(), trackWordForms) + " " + getString(R.string.main_tracks_on_last_fm)
            );
        }
    }

    private void updateNowScrobblingTrack() {
        String nowScrobblingTrack = WAILSettings.getNowScrobblingTrack(getActivity().getApplicationContext());
        if (nowScrobblingTrack != null) {
            nowScrobblingTrackTextView.setText(getString(R.string.main_now_scrobbling_label, nowScrobblingTrack));
        } else {
            nowScrobblingTrackTextView.setText(getString(R.string.main_now_scrobbling_label, getString(R.string.main_now_scrobbling_nothing)));
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
                SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
                text = getString(R.string.main_updated_today_at, dateFormat.format(lastUpdateDate.getTime()));
            } else if (timeDiff >= 86400000 && timeDiff <= 172800000) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
                text = getString(R.string.main_updated_yesterday_at, dateFormat.format(lastUpdateDate.getTime()));
            } else {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd at HH:mm");
                text = getString(R.string.main_updated_common, dateFormat.format(lastUpdateDate.getTime()));
            }

            lastfmUserInfoUpdateTimeTextView.setText(text);
        } catch (Exception e) {
            lastfmUserInfoUpdateTimeTextView.setText("");
        }
    }

    private final BroadcastReceiver tracksChangedBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateLocalInfo();
        }
    };

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.main_tracks_today_view) {
            onTracksTodayViewClick();
        } else if (v.getId() == R.id.main_feedback_please) {
            onFeedbackPleaseClick();
        }
    }

    private void onTracksTodayViewClick() {
        Toast.makeText(getActivity(), getString(R.string.main_pull_down_to_refresh_toast), Toast.LENGTH_SHORT).show();
    }

    private void onFeedbackPleaseClick() {
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

    private class PullToRefreshListener implements OnRefreshListener {

        @Override
        public void onRefreshStarted(View view) {
            refreshDataFromLastfm();
            EasyTracker.getInstance(getActivity()).send(MapBuilder.createEvent(
                    GA_EVENT_MAIN_FRAGMENT,
                    "pullToRefresh",
                    null,
                    1L).build());
        }
    }

    private static class PullToRefreshHeaderTransformer extends DefaultHeaderTransformer {

        @Override
        public void onViewCreated(Activity activity, View headerView) {
            super.onViewCreated(activity, headerView);

            setProgressBarColor(Color.parseColor("#FFFFFF"));
            setProgressBarHeight((int) DisplayUnitsConverter.dpToPx(activity, 3));

            setPullText(activity.getString(R.string.main_pull_to_refresh_pull_text));
            setRefreshingText(activity.getString(R.string.main_pull_to_refresh_refreshing_text));
            setReleaseText(activity.getString(R.string.main_pull_to_refresh_release_text));
        }
    }
}
