package com.artemzin.android.wail.storage;

import android.content.Context;
import android.content.SharedPreferences;

import com.artemzin.android.bytes.common.StringUtil;
import com.artemzin.android.wail.api.lastfm.model.response.LFUserResponseModel;
import com.artemzin.android.wail.service.WAILService;

public class WAILSettings {

    private static final String APP_SETTINGS                       = "APP_SETTINGS";

    // region keys
    private static final String KEY_IS_ENABLED                     = "KEY_IS_ENABLED";
    private static final String KEY_START_ON_BOOT                  = "KEY_START_ON_BOOT";
    private static final String KEY_LASTFM_SESSION_KEY             = "KEY_LASTFM_SESSION_KEY";
    private static final String KEY_MIN_TRACK_DURATION_IN_PERCENTS = "KEY_MIN_TRACK_DURATION_IN_PERCENTS";
    private static final String KEY_MIN_TRACK_DURATION_IN_SECONDS  = "KEY_MIN_TRACK_DURATION_IN_SECONDS";
    private static final String KEY_DISABLE_SCROBBLING_OVER_MOBILE_NETWORK = "KEY_DISABLE_SCROBBLING_OVER_MOBILE_NETWORK";
    private static final String KEY_TOTAL_HANDLED_TRACKS_COUNT     = "KEY_TOTAL_HANDLED_TRACKS_COUNT";
    private static final String KEY_LASTFM_USER_NAME               = "KEY_LASTFM_USER_NAME";
    private static final String KEY_IS_FIRST_LAUNCH                = "KEY_IS_FIRST_LAUNCH";
    private static final String KEY_IS_SHOW_FEEDBACK_REQUEST       = "KEY_IS_SHOW_FEEDBACK_REQUEST";

    private static final String KEY_IS_LASTFM_NOWPLAYING_UPDATE_ENABLED = "KEY_IS_LASTFM_NOWPLAYING_UPDATE_ENABLED";
    private static final String KEY_LAST_CAPTURED_TRACK_INFO            = "KEY_LAST_CAPTURED_TRACK_INFO";
    private static final String KEY_LASTFM_USER_INFO                    = "KEY_LASTFM_USER_INFO";
    private static final String KEY_LASTFM_USER_INFO_UPDATE_TIMESTAMP   = "KEY_LASTFM_USER_INFO_UPDATE_TIMESTAMP";

    private static final String KEY_SOUND_NOTIFICATION_TRACK_MARKED_AS_SCROBBLED_ENABLED = "KEY_SOUND_NOTIFICATION_TRACK_MARKED_AS_SCROBBLED_ENABLED";
    private static final String KEY_SOUND_NOTIFICATION_TRACK_SKIPPED_ENABLED = "KEY_SOUND_NOTIFICATION_TRACK_SKIPPED_ENABLED";

    private static final String KEY_NOW_SCROBBLING_TRACK = "KEY_NOW_SCROBBLING_TRACK";

    private static final String KEY_SHOULD_SHOW_NOTIFICATION_ABOUT_MIN_TRACK_DURATION_BEHAVIOR_CHANGED = "KEY_SHOULD_SHOW_NOTIFICATION_ABOUT_MIN_TRACK_DURATION_BEHAVIOR_CHANGED";

    // endregion

    // region default values
    public static final int DEFAULT_MIN_TRACK_DURATION_IN_PERCENT = 50;
    public static final int DEFAULT_MIN_TRACK_DURATION_IN_SECONDS = 90;
    // endregion

    // region memory cached values
    private static String  lastfmSessionKey;
    private static Boolean isEnabled;
    private static Integer minTrackDurationInPercents;
    private static Integer minTrackDurationInSeconds;
    private static Boolean disableScrobblingOverMobileNetwork;
    private static Long    totalHandledTracksCount;
    private static Boolean isLastfmNowplayingUpdateEnabled;
    private static String  lastfmUserName;
    private static Boolean isShowFeedbackRequest;

    private static Boolean soundNotificationTrackScrobbledEnabled;
    private static Boolean soundNotificationTrackSkippedEnabled;
    // endregion

    private WAILSettings() {}

    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(APP_SETTINGS, Context.MODE_PRIVATE);
    }

    public static synchronized void clearAllSettings(Context context) {
        lastfmSessionKey                       = null;
        isEnabled                              = null;
        minTrackDurationInPercents             = null;
        minTrackDurationInSeconds              = null;
        totalHandledTracksCount                = null;
        isLastfmNowplayingUpdateEnabled        = null;
        lastfmUserName                         = null;
        isShowFeedbackRequest                  = null;
        isShowFeedbackRequest                  = null;
        soundNotificationTrackScrobbledEnabled = null;
        soundNotificationTrackSkippedEnabled   = null;

        getSharedPreferences(context).edit().clear().commit();
    }

    public static synchronized boolean isAuthorized(Context context) {
        return !StringUtil.isNullOrEmpty(getLastfmSessionKey(context));
    }

    public static String getLastfmApiKey() {
        return "8974fa2aeab3c058d87767a60e38cbc6";
    }

    public static String getLastfmSecret() {
        return "588695f95da39dd308e1b275a4c47ece";
    }

    public static synchronized boolean isEnabled(Context context) {
        return isEnabled != null ? isEnabled : (isEnabled = getSharedPreferences(context).getBoolean(KEY_IS_ENABLED, false));
    }

    public static synchronized void setEnabled(Context context, boolean value) {
        isEnabled = value;
        getSharedPreferences(context).edit().putBoolean(KEY_IS_ENABLED, value).commit();
    }

    public static synchronized boolean isStartOnBoot(Context context) {
        return getSharedPreferences(context).getBoolean(KEY_START_ON_BOOT, true);
    }

    public static synchronized void setStartOnBoot(Context context, boolean value) {
        getSharedPreferences(context).edit().putBoolean(KEY_START_ON_BOOT, value).commit();
    }

    public static synchronized String getLastfmSessionKey(Context context) {
        return lastfmSessionKey != null ? lastfmSessionKey
                : (lastfmSessionKey = getSharedPreferences(context).getString(KEY_LASTFM_SESSION_KEY, null));
    }

    public static synchronized void setLastfmSessionKey(Context context, String value) {
        lastfmSessionKey = value;
        getSharedPreferences(context).edit().putString(KEY_LASTFM_SESSION_KEY, value).commit();
    }

    public static synchronized int getMinTrackDurationInPercents(Context context) {
        return minTrackDurationInPercents != null ? minTrackDurationInPercents
                : (minTrackDurationInPercents = getSharedPreferences(context).getInt(KEY_MIN_TRACK_DURATION_IN_PERCENTS, DEFAULT_MIN_TRACK_DURATION_IN_PERCENT));
    }

    public static synchronized void setMinTrackDurationInPercents(Context context, int value) {
        minTrackDurationInPercents = value;
        getSharedPreferences(context).edit().putInt(KEY_MIN_TRACK_DURATION_IN_PERCENTS, value).commit();
    }

    public static synchronized int getMinTrackDurationInSeconds(Context context) {
        return minTrackDurationInSeconds != null ? minTrackDurationInSeconds
                : (minTrackDurationInSeconds = getSharedPreferences(context).getInt(KEY_MIN_TRACK_DURATION_IN_SECONDS, DEFAULT_MIN_TRACK_DURATION_IN_SECONDS));
    }

    public static synchronized void setMinTrackDurationInSeconds(Context context, int value) {
        minTrackDurationInSeconds = value;
        getSharedPreferences(context).edit().putInt(KEY_MIN_TRACK_DURATION_IN_SECONDS, value).commit();
    }

    public static synchronized long getTotalHandledTracksCount(Context context) {
        return totalHandledTracksCount != null ? totalHandledTracksCount
                : (totalHandledTracksCount = getSharedPreferences(context).getLong(KEY_TOTAL_HANDLED_TRACKS_COUNT, 0));
    }

    public static synchronized void setTotalHandledTracksCount(Context context, long value) {
        totalHandledTracksCount = value;
        getSharedPreferences(context).edit().putLong(KEY_TOTAL_HANDLED_TRACKS_COUNT, value).commit();
    }

    public static synchronized boolean isLastfmNowplayingUpdateEnabled(Context context) {
        return isLastfmNowplayingUpdateEnabled != null ? isLastfmNowplayingUpdateEnabled
                : (isLastfmNowplayingUpdateEnabled = getSharedPreferences(context).getBoolean(KEY_IS_LASTFM_NOWPLAYING_UPDATE_ENABLED, true));
    }

    public static synchronized void setLastfmNowplayingUpdateEnabled(Context context, boolean value) {
        isLastfmNowplayingUpdateEnabled = value;
        getSharedPreferences(context).edit().putBoolean(KEY_IS_LASTFM_NOWPLAYING_UPDATE_ENABLED, value).commit();
    }

    public static synchronized String getLastfmUserName(Context context) {
        final String lastfmUserNameRefCopy = lastfmUserName;

        if (!StringUtil.isNullOrEmpty(lastfmUserNameRefCopy)) {
            return lastfmUserNameRefCopy;
        }

        return lastfmUserName = getSharedPreferences(context).getString(KEY_LASTFM_USER_NAME, "");
    }

    public static synchronized void setLastfmUserName(Context context, String userName) {
        lastfmUserName = userName;
        final SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(KEY_LASTFM_USER_NAME, userName);
        editor.commit();
    }

    public static synchronized boolean isFirstLaunch(Context context) {
        return getSharedPreferences(context).getBoolean(KEY_IS_FIRST_LAUNCH, true);
    }

    public static synchronized void setIsFirstLaunch(Context context, boolean isFirstLaunch) {
        getSharedPreferences(context).edit().putBoolean(KEY_IS_FIRST_LAUNCH, isFirstLaunch).commit();
    }

    public static synchronized WAILService.LastCapturedTrackInfo getLastCapturedTrackInfo(Context context) {
        return WAILService.LastCapturedTrackInfo.fromJSON(
                getSharedPreferences(context).getString(KEY_LAST_CAPTURED_TRACK_INFO, "")
        );
    }

    public static synchronized void setLastCapturedTrackInfo(Context context, WAILService.LastCapturedTrackInfo lastCapturedTrackInfo) {
        getSharedPreferences(context).edit().putString(KEY_LAST_CAPTURED_TRACK_INFO, lastCapturedTrackInfo.toJSON()).commit();
    }

    public static synchronized LFUserResponseModel getLastfmUserInfo(Context context) {
        try {
            return LFUserResponseModel.parseFromJSON(
                    getSharedPreferences(context).getString(KEY_LASTFM_USER_INFO, "")
            );
        } catch (Exception e) {
            return null;
        }
    }

    public static synchronized void setLastfmUserInfo(Context context, String json) {
        getSharedPreferences(context).edit()
                .putString(KEY_LASTFM_USER_INFO, json)
                .commit();
    }

    public static synchronized long getLastfmUserInfoUpdateTimestamp(Context context) {
        return getSharedPreferences(context).getLong(KEY_LASTFM_USER_INFO_UPDATE_TIMESTAMP, -1);
    }

    public static synchronized void setLastfmUserInfoUpdateTimestamp(Context context, long timestamp) {
        getSharedPreferences(context).edit().putLong(KEY_LASTFM_USER_INFO_UPDATE_TIMESTAMP, timestamp).commit();
    }

    public static synchronized boolean isSoundNotificationTrackMarkedAsScrobbledEnabled(Context context) {
        return soundNotificationTrackScrobbledEnabled != null ?
                soundNotificationTrackScrobbledEnabled :
                (soundNotificationTrackScrobbledEnabled = getSharedPreferences(context).getBoolean(KEY_SOUND_NOTIFICATION_TRACK_MARKED_AS_SCROBBLED_ENABLED, false));
    }

    public static synchronized void setSoundNotificationTrackMarkedAsScrobbledEnabled(Context context, boolean value) {
        soundNotificationTrackScrobbledEnabled = value;
        getSharedPreferences(context).edit().putBoolean(KEY_SOUND_NOTIFICATION_TRACK_MARKED_AS_SCROBBLED_ENABLED, value).commit();
    }

    public static synchronized boolean isSoundNotificationTrackSkippedEnabled(Context context) {
        return soundNotificationTrackSkippedEnabled != null ?
                soundNotificationTrackSkippedEnabled :
                (soundNotificationTrackSkippedEnabled = getSharedPreferences(context).getBoolean(KEY_SOUND_NOTIFICATION_TRACK_SKIPPED_ENABLED, false));
    }

    public static synchronized void setSoundNotificationTrackSkippedEnabled(Context context, boolean value) {
        soundNotificationTrackSkippedEnabled = value;
        getSharedPreferences(context).edit().putBoolean(KEY_SOUND_NOTIFICATION_TRACK_SKIPPED_ENABLED, value).commit();
    }

    public static synchronized boolean isShowFeedbackRequest(Context context) {
        return isShowFeedbackRequest != null ?
                isShowFeedbackRequest :
                (isShowFeedbackRequest = getSharedPreferences(context).getBoolean(KEY_IS_SHOW_FEEDBACK_REQUEST, true));
    }

    public static synchronized void setShowFeedbackRequest(Context context, boolean value) {
        isShowFeedbackRequest = value;
        getSharedPreferences(context).edit().putBoolean(KEY_IS_SHOW_FEEDBACK_REQUEST, value).commit();
    }

    public static synchronized String getNowScrobblingTrack(Context context) {
        return getSharedPreferences(context).getString(KEY_NOW_SCROBBLING_TRACK, null);
    }

    public static synchronized void setNowScrobblingTrack(Context context, String value) {
        getSharedPreferences(context).edit().putString(KEY_NOW_SCROBBLING_TRACK, value).commit();
    }

    public static boolean isDisableScrobblingOverMobileNetwork(Context context) {
        return disableScrobblingOverMobileNetwork != null
                ? disableScrobblingOverMobileNetwork
                : getSharedPreferences(context).getBoolean(KEY_DISABLE_SCROBBLING_OVER_MOBILE_NETWORK, false);
    }

    public static void setDisableScrobblingOverMobileNetwork(Context context, boolean value) {
        disableScrobblingOverMobileNetwork = value;
        getSharedPreferences(context).edit().putBoolean(KEY_DISABLE_SCROBBLING_OVER_MOBILE_NETWORK, value).commit();
    }
}
