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
import com.jiangzg.lovenote.domain.User;

/**
 * Created by JZG on 2018/10/19.
 * 推送帮助类
 */
public class PushHelper {

    public static void initApp(Context ctx, boolean debug) {
        PushInfo pushInfo = SPHelper.getPushInfo();
        if (pushInfo == null || StringUtils.isEmpty(pushInfo.getAliAppKey()) || StringUtils.isEmpty(pushInfo.getAliAppSecret())) {
            LogUtils.d(PushHelper.class, "initApp", "推送注册-首次。");
            return;
        }
        PushServiceFactory.init(ctx);
        CloudPushService service = PushServiceFactory.getCloudPushService();
        if (service == null) return;
        service.setLogLevel(debug ? CloudPushService.LOG_DEBUG : CloudPushService.LOG_OFF);
        service.register(ctx, pushInfo.getAliAppKey(), pushInfo.getAliAppSecret(), new CommonCallback() {
            @Override
            public void onSuccess(String response) {
                LogUtils.i(PushHelper.class, "initApp", "推送注册-成功。\n" + response);
                // 别忘了辅助通道
                initThirdPush();
                // 开启推送通道
                initNotification();
                // 绑定账号
                bindAccount();
                // 绑定tag
                checkTagBind();
            }

            @Override
            public void onFailed(String errorCode, String errorMessage) {
                // 失败会自动进行重新注册，直到onSuccess为止
                LogUtils.w(PushHelper.class, "initApp", "推送注册-失败。\nerrorCode:" + errorCode + "\nerrorMessage:" + errorMessage);
            }
        });
    }

    // 辅助通道注册务必在Application中执行且放在推送SDK初始化代码之后，否则可能导致辅助通道注册失败
    private static void initThirdPush() {
        PushInfo info = SPHelper.getPushInfo();
        if (info == null || StringUtils.isEmpty(info.getChannelId())) return;
        LogUtils.i(PushHelper.class, "initThirdPush", "推送注册(辅助通道)");
        // 注册方法会自动判断是否支持小米系统推送，如不支持会跳过注册。
        MiPushRegister.register(MyApp.get(), info.getMiAppId(), info.getMiAppKey());
        // 注册方法会自动判断是否支持华为系统推送，如不支持会跳过注册。
        HuaWeiRegister.register(MyApp.get());
    }

    private static void initNotification() {
        //CloudPushService pushService = PushServiceFactory.getCloudPushService();
        //pushService.turnOnPushChannel(new CommonCallback() {
        //    @Override
        //    public void onSuccess(String s) {
        //    }
        //
        //    @Override
        //    public void onFailed(String s, String s1) {
        //    }
        //});
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

    private static void bindAccount() {
        CloudPushService service = PushServiceFactory.getCloudPushService();
        if (service == null) return;
        User me = SPHelper.getMe();
        String phone = me.getPhone();
        if (StringUtils.isEmpty(phone)) return;
        service.bindAccount(phone, new CommonCallback() {
            @Override
            public void onSuccess(String s) {
                LogUtils.i(PushHelper.class, "bindAccount", "推送绑定-成功\n" + s);
            }

            @Override
            public void onFailed(String s, String s1) {
                LogUtils.i(PushHelper.class, "bindAccount", "推送绑定-失败\n" + s + "\n" + s1);
            }
        });
    }

    public static void unBindAccount() {
        CloudPushService service = PushServiceFactory.getCloudPushService();
        if (service == null) return;
        User me = SPHelper.getMe();
        long id = me.getId();
        if (id <= 0) return;
        service.unbindAccount(new CommonCallback() {
            @Override
            public void onSuccess(String s) {
                LogUtils.i(PushHelper.class, "unBindAccount", "推送取消绑定-成功\n" + s);
            }

            @Override
            public void onFailed(String s, String s1) {
                LogUtils.i(PushHelper.class, "unBindAccount", "推送取消绑定-失败\n" + s + "\n" + s1);
            }
        });
    }

    public static void checkTagBind() {
        boolean system = SPHelper.getSettingsNoticeSystem();
        boolean social = SPHelper.getSettingsNoticeSocial();
        boolean disturb = SPHelper.getSettingsNoticeDisturb();
        if (system) {
            bindTag("system");
        } else {
            unBindTag("system");
        }
        if (social) {
            bindTag("social");
        } else {
            unBindTag("social");
        }
        // 注意顺序
        if (disturb) {
            unBindTag("disturb");
            stopDisturb();
        } else {
            bindTag("disturb");
            startDisturb();
        }
    }

    private static void bindTag(String tag) {
        CloudPushService service = PushServiceFactory.getCloudPushService();
        if (service == null) return;
        // 绑定设备吧，tag要和本地数据保持一致
        service.bindTag(CloudPushService.DEVICE_TARGET, new String[]{tag}, "", new CommonCallback() {
            @Override
            public void onSuccess(String s) {
                LogUtils.i(PushHelper.class, "bindTag", "TAG绑定-成功\n" + s);
            }

            @Override
            public void onFailed(String s, String s1) {
                LogUtils.i(PushHelper.class, "bindTag", "TAG绑定-失败\n" + s + "\n" + s1);
            }
        });
    }

    private static void unBindTag(String tag) {
        CloudPushService service = PushServiceFactory.getCloudPushService();
        if (service == null) return;
        service.unbindTag(CloudPushService.DEVICE_TARGET, new String[]{tag}, "", new CommonCallback() {
            @Override
            public void onSuccess(String s) {
                LogUtils.i(PushHelper.class, "unBindTag", "TAG取消绑定-成功\n" + s);
            }

            @Override
            public void onFailed(String s, String s1) {
                LogUtils.i(PushHelper.class, "unBindTag", "TAG取消绑定-失败\n" + s + "\n" + s1);
            }
        });
    }

    private static void startDisturb() {
        // TODO
        CloudPushService service = PushServiceFactory.getCloudPushService();
        if (service == null) return;
        int startHour = 22;
        int startMin = 0;
        int endHour = 9;
        int endMin = 0;
        service.setDoNotDisturb(startHour, startMin, endHour, endMin, new CommonCallback() {
            @Override
            public void onSuccess(String s) {

            }

            @Override
            public void onFailed(String s, String s1) {

            }
        });
    }

    private static void stopDisturb() {
        // TODO
    }

}
