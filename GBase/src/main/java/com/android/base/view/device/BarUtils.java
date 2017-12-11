package com.android.base.view.device;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

/**
 * Created by gg on 2017/4/19.
 * 状态栏工具类
 */
public class BarUtils {

    /**
     * 无actionBar, 要在setContentView之前调用
     */
    public static void requestNoTitle(AppCompatActivity activity) {
        activity.supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    /**
     * 隐藏状态栏 , 也就是设置全屏，一定要在setContentView之前调用，否则报错
     */
    public static void hideStatusBar(Activity activity) {
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    /**
     * 判断状态栏是否存在
     */
    public static boolean isStatusExists(Activity activity) {
        WindowManager.LayoutParams params = activity.getWindow().getAttributes();
        return (params.flags & WindowManager.LayoutParams.FLAG_FULLSCREEN)
                != WindowManager.LayoutParams.FLAG_FULLSCREEN;
    }

    /**
     * 动态显示Status
     */
    public static void showStatus(View view) {
        view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
    }

    /**
     * 动态显示Status , 会遮挡top布局
     */
    public static void fitStatus(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
    }

    /**
     * 动态隐藏Status
     */
    public static void disStatus(View view) {
        view.setSystemUiVisibility(View.INVISIBLE);
    }

    /**
     * ***************************************高度******************************************
     * 获取状态栏＋标题栏高度,如果没有ActionBar，那么只有状态栏的高度
     */
    public static int getTopBarHeight(Activity activity) {
        return ScreenUtils.getMainLayout(activity).getBottom();
    }

    /**
     * 获取状态栏高度
     */
    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources()
                .getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0)
            result = context.getResources().getDimensionPixelSize(resourceId);
        return result;
    }

    /**
     * 获取状态栏高度, 和getStatusBarHeight()效果一样
     */
    public static int getStatusBarHeight(Activity activity) {
        return getTopBarHeight(activity) - getActionBarHeight(activity);
    }

    /**
     * 获取actionBar高度
     */
    public static int getActionBarHeight(Activity activity) {
        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        return frame.top;
    }

    /**
     * 获取ActionBar高度
     */
    public static int getActionBarHeight2(Activity activity) {
        int height = 0;
        TypedValue tv = new TypedValue();
        if (activity.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
            height = TypedValue.complexToDimensionPixelSize(tv.data, activity.getResources()
                    .getDisplayMetrics());
        return height;
    }

    /**
     * ***************************************沉浸式******************************************
     * 着色模式: 为status着色 ContextCompat.getColor(id)
     * Status底部为白色,所以这个不能全屏模式,下同
     */
    public static void setStatusColor(Activity activity, int color) {
        Window window = activity.getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) { // 5.0
            // 清除Status透明的状态
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // 添加Status可以着色的状态
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            // 开始着色Status
            window.setStatusBarColor(color);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) { // 4.4
            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    /**
     * 着色模式: 为navigation着色 ContextCompat.getColor(id)
     */
    public static void setNavigationColor(Activity activity, int color) {
        Window window = activity.getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) { // 5.0
            // 清除Navigation透明的状态
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            // 添加Navigation可以着色的状态
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            // 开始着色Navigation
            window.setNavigationBarColor(color);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) { // 4.4
            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
    }

    /**
     * 全屏模式：这里只负责status的透明
     */
    public static void setStatusTrans(Activity activity) {
        Window window = activity.getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) { // 5.0
            // 清除Status和navigation透明的状态
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // 让DecorView填充Status和Navigation，这样他们的底色就不是白色，而是我们的Layout的背景色
            // setSystemUiVisibility就是用来操作Status的方法
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            // 添加Status可以着色的状态
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            // 开始着色Status
            window.setStatusBarColor(Color.TRANSPARENT);
            setRootView(activity);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) { // 4.4
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            setRootView(activity);
        }
    }

    /**
     * 全屏模式：这里只负责Navigation的透明
     */
    public static void setNavigationTrans(Activity activity) {
        Window window = activity.getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) { // 5.0
            // 清除Status和navigation透明的状态
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            // 让DecorView填充Status和Navigation，这样他们的底色就不是白色，而是我们的Layout的背景色
            // setSystemUiVisibility就是用来操作Status的方法
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
            // 添加Status可以着色的状态
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            // 开始着色Navigation
            window.setNavigationBarColor(Color.TRANSPARENT);
            setRootView(activity);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) { // 4.4
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            setRootView(activity);
        }

    }

    /**
     * 设置根布局参数 相当于最顶部view要设置 fitsSystemWindows="true"
     */
    private static void setRootView(Activity activity) {
        ViewGroup parent = (ViewGroup) activity.findViewById(android.R.id.content);
        for (int i = 0, count = parent.getChildCount(); i < count; i++) {
            View childView = parent.getChildAt(i);
            if (childView instanceof ViewGroup) {
                childView.setFitsSystemWindows(true);
                ((ViewGroup) childView).setClipToPadding(true);
            }
        }
    }

}
