package com.jiangzg.base.system;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.jiangzg.base.application.AppBase;
import com.jiangzg.base.common.LogUtils;
import com.jiangzg.base.common.StringUtils;

/**
 * Created by JiangZhiGuo on 2016/10/13.
 * describe 输入管理工具类
 */
public class InputUtils {

    private static final String LOG_TAG = "InputUtils";

    /**
     * 软键盘设置, 要在setContentView之前调用
     */
    public static void initActivity(Activity activity) {
        if (activity == null) {
            LogUtils.w(LOG_TAG, "initActivity: activity == null");
            return;
        }
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
        if (focus == null) {
            LogUtils.w(LOG_TAG, "hideSoftInput: focus == null");
            return false;
        }
        focus.clearFocus();
        InputMethodManager inputManager = AppBase.getInputManager();
        return inputManager.hideSoftInputFromWindow(focus.getWindowToken(), 0);
    }

    /**
     * 动态显示软键盘
     */
    public static boolean showSoftInput(View focus) {
        if (focus == null) {
            LogUtils.w(LOG_TAG, "showSoftInput: focus == null");
            return false;
        }
        focus.setFocusable(true);
        focus.setFocusableInTouchMode(true);
        focus.requestFocus();
        InputMethodManager inputManager = AppBase.getInputManager();
        return inputManager.showSoftInput(focus, 0);
    }

    /**
     * 切换键盘显示与否状态
     */
    public static void toggleSoftInput(View focus) {
        if (focus == null) {
            LogUtils.w(LOG_TAG, "toggleSoftInput: focus == null");
            return;
        }
        focus.setFocusable(true);
        focus.setFocusableInTouchMode(true);
        focus.requestFocus();
        InputMethodManager inputManager = AppBase.getInputManager();
        inputManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    /**
     * **********************************剪切板************************************
     * 复制文本到剪贴板
     */
    public static void copyText(String text) {
        if (StringUtils.isEmpty(text)) {
            LogUtils.w(LOG_TAG, "copyText: text == null");
            return;
        }
        ClipData myClip = ClipData.newPlainText("text", text);
        AppBase.getClipboardManager().setPrimaryClip(myClip);
    }

    /**
     * 获取剪贴板的文本
     */
    public static CharSequence getCopyText() {
        CharSequence copy = "";
        ClipData clip = AppBase.getClipboardManager().getPrimaryClip();
        if (clip != null && clip.getItemCount() > 0)
            copy = clip.getItemAt(0).coerceToText(AppBase.getInstance());
        return copy;
    }

    /**
     * 复制uri到剪贴板
     */
    public static void copyUri(Uri uri) {
        if (uri == null) {
            LogUtils.w(LOG_TAG, "copyUri: uri == null");
            return;
        }
        ClipData clipData = ClipData.newUri(AppBase.getInstance().getContentResolver(), "uri", uri);
        AppBase.getClipboardManager().setPrimaryClip(clipData);
    }

    /**
     * 获取剪贴板的uri
     */
    public static Uri getCopyUri() {
        ClipData clip = AppBase.getClipboardManager().getPrimaryClip();
        if (clip == null || clip.getItemCount() < 1) return null;
        return clip.getItemAt(0).getUri();
    }

    /**
     * 复制意图到剪贴板
     */
    public static void copyIntent(Intent intent) {
        if (intent == null) {
            LogUtils.w(LOG_TAG, "copyIntent: intent == null");
            return;
        }
        ClipData clipData = ClipData.newIntent("intent", intent);
        AppBase.getClipboardManager().setPrimaryClip(clipData);
    }

    /**
     * 获取剪贴板的意图
     */
    public static Intent getCopyIntent() {
        ClipData clip = AppBase.getClipboardManager().getPrimaryClip();
        if (clip == null || clip.getItemCount() < 1) return null;
        return clip.getItemAt(0).getIntent();
    }
}
