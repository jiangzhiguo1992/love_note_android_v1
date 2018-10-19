package com.jiangzg.lovenote.base;

import com.jiangzg.base.application.AppBase;
import com.jiangzg.base.application.AppListener;
import com.jiangzg.base.common.LogUtils;
import com.jiangzg.base.component.ActivityStack;
import com.jiangzg.lovenote.helper.FrescoHelper;
import com.jiangzg.lovenote.helper.PushHelper;
import com.jiangzg.lovenote.helper.ResHelper;
import com.jiangzg.lovenote.helper.ThemeHelper;

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
        setTheme(ThemeHelper.getTheme());
        LogUtils.initApp(DEBUG, true);
        AppListener.initApp(this);
        ActivityStack.initApp();
        ResHelper.initApp();
        // 三方的
        ButterKnife.setDebug(DEBUG);
        FrescoHelper.init(this, DEBUG);
        PushHelper.initApp(this);
    }

    public static MyApp get() {
        return (MyApp) getInstance();
    }

}
