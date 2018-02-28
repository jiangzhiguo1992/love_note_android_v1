package com.jiangzg.ita.utils;

import com.jiangzg.base.common.ConstantUtils;

/**
 * Created by Jiang on 5/26/16.
 * 常量类
 */
public class Constants {
    // 测试模式(上线为false)
    public static boolean DEBUG = true;
    // imgFileSize
    public static final long IMG_SIZE = ConstantUtils.KB * 200; // 图片最大尺寸
    // requestCode
    public static final int REQUEST_CAMERA = 1001;  // 相机
    public static final int REQUEST_PICTURE = 1002;  // 图库
    public static final int REQUEST_CROP = 1003;  // 裁剪
    public static final int REQUEST_SCAN = 1004;  // 扫描
    public static final int REQUEST_PERM_ALERT = 1005;  // perm
    public static final int REQUEST_APP_INFO = 1006;  // app
    public static final int REQUEST_DEVICE_INFO = 1007;  // device
    // RxBus.ID
    public static final int EVENT_COMMON = 0;
    public static final int EVENT_LOGIN_NO = 1;
    public static final int EVENT_LOGIN_YES = 2;
    public static final int EVENT_COUPLE_NO = 3;
    public static final int EVENT_COUPLE_YES = 4;
    // domain status


}
