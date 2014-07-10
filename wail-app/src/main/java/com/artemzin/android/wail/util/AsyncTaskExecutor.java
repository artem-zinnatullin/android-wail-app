package com.artemzin.android.wail.util;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Build;

/**
 * An executor for concurrently executing AsyncTask tasks
 * It takes all work for understanding device`s Android version
 * and executes your AsyncTasks tasks concurrently
 * @author Artem Zinnatullin (artem.zinnatullin@gmail.com)
 * @version 1.2
 */
public class AsyncTaskExecutor {

    private static final int CORE_POOL_SIZE;
    private static final int MAXIMUM_POOL_SIZE;
    private static final int KEEP_ALIVE;
    private static final TimeUnit TIME_UNIT;

    private static final BlockingQueue<Runnable> concurrentPoolWorkQueue;
    private static final ThreadFactory concurrentThreadFactory;
    private static final ThreadPoolExecutor concurrentExecutor;

    private AsyncTaskExecutor() {}

    static {
        CORE_POOL_SIZE    = 5;
        MAXIMUM_POOL_SIZE = 128;
        KEEP_ALIVE        = 1;
        TIME_UNIT         = TimeUnit.SECONDS;

        concurrentPoolWorkQueue = new LinkedBlockingQueue<Runnable>(10);
        concurrentThreadFactory = new AsyncTaskThreadFactory();
        concurrentExecutor      = new ThreadPoolExecutor(
                CORE_POOL_SIZE,
                MAXIMUM_POOL_SIZE,
                KEEP_ALIVE,
                TIME_UNIT,
                concurrentPoolWorkQueue,
                concurrentThreadFactory
        );
    }

    /**
     * Concurrently executes AsyncTask on any Android version
     * @param task to execute
     * @param params for task
     * @return executing AsyncTask
     */
    @SuppressLint("NewApi")
    public static <Params, Progress, Result> AsyncTask<Params, Progress, Result>
    executeConcurrently(AsyncTask<Params, Progress, Result> task, Params... params) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            task.executeOnExecutor(concurrentExecutor, params);
        } else {
            task.execute(params);
        }

        return task;
    }

    /**
     * Thread factory for AsyncTaskExecutor
     * @author Artem Zinnatullin
     */
    private static class AsyncTaskThreadFactory implements ThreadFactory {
        private final AtomicInteger count = new AtomicInteger(1);;

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "AsyncTask #" + count.getAndIncrement());
        }
    }
}