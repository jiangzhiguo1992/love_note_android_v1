package com.jiangzg.ita.helper;

import android.content.SharedPreferences;

import com.jiangzg.base.common.LogUtils;
import com.jiangzg.base.system.SPUtils;
import com.jiangzg.ita.domain.Couple;
import com.jiangzg.ita.domain.OssInfo;
import com.jiangzg.ita.domain.User;

/**
 * Created by Fan on 2017/3/2.
 * Preferences 帮助类
 */
public class SPHelper {

    private static final String LOG_TAG = "SPHelper";

    private static final String SHARE_USER = "shareUser";
    private static final String SHARE_COUPLE = "shareCouple";
    private static final String SHARE_OSS_INFO = "shareOssInfo";
    private static final String SHARE_SETTINGS = "shareSettings";

    /* 储存字段 */
    // user
    private static final String FIELD_USER_ID = "id";
    private static final String FIELD_USER_PHONE = "phone";
    private static final String FIELD_USER_SEX = "sex";
    private static final String FIELD_USER_BIRTHDAY = "birthday";
    private static final String FIELD_USER_TOKEN = "userToken";
    // couple
    private static final String FIELD_CP_ID = "coupleId";
    private static final String FIELD_CP_STAUTS = "status";
    private static final String FIELD_CP_UPDATE_AT = "updateAt";
    private static final String FIELD_CP_CREATOR_ID = "creatorId";
    private static final String FIELD_CP_CREATOR_NAME = "creatorName";
    private static final String FIELD_CP_CREATOR_AVATAR = "creatorAvatar";
    private static final String FIELD_CP_INVITEE_ID = "inviteeId";
    private static final String FIELD_CP_INVITEE_NAME = "inviteeName";
    private static final String FIELD_CP_INVITEE_AVATAR = "inviteeAvatar";
    // ossInfo
    private static final String FIELD_OSS_SECURITY_TOKEN = "securityToken";
    private static final String FIELD_OSS_KEY_ID = "accessKeyId";
    private static final String FIELD_OSS_KEY_SECRET = "accessKeySecret";
    private static final String FIELD_OSS_REGION = "region";
    private static final String FIELD_OSS_ENDPOINT = "endpoint";
    private static final String FIELD_OSS_DOMAIN = "domain";
    private static final String FIELD_OSS_BUCKET = "bucket";
    private static final String FIELD_OSS_EXPIRATION = "expiration";
    private static final String FIELD_OSS_INTERVAL = "interval";
    private static final String FIELD_OSS_PATH_VERSION = "pathVersion";
    private static final String FIELD_OSS_PATH_SUGGEST = "pathSuggest";
    private static final String FIELD_OSS_PATH_COUPLE_AVATAR = "pathCoupleAvatar";
    private static final String FIELD_OSS_PATH_COUPLE_WALL = "pathCoupleWall";
    private static final String FIELD_OSS_PATH_BOOK_ALBUM = "pathBookAlbum";
    private static final String FIELD_OSS_PATH_BOOK_PICTURE = "pathBookPicture";
    private static final String FIELD_OSS_PATH_BOOK_DIARY = "pathBookDiary";
    private static final String FIELD_OSS_PATH_BOOK_GIFT = "pathBookGift";
    private static final String FIELD_OSS_PATH_BOOK_AUDIO = "pathBookAudio";
    private static final String FIELD_OSS_PATH_BOOK_VIDEO = "pathBookVideo";
    private static final String FIELD_OSS_PATH_BOOK_THUMB = "pathBookThumb";

    // settings
    private static final String FIELD_SET_THEME = "theme";
    private static final String FIELD_SET_NOTICE_SYSTEM = "system";
    private static final String FIELD_SET_NOTICE_SOCIAL = "social";

    public static void setUser(User user) {
        clearUser();
        clearCouple();
        if (user == null) {
            LogUtils.w(LOG_TAG, "user == null");
            return;
        } else {
            LogUtils.d(LOG_TAG, "setUser: " + GsonUtils.get().toJson(user));
        }
        SharedPreferences.Editor editor = SPUtils.getSharedPreferences(SHARE_USER).edit();
        editor.putLong(FIELD_USER_ID, user.getId());
        editor.putString(FIELD_USER_PHONE, user.getPhone());
        editor.putInt(FIELD_USER_SEX, user.getSex());
        editor.putLong(FIELD_USER_BIRTHDAY, user.getBirthday());
        editor.putString(FIELD_USER_TOKEN, user.getUserToken());
        editor.apply();
        // 存cp
        Couple couple = user.getCouple();
        setCouple(couple);
    }

    public static User getUser() {
        SharedPreferences preUser = SPUtils.getSharedPreferences(SHARE_USER);
        User user = new User();
        user.setId(preUser.getLong(FIELD_USER_ID, 0));
        user.setPhone(preUser.getString(FIELD_USER_PHONE, ""));
        user.setSex(preUser.getInt(FIELD_USER_SEX, 0));
        user.setBirthday(preUser.getLong(FIELD_USER_BIRTHDAY, 0));
        user.setUserToken(preUser.getString(FIELD_USER_TOKEN, ""));
        // 取cp
        Couple couple = getCouple();
        user.setCouple(couple);
        return user;
    }

    public static void setCouple(Couple couple) {
        clearCouple();
        if (CheckHelper.isNullCouple(couple)) {
            LogUtils.w(LOG_TAG, "couple == null");
            return;
        } else {
            LogUtils.d(LOG_TAG, "setCouple: " + GsonUtils.get().toJson(couple));
        }
        SharedPreferences.Editor editor = SPUtils.getSharedPreferences(SHARE_COUPLE).edit();
        editor.putLong(FIELD_CP_ID, couple.getId());
        editor.putInt(FIELD_CP_STAUTS, couple.getStatus());
        editor.putLong(FIELD_CP_UPDATE_AT, couple.getUpdateAt());
        editor.putLong(FIELD_CP_CREATOR_ID, couple.getCreatorId());
        editor.putString(FIELD_CP_CREATOR_NAME, couple.getCreatorName());
        editor.putString(FIELD_CP_CREATOR_AVATAR, couple.getCreatorAvatar());
        editor.putLong(FIELD_CP_INVITEE_ID, couple.getInviteeId());
        editor.putString(FIELD_CP_INVITEE_NAME, couple.getInviteeName());
        editor.putString(FIELD_CP_INVITEE_AVATAR, couple.getInviteeAvatar());
        editor.apply();
    }

    public static Couple getCouple() {
        SharedPreferences preCouple = SPUtils.getSharedPreferences(SHARE_COUPLE);
        Couple couple = new Couple();
        couple.setId(preCouple.getLong(FIELD_CP_ID, 0));
        couple.setStatus(preCouple.getInt(FIELD_CP_STAUTS, Couple.CoupleStatusTogether));
        couple.setUpdateAt(preCouple.getLong(FIELD_CP_UPDATE_AT, 0));
        couple.setCreatorId(preCouple.getLong(FIELD_CP_CREATOR_ID, 0));
        couple.setCreatorName(preCouple.getString(FIELD_CP_CREATOR_NAME, ""));
        couple.setCreatorAvatar(preCouple.getString(FIELD_CP_CREATOR_AVATAR, ""));
        couple.setInviteeId(preCouple.getLong(FIELD_CP_INVITEE_ID, 0));
        couple.setInviteeName(preCouple.getString(FIELD_CP_INVITEE_NAME, ""));
        couple.setInviteeAvatar(preCouple.getString(FIELD_CP_INVITEE_AVATAR, ""));
        return couple;
    }

    public static void setOssInfo(OssInfo ossInfo) {
        clearOssInfo();
        if (ossInfo == null) {
            LogUtils.w(LOG_TAG, "ossInfo == null");
            return;
        } else {
            LogUtils.d(LOG_TAG, "setOssInfo: " + GsonUtils.get().toJson(ossInfo));
        }
        SharedPreferences.Editor editor = SPUtils.getSharedPreferences(SHARE_OSS_INFO).edit();
        editor.putString(FIELD_OSS_SECURITY_TOKEN, ossInfo.getSecurityToken());
        editor.putString(FIELD_OSS_KEY_ID, ossInfo.getAccessKeyId());
        editor.putString(FIELD_OSS_KEY_SECRET, ossInfo.getAccessKeySecret());
        editor.putString(FIELD_OSS_REGION, ossInfo.getRegion());
        editor.putString(FIELD_OSS_ENDPOINT, ossInfo.getEndpoint());
        editor.putString(FIELD_OSS_DOMAIN, ossInfo.getDomain());
        editor.putString(FIELD_OSS_BUCKET, ossInfo.getBucket());
        editor.putLong(FIELD_OSS_EXPIRATION, ossInfo.getExpiration());
        editor.putLong(FIELD_OSS_INTERVAL, ossInfo.getInterval());
        editor.putString(FIELD_OSS_PATH_VERSION, ossInfo.getPathVersion());
        editor.putString(FIELD_OSS_PATH_SUGGEST, ossInfo.getPathSuggest());
        editor.putString(FIELD_OSS_PATH_COUPLE_AVATAR, ossInfo.getPathCoupleAvatar());
        editor.putString(FIELD_OSS_PATH_COUPLE_WALL, ossInfo.getPathCoupleWall());
        editor.putString(FIELD_OSS_PATH_BOOK_ALBUM, ossInfo.getPathBookAlbum());
        editor.putString(FIELD_OSS_PATH_BOOK_PICTURE, ossInfo.getPathBookPicture());
        editor.putString(FIELD_OSS_PATH_BOOK_DIARY, ossInfo.getPathBookDiary());
        editor.putString(FIELD_OSS_PATH_BOOK_GIFT, ossInfo.getPathBookGift());
        editor.putString(FIELD_OSS_PATH_BOOK_AUDIO, ossInfo.getPathBookAudio());
        editor.putString(FIELD_OSS_PATH_BOOK_VIDEO, ossInfo.getPathBookVideo());
        editor.putString(FIELD_OSS_PATH_BOOK_THUMB, ossInfo.getPathBookThumb());
        editor.apply();
    }

    public static OssInfo getOssInfo() {
        SharedPreferences preOss = SPUtils.getSharedPreferences(SHARE_OSS_INFO);
        OssInfo ossInfo = new OssInfo();
        ossInfo.setSecurityToken(preOss.getString(FIELD_OSS_SECURITY_TOKEN, ""));
        ossInfo.setAccessKeyId(preOss.getString(FIELD_OSS_KEY_ID, ""));
        ossInfo.setAccessKeySecret(preOss.getString(FIELD_OSS_KEY_SECRET, ""));
        ossInfo.setRegion(preOss.getString(FIELD_OSS_REGION, ""));
        ossInfo.setEndpoint(preOss.getString(FIELD_OSS_ENDPOINT, ""));
        ossInfo.setDomain(preOss.getString(FIELD_OSS_DOMAIN, ""));
        ossInfo.setBucket(preOss.getString(FIELD_OSS_BUCKET, ""));
        ossInfo.setExpiration(preOss.getLong(FIELD_OSS_EXPIRATION, 0));
        ossInfo.setInterval(preOss.getLong(FIELD_OSS_INTERVAL, 0));
        ossInfo.setPathVersion(preOss.getString(FIELD_OSS_PATH_VERSION, ""));
        ossInfo.setPathSuggest(preOss.getString(FIELD_OSS_PATH_SUGGEST, ""));
        ossInfo.setPathCoupleAvatar(preOss.getString(FIELD_OSS_PATH_COUPLE_AVATAR, ""));
        ossInfo.setPathCoupleWall(preOss.getString(FIELD_OSS_PATH_COUPLE_WALL, ""));
        ossInfo.setPathBookAlbum(preOss.getString(FIELD_OSS_PATH_BOOK_ALBUM, ""));
        ossInfo.setPathBookPicture(preOss.getString(FIELD_OSS_PATH_BOOK_PICTURE, ""));
        ossInfo.setPathBookDiary(preOss.getString(FIELD_OSS_PATH_BOOK_DIARY, ""));
        ossInfo.setPathBookGift(preOss.getString(FIELD_OSS_PATH_BOOK_GIFT, ""));
        ossInfo.setPathBookAudio(preOss.getString(FIELD_OSS_PATH_BOOK_AUDIO, ""));
        ossInfo.setPathBookVideo(preOss.getString(FIELD_OSS_PATH_BOOK_VIDEO, ""));
        ossInfo.setPathBookThumb(preOss.getString(FIELD_OSS_PATH_BOOK_THUMB, ""));
        return ossInfo;
    }

    public static void clearUser() {
        SPUtils.clear(SHARE_USER);
    }

    public static void clearOssInfo() {
        SPUtils.clear(SHARE_OSS_INFO);
    }

    public static void clearCouple() {
        SPUtils.clear(SHARE_COUPLE);
    }

    public static void setSettingsTheme(int themeId) {
        SharedPreferences.Editor editor = SPUtils.getSharedPreferences(SHARE_SETTINGS).edit();
        editor.putInt(FIELD_SET_THEME, themeId);
        editor.apply();
    }

    public static int getSettingsTheme() {
        SharedPreferences preferences = SPUtils.getSharedPreferences(SHARE_SETTINGS);
        return preferences.getInt(FIELD_SET_THEME, ThemeHelper.THEME_PINK);
    }

    public static void setSettingsNoticeSystem(boolean system) {
        SharedPreferences.Editor editor = SPUtils.getSharedPreferences(SHARE_SETTINGS).edit();
        editor.putBoolean(FIELD_SET_NOTICE_SYSTEM, system);
        editor.apply();
    }

    public static boolean getSettingsNoticeSystem() {
        SharedPreferences preferences = SPUtils.getSharedPreferences(SHARE_SETTINGS);
        return preferences.getBoolean(FIELD_SET_NOTICE_SYSTEM, true);
    }

    public static void setSettingsNoticeSocial(boolean ta) {
        SharedPreferences.Editor editor = SPUtils.getSharedPreferences(SHARE_SETTINGS).edit();
        editor.putBoolean(FIELD_SET_NOTICE_SOCIAL, ta);
        editor.apply();
    }

    public static boolean getSettingsNoticeSocial() {
        SharedPreferences preferences = SPUtils.getSharedPreferences(SHARE_SETTINGS);
        return preferences.getBoolean(FIELD_SET_NOTICE_SOCIAL, false);
    }

}
