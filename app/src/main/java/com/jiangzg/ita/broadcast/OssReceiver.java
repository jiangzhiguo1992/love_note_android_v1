package com.jiangzg.ita.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.jiangzg.base.common.ConstantUtils;
import com.jiangzg.base.common.LogUtils;
import com.jiangzg.base.system.AlarmUtils;
import com.jiangzg.base.time.DateUtils;
import com.jiangzg.ita.base.MyApp;
import com.jiangzg.ita.domain.OssInfo;
import com.jiangzg.ita.domain.Result;
import com.jiangzg.ita.helper.SPHelper;
import com.jiangzg.ita.third.API;
import com.jiangzg.ita.third.OssHelper;
import com.jiangzg.ita.third.RetrofitHelper;

import retrofit2.Call;

/**
 * 定时广播oss更新
 */
public class OssReceiver extends BroadcastReceiver {

    private static String LOG_TAG = "OssReceiver";
    private static long expire; // 过期时间
    private static long interval = 10 * 60 * 1000; // 默认十分钟

    @Override
    public void onReceive(Context context, Intent intent) {
        LogUtils.i(LOG_TAG, "收到oss更新广播");
        AlarmUtils.sendWaitBroadcast(OssReceiver.class, interval); // 继续发送定时
        ossInfoUpdate();
    }

    // ossInfo获取到之后在开始
    public static void startAlarm() {
        // 初始化信息
        OssInfo ossInfo = SPHelper.getOssInfo();
        initInfo(ossInfo);
        long advance = 5 * ConstantUtils.MIN; // 提前五分钟更新时间
        long currentLong = DateUtils.getCurrentLong();
        long between = expire - currentLong;
        long wait;
        if (between <= advance) { // 到更新时间了
            ossInfoUpdate();
            wait = interval - advance; // 计算下次更新时间
        } else { // 没到更新时间
            wait = (expire - currentLong) - advance;
        }
        // 发送定时广播
        AlarmUtils.sendWaitBroadcast(OssReceiver.class, wait);
    }

    // 更新oss信息
    private static void ossInfoUpdate() {
        Call<Result> call = new RetrofitHelper().call(API.class).ossGet();
        RetrofitHelper.enqueue(call, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                LogUtils.i(LOG_TAG, "oss更新成功");
                OssInfo ossInfo = data.getOssInfo();
                initInfo(ossInfo);
                SPHelper.setOssInfo(ossInfo);
                OssHelper.refreshOssClient();
            }

            @Override
            public void onFailure() {
                MyApp.get().getHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        LogUtils.w(LOG_TAG, "oss更新失败");
                        ossInfoUpdate(); // 重复更新
                    }
                }, 5 * ConstantUtils.SEC);
            }
        });
    }

    private static void initInfo(OssInfo ossInfo) {
        expire = ossInfo.getExpiration() * 1000;
        long inter = ossInfo.getInterval() * 1000;
        if (inter > 0) {
            interval = inter;
        }
    }

}
