package com.android.base.component.application;

import android.annotation.SuppressLint;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.android.base.file.EnvironUtils;
import com.android.base.file.FileUtils;
import com.android.base.string.EncryptUtils;
import com.android.base.string.StringUtils;

import java.io.File;
import java.util.Arrays;

/**
 * Created by JiangZhiGuo on 2016/10/12.
 * describe  App信息
 */
public class AppInfo {

    private static final String LOG_TAG = "AppContext";

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
    private String resDir; // SDCard/包名/
    private String filesDir; // SDCard/Android/data/包名/files/ 或者是sys的
    private String cacheDir; // SDCard/Android/data/包名/cache/ 或者是sys的

    /**
     * 获取当前App信息
     */
    @SuppressLint("PackageManagerGetSignatures")
    public static AppInfo get() throws SecurityException {
        //if (instance != null) return instance;
        if (instance == null) {
            instance = new AppInfo();
        }
        String packageName = AppContext.get().getPackageName();
        PackageManager pm = AppContext.get().getPackageManager();
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
        Log.d(LOG_TAG, "getSHA1-->" + SHA1);
        return SHA1;
    }

    public void setSignature(Signature[] signature) {
        this.signature = signature;
    }

    public Signature[] getSignature() {
        Log.d(LOG_TAG, "getSignature-->" + Arrays.toString(signature));
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
        Log.d(LOG_TAG, "getPackageName-->" + packageName);
        return packageName;
    }

    public void setPackagePath(String packagePath) {
        this.packagePath = packagePath;
    }

    public String getPackagePath() {
        Log.d(LOG_TAG, "getPackagePath-->" + packagePath);
        return packagePath;
    }

    public void setSystem(boolean system) {
        isSystem = system;
    }

    public boolean isSystem() {
        Log.d(LOG_TAG, "isSystem-->" + isSystem);
        return isSystem;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        Log.d(LOG_TAG, "getName-->" + name);
        return name;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public int getVersionCode() {
        Log.d(LOG_TAG, "getVersionCode-->" + versionCode);
        return versionCode;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getVersionName() {
        Log.d(LOG_TAG, "getVersionName-->" + versionName);
        return versionName;
    }

    /**
     * 自定义资源路径(部分手机有差别)
     */
    public String getResDir() {
        if (!StringUtils.isEmpty(resDir)) return resDir;
        resDir = EnvironUtils.getRealSDCardPath() + packageName + File.separator;
        FileUtils.createOrExistsDir(resDir); // 并创建
        Log.d(LOG_TAG, "getResDir-->" + resDir);
        return resDir;
    }

    /**
     * 如果SD卡存在，则获取 SDCard/Android/data/你的应用的包名/
     * 如果不存在，则获取 /data/data/<application package>/
     */
    public String getFilesDir() {
        File dir;
        if (EnvironUtils.isSDCardEnable()) {
            dir = AppContext.get().getExternalFilesDir("");
        } else {
            dir = AppContext.get().getFilesDir();
        }
        if (dir != null) {
            filesDir = dir.getAbsolutePath();
        }
        Log.d(LOG_TAG, "getFilesDir-->" + filesDir);
        return filesDir;
    }

    /**
     * 如果SD卡存在，则获取 SDCard/Android/data/你的应用包名/cache/
     * 如果不存在，则获取 /data/data/<application package>/cache
     */
    public String getCacheDir() {
        if (!StringUtils.isEmpty(cacheDir)) return cacheDir;
        File dir;
        if (EnvironUtils.isSDCardEnable()) {
            dir = AppContext.get().getExternalCacheDir();
        } else {
            dir = AppContext.get().getCacheDir();
        }
        if (dir != null) {
            cacheDir = dir.getAbsolutePath();
        }
        Log.d(LOG_TAG, "getCacheDir-->" + cacheDir);
        return cacheDir;
    }

}
