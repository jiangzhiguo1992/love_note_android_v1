package com.android.base.file;

import android.app.ActivityManager;
import android.os.Environment;

import com.android.base.component.application.AppContext;

import java.io.File;

/**
 * Created by gg on 2017/5/16.
 * 存储环境工具类
 */
public class EnvironUtils {

    private static final String LOG_TAG = "EnvironUtils";

    /**
     * **********************************外存**********************************
     * 判断SD卡是否可用
     */
    public static boolean isSDCardEnable() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    /**
     * 获取根目录
     */
    public static String getRootPath() {
        return Environment.getRootDirectory() + File.separator;
    }

    /**
     * 获取SD卡路径 一般是/storage/emulated/0/
     */
    public static String getSDCardPath() {
        return Environment.getExternalStorageDirectory().getPath() + File.separator;
    }

    /**
     * 获取可用的SD卡路径
     */
    public static String getRealSDCardPath() {
        if (isSDCardEnable()) {
            return getSDCardPath();
        } else {
            return getRootPath();
        }
    }

    /**
     * 获取外存使用情况: getRealSDCardFile().getXXXXSpace()
     */
    public static File getRealSDCardFile() {
        File dir = new File(getRealSDCardPath());
        FileUtils.createOrExistsDir(dir);
        return dir;
    }

    /**
     * 获取手机内存信息, 总共运存:totalMem   可用运存:availMem   即临界值threshold
     */
    private static ActivityManager.MemoryInfo getMemoryInfo() {
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        AppContext.getActivityManager().getMemoryInfo(memoryInfo);
        return memoryInfo;
    }

}
