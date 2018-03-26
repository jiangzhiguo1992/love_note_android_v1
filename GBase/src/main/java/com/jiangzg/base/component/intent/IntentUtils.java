package com.jiangzg.base.component.intent;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.jiangzg.base.common.ConvertUtils;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.component.application.AppContext;
import com.jiangzg.base.file.FileUtils;

import java.io.File;

/**
 * Created by gg on 2017/3/13.
 * 原生意图获取
 */
public class IntentUtils {

    private static final String LOG_TAG = "IntentUtils";

    /**
     * activity跳转intent,也可以setClass来设置
     *
     * @param packageName app包名
     * @param className   Activity全名
     * @param bundle      可为null
     * @return 直接startActivity即可
     */
    private static Intent getComponent(String packageName, String className, Bundle bundle) {
        if (StringUtils.isEmpty(packageName) || StringUtils.isEmpty(className)) {
            Log.e(LOG_TAG, "getComponent: packageName == null || className == null");
            return null;
        }
        Intent intent = new Intent(IntentCons.action_view);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        ComponentName cn = new ComponentName(packageName, className);
        return intent.setComponent(cn);
    }

    /**
     * 拍照 ,不加保存路径，图片会被压缩
     */
    @SuppressLint("MissingPermission")
    public static Intent getCamera(File cameraFile) {
        Intent intent = new Intent(IntentCons.action_capture);
        intent.putExtra(IntentCons.extra_image_orientation, 0);
        if (cameraFile == null) return intent;
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            intent.putExtra(IntentCons.extra_media_output, Uri.fromFile(cameraFile));
        } else {
            ContentValues contentValues = new ContentValues(1);
            contentValues.put(MediaStore.Images.Media.DATA, cameraFile.getAbsolutePath());
            Uri uri = AppContext.get().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
            intent.putExtra(IntentCons.extra_media_output, uri);
        }
        return intent;
    }

    /**
     * 相册 ,可以自定义保存路径
     */
    public static Intent getPicture() {
        Intent intent = new Intent();
        intent.setAction(IntentCons.action_open_document);
        intent.addCategory(IntentCons.category_openable);
        if (intent.resolveActivity(AppContext.get().getPackageManager()) == null) {
            intent.setAction(IntentCons.action_get_content); //4.4以下也走这个方法
        }
        intent.setType(IntentCons.type_image);
        return intent;
    }

    /**
     * 裁剪(通用) 1.启动拍照/相册 2.在onActivityForResult里调用此方法，启动裁剪功能
     *
     * @param from    源文件
     * @param save    保存文件
     * @param aspectX 比例长 0：随意
     * @param aspectY 比例宽 0：随意
     * @param outputX 输出长
     * @param outputY 输出宽
     * @return intent
     */
    public static Intent getCrop(File from, File save, int aspectX, int aspectY, int outputX, int outputY) {
        if (FileUtils.isFileEmpty(from)) { // 源文件不存在
            FileUtils.deleteFile(from);
            FileUtils.deleteFile(save);
            Log.e(LOG_TAG, "getCrop-->from/save == empty");
            return null;
        }
        Uri uriFrom = ConvertUtils.File2URI(from);
        Uri uriTo = ConvertUtils.File2URI(save);
        return getCrop(uriFrom, uriTo, aspectX, aspectY, outputX, outputY);
    }

    public static Intent getCrop(Uri from, Uri save, int aspectX, int aspectY, int outputX, int outputY) {
        if (from == null || save == null) {
            Log.e(LOG_TAG, "getCrop: from == null || save == null");
            return null;
        }
        Intent intent = new Intent(IntentCons.action_crop);
        intent.setDataAndType(from, IntentCons.type_image);
        intent.putExtra(IntentCons.extra_crop, "true");
        // 裁剪框比例
        if (aspectX != 0 && aspectY != 0) {
            intent.putExtra(IntentCons.extra_aspect_x, aspectX);
            intent.putExtra(IntentCons.extra_aspect_y, aspectY);
        }
        // 输出图片大小(太大会传输失败)
        if (outputX != 0 && outputY != 0) {
            intent.putExtra(IntentCons.extra_output_x, outputX);
            intent.putExtra(IntentCons.extra_output_y, outputY);
        }
        // 裁剪选项
        intent.putExtra(IntentCons.extra_scale, true);
        intent.putExtra(IntentCons.extra_no_face, true);
        // 数据返回
        intent.putExtra(IntentCons.extra_return_data, false); // 不从intent里面拿
        intent.putExtra(IntentCons.extra_media_output, save);
        intent.putExtra(IntentCons.extra_output_format, Bitmap.CompressFormat.JPEG.toString());
        return intent;
    }

    /**
     * 获取打开当前App的意图
     */
    public static Intent getApp(String appPackageName) {
        if (StringUtils.isEmpty(appPackageName)) {
            Log.e(LOG_TAG, "getApp: appPackageName == null");
            return null;
        }
        return AppContext.getPackageManager().getLaunchIntentForPackage(appPackageName);
    }

    /**
     * 回到Home
     */
    public static Intent getHome() {
        Intent intent = new Intent(IntentCons.action_main);
        intent.addCategory(IntentCons.category_home);
        intent.addFlags(IntentCons.flag_new_task | IntentCons.flag_reset_task);
        return intent;
    }

    /**
     * 获取安装App的意图
     */
    public static Intent getInstall(File file) {
        if (FileUtils.isFileEmpty(file)) {
            Log.e(LOG_TAG, "getInstall: file == null");
            return null;
        }
        Intent intent = new Intent(IntentCons.action_view);
        intent.addCategory(IntentCons.category_default);
        intent.addFlags(IntentCons.flag_new_task);
        String type;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            type = IntentCons.type_archive;
        } else {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(FileUtils.getFileExtension(file));
        }
        return intent.setDataAndType(Uri.fromFile(file), type);
    }

    /**
     * 获取分享意图
     */
    public static Intent getShare(String content, File image) {
        if (StringUtils.isEmpty(content) || FileUtils.isFileEmpty(image)) {
            Log.e(LOG_TAG, "getShare: content == null || image == null");
            return null;
        }
        if (!FileUtils.isFileExists(image)) return null;
        return getShare(content, Uri.fromFile(image));
    }

    public static Intent getShare(String content, Uri uri) {
        if (StringUtils.isEmpty(content) || uri == null) {
            Log.e(LOG_TAG, "getShare: content == null || uri == null");
            return null;
        }
        Intent intent = new Intent(IntentCons.action_send);
        intent.putExtra(IntentCons.extra_text, content);
        intent.putExtra(IntentCons.extra_stream, uri);
        intent.setType(IntentCons.type_image);
        return intent;
    }

    public static Intent getShare(String content) {
        if (StringUtils.isEmpty(content)) {
            Log.e(LOG_TAG, "getShare: content == null");
            return null;
        }
        Intent intent = new Intent(IntentCons.action_send);
        intent.setType(IntentCons.type_text);
        intent.putExtra(IntentCons.extra_text, content); // 设置分享信息
        return intent;
    }

    /**
     * 跳至填充好phoneNumber的拨号界面
     */
    public static Intent getDial(String phoneNumber) {
        return new Intent(IntentCons.action_dial, Uri.parse("tel:" + phoneNumber));
    }

    /**
     * 直接拨打phoneNumber
     */
    @SuppressLint("MissingPermission")
    public static Intent getCall(String phoneNumber) {
        Intent intent = new Intent();
        intent.setAction(IntentCons.action_call);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        return intent;
    }

    /**
     * 短信发送界面
     */
    public static Intent getSMS(String phoneNumber, String content) {
        Uri uri = Uri.parse("smsto:" + (TextUtils.isEmpty(phoneNumber) ? "" : phoneNumber));
        Intent intent = new Intent(IntentCons.action_send_to, uri);
        intent.putExtra(IntentCons.extra_sms_body, TextUtils.isEmpty(content) ? "" : content);
        return intent;
    }

    /**
     * 打开手机联系人界面点击联系人后便获取该号码
     * 启动方式 startActivityForResult
     * 获取返回值需要在ContactUtils里有方法获取
     */
    public static Intent getContacts() {
        Intent intent = new Intent();
        intent.setAction(IntentCons.action_pick);
        intent.setType(IntentCons.type_phone);
        return intent;
    }

    /**
     * 跳转应用市场的意图
     */
    public static Intent getMarket() {
        String str = "market://details?id=" + AppContext.get().getPackageName();
        return new Intent(IntentCons.action_view, Uri.parse(str));
    }

    /**
     * 获取打开浏览器的意图
     */
    public static Intent getWebBrowse(String url) {
        if (StringUtils.isEmpty(url)) {
            Log.e(LOG_TAG, "getWebBrowse: url == null");
            return null;
        }
        Uri address = Uri.parse(url);
        return new Intent(IntentCons.action_view, address);
    }

    /**
     * 打开网络设置界面
     */
    public static Intent getNetSettings() {
        return new Intent(IntentCons.action_settings);
    }

    /**
     * 获取App系统设置
     */
    public static Intent getSetings(String packageName) {
        Intent intent = new Intent(IntentCons.action_app_settings);
        return intent.setData(Uri.parse("package:" + packageName));
    }

    /**
     * 打开Gps设置界面
     */
    public static Intent getGps() {
        Intent intent = new Intent(IntentCons.action_location_settings);
        intent.setFlags(IntentCons.flag_new_task);
        return intent;
    }

}
