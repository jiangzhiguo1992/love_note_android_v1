package com.jiangzg.base.view;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;

import com.jiangzg.base.application.AppBase;
import com.jiangzg.base.common.LogUtils;

/**
 * Created by JiangZhiGuo on 2016/10/12.
 * describe 屏幕工具类
 */
public class ScreenUtils {

    private static final String LOG_TAG = "ScreenUtils";

    /**
     * 设置屏幕为竖屏
     * <p>还有一种就是在Activity中加属性android:screenOrientation="landscape"</p>
     * <p>不设置Activity的android:configChanges时，切屏会重新调用各个生命周期，切横屏时会执行一次，切竖屏时会执行两次</p>
     * <p>设置Activity的android:configChanges="orientation"时，切屏还是会重新调用各个生命周期，切横、竖屏时只会执行一次</p>
     * <p>设置Activity的android:configChanges="orientation|keyboardHidden|screenSize"（4.0以上必须带最后一个参数）时
     * 切屏不会重新调用各个生命周期，只会执行onConfigurationChanged方法</p>
     */
    public static void requestPortrait(Activity activity) {
        if (activity == null) {
            LogUtils.w(LOG_TAG, "requestPortrait: activity == null");
            return;
        }
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    /**
     * 设置屏幕为横屏
     */
    public static void requestLandscape(Activity activity) {
        if (activity == null) {
            LogUtils.w(LOG_TAG, "requestLandscape: activity == null");
            return;
        }
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    /**
     * 获取屏幕参数集
     */
    public static DisplayMetrics getDisplay(Context context) {
        if (context == null) {
            LogUtils.w(LOG_TAG, "getDisplay: context == null");
            return null;
        }
        DisplayMetrics displayMetrics;
        if (context instanceof Activity) {
            displayMetrics = new DisplayMetrics();
            ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        } else {
            displayMetrics = context.getResources().getDisplayMetrics();
        }
        return displayMetrics;
    }

    /**
     * 获取屏幕参数集，不带虚拟键盘
     */
    public static DisplayMetrics getRealDisplay(Activity activity) {
        if (activity == null) {
            LogUtils.w(LOG_TAG, "getRealDisplay: activity == null");
            return null;
        }
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getRealMetrics(displayMetrics);
        return displayMetrics;
    }

    /**
     * 获取屏幕的宽度px
     */
    public static int getScreenWidth(Context context) {
        DisplayMetrics display = getDisplay(context);
        if (display == null) return 0;
        return display.widthPixels;
    }

    /**
     * 获取屏幕的高度px
     */
    public static int getScreenHeight(Context context) {
        DisplayMetrics display = getDisplay(context);
        if (display == null) return 0;
        return display.heightPixels;
    }

    /**
     * 获取屏幕的高度px
     */
    public static int getScreenRealWidth(Activity activity) {
        DisplayMetrics display = getRealDisplay(activity);
        if (display == null) return 0;
        return display.widthPixels;
    }

    /**
     * 获取屏幕的高度px
     */
    public static int getScreenRealHeight(Activity activity) {
        DisplayMetrics display = getRealDisplay(activity);
        if (display == null) return 0;
        return display.heightPixels;
    }

    /**
     * 获取activity的xml布局
     */
    public static View getMainLayout(Activity activity) {
        if (activity == null) {
            LogUtils.w(LOG_TAG, "getMainLayout: context == null");
            return null;
        }
        return activity.getWindow().findViewById(Window.ID_ANDROID_CONTENT);
    }

    /**
     * 获取当前屏幕截图，包含状态栏
     */
    public static Bitmap captureFullScreen(Activity activity) {
        if (activity == null) {
            LogUtils.w(LOG_TAG, "captureFullScreen: context == null");
            return null;
        }
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bmp = view.getDrawingCache();
        int width = getScreenWidth(activity);
        int height = getScreenHeight(activity);
        Bitmap bp = Bitmap.createBitmap(bmp, 0, 0, width, height);
        view.destroyDrawingCache();
        return bp;
    }

    /**
     * 获取当前屏幕截图，不包含状态栏
     */
    public static Bitmap captureNoStatus(Activity activity) {
        if (activity == null) {
            LogUtils.w(LOG_TAG, "captureNoStatus: context == null");
            return null;
        }
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bmp = view.getDrawingCache();
        int statusBarHeight = BarUtils.getStatusBarHeight(activity);
        int width = getScreenWidth(activity);
        int height = getScreenHeight(activity);
        Bitmap bp = Bitmap.createBitmap(bmp, 0, statusBarHeight, width, height - statusBarHeight);
        view.destroyDrawingCache();
        return bp;
    }

    /**
     * 判断当前手机是否处于锁屏(睡眠)状态
     */
    public static boolean isScreenLock() {
        return AppBase.getKeyguardManager().inKeyguardRestrictedInputMode();
    }

}
