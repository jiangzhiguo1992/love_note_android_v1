package com.jiangzg.base.component;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;

import com.jiangzg.base.application.AppBase;
import com.jiangzg.base.common.LogUtils;

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
        if (cls == null) {
            LogUtils.w(ServiceUtils.class, "isServiceRunning", "cls == null");
            return false;
        }
        ActivityManager manager = AppBase.getActivityManager();
        List<ActivityManager.RunningServiceInfo> services = manager.getRunningServices(Integer.MAX_VALUE);
        if (services == null || services.size() < 1) return false;
        for (ActivityManager.RunningServiceInfo serviceInfo : services) {
            // 从所有的服务里找
            if (serviceInfo.service.getClassName().equals(cls.getName())) return true;
        }
        return false;
    }

    /**
     * 关闭所有正在运行的服务
     */
    public static void stopAll() {
        Set<Class<?>> runningService = getRunningService();
        if (runningService == null || runningService.size() <= 0) return;
        for (Class<?> cls : runningService) {
            LogUtils.i(ServiceUtils.class, "stopAll", cls.getSimpleName());
            stopService(cls);
        }
    }

    /**
     * 获取所有运行的服务
     */
    public static Set<Class<?>> getRunningService() {
        ActivityManager manager = AppBase.getActivityManager();
        List<ActivityManager.RunningServiceInfo> services = manager.getRunningServices(Integer.MAX_VALUE);
        if (services == null || services.size() == 0) return new HashSet<>();
        Set<Class<?>> classSet = new HashSet<>();
        for (ActivityManager.RunningServiceInfo info : services) {
            classSet.add(info.service.getClass());
        }
        return classSet;
    }

    /**
     * 启动服务 onCreate -> onStartCommand(onStart一般为1.0以下时使用)
     */
    public static void startService(Class<?> cls) {
        if (cls == null) {
            LogUtils.w(ServiceUtils.class, "startService", "cls == null");
            return;
        }
        Intent intent = new Intent(AppBase.getInstance(), cls);
        AppBase.getInstance().startService(intent);
    }

    /**
     * 停止服务
     */
    public static boolean stopService(Class<?> cls) {
        if (cls == null) {
            LogUtils.w(ServiceUtils.class, "stopService", "cls == null");
            return false;
        }
        Intent intent = new Intent(AppBase.getInstance(), cls);
        return AppBase.getInstance().stopService(intent);
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
        if (cls == null || conn == null) {
            LogUtils.w(ServiceUtils.class, "bindService", "cls == null || conn == null");
            return;
        }
        Intent intent = new Intent(AppBase.getInstance(), cls);
        AppBase.getInstance().bindService(intent, conn, flags);
    }

    /**
     * 解绑服务,之后会destroy
     *
     * @param conn 服务连接对象
     */
    public static void unbindService(ServiceConnection conn) {
        if (conn == null) {
            LogUtils.w(ServiceUtils.class, "unbindService", "cls == null");
            return;
        }
        AppBase.getInstance().unbindService(conn);
    }

}
