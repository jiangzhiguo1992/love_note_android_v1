package com.jiangzg.lovenote.controller.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.jiangzg.base.common.LogUtils;
import com.jiangzg.base.system.AlarmUtils;
import com.jiangzg.lovenote.helper.ApiHelper;
import com.jiangzg.lovenote.helper.SPHelper;

/**
 * 定时广播oss更新
 */
public class OssRefreshReceiver extends BroadcastReceiver {

    // ossInfo获取到之后在开始
    public static void startAlarm() {
        long interval = SPHelper.getOssInfo().getOssRefreshSec() * 1000;
        // 发送定时广播
        AlarmUtils.sendWaitBroadcast(OssRefreshReceiver.class, interval);
    }

    // startAlarm之后接受
    @Override
    public void onReceive(Context context, Intent intent) {
        LogUtils.i(OssRefreshReceiver.class, "onReceive", "收到oss更新广播");
        // 这次广播要刷新的数据
        ApiHelper.ossInfoUpdate();
        // 继续发送定时，下一次广播
        long interval = SPHelper.getOssInfo().getOssRefreshSec() * 1000;
        AlarmUtils.sendWaitBroadcast(OssRefreshReceiver.class, interval);
    }

}
