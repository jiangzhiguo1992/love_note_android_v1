package com.jiangzg.lovenote.helper;

import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import com.alibaba.sdk.android.push.CloudPushService;
import com.alibaba.sdk.android.push.CommonCallback;
import com.alibaba.sdk.android.push.noonesdk.PushServiceFactory;
import com.alibaba.sdk.android.push.register.HuaWeiRegister;
import com.alibaba.sdk.android.push.register.MiPushRegister;
import com.jiangzg.base.common.LogUtils;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.system.NotificationUtils;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.base.MyApp;
import com.jiangzg.lovenote.domain.PushInfo;

/**
 * Created by JZG on 2018/10/19.
 * 推送帮助类
 */
public class PushHelper {

    private static boolean isRegister = false;

    public static void initApp(Context ctx) {
        PushServiceFactory.init(ctx);
        final CloudPushService pushService = PushServiceFactory.getCloudPushService();
        pushService.register(ctx, new CommonCallback() {
            @Override
            public void onSuccess(String response) {
                //String deviceId = pushService.getDeviceId();
                LogUtils.i(PushHelper.class, "init", "推送注册-成功。\n" + response);
                isRegister = true;
                // 别忘了辅助通道
                initThirdPush();
            }

            // 失败会自动进行重新注册，直到onSuccess为止
            @Override
            public void onFailed(String errorCode, String errorMessage) {
                LogUtils.w(PushHelper.class, "init", "推送注册-失败。\nerrorCode:" + errorCode + "\nerrorMessage:" + errorMessage);
                isRegister = false;
            }
        });
    }

    // 辅助通道注册务必在Application中执行且放在推送SDK初始化代码之后，否则可能导致辅助通道注册失败
    public static void initThirdPush() {
        if (!isRegister) return;
        PushInfo info = SPHelper.getPushInfo();
        if (info == null || StringUtils.isEmpty(info.getChannelId())) return;
        // 注册方法会自动判断是否支持小米系统推送，如不支持会跳过注册。
        MiPushRegister.register(MyApp.get(), info.getMiAppId(), info.getMiAppKey());
        // 注册方法会自动判断是否支持华为系统推送，如不支持会跳过注册。
        HuaWeiRegister.register(MyApp.get());
    }

    public static void initNotification() {
        PushInfo info = SPHelper.getPushInfo();
        if (info == null) {
            info = new PushInfo();
            info.setChannelId("1");
            info.setChannelName(MyApp.get().getString(R.string.notification));
            info.setChannelDesc("");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                info.setChannelLevel(NotificationManager.IMPORTANCE_HIGH);
            }
            info.setNoticeLight(true);
            info.setNoticeSound(true);
            info.setNoticeVibrate(true);
        }
        NotificationManager manager = MyApp.getNotificationManager();
        NotificationUtils.setNotificationChannel(manager, info.getChannelId(), info.getChannelName(),
                info.getChannelLevel(), info.getChannelDesc(), info.isNoticeLight(), info.isNoticeVibrate());
    }

    // TODO
    public static void unRegister() {

    }

}
