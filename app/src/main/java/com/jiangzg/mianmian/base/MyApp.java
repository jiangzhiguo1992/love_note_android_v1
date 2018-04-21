package com.jiangzg.mianmian.base;

import android.os.Handler;
import android.os.Looper;

import com.jiangzg.base.application.AppBase;
import com.jiangzg.base.application.AppListener;
import com.jiangzg.base.common.LogUtils;
import com.jiangzg.base.component.ActivityStack;
import com.jiangzg.mianmian.helper.CleanHelper;
import com.jiangzg.mianmian.view.GImageView;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import butterknife.ButterKnife;

/**
 * Created by JiangZhiGuo on 2016-12-2.
 * describe Application的基类
 */
public class MyApp extends AppBase {

    public static boolean DEBUG = true; // 测试模式(上线为false)

    private Handler mainHandler; // 主线程handler
    private ExecutorService threadPool; // 缓冲线程池
    //private ScheduledExecutorService schedule; // 定时线程池

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.initApp(DEBUG);
        AppListener.initApp(this);
        ActivityStack.initApp();
        CleanHelper.initApp();

        ButterKnife.setDebug(DEBUG);
        GImageView.init(this, DEBUG);
        //AnalyUtils.initApp();
    }

    public static MyApp get() {
        return (MyApp) getInstance();
    }

    public Handler getHandler() {
        if (null == mainHandler) {
            mainHandler = new Handler(Looper.getMainLooper());
        }
        return mainHandler;
    }

    public ExecutorService getThread() {
        if (null == threadPool) {
            threadPool = Executors.newCachedThreadPool();
        }
        return threadPool;
    }

    //public ScheduledExecutorService getSchedule() {
    //    if (null == schedule) {
    //        schedule = Executors.newScheduledThreadPool(1);
    //    }
    //    return schedule;
    //}

}
