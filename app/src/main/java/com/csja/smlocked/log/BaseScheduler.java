package com.csja.smlocked.log;

/**
 * author: 后知后觉(307817387)
 * email: zhanyuanmin@meituan.com
 * time: 16/9/22 10:54
 */

public interface BaseScheduler {

    int SCHEDULER_LOG = 0;

    int SCHEDULER_TRANSFER = 1;

    int SCHEDULER_MESSAGE = 2;

    int SCHEDULER_REPORT = 3;

    int SCHEDULER_LIVE_NORMAL_ID = 4;

    int SCHEDULER_MESSAGE_DB = 5;

    void init();

    void runOnUIThread(Runnable runnable);

    void runOnIOThread(Runnable runnable);

    void runOnIOThread(Runnable runnable, long delayInMilli);

    void runOnQueueThread(int queue, Runnable runnable);

    void runOnQueueThread(int queue, Runnable runnable, long delayInMilli);

    void runOnPoolThread(int pool, int threads, Runnable runnable);
}
