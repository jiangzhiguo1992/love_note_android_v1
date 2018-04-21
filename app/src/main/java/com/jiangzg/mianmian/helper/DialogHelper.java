package com.jiangzg.mianmian.helper;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.support.annotation.StyleRes;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.jiangzg.base.view.BarUtils;
import com.jiangzg.mianmian.R;

/**
 * Created by JZG on 2018/3/30.
 * 对话框
 */
public class DialogHelper {

    public static MaterialDialog.Builder getBuild(Context context) {
        return new MaterialDialog.Builder(context)
                .autoDismiss(true)
                .theme(Theme.LIGHT)
                // color
                .titleColorRes(R.color.font_black)
                .contentColorRes(R.color.font_black)
                .positiveColorAttr(R.attr.colorPrimaryDark)
                .negativeColorAttr(R.attr.colorPrimary)
                .neutralColorAttr(R.attr.colorAccent)
                //.linkColorAttr(R.attr.colorAccent) // 默认的就好
                //.dividerColorAttr(R.attr.colorAccent) // 默认的就好
                //.backgroundColorRes(R.color.material_blue_grey_800) // 默认的就好
                //.widgetColorRes(R.color.material_red_500) // 默认的就好
                //.buttonRippleColorRes(R.color.material_red_500) // 默认的就好
                // selector 有ripple就不要这个了
                //.btnSelector(R.drawable.custom_btn_selector)
                //.btnSelector(R.drawable.custom_btn_selector_primary, DialogAction.POSITIVE)
                //.btnSelectorStacked(R.drawable.custom_btn_selector_stacked)
                //.listSelector(R.drawable.custom_list_and_stackedbtn_selector)
                // Gravity
                .titleGravity(GravityEnum.START)
                .contentGravity(GravityEnum.START)
                .btnStackedGravity(GravityEnum.END)
                .itemsGravity(GravityEnum.START)
                .buttonsGravity(GravityEnum.START);
    }

    public static void showWithAnim(Dialog dialog) {
        setAnim(dialog);
        show(dialog);
    }

    public static void show(Dialog dialog) {
        if (dialog == null || dialog.isShowing()) return;
        dialog.show();
    }

    @SuppressLint("MissingPermission")
    public static void showInContext(Dialog dialog) {
        Window window = dialog.getWindow();
        if (window != null) {
            window.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        }
        show(dialog);
    }

    public static void dismiss(Dialog dialog) {
        if (dialog == null || !dialog.isShowing()) return;
        dialog.dismiss();
    }

    /**
     * 设置动画，防止闪屏
     */
    public static void setAnim(Dialog dialog) {
        setAnim(dialog, R.style.DialogAnim);
    }

    public static void setAnim(Dialog dialog, @StyleRes int resId) {
        if (dialog == null || resId == 0) return;
        Window window = dialog.getWindow();
        if (window != null) {
            window.setWindowAnimations(resId);
        }
    }

    /**
     * 设置透明度
     */
    private static void setAlpha(Dialog dialog, float alpha) {
        Window window = dialog.getWindow();
        if (window == null) return;
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.alpha = alpha;
        window.setAttributes(lp);
    }

    /**
     * 设置暗黑背景层
     */
    private static void setDimamount(Dialog dialog, float alpha) {
        Window window = dialog.getWindow();
        if (window == null) return;
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.dimAmount = alpha;
        window.setAttributes(lp);
        window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
    }

    /**
     * @param height 百分比
     * @param width  百分比
     */
    private static Dialog createCustom(Activity activity, View view, int theme, float height, float width) {
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
    private static Dialog createCustom(Activity activity, View view, int theme, int height, int width) {
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
