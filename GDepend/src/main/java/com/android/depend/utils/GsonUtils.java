package com.android.depend.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by Fan-pc on 2015/11/13.
 * describe json工具类
 */
public class GsonUtils {

    private static Gson instance;

    public static Gson getGSON() {
        if (instance == null) {
            synchronized (GsonUtils.class) {
                if (instance == null) {
                    instance = new GsonBuilder()
                            // 配置
                            .create();
                }
            }
        }
        return instance;
    }

    public static <T> List<T> getList(JSONObject object, String key) {
        JSONArray array = object.optJSONArray(key);
        return getList(array, getType());
    }

    public static <T> List<T> getList(JSONArray array) {
        return instance.fromJson(array.toString(), getType());
    }

    private static <T> List<T> getList(JSONArray array, Type type) {
        return instance.fromJson(array.toString(), type);
    }

    private static <T> Type getType() {
        return new TypeToken<List<T>>() {
        }.getType();
    }

}
