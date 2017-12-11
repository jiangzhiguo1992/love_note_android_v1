package com.android.base.component.application;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentCallbacks2;
import android.content.res.Configuration;
import android.os.Bundle;

import com.android.base.string.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by gg on 2017/5/9.
 * Activity生命周期
 */
public class AppListener {

    private static HashMap<String, ActivityListener> activityListener = new HashMap<>();
    private static HashMap<String, ComponentListener> componentListener = new HashMap<>();

    /**
     * 添加其他监听器
     */
    public static void addComponentListener(String tag, ComponentListener listener) {
        if (StringUtils.isEmpty(tag) || listener == null) return;
        componentListener.put(tag, listener);
    }

    /**
     * 移除其他监听器
     */
    public static void removeComponentListener(String tag) {
        if (StringUtils.isEmpty(tag)) return;
        componentListener.remove(tag);
    }

    /**
     * 添加生命周期监听器
     */
    public static void addActivityListener(String tag, ActivityListener listener) {
        if (StringUtils.isEmpty(tag) || listener == null) return;
        activityListener.put(tag, listener);
    }

    /**
     * 移除生命周期监听器
     */
    public static void removeActivityListener(String tag) {
        if (StringUtils.isEmpty(tag)) return;
        activityListener.remove(tag);
    }

    public static void initApp(Application app) {
        app.registerComponentCallbacks(new ComponentCallbacks2() {
            @Override
            public void onTrimMemory(int level) {
                for (Map.Entry<String, ComponentListener> entry : componentListener.entrySet()) {
                    entry.getValue().onTrimMemory(level);
                }
            }

            @Override
            public void onConfigurationChanged(Configuration newConfig) {
                for (Map.Entry<String, ComponentListener> entry : componentListener.entrySet()) {
                    entry.getValue().onConfigurationChanged(newConfig);
                }
            }

            @Override
            public void onLowMemory() {
                for (Map.Entry<String, ComponentListener> entry : componentListener.entrySet()) {
                    entry.getValue().onLowMemory();
                }
            }
        });
        app.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                for (Map.Entry<String, ActivityListener> entry : activityListener.entrySet()) {
                    entry.getValue().onActivityCreated(activity, savedInstanceState);
                }
            }

            @Override
            public void onActivityStarted(Activity activity) {
                for (Map.Entry<String, ActivityListener> entry : activityListener.entrySet()) {
                    entry.getValue().onActivityStarted(activity);
                }
            }

            @Override
            public void onActivityResumed(Activity activity) {
                for (Map.Entry<String, ActivityListener> entry : activityListener.entrySet()) {
                    entry.getValue().onActivityResumed(activity);
                }
            }

            @Override
            public void onActivityPaused(Activity activity) {
                for (Map.Entry<String, ActivityListener> entry : activityListener.entrySet()) {
                    entry.getValue().onActivityPaused(activity);
                }
            }

            @Override
            public void onActivityStopped(Activity activity) {
                for (Map.Entry<String, ActivityListener> entry : activityListener.entrySet()) {
                    entry.getValue().onActivityStopped(activity);
                }
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
                for (Map.Entry<String, ActivityListener> entry : activityListener.entrySet()) {
                    entry.getValue().onActivitySaveInstanceState(activity, outState);
                }
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
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
