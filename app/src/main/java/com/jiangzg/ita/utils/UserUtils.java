package com.jiangzg.ita.utils;

import android.content.SharedPreferences;

import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.file.SPUtils;
import com.jiangzg.ita.domain.Couple;
import com.jiangzg.ita.domain.User;
import com.jiangzg.ita.third.GsonUtils;
import com.jiangzg.ita.third.LogUtils;

/**
 * Created by Fan on 2017/3/2.
 * Preferences 帮助类
 */
public class UserUtils {

    private static final String LOG_TAG = "UserUtils";

    private static final String SHARE_USER = "shareUser";

    /* 储存字段 */
    private static final String FIELD_ID = "id";
    private static final String FIELD_PHONE = "phone";
    private static final String FIELD_SEX = "sex";
    private static final String FIELD_BIRTHDAY = "birthday";
    private static final String FIELD_USERTOKEN = "userToken";
    private static final String FIELD_CP_ID = "coupleId";
    private static final String FIELD_CP_CREATOR_ID = "creatorId";
    private static final String FIELD_CP_CREATOR_NAME = "creatorName";
    private static final String FIELD_CP_CREATOR_AVATAR = "creatorAvatar";
    private static final String FIELD_CP_INVITEE_ID = "inviteeId";
    private static final String FIELD_CP_INVITEE_NAME = "inviteeName";
    private static final String FIELD_CP_INVITEE_AVATAR = "inviteeAvatar";

    /**
     * 存取User
     */
    public static void setUser(User user) {
        if (user == null) {
            LogUtils.e(LOG_TAG, "user == null");
            return;
        }
        clearUser();
        SharedPreferences.Editor editor = SPUtils.getSharedPreferences(SHARE_USER).edit();
        LogUtils.json(GsonUtils.getGson().toJson(user));
        editor.putLong(FIELD_ID, user.getId());
        editor.putString(FIELD_PHONE, user.getPhone());
        editor.putInt(FIELD_SEX, user.getSex());
        editor.putLong(FIELD_BIRTHDAY, user.getBirthday());
        editor.putString(FIELD_USERTOKEN, user.getUserToken());
        editor.apply();
    }

    public static void setCouple(Couple couple) {
        if (couple == null || couple.getId() <= 0 || couple.getCreatorId() <= 0 || couple.getInviteeId() <= 0) {
            LogUtils.e(LOG_TAG, "couple == null");
            return; //只存带cp的user
        }
        SharedPreferences.Editor editor = SPUtils.getSharedPreferences(SHARE_USER).edit();
        editor.putLong(FIELD_CP_ID, couple.getId());
        editor.putLong(FIELD_CP_CREATOR_ID, couple.getCreatorId());
        editor.putString(FIELD_CP_CREATOR_NAME, couple.getCreatorName());
        editor.putString(FIELD_CP_CREATOR_AVATAR, couple.getCreatorAvatar());
        editor.putLong(FIELD_CP_INVITEE_ID, couple.getInviteeId());
        editor.putString(FIELD_CP_INVITEE_NAME, couple.getInviteeName());
        editor.putString(FIELD_CP_INVITEE_AVATAR, couple.getInviteeAvatar());
        editor.apply();
    }

    /**
     * 获取缓存User
     */
    public static User getUser() {
        SharedPreferences preference = SPUtils.getSharedPreferences(SHARE_USER);
        User user = new User();
        user.setId(preference.getLong(FIELD_ID, 0));
        user.setPhone(preference.getString(FIELD_PHONE, ""));
        user.setSex(preference.getInt(FIELD_SEX, 0));
        user.setBirthday(preference.getLong(FIELD_BIRTHDAY, 0));
        user.setUserToken(preference.getString(FIELD_USERTOKEN, ""));
        Couple couple = new Couple();
        couple.setId(preference.getLong(FIELD_CP_ID, 0));
        couple.setCreatorId(preference.getLong(FIELD_CP_CREATOR_ID, 0));
        couple.setCreatorName(preference.getString(FIELD_CP_CREATOR_NAME, ""));
        couple.setCreatorAvatar(preference.getString(FIELD_CP_CREATOR_AVATAR, ""));
        couple.setInviteeId(preference.getLong(FIELD_CP_INVITEE_ID, 0));
        couple.setInviteeName(preference.getString(FIELD_CP_INVITEE_NAME, ""));
        couple.setInviteeAvatar(preference.getString(FIELD_CP_INVITEE_AVATAR, ""));
        user.setCouple(couple);
        return user;
    }

    /**
     * 清除用户信息
     */
    public static void clearUser() {
        SharedPreferences.Editor editor = SPUtils.getSharedPreferences(SHARE_USER).edit();
        editor.clear().apply();
    }

    public static boolean noLogin() {
        String userToken = UserUtils.getUser().getUserToken();
        return StringUtils.isEmpty(userToken);
    }

    public static boolean noCouple() {
        Couple couple = getUser().getCouple();
        if (couple == null || couple.getId() <= 0 || couple.getCreatorId() <= 0 || couple.getInviteeId() <= 0) {
            LogUtils.e(LOG_TAG, "couple == null");
            return true;
        }
        return false;
    }

    public static boolean noCouple(Couple couple) {
        if (couple == null || couple.getId() <= 0 || couple.getCreatorId() <= 0 || couple.getInviteeId() <= 0) {
            LogUtils.e(LOG_TAG, "couple == null");
            return true;
        }
        return false;
    }

}
