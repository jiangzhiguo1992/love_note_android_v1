package com.jiangzg.lovenote.controller.activity.more;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.widget.FrameLayout;

import com.afollestad.materialdialogs.MaterialDialog;
import com.jiangzg.base.common.SPUtils;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.controller.activity.base.BaseActivity;
import com.jiangzg.lovenote.helper.common.ResHelper;
import com.jiangzg.lovenote.helper.common.SPHelper;
import com.jiangzg.lovenote.helper.view.DialogHelper;
import com.jiangzg.lovenote.helper.view.ViewHelper;
import com.just.agentweb.AgentWeb;
import com.just.agentweb.DefaultWebClient;
import com.just.agentweb.IAgentWebSettings;

import butterknife.BindView;

public class CatchBabyActivity extends BaseActivity<CatchBabyActivity> {

    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.flWeb)
    FrameLayout flWeb;

    private AgentWeb mAgentWeb;

    public static void goActivity(Activity from) {
        Intent intent = new Intent(from, CatchBabyActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_catch_baby;
    }

    @Override
    protected void initView(Intent intent, Bundle state) {
        ViewHelper.initTopBar(mActivity, tb, getString(R.string.nav_catch_baby), true);
        // p
        //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        //    String packageName = AppInfo.get().getPackageName();
        //    WebView.setDataDirectorySuffix(packageName);
        //}
        // help
        SharedPreferences sp = SPUtils.getSharedPreferences("share_catch_baby");
        if (!sp.getBoolean("no_first", false)) {
            showHelpDialog();
            // sp
            SharedPreferences.Editor editor = SPUtils.getSharedPreferences("share_catch_baby").edit();
            editor.putBoolean("no_first", true);
            editor.apply();
        }
        // url
        String url = SPHelper.getCooperation().getCatchBabyUrl();
        if (StringUtils.isEmpty(url)) {
            mActivity.finish();
            return;
        }
        if (!url.startsWith("http")) {
            url = "https://" + url;
        }
        // web
        mAgentWeb = AgentWeb.with(mActivity)
                .setAgentWebParent(flWeb, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT))
                .useDefaultIndicator()
                //.setAgentWebWebSettings()
                //.setWebChromeClient()
                //.setWebViewClient()
                .setSecurityType(AgentWeb.SecurityType.DEFAULT_CHECK)
                .setOpenOtherPageWays(DefaultWebClient.OpenOtherPageWays.ASK)
                .interceptUnkownUrl()
                .createAgentWeb()
                .ready()
                .go(url);
        // settings
        initWebViewSettings(false, false);
    }

    @Override
    protected void initData(Intent intent, Bundle state) {
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.help, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuHelp: // 帮助
                showHelpDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showHelpDialog() {
        MaterialDialog dialog = DialogHelper.getBuild(mActivity)
                .cancelable(true)
                .canceledOnTouchOutside(true)
                .content(R.string.catch_baby_help)
                .positiveText(R.string.i_know)
                .build();
        DialogHelper.showWithAnim(dialog);
    }

    @Override
    public void onBackPressed() {
        if (!mAgentWeb.back()) {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (mAgentWeb.handleKeyEvent(keyCode, event)) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onPause() {
        mAgentWeb.getWebLifeCycle().onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        mAgentWeb.getWebLifeCycle().onResume();
        super.onResume();
    }

    @Override
    protected void onFinish(Bundle state) {
        mAgentWeb.getWebLifeCycle().onDestroy();
    }

    @SuppressLint("SetJavaScriptEnabled")
    public void initWebViewSettings(boolean zoom, boolean cache) {
        // JS
        IAgentWebSettings agentWebSettings = mAgentWeb.getAgentWebSettings();
        WebSettings settings = agentWebSettings.getWebSettings();
        settings.setJavaScriptEnabled(true);// 支持JS
        settings.setJavaScriptCanOpenWindowsAutomatically(true);// 支持通过JS打开新窗口
        settings.setDomStorageEnabled(true); // 开启DOM 缓存
        // zoom
        settings.setSupportZoom(zoom); // 支持缩放
        settings.setBuiltInZoomControls(zoom); // 手势缩放
        settings.setDisplayZoomControls(false); // 按钮缩放
        settings.setLoadWithOverviewMode(!zoom); // 直接调整至屏幕的大小
        // cache
        settings.setAppCacheEnabled(cache);
        settings.setDatabaseEnabled(cache); // 开启database 缓存
        if (cache) { // 优先使用缓存
            settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
            settings.setAppCachePath(ResHelper.createWebCacheDir().getAbsolutePath());
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

}
