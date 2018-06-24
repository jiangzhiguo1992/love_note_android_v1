package com.jiangzg.base.system;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import com.jiangzg.base.application.AppBase;

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

}
