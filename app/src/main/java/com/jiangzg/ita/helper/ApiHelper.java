package com.jiangzg.ita.helper;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.provider.Settings;

import com.afollestad.materialdialogs.MaterialDialog;
import com.jiangzg.base.application.AppInfo;
import com.jiangzg.base.common.EncryptUtils;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.system.DeviceInfo;
import com.jiangzg.base.time.DateUtils;
import com.jiangzg.ita.activity.HomeActivity;
import com.jiangzg.ita.activity.user.UserInfoActivity;
import com.jiangzg.ita.base.BaseActivity;
import com.jiangzg.ita.base.MyApp;
import com.jiangzg.ita.broadcast.OssReceiver;
import com.jiangzg.ita.domain.Couple;
import com.jiangzg.ita.domain.Entry;
import com.jiangzg.ita.domain.OssInfo;
import com.jiangzg.ita.domain.Result;
import com.jiangzg.ita.domain.Sms;
import com.jiangzg.ita.domain.Suggest;
import com.jiangzg.ita.domain.User;
import com.jiangzg.ita.domain.Version;
import com.jiangzg.ita.domain.VipPower;

import java.util.ArrayList;

import retrofit2.Call;

/**
 * Created by JZG on 2018/3/27.
 * api辅助类
 */
public class ApiHelper {

    // cp修改类型
    public static final int CoupleUpdateGood = 1; // 更好
    public static final int CoupleUpdateBad = 2; // 更坏
    public static final int CoupleUpdateInfo = 3;// 信息
    // 用户登录类型
    public static final int LOG_PWD = 1;
    public static final int LOG_VER = 2;
    // 用户修改类型
    public static final int MODIFY_FORGET = 1;
    public static final int MODIFY_PASSWORD = 2;
    public static final int MODIFY_PHONE = 3;
    public static final int MODIFY_INFO = 4;

    @SuppressLint("HardwareIds")
    public static Entry getEntryBody() {
        Entry entry = new Entry();
        User user = SPHelper.getUser();
        entry.setUserId(user.getId());
        entry.setUserToken(user.getUserToken());
        ContentResolver contentResolver = MyApp.get().getContentResolver();
        String deviceId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID);
        if (StringUtils.isEmpty(deviceId)) {
            deviceId = DeviceInfo.get().getMacAddress();
        }
        entry.setDeviceId(deviceId);
        String manufacturer = DeviceInfo.get().getManufacturer();
        String model = DeviceInfo.get().getModel();
        entry.setDeviceName(manufacturer + " : " + model);
        entry.setPlatform(DeviceInfo.get().getPlatform());
        entry.setOsVersion(DeviceInfo.get().getOsVersion());
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
                if (code == Result.ResultCodeNoUserInfo) {
                    UserInfoActivity.goActivity(mActivity);
                } else {
                    onEntryFinish(mActivity, code, data);
                }
            }

            @Override
            public void onFailure() {
            }
        });
    }

    public static void onEntryFinish(Activity mActivity, int code, Result.Data data) {
        onEntryFinish(0, 0, mActivity, code, data);
    }

    public static void onEntryFinish(long startTime, long totalWait, final Activity mActivity, int code, Result.Data data) {
        // user
        User user = data.getUser();
        SPHelper.setUser(user);
        // version
        final ArrayList<Version> versionList = (ArrayList<Version>) data.getVersionList();
        // oss
        OssInfo ossInfo = data.getOssInfo();
        SPHelper.setOssInfo(ossInfo);
        OssHelper.refreshOssClient();
        OssReceiver.startAlarm();
        // todo notice
        int noticeNoRead = data.getNoticeNoRead();
        // todo vip
        VipPower vipPower = data.getVipPower();

        long endTime = DateUtils.getCurrentLong();
        long between = endTime - startTime;
        if (between >= totalWait) {
            // 间隔时间太大
            HomeActivity.goActivity(mActivity, versionList);
        } else {
            // 间隔时间太小
            MyApp.get().getHandler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    HomeActivity.goActivity(mActivity, versionList);
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
        user.setType(CoupleUpdateGood);
        Couple couple = new Couple();
        couple.setId(cid);
        user.setCouple(couple);
        return user;
    }

    public static User getCoupleUpdate2BadBody(long cid) {
        User user = new User();
        user.setType(CoupleUpdateBad);
        Couple couple = new Couple();
        couple.setId(cid);
        user.setCouple(couple);
        return user;
    }

    public static User getCoupleUpdateInfo(String avatar, String name) {
        User user = SPHelper.getUser();
        user.setType(CoupleUpdateInfo);
        Couple couple = user.getCouple();
        boolean isCreator = user.isCoupleCreator();
        if (isCreator) {
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

}
