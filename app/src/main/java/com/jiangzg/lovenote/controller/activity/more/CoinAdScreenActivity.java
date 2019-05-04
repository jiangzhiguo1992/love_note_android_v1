package com.jiangzg.lovenote.controller.activity.more;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jiangzg.base.common.LogUtils;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.base.system.PermUtils;
import com.jiangzg.base.view.ToastUtils;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.controller.activity.base.BaseActivity;
import com.jiangzg.lovenote.helper.common.SPHelper;
import com.jiangzg.lovenote.helper.view.DialogHelper;
import com.jiangzg.lovenote.model.entity.AdInfo;
import com.qq.e.ads.splash.SplashAD;
import com.qq.e.ads.splash.SplashADListener;
import com.qq.e.comm.util.AdError;

import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;

public class CoinAdScreenActivity extends BaseActivity<CoinAdScreenActivity> {

    @BindView(R.id.llDesc)
    LinearLayout llDesc;
    @BindView(R.id.tvDesc)
    TextView tvDesc;
    @BindView(R.id.btnBack)
    Button btnBack;
    @BindView(R.id.btnStart)
    Button btnStart;
    @BindView(R.id.tvHolder)
    TextView tvHolder;
    @BindView(R.id.rlAd)
    RelativeLayout rlAd;

    private long adTick;

    public static void goActivity(Activity from) {
        PermUtils.requestPermissions(from, BaseActivity.REQUEST_APP_INFO, PermUtils.appInfo, new PermUtils.OnPermissionListener() {
            @Override
            public void onPermissionGranted(int requestCode, String[] permissions) {
                Intent intent = new Intent(from, CoinAdScreenActivity.class);
                // intent.putExtra();
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                ActivityTrans.start(from, intent);
            }

            @Override
            public void onPermissionDenied(int requestCode, String[] permissions) {
                DialogHelper.showGoPermDialog(from);
            }
        });
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_coin_ad_screen;
    }

    @Override
    protected void initView(Intent intent, Bundle state) {
        int browseChange = 1; // TODO
        int clickChange = 5; // TODO
        int coinAdLimit = 5; // TODO
        tvDesc.setText(String.format(Locale.getDefault(), getString(R.string.coin_ad_desc), browseChange, clickChange, coinAdLimit));
    }

    @Override
    protected void initData(Intent intent, Bundle state) {
        initViewStart();
    }

    @Override
    protected void onFinish(Bundle state) {
    }

    @OnClick({R.id.btnBack, R.id.btnStart})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnBack:
                mActivity.finish();
                break;
            case R.id.btnStart:
                fetchAd();
                break;
        }
    }

    private void initViewStart() {
        llDesc.setVisibility(View.VISIBLE);
        tvHolder.setVisibility(View.GONE);
        rlAd.removeAllViews();
        adTick = 5000; // TODO
    }

    private void initViewFetch() {
        llDesc.setVisibility(View.GONE);
        tvHolder.setVisibility(View.VISIBLE);
        rlAd.removeAllViews();
        adTick = 5000; // TODO
    }

    private void initViewAD() {
        llDesc.setVisibility(View.GONE);
        tvHolder.setVisibility(View.GONE);
    }

    private void fetchAd() {
        initViewFetch();
        // ad
        AdInfo adInfo = SPHelper.getAdInfo();
        String appId = adInfo.getAppId();
        String posId = "6030660059353673"; // TODO
        int screenDuration = 5000; // TODO
        new SplashAD(mActivity, rlAd, appId, posId, listener, screenDuration);
    }

    private SplashADListener listener = new SplashADListener() {
        @Override
        public void onNoAD(AdError error) {
            LogUtils.w(CoinAdScreenActivity.class, "SplashADListener", "onNoAD: " + error.getErrorCode() + "\n" + error.getErrorMsg());
            ToastUtils.show(mActivity.getString(R.string.ad_fetch_fail));
            initViewStart();
        }

        @Override
        public void onADPresent() {
            LogUtils.d(CoinAdScreenActivity.class, "SplashADListener", "onADPresent");
            initViewAD();
        }

        @Override
        public void onADTick(long l) {
            LogUtils.d(CoinAdScreenActivity.class, "SplashADListener", "onADTick " + l);
            adTick = l;
        }

        @Override
        public void onADClicked() {
            LogUtils.d(CoinAdScreenActivity.class, "SplashADListener", "onADClicked");
            getCoin(true);
        }

        @Override
        public void onADExposure() {
            LogUtils.d(CoinAdScreenActivity.class, "SplashADListener", "onADExposure");
            getCoin(false);
        }

        @Override
        public void onADDismissed() {
            LogUtils.d(CoinAdScreenActivity.class, "SplashADListener", "onADDismissed");
            initViewStart();
        }
    };

    private void getCoin(Boolean click) {
        if (!click && adTick >= 1000) return;
        // TODO
        ToastUtils.show("获取金币");
        initViewStart();
    }

}
