package com.jiangzg.project.utils;

import android.content.SharedPreferences;

import com.android.base.file.SPUtils;
import com.android.depend.utils.GsonUtils;
import com.android.depend.utils.LogUtils;
import com.jiangzg.project.domain.User;

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
        LogUtils.json(GsonUtils.getGSON().toJson(user));
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
