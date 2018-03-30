package com.jiangzg.ita.helper;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.support.annotation.StyleRes;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.jiangzg.base.view.BarUtils;

/**
 * Created by JZG on 2018/3/30.
 * 对话框
 */
public class DialogHelper {


    /**
     * 设置动画，防止闪屏
     */
    public static void setAnim(Dialog dialog, @StyleRes int resId) {
        if (dialog == null) return;
        Window window = dialog.getWindow();
        if (window != null) {
            window.setWindowAnimations(resId);
        }
    }

    /**
     * 设置透明度
     */
    public static void setAlpha(Dialog dialog, float alpha) {
        Window window = dialog.getWindow();
        if (window == null) return;
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.alpha = alpha;
        window.setAttributes(lp);
    }

    /**
     * 设置暗黑背景层
     */
    public static void setDimamount(Dialog dialog, float alpha) {
        Window window = dialog.getWindow();
        if (window == null) return;
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.dimAmount = alpha;
        window.setAttributes(lp);
        window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
    }

    /**
     * 后台弹框
     */
    @SuppressLint("MissingPermission")
    public static void showInContext(Dialog dialog) {
        Window window = dialog.getWindow();
        if (window != null) {
            window.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        }
        dialog.show();
    }

    /**
     * @param height 百分比
     * @param width  百分比
     */
    public static Dialog createCustom(Activity activity, View view, int theme, float height, float width) {
        DisplayMetrics d = activity.getResources().getDisplayMetrics(); // 获取屏幕宽、高用
        int rHeight = (int) (d.heightPixels * height) - BarUtils.getStatusBarHeight(activity); // 高度设置为屏幕的0.x（减去statusBar高度）
        int rWidth = (int) (d.widthPixels * width);  // 宽度设置为屏幕的0.x
        return createCustom(activity, view, theme, rHeight, rWidth);
    }

    /**
     * 自定义对话框
     *
     * @param view  LayoutInflater.from(activity).inflate(layoutId, null);
     * @param theme R.style.DialogCustom
     */
    public static Dialog createCustom(Activity activity, View view, int theme, int height, int width) {
        final Dialog dialog = new Dialog(activity, theme);
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        if (height != 0)   // 高度设置为屏幕的0.x（减去statusBar高度）
            lp.height = height;
        if (width != 0)   // 宽度设置为屏幕的0.x
            lp.width = width;
        dialog.setContentView(view, lp);
        return dialog;
    }

}
