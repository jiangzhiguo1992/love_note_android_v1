package com.jiangzg.ita.utils;

import android.content.SharedPreferences;

import com.jiangzg.base.file.SPUtils;
import com.jiangzg.ita.R;
import com.jiangzg.ita.domain.Couple;
import com.jiangzg.ita.domain.User;
import com.jiangzg.ita.third.GsonUtils;
import com.jiangzg.ita.third.LogUtils;

/**
 * Created by Fan on 2017/3/2.
 * Preferences 帮助类
 */
public class PreferenceUser {

    private static final String LOG_TAG = "PreferenceUser";

    private static final String SHARE_USER = "shareUser";
    private static final String SHARE_COUPLE = "shareCouple";
    private static final String SHARE_SETTINGS = "shareSettings";

    /* 储存字段 */
    private static final String FIELD_USER_ID = "id";
    private static final String FIELD_USER_PHONE = "phone";
    private static final String FIELD_USER_SEX = "sex";
    private static final String FIELD_USER_BIRTHDAY = "birthday";
    private static final String FIELD_USER_TOKEN = "userToken";
    private static final String FIELD_CP_ID = "coupleId";
    private static final String FIELD_CP_CREATOR_ID = "creatorId";
    private static final String FIELD_CP_CREATOR_NAME = "creatorName";
    private static final String FIELD_CP_CREATOR_AVATAR = "creatorAvatar";
    private static final String FIELD_CP_INVITEE_ID = "inviteeId";
    private static final String FIELD_CP_INVITEE_NAME = "inviteeName";
    private static final String FIELD_CP_INVITEE_AVATAR = "inviteeAvatar";

    private static final String FIELD_SET_THEME = "theme";
    private static final String FIELD_SET_ONLY_WIFI = "phone";
    private static final String FIELD_SET_AUTO_DOWNLOAD = "sex";
    private static final String FIELD_SET_NOTICE_SYSTEM = "birthday";
    private static final String FIELD_SET_NOTICE_TA = "userToken";
    private static final String FIELD_SET_NOTICE_OTHER = "coupleId";

    public static void setUser(User user) {
        if (user == null) {
            LogUtils.e(LOG_TAG, "user == null");
            return;
        }
        clearUser();
        SharedPreferences.Editor editor = SPUtils.getSharedPreferences(SHARE_USER).edit();
        LogUtils.json(GsonUtils.getGson().toJson(user));
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

    public static void clearUser() {
        SharedPreferences.Editor editor = SPUtils.getSharedPreferences(SHARE_USER).edit();
        editor.clear().apply();
    }

    public static void setCouple(Couple couple) {
        if (noCouple(couple)) {
            LogUtils.e(LOG_TAG, "couple == null");
            return;
        }
        clearCouple();
        SharedPreferences.Editor editor = SPUtils.getSharedPreferences(SHARE_COUPLE).edit();
        editor.putLong(FIELD_CP_ID, couple.getId());
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
        couple.setCreatorId(preCouple.getLong(FIELD_CP_CREATOR_ID, 0));
        couple.setCreatorName(preCouple.getString(FIELD_CP_CREATOR_NAME, ""));
        couple.setCreatorAvatar(preCouple.getString(FIELD_CP_CREATOR_AVATAR, ""));
        couple.setInviteeId(preCouple.getLong(FIELD_CP_INVITEE_ID, 0));
        couple.setInviteeName(preCouple.getString(FIELD_CP_INVITEE_NAME, ""));
        couple.setInviteeAvatar(preCouple.getString(FIELD_CP_INVITEE_AVATAR, ""));
        return couple;
    }

    public static void clearCouple() {
        SharedPreferences.Editor editor = SPUtils.getSharedPreferences(SHARE_COUPLE).edit();
        editor.clear().apply();
    }

    public static boolean noLogin() {
        String userToken = PreferenceUser.getUser().getUserToken();
        //return StringUtils.isEmpty(userToken);
        return true;
    }

    public static boolean noCouple() {
        Couple couple = getUser().getCouple();
        return noCouple(couple);
    }

    public static boolean noCouple(Couple couple) {
        return couple == null || couple.getId() <= 0
                || couple.getCreatorId() <= 0 || couple.getInviteeId() <= 0
                || couple.getStatus() == Couple.CoupleStatusDel
                || couple.getStatus() == Couple.CoupleStatusReg;
    }

    public static void setSettingsTheme(int themeId) {
        SharedPreferences.Editor editor = SPUtils.getSharedPreferences(SHARE_SETTINGS).edit();
        editor.putInt(FIELD_SET_THEME, themeId);
        editor.apply();
    }

    public static int getSettingsTheme() {
        SharedPreferences preferences = SPUtils.getSharedPreferences(SHARE_SETTINGS);
        return preferences.getInt(FIELD_SET_THEME, ThemeUtils.THEME_PINK);
    }

    public static void setSettingsOnlyWifi(boolean onlyWifi) {
        SharedPreferences.Editor editor = SPUtils.getSharedPreferences(SHARE_SETTINGS).edit();
        editor.putBoolean(FIELD_SET_ONLY_WIFI, onlyWifi);
        editor.apply();
    }

    public static boolean getSettingsOnlyWifi() {
        SharedPreferences preferences = SPUtils.getSharedPreferences(SHARE_SETTINGS);
        return preferences.getBoolean(FIELD_SET_ONLY_WIFI, false);
    }

    public static void setSettingsAutoDownload(boolean autoDownload) {
        SharedPreferences.Editor editor = SPUtils.getSharedPreferences(SHARE_SETTINGS).edit();
        editor.putBoolean(FIELD_SET_AUTO_DOWNLOAD, autoDownload);
        editor.apply();
    }

    public static boolean getSettingsAutoDownload() {
        SharedPreferences preferences = SPUtils.getSharedPreferences(SHARE_SETTINGS);
        return preferences.getBoolean(FIELD_SET_AUTO_DOWNLOAD, false);
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

    public static void setSettingsNoticeTa(boolean ta) {
        SharedPreferences.Editor editor = SPUtils.getSharedPreferences(SHARE_SETTINGS).edit();
        editor.putBoolean(FIELD_SET_NOTICE_TA, ta);
        editor.apply();
    }

    public static boolean getSettingsNoticeTa() {
        SharedPreferences preferences = SPUtils.getSharedPreferences(SHARE_SETTINGS);
        return preferences.getBoolean(FIELD_SET_NOTICE_TA, true);
    }

    public static void setSettingsNoticeOther(boolean ta) {
        SharedPreferences.Editor editor = SPUtils.getSharedPreferences(SHARE_SETTINGS).edit();
        editor.putBoolean(FIELD_SET_NOTICE_OTHER, ta);
        editor.apply();
    }

    public static boolean getSettingsNoticeOther() {
        SharedPreferences preferences = SPUtils.getSharedPreferences(SHARE_SETTINGS);
        return preferences.getBoolean(FIELD_SET_NOTICE_OTHER, false);
    }

}
