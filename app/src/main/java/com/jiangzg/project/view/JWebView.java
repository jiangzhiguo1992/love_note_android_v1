package com.jiangzg.project.view;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.android.base.view.widget.WebUtils;

/**
 * Created by JiangZhiGuo on 2016-11-7.
 * 通用WebView
 * 4.4以后WebView的操作尽量放在ui线程中去执行
 */
public class JWebView extends WebView {

    private Context mContext;
    private WebUtils webUtils;

    public JWebView(Context context) {
        super(context);
        init(context);
    }

    public JWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public JWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public JWebView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void init(Context context) {
        mContext = context;
        webUtils = new WebUtils(this);
        webUtils.init(true, false, false);
        webUtils.setViewClient(webViewClient);
        webUtils.setChromeClient(webChromeClient);
    }

    private WebViewClient webViewClient = new WebViewClient() {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            // loadingShow
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            webUtils.setCookie(url);
            super.onPageFinished(view, url);
            // loadingHide
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            webUtils.load(url);
            return true;
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            String url = request.getUrl().toString();
            webUtils.load(url);
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

}
