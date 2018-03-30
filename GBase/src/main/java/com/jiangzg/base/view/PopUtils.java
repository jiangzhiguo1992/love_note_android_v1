package com.jiangzg.base.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.annotation.LayoutRes;
import android.support.annotation.MenuRes;
import android.transition.AutoTransition;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupMenu;
import android.widget.PopupWindow;

import com.jiangzg.base.common.LogUtils;

/**
 * Created by Jiang on 2016/06/01
 * 处理   popWindow,popMenu管理类
 */
public class PopUtils {

    private static final String LOG_TAG = "PopUtils";

    /**
     * 创建PopupWindow
     */
    public static PopupWindow createWindow(Context context, @LayoutRes int layoutId) {
        return createWindow(context, layoutId, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
    }

    public static PopupWindow createWindow(View window) {
        return createWindow(window, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
    }

    public static PopupWindow createWindow(Context context, @LayoutRes int layoutId, int width, int height) {
        if (context == null || layoutId == 0) {
            LogUtils.w(LOG_TAG, "createWindow: context == null || layoutId == 0");
            return null;
        }
        View window = LayoutInflater.from(context).inflate(layoutId, null);
        if (window == null) {
            LogUtils.w(LOG_TAG, "createWindow: window == null");
            return null;
        }
        return createWindow(window, width, height);
    }

    public static PopupWindow createWindow(View window, int width, int height) {
        if (window == null) {
            LogUtils.w(LOG_TAG, "createWindow: window == null");
            return null;
        }
        final PopupWindow pop = new PopupWindow(window, width, height);
        pop.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pop.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            pop.setEnterTransition(new AutoTransition());
            pop.setExitTransition(new AutoTransition());
        }
        pop.setOutsideTouchable(true); // 点击外部区域消失
        pop.setFocusable(true);
        pop.setTouchable(true);
        pop.setTouchInterceptor(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.BUTTON_BACK) {
                    pop.dismiss(); // 返回键消失，不会退出activity
                    return true;
                }
                return false;
            }
        });
        return pop;
    }

    /* 显示popupWindow ,会依附在anchor的下方 还有偏移量(有动画) */
    public static void show(PopupWindow window, View anchor, int offsetX, int offsetY) {
        if (window != null && !window.isShowing()) {
            window.showAsDropDown(anchor, offsetX, offsetY);
        }
    }

    public static void show(PopupWindow window, View anchor) {
        if (window != null && !window.isShowing()) {
            window.showAsDropDown(anchor);
        }
    }

    /* 显示popupWindow ,先按gravity来放 然后再偏移x和y */
    public static void show(PopupWindow window, View parent, int gravity, int offsetX, int offsetY) {
        if (window != null && !window.isShowing()) {
            window.showAtLocation(parent, gravity, offsetX, offsetY);
        }
    }

    public static void show(PopupWindow window, View parent, int gravity) {
        if (window != null && !window.isShowing()) {
            window.showAtLocation(parent, gravity, 0, 0);
        }
    }

    /* 更新popupWindow宽高 */
    public static void update(PopupWindow pop, int width, int height) {
        if (pop != null) {
            pop.update(width, height);
        }
    }

    public static void update(PopupWindow pop, int x, int y, int width, int height) {
        if (pop != null) {
            pop.update(x, y, width, height);
        }
    }

    /* 移除popupWindow */
    public static void dismiss(PopupWindow window) {
        if (window != null && window.isShowing()) {
            window.dismiss();
        }
    }

    /**
     * 创建PopMenu
     */
    public static PopupMenu createMenu(Context context, View anchor, @MenuRes int menuID, PopupMenu.OnMenuItemClickListener listener) {
        if (context == null || anchor == null || menuID == 0) {
            LogUtils.w(LOG_TAG, "createMenu: context == null || anchor == null || menuID == 0");
            return null;
        }
        PopupMenu menu = new PopupMenu(context, anchor);
        menu.getMenuInflater().inflate(menuID, menu.getMenu());
        if (listener != null) {
            menu.setOnMenuItemClickListener(listener);
        }
        return menu;
    }

    /* 显示PopupMenu */
    public static void show(PopupMenu menu) {
        if (menu != null) {
            menu.show();
        }
    }

    /* 移除的PopupMenu */
    public static void dismiss(PopupMenu menu) {
        if (menu != null) {
            menu.dismiss();
        }
    }

}
