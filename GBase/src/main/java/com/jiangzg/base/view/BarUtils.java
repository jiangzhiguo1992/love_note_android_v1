package com.jiangzg.base.view;

import android.app.Activity;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Rect;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.jiangzg.base.R;

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
        return (params.flags & WindowManager.LayoutParams.FLAG_FULLSCREEN) != WindowManager.LayoutParams.FLAG_FULLSCREEN;
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
        view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
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
     * 获取状态栏高度, 和getStatusBarHeight()效果一样
     */
    public static int getStatusBarHeight(Activity activity) {
        int result = 0;
        int resourceId = activity.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = activity.getResources().getDimensionPixelSize(resourceId);
        }
        if (result > 0) {
            return result;
        }
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

    public static int getActionBarHeight2(Activity activity) {
        int height = 0;
        TypedValue tv = new TypedValue();
        if (activity.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
            height = TypedValue.complexToDimensionPixelSize(tv.data, activity.getResources().getDisplayMetrics());
        return height;
    }

    /**
     * ***************************************沉浸式******************************************
     * 着色模式
     * ContextCompat.getColor(id)
     * color = 0 不设置
     */
    public static void setStatusBarColor(Activity activity, int color) {
        Window window = activity.getWindow();
        if (color != 0) {
            // 清除Status透明的状态
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // 添加Status可以着色的状态，并开始着色
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(color);
        }
    }

    public static void setNavigationBarColor(Activity activity, int color) {
        Window window = activity.getWindow();
        if (color != 0) {
            // 清除navigation透明的状态
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            // 添加navigation可以着色的状态，并开始着色
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setNavigationBarColor(color);
        }
    }

    /**
     * 全屏模式：注意在setContent之前调用，否则填充不到bar里
     */
    public static void setStatusBarTrans(Activity activity, boolean trans) {
        Window window = activity.getWindow();
        // 清除themes中指定的statusTrans带来的Status半透明的状态
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        if (trans) {
            // 让DecorView填充Status，相当于fitSystemWindows=true
            int options = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            window.getDecorView().setSystemUiVisibility(options);
            // 添加Status可以着色的状态，并开始设置透明颜色
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            //setRootViewFit(activity, false);
        } else {
            // 获取默认colorPrimary的颜色
            TypedValue typedValue = new TypedValue();
            activity.getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
            // 获取themes里的statusBarColor属性
            int[] attrsArray = {android.R.attr.statusBarColor};
            TypedArray typedArray = activity.obtainStyledAttributes(attrsArray);
            int statusBarColor = typedArray.getColor(0, typedValue.data);
            typedArray.recycle();
            // 添加Status可以着色的状态，并开始着色
            setStatusBarColor(activity, statusBarColor);
            //setRootViewFit(activity, true);
        }
    }

    public static void setNavigationBarTrans(Activity activity, boolean trans) {
        Window window = activity.getWindow();
        // 清除navigation半透明的状态
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        if (trans) {
            // 让DecorView填充Navigation，其实就是隐藏掉Navigation
            int options = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
            // 刚开始隐藏，点击消失 | View.SYSTEM_UI_FLAG_FULLSCREEN| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
            window.getDecorView().setSystemUiVisibility(options);
            // 添加Navigation可以着色的状态，并开始设置透明颜色
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setNavigationBarColor(Color.TRANSPARENT);
            //setRootViewFit(activity, false);
        } else {
            // 获取默认colorPrimary的颜色
            TypedValue typedValue = new TypedValue();
            activity.getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
            // 获取themes里的statusBarColor属性
            int[] attrsArray = {android.R.attr.statusBarColor};
            TypedArray typedArray = activity.obtainStyledAttributes(attrsArray);
            int statusBarColor = typedArray.getColor(0, typedValue.data);
            typedArray.recycle();
            // 添加Status可以着色的状态，并开始着色
            setNavigationBarColor(activity, statusBarColor);
            //setRootViewFit(activity, false);
        }
    }

    /**
     * 设置根布局参数 相当于最顶部view要设置 fitsSystemWindows="true"
     */
    private static void setRootViewFit(Activity activity, boolean fit) {
        ViewGroup parent = activity.findViewById(android.R.id.content);
        for (int i = 0; i < parent.getChildCount(); i++) {
            View childView = parent.getChildAt(i);
            if (childView instanceof ViewGroup) {
                childView.setFitsSystemWindows(fit);
                ((ViewGroup) childView).setClipToPadding(fit);
                //((ViewGroup) childView).setClipChildren(clip);
            }
        }
    }

}
