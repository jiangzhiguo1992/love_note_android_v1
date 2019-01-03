package com.jiangzg.base.system;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.jiangzg.base.application.AppBase;
import com.jiangzg.base.common.ConstantUtils;
import com.jiangzg.base.common.LogUtils;
import com.jiangzg.base.time.DateUtils;

/**
 * author cipherGG
 * Created by gg on 2016/4/10.
 * 定时广播机制
 */
public class AlarmUtils {

    /**
     * 在RTCTrigger时间启动activity
     *
     * @return PendingIntent便于cancel
     */
    public static PendingIntent sendWaitActivity(Context context, Class<?> cls, long waitMillis) {
        return sendWaitActivity(context, cls, 0, 0, waitMillis);
    }

    public static PendingIntent sendTriggerActivity(Context context, Class<?> cls, long trigger) {
        return sendTriggerActivity(context, cls, 0, 0, trigger);
    }

    public static PendingIntent sendWaitActivity(Context context, Class<?> cls, int requestCode, int flags, long waitMillis) {
        long trigger = System.currentTimeMillis() + waitMillis;
        return sendTriggerActivity(context, cls, requestCode, flags, trigger);
    }

    public static PendingIntent sendTriggerActivity(Context context, Class<?> cls, int requestCode, int flags, long trigger) {
        if (context == null || cls == null) {
            LogUtils.w(AlarmUtils.class, "sendTriggerActivity", "context/cls == null");
            return null;
        } else {
            String time = DateUtils.getStr(trigger, ConstantUtils.FORMAT_LINE_Y_M_D_H_M_S);
            LogUtils.i(AlarmUtils.class, "sendTriggerService", "将在 " + time + " 启动Activity");
        }
        AlarmManager alarmManager = AppBase.getAlarmManager();
        Intent intent = new Intent(context, cls);
        PendingIntent pendingIntent = PendingIntent.getActivity(AppBase.getInstance(), requestCode, intent, flags);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { // 防止低电量不发广播
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, trigger, pendingIntent);
        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, trigger, pendingIntent);
        }
        return pendingIntent;
    }

    /**
     * 在RTCTrigger时间发送广播
     */
    public static PendingIntent sendWaitBroadcast(Class<?> cls, long waitMillis) {
        return sendWaitBroadcast(cls, 0, 0, waitMillis);
    }

    public static PendingIntent sendTriggerBroadcast(Class<?> cls, long trigger) {
        return sendTriggerBroadcast(cls, 0, 0, trigger);
    }

    public static PendingIntent sendWaitBroadcast(Class<?> cls, int requestCode, int flags, long waitMillis) {
        long trigger = System.currentTimeMillis() + waitMillis;
        return sendTriggerBroadcast(cls, requestCode, flags, trigger);
    }

    public static PendingIntent sendTriggerBroadcast(Class<?> cls, int requestCode, int flags, long trigger) {
        if (cls == null) {
            LogUtils.w(AlarmUtils.class, "sendTriggerBroadcast", "cls == null");
            return null;
        } else {
            String time = DateUtils.getStr(trigger, ConstantUtils.FORMAT_LINE_Y_M_D_H_M_S);
            LogUtils.i(AlarmUtils.class, "sendTriggerBroadcast", "将在 " + time + " 发送Broadcast");
        }
        AlarmManager alarmManager = AppBase.getAlarmManager();
        Intent intent = new Intent(AppBase.getInstance(), cls);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(AppBase.getInstance(), requestCode, intent, flags);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { // 防止低电量不发广播
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, trigger, pendingIntent);
        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, trigger, pendingIntent);
        }
        return pendingIntent;
    }

    /**
     * 在RTCTrigger时间启动service
     */
    public static PendingIntent sendWaitService(Class<?> cls, long waitMillis) {
        return sendWaitService(cls, 0, 0, waitMillis);
    }

    public static PendingIntent sendTriggerService(Class<?> cls, long trigger) {
        return sendTriggerService(cls, 0, 0, trigger);
    }

    public static PendingIntent sendWaitService(Class<?> cls, int requestCode, int flags, long waitMillis) {
        long trigger = System.currentTimeMillis() + waitMillis;
        return sendTriggerService(cls, requestCode, flags, trigger);
    }

    public static PendingIntent sendTriggerService(Class<?> cls, int requestCode, int flags, long trigger) {
        if (cls == null) {
            LogUtils.w(AlarmUtils.class, "sendTriggerService", "cls == null");
            return null;
        } else {
            String time = DateUtils.getStr(trigger, ConstantUtils.FORMAT_LINE_Y_M_D_H_M_S);
            LogUtils.i(AlarmUtils.class, "sendTriggerService", "将在 " + time + " 启动Service");
        }
        AlarmManager alarmManager = AppBase.getAlarmManager();
        Intent intent = new Intent(AppBase.getInstance(), cls);
        PendingIntent pendingIntent = PendingIntent.getService(AppBase.getInstance(), requestCode, intent, flags);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { // 防止低电量不发广播
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, trigger, pendingIntent);
        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, trigger, pendingIntent);
        }
        return pendingIntent;
    }

}
