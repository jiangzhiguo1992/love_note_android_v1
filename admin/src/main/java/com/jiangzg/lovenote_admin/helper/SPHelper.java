package com.jiangzg.lovenote_admin.helper;

import android.content.SharedPreferences;

import com.jiangzg.base.common.SPUtils;
import com.jiangzg.lovenote_admin.domain.OssInfo;
import com.jiangzg.lovenote_admin.domain.User;

/**
 * Created by Fan on 2017/3/2.
 * Preferences 帮助类
 */
public class SPHelper {

    // 存储集合
    private static final String SHARE_USER = "share_user";
    private static final String SHARE_OSS_INFO = "share_oss_info";

    // common
    private static final String FIELD_USER_ID = "user_id";
    private static final String FIELD_USER_PHONE = "user_phone";
    private static final String FIELD_USER_PWD = "user_pwd";
    private static final String FIELD_USER_TOKEN = "userToken";
    // ossInfo
    private static final String FIELD_OSS_SECURITY_TOKEN = "security_token";
    private static final String FIELD_OSS_KEY_ID = "access_key_id";
    private static final String FIELD_OSS_KEY_SECRET = "access_key_secret";
    private static final String FIELD_OSS_REGION = "region";
    private static final String FIELD_OSS_DOMAIN = "domain";
    private static final String FIELD_OSS_BUCKET = "bucket";
    private static final String FIELD_OSS_STS_EXPIRE_TIME = "sts_expire_time";
    private static final String FIELD_OSS_OSS_REFRESH_SEC = "oss_refresh_sec";
    private static final String FIELD_OSS_URL_EXPIRE_SEC = "url_expire_sec";
    private static final String FIELD_OSS_PATH_VERSION = "path_version";
    private static final String FIELD_OSS_PATH_NOTICE = "path_notice";
    private static final String FIELD_OSS_PATH_BROADCAST = "path_broadcast";

    /**
     * ***********************************User***********************************
     */
    public static void setUser(User user) {
        if (user == null) return;
        SharedPreferences.Editor editor = SPUtils.getSharedPreferences(SHARE_USER).edit();
        editor.putLong(FIELD_USER_ID, user.getId());
        editor.putString(FIELD_USER_PHONE, user.getPhone());
        editor.putString(FIELD_USER_PWD, user.getPassword());
        editor.putString(FIELD_USER_TOKEN, user.getUserToken());
        editor.apply();
    }

    public static User getUser() {
        SharedPreferences sp = SPUtils.getSharedPreferences(SHARE_USER);
        User user = new User();
        user.setId(sp.getLong(FIELD_USER_ID, 0));
        user.setPhone(sp.getString(FIELD_USER_PHONE, ""));
        user.setPassword(sp.getString(FIELD_USER_PWD, ""));
        user.setUserToken(sp.getString(FIELD_USER_TOKEN, ""));
        return user;
    }

    /**
     * ***********************************OssInfo***********************************
     */
    public static void setOssInfo(OssInfo ossInfo) {
        if (ossInfo == null) return;
        SharedPreferences.Editor editor = SPUtils.getSharedPreferences(SHARE_OSS_INFO).edit();
        editor.putString(FIELD_OSS_SECURITY_TOKEN, ossInfo.getSecurityToken());
        editor.putString(FIELD_OSS_KEY_ID, ossInfo.getAccessKeyId());
        editor.putString(FIELD_OSS_KEY_SECRET, ossInfo.getAccessKeySecret());
        editor.putString(FIELD_OSS_REGION, ossInfo.getRegion());
        editor.putString(FIELD_OSS_DOMAIN, ossInfo.getDomain());
        editor.putString(FIELD_OSS_BUCKET, ossInfo.getBucket());
        editor.putLong(FIELD_OSS_STS_EXPIRE_TIME, ossInfo.getStsExpireTime());
        editor.putLong(FIELD_OSS_OSS_REFRESH_SEC, ossInfo.getOssRefreshSec());
        editor.putLong(FIELD_OSS_URL_EXPIRE_SEC, ossInfo.getUrlExpireSec());
        editor.putString(FIELD_OSS_PATH_VERSION, ossInfo.getPathVersion());
        editor.putString(FIELD_OSS_PATH_NOTICE, ossInfo.getPathNotice());
        editor.putString(FIELD_OSS_PATH_BROADCAST, ossInfo.getPathBroadcast());
        editor.apply();
    }

    public static OssInfo getOssInfo() {
        SharedPreferences sp = SPUtils.getSharedPreferences(SHARE_OSS_INFO);
        OssInfo ossInfo = new OssInfo();
        ossInfo.setSecurityToken(sp.getString(FIELD_OSS_SECURITY_TOKEN, ""));
        ossInfo.setAccessKeyId(sp.getString(FIELD_OSS_KEY_ID, ""));
        ossInfo.setAccessKeySecret(sp.getString(FIELD_OSS_KEY_SECRET, ""));
        ossInfo.setRegion(sp.getString(FIELD_OSS_REGION, ""));
        ossInfo.setDomain(sp.getString(FIELD_OSS_DOMAIN, ""));
        ossInfo.setBucket(sp.getString(FIELD_OSS_BUCKET, ""));
        ossInfo.setStsExpireTime(sp.getLong(FIELD_OSS_STS_EXPIRE_TIME, 0));
        ossInfo.setOssRefreshSec(sp.getLong(FIELD_OSS_OSS_REFRESH_SEC, 60 * 30));
        ossInfo.setUrlExpireSec(sp.getLong(FIELD_OSS_URL_EXPIRE_SEC, 60 * 10));
        ossInfo.setPathVersion(sp.getString(FIELD_OSS_PATH_VERSION, ""));
        ossInfo.setPathNotice(sp.getString(FIELD_OSS_PATH_NOTICE, ""));
        ossInfo.setPathBroadcast(sp.getString(FIELD_OSS_PATH_BROADCAST, ""));
        return ossInfo;
    }

}
