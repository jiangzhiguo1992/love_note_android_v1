package com.jiangzg.lovenote.helper.system;

import android.app.NotificationManager;
import android.content.Context;

import com.alibaba.sdk.android.push.CloudPushService;
import com.alibaba.sdk.android.push.CommonCallback;
import com.alibaba.sdk.android.push.noonesdk.PushServiceFactory;
import com.alibaba.sdk.android.push.register.HuaWeiRegister;
import com.alibaba.sdk.android.push.register.MiPushRegister;
import com.alibaba.sdk.android.push.register.OppoRegister;
import com.jiangzg.base.common.LogUtils;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.system.PushUtils;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.helper.common.SPHelper;
import com.jiangzg.lovenote.main.MyApp;
import com.jiangzg.lovenote.model.entity.PushInfo;
import com.jiangzg.lovenote.model.entity.User;

/**
 * Created by JZG on 2018/10/19.
 * 推送帮助类
 */
public class PushHelper {

    public static void initApp(Context ctx, boolean debug) {
        // 第一次进入注册不成功！
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
                // 辅助通道
                initThirdPush();
                // 开启推送通道
                initNotification();
            }

            @Override
            public void onFailed(String errorCode, String errorMessage) {
                // 失败会自动进行重新注册，直到onSuccess为止
                LogUtils.w(PushHelper.class, "initApp", "推送注册-失败。\nerrorCode:" + errorCode + "\nerrorMessage:" + errorMessage);
            }
        });
    }

    // 辅助通道注册务必在Application中执行且放在推送SDK初始化代码之后，否则可能导致辅助通道注册失败
    // 小米辅助弹窗：v2.3.0及以上支持；华为辅助弹窗：v3.0.8及以上支持；OPPO辅助弹窗：v3.1.4及以上支持
    // 注册方法会自动判断是否支持辅助推送，如不支持会跳过注册。
    private static void initThirdPush() {
        PushInfo info = SPHelper.getPushInfo();
        if (info == null) return;
        LogUtils.i(PushHelper.class, "initThirdPush", "推送注册(辅助通道)");
        // 华为
        HuaWeiRegister.register(MyApp.get());
        // oppo
        if (!StringUtils.isEmpty(info.getOppoAppKey()) && !StringUtils.isEmpty(info.getOppoAppSecret())) {
            OppoRegister.register(MyApp.get(), info.getOppoAppKey(), info.getOppoAppSecret());
        }
        // 小米
        if (!StringUtils.isEmpty(info.getMiAppId()) && !StringUtils.isEmpty(info.getMiAppKey())) {
            MiPushRegister.register(MyApp.get(), info.getMiAppId(), info.getMiAppKey());
        }
    }

    private static void initNotification() {
        PushInfo info = SPHelper.getPushInfo();
        if (info == null) {
            info = new PushInfo();
            info.setChannelId("520");
            info.setNoticeLight(true);
            info.setNoticeSound(true);
            info.setNoticeVibrate(false);
        }
        if (StringUtils.isEmpty(info.getChannelId())) {
            info.setChannelId("520");
        }
        String name = MyApp.get().getString(R.string.notification);
        NotificationManager manager = MyApp.getNotificationManager();
        PushUtils.setNotificationChannel(manager, info.getChannelId(), name, name, info.isNoticeLight(), info.isNoticeVibrate());
    }

    // 账号
    public static void checkAccountBind() {
        if (SPHelper.getSettingsNoticeSocial()) {
            PushHelper.bindAccount();
        } else {
            PushHelper.unBindAccount();
        }
    }

    private static void bindAccount() {
        CloudPushService service = PushServiceFactory.getCloudPushService();
        if (service == null) return;
        User me = SPHelper.getMe();
        if (me == null || me.getId() == 0) return;
        long uid = me.getId();
        service.bindAccount(String.valueOf(uid), new CommonCallback() {
            @Override
            public void onSuccess(String s) {
                LogUtils.i(PushHelper.class, "bindAccount", "account绑定-成功\n" + s);
            }

            @Override
            public void onFailed(String s, String s1) {
                LogUtils.i(PushHelper.class, "bindAccount", "account绑定-失败\n" + s + "\n" + s1);
            }
        });
    }

    public static void unBindAccount() {
        CloudPushService service = PushServiceFactory.getCloudPushService();
        if (service == null) return;
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
        // 清除SDK创建的所有通知
        service.clearNotifications();
    }

    // 标签
    public static void checkTagBind() {
        if (SPHelper.getSettingsNoticeSystem()) {
            PushHelper.bindTag("lovenote_notice_system");
        } else {
            PushHelper.unBindTag("lovenote_notice_system");
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

    // 免打扰
    public static void checkDisturb() {
        if (SPHelper.getSettingsNoticeDisturb()) {
            PushHelper.startDisturb();
        } else {
            PushHelper.stopDisturb();
        }
    }

    private static void startDisturb() {
        CloudPushService service = PushServiceFactory.getCloudPushService();
        if (service == null) return;
        PushInfo pushInfo = SPHelper.getPushInfo();
        if (pushInfo == null) return;
        int startHour = pushInfo.getNoStartHour();
        int endHour = pushInfo.getNoEndHour();
        service.setDoNotDisturb(startHour, 0, endHour, 0, new CommonCallback() {
            @Override
            public void onSuccess(String s) {
                LogUtils.i(PushHelper.class, "startDisturb", "免打扰-成功\n" + s);
            }

            @Override
            public void onFailed(String s, String s1) {
                LogUtils.i(PushHelper.class, "startDisturb", "免打扰-失败\n" + s + "\n" + s1);
            }
        });
    }

    private static void stopDisturb() {
        CloudPushService service = PushServiceFactory.getCloudPushService();
        if (service == null) return;
        service.closeDoNotDisturbMode();
    }

}
