package com.jiangzg.base.component.application;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.util.Log;

import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.component.activity.ActivityStack;
import com.jiangzg.base.component.intent.IntentUtils;

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
            Log.e(LOG_TAG, "isAppForeground: packageName == null");
            return false;
        }
        ActivityManager activityManager = AppContext.getActivityManager();
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
    @SuppressLint("MissingPermission")
    public static void appExit() {
        try {
            ActivityStack.finishAll();
            //ActivityManager activityManager = AppContext.getActivityManager();
            //activityManager.killBackgroundProcesses(AppContext.get().getPackageName());
            System.exit(0); // 真正退出程序
        } catch (Exception e) { // 退出JVM(java虚拟机),释放所占内存资源,非0的都为异常退出
            System.exit(-1);
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
            Log.e(LOG_TAG, "isAppInstall: packageName == null");
            return false;
        }
        return !isSpace(packageName) && IntentUtils.getApp(packageName) != null;
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
