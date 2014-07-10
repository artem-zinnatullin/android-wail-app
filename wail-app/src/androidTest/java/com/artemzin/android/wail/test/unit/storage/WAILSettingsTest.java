package com.artemzin.android.wail.test.unit.storage;

import com.artemzin.android.wail.api.lastfm.model.response.LFUserResponseModel;
import com.artemzin.android.wail.service.WAILService;
import com.artemzin.android.wail.storage.WAILSettings;
import com.artemzin.android.wail.storage.model.Track;
import com.artemzin.android.wail.test.unit.BaseAndroidTestCase;
import com.artemzin.android.wail.test.unit.factory.TestTrackFactory;

/**
 * @author Artem Zinnatullin [artem.zinnatullin@gmail.com]
 */
public class WAILSettingsTest extends BaseAndroidTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        clearAllWAILData(getContext());
    }

    public void testIsAuthorizedDefault() {
        assertFalse(WAILSettings.isAuthorized(getContext()));
    }

    public void testIsAuthorizedWithSessionKey() {
        WAILSettings.setLastfmSessionKey(getContext(), "asasdff");
        assertTrue(WAILSettings.isAuthorized(getContext()));
    }

    public void testGetLastfmApiKeyNotNull() {
        assertNotNull(WAILSettings.getLastfmApiKey());
    }

    public void testGetLastfmSecret() {
        assertNotNull(WAILSettings.getLastfmSecret());
    }

    public void testIsEnabledDefault() {
        assertFalse(WAILSettings.isEnabled(getContext()));
    }

    public void testSetAndGetIsEnabled() {
        assertFalse(WAILSettings.isEnabled(getContext()));
        WAILSettings.setEnabled(getContext(), true);
        assertTrue(WAILSettings.isEnabled(getContext()));
    }

    public void testIsStartOnBootDefault() {
        assertTrue(WAILSettings.isStartOnBoot(getContext()));
    }

    public void testSetAndGetIsStartOnBoot() {
        assertTrue(WAILSettings.isStartOnBoot(getContext()));
        WAILSettings.setStartOnBoot(getContext(), false);
        assertFalse(WAILSettings.isStartOnBoot(getContext()));
    }

    public void testGetLastfmSessionKeyDefault() {
        assertNull(WAILSettings.getLastfmSessionKey(getContext()));
    }

    public void testSetAndGetLastfmSessionKey() {
        assertNull(WAILSettings.getLastfmSessionKey(getContext()));
        String value = "fakjasfolasddaf";
        WAILSettings.setLastfmSessionKey(getContext(), value);
        assertEquals(value, WAILSettings.getLastfmSessionKey(getContext()));
    }

    public void testGetMinTrackDurationInPercentsDefault() {
        assertEquals(WAILSettings.DEFAULT_MIN_TRACK_DURATION_IN_PERCENT, WAILSettings.getMinTrackDurationInPercents(getContext()));
    }

    public void testSetAndGetMinTrackDurationInPercents() {
        final int value = 21;
        assertTrue(value != WAILSettings.getMinTrackDurationInPercents(getContext()));
        WAILSettings.setMinTrackDurationInPercents(getContext(), value);
        assertEquals(value, WAILSettings.getMinTrackDurationInPercents(getContext()));
    }

    public void testGetMinTrackDurationInSecondsDefault() {
        assertEquals(WAILSettings.DEFAULT_MIN_TRACK_DURATION_IN_SECONDS, WAILSettings.getMinTrackDurationInSeconds(getContext()));
    }

    public void testSetAndGetMinTrackDurationInSeconds() {
        final int value = 124512;
        assertTrue(value != WAILSettings.getMinTrackDurationInSeconds(getContext()));
        WAILSettings.setMinTrackDurationInSeconds(getContext(), value);
        assertEquals(value, WAILSettings.getMinTrackDurationInSeconds(getContext()));
    }

    public void testGetTotalHandledTracksCountDefault() {
        assertEquals(0, WAILSettings.getTotalHandledTracksCount(getContext()));
    }

    public void testSetAndGetTotalHandledTracksCount() {
        final long value = 215;
        assertTrue(value != WAILSettings.getTotalHandledTracksCount(getContext()));
        WAILSettings.setTotalHandledTracksCount(getContext(), value);
        assertEquals(value, WAILSettings.getTotalHandledTracksCount(getContext()));
    }

    public void testIsLastfmNowplayingUpdateEnabledDefault() {
        assertTrue(WAILSettings.isLastfmNowplayingUpdateEnabled(getContext()));
    }

    public void testSetAndGetIsLastfmNowplayingUpdateEnabled() {
        final boolean value = false;
        assertTrue(value != WAILSettings.isLastfmNowplayingUpdateEnabled(getContext()));
        WAILSettings.setLastfmNowplayingUpdateEnabled(getContext(), value);
        assertEquals(value, WAILSettings.isLastfmNowplayingUpdateEnabled(getContext()));
    }

    public void testGetLastfmUserNameDefault() {
        assertEquals("", WAILSettings.getLastfmUserName(getContext()));
    }

    public void testSetAndGetLastfmUserName() {
        final String value = "adkjhafklio3";
        assertFalse(value.equals(WAILSettings.getLastfmUserName(getContext())));
        WAILSettings.setLastfmUserName(getContext(), value);
        assertEquals(value, WAILSettings.getLastfmUserName(getContext()));
    }

    public void testIsFirstLaunchDefault() {
        assertTrue(WAILSettings.isFirstLaunch(getContext()));
    }

    public void testSetAndGetIsFirstLaunch() {
        assertTrue(WAILSettings.isFirstLaunch(getContext()));
        WAILSettings.setIsFirstLaunch(getContext(), false);
        assertFalse(WAILSettings.isFirstLaunch(getContext()));
    }

    public void testGetLastCapturedTrackInfoDefault() {
        assertNull(WAILSettings.getLastCapturedTrackInfo(getContext()));
    }

    public void testSetAndGetLastCapturedTrackInfo() {
        assertNull(WAILSettings.getLastCapturedTrackInfo(getContext()));
        Track track = TestTrackFactory.newTrackWithRandomData();

        WAILService.LastCapturedTrackInfo trackInfo = new WAILService.LastCapturedTrackInfo(track, true);

        WAILSettings.setLastCapturedTrackInfo(getContext(), trackInfo);
        WAILService.LastCapturedTrackInfo actualTrackInfo = WAILSettings.getLastCapturedTrackInfo(getContext());

        assertNotNull(actualTrackInfo);
        assertTrue(actualTrackInfo.isPlaying());
        assertEquals(track, actualTrackInfo.getTrack());
    }

    public void testGetLastfmUserInfoDefault() {
        assertNull(WAILSettings.getLastfmUserInfo(getContext()));
    }

    public void testSetAndGetLastfmUserInfo() throws Exception {
        final String userInfoJSON = "\n" +
                "{\"user\":{\"name\":\"RJ\",\"realname\":\"Richard Jones \",\"image\":[{\"#text\":\"http:\\/\\/userserve-ak.last.fm\\/serve\\/34\\/84504153.jpg\",\"size\":\"small\"},{\"#text\":\"http:\\/\\/userserve-ak.last.fm\\/serve\\/64\\/84504153.jpg\",\"size\":\"medium\"},{\"#text\":\"http:\\/\\/userserve-ak.last.fm\\/serve\\/126\\/84504153.jpg\",\"size\":\"large\"},{\"#text\":\"http:\\/\\/userserve-ak.last.fm\\/serve\\/252\\/84504153.jpg\",\"size\":\"extralarge\"}],\"url\":\"http:\\/\\/www.last.fm\\/user\\/RJ\",\"id\":\"1000002\",\"country\":\"UK\",\"age\":\"31\",\"gender\":\"m\",\"subscriber\":\"1\",\"playcount\":\"89564\",\"playlists\":\"4\",\"bootstrap\":\"0\",\"registered\":{\"#text\":\"2002-11-20 11:50\",\"unixtime\":\"1037793040\"},\"type\":\"alumni\"}}";

        assertNull(WAILSettings.getLastfmUserInfo(getContext()));
        WAILSettings.setLastfmUserInfo(getContext(), userInfoJSON);
        assertEquals(LFUserResponseModel.parseFromJSON(userInfoJSON), WAILSettings.getLastfmUserInfo(getContext()));
    }

    public void testGetLastfmUserInfoUpdateTimestampDefault() {
        assertEquals(-1, WAILSettings.getLastfmUserInfoUpdateTimestamp(getContext()));
    }

    public void testSetAndGetLastfmUserInfoUpdateTimestamp() {
        final long timestamp = System.currentTimeMillis();
        WAILSettings.setLastfmUserInfoUpdateTimestamp(getContext(), timestamp);
        assertEquals(timestamp, WAILSettings.getLastfmUserInfoUpdateTimestamp(getContext()));
    }

    public void testIsSoundNotificationTrackMarkedAsScrobbledEnabledDefault() {
        assertFalse(WAILSettings.isSoundNotificationTrackMarkedAsScrobbledEnabled(getContext()));
    }

    public void testSetAndGetIsSoundNotificationTrackMarkedAsScrobbledEnabled() {
        WAILSettings.setSoundNotificationTrackMarkedAsScrobbledEnabled(getContext(), true);
        assertTrue(WAILSettings.isSoundNotificationTrackMarkedAsScrobbledEnabled(getContext()));

        WAILSettings.setSoundNotificationTrackMarkedAsScrobbledEnabled(getContext(), false);
        assertFalse(WAILSettings.isSoundNotificationTrackMarkedAsScrobbledEnabled(getContext()));
    }

    public void testIsSoundNotificationTrackSkippedEnabledDefault() {
        assertFalse(WAILSettings.isSoundNotificationTrackSkippedEnabled(getContext()));
    }

    public void testSetAndGetIsSoundNotificationTrackSkippedEnabled() {
        WAILSettings.setSoundNotificationTrackSkippedEnabled(getContext(), true);
        assertTrue(WAILSettings.isSoundNotificationTrackSkippedEnabled(getContext()));

        WAILSettings.setSoundNotificationTrackSkippedEnabled(getContext(), false);
        assertFalse(WAILSettings.isSoundNotificationTrackSkippedEnabled(getContext()));
    }

    public void testIsShowFeedbackRequestDefault() {
        assertTrue(WAILSettings.isShowFeedbackRequest(getContext()));
    }

    public void testSetAndGetIsShowFeedbackRequest() {
        WAILSettings.setShowFeedbackRequest(getContext(), false);
        assertFalse(WAILSettings.isShowFeedbackRequest(getContext()));

        WAILSettings.setShowFeedbackRequest(getContext(), true);
        assertTrue(WAILSettings.isShowFeedbackRequest(getContext()));
    }
}
