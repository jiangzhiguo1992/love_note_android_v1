package com.android.base.function;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
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

    private static final String LOG_TAG = "InputUtils";

    /**
     * 软键盘设置, 要在setContentView之前调用
     */
    public static void initActivity(@NonNull Activity activity) {
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
    public static boolean hideSoftInput(@NonNull View focus) {
        focus.clearFocus();
        Log.d(LOG_TAG, "hideSoftInput");
        InputMethodManager inputManager = AppContext.getInputManager();
        return inputManager.hideSoftInputFromWindow(focus.getWindowToken(), 0);
    }

    /**
     * 动态显示软键盘
     */
    public static boolean showSoftInput(@NonNull View focus) {
        focus.setFocusable(true);
        focus.setFocusableInTouchMode(true);
        focus.requestFocus();
        Log.d(LOG_TAG, "hideSoftInput");
        InputMethodManager inputManager = AppContext.getInputManager();
        return inputManager.showSoftInput(focus, 0);
    }

    /**
     * 切换键盘显示与否状态
     */
    public static void toggleSoftInput(@NonNull View focus) {
        focus.setFocusable(true);
        focus.setFocusableInTouchMode(true);
        focus.requestFocus();
        Log.d(LOG_TAG, "toggleSoftInput");
        InputMethodManager inputManager = AppContext.getInputManager();
        inputManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    /**
     * **********************************剪切板************************************
     * 复制文本到剪贴板
     */
    public static void copyText(@NonNull String text) {
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
    public static void copyUri(@NonNull Uri uri) {
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
    public static void copyIntent(@NonNull Intent intent) {
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
