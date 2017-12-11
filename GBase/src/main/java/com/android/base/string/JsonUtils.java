package com.android.base.string;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * json 工具
 *
 * @author duohuo-jinghao
 */
public class JsonUtils {

    public static List<Map<String, Object>> getList(String jsonString, String key) {
        List<Map<String, Object>> list = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(key);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject tempObject = jsonArray.getJSONObject(i);
                Map<String, Object> map = new HashMap<>();
                Iterator<String> iterator = tempObject.keys();
                while (iterator.hasNext()) {
                    String json_key = iterator.next();
                    Object json_value = tempObject.get(json_key);
                    if (json_value == null)
                        json_value = "";
                    map.put(json_key, json_value);
                }
                list.add(map);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static List<Map<String, Object>> getList(String jsonString) {
        List<Map<String, Object>> list = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONObject resultObject = jsonObject.getJSONObject("result");
            JSONArray listArray = resultObject.getJSONArray("list");
            for (int i = 0; i < listArray.length(); i++) {
                JSONObject listObject = listArray.getJSONObject(i);
                JSONArray trArray = listObject.getJSONArray("tr");
                for (int m = 0; m < trArray.length(); m++) {
                    JSONObject trObject = trArray.getJSONObject(m);

                    Iterator<String> iterator = trObject.keys();
                    Map<String, Object> map = new HashMap<>();
                    while (iterator.hasNext()) {
                        String json_key = iterator.next();
                        Object json_value = trObject.get(json_key);
                        if (json_value == null)
                            json_value = "";
                        map.put(json_key, json_value);
                    }
                    list.add(map);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 获取 string, key可用点用分割 ,空返回null
     */
    public static String getString(JSONObject jo, String key) {
        if (jo == null) return null;
        try {
            if (key.contains(".")) {
                String[] tks = key.split("\\.");
                JSONObject jot = jo;
                String value = null;
                for (int j = 0; j < tks.length; j++) {
                    String tk = tks[j];
                    if (jot.has(tk)) {
                        if (j == tks.length - 1) {
                            value = jot.getString(tk);
                        } else {
                            jot = jot.getJSONObject(tk);
                        }
                    } else {
                        if (value == null) value = "";
                        return value;
                    }
                }
                if ("null".equals(value)) {
                    value = "";
                }
                return value;
            } else {
                String value = null;
                if (jo.has(key)) {
                    value = jo.getString(key);
                }
                if ("null".equals(value)) {
                    value = "";
                }
                return value;
            }
        } catch (JSONException e) {
        }
        return "";
    }

    /**
     * 获取string key可用点做分割  空时返回""
     */
    public static String getStringNoEmpty(JSONObject jo, String key) {
        return getString(jo, key, "");
    }

    public static String getString(JSONObject jo, String key, String def) {
        String value = getString(jo, key);
        if (!TextUtils.isEmpty(value)) {
            return value;
        } else {
            return def;
        }
    }

    public static Integer getInt(JSONObject jo, String key) {
        String value = getString(jo, key);
        if (!TextUtils.isEmpty(value)) {
            try {
                return Integer.parseInt(value);
            } catch (Exception e) {
            }
        }
        return 0;
    }

    public static Integer getInt(JSONObject jo, String key, Integer def) {
        String value = getString(jo, key);
        if (!TextUtils.isEmpty(value)) {
            try {
                return Integer.parseInt(value);
            } catch (Exception e) {
            }

        }
        return def;
    }


    public static Long getLong(JSONObject jo, String key) {
        String value = getString(jo, key);
        if (!TextUtils.isEmpty(value)) {
            try {
                return Long.parseLong(value);
            } catch (Exception e) {
            }

        }
        return 0l;
    }

    public static Long getLong(JSONObject jo, String key, Long def) {
        String value = getString(jo, key);
        if (!TextUtils.isEmpty(value)) {
            try {
                return Long.parseLong(value);
            } catch (Exception e) {
            }
        }
        return def;
    }

    public static Float getFloat(JSONObject jo, String key) {
        String value = getString(jo, key);
        if (!TextUtils.isEmpty(value)) {
            try {
                return Float.parseFloat(value);
            } catch (Exception e) {
            }
        }
        return 0f;
    }

    public static Double getDouble(JSONObject jo, String key) {
        String value = getString(jo, key);
        if (!TextUtils.isEmpty(value)) {
            try {
                return Double.parseDouble(value);
            } catch (Exception e) {
            }

        }
        return 0d;
    }

    /**
     * 获取boolean 如果没有返回 false
     */
    public static Boolean getBoolean(JSONObject jo, String key) {
        String value = getString(jo, key);
        if (!TextUtils.isEmpty(value)) {
            try {
                if (value.equals("1")) {
                    return true;
                } else if (value.equals("0")) {
                    return false;
                }
                return Boolean.parseBoolean(value);
            } catch (Exception e) {
            }
        }
        return false;
    }

    /**
     * 获取jsonobject key 不可点分割
     */
    public static JSONObject getJSONObject(JSONObject jo, String key) {
        if (jo == null || TextUtils.isEmpty(key)) return null;
        try {
            if (key.contains(".")) {
                String[] tks = key.split("\\.");
                JSONObject jot = jo;
                JSONObject value = null;
                for (int j = 0; j < tks.length; j++) {
                    String tk = tks[j];
                    if (jot.has(tk)) {
                        if (j == tks.length - 1) {
                            value = jot.getJSONObject(tk);
                        } else {
                            jot = jot.getJSONObject(tk);
                        }
                    } else {
                        return null;
                    }
                }
                return value;
            } else {
                JSONObject value = null;
                if (jo.has(key)) {
                    value = jo.getJSONObject(key);
                }
                if ("null".equals(value)) {
                    value = null;
                }
                return value;
            }
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取jsonarray 可不可点分割
     */
    public static JSONArray getJSONArray(JSONObject jo, String key) {
        if (jo == null || TextUtils.isEmpty(key)) return null;
        try {
            if (key.contains(".")) {
                String[] tks = key.split("\\.");
                JSONObject jot = jo;
                JSONArray value = null;
                for (int j = 0; j < tks.length; j++) {
                    String tk = tks[j];
                    if (jot.has(tk)) {
                        if (j == tks.length - 1) {
                            value = jot.getJSONArray(tk);
                        } else {
                            jot = jot.getJSONObject(tk);
                        }
                    } else {
                        return null;
                    }
                }
                if (value == null) {
                    value = new JSONArray();
                }
                return value;
            } else {
                JSONArray value = null;
                if (jo.has(key)) {

                    value = jo.getJSONArray(key);
                }
                if ("null".equals(value)) {
                    value = null;
                }
                if (value == null) {
                    value = new JSONArray();
                }
                return value;
            }
        } catch (Exception e) {
            return null;
        }
    }

    public static JSONObject getJSONObjectAt(JSONArray array, int index) {
        try {
            return array.getJSONObject(index);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;

    }

    /**
     * json转为map
     */
    public static Map<String, Object> jsonToMap(JSONObject jo) {
        if (jo == null)
            return null;
        Map<String, Object> map = new HashMap<String, Object>();
        for (Iterator<String> iterator = jo.keys(); iterator.hasNext(); ) {
            String key = iterator.next();
            try {
                Object value = jo.get(key);
                if (!(value instanceof JSONObject) && !(value instanceof JSONArray)) {
                    map.put(key, value);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return map;
    }


    public static boolean isEmpty(JSONArray array) {
        return array == null || array.length() == 0;
    }

    /**
     *
     */
    public static void put(JSONObject jo, String key, Object value) {
        if (jo == null || TextUtils.isEmpty(key)) return;
        try {
            jo.put(key, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void add(JSONArray array, Object value) {
        if (array == null || value == null) return;
        array.put(value);
    }


}
