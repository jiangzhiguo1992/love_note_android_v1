package com.android.base.function;

import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.telephony.SmsManager;

import com.android.base.component.application.AppContext;
import com.android.base.string.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by gg on 2017/4/11.
 * 短信管理类
 */
public class SMSUtils {

    /**
     * 查询SMS ( date为long，type = 1 为接受的短信， 2 为发送的短信 )
     * <uses-permission android:name="android.permission.READ_SMS"/>
     */
    public static List<Map<String, String>> getSMS() {
        List<Map<String, String>> list = new ArrayList<>();
        String[] pros = new String[]{"_id", "address", "person", "body", "date", "type"};
        Uri sms_uri = Uri.parse("content://sms");
        Cursor cursor = AppContext.get().getContentResolver().query(sms_uri, pros, null, null, "date desc");

        if (cursor == null) return null;
        while (cursor.moveToNext()) {
            Map<String, String> map = new HashMap<>();
            String id = cursor.getString(cursor.getColumnIndex("_id"));
            String address = cursor.getString(cursor.getColumnIndex("address"));
            String person = cursor.getString(cursor.getColumnIndex("person"));
            String body = cursor.getString(cursor.getColumnIndex("body"));
            String date = cursor.getString(cursor.getColumnIndex("date"));
            String type = cursor.getString(cursor.getColumnIndex("type"));
            map.put("id", id);
            map.put("address", address);
            map.put("person", person);
            map.put("body", body);
            map.put("data", date);
            map.put("type", type);
            list.add(map);
        }
        cursor.close();

        return list;
    }

    /**
     * 添加SMS type(见上)
     * <uses-permission android:name="android.permission.WRITE_SMS"/>
     */
    public static boolean insertSMS(String phone, String body, long date, int type) {
        ContentValues values = new ContentValues();
        values.put("address", phone);
        values.put("body", body);
        values.put("type", String.valueOf(type));
        values.put("date", String.valueOf(date));
        try {
            Uri sms_uri = Uri.parse("content://sms");
            AppContext.get().getContentResolver().insert(sms_uri, values);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 直接发送短信
     * <uses-permission android:name="android.permission.SEND_SMS"/>
     */
    public static void sendSMS(String phoneNumber, String content) {
        if (StringUtils.isEmpty(content)) return;
        PendingIntent sentIntent = PendingIntent.getBroadcast(AppContext.get(), 0, new Intent(), 0);
        SmsManager smsManager = SmsManager.getDefault();
        if (content.length() >= 70) {
            List<String> ms = smsManager.divideMessage(content);
            for (String str : ms) {
                smsManager.sendTextMessage(phoneNumber, null, str, sentIntent, null);
            }
        } else {
            smsManager.sendTextMessage(phoneNumber, null, content, sentIntent, null);
        }
    }

}
