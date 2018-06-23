package com.jiangzg.base.component;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.util.ArrayMap;

import com.jiangzg.base.application.AppBase;
import com.jiangzg.base.application.AppListener;
import com.jiangzg.base.common.LogUtils;
import com.jiangzg.base.common.StringUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * Created by gg on 2017/4/6.
 * 任务栈管理类
 */
public class ActivityStack {

    private static Stack<Activity> STACK; // 任务栈(不会内存泄露)

    public static Stack<Activity> getStack() {
        if (STACK == null) {
            STACK = new Stack<>();
        }
        return STACK;
    }

    /* 管理Activity栈 */
    public static void initApp() {
        AppListener.addActivityListener("ActivityStack", new AppListener.ActivityListener() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                getStack().add(activity);
                LogUtils.i(ActivityStack.class, "onActivityCreated", "Activity栈数量: " + getStack().size() + " -- Activity栈标识: " + activity.getTaskId());
            }

            @Override
            public void onActivityStarted(Activity activity) {
            }

            @Override
            public void onActivityResumed(Activity activity) {

            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                getStack().remove(activity);
                LogUtils.i(ActivityStack.class, "onActivityDestroyed", "Activity栈数量: " + getStack().size() + " -- Activity栈标识:" + activity.getTaskId());
            }
        });
    }

    /**
     * 获取Activity
     */
    public List<Activity> findInStack(Class<?> cls) {
        if (cls == null) {
            LogUtils.w(ActivityStack.class, "findInStack", "cls == null");
            return new ArrayList<>();
        }
        List<Activity> activities = new ArrayList<>();
        for (Activity activity : getStack()) {
            if (activity.getClass().equals(cls)) {
                activities.add(activity);
            }
        }
        return activities;
    }

    /**
     * 判断栈中是否存在Activity
     */
    public static boolean findInSystem(String packageName, String className) {
        if (StringUtils.isEmpty(packageName) || StringUtils.isEmpty(className)) {
            LogUtils.w(ActivityStack.class, "findInSystem", "packageName == null || className == null");
            return false;
        }
        PackageManager packageManager = AppBase.getInstance().getPackageManager();
        Intent intent = new Intent();
        intent.setClassName(packageName, className);
        ResolveInfo resolveInfo = packageManager.resolveActivity(intent, 0);
        ComponentName componentName = intent.resolveActivity(packageManager);
        int size = packageManager.queryIntentActivities(intent, 0).size();
        return !(resolveInfo == null || componentName == null || size == 0);
    }

    /**
     * 获取前台的activity
     */
    @SuppressWarnings("unchecked")
    @SuppressLint("PrivateApi")
    public static Activity getTop() {
        Stack<Activity> stack = getStack();
        if (stack.isEmpty()) {
            try {
                Class activityThreadClass = Class.forName("android.app.ActivityThread");
                Object activityThread = activityThreadClass.getMethod("currentActivityThread").invoke(null);
                Field activitiesField = activityThreadClass.getDeclaredField("mActivities");
                activitiesField.setAccessible(true);
                Map activities = (ArrayMap) activitiesField.get(activityThread);
                for (Object activityRecord : activities.values()) {
                    Class activityRecordClass = activityRecord.getClass();
                    Field pausedField = activityRecordClass.getDeclaredField("paused");
                    pausedField.setAccessible(true);
                    if (!pausedField.getBoolean(activityRecord)) {
                        Field activityField = activityRecordClass.getDeclaredField("activity");
                        activityField.setAccessible(true);
                        return (Activity) activityField.get(activityRecord);
                    }
                }
            } catch (Exception e) {
                LogUtils.e(ActivityStack.class, "getTop", e);
            }
            return null;
        } else {
            return stack.lastElement();
        }
    }

    /**
     * 获取底部的activity
     */
    public static Activity getBottom() {
        Stack<Activity> stack = getStack();
        if (stack.isEmpty()) return null;
        return stack.firstElement();
    }

    /**
     * 结束指定的Activity(重载)
     */
    public void finishActivity(Class<?> cls) {
        if (cls == null) {
            LogUtils.w(ActivityStack.class, "finishActivity", "cls == null");
            return;
        }
        for (Activity activity : getStack()) {
            if (activity.getClass().equals(cls)) {
                finishActivity(activity);
            }
        }
    }

    /**
     * 关闭除了指定activity以外的全部activity
     */
    public void finishOthersActivity(Class<?> cls) {
        if (cls == null) {
            LogUtils.w(ActivityStack.class, "finishOthersActivity", "cls == null");
            return;
        }
        for (Activity activity : getStack()) {
            if (!(activity.getClass().equals(cls))) {
                finishActivity(activity);
            }
        }
    }

    /**
     * 关闭task底部activity
     */
    public static void finishTask(int taskId) {
        for (Activity activity : getStack()) {
            if (taskId == activity.getTaskId()) {
                finishActivity(activity);
            }
        }
    }

    /**
     * 关闭所有activity
     */
    public static void finishAllActivity() {
        for (Activity activity : getStack()) {
            finishActivity(activity);
        }
        getStack().clear();
    }

    /**
     * 关闭activity
     */
    private static void finishActivity(Activity activity) {
        if (activity == null) {
            LogUtils.w(ActivityStack.class, "finishActivity", "activity == null");
            return;
        }
        getStack().remove(activity);
        activity.finish();
    }

}
