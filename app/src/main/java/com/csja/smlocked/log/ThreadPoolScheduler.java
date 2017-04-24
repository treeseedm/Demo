package com.csja.smlocked.log;

import android.os.Handler;
import android.os.Looper;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * author: 后知后觉(307817387)
 * email: zhanyuanmin@meituan.com
 * time: 16/9/22 10:55
 */

public class ThreadPoolScheduler implements BaseScheduler {

    public static final int DEFAULT_SCHEDULED_COUNT = 4;

    private static ThreadPoolScheduler sInstance;
    public static ThreadPoolScheduler getInstance() {
        if (sInstance == null) {
            synchronized (ThreadPoolScheduler.class) {
                if (sInstance == null) {
                    sInstance = new ThreadPoolScheduler();
                }
            }
        }
        return sInstance;
    }

    private ExecutorService mExecutorService;
    private ScheduledExecutorService mScheduledExecutorService;
    private Handler mHandler;
    private Map<Integer, ScheduledExecutorService> mQueueThreads;
    private Map<Integer, ExecutorService> mPoolThreads;

    public ThreadPoolScheduler() {
        mExecutorService = Executors.newCachedThreadPool();
        mScheduledExecutorService = Executors.newScheduledThreadPool(DEFAULT_SCHEDULED_COUNT);
        mHandler = new Handler(Looper.getMainLooper());
        mQueueThreads = new ConcurrentHashMap<>();
        mPoolThreads = new ConcurrentHashMap<>();
    }

    @Override
    public void init() {
    }

    @Override
    public void runOnUIThread(Runnable runnable) {
        mHandler.post(runnable);
    }

    @Override
    public void runOnIOThread(Runnable runnable) {
        mExecutorService.execute(runnable);
    }

    @Override
    public void runOnIOThread(Runnable runnable, long delayInMilli) {
        mScheduledExecutorService.schedule(runnable, delayInMilli, TimeUnit.MILLISECONDS);
    }

    @Override
    public synchronized void runOnQueueThread(int queue, Runnable runnable) {
        runOnQueueThread(queue, runnable, 0);
    }

    @Override
    public synchronized void runOnQueueThread(int queue, Runnable runnable, long delayInMilli) {
        Set<Integer> keySet = mQueueThreads.keySet();
        ScheduledExecutorService executor = null;
        for (Integer key : keySet) {
            if (key.intValue() == queue) {
                executor = mQueueThreads.get(key);
                break;
            }
        }
        if (executor == null) {
            executor = Executors.newSingleThreadScheduledExecutor();
            mQueueThreads.put(queue, executor);
        }
        executor.schedule(runnable, delayInMilli, TimeUnit.MILLISECONDS);
    }

    @Override
    public synchronized void runOnPoolThread(int pool, int threads, Runnable runnable) {
        Set<Integer> keySet = mPoolThreads.keySet();
        ExecutorService executor = null;
        for (Integer key : keySet) {
            if (key.intValue() == pool) {
                executor = mPoolThreads.get(key);
                break;
            }
        }
        if (executor == null) {
            executor = Executors.newFixedThreadPool(threads);
            mPoolThreads.put(pool, executor);
        }

        executor.execute(runnable);
    }
}
