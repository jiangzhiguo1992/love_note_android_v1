package com.jiangzg.ita.base;

import android.os.Handler;
import android.os.Looper;

import com.jiangzg.base.component.activity.ActivityStack;
import com.jiangzg.base.component.application.AppBase;
import com.jiangzg.base.component.application.AppContext;
import com.jiangzg.base.component.application.AppListener;
import com.jiangzg.base.file.CleanUtils;
import com.jiangzg.ita.third.LogUtils;
import com.jiangzg.ita.view.GImageView;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import butterknife.ButterKnife;

/**
 * Created by JiangZhiGuo on 2016-12-2.
 * describe Application的基类
 */
public class MyApp extends AppBase {

    @Override
    public void onCreate() {
        super.onCreate();
        AppListener.initApp(this);
        ActivityStack.initApp();
        CleanUtils.initApp();

        ButterKnife.setDebug(true);
        LogUtils.initApp();
        GImageView.init();
        //AnalyUtils.initApp();
    }

    private Handler mainHandler; // 主线程handler
    private ExecutorService threadPool; // 缓冲线程池

    public static MyApp get() {
        return (MyApp) AppContext.get();
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

}
