package com.jiangzg.lovenote.helper.common;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.provider.Settings;

import com.jiangzg.base.application.AppInfo;
import com.jiangzg.base.application.AppUtils;
import com.jiangzg.base.common.DateUtils;
import com.jiangzg.base.common.EncryptUtils;
import com.jiangzg.base.common.FileUtils;
import com.jiangzg.base.common.LogUtils;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.common.TimeUnit;
import com.jiangzg.base.system.DeviceInfo;
import com.jiangzg.base.system.LanguageUtils;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.controller.activity.base.BaseActivity;
import com.jiangzg.lovenote.controller.activity.main.HomeActivity;
import com.jiangzg.lovenote.controller.receiver.OssRefreshReceiver;
import com.jiangzg.lovenote.helper.system.PushHelper;
import com.jiangzg.lovenote.helper.system.RetrofitHelper;
import com.jiangzg.lovenote.main.MyApp;
import com.jiangzg.lovenote.model.api.API;
import com.jiangzg.lovenote.model.api.Result;
import com.jiangzg.lovenote.model.entity.Entry;
import com.jiangzg.lovenote.model.entity.Lock;
import com.jiangzg.lovenote.model.entity.PostComment;
import com.jiangzg.lovenote.model.entity.Sms;
import com.jiangzg.lovenote.model.entity.User;
import com.jiangzg.lovenote.model.entity.Version;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

/**
 * Created by JZG on 2018/3/27.
 * api辅助类
 */
public class ApiHelper {

    // user登录类型
    public static final int LOG_PWD = 1;
    public static final int LOG_VER = 2;
    // user修改类型
    public static final int MODIFY_FORGET = 11;
    public static final int MODIFY_PASSWORD = 12;
    public static final int MODIFY_PHONE = 13;
    public static final int MODIFY_INFO = 14;
    // cp修改类型
    public static final int COUPLE_UPDATE_GOOD = 1;
    public static final int COUPLE_UPDATE_BAD = 2;
    public static final int COUPLE_UPDATE_INFO = 3;
    // comment order类型
    private static final int LIST_COMMENT_ORDER_POINT = 0;
    private static final int LIST_COMMENT_ORDER_TIME = 1;
    public static final int[] LIST_COMMENT_ORDER_TYPE = new int[]{
            LIST_COMMENT_ORDER_POINT,
            LIST_COMMENT_ORDER_TIME
    };
    public static final String[] LIST_COMMENT_ORDER_SHOW = new String[]{
            MyApp.get().getString(R.string.point),
            MyApp.get().getString(R.string.time)
    };
    // note search类型
    private static final int LIST_NOTE_WHO_CP = 0;
    public static final int LIST_NOTE_WHO_MY = 1;
    public static final int LIST_NOTE_WHO_TA = 2;
    public static final int[] LIST_NOTE_WHO_TYPE = new int[]{
            LIST_NOTE_WHO_CP,
            LIST_NOTE_WHO_MY,
            LIST_NOTE_WHO_TA
    };
    public static final String[] LIST_NOTE_WHO_SHOW = new String[]{
            MyApp.get().getString(R.string.we_de),
            MyApp.get().getString(R.string.me_de),
            MyApp.get().getString(R.string.ta_de)
    };
    // topic search类型
    public static final int LIST_TOPIC_TYPE_ALL = 0;
    public static final int LIST_TOPIC_TYPE_OFFICIAL = 1;
    public static final int LIST_TOPIC_TYPE_WELL = 2;
    public static final int[] LIST_TOPIC_TYPE_TYPE = new int[]{
            LIST_TOPIC_TYPE_ALL,
            LIST_TOPIC_TYPE_OFFICIAL,
            LIST_TOPIC_TYPE_WELL
    };
    public static final String[] LIST_TOPIC_TYPE_SHOW = new String[]{
            MyApp.get().getString(R.string.all),
            MyApp.get().getString(R.string.official),
            MyApp.get().getString(R.string.well)
    };
    // match search类型
    private static final int LIST_MATCH_ORDER_COIN = 0;
    private static final int LIST_MATCH_ORDER_POINT = 1;
    private static final int LIST_MATCH_ORDER_NEW = 2;
    public static final int[] LIST_MATCH_ORDER_TYPE = new int[]{
            LIST_MATCH_ORDER_COIN,
            LIST_MATCH_ORDER_POINT,
            LIST_MATCH_ORDER_NEW
    };
    public static final String[] LIST_MATCH_ORDER_SHOW = new String[]{
            MyApp.get().getString(R.string.coin_board),
            MyApp.get().getString(R.string.point_board),
            MyApp.get().getString(R.string.new_board)
    };

    @SuppressLint("HardwareIds")
    public static Entry getEntryBody() {
        Entry entry = new Entry();
        // deviceId 最好是不需要权限就能获取
        ContentResolver contentResolver = MyApp.get().getContentResolver();
        String deviceId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID);
        if (StringUtils.isEmpty(deviceId)) {
            deviceId = DeviceInfo.get().getMacAddress();
        }
        entry.setDeviceId(deviceId);
        // 手机介绍
        String manufacturer = DeviceInfo.get().getManufacturer();
        String model = DeviceInfo.get().getModel();
        entry.setDeviceName(manufacturer + " : " + model);
        // 下载渠道
        String marketChannel = AppUtils.getAppMetaDataString(MyApp.get(), "market_channel");
        entry.setMarket(marketChannel);
        // 语言
        entry.setLanguage(LanguageUtils.getLocale().getLanguage());
        // 手机平台
        entry.setPlatform("android");
        // 手机版本
        entry.setOsVersion(DeviceInfo.get().getOsVersion());
        // 软件版本
        entry.setAppVersion(AppInfo.get().getVersionCode());
        return entry;
    }

    public static Sms getSmsLoginBody(String phone) {
        Sms sms = new Sms();
        sms.setSendType(Sms.TYPE_LOGIN);
        sms.setPhone(phone);
        return sms;
    }

    public static Sms getSmsRegisterBody(String phone) {
        Sms sms = new Sms();
        sms.setSendType(Sms.TYPE_REGISTER);
        sms.setPhone(phone);
        return sms;
    }

    public static Sms getSmsForgetBody(String phone) {
        Sms sms = new Sms();
        sms.setSendType(Sms.TYPE_FORGET);
        sms.setPhone(phone);
        return sms;
    }

    public static Sms getSmsPhoneBody(String phone) {
        Sms sms = new Sms();
        sms.setSendType(Sms.TYPE_PHONE);
        sms.setPhone(phone);
        return sms;
    }

    public static Sms getSmsLockBody(String taPhone) {
        Sms sms = new Sms();
        sms.setSendType(Sms.TYPE_LOCK);
        sms.setPhone(taPhone);
        return sms;
    }

    public static User getUserBody(String phone, String pwd) {
        User user = new User();
        if (!StringUtils.isEmpty(phone)) {
            user.setPhone(phone);
        }
        if (!StringUtils.isEmpty(pwd)) {
            String md5Pwd = EncryptUtils.encryptMD5ToString(pwd);
            user.setPassword(md5Pwd);
        }
        return user;
    }

    public static Lock getLockBody(String pwd) {
        Lock lock = new Lock();
        if (!StringUtils.isEmpty(pwd)) {
            String md5Pwd = EncryptUtils.encryptMD5ToString(pwd);
            lock.setPassword(md5Pwd);
        }
        return lock;
    }

    public static PostComment getPostCommentTextBody(long pid, long tcid, String content) {
        PostComment postComment = new PostComment();
        postComment.setPostId(pid);
        postComment.setToCommentId(tcid);
        postComment.setKind(PostComment.KIND_TEXT);
        postComment.setContentText(content);
        return postComment;
    }

    public static PostComment getPostCommentJabBody(long pid, long tcid) {
        PostComment postComment = new PostComment();
        postComment.setPostId(pid);
        postComment.setToCommentId(tcid);
        postComment.setKind(PostComment.KIND_JAB);
        postComment.setContentText("");
        return postComment;
    }

    public static void postEntry(final BaseActivity mActivity) {
        long start = DateUtils.getCurrentLong();
        Entry entry = getEntryBody();
        // api
        Call<Result> api = new RetrofitHelper().call(API.class).entryPush(entry);
        RetrofitHelper.enqueue(api, mActivity.getLoading(true), new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                onEntryFinish(start, 0, mActivity, code, data);
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
            }
        });
        mActivity.pushApi(api);
    }

    public static void onEntryFinish(long startTime, long totalWait, final Activity mActivity, int code, Result.Data data) {
        if (data == null) return;
        // me
        SPHelper.setMe(data.getUser());
        // commonConst
        SPHelper.setCommonConst(data.getCommonConst());
        // modelShow
        SPHelper.setModelShow(data.getModelShow());
        // cooperation
        SPHelper.setCooperation(data.getCooperation());
        // limit
        SPHelper.setLimit(data.getLimit());
        // vipLimit
        SPHelper.setVipLimit(data.getVipLimit());
        // ossInfo
        SPHelper.setOssInfo(data.getOssInfo());
        OssHelper.refreshOssClient();
        OssRefreshReceiver.startAlarm();
        // pushInfo
        SPHelper.setPushInfo(data.getPushInfo());
        PushHelper.checkAccountBind();
        PushHelper.checkTagBind();
        PushHelper.checkDisturb();
        // adInfo
        SPHelper.setAdInfo(data.getAdInfo());
        // commonCount
        SPHelper.setCommonCount(data.getCommonCount());
        // version
        final ArrayList<Version> versionList = (ArrayList<Version>) data.getVersionList();
        if (versionList == null || versionList.size() <= 0) {
            SPHelper.clearVersion();
            // 删除旧apk
            File apkDir = ResHelper.newApkDir();
            List<File> fileList = FileUtils.listFilesAndDirInDir(apkDir, true);
            ResHelper.deleteFileListInBackground(fileList);
            // 日志上传(只限最新版本)
            OssHelper.uploadLog();
        } else {
            SPHelper.setVersion(versionList.get(0));
            // 删除日志
            LogUtils.clearLog();
        }
        // delay
        long between = DateUtils.getCurrentLong() - startTime;
        if (between >= totalWait) {
            // 间隔时间太大
            HomeActivity.goActivity(mActivity);
        } else {
            // 间隔时间太小
            MyApp.get().getHandler().postDelayed(() -> HomeActivity.goActivity(mActivity), totalWait - between);
        }
    }

    // 更新oss信息
    public static void ossInfoUpdate() {
        if (UserHelper.isEmpty(SPHelper.getMe())) return;
        Call<Result> call = new RetrofitHelper().call(API.class).ossGet();
        RetrofitHelper.enqueue(call, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                LogUtils.i(ApiHelper.class, "ossInfoUpdate", "oss更新成功");
                // 刷新ossInfo
                SPHelper.setOssInfo(data.getOssInfo());
                // 刷新ossClient
                OssHelper.refreshOssClient();
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                MyApp.get().getHandler().postDelayed(() -> {
                    LogUtils.w(ApiHelper.class, "ossInfoUpdate", "oss更新失败");
                    ossInfoUpdate(); // 重复更新
                }, TimeUnit.MIN);
            }
        });
    }

}
