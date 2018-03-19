package com.jiangzg.base.media.image;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;

import com.jiangzg.base.common.ConvertUtils;
import com.jiangzg.base.component.application.AppContext;
import com.jiangzg.base.component.application.AppInfo;
import com.jiangzg.base.file.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

/**
 * Created by jiang on 2016/10/13
 * describe 多媒体里的图片获取工具类
 */
public class BitmapMedia {

    private static final String LOG_TAG = "BitmapMedia";
    private File file;

    /**
     * 获取拍照图片: 在onActivityResult中执行
     */
    public static Bitmap getCameraBitmap(File cameraFile) {
        if (FileUtils.isFileEmpty(cameraFile)) {
            FileUtils.deleteFile(cameraFile); // 删除垃圾文件
            return null;
        }
        Bitmap adjust = adjust(cameraFile);// 摆正角度
        FileUtils.deleteFile(cameraFile); // 删除源文件
        return adjust;
    }

    public static Bitmap getCameraBitmap(File cameraFile, long maxByteSize) {
        if (FileUtils.isFileEmpty(cameraFile)) {
            FileUtils.deleteFile(cameraFile); // 删除垃圾文件
            return null;
        }
        Bitmap small = BitmapCompress.compressBySize(cameraFile, maxByteSize); // File转Bitmap(压缩)
        FileUtils.createFileByDeleteOldFile(cameraFile); // 重置源文件
        BitmapConvert.save(small, cameraFile.getAbsolutePath(), Bitmap.CompressFormat.JPEG, true); // 保存图像
        if (small != null && !small.isRecycled()) small.recycle();
        return getCameraBitmap(cameraFile);
    }

    public static Bitmap getCameraBitmap(File cameraFile, int maxWidth, int maxHeight) {
        if (FileUtils.isFileEmpty(cameraFile)) {
            FileUtils.deleteFile(cameraFile); // 删除垃圾文件
            return null;
        }
        Bitmap small = BitmapConvert.getBitmap(cameraFile, maxWidth, maxHeight); // File转Bitmap(压缩)
        FileUtils.createFileByDeleteOldFile(cameraFile); // 重置源文件
        BitmapConvert.save(small, cameraFile.getAbsolutePath(), Bitmap.CompressFormat.JPEG, true); // 保存图像
        if (small != null && !small.isRecycled()) small.recycle();
        return getCameraBitmap(cameraFile);
    }

    public static File getPictureFile(Intent data) {
        if (data == null) return null;
        Uri uri = getPictureUri(data);
        File file;
        if (uri != null) {
            file = ConvertUtils.URI2File(uri);
        } else {
            long time = new Date().getTime();
            file = new File(AppInfo.get().getAppCacheDir(), time + ".jpg");
            Bitmap picture = data.getParcelableExtra("data");
            BitmapConvert.save(picture, file.getAbsolutePath(), Bitmap.CompressFormat.JPEG, true);
        }
        return file;
    }

    /**
     * 获取相册图片: 在onActivityResult中执行
     */
    public static Bitmap getPictureBitmap(Intent data) {
        if (data == null) return null;
        Bitmap picture = null;
        Uri uri = getPictureUri(data);
        if (uri != null) {
            try {
                InputStream stream = AppContext.get().getContentResolver().openInputStream(uri);
                picture = BitmapFactory.decodeStream(stream);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            picture = data.getParcelableExtra("data");
        }
        return picture;
    }

    public static Bitmap getPictureBitmap(Intent data, long maxByteSize) {
        if (data == null) return null;
        Bitmap picture;
        Uri uri = getPictureUri(data);
        if (uri != null) {
            File file = ConvertUtils.URI2File(uri);
            picture = BitmapCompress.compressBySize(file, maxByteSize);
        } else {
            picture = data.getParcelableExtra("data");
        }
        return picture;
    }

    public static Bitmap getPictureBitmap(Intent data, int maxWidth, int maxHeight) {
        if (data == null) return null;
        Bitmap picture;
        Uri uri = getPictureUri(data);
        if (uri != null) {
            File file = ConvertUtils.URI2File(uri);
            picture = BitmapConvert.getBitmap(file, maxWidth, maxHeight);
        } else {
            picture = data.getParcelableExtra("data");
        }
        return picture;
    }

    /**
     * 获取裁剪图片: 在onActivityResult中执行,记得删除源文件!!
     */
    public static Bitmap getCropBitmap(File cropFile) {
        if (FileUtils.isFileEmpty(cropFile)) {
            FileUtils.deleteFile(cropFile); // 删除垃圾文件
            return null;
        } else {
            return BitmapFactory.decodeFile(cropFile.getAbsolutePath());
        }
    }

    public static Bitmap getCropBitmap(File cropFile, long maxByteSize) {
        if (FileUtils.isFileEmpty(cropFile)) {
            FileUtils.deleteFile(cropFile); // 删除垃圾文件
            return null;
        }
        Bitmap small = BitmapCompress.compressBySize(cropFile, maxByteSize); // File转Bitmap(压缩)
        FileUtils.createFileByDeleteOldFile(cropFile); // 重置源文件
        BitmapConvert.save(small, cropFile.getAbsolutePath(), Bitmap.CompressFormat.JPEG, true); // 保存图像
        if (small != null && !small.isRecycled()) {
            small.recycle();
        }
        return getCropBitmap(cropFile);
    }

    public static Bitmap getCropBitmap(File cropFile, int maxWidth, int maxHeight) {
        if (FileUtils.isFileEmpty(cropFile)) {
            FileUtils.deleteFile(cropFile); // 重置源文件
            return null;
        }
        Bitmap small = BitmapConvert.getBitmap(cropFile, maxWidth, maxHeight); // File转Bitmap(压缩)
        FileUtils.createFileByDeleteOldFile(cropFile); // 删除源文件
        BitmapConvert.save(small, cropFile.getAbsolutePath(), Bitmap.CompressFormat.JPEG, true); // 保存图像
        if (small != null && !small.isRecycled()) {
            small.recycle();
        }
        return getCropBitmap(cropFile);
    }

    /**
     * 获取相册图片: 在onActivityResult中执行 data.getData()
     */
    public static Uri getPictureUri(Intent data) {
        if (data == null) return null;
        Uri uri = data.getData();
        if (uri == null) return null;
        if (uri.getScheme().equals(ContentResolver.SCHEME_FILE)
                && (data.getType() != null && data.getType().contains("image/"))) { //小米的type不是null 其他的是
            String path = uri.getEncodedPath();
            if (path != null) {
                path = Uri.decode(path);
                ContentResolver cr = AppContext.get().getContentResolver();
                String buff = "(" + MediaStore.Images.ImageColumns.DATA + "=" +
                        "'" + path + "'" + ")";
                Cursor cur = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        new String[]{MediaStore.Images.ImageColumns._ID},
                        buff, null, null);
                int index = 0;
                if (cur != null) {
                    for (cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()) {
                        index = cur.getColumnIndex(MediaStore.Images.ImageColumns._ID);
                        index = cur.getInt(index);
                    }
                    cur.close();
                }
                if (index > 0) {
                    Uri uri_temp = Uri.parse("content://media/external/images/media/" + index);
                    if (uri_temp != null) {
                        return uri_temp;
                    }
                }
            }
        }
        return uri;
    }

    /* 摆正图片旋转角度 */
    private static Bitmap adjust(File file) {
        if (FileUtils.isFileEmpty(file)) return null;
        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
        int degree = getRotateDegree(file.getAbsolutePath());
        if (degree != 0) {
            Matrix m = new Matrix();
            m.setRotate(degree);
            Bitmap adjust = Bitmap.createBitmap(bitmap, 0, 0,
                    bitmap.getWidth(), bitmap.getHeight(), m, true);
            if (!bitmap.isRecycled()) {
                bitmap.recycle();
            }
            return adjust;
        } else {
            return bitmap;
        }
    }

    /* 获取图片旋转角度 */
    private static int getRotateDegree(String filePath) {
        if (FileUtils.isFileEmpty(filePath)) return 0;
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(filePath);
            int orientation = exifInterface.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            if (orientation != -1) {
                switch (orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        degree = 90;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        degree = 180;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        degree = 270;
                        break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

}
