package com.artemzin.android.wail.test.unit.factory;

import com.artemzin.android.wail.storage.model.Track;

import java.util.Random;

/**
 * @author Artem Zinnatullin [artem.zinnatullin@gmail.com]
 */
public class TestTrackFactory {

    private static final Random random = new Random(System.currentTimeMillis());

    public static Track newTrackWithRandomData() {
        Track track = new Track();

        track.setTrack("Song name " + random.nextInt(1000));
        track.setArtist("Artist " + random.nextInt(1005));
        track.setAlbum("Album " + random.nextInt(100000));
        track.setDuration(random.nextInt(102455));
        track.setTimestamp(System.currentTimeMillis());
        track.setState(random.nextInt(4));
        track.setStateTimestamp(System.currentTimeMillis());
        track.setPlayerPackageName("playerPackageName" + random.nextInt(1251561));

        return track;
    }
}
