package com.jiangzg.lovenote.helper;

import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import com.alibaba.sdk.android.push.CloudPushService;
import com.alibaba.sdk.android.push.CommonCallback;
import com.alibaba.sdk.android.push.noonesdk.PushServiceFactory;
import com.alibaba.sdk.android.push.register.HuaWeiRegister;
import com.alibaba.sdk.android.push.register.MiPushRegister;
import com.jiangzg.base.common.LogUtils;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.system.NotificationUtils;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.main.MyApp;
import com.jiangzg.lovenote.model.entity.PushInfo;
import com.jiangzg.lovenote.model.entity.User;

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
                checkAccountBind();
                // 绑定tag
                checkTagBind();
                // 免打扰
                checkDisturb();
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
            info.setChannelId("520");
            info.setChannelName(MyApp.get().getString(R.string.notification));
            info.setChannelDesc("");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                info.setChannelLevel(NotificationManager.IMPORTANCE_HIGH);
            }
            info.setNoticeLight(true);
            info.setNoticeSound(false);
            info.setNoticeVibrate(false);
        }
        NotificationManager manager = MyApp.getNotificationManager();
        NotificationUtils.setNotificationChannel(manager, info.getChannelId(), info.getChannelName(),
                info.getChannelLevel(), info.getChannelDesc(), info.isNoticeLight(), info.isNoticeVibrate());
    }

    // 账号
    public static void checkAccountBind() {
        boolean social = SPHelper.getSettingsNoticeSocial();
        if (social) {
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
        boolean system = SPHelper.getSettingsNoticeSystem();
        if (system) {
            PushHelper.bindTag("system");
        } else {
            PushHelper.unBindTag("system");
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
        boolean disturb = SPHelper.getSettingsNoticeDisturb();
        if (disturb) {
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

    public static class Push implements Parcelable {

        public static final int TYPE_APP = 0; // 打开app
        public static final int TYPE_SUGGEST = 50;// 打开suggest
        public static final int TYPE_COUPLE = 100;// 打开couple
        public static final int TYPE_NOTE = 200; // 打开note
        public static final int TYPE_NOTE_LOCK = 210;// 打开密码锁
        public static final int TYPE_NOTE_TRENDS = 211;// 打开动态
        public static final int TYPE_NOTE_TOTAL = 212;// 打开统计
        public static final int TYPE_NOTE_RECYCLE = 213;// 打开回收箱
        public static final int TYPE_NOTE_SHY = 220; // 打开羞羞
        public static final int TYPE_NOTE_MENSES = 221;// 打开姨妈
        public static final int TYPE_NOTE_SLEEP = 222;// 打开睡眠
        public static final int TYPE_NOTE_SOUVENIR = 230; // 打开纪念日
        public static final int TYPE_TOPIC = 300;// 打开topic
        public static final int TYPE_TOPIC_MINE = 310;// 打开我的
        public static final int TYPE_TOPIC_COLLECT = 320;// 打开收藏
        public static final int TYPE_TOPIC_MESSAGE = 330;// 打开消息
        public static final int TYPE_TOPIC_POST = 340;// 打开post
        public static final int TYPE_TOPIC_COMMENT = 350;// 打开postComment
        public static final int TYPE_MORE = 400;// 打开more

        private long createAt;
        private long userId;
        private String platform;
        private String title;
        private String contentText;
        private int contentType;
        private long contentId;

        public long getCreateAt() {
            return createAt;
        }

        public void setCreateAt(long createAt) {
            this.createAt = createAt;
        }

        public long getUserId() {
            return userId;
        }

        public void setUserId(long userId) {
            this.userId = userId;
        }

        public String getPlatform() {
            return platform;
        }

        public void setPlatform(String platform) {
            this.platform = platform;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getContentText() {
            return contentText;
        }

        public void setContentText(String contentText) {
            this.contentText = contentText;
        }

        public int getContentType() {
            return contentType;
        }

        public void setContentType(int contentType) {
            this.contentType = contentType;
        }

        public long getContentId() {
            return contentId;
        }

        public void setContentId(long contentId) {
            this.contentId = contentId;
        }

        protected Push(Parcel in) {
            createAt = in.readLong();
            userId = in.readLong();
            platform = in.readString();
            title = in.readString();
            contentText = in.readString();
            contentType = in.readInt();
            contentId = in.readLong();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeLong(createAt);
            dest.writeLong(userId);
            dest.writeString(platform);
            dest.writeString(title);
            dest.writeString(contentText);
            dest.writeInt(contentType);
            dest.writeLong(contentId);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<Push> CREATOR = new Creator<Push>() {
            @Override
            public Push createFromParcel(Parcel in) {
                return new Push(in);
            }

            @Override
            public Push[] newArray(int size) {
                return new Push[size];
            }
        };
    }
}
