package com.jiangzg.base.system;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.util.ArrayMap;

import com.jiangzg.base.common.LogUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by gg on 2017/5/9.
 * 权限
 */
public class PermUtils {

    // app信息 后面的始终拒绝 MOUNT_UNMOUNT_FILESYSTEMS,MOUNT_FORMAT_FILESYSTEMS
    public static final String[] appInfo = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    // 设备信息
    public static final String[] deviceInfo = new String[]{Manifest.permission.READ_PHONE_STATE};
    // 拍照
    public static final String[] camera = new String[]{Manifest.permission.CAMERA};
    // 相册
    public static final String[] picture = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    // 安装apk
    @RequiresApi(api = Build.VERSION_CODES.M)
    public static final String[] installApk = new String[]{Manifest.permission.REQUEST_INSTALL_PACKAGES};
    // 地图定位
    public static final String[] location = new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
    // context显示dialog
    public static final String[] alertWindow = new String[]{Manifest.permission.SYSTEM_ALERT_WINDOW};
    // 拨号
    public static final String[] callPhone = new String[]{Manifest.permission.CALL_PHONE};
    // 联系人，账号
    public static final String[] contacts = new String[]{Manifest.permission.WRITE_CONTACTS, Manifest.permission.GET_ACCOUNTS, Manifest.permission.READ_CONTACTS};
    // 短信
    public static final String[] sms = new String[]{Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_WAP_PUSH, Manifest.permission.RECEIVE_MMS, Manifest.permission.RECEIVE_SMS, Manifest.permission.SEND_SMS};
    // 日历
    public static final String[] calender = new String[]{Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR};
    // 麦克风
    public static final String[] audio = new String[]{Manifest.permission.RECORD_AUDIO};
    // 传感器
    public static final String[] sensors = new String[]{Manifest.permission.BODY_SENSORS};

    private static Map<Integer, OnPermissionListener> permListeners = new ArrayMap<>();

    public interface OnPermissionListener {
        void onPermissionGranted(int requestCode, String[] permissions);

        void onPermissionDenied(int requestCode, String[] permissions);
    }

    public static void requestPermissions(Activity activity, int requestCode, String[] permissions, OnPermissionListener listener) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M || activity == null || permissions == null || permissions.length <= 0) {
            LogUtils.i(PermUtils.class, "requestPermissions", "requestPermissions: LOW_SDK || activity/permissions == null");
            if (listener != null) {
                listener.onPermissionGranted(requestCode, permissions);
            }
            return;
        }
        if (isPermissionOK(activity, permissions)) {
            if (listener != null) {
                listener.onPermissionGranted(requestCode, permissions);
            }
            return;
        } else { // 加进去
            if (listener != null) {
                permListeners.put(requestCode, listener);
            }
        }
        List<String> deniedPermissions = getDeniedPermissions(activity, permissions);
        if (deniedPermissions.size() > 0) {
            String[] strings = deniedPermissions.toArray(new String[deniedPermissions.size()]);
            activity.requestPermissions(strings, requestCode);
        } else {
            LogUtils.d(PermUtils.class, "requestPermissions", "Granted = " + LogUtils.getArrayLog(permissions));
            if (listener != null) {
                listener.onPermissionGranted(requestCode, permissions);
                permListeners.remove(requestCode); // 顺便移除
            }
        }
    }

    /**
     * 请求权限结果，对应Activity中onRequestPermissionsResult()方法。
     */
    public static void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        OnPermissionListener listener = permListeners.get(requestCode);
        if (listener != null) {
            if (verifyPermissions(grantResults)) {
                LogUtils.d(PermUtils.class, "onRequestPermissionsResult", "Granted = " + LogUtils.getArrayLog(permissions));
                listener.onPermissionGranted(requestCode, permissions);
            } else {
                LogUtils.d(PermUtils.class, "onRequestPermissionsResult", "Denied = " + LogUtils.getArrayLog(permissions));
                listener.onPermissionDenied(requestCode, permissions);
            }
            permListeners.remove(requestCode);
        }
    }

    /**
     * 检查权限
     */
    public static boolean isPermissionOK(Context context, String... permissions) {
        if (context == null) {
            LogUtils.w(PermUtils.class, "isPermissionOK", "context == null");
            return false;
        }
        if (permissions == null || permissions.length <= 0) {
            LogUtils.d(PermUtils.class, "isPermissionOK", "permissions == null");
            return true;
        }
        List<String> deniedPermissions = getDeniedPermissions(context, permissions);
        return deniedPermissions.size() <= 0;
    }

    /* 获取请求权限中需要授权的权限 */
    private static List<String> getDeniedPermissions(Context context, String... permissions) {
        List<String> deniedPermissions = new ArrayList<>();
        if (context == null || permissions == null || permissions.length <= 0) {
            LogUtils.w(PermUtils.class, "getDeniedPermissions", "context == null");
            return deniedPermissions;
        }
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_DENIED) {
                LogUtils.d(PermUtils.class, "getDeniedPermissions", permission);
                deniedPermissions.add(permission);
            }
        }
        return deniedPermissions;
    }

    /* 验证所有权限是否都已经授权 */
    private static boolean verifyPermissions(int[] grantResults) {
        for (int grantResult : grantResults) {
            if (grantResult != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

}
