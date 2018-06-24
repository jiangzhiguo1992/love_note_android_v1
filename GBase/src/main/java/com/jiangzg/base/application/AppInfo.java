package com.jiangzg.base.application;

import android.annotation.SuppressLint;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.drawable.Drawable;
import android.os.Environment;

import com.jiangzg.base.common.EncryptUtils;
import com.jiangzg.base.common.LogUtils;
import com.jiangzg.base.common.StringUtils;

import java.io.File;
import java.util.Arrays;

/**
 * Created by JiangZhiGuo on 2016/10/12.
 * describe  App信息
 */
public class AppInfo {

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
    private String inCacheDir; // 内部缓存目录 /data/user/0/packageName/cache，拍照裁剪没权限
    private String inFilesDir; // 内部文件目录 /data/user/0/packageName/files，拍照裁剪没权限
    private String outCacheDir; // sd卡缓存目录 /storage/emulated/0/Android/data/packageName/cache
    private String outFilesDir; // sd卡文件目录 /storage/emulated/0/Android/data/packageName/files
    private String sdCardDir; // sd卡路径 /storage/emulated/0/ 或 /

    /**
     * 获取当前App信息
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
                    String sha1 = EncryptUtils.encryptSHA1ToString(signatures[0].toByteArray()).replaceAll("(?<=[0-9A-F]{2})[0-9A-F]{2}", ":$0");
                    instance.setSHA1(sha1);
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            LogUtils.e(AppInfo.class, "get", e);
        }
        return instance;
    }

    public void setSHA1(String SHA1) {
        this.SHA1 = SHA1;
    }

    public String getSHA1() {
        LogUtils.d(AppInfo.class, "getSHA1", SHA1);
        return SHA1;
    }

    public void setSignature(Signature[] signature) {
        this.signature = signature;
    }

    public Signature[] getSignature() {
        LogUtils.d(AppInfo.class, "getSignature", Arrays.toString(signature));
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
        LogUtils.d(AppInfo.class, "getPackageName", packageName);
        return packageName;
    }

    public void setPackagePath(String packagePath) {
        this.packagePath = packagePath;
    }

    public String getPackagePath() {
        LogUtils.d(AppInfo.class, "getPackagePath", packagePath);
        return packagePath;
    }

    public void setSystem(boolean system) {
        isSystem = system;
    }

    public boolean isSystem() {
        LogUtils.d(AppInfo.class, "isSystem", String.valueOf(isSystem));
        return isSystem;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        LogUtils.d(AppInfo.class, "getName", name);
        return name;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public int getVersionCode() {
        LogUtils.d(AppInfo.class, "getVersionCode", String.valueOf(versionCode));
        return versionCode;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getVersionName() {
        LogUtils.d(AppInfo.class, "getVersionNam", versionName);
        return versionName;
    }

    public String getInCacheDir() {
        if (StringUtils.isEmpty(inCacheDir)) {
            File cacheDir = AppBase.getInstance().getCacheDir();
            inCacheDir = cacheDir.getAbsolutePath();
        }
        LogUtils.d(AppInfo.class, "getInCacheDir", inCacheDir);
        return inCacheDir;
    }

    public String getInFilesDir() {
        if (StringUtils.isEmpty(inFilesDir)) {
            File filesDir = AppBase.getInstance().getFilesDir();
            inFilesDir = filesDir.getAbsolutePath();
        }
        LogUtils.d(AppInfo.class, "getInFilesDir", inFilesDir);
        return inFilesDir;
    }

    public String getOutCacheDir() {
        if (StringUtils.isEmpty(outCacheDir)) {
            File externalCacheDir = AppBase.getInstance().getExternalCacheDir();
            if (isSDCardExits() && externalCacheDir != null && !StringUtils.isEmpty(externalCacheDir.getAbsolutePath())) {
                outCacheDir = externalCacheDir.getAbsolutePath();
            } else {
                outCacheDir = getInCacheDir();
            }
        }
        LogUtils.d(AppInfo.class, "getOutCacheDir", outCacheDir);
        return outCacheDir;
    }

    public String getOutFilesDir() {
        if (StringUtils.isEmpty(outFilesDir)) {
            File externalFilesDir = AppBase.getInstance().getExternalFilesDir("");
            if (isSDCardExits() && externalFilesDir != null && !StringUtils.isEmpty(externalFilesDir.getAbsolutePath())) {
                outFilesDir = externalFilesDir.getAbsolutePath();
            } else {
                outFilesDir = getInFilesDir();
            }
        }
        LogUtils.d(AppInfo.class, "getOutFilesDir", outFilesDir);
        return outFilesDir;
    }

    /**
     * 获取可用的SD卡路径，需要动态权限
     */
    @SuppressLint("MissingPermission")
    public String getSdCardDir() {
        if (StringUtils.isEmpty(sdCardDir)) {
            if (isSDCardExits()) { // 有sd卡 == /storage/emulated/0/
                this.sdCardDir = Environment.getExternalStorageDirectory().getPath() + File.separator;
            } else { // 没sd卡 == /
                this.sdCardDir = Environment.getRootDirectory() + File.separator;
            }
        }
        LogUtils.d(AppInfo.class, "getSDCardDir", sdCardDir);
        return this.sdCardDir;
    }

    public static boolean isSDCardExits() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
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
                ", inCacheDir='" + inCacheDir + '\'' +
                ", inFilesDir='" + inFilesDir + '\'' +
                ", outCacheDir='" + outCacheDir + '\'' +
                ", outFilesDir='" + outFilesDir + '\'' +
                ", sdCardDir='" + sdCardDir + '\'' +
                '}';
    }
}
