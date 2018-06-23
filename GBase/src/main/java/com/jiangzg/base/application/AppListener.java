package com.jiangzg.base.application;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentCallbacks2;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.util.ArrayMap;

import com.jiangzg.base.common.LogUtils;
import com.jiangzg.base.common.StringUtils;

import java.util.Map;

/**
 * Created by gg on 2017/5/9.
 * Activity生命周期
 */
public class AppListener {

    private static Map<String, ActivityListener> activityListener = new ArrayMap<>();
    private static Map<String, ComponentListener> componentListener = new ArrayMap<>();

    /**
     * 添加其他监听器
     */
    public static void addComponentListener(String tag, ComponentListener listener) {
        if (StringUtils.isEmpty(tag) || listener == null) {
            LogUtils.w(AppListener.class, "addComponentListener", "tag == null || listener == null");
            return;
        }
        componentListener.put(tag, listener);
    }

    /**
     * 移除其他监听器
     */
    public static void removeComponentListener(String tag) {
        if (StringUtils.isEmpty(tag)) {
            LogUtils.w(AppListener.class, "removeComponentListener", "tag == null");
            return;
        }
        componentListener.remove(tag);
    }

    /**
     * 添加生命周期监听器
     */
    public static void addActivityListener(String tag, ActivityListener listener) {
        if (StringUtils.isEmpty(tag) || listener == null) {
            LogUtils.w(AppListener.class, "addActivityListener", "tag == null || listener == null");
            return;
        }
        activityListener.put(tag, listener);
    }

    /**
     * 移除生命周期监听器
     */
    public static void removeActivityListener(String tag) {
        if (StringUtils.isEmpty(tag)) {
            LogUtils.w(AppListener.class, "removeActivityListener", "tag == null");
            return;
        }
        activityListener.remove(tag);
    }

    public static void initApp(Application app) {
        if (app == null) {
            LogUtils.w(AppListener.class, "initApp", "app == null");
            return;
        }
        app.registerComponentCallbacks(new ComponentCallbacks2() {
            @Override
            public void onTrimMemory(int level) {
                LogUtils.d(AppListener.class, "onTrimMemory", String.valueOf(level));
                for (Map.Entry<String, ComponentListener> entry : componentListener.entrySet()) {
                    entry.getValue().onTrimMemory(level);
                }
            }

            @Override
            public void onConfigurationChanged(Configuration newConfig) {
                LogUtils.i(AppListener.class, "onConfigurationChanged", newConfig.toString());
                for (Map.Entry<String, ComponentListener> entry : componentListener.entrySet()) {
                    entry.getValue().onConfigurationChanged(newConfig);
                }
            }

            @Override
            public void onLowMemory() {
                LogUtils.w(AppListener.class, "onLowMemory", "!!!!!");
                for (Map.Entry<String, ComponentListener> entry : componentListener.entrySet()) {
                    entry.getValue().onLowMemory();
                }
            }
        });
        app.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                LogUtils.i(AppListener.class, "onActivityCreated", activity.getClass().getSimpleName());
                for (Map.Entry<String, ActivityListener> entry : activityListener.entrySet()) {
                    entry.getValue().onActivityCreated(activity, savedInstanceState);
                }
            }

            @Override
            public void onActivityStarted(Activity activity) {
                LogUtils.i(AppListener.class, "onActivityStarted",activity.getClass().getSimpleName());
                for (Map.Entry<String, ActivityListener> entry : activityListener.entrySet()) {
                    entry.getValue().onActivityStarted(activity);
                }
            }

            @Override
            public void onActivityResumed(Activity activity) {
                LogUtils.i(AppListener.class, "onActivityResumed", activity.getClass().getSimpleName());
                for (Map.Entry<String, ActivityListener> entry : activityListener.entrySet()) {
                    entry.getValue().onActivityResumed(activity);
                }
            }

            @Override
            public void onActivityPaused(Activity activity) {
                LogUtils.i(AppListener.class, "onActivityPaused", activity.getClass().getSimpleName());
                for (Map.Entry<String, ActivityListener> entry : activityListener.entrySet()) {
                    entry.getValue().onActivityPaused(activity);
                }
            }

            @Override
            public void onActivityStopped(Activity activity) {
                LogUtils.i(AppListener.class, "onActivityStopped", activity.getClass().getSimpleName());
                for (Map.Entry<String, ActivityListener> entry : activityListener.entrySet()) {
                    entry.getValue().onActivityStopped(activity);
                }
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
                LogUtils.i(AppListener.class, "onActivitySaveInstanceState", activity.getClass().getSimpleName());
                for (Map.Entry<String, ActivityListener> entry : activityListener.entrySet()) {
                    entry.getValue().onActivitySaveInstanceState(activity, outState);
                }
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                LogUtils.i(AppListener.class, "onActivityDestroyed", activity.getClass().getSimpleName());
                for (Map.Entry<String, ActivityListener> entry : activityListener.entrySet()) {
                    entry.getValue().onActivityDestroyed(activity);
                }
            }
        });
    }

    public interface ComponentListener extends ComponentCallbacks2 {
    }

    public interface ActivityListener extends Application.ActivityLifecycleCallbacks {
    }

}
