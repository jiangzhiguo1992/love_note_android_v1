package com.android.base.component.service;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;

import com.android.base.component.application.AppContext;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by gg on 2017/5/9.
 * Service工具类
 */
public class ServiceUtils {

    /**
     * 判断服务是否运行
     */
    public static boolean isServiceRunning(Class<?> cls) {
        List<ActivityManager.RunningServiceInfo> myList = AppContext
                .getActivityManager().getRunningServices(Integer.MAX_VALUE);
        if (myList == null || myList.size() < 1) return false;
        for (ActivityManager.RunningServiceInfo serviceInfo : myList) {
            if (serviceInfo.service.getClassName().equals(cls.getName())) return true;
        }
        return false;
    }

    /**
     * 获取所有运行的服务
     */
    public static Set getRunningService() {
        ActivityManager activityManager = AppContext.getActivityManager();
        List<ActivityManager.RunningServiceInfo> infos = activityManager
                .getRunningServices(Integer.MAX_VALUE);
        Set<String> names = new HashSet<>();
        if (infos == null || infos.size() == 0) return names;
        for (ActivityManager.RunningServiceInfo info : infos) {
            names.add(info.service.getClassName());
        }
        return names;
    }

    /**
     * 启动服务 onCreate -> onStartCommand(onStart一般为1.0以下时使用)
     */
    public static void startService(Class<?> cls) {
        Intent intent = new Intent(AppContext.get(), cls);
        AppContext.get().startService(intent);
    }

    /**
     * 停止服务
     */
    public static boolean stopService(Class<?> cls) {
        Intent intent = new Intent(AppContext.get(), cls);
        return AppContext.get().stopService(intent);
    }

    /**
     * 绑定服务 onCreate -> onBind
     *
     * @param cls   服务类
     * @param conn  服务连接对象
     * @param flags 绑定选项
     *              <ul>
     *              <li>{@link Context#BIND_AUTO_CREATE}</li>
     *              <li>{@link Context#BIND_DEBUG_UNBIND}</li>
     *              <li>{@link Context#BIND_NOT_FOREGROUND}</li>
     *              <li>{@link Context#BIND_ABOVE_CLIENT}</li>
     *              <li>{@link Context#BIND_ALLOW_OOM_MANAGEMENT}</li>
     *              <li>{@link Context#BIND_WAIVE_PRIORITY}</li>
     *              </ul>
     */
    public static void bindService(Class<?> cls, ServiceConnection conn, int flags) {
        Intent intent = new Intent(AppContext.get(), cls);
        AppContext.get().bindService(intent, conn, flags);
    }

    /**
     * 解绑服务,之后会destroy
     *
     * @param conn 服务连接对象
     */
    public static void unbindService(ServiceConnection conn) {
        AppContext.get().unbindService(conn);
    }

}
