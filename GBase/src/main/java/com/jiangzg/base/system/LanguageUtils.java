package com.jiangzg.base.system;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.support.annotation.StringRes;
import android.util.DisplayMetrics;

import com.jiangzg.base.application.AppBase;
import com.jiangzg.base.common.LogUtils;

import java.util.Locale;

/**
 * Created by gg on 2017/4/11.
 * 语言管理类
 */
public class LanguageUtils {

    /* 语言环境 */
    public static Locale getLocale() {
        return AppBase.getInstance().getResources().getConfiguration().locale;
    }

    /* 是否为英语环境 */
    public static boolean isEN() {
        String language = getLocale().getLanguage();
        return language.endsWith("en");
    }

    /* 是否为中文环境 */
    public static boolean isZH() {
        String language = getLocale().getLanguage();
        return language.endsWith("zh");
    }

    /**
     * 设置默认语言 eg:"en" 应在app中初始化调用
     */
    public static void setDefault(String name) {
        Resources resources = AppBase.getInstance().getResources();
        String language = resources.getConfiguration().locale.getLanguage();
        Configuration config = resources.getConfiguration();
        String languageToLoad;
        if (language.endsWith("zh")) {
            languageToLoad = "zh";
            config.locale = Locale.SIMPLIFIED_CHINESE;
        } else {
            languageToLoad = name;
            config.locale = Locale.ENGLISH;
        }
        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        DisplayMetrics metrics = resources.getDisplayMetrics();
        resources.updateConfiguration(config, metrics);
    }

    public static String getStringToEnglish(Context context, @StringRes int resId) {
        return getStringByLanguage(context, resId, "en");
    }

    public static String getStringByLanguage(Context context, @StringRes int resId, String language) {
        Resources resources = getApplicationResource(context.getApplicationContext().getPackageManager(),
                context.getPackageName(), new Locale(language));
        try {
            if (resources != null) {
                return resources.getString(resId);
            }
        } catch (Exception e) {
            LogUtils.e(LanguageUtils.class, "getApplicationResource", e);
        }
        return "";

    }

    private static Resources getApplicationResource(PackageManager pm, String pkgName, Locale l) {
        Resources resourceForApplication = null;
        try {
            resourceForApplication = pm.getResourcesForApplication(pkgName);
            Configuration config = resourceForApplication.getConfiguration();
            config.locale = l;
            resourceForApplication.updateConfiguration(config, null);
        } catch (PackageManager.NameNotFoundException e) {
            LogUtils.e(LanguageUtils.class, "getApplicationResource", e);
        }
        return resourceForApplication;
    }

}
