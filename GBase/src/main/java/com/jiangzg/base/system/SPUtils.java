package com.jiangzg.base.system;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.ArrayMap;

import com.jiangzg.base.application.AppBase;
import com.jiangzg.base.common.LogUtils;

import java.util.Map;

/**
 * author cipherGG
 * Created by Administrator on 2016/5/25.
 * describe SharedPreferences的管理类
 */
public class SPUtils {

    /* SP集合 */
    private static Map<String, SharedPreferences> mapShare;

    public static SharedPreferences getSharedPreferences(String name) {
        SharedPreferences sharedPreferences;
        if (TextUtils.isEmpty(name)) { // 获取默认Preferences
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(AppBase.getInstance());
        } else { // 创建以name为名称的Preferences
            if (mapShare == null) {
                mapShare = new ArrayMap<>();
            }
            sharedPreferences = mapShare.get(name);
            if (sharedPreferences == null) {
                LogUtils.d(SPUtils.class, "getSharedPreferences", name);
                sharedPreferences = AppBase.getInstance().getSharedPreferences(name, Context.MODE_PRIVATE);
                mapShare.put(name, sharedPreferences);
            }
        }
        return sharedPreferences;
    }

    /**
     * 清除key内容
     */
    public static void clear(String spName) {
        SharedPreferences preferences = getSharedPreferences(spName);
        clear(preferences);
    }

    public static void clear(SharedPreferences preferences) {
        if (preferences == null) {
            LogUtils.w(SPUtils.class, "clear", "preferences == null");
            return;
        }
        preferences.edit().clear().apply();
    }

    /**
     * 移除Key
     */
    public static boolean removeKey(SharedPreferences sp, String key) {
        return sp.edit().remove(key).commit();
    }

    /**
     * 是否包含Key
     */
    public static boolean containsKey(SharedPreferences sp, String key) {
        return sp.contains(key);
    }

    /**
     * 获取所有键值对
     */
    public static Map<String, ?> getAllKey(SharedPreferences sp) {
        return sp.getAll();
    }

}
