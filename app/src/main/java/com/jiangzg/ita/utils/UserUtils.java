package com.jiangzg.ita.utils;

import android.content.SharedPreferences;

import com.jiangzg.base.file.SPUtils;
import com.jiangzg.ita.third.GsonUtils;
import com.jiangzg.ita.third.LogUtils;
import com.jiangzg.ita.domain.User;

/**
 * Created by Fan on 2017/3/2.
 * Preferences 帮助类
 */
public class UserUtils {

    private static final String SHARE_USER = "user_info";

    /* 储存字段 */
    private static final String id = "id";
    private static final String userToken = "userToken";

    /**
     * 存取User
     */
    public static void setUser(User user) {
        LogUtils.json(GsonUtils.getGson().toJson(user));
        clearUser();
        SharedPreferences.Editor editor = SPUtils.getSharedPreferences(SHARE_USER).edit();
        editor.putString(id, user.getId());
        editor.putString(userToken, user.getUserToken());
        editor.apply();
    }

    /**
     * 获取缓存User
     */
    public static User getUser() {
        SharedPreferences preference = SPUtils.getSharedPreferences(SHARE_USER);
        User user = new User();
        user.setId(preference.getString(id, ""));
        user.setUserToken(preference.getString(userToken, ""));
        return user;
    }

    /**
     * 清除用户信息
     */
    public static void clearUser() {
        SharedPreferences.Editor editor = SPUtils.getSharedPreferences(SHARE_USER).edit();
        editor.clear().apply();
    }
}
