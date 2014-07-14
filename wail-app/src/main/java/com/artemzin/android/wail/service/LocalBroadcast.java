package com.artemzin.android.wail.service;

import com.artemzin.android.wail.storage.model.Track;
import com.artemzin.android.wail.ui.fragment.main.MainFragment;

public class LocalBroadcast {
    private static LocalBroadcast instance;
    private Track currentTrack;
    private MainFragment listener;

    private LocalBroadcast() {}

    public synchronized static LocalBroadcast getInstance() {
        if (instance == null) {
            instance = new LocalBroadcast();
        }
        return instance;
    }

    public Track getCurrentTrack() {
        return currentTrack;
    }

    public void setCurrentTrack(Track currentTrack) {
        this.currentTrack = currentTrack;
        if (listener != null) {
            listener.onTrackReceived(currentTrack);
        }
    }

    public void setListener(MainFragment listener) {
        this.listener = listener;
    }

    public interface LocalBroadcastListener {
        public void onTrackReceived(Track track);
    }
}
