package com.artemzin.android.wail.test.unit.storage.db;

import com.artemzin.android.wail.storage.db.TracksDBHelper;
import com.artemzin.android.wail.storage.model.Track;
import com.artemzin.android.wail.test.unit.BaseAndroidTestCase;
import com.artemzin.android.wail.test.unit.factory.TestTrackFactory;

/**
 * @author Artem Zinnatullin [artem.zinnatullin@gmail.com]
 */
public class TracksDBHelperTest extends BaseAndroidTestCase {

    public void testGetInstanceIsSingleton() {
        assertSame(TracksDBHelper.getInstance(getContext()), TracksDBHelper.getInstance(getContext()));
    }

    public void testAdd() {
        Track track = TestTrackFactory.newTrackWithRandomData();
        assertTrue(TracksDBHelper.getInstance(getContext()).add(track) != -1);
        assertTrue(track.specialEquals(TracksDBHelper.getInstance(getContext()).getLastAddedTrack()));
    }

    public void testUpdate() {
        Track track = TestTrackFactory.newTrackWithRandomData();

        final long trackDBId = TracksDBHelper.getInstance(getContext()).add(track);
        assertTrue(trackDBId != -1);

        track.setInternalDBId(trackDBId);
        track.setDuration(getRandom().nextLong());
        track.setArtist(track.getArtist() + getRandom().nextFloat());
        track.setAlbum(track.getAlbum() + getRandom().nextDouble());
        track.setPlayerPackageName(track.getPlayerPackageName() + getRandom().nextGaussian());

        assertTrue(TracksDBHelper.getInstance(getContext()).update(track));
        assertTrue(track.specialEquals(TracksDBHelper.getInstance(getContext()).getLastAddedTrack()));
    }

    public void testDelete() {
        Track track = TestTrackFactory.newTrackWithRandomData();

        final long trackDBId = TracksDBHelper.getInstance(getContext()).add(track);
        assertTrue(trackDBId != -1);
        track.setInternalDBId(trackDBId);

        assertTrue(track.specialEquals(TracksDBHelper.getInstance(getContext()).getLastAddedTrack()));
        assertEquals(1, TracksDBHelper.getInstance(getContext()).delete(track));
        assertNull(TracksDBHelper.getInstance(getContext()).getLastAddedTrack());
    }

    public void testUpdateAll() {
        // TODO implement
    }

    public void testDeleteAll() {
        assertEquals(0, TracksDBHelper.getInstance(getContext()).getAllDesc().getCount());

        final int count = getRandom().nextInt(1000) + 200;

        for (int i = 0; i < count; i++) {
            TracksDBHelper.getInstance(getContext()).add(TestTrackFactory.newTrackWithRandomData());
        }

        assertEquals(count, TracksDBHelper.getInstance(getContext()).getAllDesc().getCount());
        assertEquals(count, TracksDBHelper.getInstance(getContext()).deleteAll());
        assertEquals(0, TracksDBHelper.getInstance(getContext()).getAllDesc().getCount());
    }
}
