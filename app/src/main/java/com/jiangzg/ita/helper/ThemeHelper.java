package com.jiangzg.ita.helper;

import android.app.Activity;

import com.jiangzg.ita.R;

/**
 * Created by JZG on 2018/3/12.
 * 主题工具类
 */

public class ThemeHelper {

    public static final int THEME_PINK = 0;
    public static final int THEME_RED = 1;
    public static final int THEME_PURPLE = 2;
    public static final int THEME_BLUE = 3;
    public static final int THEME_GREEN = 4;
    public static final int THEME_YELLOW = 5;
    public static final int THEME_ORANGE = 6;
    public static final int THEME_BROWN = 7;

    public static void initTheme(Activity activity) {
        int settingsTheme = PrefHelper.getSettingsTheme();
        switch (settingsTheme) {
            case THEME_PINK:
                activity.setTheme(R.style.AppThemePink);
                break;
            case THEME_RED:
                activity.setTheme(R.style.AppThemeRed);
                break;
            case THEME_PURPLE:
                activity.setTheme(R.style.AppThemePurple);
                break;
            case THEME_BLUE:
                activity.setTheme(R.style.AppThemeBlue);
                break;
            case THEME_GREEN:
                activity.setTheme(R.style.AppThemeGreen);
                break;
            case THEME_YELLOW:
                activity.setTheme(R.style.AppThemeYellow);
                break;
            case THEME_ORANGE:
                activity.setTheme(R.style.AppThemeOrange);
                break;
            case THEME_BROWN:
                activity.setTheme(R.style.AppThemeBrown);
                break;
        }
    }

    public static void setTheme(Activity activity, int theme) {
        PrefHelper.setSettingsTheme(theme);
        //initTheme(activity);
    }

}
