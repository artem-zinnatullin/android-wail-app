package com.artemzin.android.wail.api.lastfm.model.request;

public class LFTrackRequestModel extends LFBaseRequestModel {

    public static final int DEFAULT_DURATION = -1;

    /**
     * Artist name
     * Required
     */
    private String artist;

    /**
     * Track name
     * Required
     */
    private String track;

    /**
     * The time the track started playing (UNIX timestamp, number of seconds after 00:00:00 01.01.1970)
     * Must be in UTC time zone
     * Required
     */
    private Long timeStamp;

    /**
     * Album name
     * Optional
     */
    private String album;

    /**
     * Set to 1 if user chose this track
     * Optional
     */
    private Integer chosenByUser = 1;

    /**
     * The track number of track on the album
     * Optional
     */
    private Integer trackNumber;

    /**
     * The album artist if it differs from the track artist
     * Optional
     */
    private String albumArtist;

    /**
     * Track duration in seconds
     * Optional
     */
    private Integer duration = DEFAULT_DURATION;

    /**
     * Player package name
     */
    private String playerPackageName;

    public LFTrackRequestModel() {

    }

    public LFTrackRequestModel(com.artemzin.android.wail.storage.model.Track trackModel) {
        artist = trackModel.getArtist();
        track = trackModel.getTrack();
        timeStamp = trackModel.getTimestamp() / 1000;
        album = trackModel.getAlbum();
        duration = (int) (trackModel.getDurationInMillis() / 1000);
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getTrack() {
        return track;
    }

    public void setTrack(String track) {
        this.track = track;
    }

    public Long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public Integer getChosenByUser() {
        return chosenByUser;
    }

    public void setChosenByUser(Integer chosenByUser) {
        this.chosenByUser = chosenByUser;
    }

    public Integer getTrackNumber() {
        return trackNumber;
    }

    public void setTrackNumber(Integer trackNumber) {
        this.trackNumber = trackNumber;
    }

    public String getAlbumArtist() {
        return albumArtist;
    }

    public void setAlbumArtist(String albumArtist) {
        this.albumArtist = albumArtist;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public String getPlayerPackageName() {
        return playerPackageName;
    }

    public void setPlayerPackageName(String playerPackageName) {
        this.playerPackageName = playerPackageName;
    }
}
