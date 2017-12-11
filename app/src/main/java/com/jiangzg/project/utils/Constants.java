package com.jiangzg.project.utils;

import com.android.base.string.ConstantUtils;

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
    public static final int REQUEST_CAMERA = 191;  // 相机
    public static final int REQUEST_PICTURE = 192;  // 图库
    public static final int REQUEST_CROP = 193;  // 裁剪
    public static final int REQUEST_SCAN = 194;  // 扫描
    // RxBus.ID
    public static final int EVENT_COMMON = 1;

}
