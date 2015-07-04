package com.artemzin.android.wail.storage.model;

import com.artemzin.android.bytes.common.StringUtil;

public class Track {

    public static final int STATE_WAITING_FOR_SCROBBLE = 0;
    public static final int STATE_SCROBBLING = 1;
    public static final int STATE_SCROBBLE_SUCCESS = 2;
    public static final int STATE_SCROBBLE_ERROR = 3;

    private long internalDBId;
    private String playerPackageName;
    private String track;
    private String artist;
    private String album;
    private long duration;
    private long timestamp;
    private int state = STATE_WAITING_FOR_SCROBBLE;
    private long stateTimestamp;

    public long getInternalDBId() {
        return internalDBId;
    }

    public void setInternalDBId(long internalDBId) {
        this.internalDBId = internalDBId;
    }

    public String getPlayerPackageName() {
        return playerPackageName;
    }

    public void setPlayerPackageName(String playerPackageName) {
        this.playerPackageName = playerPackageName;
    }

    public String getTrack() {
        return track;
    }

    public void setTrack(String track) {
        this.track = track;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public long getStateTimestamp() {
        return stateTimestamp;
    }

    public void setStateTimestamp(long stateTimestamp) {
        this.stateTimestamp = stateTimestamp;
    }

    @Override
    public String toString() {
        return "Track: playerPackageName - " + playerPackageName +
                ", track name: " + track +
                ", artist: " + artist +
                ", album: " + album +
                ", duration: " + duration;
    }

    public Track copy() {
        final Track trackCopy = new Track();

        trackCopy.playerPackageName = playerPackageName;
        trackCopy.track = track;
        trackCopy.artist = artist;
        trackCopy.album = album;
        trackCopy.duration = duration;
        trackCopy.timestamp = timestamp;
        trackCopy.state = state;

        return trackCopy;
    }

    /**
     * Checks equality only of some Track's fields
     *
     * @param track to compare
     * @return true if special fields are equal, false otherwise
     */
    public boolean specialEquals(Track track) {
        if (track == null) return false;


        if (!StringUtil.equals(getPlayerPackageName(), track.getPlayerPackageName())) {
            return false;
        }

        if (!StringUtil.equals(getTrack(), track.getTrack())) {
            return false;
        }

        if (!StringUtil.equals(getAlbum(), track.getAlbum())) {
            return false;
        }

        if (duration != track.getDuration()) {
            return false;
        }

        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Track) {
            Track track = (Track) o;

            return internalDBId == track.internalDBId
                    && StringUtil.equals(playerPackageName, track.playerPackageName)
                    && StringUtil.equals(this.track, track.track)
                    && StringUtil.equals(artist, track.artist)
                    && StringUtil.equals(album, track.album)
                    && duration == track.duration
                    && timestamp == track.timestamp
                    && state == track.state
                    && stateTimestamp == track.stateTimestamp;
        }

        return false;
    }
}
