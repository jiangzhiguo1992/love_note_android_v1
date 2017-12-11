package com.android.base.file;

import android.app.ActivityManager;
import android.os.Build;
import android.os.Environment;

import com.android.base.component.application.AppContext;

import java.io.File;

/**
 * Created by gg on 2017/5/16.
 * 存储环境工具类
 */
public class EnvironUtils {

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

    public static File getRealSDCardFile() {
        File dir = new File(getRealSDCardPath());
        FileUtils.createOrExistsDir(dir);
        return dir;
    }

    /**
     * 外存总共空间
     */
    public static long getExternalTotal() {
        return getRealSDCardFile().getTotalSpace();
    }

    /**
     * 外存使用空间
     */
    public static long getExternalUsable() {
        return getRealSDCardFile().getUsableSpace();
    }

    /**
     * 外存剩余空间
     */
    public static long getExternalFree() {
        return getRealSDCardFile().getFreeSpace();
    }

    /**
     * **********************************内存**********************************
     * 获取手机内存信息
     */
    private static ActivityManager.MemoryInfo getMemoryInfo() {
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        AppContext.getActivityManager().getMemoryInfo(memoryInfo);
        return memoryInfo;
    }

    /**
     * 获取总共运存
     */
    public static long getMemoryTotal() {
        long totalMem = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            totalMem = getMemoryInfo().totalMem;
        }
        return totalMem;
    }

    /**
     * 获取可用运存
     */
    public static long getMemoryAvail() {
        return getMemoryInfo().availMem;
    }

    /**
     * 系统内存不足的阀值，即临界值
     */
    public static long getMemoryThreshold() {
        return getMemoryInfo().threshold;
    }

    /**
     * 如果当前可用内存 <= threshold，该值为真
     */
    public static boolean isLowMemory() {
        return getMemoryAvail() <= getMemoryThreshold();
    }

}
