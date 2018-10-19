package com.jiangzg.lovenote.helper;

import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import com.alibaba.sdk.android.push.CloudPushService;
import com.alibaba.sdk.android.push.CommonCallback;
import com.alibaba.sdk.android.push.noonesdk.PushServiceFactory;
import com.jiangzg.base.common.LogUtils;
import com.jiangzg.base.system.NotificationUtils;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.base.MyApp;
import com.jiangzg.lovenote.domain.NotificationInfo;

/**
 * Created by JZG on 2018/10/19.
 * 推送帮助类
 */
public class PushHelper {

    public static void initApp(Context ctx) {
        PushServiceFactory.init(ctx);
        final CloudPushService pushService = PushServiceFactory.getCloudPushService();
        pushService.register(ctx, new CommonCallback() {
            @Override
            public void onSuccess(String response) {
                //String deviceId = pushService.getDeviceId();
                LogUtils.i(PushHelper.class, "init", "推送注册-成功。\n" + response);
            }

            // 失败会自动进行重新注册，直到onSuccess为止
            @Override
            public void onFailed(String errorCode, String errorMessage) {
                LogUtils.w(PushHelper.class, "init", "推送注册-失败。\nerrorCode:" + errorCode + "\nerrorMessage:" + errorMessage);
            }
        });
    }

    public static void initNitification(NotificationInfo info) {
        if (info == null) {
            info = new NotificationInfo();
            info.setChannelId("1");
            info.setChannelName(MyApp.get().getString(R.string.notification));
            info.setChannelDesc("");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                info.setChannelLevel(NotificationManager.IMPORTANCE_HIGH);
            }
            info.setLight(true);
            info.setVibrate(true);
        }
        NotificationManager manager = MyApp.getNotificationManager();
        NotificationUtils.setNotificationChannel(manager, info.getChannelId(), info.getChannelName(),
                info.getChannelLevel(), info.getChannelDesc(), info.isLight(), info.isVibrate());
    }

}
