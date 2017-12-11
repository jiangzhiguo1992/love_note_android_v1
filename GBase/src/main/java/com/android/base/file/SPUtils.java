package com.android.base.file;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.android.base.component.application.AppContext;

import java.util.HashMap;
import java.util.Map;

/**
 * author cipherGG
 * Created by Administrator on 2016/5/25.
 * describe SharedPreferences的管理类
 */
public class SPUtils {

    /* SP集合 */
    private static Map<String, SharedPreferences> map = new HashMap<>();

    public static SharedPreferences getSharedPreferences(String name) {
        SharedPreferences sharedPreferences;
        if (TextUtils.isEmpty(name)) { // 获取默认Preferences
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(AppContext.get());
        } else { // 创建以name为名称的Preferences
            sharedPreferences = map.get(name);
            if (sharedPreferences == null) {
                sharedPreferences = AppContext.get()
                        .getSharedPreferences(name, Context.MODE_PRIVATE);
                map.put(name, sharedPreferences);
            }
        }
        return sharedPreferences;
    }

    /**
     * 是否包含Key
     */
    public static boolean contains(SharedPreferences sp, String key) {
        return sp.contains(key);
    }

    /**
     * 移除Key
     */
    public static boolean remove(SharedPreferences sp, String key) {
        return sp.edit().remove(key).commit();
    }

    /**
     * 获取所有键值对
     */
    public static Map<String, ?> getAll(SharedPreferences sp) {
        return sp.getAll();
    }

    /**
     * int
     */
    public static boolean putInt(SharedPreferences sp, String key, int value) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(key, value);
        return editor.commit();
    }

    public static int getInt(SharedPreferences sp, String key, int defValue) {
        return sp.getInt(key, defValue);
    }

    /**
     * long
     */
    public static boolean putLong(SharedPreferences sp, String key, long value) {
        return sp.edit().putLong(key, value).commit();
    }

    public static long getLong(SharedPreferences sp, String key, long defValue) {
        return sp.getLong(key, defValue);
    }

    /**
     * boolean
     */
    public static boolean putBoolean(SharedPreferences sp, String key, boolean value) {
        return sp.edit().putBoolean(key, value).commit();
    }

    public static boolean getBoolean(SharedPreferences sp, String key, boolean defValue) {
        return sp.getBoolean(key, defValue);
    }

    /**
     * string
     */
    public static boolean putString(SharedPreferences sp, String key, String value) {
        return sp.edit().putString(key, value).commit();
    }

    public static String getString(SharedPreferences sp, String key, String defValue) {
        return sp.getString(key, defValue);
    }

}
