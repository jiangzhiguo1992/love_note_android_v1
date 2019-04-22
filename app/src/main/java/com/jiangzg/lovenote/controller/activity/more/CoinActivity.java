package com.jiangzg.lovenote.controller.activity.more;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.controller.activity.base.BaseActivity;
import com.jiangzg.lovenote.controller.activity.couple.CouplePairActivity;
import com.jiangzg.lovenote.controller.activity.settings.HelpActivity;
import com.jiangzg.lovenote.helper.common.RxBus;
import com.jiangzg.lovenote.helper.common.SPHelper;
import com.jiangzg.lovenote.helper.common.UserHelper;
import com.jiangzg.lovenote.helper.system.RetrofitHelper;
import com.jiangzg.lovenote.helper.view.ViewHelper;
import com.jiangzg.lovenote.model.api.API;
import com.jiangzg.lovenote.model.api.Result;
import com.jiangzg.lovenote.model.entity.Coin;
import com.jiangzg.lovenote.model.entity.User;
import com.jiangzg.lovenote.view.FrescoAvatarView;
import com.jiangzg.lovenote.view.GSwipeRefreshLayout;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;
import rx.Observable;

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
    @BindView(R.id.cvInPay)
    CardView cvInPay;
    @BindView(R.id.cvInSign)
    CardView cvInSign;
    @BindView(R.id.cvInWife)
    CardView cvInWife;
    @BindView(R.id.cvInLetter)
    CardView cvInLetter;
    @BindView(R.id.cvOutWife)
    CardView cvOutWife;
    @BindView(R.id.cvOutLetter)
    CardView cvOutLetter;
    @BindView(R.id.cvOutWish)
    CardView cvOutWish;
    @BindView(R.id.cvOutCard)
    CardView cvOutCard;

    private Coin coin;

    public static void goActivity(Fragment from) {
        if (UserHelper.isCoupleBreak(SPHelper.getCouple())) {
            CouplePairActivity.goActivity(from);
            return;
        }
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
        // pay
        btnBuy.setVisibility(SPHelper.getModelShow().isMarketPay() ? View.VISIBLE : View.GONE);
        cvInPay.setVisibility(SPHelper.getModelShow().isMarketPay() ? View.VISIBLE : View.GONE);
        // view
        refreshView();
    }

    @Override
    protected void initData(Intent intent, Bundle state) {
        // event
        Observable<Coin> bus = RxBus.register(RxBus.EVENT_COIN_INFO_REFRESH, coin -> {
            refreshData();
        });
        pushBus(RxBus.EVENT_COIN_INFO_REFRESH, bus);
        // avatar
        User me = SPHelper.getMe();
        User ta = SPHelper.getTa();
        String myAvatar = UserHelper.getMyAvatar(me);
        String taAvatar = UserHelper.getTaAvatar(me);
        ivAvatarLeft.setData(taAvatar, ta);
        ivAvatarRight.setData(myAvatar, me);
        // data
        refreshData();
    }

    @Override
    protected void onFinish(Bundle state) {
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
                HelpActivity.goActivity(mActivity, HelpActivity.INDEX_MORE_COIN);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick({R.id.btnHistory, R.id.btnBuy,
            R.id.cvInPay, R.id.cvInSign, R.id.cvInWife, R.id.cvInLetter,
            R.id.cvOutWife, R.id.cvOutLetter, R.id.cvOutWish, R.id.cvOutCard})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnHistory: // 获取历史
                CoinListActivity.goActivity(mActivity);
                break;
            case R.id.btnBuy: // 前往购买
                CoinBuyActivity.goActivity(mActivity);
                break;
            case R.id.cvInPay:
                CoinBuyActivity.goActivity(mActivity);
                break;
            case R.id.cvInSign:
                SignActivity.goActivity(mActivity);
                break;
            case R.id.cvInWife:
                MatchWifeActivity.goActivity(mActivity);
                break;
            case R.id.cvInLetter:
                MatchLetterActivity.goActivity(mActivity);
                break;
            case R.id.cvOutWife:
                MatchWifeActivity.goActivity(mActivity);
                break;
            case R.id.cvOutLetter:
                MatchLetterActivity.goActivity(mActivity);
                break;
            case R.id.cvOutWish:
                break;
            case R.id.cvOutCard:
                break;
        }
    }

    private void refreshData() {
        if (!srl.isRefreshing()) {
            srl.setRefreshing(true);
        }
        // api
        Call<Result> api = new RetrofitHelper().call(API.class).moreCoinHomeGet();
        RetrofitHelper.enqueue(api, null, new RetrofitHelper.CallBack() {
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
        pushApi(api);
    }

    private void refreshView() {
        // coin
        String coinCount;
        if (coin != null) {
            coinCount = String.valueOf(coin.getCount());
        } else {
            coinCount = String.valueOf(0);
        }
        tvCoinCount.setText(coinCount);
    }

}
