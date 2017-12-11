package com.android.base.function;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.android.base.component.application.AppContext;

/**
 * Created by JiangZhiGuo on 2016/10/13.
 * describe 输入管理工具类
 */
public class InputUtils {

    /**
     * 软键盘设置, 要在setContentView之前调用
     */
    public static void initActivity(Activity activity){
        Window window = activity.getWindow();
        // 键盘不会遮挡输入框
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        // 不自动弹键盘
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        // 总是隐藏键盘
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    /**
     * 动态隐藏软键盘
     */
    public static boolean hideSoftInput(View focus) {
        if (focus == null) return false;
        focus.clearFocus();
        InputMethodManager inputManager = AppContext.getInputManager();
        return inputManager.hideSoftInputFromWindow(focus.getWindowToken(), 0);
    }

    /**
     * 动态显示软键盘
     */
    public static boolean showSoftInput(View focus) {
        if (focus == null) return false;
        focus.setFocusable(true);
        focus.setFocusableInTouchMode(true);
        focus.requestFocus();
        InputMethodManager inputManager = AppContext.getInputManager();
        return inputManager.showSoftInput(focus, 0);
    }

    /**
     * 切换键盘显示与否状态
     */
    public static void toggleSoftInput(View focus) {
        if (focus == null) return;
        focus.setFocusable(true);
        focus.setFocusableInTouchMode(true);
        focus.requestFocus();
        InputMethodManager inputManager = AppContext.getInputManager();
        inputManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    /**
     * **********************************剪切板************************************
     * 复制文本到剪贴板
     */
    public static void copyText(String text) {
        ClipData myClip = ClipData.newPlainText("text", text);
        AppContext.getClipboardManager().setPrimaryClip(myClip);
    }

    /**
     * 获取剪贴板的文本
     */
    public static CharSequence getCopy() {
        CharSequence copy = "";
        ClipData clip = AppContext.getClipboardManager().getPrimaryClip();
        if (clip != null && clip.getItemCount() > 0)
            copy = clip.getItemAt(0).coerceToText(AppContext.get());
        return copy;
    }

    /**
     * 复制uri到剪贴板
     */
    public static void copyUri(Uri uri) {
        ClipData clipData = ClipData.newUri(AppContext.get().getContentResolver(), "uri", uri);
        AppContext.getClipboardManager().setPrimaryClip(clipData);
    }

    /**
     * 获取剪贴板的uri
     */
    public static Uri getUri() {
        ClipData clip = AppContext.getClipboardManager().getPrimaryClip();
        if (clip == null || clip.getItemCount() < 1) return null;
        return clip.getItemAt(0).getUri();
    }

    /**
     * 复制意图到剪贴板
     */
    public static void copyIntent(Intent intent) {
        ClipData clipData = ClipData.newIntent("intent", intent);
        AppContext.getClipboardManager().setPrimaryClip(clipData);
    }

    /**
     * 获取剪贴板的意图
     */
    public static Intent getIntent() {
        ClipData clip = AppContext.getClipboardManager().getPrimaryClip();
        if (clip == null || clip.getItemCount() < 1) return null;
        return clip.getItemAt(0).getIntent();
    }
}
