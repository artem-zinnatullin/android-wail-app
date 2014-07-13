package com.artemzin.android.wail.service;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.SystemClock;

import com.artemzin.android.wail.api.lastfm.LFApiException;
import com.artemzin.android.wail.api.lastfm.LFTrackApi;
import com.artemzin.android.wail.api.lastfm.model.request.LFTrackRequestModel;
import com.artemzin.android.wail.api.network.NetworkException;
import com.artemzin.android.wail.receiver.music.CommonMusicAppReceiver;
import com.artemzin.android.wail.sound.SoundNotificationsManager;
import com.artemzin.android.wail.storage.db.TracksDBHelper;
import com.artemzin.android.wail.storage.WAILSettings;
import com.artemzin.android.wail.storage.model.Track;
import com.artemzin.android.wail.ui.fragment.main.MainFragment;
import com.artemzin.android.wail.util.AsyncTaskExecutor;
import com.artemzin.android.wail.util.IntentUtil;
import com.artemzin.android.wail.util.Loggi;
import com.artemzin.android.wail.util.NetworkUtil;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WAILService extends Service {

    private static final String GA_EVENT_SCROBBLE_TO_THE_LASTFM    = "scrobbleToTheLastfm";
    private static final String GA_EVENT_UPDATE_LASTFM_NOW_PLAYING = "updateLastfmNowplaying";

    public static final String INTENT_ACTION_HANDLE_TRACK                 = "INTENT_ACTION_HANDLE_TRACK";
    public static final String INTENT_ACTION_SCROBBLE_PENDING_TRACKS      = "INTENT_ACTION_SCROBBLE_PENDING_TRACKS";

    private static final int DEFAULT_TRACK_DURATION_IF_UNKNOWN_SECONDS = 210;

    private static volatile com.artemzin.android.wail.storage.model.Track lastUpdatedNowPlayingTrackInfo;

    private LocalBroadcast localBroadcast = LocalBroadcast.getInstance();

    private long lastScrobbleTime = 0;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Loggi.i("WAILService onCreate()");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Loggi.i("WAILService.onStartCommand() " + IntentUtil.getIntentAsString(intent));

        if (intent == null) {
            // seems that system has recreated the service, if so
            // we should return START_STICKY
            return START_STICKY;
        }

        final String action = intent.getAction();

        if (action == null) {
            // null intent action
            return START_STICKY;
        }

        if (action.equals(INTENT_ACTION_HANDLE_TRACK)) {
            handleTrack(intent);
        } else if (action.equals(INTENT_ACTION_SCROBBLE_PENDING_TRACKS)) {
            scrobblePendingTracks(false);
        } else {
            // unknown intent action
        }

        return START_STICKY;
    }

    private void handleTrack(final Intent intent) {
        if (intent == null || !WAILSettings.isEnabled(this)) {
            Loggi.w("WAILService track is not handled because WAIL is disabled");
            return;
        }

        AsyncTaskExecutor.executeConcurrently(new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                Loggi.i("\n\n----------\nWAILService track handling: " + intent);

                mayBeCleanDB();

                final String extraAction = intent.getStringExtra(CommonMusicAppReceiver.EXTRA_ACTION);

                if (extraAction == null || extraAction.lastIndexOf('.') == -1) {
                    Loggi.e("Can not handle track without player package name");
                    return null;
                }

                final boolean isCurrentTrackPlaying = intent.getBooleanExtra(CommonMusicAppReceiver.EXTRA_PLAYING, false);
                final Track currentTrack = CommonMusicAppReceiver.parseFromIntentExtras(intent);

                if (isCurrentTrackPlaying) {
                    updateNowPlaying(currentTrack);
                    LocalBroadcast.getInstance().setCurrentTrack(currentTrack);
                } else {
                    LocalBroadcast.getInstance().setCurrentTrack(null);
                }

                final LastCapturedTrackInfo mLastCapturedTrackInfo = WAILSettings.getLastCapturedTrackInfo(getApplicationContext());

                if (mLastCapturedTrackInfo == null) {
                    // just add track in lastCapturedTrackInfo
                } else {
                    final long trackPlayingDurationInMillis = currentTrack.getTimestamp() - mLastCapturedTrackInfo.getTrack().getTimestamp();
                    final long minTrackDurationInMillis = WAILSettings.getMinTrackDurationInSeconds(getApplicationContext()) * 1000;

                    if ((!isCurrentTrackPlaying && mLastCapturedTrackInfo.isPlaying()) || mLastCapturedTrackInfo.isPlaying()) {
                        if (trackPlayingDurationInMillis >= minTrackDurationInMillis) {
                            Loggi.w("Track playing duration in millis == " + trackPlayingDurationInMillis + " >= min track duration in millis == " + (WAILSettings.getMinTrackDurationInSeconds(getApplicationContext()) * 1000));

                            mLastCapturedTrackInfo.getTrack().setStateTimestamp(System.currentTimeMillis());
                            addTrackToDB(mLastCapturedTrackInfo.getTrack());
                        } else {
                            if (mLastCapturedTrackInfo.getTrack().getDuration() != -1) {
                                final int trackDurationInPercents = (int) (100 * trackPlayingDurationInMillis / (mLastCapturedTrackInfo.getTrack().getDuration() + 2500));

                                if (trackDurationInPercents >= WAILSettings.getMinTrackDurationInPercents(getApplicationContext())) {
                                    Loggi.w("Track playing duration in millis == " + trackPlayingDurationInMillis + ", in % == " + trackDurationInPercents + " >= min track duration in % == " + WAILSettings.getMinTrackDurationInPercents(getApplicationContext()));
                                    mLastCapturedTrackInfo.getTrack().setStateTimestamp(System.currentTimeMillis());
                                    addTrackToDB(mLastCapturedTrackInfo.getTrack());
                                } else {
                                    Loggi.w("Track playing duration is too small to scrobble it! Track duration in millis == " + trackPlayingDurationInMillis + ", in percents == " + trackDurationInPercents + "%, min duration == " + WAILSettings.getMinTrackDurationInPercents(getApplicationContext()));
                                    SoundNotificationsManager.getInstance(getApplicationContext()).playTrackSkippedSound();
                                }
                            } else {
                                Loggi.w("Skipping track, playing duration in millis == " + trackPlayingDurationInMillis + ", it does not played required time(" + minTrackDurationInMillis + " millis) and WAIL can not calculate playing time in percents");
                            }
                        }
                    } else {
                        Loggi.w("Skipping track");
                    }
                }

                WAILSettings.setLastCapturedTrackInfo(getApplicationContext(), new LastCapturedTrackInfo(currentTrack, isCurrentTrackPlaying));

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                scrobblePendingTracks(false);
            }
        });
    }

    private synchronized void addTrackToDB(com.artemzin.android.wail.storage.model.Track track) {
        com.artemzin.android.wail.storage.model.Track lastAddedTrack = TracksDBHelper.getInstance(getApplicationContext())
                .getLastAddedTrack();


        if (lastAddedTrack != null) {
            final long pauseBetweenTracksInSeconds = (track.getTimestamp() - lastAddedTrack.getTimestamp()) / 1000;
            if (pauseBetweenTracksInSeconds < 10) {
                Loggi.w("Too small pause between tracks " + pauseBetweenTracksInSeconds + " seconds, skipping track: " + track);
                return;
            } else {
                Loggi.w("Pause between tracks is ok " + pauseBetweenTracksInSeconds + " seconds");
            }
        }

        if (TracksDBHelper.getInstance(WAILService.this).add(track) != -1) {
            WAILSettings.setTotalHandledTracksCount(
                    WAILService.this,
                    WAILSettings.getTotalHandledTracksCount(WAILService.this) + 1
            );

            Loggi.w("Track has been written to db: " + track);
            SoundNotificationsManager.getInstance(getApplicationContext()).playTrackMarkedAsScrobbledSound();
        } else {
            Loggi.e("Track was not written to db: " + track);
        }
    }

    private void scrobblePendingTracks(boolean forceScrobble) {
        if (!forceScrobble && (lastScrobbleTime != 0 && SystemClock.elapsedRealtime() - lastScrobbleTime < 30000)) {
            Loggi.w("WAILService lastScrobble request was less than 30 seconds from current, skipping scrobble");
            return;
        }

        if (!NetworkUtil.isAvailable(this)) {
            Loggi.e("WAILService scrobblePendingTracks() stopped, network is not available");
            return;
        }

        lastScrobbleTime = SystemClock.elapsedRealtime();

        AsyncTaskExecutor.executeConcurrently(new AsyncTask<Void, Void, Void>() {

            @Override
            protected void onPreExecute() {
                Loggi.w("WAILService going to scrobble pending tracks to Last.fm");
            }

            @Override
            protected Void doInBackground(Void... params) {
                Cursor tracksCursor = TracksDBHelper.getInstance(getApplicationContext()).getAllDesc();

                final List<com.artemzin.android.wail.storage.model.Track> tracksToScrobbleListForDB = new ArrayList<com.artemzin.android.wail.storage.model.Track>();
                final List<LFTrackRequestModel> tracksToScrobbleForApiRequest = new ArrayList<LFTrackRequestModel>();

                boolean isTracksToScrobbleCountMoreThanMaxForRequest = false;

                if (tracksCursor.getCount() == 0) {
                    Loggi.w("Nothing to scrobble");
                    tracksCursor.close();
                    return null;
                }

                if (tracksCursor.moveToFirst()) {
                    do {
                        com.artemzin.android.wail.storage.model.Track track = TracksDBHelper.parseFromCursor(tracksCursor);

                        if (tracksToScrobbleForApiRequest.size() >= 48) {
                            isTracksToScrobbleCountMoreThanMaxForRequest = true;
                            break; // max count of tracks to scrobble by one request is 50, but we use 48
                        }

                        if (track.getState() == com.artemzin.android.wail.storage.model.Track.STATE_WAITING_FOR_SCROBBLE
                                || track.getState() == com.artemzin.android.wail.storage.model.Track.STATE_SCROBBLE_ERROR) {

                            track.setState(com.artemzin.android.wail.storage.model.Track.STATE_SCROBBLING);
                            track.setStateTimestamp(System.currentTimeMillis());
                            tracksToScrobbleListForDB.add(track);

                            tracksToScrobbleForApiRequest.add(new LFTrackRequestModel(track));
                        }
                    } while (tracksCursor.moveToNext());
                }

                tracksCursor.close();

                if (tracksToScrobbleListForDB.size() != 0) {
                    TracksDBHelper.getInstance(WAILService.this).updateAll(tracksToScrobbleListForDB);
                }

                if (tracksToScrobbleForApiRequest.size() == 0) {
                    Loggi.w("WAILService all tracks marked as scrobbled, skipping scrobble");
                    return null;
                }

                try {
                    final String result = LFTrackApi.scrobble(
                            WAILSettings.getLastfmSessionKey(WAILService.this),
                            WAILSettings.getLastfmApiKey(),
                            WAILSettings.getLastfmSecret(),
                            tracksToScrobbleForApiRequest
                    );

                    Loggi.w("WAILService tracks scrobbling succeed! Response: " + result);

                    for (com.artemzin.android.wail.storage.model.Track track : tracksToScrobbleListForDB) {
                        track.setState(com.artemzin.android.wail.storage.model.Track.STATE_SCROBBLE_SUCCESS);
                        track.setStateTimestamp(System.currentTimeMillis());
                    }

                    TracksDBHelper.getInstance(WAILService.this).updateAll(tracksToScrobbleListForDB);

                    EasyTracker.getInstance(WAILService.this).send(
                            MapBuilder.createEvent(GA_EVENT_SCROBBLE_TO_THE_LASTFM,
                                    "success",
                                    null,
                                    (long) tracksToScrobbleListForDB.size())
                            .build()
                    );

                    if (isTracksToScrobbleCountMoreThanMaxForRequest) {
                        Loggi.w("scrobble started again with force flag, because tracks count > max tracks per request");

                        EasyTracker.getInstance(WAILService.this).send(
                                MapBuilder.createEvent(GA_EVENT_SCROBBLE_TO_THE_LASTFM,
                                        "started again because counts of track was too big",
                                        null,
                                        0L)
                                        .build()
                        );

                        scrobblePendingTracks(true);
                    }
                } catch (NetworkException e) {
                    Loggi.e("WAILService tracks scrobbling to Last.fm failed with network error: " + e.getMessage());

                    for (com.artemzin.android.wail.storage.model.Track track : tracksToScrobbleListForDB) {
                        track.setState(com.artemzin.android.wail.storage.model.Track.STATE_SCROBBLE_ERROR);
                        track.setStateTimestamp(System.currentTimeMillis());
                    }

                    TracksDBHelper.getInstance(WAILService.this).updateAll(tracksToScrobbleListForDB);

                    EasyTracker.getInstance(WAILService.this).send(
                            MapBuilder.createEvent(GA_EVENT_SCROBBLE_TO_THE_LASTFM,
                                    "failed with NetworkException: " + e.getMessage(),
                                    null,
                                    0L)
                            .build()
                    );
                } catch (LFApiException e) {
                    Loggi.e("WAILService tracks scrobbling to Last.fm failed with api error: " + e.getMessage());

                    for (com.artemzin.android.wail.storage.model.Track track : tracksToScrobbleListForDB) {
                        track.setState(com.artemzin.android.wail.storage.model.Track.STATE_SCROBBLE_ERROR);
                        track.setStateTimestamp(System.currentTimeMillis());
                    }

                    TracksDBHelper.getInstance(WAILService.this).updateAll(tracksToScrobbleListForDB);

                    EasyTracker.getInstance(WAILService.this).send(
                            MapBuilder.createEvent(GA_EVENT_SCROBBLE_TO_THE_LASTFM,
                                    "failed with LFApiException: " + e.getMessage(),
                                    null,
                                    0L)
                            .build()
                    );
                }

                return null;
            }
        });
    }

    private synchronized void updateNowPlaying(Track track) {
        if (track == null) {
            Loggi.w("WAILService.updateNowPlaying() track is null, skipping");
            return;
        }

        if (!NetworkUtil.isAvailable(getApplicationContext())) {
            Loggi.w("WAILService.updateNowPlaying() network is not available, update skipped: " + track);
            return;
        }

        final Track mLastUpdatedNowPlayingTrackInfo = lastUpdatedNowPlayingTrackInfo;

        if (mLastUpdatedNowPlayingTrackInfo != null) {
            if (System.currentTimeMillis() - mLastUpdatedNowPlayingTrackInfo.getStateTimestamp() < 10000
                    && mLastUpdatedNowPlayingTrackInfo.specialEquals(track)) {
                Loggi.w("WAILService.updateNowPlaying() skipping nowplaying update, too small pause for track: " + track);
                return;
            }
        }

        lastUpdatedNowPlayingTrackInfo = track.copy();
        lastUpdatedNowPlayingTrackInfo.setStateTimestamp(System.currentTimeMillis());

        if (WAILSettings.isLastfmNowplayingUpdateEnabled(getApplicationContext())) {
            updateLastfmNowplaying(track);
        } else {
            Loggi.w("WAILService.updateNowPlaying() last.fm nowplaying updates disabled, skipping track: " + track);
        }
    }

    private synchronized void updateLastfmNowplaying(final com.artemzin.android.wail.storage.model.Track track) {
        Loggi.i("WAILService going to update last.fm nowplaying with track: " + track);

        AsyncTaskExecutor.executeConcurrently(new AsyncTask<Object, Object, Object>() {
            @Override
            protected Object doInBackground(Object... params) {
                try {
                    final LFTrackRequestModel trackForRequest = new LFTrackRequestModel(track);

                    if (trackForRequest.getDuration() == null || trackForRequest.getDuration() <= 0) {
                        trackForRequest.setDuration(DEFAULT_TRACK_DURATION_IF_UNKNOWN_SECONDS);
                    }

                    Loggi.w("Result: " + LFTrackApi.updateNowPlaying(
                            WAILSettings.getLastfmSessionKey(getApplicationContext()),
                            WAILSettings.getLastfmApiKey(),
                            WAILSettings.getLastfmSecret(),
                            trackForRequest)
                    );

                    EasyTracker.getInstance(getApplicationContext()).send(
                            MapBuilder.createEvent(GA_EVENT_UPDATE_LASTFM_NOW_PLAYING,
                                    "success",
                                    null,
                                    1L)
                                    .build()
                    );
                } catch (NetworkException e) {
                    Loggi.e("Can not update last.fm nowplaying with track: " + track + ", exception: " + e.getMessage());

                    EasyTracker.getInstance(getApplicationContext()).send(
                            MapBuilder.createEvent(GA_EVENT_UPDATE_LASTFM_NOW_PLAYING,
                                    "failed with NetworkException: " + e.getMessage(),
                                    null,
                                    0L)
                                    .build()
                    );
                } catch (LFApiException e) {
                    Loggi.e("Can not update last.fm nowplaying with track: " + track + ", exception: " + e.getMessage());

                    EasyTracker.getInstance(getApplicationContext()).send(
                            MapBuilder.createEvent(GA_EVENT_UPDATE_LASTFM_NOW_PLAYING,
                                    "failed with LFApiException: " + e.getMessage(),
                                    null,
                                    0L)
                                    .build()
                    );
                }

                return null;
            }
        });
    }

    private void mayBeCleanDB() {
        // 30% cases
        if (new Random(System.currentTimeMillis()).nextInt(100) > 70) {
            try {
                final int removedTracksCount = TracksDBHelper.getInstance(getApplicationContext())
                        .removeOldOrInconsistentTracks(200);
                Loggi.w("Removed old tracks, count: " + removedTracksCount);
            } catch (Exception e) {
                Loggi.e("Could not remove old tracks: " + e.getMessage());
                EasyTracker.getInstance(getApplication()).send(MapBuilder.createException(
                        "removeOldOrInconsistentTracks failed: " + e.getMessage(), false)
                        .build());
            }
        }
    }

    public static class LastCapturedTrackInfo {

        private com.artemzin.android.wail.storage.model.Track track;
        private boolean isPlaying;

        public LastCapturedTrackInfo(com.artemzin.android.wail.storage.model.Track track, boolean isPlaying) {
            this.track = track;
            this.isPlaying = isPlaying;
        }

        public com.artemzin.android.wail.storage.model.Track getTrack() {
            return track;
        }

        public boolean isPlaying() {
            return isPlaying;
        }

        public String toJSON() {
            final JSONObject json = new JSONObject();

            try {
                json.put("playerPackageName", track.getPlayerPackageName());
                json.put("track", track.getTrack());
                json.put("artist", track.getArtist());
                json.put("album", track.getAlbum());
                json.put("duration", track.getDuration());
                json.put("timestamp", track.getTimestamp());
                json.put("state", track.getState());
                json.put("stateTimestamp", track.getStateTimestamp());
                json.put("isPlaying", isPlaying);
            } catch (Exception e) {
                return null;
            }

            return json.toString();
        }

        public static LastCapturedTrackInfo fromJSON(String jsonString) {
            try {
                final JSONObject json = new JSONObject(jsonString);
                final com.artemzin.android.wail.storage.model.Track track = new com.artemzin.android.wail.storage.model.Track();

                track.setPlayerPackageName(json.optString("playerPackageName"));
                track.setTrack(json.optString("track"));
                track.setArtist(json.optString("artist"));
                track.setAlbum(json.optString("album"));
                track.setDuration(json.optLong("duration"));
                track.setTimestamp(json.optLong("timestamp"));
                track.setState(json.optInt("state"));
                track.setStateTimestamp(json.optLong("stateTimestamp"));

                return new LastCapturedTrackInfo(track, json.optBoolean("isPlaying"));
            } catch (Exception e) {
                return null;
            }
        }
    }
}
