package com.android.depend.utils;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import com.android.base.component.application.AppContext;
import com.android.base.component.application.AppListener;
import com.umeng.analytics.MobclickAgent;

/**
 * Created by gg on 2017/2/27.
 * 友盟统计(是友盟的单独的服务)
 */
public class AnalyUtils {

    /* 收集奔溃日志 */
    public static void initApp() {
        MobclickAgent.setScenarioType(AppContext.get(), MobclickAgent.EScenarioType.E_UM_NORMAL);
        MobclickAgent.setCatchUncaughtExceptions(true);
        AppListener.addActivityListener("AnalyUtils", new AppListener.ActivityListener() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

            }

            @Override
            public void onActivityStarted(Activity activity) {

            }

            @Override
            public void onActivityResumed(Activity activity) {
                AnalyUtils.analysisOnResume(activity);
            }

            @Override
            public void onActivityPaused(Activity activity) {
                AnalyUtils.analysisOnPause(activity);
            }

            @Override
            public void onActivityStopped(Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }
        });
    }

    /* 数据统计(崩溃日志) 在activity中的OnResume中调用 */
    private static void analysisOnResume(Context context) {
        MobclickAgent.onResume(context);
    }

    /* 数据统计(崩溃日志) 在activity中的OnPause中调用 */
    private static void analysisOnPause(Context context) {
        MobclickAgent.onPause(context);
    }

    /* 应该有账号之类的统计 */
}
