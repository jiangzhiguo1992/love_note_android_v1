package com.jiangzg.base.application;

import android.annotation.SuppressLint;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.drawable.Drawable;
import android.os.Environment;

import com.jiangzg.base.common.EncryptUtils;
import com.jiangzg.base.common.FileUtils;
import com.jiangzg.base.common.LogUtils;
import com.jiangzg.base.common.StringUtils;

import java.io.File;
import java.util.Arrays;

/**
 * Created by JiangZhiGuo on 2016/10/12.
 * describe  App信息
 */
public class AppInfo {

    private static final String LOG_TAG = "AppInfo";
    private static AppInfo instance;

    private String name; // APP名称
    private Drawable icon; // 图标
    private String packageName; // 包名
    private String packagePath; // 包路径
    private String versionName; // 版本
    private int versionCode; // 版本
    private boolean isSystem; // 是否是系统级别
    private Signature[] signature; // 签名
    private String SHA1; // 地图的sha1值
    private String appFilesDir; // /data/user/0/packageName/files
    private String appCacheDir; // /data/user/0/packageName/cache
    private String resFilesDir; // /storage/emulated/0/Android/data/packageName/files
    private String resCacheDir; // /storage/emulated/0/Android/data/packageName/cache

    /**
     * 获取当前App信息，4个Dir需要权限
     */
    @SuppressLint({"PackageManagerGetSignatures"})
    public static AppInfo get() {
        if (instance == null) {
            instance = new AppInfo();
        } else {
            return instance;
        }
        String packageName = AppBase.getInstance().getPackageName();
        PackageManager pm = AppBase.getInstance().getPackageManager();
        try { // packageName可换成其他的app包名
            PackageInfo pi = pm.getPackageInfo(packageName, 0);
            if (pi != null) {
                instance.setPackageName(pi.packageName);
                instance.setVersionCode(pi.versionCode);
                instance.setVersionName(pi.versionName);
                ApplicationInfo ai = pi.applicationInfo;
                if (ai != null) {
                    boolean isSystem = (ApplicationInfo.FLAG_SYSTEM & ai.flags) == ApplicationInfo.FLAG_SYSTEM;
                    instance.setSystem(isSystem);
                    instance.setName(ai.loadLabel(pm).toString());
                    instance.setIcon(ai.loadIcon(pm));
                    instance.setPackagePath(ai.sourceDir);
                }
            }
            PackageInfo piSign = pm.getPackageInfo(packageName, PackageManager.GET_SIGNATURES);
            if (piSign != null) {
                Signature[] signatures = piSign.signatures;
                instance.setSignature(signatures);
                if (signatures.length > 0) {
                    String sha1 = EncryptUtils.encryptSHA1ToString(signatures[0].toByteArray()).
                            replaceAll("(?<=[0-9A-F]{2})[0-9A-F]{2}", ":$0");
                    instance.setSHA1(sha1);
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return instance;
    }

    public void setSHA1(String SHA1) {
        this.SHA1 = SHA1;
    }

    public String getSHA1() {
        LogUtils.d(LOG_TAG, "getSHA1: " + SHA1);
        return SHA1;
    }

    public void setSignature(Signature[] signature) {
        this.signature = signature;
    }

    public Signature[] getSignature() {
        LogUtils.d(LOG_TAG, "getSignature: " + Arrays.toString(signature));
        return signature;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getPackageName() {
        LogUtils.d(LOG_TAG, "getPackageName: " + packageName);
        return packageName;
    }

    public void setPackagePath(String packagePath) {
        this.packagePath = packagePath;
    }

    public String getPackagePath() {
        LogUtils.d(LOG_TAG, "getPackagePath: " + packagePath);
        return packagePath;
    }

    public void setSystem(boolean system) {
        isSystem = system;
    }

    public boolean isSystem() {
        LogUtils.d(LOG_TAG, "isSystem: " + isSystem);
        return isSystem;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        LogUtils.d(LOG_TAG, "getName: " + name);
        return name;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public int getVersionCode() {
        LogUtils.d(LOG_TAG, "getVersionCode: " + versionCode);
        return versionCode;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getVersionName() {
        LogUtils.d(LOG_TAG, "getVersionNam: " + versionName);
        return versionName;
    }

    @SuppressLint("MissingPermission")
    public String getAppFilesDir() {
        if (!StringUtils.isEmpty(appFilesDir)) {
            LogUtils.d(LOG_TAG, "getAppFilesDir: " + appFilesDir);
            return appFilesDir;
        }
        File filesDir = AppBase.getInstance().getFilesDir();
        appFilesDir = filesDir.getAbsolutePath();
        LogUtils.d(LOG_TAG, "getAppFilesDir: " + appFilesDir);
        return appFilesDir;
    }

    @SuppressLint("MissingPermission")
    public String getAppCacheDir() {
        if (!StringUtils.isEmpty(appCacheDir)) {
            LogUtils.d(LOG_TAG, "getAppCacheDir: " + appCacheDir);
            return appCacheDir;
        }
        File cacheDir = AppBase.getInstance().getCacheDir();
        appCacheDir = cacheDir.getAbsolutePath();
        LogUtils.d(LOG_TAG, "getAppCacheDir-->" + appCacheDir);
        return appCacheDir;
    }

    @SuppressLint("MissingPermission")
    public String getResFilesDir() {
        if (!StringUtils.isEmpty(resFilesDir)) {
            LogUtils.d(LOG_TAG, "getResFilesDir: " + resFilesDir);
            return resFilesDir;
        }
        File externalFilesDir = AppBase.getInstance().getExternalFilesDir("");
        if (externalFilesDir != null) {
            resFilesDir = externalFilesDir.getAbsolutePath();
        } else {
            resFilesDir = getRealSDCardPath() + packageName + "/files/";
            FileUtils.createOrExistsDir(resFilesDir); // 并创建
        }
        LogUtils.d(LOG_TAG, "getResFilesDir: " + resFilesDir);
        return resFilesDir;
    }

    @SuppressLint("MissingPermission")
    public String getResCacheDir() {
        if (!StringUtils.isEmpty(resCacheDir)) {
            LogUtils.d(LOG_TAG, "getResCacheDir: " + resCacheDir);
            return resCacheDir;
        }
        File externalCacheDir = AppBase.getInstance().getExternalCacheDir();
        if (externalCacheDir != null) {
            resCacheDir = externalCacheDir.getAbsolutePath();
        } else {
            resCacheDir = getRealSDCardPath() + packageName + "/cache/";
            FileUtils.createOrExistsDir(resCacheDir); // 并创建
        }
        LogUtils.d(LOG_TAG, "getResCacheDir: " + resCacheDir);
        return resCacheDir;
    }

    /**
     * 获取可用的SD卡路径
     */
    public static String getRealSDCardPath() {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            // sdCard 一般是/storage/emulated/0/
            return Environment.getExternalStorageDirectory().getPath() + File.separator;
        } else {
            // root
            return Environment.getRootDirectory() + File.separator;
        }
    }

    @Override
    public String toString() {
        return "AppInfo{" +
                "name='" + name + '\'' +
                ", icon=" + icon +
                ", packageName='" + packageName + '\'' +
                ", packagePath='" + packagePath + '\'' +
                ", versionName='" + versionName + '\'' +
                ", versionCode=" + versionCode +
                ", isSystem=" + isSystem +
                ", signature=" + Arrays.toString(signature) +
                ", SHA1='" + SHA1 + '\'' +
                ", appFilesDir='" + appFilesDir + '\'' +
                ", appCacheDir='" + appCacheDir + '\'' +
                ", resFilesDir='" + resFilesDir + '\'' +
                ", resCacheDir='" + resCacheDir + '\'' +
                '}';
    }
}
