package com.jiangzg.mianmian.helper;

import com.jiangzg.base.common.ConstantUtils;

/**
 * Created by Jiang on 5/26/16.
 * 常量类
 */
public class ConsHelper {

    // imgLimit
    public static final int UPLOAD_IMAGE_SIZE_MAX = ConstantUtils.MB * 10;
    // requestCode
    public static final int REQUEST_APP_INFO = 1001;
    public static final int REQUEST_DEVICE_INFO = 1002;
    public static final int REQUEST_CAMERA = 1003;
    public static final int REQUEST_PICTURE = 1004;
    public static final int REQUEST_CROP = 1005;
    public static final int REQUEST_INSTALL = 1006;
    public static final int REQUEST_CONTEXT_ALERT = 1007;
    public static final int REQUEST_SCAN = 1008;
    public static final int REQUEST_CONTACT = 1009;
    public static final int REQUEST_LOCATION = 1010;
    public static final int REQUEST_BOOK_PICTURE = 1011;
    // RxBus.ID
    public static final int EVENT_MAP_SELECT = 1; // 选择
    public static final int EVENT_ALBUM_SELECT = 2;
    public static final int EVENT_PICTURE_SELECT = 3;
    public static final int EVENT_SUGGEST_LIST_REFRESH = 100; // 建议
    public static final int EVENT_SUGGEST_LIST_ITEM_DELETE = 101;
    public static final int EVENT_SUGGEST_LIST_ITEM_REFRESH = 102;
    public static final int EVENT_SUGGEST_DETAIL_REFRESH = 103;
    public static final int EVENT_USER_REFRESH = 201; // 用户
    public static final int EVENT_COUPLE_REFRESH = 250; // 配对
    public static final int EVENT_WALL_PAPER_REFRESH = 251; // 墙纸
    public static final int EVENT_DIARY_LIST_REFRESH = 300; // 日记
    public static final int EVENT_DIARY_LIST_ITEM_DELETE = 301;
    public static final int EVENT_DIARY_LIST_ITEM_REFRESH = 302;
    public static final int EVENT_DIARY_DETAIL_REFRESH = 303;
    public static final int EVENT_WORD_LIST_ITEM_DELETE = 304; // 留言
    public static final int EVENT_ALBUM_LIST_REFRESH = 305; // 相册
    public static final int EVENT_ALBUM_LIST_ITEM_REFRESH = 306;
    public static final int EVENT_PICTURE_LIST_REFRESH = 307; // 照片
    public static final int EVENT_PICTURE_LIST_ITEM_REFRESH = 308;
    public static final int EVENT_PICTURE_LIST_ITEM_DELETE = 309;
    public static final int EVENT_DREAM_LIST_REFRESH = 310; // 梦境
    public static final int EVENT_DREAM_LIST_ITEM_DELETE = 311;
    public static final int EVENT_DREAM_LIST_ITEM_REFRESH = 312;
    public static final int EVENT_DREAM_DETAIL_REFRESH = 313;
    public static final int EVENT_GIFT_LIST_REFRESH = 314; // 礼物
    public static final int EVENT_GIFT_LIST_ITEM_DELETE = 315;
    public static final int EVENT_GIFT_LIST_ITEM_REFRESH = 316;
    public static final int EVENT_PROMISE_LIST_REFRESH = 317; // 承诺
    public static final int EVENT_PROMISE_LIST_ITEM_DELETE = 318;
    public static final int EVENT_PROMISE_LIST_ITEM_REFRESH = 319;
    public static final int EVENT_PROMISE_DETAIL_REFRESH = 320;

}
