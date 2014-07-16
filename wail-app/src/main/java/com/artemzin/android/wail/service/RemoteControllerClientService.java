package com.artemzin.android.wail.service;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.RemoteControlClient;
import android.media.RemoteController;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

import com.artemzin.android.bytes.common.StringUtil;
import com.artemzin.android.wail.receiver.music.CommonMusicAppReceiver;
import com.artemzin.android.wail.storage.model.Track;
import com.artemzin.android.wail.util.Loggi;

/**
 * @author Artem Zinnatullin [artem.zinnatullin@gmail.com]
 */
@TargetApi(19)
public class RemoteControllerClientService extends NotificationListenerService implements RemoteController.OnClientUpdateListener {

    private RemoteController remoteController;

    private Boolean lastIsPlayingState;
    private Track lastTrackInfo;

    @Override
    public void onClientChange(boolean clearing) {
        Loggi.w("RemoteControllerClientService.onClientChange, clearing: " + clearing);

        if (clearing) {
            lastIsPlayingState = false;
            mayBeSendTrackToWAILService();
        }
    }

    @Override
    public void onClientPlaybackStateUpdate(int state) {
        Loggi.w("RemoteControllerClientService.onClientPlaybackStateUpdate() state: " + getStateDescription(state));
        lastIsPlayingState = isStatePlaying(state);
        mayBeSendTrackToWAILService();
    }

    @Override
    public void onClientPlaybackStateUpdate(int state, long stateChangeTimeMs, long currentPosMs, float speed) {
        Loggi.w("RemoteControllerClientService.onClientPlaybackStateUpdate()  state: " + getStateDescription(state) + ", stateChangeTimeMs: " + stateChangeTimeMs + ", currentPosMs: " + currentPosMs);
        lastIsPlayingState = isStatePlaying(state);
        mayBeSendTrackToWAILService();
    }

    @Override
    public void onClientTransportControlUpdate(int transportControlFlags) {
        Loggi.w("RemoteControllerClientService.onClientTransportControlUpdate: " + transportControlFlags);
    }

    @Override
    public void onClientMetadataUpdate(RemoteController.MetadataEditor metadataEditor) {
        Loggi.w("RemoteControllerClientService.onClientMetadataUpdate: " + metadataEditor);

        Track track = new Track();

        String title = metadataEditor.getString(MediaMetadataRetriever.METADATA_KEY_TITLE, null);
        String artist = metadataEditor.getString(MediaMetadataRetriever.METADATA_KEY_ARTIST, null);
        String albumArtist = metadataEditor.getString(MediaMetadataRetriever.METADATA_KEY_ALBUMARTIST, null);

        String album = metadataEditor.getString(MediaMetadataRetriever.METADATA_KEY_ALBUM, null);
        long duration = metadataEditor.getLong(MediaMetadataRetriever.METADATA_KEY_DURATION, -1);

        track.setTrack(title);

        if (!StringUtil.isNullOrEmpty(artist)) {
            track.setArtist(artist);
        } else {
            track.setArtist(albumArtist);
        }

        track.setAlbum(album);
        track.setDuration(duration);

        Loggi.w("RemoteController artist: " + artist + ", title: " + title + ", album: " + album + ", albumArtist: " + albumArtist + ", duration: " + duration);

        if (!track.specialEquals(lastTrackInfo)) {
            lastTrackInfo = track;
            mayBeSendTrackToWAILService();
        } else {
            lastTrackInfo = track; // just update info
        }
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        Loggi.w("RemoteControllerClientService.onNotificationPosted: " + sbn);
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        Loggi.w("RemoteControllerClientService.onNotificationRemoved: " + sbn);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        remoteController = new RemoteController(getApplicationContext(), this);

        if (((AudioManager) getSystemService(Context.AUDIO_SERVICE)).registerRemoteController(remoteController)) {
            Loggi.w("RemoteControllerClientService registered remote controller");
        } else {
            Loggi.w("RemoteControllerClientService can not register remote controller");
        }
    }

    private static String getStateDescription(int state) {
        final String stateStr;

        if (state == RemoteControlClient.PLAYSTATE_PAUSED) {
            stateStr = "paused";
        } else if (state == RemoteControlClient.PLAYSTATE_PLAYING) {
            stateStr = "playing";
        } else if (state == RemoteControlClient.PLAYSTATE_STOPPED) {
            stateStr = "stopped";
        } else {
            stateStr = "unknown";
        }

        return stateStr;
    }

    private static Boolean isStatePlaying(int state) {
        if (state == RemoteControlClient.PLAYSTATE_PLAYING) {
            return true;
        } else if (state == RemoteControlClient.PLAYSTATE_PAUSED || state == RemoteControlClient.PLAYSTATE_STOPPED) {
            return false;
        }

        return null;
    }

    private void mayBeSendTrackToWAILService() {
        Track track = lastTrackInfo;
        Boolean isPlaying = lastIsPlayingState;

        if (track != null && isPlaying != null ) {
            try {
                getApplicationContext().startService(prepareIntentForWAILService(track, isPlaying));
            } catch (Exception e) {
                Loggi.e("RemoteControllerClientService.mayBeSendTrackToWAILService() " + e);
            }
        }
    }

    private Intent prepareIntentForWAILService(Track track, boolean isPlaying) {
        Intent intentForWAILService = new Intent(getApplicationContext(), WAILService.class);
        intentForWAILService.setAction(WAILService.INTENT_ACTION_HANDLE_TRACK);
        CommonMusicAppReceiver.addTrackDataToTheIntent(track, intentForWAILService);
        intentForWAILService.putExtra(CommonMusicAppReceiver.EXTRA_TIMESTAMP, System.currentTimeMillis());
        intentForWAILService.putExtra(CommonMusicAppReceiver.EXTRA_PLAYING, isPlaying);
        intentForWAILService.putExtra(CommonMusicAppReceiver.EXTRA_PLAYER_PACKAGE_NAME, "com.artemzin.android.wail.remote_controller_client");
        intentForWAILService.putExtra(CommonMusicAppReceiver.EXTRA_ACTION, "com.artemzin.android.wail.remote_controller_client.update");
        return intentForWAILService;
    }
}
