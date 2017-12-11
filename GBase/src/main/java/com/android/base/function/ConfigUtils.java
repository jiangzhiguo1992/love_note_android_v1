package com.android.base.function;

import android.app.Application;
import android.content.res.Configuration;
import android.os.Build;

/**
 * Created by gg on 2017/5/16.
 * 配置工具类
 */
public class ConfigUtils {

    public static Configuration get(Application application) {
        return application.getResources().getConfiguration();
    }

    public static String getLog(Configuration cfg) {
        StringBuilder status = new StringBuilder();
        status.append("fontScale:").append(cfg.fontScale).append("\n");
        status.append("hardKeyboardHidden:").append(cfg.hardKeyboardHidden).append("\n");
        status.append("keyboard:").append(cfg.keyboard).append("\n");
        status.append("keyboardHidden:").append(cfg.keyboardHidden).append("\n");
        status.append("locale:").append(cfg.locale).append("\n");
        status.append("mcc:").append(cfg.mcc).append("\n");
        status.append("mnc:").append(cfg.mnc).append("\n");
        status.append("navigation:").append(cfg.navigation).append("\n");
        status.append("navigationHidden:").append(cfg.navigationHidden).append("\n");
        status.append("orientation:").append(cfg.orientation).append("\n");
        status.append("screenHeightDp:").append(cfg.screenHeightDp).append("\n");
        status.append("screenWidthDp:").append(cfg.screenWidthDp).append("\n");
        status.append("screenLayout:").append(cfg.screenLayout).append("\n");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            status.append("densityDpi:").append(cfg.densityDpi).append("\n");
            status.append("smallestScreenWidthDp:").append(cfg.smallestScreenWidthDp).append("\n");
            status.append("touchscreen:").append(cfg.touchscreen).append("\n");
            status.append("uiMode:").append(cfg.uiMode).append("\n");
        }
        return status.toString();
    }
}
