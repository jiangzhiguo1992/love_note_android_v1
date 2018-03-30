package com.jiangzg.base.media;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.widget.ImageView;

import com.jiangzg.base.common.FileUtils;
import com.jiangzg.base.common.LogUtils;
import com.jiangzg.base.view.ScreenUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;

/**
 * Created by gg on 2017/4/11.
 * 图片压缩工具类
 */
public class BitmapCompress {

    private static final String LOG_TAG = "BitmapCompress";

    /**
     * 适屏显示image，会向下采样
     */
    public static void showImage(Activity activity, Uri imagePath, ImageView imageView) {
        if (activity == null || imagePath == null || imageView == null) {
            LogUtils.w(LOG_TAG, "showImage: null");
            return;
        }
        DisplayMetrics display = ScreenUtils.getDisplay(activity);
        if (display == null) return;
        // 屏幕像素的绝对宽高
        int width = display.widthPixels;
        int height = display.heightPixels;
        //实现对图片裁剪的类，是匿名内部类
        BitmapFactory.Options options = new BitmapFactory.Options();
        //图片的宽度相对于手机屏幕宽度的比例
        int wRatio = (int) Math.ceil(options.outWidth / (float) width);
        //图片的高度相对于手机屏幕高度的比例
        int hRatio = (int) Math.ceil(options.outHeight / (float) height);
        //如果inSampleSize = 4；那么返回的是源位图的1/4的大小
        if (wRatio > 1 || hRatio > 1) {
            if (wRatio > hRatio) {
                options.inSampleSize = wRatio;
            } else {
                options.inSampleSize = hRatio;
            }
        }
        //如果设置为true，将返回null，设置为false，允许查询图片不用按照像素分配给内存
        options.inJustDecodeBounds = false;
        //使用BitmapFactory对图片进行适屏的操作,第一个参数就InPutStream
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream
                    (activity.getContentResolver().openInputStream(imagePath), null, options);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        imageView.setImageBitmap(bitmap);
    }

    /**
     * 获取bitmap(带压缩) 真正的压缩到200KB左右（建议200KB）
     */
    public static Bitmap compressBySize(File file, long maxByteSize) {
        if (!FileUtils.isFileExists(file)) {
            LogUtils.w(LOG_TAG, "compressBySize: file = null");
            return null;
        }
        String absolutePath = file.getAbsolutePath();
        LogUtils.i(LOG_TAG, "压缩图片文件: " + absolutePath);
        long length = file.length(); // 真正的文件大小
        if (length > maxByteSize) { // 这么算比较准
            int ratio = (int) (length / maxByteSize);
            int sample;
            if (ratio > 4) {
                sample = ratio / 2 + 1;
            } else {
                sample = 4;
            }
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(absolutePath, options);
            options.inSampleSize = sample;
            options.inJustDecodeBounds = false;
            return BitmapFactory.decodeFile(absolutePath, options);
        } else {
            return BitmapFactory.decodeFile(absolutePath);
        }
    }

    /**
     * 按缩放压缩(放大有锯齿)
     *
     * @param src       源图片
     * @param newWidth  新宽度
     * @param newHeight 新高度
     * @param recycle   是否回收
     * @return 缩放压缩后的图片
     */

    public static Bitmap compressByScale(Bitmap src, int newWidth, int newHeight, boolean recycle) {
        return BitmapUtils.scale(src, newWidth, newHeight, recycle);
    }

    /**
     * 按缩放压缩(放大有锯齿)
     *
     * @param src         源图片
     * @param scaleWidth  缩放宽度倍数
     * @param scaleHeight 缩放高度倍数
     * @param recycle     是否回收
     * @return 缩放压缩后的图片
     */
    public static Bitmap compressByScale(Bitmap src, float scaleWidth, float scaleHeight, boolean recycle) {
        return BitmapUtils.scale(src, scaleWidth, scaleHeight, recycle);
    }

    /**
     * 按质量压缩
     *
     * @param src     源图片
     * @param quality 质量
     * @param recycle 是否回收
     * @return 质量压缩后的图片
     */
    public static Bitmap compressByQuality(Bitmap src, int quality, boolean recycle) {
        if (BitmapUtils.isEmptyBitmap(src) || quality < 0 || quality > 100) {
            LogUtils.w(LOG_TAG, "compressByQuality: src == null");
            return null;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, quality, baos);
        byte[] bytes = baos.toByteArray();
        if (recycle && !src.isRecycled()) src.recycle();
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    /**
     * 按采样大小压缩
     *
     * @param src        源图片
     * @param sampleSize 采样率大小
     * @param recycle    是否回收
     * @return 按采样率压缩后的图片
     */
    public static Bitmap compressBySampleSize(Bitmap src, int sampleSize, boolean recycle) {
        if (BitmapUtils.isEmptyBitmap(src)) {
            LogUtils.w(LOG_TAG, "compressBySampleSize: src == null");
            return null;
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = sampleSize;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] bytes = baos.toByteArray();
        if (recycle && !src.isRecycled()) src.recycle();
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
    }

}
