package com.jiangzg.lovenote.main;

import com.jiangzg.base.application.AppBase;
import com.jiangzg.base.application.AppListener;
import com.jiangzg.base.common.LogUtils;
import com.jiangzg.base.component.ActivityStack;
import com.jiangzg.lovenote.helper.common.ResHelper;
import com.jiangzg.lovenote.helper.media.FrescoHelper;
import com.jiangzg.lovenote.helper.system.PushHelper;
import com.jiangzg.lovenote.helper.view.ThemeHelper;

import butterknife.ButterKnife;

/**
 * Created by JiangZhiGuo on 2016-12-2.
 * describe Application的基类
 */
public class MyApp extends AppBase {

    // 测试模式(上线为false)，记得修改host + version信息
    public static boolean DEBUG = true;
    // 首发结束时间
    public static final int SHOUFA_END_YEAR = 2018;
    public static final int SHOUFA_END_MONTH = 12;
    public static final int SHOUFA_END_DAY = 3;

    @Override
    public void onCreate() {
        super.onCreate();
        // 自己的
        setTheme(ThemeHelper.getTheme());
        LogUtils.initApp(DEBUG, DEBUG, true);
        AppListener.initApp(this);
        ActivityStack.initApp();
        ResHelper.initApp();
        // 三方的
        ButterKnife.setDebug(DEBUG);
        FrescoHelper.initApp(this, DEBUG);
        PushHelper.initApp(this, DEBUG);
    }

    public static MyApp get() {
        return (MyApp) getInstance();
    }

}
