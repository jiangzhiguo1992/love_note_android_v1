package com.jiangzg.lovenote_admin.helper;

import com.jiangzg.lovenote_admin.R;
import com.jiangzg.lovenote_admin.base.MyApp;
import com.jiangzg.lovenote_admin.domain.Bill;
import com.jiangzg.lovenote_admin.domain.Coin;
import com.jiangzg.lovenote_admin.domain.Sms;
import com.jiangzg.lovenote_admin.domain.Trends;
import com.jiangzg.lovenote_admin.domain.User;
import com.jiangzg.lovenote_admin.domain.Vip;

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
            "全部",
            Sms.getTypeShow(Sms.TYPE_REGISTER),
            Sms.getTypeShow(Sms.TYPE_LOGIN),
            Sms.getTypeShow(Sms.TYPE_FORGET),
            Sms.getTypeShow(Sms.TYPE_PHONE),
            Sms.getTypeShow(Sms.TYPE_LOCK),
    };
    // user sex
    public static final int[] LIST_USER_SEX_TYPE = new int[]{
            0,
            User.SEX_GIRL,
            User.SEX_BOY,
    };
    public static final String[] LIST_USER_SEX_SHOW = new String[]{
            "全部",
            User.getSexShow(User.SEX_GIRL),
            User.getSexShow(User.SEX_BOY),
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
    public static final String[] LIST_ENTRY_FILED_SHOW = new String[]{
            ENTRY_FILED_DEVICE_NAME,
            ENTRY_FILED_MARKET,
            ENTRY_FILED_PLATFORM,
            ENTRY_FILED_OS_VERSION,
            ENTRY_FILED_APP_VERSION,
    };
    // entry
    public static final String PLACE_FILED_COUNTRY = "country";
    public static final String PLACE_FILED_PROVINCE = "province";
    public static final String PLACE_FILED_CITY = "city";
    public static final String PLACE_FILED_DISTRICT = "district";
    public static final String[] LIST_PLACE_FILED_SHOW = new String[]{
            PLACE_FILED_COUNTRY,
            PLACE_FILED_PROVINCE,
            PLACE_FILED_CITY,
            PLACE_FILED_DISTRICT,
    };
    // bill
    public static final String[] BILL_PLATFORM_OS_SHOW = new String[]{
            "操作系统",
            Bill.BILL_PLATFORM_OS_ANDROID,
            Bill.BILL_PLATFORM_OS_IOS,
    };
    public static final int[] BILL_PLATFORM_PAY_TYPE = new int[]{
            0,
            Bill.BILL_PLATFORM_PAY_ALI,
            Bill.BILL_PLATFORM_PAY_WX,
    };
    public static final String[] BILL_PLATFORM_PAY_SHOW = new String[]{
            "支付平台",
            Bill.getPlatformPayShow(Bill.BILL_PLATFORM_PAY_ALI),
            Bill.getPlatformPayShow(Bill.BILL_PLATFORM_PAY_WX),
    };
    public static final int[] BILL_GOODS_TYPE_TYPE = new int[]{
            0,
            Bill.BILL_GOODS_VIP_1,
            Bill.BILL_GOODS_VIP_2,
            Bill.BILL_GOODS_VIP_3,
            Bill.BILL_GOODS_COIN_1,
            Bill.BILL_GOODS_COIN_2,
            Bill.BILL_GOODS_COIN_3,
    };
    public static final String[] BILL_GOODS_TYPE_SHOW = new String[]{
            "商品类型",
            Bill.getGoodsTypeShow(Bill.BILL_GOODS_VIP_1),
            Bill.getGoodsTypeShow(Bill.BILL_GOODS_VIP_2),
            Bill.getGoodsTypeShow(Bill.BILL_GOODS_VIP_3),
            Bill.getGoodsTypeShow(Bill.BILL_GOODS_COIN_1),
            Bill.getGoodsTypeShow(Bill.BILL_GOODS_COIN_2),
            Bill.getGoodsTypeShow(Bill.BILL_GOODS_COIN_3),
    };
    // vip
    public static final int[] VIP_FROM_TYPE = new int[]{
            0,
            Vip.VIP_FROM_TYPE_SYS_SEND,
            Vip.VIP_FROM_TYPE_USER_BUY,
    };
    public static final String[] VIP_FROM_SHOW = new String[]{
            "全部",
            Vip.getFromTypeShow(Vip.VIP_FROM_TYPE_SYS_SEND),
            Vip.getFromTypeShow(Vip.VIP_FROM_TYPE_USER_BUY),
    };
    // coin
    public static final int[] COIN_KIND_TYPE = new int[]{
            0,
            Coin.COIN_KIND_ADD_BY_SYS,
            Coin.COIN_KIND_ADD_BY_PLAY_PAY,
            Coin.COIN_KIND_ADD_BY_SIGN_DAY,
            Coin.COIN_KIND_ADD_BY_MATCH_POST,
            Coin.COIN_KIND_SUB_BY_MATCH_UP,
            Coin.COIN_KIND_SUB_BY_WISH_UP,
            Coin.COIN_KIND_SUB_BY_PLANE_UP,
    };
    public static final String[] COIN_KIND_SHOW = new String[]{
            "全部",
            Coin.getKindShow(Coin.COIN_KIND_ADD_BY_SYS),
            Coin.getKindShow(Coin.COIN_KIND_ADD_BY_PLAY_PAY),
            Coin.getKindShow(Coin.COIN_KIND_ADD_BY_SIGN_DAY),
            Coin.getKindShow(Coin.COIN_KIND_ADD_BY_MATCH_POST),
            Coin.getKindShow(Coin.COIN_KIND_SUB_BY_MATCH_UP),
            Coin.getKindShow(Coin.COIN_KIND_SUB_BY_WISH_UP),
            Coin.getKindShow(Coin.COIN_KIND_SUB_BY_PLANE_UP),
    };
    // trends
    public static final int[] TRENDS_ACT_TYPE = new int[]{
            0,
            Trends.TRENDS_ACT_TYPE_INSERT,
            Trends.TRENDS_ACT_TYPE_DELETE,
            Trends.TRENDS_ACT_TYPE_UPDATE,
            Trends.TRENDS_ACT_TYPE_QUERY,
    };
    public static final String[] TRENDS_ACT_SHOW = new String[]{
            "全部",
            Trends.getActShow(Trends.TRENDS_ACT_TYPE_INSERT, 0),
            Trends.getActShow(Trends.TRENDS_ACT_TYPE_DELETE, 0),
            Trends.getActShow(Trends.TRENDS_ACT_TYPE_UPDATE, 0),
            Trends.getActShow(Trends.TRENDS_ACT_TYPE_QUERY, 0),
    };
    public static final int[] TRENDS_CON_TYPE = new int[]{
            0,
            Trends.TRENDS_CON_TYPE_SOUVENIR,
            Trends.TRENDS_CON_TYPE_WISH,
            Trends.TRENDS_CON_TYPE_SHY,
            Trends.TRENDS_CON_TYPE_MENSES,
            Trends.TRENDS_CON_TYPE_SLEEP,
            Trends.TRENDS_CON_TYPE_AUDIO,
            Trends.TRENDS_CON_TYPE_VIDEO,
            Trends.TRENDS_CON_TYPE_ALBUM,
            Trends.TRENDS_CON_TYPE_PICTURE,
            Trends.TRENDS_CON_TYPE_WORD,
            Trends.TRENDS_CON_TYPE_WHISPER,
            Trends.TRENDS_CON_TYPE_DIARY,
            Trends.TRENDS_CON_TYPE_AWARD,
            Trends.TRENDS_CON_TYPE_AWARD_RULE,
            Trends.TRENDS_CON_TYPE_DREAM,
            Trends.TRENDS_CON_TYPE_FOOD,
            Trends.TRENDS_CON_TYPE_TRAVEL,
            Trends.TRENDS_CON_TYPE_GIFT,
            Trends.TRENDS_CON_TYPE_PROMISE,
            Trends.TRENDS_CON_TYPE_ANGRY,
    };
    public static final String[] TRENDS_CON_SHOW = new String[]{
            "全部",
            Trends.getContentShow(Trends.TRENDS_CON_TYPE_SOUVENIR),
            Trends.getContentShow(Trends.TRENDS_CON_TYPE_WISH),
            Trends.getContentShow(Trends.TRENDS_CON_TYPE_SHY),
            Trends.getContentShow(Trends.TRENDS_CON_TYPE_MENSES),
            Trends.getContentShow(Trends.TRENDS_CON_TYPE_SLEEP),
            Trends.getContentShow(Trends.TRENDS_CON_TYPE_AUDIO),
            Trends.getContentShow(Trends.TRENDS_CON_TYPE_VIDEO),
            Trends.getContentShow(Trends.TRENDS_CON_TYPE_ALBUM),
            Trends.getContentShow(Trends.TRENDS_CON_TYPE_PICTURE),
            Trends.getContentShow(Trends.TRENDS_CON_TYPE_WORD),
            Trends.getContentShow(Trends.TRENDS_CON_TYPE_WHISPER),
            Trends.getContentShow(Trends.TRENDS_CON_TYPE_DIARY),
            Trends.getContentShow(Trends.TRENDS_CON_TYPE_AWARD),
            Trends.getContentShow(Trends.TRENDS_CON_TYPE_AWARD_RULE),
            Trends.getContentShow(Trends.TRENDS_CON_TYPE_DREAM),
            Trends.getContentShow(Trends.TRENDS_CON_TYPE_FOOD),
            Trends.getContentShow(Trends.TRENDS_CON_TYPE_TRAVEL),
            Trends.getContentShow(Trends.TRENDS_CON_TYPE_GIFT),
            Trends.getContentShow(Trends.TRENDS_CON_TYPE_PROMISE),
            Trends.getContentShow(Trends.TRENDS_CON_TYPE_ANGRY),
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
