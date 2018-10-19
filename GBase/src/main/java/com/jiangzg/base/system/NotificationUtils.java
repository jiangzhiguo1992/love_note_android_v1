package com.jiangzg.base.system;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.graphics.Color;
import android.os.Build;

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
}
