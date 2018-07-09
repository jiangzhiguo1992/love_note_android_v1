package com.jiangzg.mianmian.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.jiangzg.base.common.ConstantUtils;
import com.jiangzg.base.common.LogUtils;
import com.jiangzg.base.system.AlarmUtils;
import com.jiangzg.mianmian.base.MyApp;
import com.jiangzg.mianmian.domain.OssInfo;
import com.jiangzg.mianmian.domain.Result;
import com.jiangzg.mianmian.helper.API;
import com.jiangzg.mianmian.helper.OssHelper;
import com.jiangzg.mianmian.helper.RetrofitHelper;
import com.jiangzg.mianmian.helper.SPHelper;

import retrofit2.Call;

/**
 * 定时广播oss更新
 */
public class OssRefreshReceiver extends BroadcastReceiver {

    // ossInfo获取到之后在开始
    public static void startAlarm() {
        long interval = SPHelper.getOssInfo().getIntervalSec() * 1000;
        // 发送定时广播
        AlarmUtils.sendWaitBroadcast(OssRefreshReceiver.class, interval);
    }

    // startAlarm之后接受
    @Override
    public void onReceive(Context context, Intent intent) {
        LogUtils.i(OssRefreshReceiver.class, "onReceive", "收到oss更新广播");
        // 这次广播要刷新的数据
        ossInfoUpdate();
        // 继续发送定时，下一次广播
        long interval = SPHelper.getOssInfo().getIntervalSec() * 1000;
        AlarmUtils.sendWaitBroadcast(OssRefreshReceiver.class, interval);
    }

    // 更新oss信息
    private static void ossInfoUpdate() {
        Call<Result> call = new RetrofitHelper().call(API.class).ossGet();
        RetrofitHelper.enqueue(call, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                LogUtils.i(OssRefreshReceiver.class, "ossInfoUpdate", "oss更新成功");
                OssInfo ossInfo = data.getOssInfo();
                // 刷新ossInfo
                SPHelper.setOssInfo(ossInfo);
                // 刷新ossClient
                OssHelper.refreshOssClient();
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                MyApp.get().getHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        LogUtils.w(OssRefreshReceiver.class, "ossInfoUpdate", "oss更新失败");
                        ossInfoUpdate(); // 重复更新
                    }
                }, 5 * ConstantUtils.SEC);
            }
        });
    }

}
