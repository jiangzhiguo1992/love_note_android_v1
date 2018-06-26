package com.jiangzg.mianmian.helper;

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
import com.jiangzg.mianmian.R;
import com.jiangzg.mianmian.activity.HomeActivity;
import com.jiangzg.mianmian.activity.user.UserInfoActivity;
import com.jiangzg.mianmian.base.BaseActivity;
import com.jiangzg.mianmian.base.MyApp;
import com.jiangzg.mianmian.broadcast.OssRefreshReceiver;
import com.jiangzg.mianmian.domain.Album;
import com.jiangzg.mianmian.domain.CommonConst;
import com.jiangzg.mianmian.domain.Couple;
import com.jiangzg.mianmian.domain.Entry;
import com.jiangzg.mianmian.domain.Limit;
import com.jiangzg.mianmian.domain.OssInfo;
import com.jiangzg.mianmian.domain.Picture;
import com.jiangzg.mianmian.domain.Place;
import com.jiangzg.mianmian.domain.Result;
import com.jiangzg.mianmian.domain.Sms;
import com.jiangzg.mianmian.domain.Suggest;
import com.jiangzg.mianmian.domain.SuggestComment;
import com.jiangzg.mianmian.domain.User;
import com.jiangzg.mianmian.domain.Version;
import com.jiangzg.mianmian.domain.VipLimit;
import com.jiangzg.mianmian.domain.WallPaper;
import com.jiangzg.mianmian.domain.Whisper;
import com.jiangzg.mianmian.domain.Word;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

/**
 * Created by JZG on 2018/3/27.
 * api辅助类
 */
public class ApiHelper {

    // cp修改类型
    public static final int COUPLE_UPDATE_GOOD = 1; // 更好
    public static final int COUPLE_UPDATE_BAD = 2; // 更坏
    public static final int COUPLE_UPDATE_INFO = 3;// 信息
    // 用户登录类型
    public static final int LOG_PWD = 1;
    public static final int LOG_VER = 2;
    // 用户修改类型
    public static final int MODIFY_FORGET = 1;
    public static final int MODIFY_PASSWORD = 2;
    public static final int MODIFY_PHONE = 3;
    public static final int MODIFY_INFO = 4;
    // list类型 type要和show相对应
    public static final int LIST_CP = 0;
    public static final int LIST_MY = 1;
    public static final int LIST_TA = 2;
    public static final String[] LIST_SHOW = new String[]{
            MyApp.get().getString(R.string.we_de),
            MyApp.get().getString(R.string.me_de),
            MyApp.get().getString(R.string.ta_de)
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
                if (code == Result.RESULT_CODE_NO_USER_INFO) {
                    UserInfoActivity.goActivity(mActivity);
                } else {
                    onEntryFinish(mActivity, code, data);
                }
            }

            @Override
            public void onFailure(String errMsg) {
            }
        });
    }

    public static void onEntryFinish(Activity mActivity, int code, Result.Data data) {
        onEntryFinish(0, 0, mActivity, code, data);
    }

    public static void onEntryFinish(long startTime, long totalWait, final Activity mActivity, int code, Result.Data data) {
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
        // 日志上传
        OssHelper.uploadLog();
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

    public static User getUserRegisterBody(String phone, String pwd, String validateCode) {
        User user = new User();
        user.setPhone(phone);
        String md5Pwd = EncryptUtils.encryptMD5ToString(pwd);
        user.setPassword(md5Pwd);
        user.setValidateCode(validateCode);
        return user;
    }

    public static User getUserForgetBody(String phone, String pwd, String validateCode) {
        User user = new User();
        user.setPhone(phone);
        String md5Pwd = EncryptUtils.encryptMD5ToString(pwd);
        user.setPassword(md5Pwd);
        user.setValidateCode(validateCode);
        user.setType(MODIFY_FORGET);
        return user;
    }

    public static User getUserLoginBody(String phone, String pwd, String validateCode, int type) {
        User user = new User();
        user.setPhone(phone);
        String md5Pwd = EncryptUtils.encryptMD5ToString(pwd);
        user.setPassword(md5Pwd);
        user.setValidateCode(validateCode);
        user.setType(type);
        return user;
    }

    public static User getUserPasswordBody(String oldPwd, String newPwd) {
        User user = new User();
        String md5OldPwd = EncryptUtils.encryptMD5ToString(oldPwd);
        user.setOldPassWord(md5OldPwd);
        String md5NewPwd = EncryptUtils.encryptMD5ToString(newPwd);
        user.setPassword(md5NewPwd);
        user.setType(MODIFY_PASSWORD);
        return user;
    }

    public static User getUserPhoneBody(String phone, String code) {
        User user = new User();
        user.setPhone(phone);
        user.setValidateCode(code);
        user.setType(MODIFY_PHONE);
        return user;
    }

    public static User getUserInfoBody(int sex, long birthday) {
        User user = new User();
        user.setSex(sex);
        user.setBirthday(birthday);
        user.setType(MODIFY_INFO);
        return user;
    }

    public static User getCoupleUpdate2GoodBody(long cid) {
        User user = new User();
        user.setType(COUPLE_UPDATE_GOOD);
        Couple couple = new Couple();
        couple.setId(cid);
        user.setCouple(couple);
        return user;
    }

    public static User getCoupleUpdate2BadBody(long cid) {
        User user = new User();
        user.setType(COUPLE_UPDATE_BAD);
        Couple couple = new Couple();
        couple.setId(cid);
        user.setCouple(couple);
        return user;
    }

    public static User getCoupleUpdateInfo(String avatar, String name) {
        User user = SPHelper.getUser();
        user.setType(COUPLE_UPDATE_INFO);
        Couple couple = user.getCouple();
        if (couple.getCreatorId() == user.getId()) {
            couple.setInviteeAvatar(avatar);
            couple.setInviteeName(name);
        } else {
            couple.setCreatorAvatar(avatar);
            couple.setCreatorName(name);
        }
        return user;
    }

    public static Suggest getSuggestAddBody(String title, int contentType, String contentText, String contentImg) {
        Suggest suggest = new Suggest();
        suggest.setTitle(title);
        suggest.setContentType(contentType);
        suggest.setContentText(contentText);
        suggest.setContentImg(contentImg);
        return suggest;
    }

    public static SuggestComment getSuggestCommentAddBody(long suggestId, String content) {
        SuggestComment suggestComment = new SuggestComment();
        suggestComment.setSuggestId(suggestId);
        suggestComment.setContentText(content);
        return suggestComment;
    }

    public static WallPaper getWallPaperUpdateBody(List<String> imgList) {
        WallPaper wallPaper = new WallPaper();
        wallPaper.setContentImageList(imgList);
        return wallPaper;
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

    public static Album getAlbumBody(String title, String coverOssPath) {
        Album album = new Album();
        album.setTitle(title);
        album.setCover(coverOssPath);
        return album;
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

}
