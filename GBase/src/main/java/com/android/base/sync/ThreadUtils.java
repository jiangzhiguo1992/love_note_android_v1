package com.android.base.sync;

import android.os.HandlerThread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Created by gg on 2017/5/8.
 * todo 线程管理类
 */
public class ThreadUtils {

    private static final String LOG_TAG = "ThreadUtils";

    private void test() {
        ExecutorService executorService = Executors.newCachedThreadPool();
        ExecutorService executorService1 = Executors.newFixedThreadPool(1);
        ExecutorService executorService2 = Executors.newSingleThreadExecutor();


//        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(,,,,);

        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
        ScheduledExecutorService scheduledExecutorService1 = Executors.newSingleThreadScheduledExecutor();

//        HandlerThread
    }


}
