package com.jiangzg.lovenote.controller.activity.common;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.Toolbar;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.jiangzg.base.application.AppInfo;
import com.jiangzg.base.common.FileUtils;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.controller.activity.base.BaseActivity;
import com.jiangzg.lovenote.helper.view.ViewHelper;

import java.io.File;

import butterknife.BindView;

public class WebActivity extends BaseActivity<WebActivity> {

    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.wv)
    WebView wv;

    public static void goActivity(Activity from, String title, String url) {
        if (StringUtils.isEmpty(url)) return;
        Intent intent = new Intent(from, WebActivity.class);
        intent.putExtra("title", title);
        intent.putExtra("url", url);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_web;
    }

    @Override
    protected void initView(Intent intent, Bundle state) {
        String title = intent.getStringExtra("title");
        ViewHelper.initTopBar(mActivity, tb, title, true);
        // view
        initWebView(false, false);
        wv.setWebViewClient(webViewClient);
        wv.setWebChromeClient(webChromeClient);
    }

    @Override
    protected void initData(Intent intent, Bundle state) {
        String url = intent.getStringExtra("url");
        if (StringUtils.isEmpty(url)) {
            mActivity.finish();
            return;
        }
        if (!url.startsWith("http")) {
            url = "http://" + url;
        }
        wv.loadUrl(url);
    }

    @Override
    protected void onFinish(Bundle state) {
    }

    @SuppressLint("SetJavaScriptEnabled")
    public void initWebView(boolean zoom, boolean cache) {
        String appCacheDir = AppInfo.get().getInCacheDir();
        String cacheDir = new File(appCacheDir, "web_cache").getAbsolutePath();
        FileUtils.createOrExistsFile(cacheDir);
        wv.requestFocusFromTouch(); // 支持获取手势焦点
        // JS
        WebSettings settings = wv.getSettings();
        settings.setJavaScriptEnabled(true);// 支持JS
        settings.setJavaScriptCanOpenWindowsAutomatically(true);// 支持通过JS打开新窗口
        // zoom
        settings.setSupportZoom(zoom); // 支持缩放
        settings.setBuiltInZoomControls(zoom); // 手势缩放
        settings.setDisplayZoomControls(false); // 按钮缩放
        settings.setLoadWithOverviewMode(!zoom); // 直接调整至屏幕的大小
        // cache
        settings.setAppCacheEnabled(cache);
        settings.setDomStorageEnabled(cache); // 开启DOM 缓存
        settings.setDatabaseEnabled(cache); // 开启database 缓存
        if (cache) { // 优先使用缓存
            settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
            settings.setAppCachePath(cacheDir);
        } else { // 不用缓存
            settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        }
        // image
        settings.setUseWideViewPort(true); // 将图片调整到适合webView的大小
        settings.setLoadsImagesAutomatically(true); // 支持自动加载图片
        settings.setBlockNetworkImage(false); // 图片加载放在最后来加载渲染 4.4需要为false
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) { // https里的http图片显示
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        // other
        settings.setDefaultTextEncodingName("UTF-8");
        settings.supportMultipleWindows();  // 多窗口
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN); // 支持内容重新布局
        settings.setAllowFileAccess(true);  // 设置可以访问文件
        settings.setNeedInitialFocus(true); // 当webView调用requestFocus时为webView设置节点
    }

    private WebViewClient webViewClient = new WebViewClient() {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            // loadingShow
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            //setCookie(url);
            super.onPageFinished(view, url);
            // loadingHide
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            wv.loadUrl(url);
            return true;
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            String url = request.getUrl().toString();
            wv.loadUrl(url);
            return true;
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
            // error
        }
    };

    private WebChromeClient webChromeClient = new WebChromeClient() {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            // loadingUpdate
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            // custom title
        }

        @Override
        public void onReceivedIcon(WebView view, Bitmap icon) {
            super.onReceivedIcon(view, icon);
            // custom icon
        }

        @Override
        public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
            return super.onCreateWindow(view, isDialog, isUserGesture, resultMsg);
        }
    };

    // 回退
    @Override
    public void onBackPressed() {
        if (wv.canGoBack()) {
            wv.goBack();
        } else {
            super.onBackPressed();
        }
    }

}
