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
import com.jiangzg.lovenote.helper.common.RxBus;
import com.jiangzg.lovenote.helper.common.SPHelper;
import com.jiangzg.lovenote.helper.system.RetrofitHelper;
import com.jiangzg.lovenote.helper.view.DialogHelper;
import com.jiangzg.lovenote.model.api.API;
import com.jiangzg.lovenote.model.api.Result;
import com.jiangzg.lovenote.model.entity.AdInfo;
import com.jiangzg.lovenote.model.entity.Coin;
import com.jiangzg.lovenote.model.entity.Limit;
import com.qq.e.ads.splash.SplashAD;
import com.qq.e.ads.splash.SplashADListener;
import com.qq.e.comm.util.AdError;

import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;

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
        Limit limit = SPHelper.getLimit();
        int watchChange = limit.getCoinAdWatchCount();
        int clickChange = limit.getCoinAdClickCount();
        int betweenMin = limit.getCoinAdBetweenSec() / 60;
        int maxCount = limit.getCoinAdMaxPerDayCount();
        tvDesc.setText(String.format(Locale.getDefault(), getString(R.string.coin_ad_desc), watchChange, clickChange, betweenMin, maxCount));
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
        adTick = SPHelper.getAdInfo().getCoinFreeTickSec() * 1000;
    }

    private void initViewFetch() {
        llDesc.setVisibility(View.GONE);
        tvHolder.setVisibility(View.VISIBLE);
        rlAd.removeAllViews();
        adTick = SPHelper.getAdInfo().getCoinFreeTickSec() * 1000;
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
        String posId = adInfo.getCoinFreePosId();
        int screenDuration = adInfo.getCoinFreeTickSec() * 1000;
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

    private void getCoin(boolean click) {
        if (!click && adTick >= 1000) return;
        Coin body = new Coin();
        body.setKind(click ? Coin.KIND_ADD_BY_AD_CLICK : Coin.KIND_ADD_BY_AD_WATCH);
        Call<Result> api = new RetrofitHelper().call(API.class).moreCoinAdd(body);
        // api
        RetrofitHelper.enqueue(api, mActivity.getLoading(true), new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                // event
                RxBus.post(new RxBus.Event<>(RxBus.EVENT_COIN_INFO_REFRESH, new Coin()));
                // view
                initViewStart();
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                initViewStart();
            }
        });
        pushApi(api);
    }

}
