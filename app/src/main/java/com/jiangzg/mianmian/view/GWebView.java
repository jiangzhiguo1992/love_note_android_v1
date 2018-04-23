package com.jiangzg.mianmian.view;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.jiangzg.base.application.AppInfo;
import com.jiangzg.base.common.FileUtils;
import com.jiangzg.mianmian.base.MyApp;

import java.io.File;
import java.util.Map;

/**
 * Created by JiangZhiGuo on 2016-11-7.
 * 通用WebView
 * 4.4以后WebView的操作尽量放在ui线程中去执行
 * TODO 搞一搞
 */
public class GWebView extends WebView {

    private WebManager webManager;

    public GWebView(Context context) {
        super(context);
        init(context);
    }

    public GWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public GWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public GWebView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void init(Context context) {
        webManager = new WebManager(this);
        webManager.init(false, false);
        webManager.setViewClient(webViewClient);
        webManager.setChromeClient(webChromeClient);
    }

    private WebViewClient webViewClient = new WebViewClient() {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            // loadingShow
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            webManager.setCookie(url);
            super.onPageFinished(view, url);
            // loadingHide
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            webManager.load(url);
            return true;
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            String url = request.getUrl().toString();
            webManager.load(url);
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

    private OnScrollChangedCallback mOnScrollChangedCallback;

    /* 这里的dx和dy代表的是x轴和y轴上的偏移量，你也可以自己把l, t, oldl, oldt四个参数暴露出来 */
    public interface OnScrollChangedCallback {
        void onScroll(int scrollX, int scrollY);
    }

    /* 滚动监听 */
    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (mOnScrollChangedCallback != null) {
            mOnScrollChangedCallback.onScroll(l - oldl, t - oldt);
        }
    }

    /* 设置滚动监听 */
    public void setOnScrollChangedCallback(final OnScrollChangedCallback onScrollChangedCallback) {
        mOnScrollChangedCallback = onScrollChangedCallback;
    }

    public class WebManager {

        private WebView mWebView;
        private String cacheDir;
        private String cookie;

        public WebManager(WebView webView) {
            mWebView = webView;
        }

        public void init(boolean zoom, boolean cache) {
            String appCacheDir = AppInfo.get().getInCacheDir();
            cacheDir = new File(appCacheDir, "web_cache").getAbsolutePath();
            FileUtils.createOrExistsFile(cacheDir);
            mWebView.requestFocusFromTouch(); // 支持获取手势焦点
            setJs();
            setZoom(zoom);
            setCache(cache);
            setImage();
            WebSettings settings = mWebView.getSettings();
            settings.setDefaultTextEncodingName("UTF-8");
            settings.supportMultipleWindows();  // 多窗口
            settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN); // 支持内容重新布局
            settings.setAllowFileAccess(true);  // 设置可以访问文件
            settings.setNeedInitialFocus(true); // 当webView调用requestFocus时为webView设置节点
        }

        private void setImage() {
            WebSettings settings = mWebView.getSettings();
            settings.setUseWideViewPort(true); // 将图片调整到适合webView的大小
            settings.setLoadsImagesAutomatically(true); // 支持自动加载图片
            settings.setBlockNetworkImage(false); // 图片加载放在最后来加载渲染 4.4需要为false
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) { // https里的http图片显示
                settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
            }
        }

        private void setZoom(boolean support) {
            WebSettings settings = mWebView.getSettings();
            settings.setSupportZoom(support); // 支持缩放
            settings.setBuiltInZoomControls(support); // 手势缩放
            settings.setDisplayZoomControls(false); // 按钮缩放
            settings.setLoadWithOverviewMode(!support); // 直接调整至屏幕的大小
        }

        private void setCache(boolean support) {
            WebSettings settings = mWebView.getSettings();
            settings.setAppCacheEnabled(support);
            settings.setDomStorageEnabled(support); // 开启DOM 缓存
            settings.setDatabaseEnabled(support); // 开启database 缓存
            if (support) { // 优先使用缓存
                settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
                settings.setAppCachePath(cacheDir);
            } else { // 不用缓存
                settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
            }
        }

        @SuppressLint("SetJavaScriptEnabled")
        private void setJs() {
            WebSettings settings = mWebView.getSettings();
            settings.setJavaScriptEnabled(true);// 支持JS
            settings.setJavaScriptCanOpenWindowsAutomatically(true);// 支持通过JS打开新窗口
        }

        /**
         * 添加JSBridge
         *
         * @param jsInterface js实现类
         * @param name        js名
         */
        @SuppressLint({"AddJavascriptInterface", "JavascriptInterface"})
        public void addJs(Object jsInterface, String name) {
            mWebView.addJavascriptInterface(jsInterface, name);
        }

        /**
         * WebViewClient可以辅助WebView处理各种通知,请求等事件
         */
        public void setViewClient(WebViewClient webViewClient) {
            mWebView.setWebViewClient(webViewClient);
        }

        /**
         * WebChromeClient专门用来辅助WebView处理js的对话框,网站title,网站图标,加载进度条等
         */
        public void setChromeClient(WebChromeClient webChromeClient) {
            mWebView.setWebChromeClient(webChromeClient);
        }

        public void load(String url) {
            mWebView.loadUrl(url);
        }

        public void load(String url, Map<String, String> headers) {
            mWebView.loadUrl(url, headers);
        }

        public void setCookie(String url) {
            CookieManager instance = CookieManager.getInstance();
            cookie = instance.getCookie(url);
        }

        /**
         * 带cookie的加载url
         */
        public void loadCookie(String url) {
            CookieSyncManager.createInstance(MyApp.get());
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.setAcceptCookie(true);
            cookieManager.setCookie(url, cookie);  // cookies是要设置的cookie字符串
            CookieSyncManager.getInstance().sync();
        }

        /**
         * 刷新界面
         */
        public void refresh() {
            mWebView.reload();
        }

        public void clearCache() {
            mWebView.clearCache(true);
            mWebView.clearHistory();
            mWebView.clearFormData();
            FileUtils.deleteDir(cacheDir);
        }

        /**
         * 回退
         */
        public boolean canGoBack() {
            if (mWebView.canGoBack()) {
                mWebView.goBack();
                return false;
            } else {
                return true;
            }
        }

        /**
         * 滚动到顶部
         */
        public void scrollTop() {
            mWebView.setScrollY(0);
        }

        /**
         * 缩放页面
         */
        public void setZoom(int scale) {
            mWebView.setInitialScale(scale); // 25最小缩放等级
        }

        /**
         * 缩放字体
         */
        public void setTextZoom(int size) {
            mWebView.getSettings().setTextZoom(size); // WebSettings.TextSize.LARGER.ordinal()
        }

    }

}
