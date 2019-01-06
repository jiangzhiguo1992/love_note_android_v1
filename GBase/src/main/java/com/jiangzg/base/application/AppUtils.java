package com.jiangzg.base.application;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.os.Bundle;

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

    /**
     * 获取manifest中的application下的MetaData
     */
    public static Bundle getAppMetaData(Context context) {
        if (context == null) {
            LogUtils.w(AppUtils.class, "getAppMetaData", "context == null");
            return null;
        }
        PackageManager packageManager = context.getPackageManager();
        String packageName = context.getPackageName();
        try {
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA);
            return applicationInfo.metaData;
        } catch (PackageManager.NameNotFoundException e) {
            LogUtils.e(AppUtils.class, "getAppMetaData", e);
        }
        return null;
    }

    public static String getAppMetaDataString(Context context, String name) {
        Bundle metaData = getAppMetaData(context);
        if (metaData == null) return "";
        return metaData.getString(name, "");
    }

    public static int getAppMetaDataInt(Context context, String name) {
        Bundle metaData = getAppMetaData(context);
        if (metaData == null) return 0;
        return metaData.getInt(name, 0);
    }

    public static long getAppMetaDataLong(Context context, String name) {
        Bundle metaData = getAppMetaData(context);
        if (metaData == null) return 0;
        return metaData.getLong(name, 0);
    }

    public static boolean getAppMetaDataBool(Context context, String name) {
        Bundle metaData = getAppMetaData(context);
        if (metaData == null) return false;
        return metaData.getBoolean(name, false);
    }

    /**
     * 获取manifest中的activity下的MetaData
     */
    public static Bundle getAppActivityMetaData(Context context, ComponentName name) {
        if (context == null || name == null) {
            LogUtils.w(AppUtils.class, "getAppMetaData", "context == null || name == null");
            return null;
        }
        PackageManager packageManager = context.getPackageManager();
        try {
            ActivityInfo activityInfo = packageManager.getActivityInfo(name, PackageManager.GET_META_DATA);
            return activityInfo.metaData;
        } catch (PackageManager.NameNotFoundException e) {
            LogUtils.e(AppUtils.class, "getAppActivityMetaData", e);
        }
        return null;
    }

    /**
     * 获取manifest中的provider里的节点
     */
    public static ProviderInfo[] getAppProviderInfoList(Context context) {
        if (context == null) {
            LogUtils.w(AppUtils.class, "getAppProviderInfo", "context == null");
            return new ProviderInfo[]{};
        }
        PackageManager packageManager = context.getPackageManager();
        String packageName = context.getPackageName();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_PROVIDERS);
            return packageInfo.providers;
        } catch (PackageManager.NameNotFoundException e) {
            LogUtils.e(AppUtils.class, "getAppProviderInfo", e);
        }
        return new ProviderInfo[]{};
    }

    public static ProviderInfo getAppProviderInfo(Context context, String name) {
        ProviderInfo[] providerInfoList = getAppProviderInfoList(context);
        if (providerInfoList == null || providerInfoList.length <= 0) return null;
        for (ProviderInfo info : providerInfoList) {
            if (info == null) continue;
            if (!info.name.trim().equals(name.trim())) continue;
            return info;
        }
        return null;
    }

    /**
     * 判断App是否在前台运行
     *
     * @param packageName 项目包名 context.getPackageName
     */
    public static boolean isAppForeground(String packageName) {
        if (StringUtils.isEmpty(packageName)) {
            LogUtils.w(AppUtils.class, "isAppForeground", "packageName == null");
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
            LogUtils.e(AppUtils.class, "appExit", e);
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
            LogUtils.w(AppUtils.class, "isAppInstall", "packageName == null");
            return false;
        }
        boolean isSpace = true;
        for (int i = 0, len = packageName.length(); i < len; ++i) {
            if (!Character.isWhitespace(packageName.charAt(i))) {
                isSpace = false;
            }
        }
        return isSpace && IntentFactory.getApp(packageName) != null;
    }

}
