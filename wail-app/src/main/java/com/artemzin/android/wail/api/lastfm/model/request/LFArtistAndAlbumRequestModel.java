package com.artemzin.android.wail.api.lastfm.model.request;

public class LFArtistAndAlbumRequestModel extends LFBaseRequestModel {

    private String artist;
    private String album;

    public LFArtistAndAlbumRequestModel(String artist, String album) {
        this.artist = artist;
        this.album  = album;
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
}
