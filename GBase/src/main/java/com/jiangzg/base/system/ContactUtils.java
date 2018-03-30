package com.jiangzg.base.system;

import android.annotation.SuppressLint;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.util.ArrayMap;

import com.jiangzg.base.application.AppBase;
import com.jiangzg.base.common.LogUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by gg on 2017/4/11.
 * 联系人管理类
 */
public class ContactUtils {

    private static final String LOG_TAG = "ContactUtils";

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
    @SuppressLint("MissingPermission")
    public static List<Map<String, String>> getContacts() {
        ContentResolver resolver = AppBase.getInstance().getContentResolver();

        List<Map<String, String>> list = new ArrayList<>();
        Cursor cursorID = resolver.query(ContactsContract.Contacts.CONTENT_URI,
                new String[]{"_id"}, null, null, null);

        if (cursorID == null) {
            LogUtils.w(LOG_TAG, "getContacts: cursorID == null");
            return list;
        }
        while (cursorID.moveToNext()) {
            int contractID = cursorID.getInt(0);
            Uri uri = Uri.parse("content://com.android.contacts/contacts/" + contractID + "/data");
            Cursor cursorData = resolver.query(uri,
                    new String[]{"mimetype", "data1", "data2"}, null, null, null);

            if (cursorData == null) {
                LogUtils.w(LOG_TAG, "getContacts: cursorData == null");
                return list;
            }
            Map<String, String> map = new ArrayMap<>();
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
    @SuppressLint("MissingPermission")
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
            AppBase.getInstance().getContentResolver().applyBatch("com.android.contacts", list);
            return true;
        } catch (RemoteException | OperationApplicationException e) {
            LogUtils.e(LOG_TAG, "insertContact", e);
        }
        return false;
    }

}
