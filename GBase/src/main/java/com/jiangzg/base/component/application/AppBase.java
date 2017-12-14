package com.jiangzg.base.component.application;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.util.Log;

/**
 * Created by JiangZhiGuo on 2016-12-2.
 * describe Application的基类
 */
public class AppBase extends MultiDexApplication {

    private static final String LOG_TAG = "AppBase";

    private static AppBase instance;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this); // 大项目需要分包
    }

    @Override
    public void onCreate() {
        instance = this;
        super.onCreate();
        Log.d(LOG_TAG, "onCreate");
    }

    public static AppBase get() {
        return instance;
    }
}
