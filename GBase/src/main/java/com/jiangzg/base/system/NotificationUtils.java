package com.jiangzg.base.system;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.NotificationManagerCompat;

import com.jiangzg.base.application.AppBase;
import com.jiangzg.base.common.LogUtils;

/**
 * Created by JZG on 2018/10/19.
 * 通知管理类
 */
public class NotificationUtils {

    // setNotificationChannel
    public static void setNotificationChannel(NotificationManager manager,
                                              String channelId, String channelName, int channelLevel,
                                              String channelDesc, boolean light, boolean vibrate) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return;
        if (manager == null) {
            LogUtils.w(NotificationUtils.class, "setNotificationChannel", "manager == null");
            return;
        }
        // id是通道，name 和 desc 用户可见，
        NotificationChannel mChannel = new NotificationChannel(channelId, channelName, channelLevel);
        // 配置通知渠道的属性
        mChannel.setDescription(channelDesc);
        // 设置通知出现时的闪灯（如果 android 设备支持的话）
        mChannel.enableLights(light);
        if (light) {
            mChannel.setLightColor(Color.RED);
        }
        // 设置通知出现时的震动（如果 android 设备支持的话）
        mChannel.enableVibration(vibrate);
        if (vibrate) {
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
        }
        //最后在notificationManager中创建该通知渠道
        manager.createNotificationChannel(mChannel);
    }

    // 获取通知是否打开
    public static boolean isNotificationEnabled() {
        return NotificationManagerCompat.from(AppBase.getInstance()).areNotificationsEnabled();
        //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        //        //    //8.0手机以上
        //        //    if (AppBase.getNotificationManager().getImportance() == NotificationManager.IMPORTANCE_NONE) {
        //        //        return false;
        //        //    }
        //        //}
        //String CHECK_OP_NO_THROW = "checkOpNoThrow";
        //String OP_POST_NOTIFICATION = "OP_POST_NOTIFICATION";
        //
        //AppOpsManager mAppOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        //ApplicationInfo appInfo = context.getApplicationInfo();
        //String pkg = context.getApplicationContext().getPackageName();
        //int uid = appInfo.uid;
        //
        //Class appOpsClass = null;
        //try {
        //    appOpsClass = Class.forName(AppOpsManager.class.getName());
        //    Method checkOpNoThrowMethod = appOpsClass.getMethod(CHECK_OP_NO_THROW, Integer.TYPE, Integer.TYPE,
        //            String.class);
        //    Field opPostNotificationValue = appOpsClass.getDeclaredField(OP_POST_NOTIFICATION);
        //
        //    int value = (Integer) opPostNotificationValue.get(Integer.class);
        //    return ((Integer) checkOpNoThrowMethod.invoke(mAppOps, value, uid, pkg) == AppOpsManager.MODE_ALLOWED);
        //
        //} catch (Exception e) {
        //    e.printStackTrace();
        //}
        //return false;
    }

}
