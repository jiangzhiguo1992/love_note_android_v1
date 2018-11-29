package com.jiangzg.lovenote.activity.more;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.activity.couple.CouplePairActivity;
import com.jiangzg.lovenote.activity.settings.HelpActivity;
import com.jiangzg.lovenote.adapter.CoinAdapter;
import com.jiangzg.lovenote.base.BaseActivity;
import com.jiangzg.lovenote.helper.ConsHelper;
import com.jiangzg.lovenote.helper.RecyclerHelper;
import com.jiangzg.lovenote.helper.RetrofitHelper;
import com.jiangzg.lovenote.helper.RxBus;
import com.jiangzg.lovenote.helper.SPHelper;
import com.jiangzg.lovenote.helper.ViewHelper;
import com.jiangzg.lovenote.model.api.API;
import com.jiangzg.lovenote.model.entity.Coin;
import com.jiangzg.lovenote.model.entity.Couple;
import com.jiangzg.lovenote.model.entity.Help;
import com.jiangzg.lovenote.model.entity.Result;
import com.jiangzg.lovenote.model.entity.User;
import com.jiangzg.lovenote.view.FrescoAvatarView;
import com.jiangzg.lovenote.view.GSwipeRefreshLayout;

import java.util.List;

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
    @BindView(R.id.btnBuy)
    Button btnBuy;
    @BindView(R.id.rv)
    RecyclerView rv;

    private Coin coin;
    private RecyclerHelper recyclerHelper;
    private Observable<Coin> obRefresh;
    private Call<Result> callGet;
    private Call<Result> callList;
    private int page;

    public static void goActivity(Fragment from) {
        if (Couple.isBreak(SPHelper.getCouple())) {
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
        // view
        refreshView();
        // data
        refreshData();
        // recycler
        recyclerHelper = new RecyclerHelper(rv)
                .initLayoutManager(new LinearLayoutManager(mActivity))
                .initRefresh(srl, false)
                .initAdapter(new CoinAdapter(mActivity))
                .viewEmpty(mActivity, R.layout.list_empty_grey, true, true)
                .viewLoadMore(new RecyclerHelper.MoreGreyView())
                .setAdapter()
                .listenerRefresh(new RecyclerHelper.RefreshListener() {
                    @Override
                    public void onRefresh() {
                        getCoinListData(false);
                    }
                })
                .listenerMore(new RecyclerHelper.MoreListener() {
                    @Override
                    public void onMore(int currentCount) {
                        getCoinListData(true);
                    }
                });
    }

    @Override
    protected void initData(Intent intent, Bundle state) {
        page = 0;
        // event
        obRefresh = RxBus.register(ConsHelper.EVENT_COIN_INFO_REFRESH, new Action1<Coin>() {
            @Override
            public void call(Coin coin) {
                refreshData();
                if (recyclerHelper != null) recyclerHelper.dataRefresh();
            }
        });
        // recyclerHelper
        recyclerHelper.dataRefresh();
    }

    @Override
    protected void onFinish(Bundle state) {
        RetrofitHelper.cancel(callGet);
        RetrofitHelper.cancel(callList);
        RxBus.unregister(ConsHelper.EVENT_COIN_INFO_REFRESH, obRefresh);
        RecyclerHelper.release(recyclerHelper);
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
                HelpActivity.goActivity(mActivity, Help.INDEX_MORE_COIN);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick({R.id.btnBuy})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnBuy: // 前往购买
                CoinBuyActivity.goActivity(mActivity);
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
            coinCount = String.valueOf(coin.getCount());
        } else {
            coinCount = String.valueOf(0);
        }
        tvCoinCount.setText(coinCount);

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

    private void getCoinListData(final boolean more) {
        page = more ? page + 1 : 0;
        // api
        callList = new RetrofitHelper().call(API.class).moreCoinListGet(page);
        RetrofitHelper.enqueue(callList, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                if (recyclerHelper == null) return;
                recyclerHelper.viewEmptyShow(data.getShow());
                List<Coin> coinList = data.getCoinList();
                recyclerHelper.dataOk(coinList, more);
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                if (recyclerHelper == null) return;
                recyclerHelper.dataFail(more, message);
            }
        });
    }

}
