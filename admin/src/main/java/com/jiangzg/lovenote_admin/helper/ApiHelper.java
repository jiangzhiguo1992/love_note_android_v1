package com.jiangzg.lovenote_admin.helper;

import com.jiangzg.lovenote_admin.R;
import com.jiangzg.lovenote_admin.base.MyApp;
import com.jiangzg.lovenote_admin.domain.Sms;
import com.jiangzg.lovenote_admin.domain.User;

/**
 * Created by JZG on 2018/3/27.
 * api辅助类
 */
public class ApiHelper {

    // sms
    public static final int[] LIST_SMS_TYPE = new int[]{
            0,
            Sms.TYPE_REGISTER,
            Sms.TYPE_LOGIN,
            Sms.TYPE_FORGET,
            Sms.TYPE_PHONE,
            Sms.TYPE_LOCK,
    };
    public static final String[] LIST_SMS_SHOW = new String[]{
            MyApp.get().getString(R.string.all),
            MyApp.get().getString(R.string.register),
            MyApp.get().getString(R.string.login),
            MyApp.get().getString(R.string.forget_pwd),
            MyApp.get().getString(R.string.change_phone),
            MyApp.get().getString(R.string.pwd_lock),
    };
    // user sex
    public static final int[] LIST_USER_SEX_TYPE = new int[]{
            0,
            User.SEX_GIRL,
            User.SEX_BOY,
    };
    public static final String[] LIST_USER_SEX_SHOW = new String[]{
            MyApp.get().getString(R.string.all),
            MyApp.get().getString(R.string.girl),
            MyApp.get().getString(R.string.boy),
    };
    // user修改类型
    public static final int MODIFY_ADMIN_UPDATE_INFO = 21;
    public static final int MODIFY_ADMIN_UPDATE_STATUS = 22;
    // entry
    public static final String ENTRY_FILED_DEVICE_NAME = "deviceName";
    public static final String ENTRY_FILED_MARKET = "market";
    public static final String ENTRY_FILED_PLATFORM = "platform";
    public static final String ENTRY_FILED_OS_VERSION = "osVersion";
    public static final String ENTRY_FILED_APP_VERSION = "appVersion";
    public static final String[] LIST_ENTRY_FILEDS_SHOW = new String[]{
            ENTRY_FILED_DEVICE_NAME,
            ENTRY_FILED_MARKET,
            ENTRY_FILED_PLATFORM,
            ENTRY_FILED_OS_VERSION,
            ENTRY_FILED_APP_VERSION,
    };
    // comment order类型
    private static final int LIST_COMMENT_ORDER_POINT = 0;
    private static final int LIST_COMMENT_ORDER_TIME = 1;
    public static final int[] LIST_COMMENT_ORDER_TYPE = new int[]{
            LIST_COMMENT_ORDER_POINT,
            LIST_COMMENT_ORDER_TIME
    };
    public static final String[] LIST_COMMENT_ORDER_SHOW = new String[]{
            MyApp.get().getString(R.string.point),
            MyApp.get().getString(R.string.time)
    };
    // topic search类型
    public static final int LIST_TOPIC_ALL = 0;
    public static final int LIST_TOPIC_OFFICIAL = 1;
    public static final int LIST_TOPIC_WELL = 2;
    public static final int[] LIST_TOPIC_TYPE = new int[]{
            LIST_TOPIC_ALL,
            LIST_TOPIC_OFFICIAL,
            LIST_TOPIC_WELL
    };
    public static final String[] LIST_TOPIC_SHOW = new String[]{
            MyApp.get().getString(R.string.all),
            MyApp.get().getString(R.string.official),
            MyApp.get().getString(R.string.well)
    };
    // bill
    public static final int BILL_PAY_PLATFORM_ALI = 100;
    public static final int BILL_PAY_PLATFORM_WX = 200;
    public static final int BILL_GOODS_VIP_1 = 1101;
    public static final int BILL_GOODS_VIP_2 = 1201;
    public static final int BILL_GOODS_VIP_3 = 1301;
    public static final int BILL_GOODS_COIN_1 = 2101;
    public static final int BILL_GOODS_COIN_2 = 2201;
    public static final int BILL_GOODS_COIN_3 = 2301;
    // match search类型
    private static final int LIST_MATCH_ORDER_COIN = 0;
    private static final int LIST_MATCH_ORDER_POINT = 1;
    private static final int LIST_MATCH_ORDER_NEW = 2;
    public static final int[] LIST_MATCH_ORDER_TYPE = new int[]{
            LIST_MATCH_ORDER_COIN,
            LIST_MATCH_ORDER_POINT,
            LIST_MATCH_ORDER_NEW
    };
    public static final String[] LIST_MATCH_ORDER_SHOW = new String[]{
            MyApp.get().getString(R.string.coin_board),
            MyApp.get().getString(R.string.point_board),
            MyApp.get().getString(R.string.new_board)
    };

}
