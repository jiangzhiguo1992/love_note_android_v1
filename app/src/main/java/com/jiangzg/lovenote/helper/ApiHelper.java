package com.jiangzg.lovenote.helper;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.os.Bundle;
import android.provider.Settings;

import com.afollestad.materialdialogs.MaterialDialog;
import com.jiangzg.base.application.AppInfo;
import com.jiangzg.base.application.AppUtils;
import com.jiangzg.base.common.EncryptUtils;
import com.jiangzg.base.common.FileUtils;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.system.DeviceInfo;
import com.jiangzg.base.system.LocationInfo;
import com.jiangzg.base.time.DateUtils;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.activity.HomeActivity;
import com.jiangzg.lovenote.base.BaseActivity;
import com.jiangzg.lovenote.base.MyApp;
import com.jiangzg.lovenote.broadcast.OssRefreshReceiver;
import com.jiangzg.lovenote.domain.CommonConst;
import com.jiangzg.lovenote.domain.Couple;
import com.jiangzg.lovenote.domain.Entry;
import com.jiangzg.lovenote.domain.Limit;
import com.jiangzg.lovenote.domain.Lock;
import com.jiangzg.lovenote.domain.OssInfo;
import com.jiangzg.lovenote.domain.Picture;
import com.jiangzg.lovenote.domain.Place;
import com.jiangzg.lovenote.domain.PostComment;
import com.jiangzg.lovenote.domain.PromiseBreak;
import com.jiangzg.lovenote.domain.Result;
import com.jiangzg.lovenote.domain.Sms;
import com.jiangzg.lovenote.domain.Suggest;
import com.jiangzg.lovenote.domain.SuggestComment;
import com.jiangzg.lovenote.domain.User;
import com.jiangzg.lovenote.domain.Version;
import com.jiangzg.lovenote.domain.VipLimit;
import com.jiangzg.lovenote.domain.WallPaper;
import com.jiangzg.lovenote.domain.Whisper;
import com.jiangzg.lovenote.domain.Word;

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
    public static final int MODIFY_FORGET = 1;
    public static final int MODIFY_PASSWORD = 2;
    public static final int MODIFY_PHONE = 3;
    public static final int MODIFY_INFO = 4;
    // cp修改类型
    public static final int COUPLE_UPDATE_GOOD = 1; // 更好
    public static final int COUPLE_UPDATE_BAD = 2; // 更坏
    public static final int COUPLE_UPDATE_INFO = 3;// 信息
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
    private static final int LIST_NOTE_CP = 0;
    public static final int LIST_NOTE_MY = 1;
    public static final int LIST_NOTE_TA = 2;
    public static final int[] LIST_NOTE_TYPE = new int[]{
            LIST_NOTE_CP,
            LIST_NOTE_MY,
            LIST_NOTE_TA
    };
    public static final String[] LIST_NOTE_SHOW = new String[]{
            MyApp.get().getString(R.string.we_de),
            MyApp.get().getString(R.string.me_de),
            MyApp.get().getString(R.string.ta_de)
    };
    // topic search类型
    public static final int LIST_TOPIC_ALL = 0;
    public static final int LIST_TOPIC_OFFICIAL = 1;
    public static final int LIST_TOPIC_WELL = 2;
    public static final int[] LIST_TOPIC_TYPE = new int[]{
            LIST_TOPIC_ALL,
            LIST_TOPIC_OFFICIAL,
            LIST_TOPIC_WELL
    };
    public static final String[] LIST_TOPIC_SHOW = new String[]{
            MyApp.get().getString(R.string.all),
            MyApp.get().getString(R.string.official),
            MyApp.get().getString(R.string.well)
    };

    public static final int PAY_PLATFORM_ALIPAY = 100;
    public static final int PAY_PLATFORM_WECHAT = 200;

    public static final int VIP_GOODS_1_MONTH = 101;
    public static final int VIP_GOODS_1_YEAR = 201;
    public static final int VIP_GOODS_FOREVER = 520;

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
        Bundle appMetaData = AppUtils.getAppMetaData(MyApp.get());
        if (appMetaData != null) {
            String marketChannel = appMetaData.getString("market_channel");
            entry.setMarket(marketChannel);
        }
        // 手机平台
        entry.setPlatform(DeviceInfo.get().getPlatform());
        // 手机版本
        entry.setOsVersion(DeviceInfo.get().getOsVersion());
        // 软件版本
        entry.setAppVersion(AppInfo.get().getVersionCode());
        return entry;
    }

    public static void postEntry(final BaseActivity mActivity) {
        Entry entry = getEntryBody();
        Call<Result> call = new RetrofitHelper().call(API.class).entryPush(entry);
        MaterialDialog loading = mActivity.getLoading(true);
        RetrofitHelper.enqueue(call, loading, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                onEntryFinish(0, 0, mActivity, code, data);
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
            }
        });
    }

    public static void onEntryFinish(long startTime, long totalWait, final Activity mActivity, int code, Result.Data data) {
        if (data == null) return;
        // user
        User user = data.getUser();
        SPHelper.setMe(user);
        // commonConst
        CommonConst commonConst = data.getCommonConst();
        SPHelper.setCommonConst(commonConst);
        // limit
        Limit limit = data.getLimit();
        SPHelper.setLimit(limit);
        // vipLimit
        VipLimit vipLimit = data.getVipLimit();
        SPHelper.setVipLimit(vipLimit);
        // ossInfo
        OssInfo ossInfo = data.getOssInfo();
        SPHelper.setOssInfo(ossInfo);
        OssHelper.refreshOssClient();
        OssRefreshReceiver.startAlarm();
        // version
        final ArrayList<Version> versionList = (ArrayList<Version>) data.getVersionList();
        if (versionList == null || versionList.size() <= 0) {
            SPHelper.clearVersion();
            File apkDir = ResHelper.newApkDir();
            List<File> fileList = FileUtils.listFilesAndDirInDir(apkDir, true);
            ResHelper.deleteFileListInBackground(fileList);
            // 日志上传(只限最新版本)
            OssHelper.uploadLog();
        } else {
            SPHelper.setVersion(versionList.get(0));
        }
        // noticeNoReadCount
        int noticeNoReadCount = data.getNoticeNoReadCount();
        SPHelper.setNoticeNoReadCount(noticeNoReadCount);
        // delay
        long endTime = DateUtils.getCurrentLong();
        long between = endTime - startTime;
        if (between >= totalWait) {
            // 间隔时间太大
            HomeActivity.goActivity(mActivity);
        } else {
            // 间隔时间太小
            MyApp.get().getHandler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    HomeActivity.goActivity(mActivity);
                }
            }, totalWait - between);
        }
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

    public static Sms getSmsLockBody() {
        Sms sms = new Sms();
        sms.setSendType(Sms.TYPE_LOCK);
        User ta = SPHelper.getTa();
        if (ta != null) {
            sms.setPhone(ta.getPhone());
        }
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

    public static User getUserInfoBody(int sex, long birthday) {
        User user = new User();
        user.setSex(sex);
        user.setBirthday(birthday);
        return user;
    }

    public static Suggest getSuggestAddBody(String title, int kind, String contentText, String contentImg) {
        Suggest suggest = new Suggest();
        suggest.setTitle(title);
        suggest.setKind(kind);
        suggest.setContentText(contentText);
        suggest.setContentImage(contentImg);
        return suggest;
    }

    public static SuggestComment getSuggestCommentAddBody(long suggestId, String content) {
        SuggestComment suggestComment = new SuggestComment();
        suggestComment.setSuggestId(suggestId);
        suggestComment.setContentText(content);
        return suggestComment;
    }

    public static Couple getCoupleUpdateInfo(String avatar, String name) {
        User user = SPHelper.getMe();
        Couple couple = user.getCouple();
        if (couple.getCreatorId() == user.getId()) {
            couple.setInviteeAvatar(avatar);
            couple.setInviteeName(name);
        } else {
            couple.setCreatorAvatar(avatar);
            couple.setCreatorName(name);
        }
        return couple;
    }

    public static WallPaper getWallPaperUpdateBody(List<String> imgList) {
        WallPaper wallPaper = new WallPaper();
        wallPaper.setContentImageList(imgList);
        return wallPaper;
    }

    public static Lock getLockBody(String pwd) {
        Lock lock = new Lock();
        if (!StringUtils.isEmpty(pwd)) {
            String md5Pwd = EncryptUtils.encryptMD5ToString(pwd);
            lock.setPassword(md5Pwd);
        }
        return lock;
    }

    public static Place getPlaceBody(LocationInfo info) {
        if (info == null || (info.getLongitude() == 0 && info.getLatitude() == 0)) {
            info = LocationInfo.getInfo();
        }
        Place place = new Place();
        place.setLongitude(info.getLongitude());
        place.setLatitude(info.getLatitude());
        place.setAddress(info.getAddress());
        place.setCountry(info.getCountry());
        place.setProvince(info.getProvince());
        place.setCity(info.getCity());
        place.setDistrict(info.getDistrict());
        place.setStreet(info.getStreet());
        place.setCityId(info.getCityId());
        return place;
    }

    public static Word getWordBody(String content) {
        Word word = new Word();
        word.setContentText(content);
        return word;
    }

    public static Whisper getWhisperBody(String channel, boolean isImage, String content) {
        Whisper whisper = new Whisper();
        whisper.setChannel(channel);
        whisper.setImage(isImage);
        whisper.setContent(content);
        return whisper;
    }

    public static Picture getPictureBody(long aid, long happenAt, String content, double longitude, double latitude, String address, String cityId) {
        Picture picture = new Picture();
        picture.setAlbumId(aid);
        picture.setHappenAt(happenAt);
        picture.setContentImage(content);
        picture.setLongitude(longitude);
        picture.setLatitude(latitude);
        picture.setAddress(address);
        picture.setCityId(cityId);
        return picture;
    }

    public static PromiseBreak getPromiseBreakBody(long pid, long happenAt, String content) {
        PromiseBreak promiseBreak = new PromiseBreak();
        promiseBreak.setPromiseId(pid);
        promiseBreak.setHappenAt(happenAt);
        promiseBreak.setContentText(content);
        return promiseBreak;
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

}
