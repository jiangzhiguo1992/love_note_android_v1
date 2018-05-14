package com.jiangzg.mianmian.helper;

import android.app.Activity;

import com.jiangzg.base.component.ActivityStack;
import com.jiangzg.mianmian.R;

import java.util.Stack;

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

    public static int getTheme() {
        int settingsTheme = SPHelper.getTheme();
        switch (settingsTheme) {
            case THEME_PINK:
                return R.style.AppThemePink;
            case THEME_RED:
                return R.style.AppThemeRed;
            case THEME_PURPLE:
                return R.style.AppThemePurple;
            case THEME_BLUE:
                return R.style.AppThemeBlue;
            case THEME_GREEN:
                return R.style.AppThemeGreen;
            case THEME_YELLOW:
                return R.style.AppThemeYellow;
            case THEME_ORANGE:
                return R.style.AppThemeOrange;
            case THEME_BROWN:
                return R.style.AppThemeBrown;
            default:
                return R.style.AppThemePink;
        }
    }

    public static void setTheme(int theme) {
        // sp保存
        SPHelper.setTheme(theme);
        // 遍历所有的activity，设置theme并重绘
        Stack<Activity> stack = ActivityStack.getStack();
        if (stack == null || stack.isEmpty()) return;
        for (Activity activity : stack) {
            activity.recreate();
            //if (activity instanceof BaseActivity) {
            //    BaseActivity mActivity = (BaseActivity) activity;
            //    mActivity.setContentView(null);
            //    initTheme(mActivity);
            //    mActivity.setContentView(mActivity.mRootViewId);
            //    mActivity.onCreate(null, null);
            //    mActivity.recreate();
            //}
        }
        //Activity top = ActivityStack.getTop();
        //if (top != null) {
        //    HomeActivity.goActivity(top);
        //}
    }

}
