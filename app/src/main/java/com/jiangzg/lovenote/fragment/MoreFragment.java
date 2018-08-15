package com.jiangzg.lovenote.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.jiangzg.base.time.DateUtils;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.activity.common.SettingsActivity;
import com.jiangzg.lovenote.activity.couple.CouplePairActivity;
import com.jiangzg.lovenote.activity.more.CoinActivity;
import com.jiangzg.lovenote.activity.more.SignActivity;
import com.jiangzg.lovenote.activity.more.VipActivity;
import com.jiangzg.lovenote.activity.settings.HelpActivity;
import com.jiangzg.lovenote.base.BaseFragment;
import com.jiangzg.lovenote.base.BasePagerFragment;
import com.jiangzg.lovenote.domain.Broadcast;
import com.jiangzg.lovenote.domain.Coin;
import com.jiangzg.lovenote.domain.Couple;
import com.jiangzg.lovenote.domain.Help;
import com.jiangzg.lovenote.domain.Result;
import com.jiangzg.lovenote.domain.Sign;
import com.jiangzg.lovenote.domain.Version;
import com.jiangzg.lovenote.domain.Vip;
import com.jiangzg.lovenote.helper.API;
import com.jiangzg.lovenote.helper.RetrofitHelper;
import com.jiangzg.lovenote.helper.SPHelper;
import com.jiangzg.lovenote.helper.TimeHelper;
import com.jiangzg.lovenote.helper.ViewHelper;
import com.jiangzg.lovenote.view.BroadcastBanner;
import com.jiangzg.lovenote.view.GSwipeRefreshLayout;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;

public class MoreFragment extends BasePagerFragment<MoreFragment> {

    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.srl)
    GSwipeRefreshLayout srl;
    @BindView(R.id.tvBroadcast)
    TextView tvBroadcast;
    @BindView(R.id.bb)
    BroadcastBanner bb;

    @BindView(R.id.cvVip)
    CardView cvVip;
    @BindView(R.id.tvVip)
    TextView tvVip;
    @BindView(R.id.cvCoin)
    CardView cvCoin;
    @BindView(R.id.tvCoin)
    TextView tvCoin;
    @BindView(R.id.cvSign)
    CardView cvSign;
    @BindView(R.id.tvSign)
    TextView tvSign;

    @BindView(R.id.cvWife)
    CardView cvWife;
    @BindView(R.id.tvWife)
    TextView tvWife;
    @BindView(R.id.cvLetter)
    CardView cvLetter;
    @BindView(R.id.tvLetter)
    TextView tvLetter;
    @BindView(R.id.cvDiscuss)
    CardView cvDiscuss;
    @BindView(R.id.tvDiscuss)
    TextView tvDiscuss;

    @BindView(R.id.cvWish)
    CardView cvWish;
    @BindView(R.id.tvWish)
    TextView tvWish;
    @BindView(R.id.cvPlane)
    CardView cvPlane;
    @BindView(R.id.tvPlane)
    TextView tvPlane;

    private Call<Result> call;
    private List<Broadcast> broadcastList;

    public static MoreFragment newFragment() {
        Bundle bundle = new Bundle();
        // bundle.putData();
        return BaseFragment.newInstance(MoreFragment.class, bundle);
    }

    @Override
    protected int getView(Bundle data) {
        return R.layout.fragment_more;
    }

    @Override
    protected void initView(@Nullable Bundle state) {
        ViewHelper.initTopBar(mActivity, tb, getString(R.string.nav_more), false);
        fitToolBar(tb);
        // srl
        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData();
            }
        });
        // broadcast
        bb.initView(mActivity);
    }

    protected void loadData() {
        // data
        refreshData();
    }

    @Override
    protected void onFinish(Bundle state) {
        RetrofitHelper.cancel(call);
    }

    @Override
    public void onStart() {
        super.onStart();
        // menu
        tb.invalidate();
    }

    // 不能和note的一样，会显示不出来
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        long noticeNoReadCount = SPHelper.getNoticeNoReadCount();
        Version version = SPHelper.getVersion();
        boolean redPoint = (noticeNoReadCount > 0) || (version != null);
        inflater.inflate(redPoint ? R.menu.help_settings_point : R.menu.help_settings, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuHelp: // 帮助
                HelpActivity.goActivity(mFragment, Help.INDEX_MORE_HOME);
                return true;
            case R.id.menuSettings: // 设置
                SettingsActivity.goActivity(mFragment);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick({R.id.cvVip, R.id.cvCoin, R.id.cvSign,
            R.id.cvWife, R.id.cvLetter, R.id.cvDiscuss,
            R.id.cvWish, R.id.cvPlane})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.cvVip: // 会员
                if (Couple.isBreak(SPHelper.getCouple())) {
                    CouplePairActivity.goActivity(mFragment);
                    return;
                }
                VipActivity.goActivity(mFragment);
                break;
            case R.id.cvCoin: // 金币
                if (Couple.isBreak(SPHelper.getCouple())) {
                    CouplePairActivity.goActivity(mFragment);
                    return;
                }
                CoinActivity.goActivity(mFragment);
                break;
            case R.id.cvSign: // 签到
                if (Couple.isBreak(SPHelper.getCouple())) {
                    CouplePairActivity.goActivity(mFragment);
                    return;
                }
                SignActivity.goActivity(mFragment);
                break;
            case R.id.cvWife: // 夫妻相
                // TODO
                break;
            case R.id.cvLetter: // 情书展
                // TODO
                break;
            case R.id.cvDiscuss: // 讨论会
                // TODO
                break;
            case R.id.cvWish: // 许愿树
                // TODO
                break;
            case R.id.cvPlane: // 纸飞机
                // TODO
                break;
        }
    }

    private void refreshData() {
        if (!srl.isRefreshing()) {
            srl.setRefreshing(true);
        }
        call = new RetrofitHelper().call(API.class).moreHomeGet();
        RetrofitHelper.enqueue(call, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                srl.setRefreshing(false);
                broadcastList = data.getBroadcastList();
                Vip vip = data.getVip();
                Coin coin = data.getCoin();
                Sign sign = data.getSign();
                // view
                initBroadcast(broadcastList);
                initVip(vip);
                initCoin(coin);
                initSign(sign);
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                srl.setRefreshing(false);
            }
        });
    }

    private void initBroadcast(List<Broadcast> broadcastList) {
        if (broadcastList == null || broadcastList.size() <= 0) {
            tvBroadcast.setVisibility(View.VISIBLE);
            bb.setVisibility(View.GONE);
        } else {
            tvBroadcast.setVisibility(View.GONE);
            bb.setVisibility(View.VISIBLE);
            bb.setDataList(broadcastList);
        }
    }

    private void initVip(Vip vip) {
        String vipInfo;
        if (vip == null || TimeHelper.getJavaTimeByGo(vip.getExpireAt()) <= DateUtils.getCurrentLong()) {
            vipInfo = "(*ˇωˇ*人)";
        }else {
            vipInfo = "";
        }
        tvVip.setText();
    }

    private void initCoin(Coin coin) {

    }

    private void initSign(Sign sign) {
        int continueDay = (sign == null) ? 0 : sign.getContinueDay();
        String signShow = String.format(Locale.getDefault(), getString(R.string.continue_holder_day), continueDay);
        tvSign.setText(signShow);
    }

}
