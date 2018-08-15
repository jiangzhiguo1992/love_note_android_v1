package com.jiangzg.lovenote.activity.more;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.base.view.ToastUtils;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.base.BaseActivity;
import com.jiangzg.lovenote.domain.Coin;
import com.jiangzg.lovenote.domain.Limit;
import com.jiangzg.lovenote.domain.Result;
import com.jiangzg.lovenote.domain.User;
import com.jiangzg.lovenote.helper.API;
import com.jiangzg.lovenote.helper.ConsHelper;
import com.jiangzg.lovenote.helper.CountHelper;
import com.jiangzg.lovenote.helper.RetrofitHelper;
import com.jiangzg.lovenote.helper.RxBus;
import com.jiangzg.lovenote.helper.SPHelper;
import com.jiangzg.lovenote.helper.ViewHelper;
import com.jiangzg.lovenote.view.FrescoAvatarView;
import com.jiangzg.lovenote.view.GSwipeRefreshLayout;

import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;
import rx.Observable;
import rx.functions.Action1;

public class CoinActivity extends BaseActivity<CoinActivity> {

    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.srl)
    GSwipeRefreshLayout srl;

    @BindView(R.id.ivAvatarLeft)
    FrescoAvatarView ivAvatarLeft;
    @BindView(R.id.tvCoinCount)
    TextView tvCoinCount;
    @BindView(R.id.ivAvatarRight)
    FrescoAvatarView ivAvatarRight;

    @BindView(R.id.btnHistory)
    Button btnHistory;
    @BindView(R.id.btnBuy)
    Button btnBuy;
    @BindView(R.id.tvGetInfo)
    TextView tvGetInfo;

    private Coin coin;
    private Call<Result> callGet;
    private Observable<Coin> obRefresh;

    public static void goActivity(Fragment from) {
        Intent intent = new Intent(from.getActivity(), CoinActivity.class);
        // intent.putExtra();
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_coin;
    }

    @Override
    protected void initView(Intent intent, Bundle state) {
        ViewHelper.initTopBar(mActivity, tb, getString(R.string.coin), true);
        srl.setEnabled(false);
        // view
        refreshView();
        // data
        refreshData();
    }

    @Override
    protected void initData(Intent intent, Bundle state) {
        // event
        obRefresh = RxBus.register(ConsHelper.EVENT_COIN_INFO_REFRESH, new Action1<Coin>() {
            @Override
            public void call(Coin coin) {
                refreshData();
            }
        });
    }

    @Override
    protected void onFinish(Bundle state) {
        RetrofitHelper.cancel(callGet);
        RxBus.unregister(ConsHelper.EVENT_COIN_INFO_REFRESH, obRefresh);
    }

    @OnClick({R.id.btnHistory, R.id.btnBuy})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnHistory: // 历史信息
                CoinListActivity.goActivity(mActivity);
                break;
            case R.id.btnBuy: // 前往购买
                ToastUtils.show("敬请期待");
                //CoinBuyActivity.goActivity(mActivity);
                break;
        }
    }

    private void refreshView() {
        // avatar
        User me = SPHelper.getMe();
        String myAvatar = me.getMyAvatarInCp();
        String taAvatar = me.getTaAvatarInCp();
        ivAvatarRight.setData(myAvatar);
        ivAvatarLeft.setData(taAvatar);
        // coin
        String coinCount;
        if (coin != null) {
            coinCount = CountHelper.getShowCount2Thousand(coin.getCount());
        } else {
            coinCount = String.valueOf(0);
        }
        tvCoinCount.setText(coinCount);
        // info
        String base = getString(R.string.coin_get_desc);
        Limit limit = SPHelper.getLimit();
        int coinSignMinCount = limit.getCoinSignMinCount();
        int coinSignContinueIncreaseCount = limit.getCoinSignIncreaseCount();
        int coinSignMaxCount = limit.getCoinSignMaxCount();
        int coinWifePostAddCount = limit.getCoinWifeAddCount();
        int coinLetterPostAddCount = limit.getCoinLetterAddCount();
        int coinDiscussAddCount = limit.getCoinDiscussAddCount();
        String wife = getString(R.string.nav_wife);
        String letter = getString(R.string.nav_letter);
        String discuss = getString(R.string.nav_discuss);
        String getInfo = String.format(Locale.getDefault(), base,
                coinSignMinCount, coinSignContinueIncreaseCount, coinSignMaxCount,
                wife, coinWifePostAddCount, letter, coinLetterPostAddCount, discuss, coinDiscussAddCount);
        tvGetInfo.setText(getInfo);
    }

    private void refreshData() {
        if (!srl.isRefreshing()) {
            srl.setRefreshing(true);
        }
        callGet = new RetrofitHelper().call(API.class).moreCoinHomeGet();
        RetrofitHelper.enqueue(callGet, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                srl.setRefreshing(false);
                coin = data.getCoin();
                refreshView();
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                srl.setRefreshing(false);
            }
        });
    }

}
