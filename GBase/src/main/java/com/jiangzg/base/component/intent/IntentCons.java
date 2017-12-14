package com.jiangzg.base.component.intent;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.provider.MediaStore;
import android.provider.Settings;

/**
 * Created by gg on 2017/5/9.
 * 意图管理类常量
 */
public class IntentCons {

    /*
     * 1.显式方式 直接设置目标组件的ComponentName，用于一个应用内部的消息传递，比如启动另一个Activity或者一个services。
     *           通过Intent的setComponent和setClass来制定目标组件的ComponentName。
     * 2.隐式方式 ComponentName为空，用于调用其他应用中的组件。
     *           需要包含足够的信息，这样系统才能根据这些信息使用intent filter在所有的组件中
     *           过滤action、data或者category来匹配目标组件。
     */

    /***********************************Action*******************************************/
    // 关机
    public static final String action_boot = Intent.ACTION_BOOT_COMPLETED;
    // 重启
    public static final String action_reboot = Intent.ACTION_REBOOT;
    // 电量
    public static final String action_battery = Intent.ACTION_BATTERY_CHANGED;
    // 拨号
    public static final String action_dial = Intent.ACTION_DIAL;
    // 拨打
    public static final String action_call = Intent.ACTION_CALL;
    // 采摘
    public static final String action_pick = Intent.ACTION_PICK;
    // 文件
    public static final String action_open_document = Intent.ACTION_OPEN_DOCUMENT;
    // 内容
    public static final String action_get_content = Intent.ACTION_GET_CONTENT;
    // 首页
    public static final String action_main = Intent.ACTION_MAIN;
    // view
    public static final String action_view = Intent.ACTION_VIEW;
    // 删除
    public static final String action_delete = Intent.ACTION_DELETE;
    // 发送
    public static final String action_send = Intent.ACTION_SEND;
    // 发送至
    public static final String action_send_to = Intent.ACTION_SENDTO;
    // 设置
    public static final String action_settings = Settings.ACTION_SETTINGS;
    // app设置
    public static final String action_app_settings = Settings.ACTION_APPLICATION_DETAILS_SETTINGS;
    // location设置
    public static final String action_location_settings = Settings.ACTION_LOCATION_SOURCE_SETTINGS;
    // 相机
    public static final String action_capture = MediaStore.ACTION_IMAGE_CAPTURE;
    // 裁剪
    public static final String action_crop = "com.android.camera.action.CROP";
    // 信号
    public static final String action_connectivity = ConnectivityManager.CONNECTIVITY_ACTION;

    /***********************************Extra*******************************************/
    // 文本
    public static final String extra_text = Intent.EXTRA_TEXT;
    // 流
    public static final String extra_stream = Intent.EXTRA_STREAM;
    // 图片输出角度
    public static final String extra_image_orientation = MediaStore.Images.Media.ORIENTATION;
    // 媒体文件输出路径
    public static final String extra_media_output = MediaStore.EXTRA_OUTPUT;
    // 裁剪
    public static final String extra_crop = "crop";
    // 比例长 0：随意
    public static final String extra_aspect_x = "aspectX";
    // 比例宽 0：随意
    public static final String extra_aspect_y = "aspectY";
    // 输出长
    public static final String extra_output_x = "outputX";
    // 输出宽
    public static final String extra_output_y = "outputY";
    // 缩放
    public static final String extra_scale = "scale";
    // 脸部识别
    public static final String extra_no_face = "noFaceDetection";
    // 数据返回
    public static final String extra_return_data = "return-data";
    // 输出格式
    public static final String extra_output_format = "outputFormat";
    // 短信内容
    public static final String extra_sms_body = "sms_body";

    /***********************************Type*******************************************/
    // 图片
    public static final String type_image = "image/*";
    // 图片png
    public static final String type_png = "image/png";
    // 文本
    public static final String type_text = "text/plain";
    // ----
    public static final String type_archive = "application/vnd.android.package-archive";
    // ----
    public static final String type_phone = "vnd.android.cursor.dir/phone_v2";

    /***********************************Category*******************************************/
    // 默认
    public static final String category_default = Intent.CATEGORY_DEFAULT;
    // 启动
    public static final String category_launcher = Intent.CATEGORY_LAUNCHER;
    // 可打开
    public static final String category_openable = Intent.CATEGORY_OPENABLE;
    // 可浏览
    public static final String category_browsable = Intent.CATEGORY_BROWSABLE;
    // 首页
    public static final String category_home = Intent.CATEGORY_HOME;

    /***********************************Flag*******************************************/
    // 新栈
    public static final int flag_new_task = Intent.FLAG_ACTIVITY_NEW_TASK;
    // 重启栈
    public static final int flag_reset_task = Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED;
    // 无过渡动画
    public static final int flag_no_anim = Intent.FLAG_ACTIVITY_NO_ANIMATION;
    // 无历史
    public static final int flag_no_history = Intent.FLAG_ACTIVITY_NO_HISTORY;
    // 单例
    public static final int flag_single_top = Intent.FLAG_ACTIVITY_SINGLE_TOP;
    // 清除栈顶
    public static final int flag_clear_top = Intent.FLAG_ACTIVITY_CLEAR_TOP;


}
