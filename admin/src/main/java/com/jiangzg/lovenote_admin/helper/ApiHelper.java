package com.jiangzg.lovenote_admin.helper;

import com.jiangzg.lovenote_admin.R;
import com.jiangzg.lovenote_admin.base.MyApp;

/**
 * Created by JZG on 2018/3/27.
 * api辅助类
 */
public class ApiHelper {

    // user登录类型
    public static final int LOG_PWD = 1;
    public static final int LOG_VER = 2;
    // user修改类型
    public static final int MODIFY_FORGET = 1;
    public static final int MODIFY_PASSWORD = 2;
    public static final int MODIFY_PHONE = 3;
    public static final int MODIFY_INFO = 4;
    // cp修改类型
    public static final int COUPLE_UPDATE_GOOD = 1; // 更好
    public static final int COUPLE_UPDATE_BAD = 2; // 更坏
    public static final int COUPLE_UPDATE_INFO = 3;// 信息
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
    // note search类型
    private static final int LIST_NOTE_CP = 0;
    public static final int LIST_NOTE_MY = 1;
    public static final int LIST_NOTE_TA = 2;
    public static final int[] LIST_NOTE_TYPE = new int[]{
            LIST_NOTE_CP,
            LIST_NOTE_MY,
            LIST_NOTE_TA
    };
    public static final String[] LIST_NOTE_SHOW = new String[]{
            MyApp.get().getString(R.string.we_de),
            MyApp.get().getString(R.string.me_de),
            MyApp.get().getString(R.string.ta_de)
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
