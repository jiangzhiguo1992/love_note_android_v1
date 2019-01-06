package com.jiangzg.lovenote.main;

import com.jiangzg.base.application.AppBase;
import com.jiangzg.base.application.AppListener;
import com.jiangzg.base.application.AppUtils;
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

    @Override
    public void onCreate() {
        super.onCreate();
        boolean debug = AppUtils.getAppMetaDataBool(this, "is_debug");
        // 自己的
        setTheme(ThemeHelper.getTheme());
        LogUtils.initApp(debug, debug, true);
        AppListener.initApp(this);
        ActivityStack.initApp();
        ResHelper.initApp();
        // 三方的
        ButterKnife.setDebug(debug);
        FrescoHelper.initApp(this, debug);
        PushHelper.initApp(this, debug);
    }

    public static MyApp get() {
        return (MyApp) getInstance();
    }

}
