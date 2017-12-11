package com.android.base.function;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;
import android.provider.ContactsContract;

import com.android.base.component.application.AppContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by gg on 2017/4/11.
 * 联系人管理类
 */
public class ContactUtils {


    /**
     * ********************************联系人相关*********************************
     * 访问的路径
     */
    private final static String mimeType_name = "vnd.android.cursor.item/name";
    private final static String mimeType_phone = "vnd.android.cursor.item/phone_v2";
    private final static String mimeType_email = "vnd.android.cursor.item/email_v2";

    /**
     * 读取联系人数据 KEY见上
     * 需添加权限 <uses-permission android:name="android.permission.READ_CONTACTS"/>
     */
    public static List<Map<String, String>> getContacts() {
        ContentResolver resolver = AppContext.get().getContentResolver();

        List<Map<String, String>> list = new ArrayList<>();
        Cursor cursorID = resolver.query(ContactsContract.Contacts.CONTENT_URI,
                new String[]{"_id"}, null, null, null);

        if (cursorID == null) return null;
        while (cursorID.moveToNext()) {
            int contractID = cursorID.getInt(0);
            Uri uri = Uri.parse("content://com.android.contacts/contacts/" + contractID + "/data");
            Cursor cursorData = resolver.query(uri,
                    new String[]{"mimetype", "data1", "data2"}, null, null, null);

            if (cursorData == null) return null;
            Map<String, String> map = new HashMap<>();
            map.put("id", "" + contractID);
            while (cursorData.moveToNext()) {
                String mimeType = cursorData.getString(cursorData.getColumnIndex("mimetype"));
                String data = cursorData.getString(cursorData.getColumnIndex("data1"));
                if (mimeType_name.equals(mimeType)) {
                    map.put("name", data);
                } else if (mimeType_email.equals(mimeType)) {
                    map.put("email", data);
                } else if (mimeType_phone.equals(mimeType)) {
                    map.put("phone", data);
                }
            }
            list.add(map);
            cursorData.close();
        }
        cursorID.close();
        return list;
    }

    /**
     * 添加联系人  <uses-permission android:name="android.permission.WRITE_CONTACTS"/>
     */
    public static boolean insertContact(String name, String number, String email) {
        ArrayList<ContentProviderOperation> list = new ArrayList<>();

        ContentProviderOperation addAccount = ContentProviderOperation
                .newInsert(ContactsContract.RawContacts.CONTENT_URI)
                .withValue("account_name", null)
                .build();
        list.add(addAccount);

        Uri data_uri = ContactsContract.Data.CONTENT_URI;

        ContentProviderOperation addName = ContentProviderOperation.newInsert(data_uri)
                .withValueBackReference("raw_contact_id", 0)
                .withValue("mimetype", mimeType_name)
                .withValue("data2", name)
                .build();
        list.add(addName);

        ContentProviderOperation addNumber = ContentProviderOperation.newInsert(data_uri)
                .withValueBackReference("raw_contact_id", 0)
                .withValue("mimetype", mimeType_phone)
                .withValue("data1", number)
                .withValue("data2", "2")
                .build();
        list.add(addNumber);

        ContentProviderOperation addEmail = ContentProviderOperation.newInsert(data_uri)
                .withValueBackReference("raw_contact_id", 0)
                .withValue("mimetype", mimeType_email)
                .withValue("data1", email)
                .withValue("data2", "2")
                .build();
        list.add(addEmail);

        try {
            AppContext.get().getContentResolver().applyBatch("com.android.contacts", list);
            return true;
        } catch (RemoteException | OperationApplicationException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * IntentUtils.getContacts返回时调用
     * 在onActivityResult中调用，获取选中的号码
     */
    public static String getContactSelect(Intent data) {
        String num = "";
        if (data == null) return num;
        Uri uri = data.getData();
        // 创建内容解析者
        ContentResolver contentResolver = AppContext.get().getContentResolver();
        Cursor cursor = contentResolver.query(uri, null, null, null, null);
        if (cursor == null) return num;
        while (cursor.moveToNext()) {
            num = cursor.getString(cursor.getColumnIndex("data1"));
        }
        cursor.close();
        num = num.replaceAll("-", "");//替换的操作,555-6 -> 5556
        return num;
    }

}
