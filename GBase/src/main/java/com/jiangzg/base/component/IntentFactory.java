package com.jiangzg.base.component;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import com.jiangzg.base.application.AppBase;
import com.jiangzg.base.common.FileUtils;
import com.jiangzg.base.common.LogUtils;
import com.jiangzg.base.common.StringUtils;

import java.io.File;

/**
 * Created by gg on 2017/3/13.
 * 原生意图获取
 */
public class IntentFactory {

    /*
     * 1.显式方式 直接设置目标组件的ComponentName，用于一个应用内部的消息传递，比如启动另一个Activity或者一个services。
     *           通过Intent的setComponent和setClass来制定目标组件的ComponentName。
     * 2.隐式方式 ComponentName为空，用于调用其他应用中的组件。
     *           需要包含足够的信息，这样系统才能根据这些信息使用intent filter在所有的组件中
     *           过滤action、data或者category来匹配目标组件。
     */

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
            LogUtils.w(IntentFactory.class, "getComponent", "packageName == null || className == null");
            return null;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        ComponentName cn = new ComponentName(packageName, className);
        return intent.setComponent(cn);
    }

    /**
     * 相册
     */
    @SuppressLint("MissingPermission")
    public static Intent getPicture() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        //Intent intent = new Intent(Intent.ACTION_PICK);
        //intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        return intent;
    }

    /**
     * 音频 *有mp3、wav、mid等
     */
    @SuppressLint("MissingPermission")
    public static Intent getAudio() {
        //Intent intent = new Intent();
        //intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
        //intent.addCategory(Intent.CATEGORY_OPENABLE);
        //intent.setType("audio/*");
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setData(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
        return intent;
    }

    /**
     * 视频 *有mp4、3gp、avi等
     */
    @SuppressLint("MissingPermission")
    public static Intent getVideo() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("video/*");
        //Intent intent = new Intent(Intent.ACTION_PICK);
        //intent.setData(MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        return intent;
    }

    /**
     * 拍照 ,可以自定义保存路径
     */
    @SuppressLint("MissingPermission")
    public static Intent getCamera(File cameraFile) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
        if (cameraFile == null) return intent; // 不加保存路径，图片会被压缩
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, ProviderUtils.getUriByFile(cameraFile));
        } else {
            // android 8.0需要
            ContentValues contentValues = new ContentValues(1);
            contentValues.put(MediaStore.Images.Media.DATA, cameraFile.getAbsolutePath());
            Uri uri = AppBase.getInstance().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        }
        return intent;
    }

    /**
     * 裁剪(通用) 1.启动拍照/相册 2.在onActivityForResult里调用此方法，启动裁剪功能
     */
    public static Intent getImageCrop(File from, File save) {
        return getImageCrop(from, save, 0, 0, 0, 0);
    }

    public static Intent getImageCrop(File from, File save, int aspectX, int aspectY) {
        return getImageCrop(from, save, aspectX, aspectY, 0, 0);
    }

    public static Intent getImageCrop(File from, File save, int aspectX, int aspectY, int outputX, int outputY) {
        if (FileUtils.isFileEmpty(from) || save == null) { // 源文件不存在
            FileUtils.deleteFile(from);
            FileUtils.deleteFile(save);
            LogUtils.w(IntentFactory.class, "getCrop", "from == empty || save == null");
            return null;
        }
        Uri uriFrom = ProviderUtils.getUriByFile(from);
        Uri uriTo = Uri.fromFile(save); // 照片 截取输出的outputUri，只能使用Uri.fromFile，不能用FileProvider
        return getImageCrop(uriFrom, uriTo, aspectX, aspectY, outputX, outputY);
    }

    public static Intent getImageCrop(Uri from, Uri save, int aspectX, int aspectY, int outputX, int outputY) {
        if (from == null || save == null) {
            LogUtils.w(IntentFactory.class, "getCrop", "from == null || save == null");
            return null;
        }
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(from, "image/*");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.putExtra("crop", "true");
        // 裁剪框比例
        if (aspectX != 0 && aspectY != 0) {
            intent.putExtra("aspectX", aspectX);
            intent.putExtra("aspectY", aspectY);
        }
        // 输出图片大小(太大会传输失败)
        if (outputX != 0 && outputY != 0) {
            intent.putExtra("outputX", outputX);
            intent.putExtra("outputY", outputY);
        }
        // 裁剪选项
        intent.putExtra("scale", true);
        intent.putExtra("noFaceDetection", true);
        // 数据返回
        intent.putExtra("return-data", false); // 不从intent里面拿
        intent.putExtra(MediaStore.EXTRA_OUTPUT, save); // 保存到save文件里
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        return intent;
    }

    /**
     * 获取播放video的意图
     */
    public static Intent getVideoPlay(File file) {
        if (FileUtils.isFileEmpty(file)) {
            LogUtils.w(IntentFactory.class, "getVideoPlay", "file == null");
            return null;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri uri = Uri.parse(file.getAbsolutePath());
        intent.setDataAndType(uri, "video/mp4");
        return intent;
    }

    /**
     * 获取打开当前App的意图
     */
    public static Intent getApp(String appPackageName) {
        if (StringUtils.isEmpty(appPackageName)) {
            LogUtils.w(IntentFactory.class, "getApp", "appPackageName == null");
            return null;
        }
        return AppBase.getInstance().getPackageManager().getLaunchIntentForPackage(appPackageName);
    }

    /**
     * 回到Home
     */
    public static Intent getHome() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        return intent;
    }

    /**
     * 获取安装App的意图
     */
    @SuppressLint("MissingPermission")
    public static Intent getInstall(File file) {
        if (FileUtils.isFileEmpty(file)) {
            LogUtils.w(IntentFactory.class, "getInstall", "file == null");
            return null;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        String type;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            type = "application/vnd.android.package-archive";
        } else {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(FileUtils.getFileExtension(file));
        }
        // 高版本uri获取
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(ProviderUtils.getUriByFile(file), type);
        } else {
            intent.setDataAndType(Uri.fromFile(file), type);
        }
        return intent;
    }

    /**
     * 获取分享意图
     */
    public static Intent getShare(String content, File image) {
        if (FileUtils.isFileEmpty(image) || !FileUtils.isFileExists(image)) {
            LogUtils.i(IntentFactory.class, "getShare", "image == empty");
            return getShare(content);
        }
        return getShare(content, Uri.fromFile(image));
    }

    public static Intent getShare(String content, Uri uri) {
        if (uri == null) {
            LogUtils.i(IntentFactory.class, "getShare", "uri == null");
            return getShare(content);
        }
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, content);
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.setType("image/*");
        return intent;
    }

    public static Intent getShare(String content) {
        if (StringUtils.isEmpty(content)) {
            LogUtils.i(IntentFactory.class, "getShare", "content == null");
        }
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, content);
        return intent;
    }

    /**
     * 跳至填充好phoneNumber的拨号界面
     */
    public static Intent getDial(String phoneNumber) {
        return new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber));
    }

    /**
     * 直接拨打phoneNumber
     */
    @SuppressLint("MissingPermission")
    public static Intent getCall(String phoneNumber) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        return intent;
    }

    /**
     * 短信发送界面
     */
    public static Intent getSMS(String phoneNumber, String content) {
        Uri uri = Uri.parse("smsto:" + (TextUtils.isEmpty(phoneNumber) ? "" : phoneNumber));
        Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
        intent.putExtra("sms_body", TextUtils.isEmpty(content) ? "" : content);
        return intent;
    }

    /**
     * 打开手机联系人界面点击联系人后便获取该号码
     * 启动方式 startActivityForResult
     * 获取返回值需要在ContactUtils里有方法获取
     */
    public static Intent getContacts() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_PICK);
        intent.setType("vnd.android.cursor.dir/phone_v2");
        return intent;
    }

    /**
     * 跳转应用市场的意图
     */
    public static Intent getMarket(String marketPkg) {
        String str = "market://details?id=" + AppBase.getInstance().getPackageName();
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(str));
        if (!StringUtils.isEmpty(marketPkg)) {
            intent.setPackage(marketPkg);
        }
        return intent;
    }

    /**
     * 获取打开浏览器的意图
     */
    public static Intent getWebBrowse(String url) {
        if (StringUtils.isEmpty(url)) {
            LogUtils.w(IntentFactory.class, "getWebBrowse", "url == null");
            return null;
        }
        Uri address = Uri.parse(url);
        return new Intent(Intent.ACTION_VIEW, address);
    }

    /**
     * 打开网络设置界面
     */
    public static Intent getNetSettings() {
        return new Intent(Settings.ACTION_SETTINGS);
    }

    /**
     * 获取App系统设置
     */
    public static Intent getSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        return intent.setData(Uri.parse("package:" + AppBase.getInstance().getPackageName()));
    }

    /**
     * 打开Gps设置界面
     */
    public static Intent getGps() {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    /**
     * 跳转到权限设置界面
     */
    public static Intent getPermission() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setData(Uri.fromParts("package", AppBase.getInstance().getPackageName(), null));
        return intent;
    }
}
