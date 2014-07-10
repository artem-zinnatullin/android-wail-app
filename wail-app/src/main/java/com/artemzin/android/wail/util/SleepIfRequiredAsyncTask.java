package com.artemzin.android.wail.util;

import android.os.AsyncTask;
import android.os.SystemClock;

public class SleepIfRequiredAsyncTask extends AsyncTask<Void, Void, Void> {

    private final long actionStartTime, actionMinDurationInMillis;
    private final Runnable executeInBackground, executeAfterSleep;

    private SleepIfRequiredAsyncTask(long actionStartTime, long actionMinDurationInMillis, Runnable executeInBackground, Runnable executeAfterSleep) {
        this.actionStartTime = actionStartTime;
        this.actionMinDurationInMillis = actionMinDurationInMillis;
        this.executeInBackground = executeInBackground;
        this.executeAfterSleep = executeAfterSleep;
    }

    /**
     * @param actionStartTime from SystemClock.elapsedRealTime()
     * @param actionMinDurationInMillis minimal duration of the action
     * @param executeAfterSleep runnable to execute in main ui thread after sleep
     * @return
     */
    public static SleepIfRequiredAsyncTask newInstance(long actionStartTime, long actionMinDurationInMillis, Runnable executeAfterSleep) {
        return new SleepIfRequiredAsyncTask(actionStartTime, actionMinDurationInMillis, null, executeAfterSleep);
    }

    /**
     * @param actionStartTime from SystemClock.elapsedRealTime()
     * @param actionMinDurationInMillis minimal duration of the action
     * @param executeInBackground runnable to execute in background thread
     * @param executeAfterSleep runnable to execute in main ui thread after sleep
     * @return
     */
    public static SleepIfRequiredAsyncTask newInstance(long actionStartTime, long actionMinDurationInMillis, Runnable executeInBackground, Runnable executeAfterSleep) {
        return new SleepIfRequiredAsyncTask(actionStartTime, actionMinDurationInMillis, executeInBackground, executeAfterSleep);
    }

    /**
     *
     * @param actionMinDurationInMillis minimal duration of the action
     * @param executeInBackground runnable to execute in background thread
     * @param executeAfterSleep runnable to execute in main ui thread after sleep
     * @return
     */
    public static SleepIfRequiredAsyncTask newInstance(long actionMinDurationInMillis, Runnable executeInBackground, Runnable executeAfterSleep) {
        return new SleepIfRequiredAsyncTask(SystemClock.elapsedRealtime(), actionMinDurationInMillis, executeInBackground, executeAfterSleep);
    }

    @Override
    protected Void doInBackground(Void... params) {
        if (executeInBackground != null) {
            executeInBackground.run();
        }

        ThreadUtil.sleepIfRequired(actionStartTime, actionMinDurationInMillis);

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        if (executeAfterSleep != null) {
            executeAfterSleep.run();
        }
    }
}
