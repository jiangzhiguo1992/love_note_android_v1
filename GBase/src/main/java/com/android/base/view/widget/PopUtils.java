package com.android.base.view.widget;

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
    private static PopupWindow getPop(View window, int width, int height, int anim) {
        PopupWindow pop = new PopupWindow(window, width, height);
        pop.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        if (0 != anim) {
            pop.setAnimationStyle(anim);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            pop.setEnterTransition(new AutoTransition());
            pop.setExitTransition(new AutoTransition());
        }
        pop.setOutsideTouchable(true); // 点击其他地方消失
        // 这些为了点击非PopupWindow区域，PopupWindow会消失的，如果没有下面的
        // 代码的话，你会发现，当你把PopupWindow显示出来了，无论你按多少次后退键
        // PopupWindow并不会关闭，而且退不出程序，加上下述代码可以解决这个问题
        pop.setTouchable(true);
        pop.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // 这里如果返回true的话，touch事件将被拦截
                // 拦截后 PopupWindow的onTouchEvent不被调用，这样点击外部区域无法dismiss
                return false;
            }
        });
        return pop;
    }

    /* 创建适配的PopupWindow，不要多次创建 */
    public static PopupWindow createWindow(View window) {
        return getPop(window, WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT, 0);
    }

    /* 创建指定宽高的PopupWindow，不要多次创建 */
    public static PopupWindow createWindow(View window, int width, int height) {
        return getPop(window, width, height, 0);
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
    public static PopupMenu createMenu(Context context, View anchor, int menuID,
                                       PopupMenu.OnMenuItemClickListener listener) {
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
