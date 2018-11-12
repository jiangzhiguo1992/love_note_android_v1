package com.jiangzg.lovenote.helper;

/**
 * Created by Jiang on 5/26/16.
 * 常量类
 */
public class ConsHelper {

    // activityFrom
    public static final int ACT_LIST_FROM_BROWSE = 0;
    public static final int ACT_LIST_FROM_SELECT = 1;
    public static final int ACT_LIST_FROM_SELECT_ALBUM = 2;
    public static final int ACT_LIST_FROM_SELECT_PICTURE = 3;
    public static final int ACT_EDIT_FROM_ADD = 0;
    public static final int ACT_EDIT_FROM_UPDATE = 1;
    public static final int ACT_DETAIL_FROM_ID = 0;
    public static final int ACT_DETAIL_FROM_OBJ = 1;
    // requestCode
    public static final int REQUEST_APP_INFO = 1001;
    public static final int REQUEST_DEVICE_INFO = 1002;
    public static final int REQUEST_PICTURE = 1003;
    public static final int REQUEST_AUDIO = 1004;
    public static final int REQUEST_VIDEO = 1005;
    public static final int REQUEST_CAMERA = 1006;
    public static final int REQUEST_CROP = 1007;
    public static final int REQUEST_INSTALL = 1008;
    public static final int REQUEST_CONTEXT_ALERT = 1009;
    public static final int REQUEST_SCAN = 1010;
    public static final int REQUEST_CONTACT = 1011;
    public static final int REQUEST_LOCATION = 1012;
    public static final int REQUEST_NOTE_PICTURE = 1013;
    // RxBus.ID
    public static final int EVENT_MAP_SELECT = 101; // 选择
    public static final int EVENT_VIDEO_SELECT = 102;
    public static final int EVENT_ALBUM_SELECT = 103;
    public static final int EVENT_PICTURE_SELECT = 104;
    public static final int EVENT_DIARY_SELECT = 105;
    public static final int EVENT_AWARD_RULE_SELECT = 106;
    public static final int EVENT_TRAVEL_SELECT = 107;
    public static final int EVENT_GIFT_SELECT = 108;
    public static final int EVENT_FOOD_SELECT = 109;
    public static final int EVENT_PROMISE_SELECT = 110;
    public static final int EVENT_MOVIE_SELECT = 111;
    public static final int EVENT_PAY_WX_RESULT = 310;
    public static final int EVENT_SUGGEST_LIST_REFRESH = 1000; // 建议
    public static final int EVENT_SUGGEST_LIST_ITEM_DELETE = 1001;
    public static final int EVENT_SUGGEST_LIST_ITEM_REFRESH = 1002;
    public static final int EVENT_SUGGEST_DETAIL_REFRESH = 1003;
    public static final int EVENT_USER_REFRESH = 2000; // 用户
    public static final int EVENT_COUPLE_REFRESH = 3000; // 配对
    public static final int EVENT_WALL_PAPER_REFRESH = 3100; // 墙纸
    public static final int EVENT_LOCK_REFRESH = 4010; // 密码锁
    public static final int EVENT_AUDIO_LIST_REFRESH = 4020; // 音频
    public static final int EVENT_AUDIO_LIST_ITEM_DELETE = 4021;
    public static final int EVENT_VIDEO_LIST_REFRESH = 4030; // 视频
    public static final int EVENT_VIDEO_LIST_ITEM_DELETE = 4031;
    public static final int EVENT_ALBUM_LIST_REFRESH = 4040; // 相册
    public static final int EVENT_ALBUM_LIST_ITEM_REFRESH = 4041;
    public static final int EVENT_ALBUM_DETAIL_REFRESH = 4042;
    public static final int EVENT_PICTURE_LIST_REFRESH = 4050; // 照片
    public static final int EVENT_PICTURE_LIST_ITEM_REFRESH = 4051;
    public static final int EVENT_PICTURE_LIST_ITEM_DELETE = 4052;
    public static final int EVENT_SOUVENIR_LIST_REFRESH = 4060; // 纪念日
    public static final int EVENT_SOUVENIR_LIST_ITEM_DELETE = 4061;
    public static final int EVENT_SOUVENIR_LIST_ITEM_REFRESH = 4062;
    public static final int EVENT_SOUVENIR_DETAIL_REFRESH = 4063;
    public static final int EVENT_WORD_LIST_ITEM_DELETE = 4070; // 留言
    public static final int EVENT_DIARY_LIST_REFRESH = 4080; // 日记
    public static final int EVENT_DIARY_LIST_ITEM_DELETE = 4081;
    public static final int EVENT_DIARY_LIST_ITEM_REFRESH = 4082;
    public static final int EVENT_DIARY_DETAIL_REFRESH = 4083;
    public static final int EVENT_AWARD_LIST_REFRESH = 4090; // 奖励
    public static final int EVENT_AWARD_LIST_ITEM_DELETE = 4091;
    public static final int EVENT_AWARD_RULE_LIST_REFRESH = 4092;
    public static final int EVENT_AWARD_RULE_LIST_ITEM_DELETE = 4093;
    public static final int EVENT_DREAM_LIST_REFRESH = 4100; // 梦境
    public static final int EVENT_DREAM_LIST_ITEM_DELETE = 4101;
    public static final int EVENT_DREAM_LIST_ITEM_REFRESH = 4102;
    public static final int EVENT_DREAM_DETAIL_REFRESH = 4103;
    public static final int EVENT_GIFT_LIST_REFRESH = 4110; // 礼物
    public static final int EVENT_GIFT_LIST_ITEM_DELETE = 4111;
    public static final int EVENT_GIFT_LIST_ITEM_REFRESH = 4112;
    public static final int EVENT_FOOD_LIST_REFRESH = 4120; // 美食
    public static final int EVENT_FOOD_LIST_ITEM_DELETE = 4121;
    public static final int EVENT_FOOD_LIST_ITEM_REFRESH = 4122;
    public static final int EVENT_TRAVEL_LIST_REFRESH = 4130; // 游记
    public static final int EVENT_TRAVEL_LIST_ITEM_DELETE = 4131;
    public static final int EVENT_TRAVEL_LIST_ITEM_REFRESH = 4132;
    public static final int EVENT_TRAVEL_DETAIL_REFRESH = 4133;
    public static final int EVENT_TRAVEL_EDIT_ADD_PLACE = 4134;
    public static final int EVENT_ANGRY_LIST_REFRESH = 4140; // 生气
    public static final int EVENT_ANGRY_LIST_ITEM_DELETE = 4141;
    public static final int EVENT_ANGRY_LIST_ITEM_REFRESH = 4142;
    public static final int EVENT_PROMISE_LIST_REFRESH = 4150; // 承诺
    public static final int EVENT_PROMISE_LIST_ITEM_DELETE = 4151;
    public static final int EVENT_PROMISE_LIST_ITEM_REFRESH = 4152;
    public static final int EVENT_PROMISE_DETAIL_REFRESH = 4153;
    public static final int EVENT_MOVIE_LIST_REFRESH = 4160; // 电影
    public static final int EVENT_MOVIE_LIST_ITEM_DELETE = 4161;
    public static final int EVENT_MOVIE_LIST_ITEM_REFRESH = 4162;
    public static final int EVENT_POST_GO_TOP = 5100;
    public static final int EVENT_POST_SEARCH_ALL = 5101;
    public static final int EVENT_POST_SEARCH_OFFICIAL = 5102;
    public static final int EVENT_POST_SEARCH_WELL = 5103;
    public static final int EVENT_POST_LIST_REFRESH = 5200;
    public static final int EVENT_POST_LIST_ITEM_DELETE = 5201;
    public static final int EVENT_POST_LIST_ITEM_REFRESH = 5202;
    public static final int EVENT_POST_DETAIL_REFRESH = 5203;
    public static final int EVENT_POST_COMMENT_LIST_REFRESH = 5300;
    public static final int EVENT_POST_COMMENT_LIST_ITEM_DELETE = 5301;
    public static final int EVENT_POST_COMMENT_LIST_ITEM_REFRESH = 5302;
    public static final int EVENT_POST_COMMENT_DETAIL_REFRESH = 5303;
    public static final int EVENT_VIP_INFO_REFRESH = 6100;
    public static final int EVENT_COIN_INFO_REFRESH = 6200;

}
