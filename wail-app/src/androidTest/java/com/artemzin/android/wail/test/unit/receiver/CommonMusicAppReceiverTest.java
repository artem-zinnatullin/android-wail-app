package com.artemzin.android.wail.test.unit.receiver;

import android.content.Intent;

import com.artemzin.android.wail.receiver.music.CommonMusicAppReceiver;
import com.artemzin.android.wail.storage.model.Track;
import com.artemzin.android.wail.test.unit.BaseAndroidTestCase;
import com.artemzin.android.wail.test.unit.factory.TestTrackFactory;

/**
 * @author Artem Zinnatullin [artem.zinnatullin@gmail.com]
 */
public class CommonMusicAppReceiverTest extends BaseAndroidTestCase {

    public void testParseFromIntentExtras() {
        Track track = TestTrackFactory.newTrackWithRandomData();

        Intent intent = new Intent();
        intent.putExtra(CommonMusicAppReceiver.EXTRA_PLAYER_PACKAGE_NAME, track.getPlayerPackageName());
        intent.putExtra(CommonMusicAppReceiver.EXTRA_TRACK, track.getTrack());
        intent.putExtra(CommonMusicAppReceiver.EXTRA_ARTIST, track.getArtist());
        intent.putExtra(CommonMusicAppReceiver.EXTRA_ALBUM, track.getAlbum());
        intent.putExtra(CommonMusicAppReceiver.EXTRA_DURATION, track.getDurationInMillis());
        intent.putExtra(CommonMusicAppReceiver.EXTRA_TIMESTAMP, track.getTimestamp());

        Track parsedTrack = CommonMusicAppReceiver.parseFromIntentExtras(intent);

        assertEquals(track.getPlayerPackageName(), parsedTrack.getPlayerPackageName());
        assertEquals(track.getTrack(), parsedTrack.getTrack());
        assertEquals(track.getArtist(), parsedTrack.getArtist());
        assertEquals(track.getAlbum(), parsedTrack.getAlbum());
        assertEquals(track.getDurationInMillis(), parsedTrack.getDurationInMillis());
        assertEquals(track.getTimestamp(), parsedTrack.getTimestamp());
    }
}
