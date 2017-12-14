package com.jiangzg.base.component.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.jiangzg.base.component.application.AppListener;
import com.jiangzg.base.component.intent.IntentCons;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Created by gg on 2017/4/6.
 * 任务栈管理类
 */
public class ActivityStack {

    private static final String LOG_TAG = "ActivityStack";

    /**
     * 转变前台的activity栈(开启和改变栈都需要调用)
     * 1.FLAG_ACTIVITY_NEW_TASK 决定是否需要开启新的任务栈
     * 2.taskAffinity 决定是否可以开启新的任务栈 每个activity都声明 不写或一样则不开启新的
     * 3.launchMode 决定新的任务栈的启动模式 根activity需要
     * 4.FLAG_ACTIVITY_NO_ANIMATION rootActivity不能有切换动画
     */
    public static void changeTask(Intent intent) {
        if (intent == null) {
            Log.e(LOG_TAG, "changeTask: intent == null");
            return;
        }
        intent.addFlags(IntentCons.flag_new_task);
        intent.addFlags(IntentCons.flag_no_anim);
    }

    private static Stack<Activity> STACK; // 任务栈

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
            }

            @Override
            public void onActivityStarted(Activity activity) {
                Log.d(LOG_TAG, "Activity栈数量:" + getStack().size() + "--taskId:" + activity.getTaskId());
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
            }
        });
    }

    /**
     * 获取Activity
     */
    public List<Activity> getActivity(Class<?> cls) {
        List<Activity> activities = new ArrayList<>();
        if (cls == null) {
            Log.e(LOG_TAG, "getActivity: cls == null");
            return activities;
        }
        for (Activity activity : getStack()) {
            if (activity.getClass().equals(cls)) {
                activities.add(activity);
            }
        }
        return activities;
    }

    /**
     * 获取前台的activity
     */
    public static Activity getTop() {
        Stack<Activity> stack = getStack();
        if (stack.isEmpty()) return null;
        return stack.lastElement();
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
            Log.e(LOG_TAG, "finishOthersActivity: cls == null");
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
    public static void finishAll() {
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
            Log.e(LOG_TAG, "finishActivity: activity == null");
            return;
        }
        getStack().remove(activity);
        ActivityUtils.finish(activity);
    }

}
