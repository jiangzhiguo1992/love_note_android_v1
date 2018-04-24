package com.jiangzg.base.application;

import android.app.ActivityManager;

import com.jiangzg.base.common.LogUtils;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.component.ActivityStack;
import com.jiangzg.base.component.IntentFactory;
import com.jiangzg.base.component.ServiceUtils;

import java.util.List;

/**
 * Created by gg on 2017/5/9.
 * App工具类
 */
public class AppUtils {

    private static final String LOG_TAG = "AppUtils";

    /**
     * 判断App是否在前台运行
     *
     * @param packageName 项目包名 context.getPackageName
     */
    public static boolean isAppForeground(String packageName) {
        if (StringUtils.isEmpty(packageName)) {
            LogUtils.w(LOG_TAG, "isAppForeground: packageName == null");
            return false;
        }
        ActivityManager activityManager = AppBase.getActivityManager();
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses == null || appProcesses.size() == 0) return false;
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND)
                return appProcess.processName.equals(packageName);
        }
        return false;
    }

    /**
     * 退出应用程序
     */
    public static void appExit() {
        try {
            ActivityStack.finishAllActivity();
            ServiceUtils.stopAll();
        } catch (Exception e) {
            LogUtils.e(LOG_TAG, "appExit", e);
            System.exit(0); // 非0的都为异常退出
        }
    }

    /**
     * 判断App是否安装
     *
     * @param packageName 包名
     * @return {@code true}: 已安装<br>{@code false}: 未安装
     */
    public static boolean isAppInstall(String packageName) {
        if (StringUtils.isEmpty(packageName)) {
            LogUtils.w(LOG_TAG, "isAppInstall: packageName == null");
            return false;
        }
        return !isSpace(packageName) && IntentFactory.getApp(packageName) != null;
    }

    private static boolean isSpace(String s) {
        if (s == null) return true;
        for (int i = 0, len = s.length(); i < len; ++i) {
            if (!Character.isWhitespace(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}
