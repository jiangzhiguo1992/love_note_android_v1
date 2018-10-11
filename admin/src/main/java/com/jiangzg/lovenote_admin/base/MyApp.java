package com.jiangzg.lovenote_admin.base;

import com.jiangzg.base.application.AppBase;
import com.jiangzg.base.application.AppListener;
import com.jiangzg.base.common.LogUtils;
import com.jiangzg.base.component.ActivityStack;
import com.jiangzg.lovenote_admin.helper.FrescoHelper;

import butterknife.ButterKnife;

/**
 * Created by JiangZhiGuo on 2016-12-2.
 * describe Application的基类
 */
public class MyApp extends AppBase {

    public static boolean DEBUG = true; // 测试模式(上线为false)

    @Override
    public void onCreate() {
        super.onCreate();
        // 自己的
        LogUtils.initApp(DEBUG, false);
        AppListener.initApp(this);
        ActivityStack.initApp();
        // 三方的
        ButterKnife.setDebug(DEBUG);
        FrescoHelper.init(this, DEBUG);
        //AnalyUtils.initApp();
    }

    public static MyApp get() {
        return (MyApp) getInstance();
    }

}
