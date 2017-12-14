package com.jiangzg.base.view.widget;

import android.annotation.SuppressLint;
import android.os.Build;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.jiangzg.base.component.application.AppContext;
import com.jiangzg.base.file.FileUtils;

import java.util.Map;

/**
 * Created by gg on 2017/5/4.
 * WebView工具类
 */
public class WebUtils {

    private static final String LOG_TAG = "WebUtils";

    private WebView mWebView;
    private String cacheDir;
    private String cookie;

    public WebUtils(WebView webView) {
        mWebView = webView;
    }

    public void init(boolean js, boolean zoom, boolean cache) {
        //String resDir = AppInfo.get().getCacheDir();
        String resDir = "";
        cacheDir = resDir + "web_cache";
        FileUtils.createOrExistsFile(cacheDir);
        mWebView.requestFocusFromTouch(); // 支持获取手势焦点
        setJs(js);
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

    private void setJs(boolean support) {
        WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(support);// 支持JS
        settings.setJavaScriptCanOpenWindowsAutomatically(support);// 支持通过JS打开新窗口
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
        CookieSyncManager.createInstance(AppContext.get());
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
