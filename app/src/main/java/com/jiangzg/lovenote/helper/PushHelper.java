package com.jiangzg.lovenote.helper;

import android.content.Context;

import com.alibaba.sdk.android.push.CloudPushService;
import com.alibaba.sdk.android.push.CommonCallback;
import com.alibaba.sdk.android.push.noonesdk.PushServiceFactory;
import com.jiangzg.base.common.LogUtils;

/**
 * Created by JZG on 2018/10/19.
 * 推送帮助类
 */
public class PushHelper {


    public static void init(Context ctx) {
        PushServiceFactory.init(ctx);
        final CloudPushService pushService = PushServiceFactory.getCloudPushService();
        pushService.register(ctx, new CommonCallback() {
            @Override
            public void onSuccess(String response) {
                String deviceId = pushService.getDeviceId();
                LogUtils.i(PushHelper.class, "init", "推送注册-成功。\n" + response);
            }

            // 失败会自动进行重新注册，直到onSuccess为止
            @Override
            public void onFailed(String errorCode, String errorMessage) {
                LogUtils.w(PushHelper.class, "init", "推送注册-失败。\nerrorCode:" + errorCode + "\nerrorMessage:" + errorMessage);
            }
        });
    }
}
