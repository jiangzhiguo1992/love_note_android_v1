package com.jiangzg.base.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.transition.AutoTransition;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupMenu;
import android.widget.PopupWindow;

/**
 * Created by Jiang on 2016/06/01
 * 处理   popWindow,popMenu管理类
 */
public class PopUtils {

    /**
     * @param window LayoutInflater.from(activity).inflate(R.layout.id, null);
     * @param width  WindowManager.LayoutParams.WRAP_CONTENT
     * @param height WindowManager.LayoutParams.WRAP_CONTENT
     * @return 下面的代码放到onTouchEvent中来点击外部dismiss也行
     * if (popupWindow != null && popupWindow.isShowing()) {popupWindow.dismiss();}
     */
    public static PopupWindow createWindow(View window, int width, int height) {
        final PopupWindow pop = new PopupWindow(window, width, height);
        pop.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pop.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            pop.setEnterTransition(new AutoTransition());
            pop.setExitTransition(new AutoTransition());
        }
        pop.setFocusable(true);
        pop.setOutsideTouchable(true); // 点击外部区域消失
        pop.setTouchable(true);
        pop.setTouchInterceptor(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // 返回键消失，不会退出activity
                if (event.getAction() == MotionEvent.BUTTON_BACK) {
                    pop.dismiss();
                    return true;
                }
                return false;
            }
        });
        return pop;
    }

    /* 创建适配的PopupWindow，不要多次创建 */
    public static PopupWindow createWindow(View window) {
        return createWindow(window, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
    }

    /* 显示popupWindow ,location可用viewUtils获取 */
    public static void show(PopupWindow window, View parent, int gravity, int offsetX, int offsetY) {
        if (window != null && !window.isShowing())
            window.showAtLocation(parent, gravity, offsetX, offsetY);
    }

    public static void show(PopupWindow window, View parent, int gravity) {
        if (window != null && !window.isShowing()) window.showAtLocation(parent, gravity, 0, 0);
    }

    /* 显示popupWindow ,会依附在anchor的下方 还有偏移量(有动画) */
    public static void show(PopupWindow window, View anchor, int offsetX, int offsetY) {
        if (window != null && !window.isShowing()) window.showAsDropDown(anchor, offsetX, offsetY);
    }

    public static void show(PopupWindow window, View anchor) {
        if (window != null && !window.isShowing()) window.showAsDropDown(anchor);
    }

    /* 更新popupWindow宽高 */
    public static void update(PopupWindow pop, int width, int height) {
        if (pop != null) pop.update(width, height);
    }

    public static void update(PopupWindow pop, int x, int y, int width, int height) {
        if (pop != null) pop.update(x, y, width, height);
    }

    /* 移除popupWindow */
    public static void dismiss(PopupWindow window) {
        if (window != null && window.isShowing()) window.dismiss();
    }

    /* 创建PopMenu(显示位置只能是在anchor的下面) */
    public static PopupMenu createMenu(Context context, View anchor, int menuID, PopupMenu.OnMenuItemClickListener listener) {
        PopupMenu menu = new PopupMenu(context, anchor);
        menu.getMenuInflater().inflate(menuID, menu.getMenu());
        menu.setOnMenuItemClickListener(listener);
        return menu;
    }

    /* 显示PopupMenu */
    public static void show(PopupMenu menu) {
        if (menu != null) menu.show();
    }

    /* 移除的PopupMenu */
    public static void dismiss(PopupMenu menu) {
        if (menu != null) menu.dismiss();
    }

}
