package com.android.base.component.application;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentCallbacks2;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.android.base.string.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by gg on 2017/5/9.
 * Activity生命周期
 */
public class AppListener {

    private static final String LOG_TAG = "AppListener";

    private static HashMap<String, ActivityListener> activityListener = new HashMap<>();
    private static HashMap<String, ComponentListener> componentListener = new HashMap<>();

    /**
     * 添加其他监听器
     */
    public static void addComponentListener(@NonNull String tag, @NonNull ComponentListener listener) {
        componentListener.put(tag, listener);
    }

    /**
     * 移除其他监听器
     */
    public static void removeComponentListener(@NonNull String tag) {
        componentListener.remove(tag);
    }

    /**
     * 添加生命周期监听器
     */
    public static void addActivityListener(@NonNull String tag, @NonNull ActivityListener listener) {
        activityListener.put(tag, listener);
    }

    /**
     * 移除生命周期监听器
     */
    public static void removeActivityListener(@NonNull String tag) {
        activityListener.remove(tag);
    }

    public static void initApp(@NonNull Application app) {
        app.registerComponentCallbacks(new ComponentCallbacks2() {
            @Override
            public void onTrimMemory(int level) {
                for (Map.Entry<String, ComponentListener> entry : componentListener.entrySet()) {
                    String key = entry.getKey();
                    Log.d(LOG_TAG, "onTrimMemory-->" + key);
                    entry.getValue().onTrimMemory(level);
                }
            }

            @Override
            public void onConfigurationChanged(Configuration newConfig) {
                for (Map.Entry<String, ComponentListener> entry : componentListener.entrySet()) {
                    String key = entry.getKey();
                    Log.d(LOG_TAG, "onConfigurationChanged-->" + key);
                    entry.getValue().onConfigurationChanged(newConfig);
                }
            }

            @Override
            public void onLowMemory() {
                for (Map.Entry<String, ComponentListener> entry : componentListener.entrySet()) {
                    String key = entry.getKey();
                    Log.d(LOG_TAG, "onLowMemory-->" + key);
                    entry.getValue().onLowMemory();
                }
            }
        });
        app.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                for (Map.Entry<String, ActivityListener> entry : activityListener.entrySet()) {
                    String key = entry.getKey();
                    Log.d(LOG_TAG, "onActivityCreated-->" + key);
                    entry.getValue().onActivityCreated(activity, savedInstanceState);
                }
            }

            @Override
            public void onActivityStarted(Activity activity) {
                for (Map.Entry<String, ActivityListener> entry : activityListener.entrySet()) {
                    String key = entry.getKey();
                    Log.d(LOG_TAG, "onActivityStarted-->" + key);
                    entry.getValue().onActivityStarted(activity);
                }
            }

            @Override
            public void onActivityResumed(Activity activity) {
                for (Map.Entry<String, ActivityListener> entry : activityListener.entrySet()) {
                    String key = entry.getKey();
                    Log.d(LOG_TAG, "onActivityResumed-->" + key);
                    entry.getValue().onActivityResumed(activity);
                }
            }

            @Override
            public void onActivityPaused(Activity activity) {
                for (Map.Entry<String, ActivityListener> entry : activityListener.entrySet()) {
                    String key = entry.getKey();
                    Log.d(LOG_TAG, "onActivityPaused-->" + key);
                    entry.getValue().onActivityPaused(activity);
                }
            }

            @Override
            public void onActivityStopped(Activity activity) {
                for (Map.Entry<String, ActivityListener> entry : activityListener.entrySet()) {
                    String key = entry.getKey();
                    Log.d(LOG_TAG, "onActivityStopped-->" + key);
                    entry.getValue().onActivityStopped(activity);
                }
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
                for (Map.Entry<String, ActivityListener> entry : activityListener.entrySet()) {
                    String key = entry.getKey();
                    Log.d(LOG_TAG, "onActivitySaveInstanceState-->" + key);
                    entry.getValue().onActivitySaveInstanceState(activity, outState);
                }
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                for (Map.Entry<String, ActivityListener> entry : activityListener.entrySet()) {
                    String key = entry.getKey();
                    Log.d(LOG_TAG, "onActivityDestroyed-->" + key);
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
