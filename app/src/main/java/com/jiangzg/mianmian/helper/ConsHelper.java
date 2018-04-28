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
    public static final int EVENT_USER_REFRESH = 1; // 用户
    public static final int EVENT_COUPLE_REFRESH = 2; // 配对
    public static final int EVENT_SUGGEST_LIST_REFRESH = 3; // 建议
    public static final int EVENT_SUGGEST_LIST_ITEM_DELETE = 4;
    public static final int EVENT_SUGGEST_LIST_ITEM_REFRESH = 5;
    public static final int EVENT_LOCATION_REFRESH = 6; // 地址
    public static final int EVENT_WALL_PAPER_REFRESH = 7; // 墙纸
    public static final int EVENT_DIARY_LIST_REFRESH = 8; // 日记
    public static final int EVENT_DIARY_LIST_ITEM_DELETE = 9;
    public static final int EVENT_DIARY_LIST_ITEM_REFRESH = 10;
    public static final int EVENT_DIARY_REFRESH = 11;
    public static final int EVENT_WORD_LIST_ITEM_DELETE = 12; // 留言

    // DetailDataType
    public static final int DETAIL_DATA_FROM_NONE = 0;
    public static final int DETAIL_DATA_FROM_ID = 1;
    public static final int DETAIL_DATA_FROM_ALL = 2;

}
