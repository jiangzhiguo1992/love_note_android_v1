package com.jiangzg.base.function;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by gg on 2017/5/9.
 * 权限
 */
public class PermUtils {

    private static final String LOG_TAG = "PermUtils";

    // app信息,后面的始终拒绝 MOUNT_UNMOUNT_FILESYSTEMS,MOUNT_FORMAT_FILESYSTEMS
    public static final String[] appInfo = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    // 设备信息
    public static final String[] device = new String[]{Manifest.permission.READ_PHONE_STATE};

    // 拍照
    public static final String[] camera = new String[]{Manifest.permission.CAMERA};

    // 地图定位
    public static final String[] location = new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION};

    // context显示dialog
    public static final String[] alertWindow = new String[]{Manifest.permission.SYSTEM_ALERT_WINDOW};

    // 拨号
    public static final String[] callPhone = new String[]{Manifest.permission.CALL_PHONE};

    // 联系人，账号
    public static final String[] contacts = new String[]{Manifest.permission.WRITE_CONTACTS,
            Manifest.permission.GET_ACCOUNTS, Manifest.permission.READ_CONTACTS};

    // 短信
    public static final String[] sms = new String[]{Manifest.permission.READ_SMS,
            Manifest.permission.RECEIVE_WAP_PUSH, Manifest.permission.RECEIVE_MMS,
            Manifest.permission.RECEIVE_SMS, Manifest.permission.SEND_SMS};

    // 日历
    public static final String[] calender = new String[]{Manifest.permission.READ_CALENDAR,
            Manifest.permission.WRITE_CALENDAR};

    // 麦克风
    public static final String[] audio = new String[]{Manifest.permission.RECORD_AUDIO};

    // 传感器
    @SuppressLint("InlinedApi")
    public static final String[] sensors = new String[]{Manifest.permission.BODY_SENSORS};

    private static Map<Integer, OnPermissionListener> permListeners = new HashMap<>();

    public interface OnPermissionListener {
        void onPermissionGranted(int requestCode, String[] permissions);

        void onPermissionDenied(int requestCode, String[] permissions);
    }

    @TargetApi(Build.VERSION_CODES.M)
    public static void requestPermissions(Activity activity, int requestCode, String[] permissions, OnPermissionListener listener) {
        if (activity == null) {
            Log.e(LOG_TAG, "requestPermissions: activity == null");
            return;
        }
        if (listener != null) {
            if (isPermissionOK(activity, permissions)) {
                listener.onPermissionGranted(requestCode, permissions);
                return;
            } else { //加进去
                permListeners.put(requestCode, listener);
            }
        }
        List<String> deniedPermissions = getDeniedPermissions(activity, permissions);
        if (deniedPermissions.size() > 0) {
            String[] strings = deniedPermissions.toArray(new String[deniedPermissions.size()]);
            activity.requestPermissions(strings, requestCode);
        } else {
            if (listener != null) {
                Log.d(LOG_TAG, "requestPermissions-->onPermissionGranted");
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
                Log.d(LOG_TAG, "onRequestPermissionsResult-->onPermissionGranted");
                listener.onPermissionGranted(requestCode, permissions);
            } else {
                Log.d(LOG_TAG, "onRequestPermissionsResult-->onPermissionDenied");
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
            Log.e(LOG_TAG, "isPermissionOK: context == null");
            return false;
        }
        List<String> deniedPermissions = getDeniedPermissions(context, permissions);
        return deniedPermissions.size() <= 0;
    }

    /* 获取请求权限中需要授权的权限 */
    private static List<String> getDeniedPermissions(Context context, String... permissions) {
        List<String> deniedPermissions = new ArrayList<>();
        if (context == null) {
            Log.e(LOG_TAG, "getDeniedPermissions: context == null");
            return deniedPermissions;
        }
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_DENIED) {
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
