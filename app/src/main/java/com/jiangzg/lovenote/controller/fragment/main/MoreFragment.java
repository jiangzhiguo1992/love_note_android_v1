package com.jiangzg.lovenote.controller.fragment.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jiangzg.base.common.DateUtils;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.controller.activity.more.CatchBabyActivity;
import com.jiangzg.lovenote.controller.activity.more.CoinActivity;
import com.jiangzg.lovenote.controller.activity.more.MatchLetterActivity;
import com.jiangzg.lovenote.controller.activity.more.MatchLetterListActivity;
import com.jiangzg.lovenote.controller.activity.more.MatchWifeActivity;
import com.jiangzg.lovenote.controller.activity.more.MatchWifeListActivity;
import com.jiangzg.lovenote.controller.activity.more.SignActivity;
import com.jiangzg.lovenote.controller.activity.more.VipActivity;
import com.jiangzg.lovenote.controller.activity.settings.HelpActivity;
import com.jiangzg.lovenote.controller.activity.settings.SettingsActivity;
import com.jiangzg.lovenote.controller.fragment.base.BaseFragment;
import com.jiangzg.lovenote.controller.fragment.base.BasePagerFragment;
import com.jiangzg.lovenote.helper.common.RxBus;
import com.jiangzg.lovenote.helper.common.SPHelper;
import com.jiangzg.lovenote.helper.common.ShowHelper;
import com.jiangzg.lovenote.helper.common.TimeHelper;
import com.jiangzg.lovenote.helper.system.RetrofitHelper;
import com.jiangzg.lovenote.helper.view.ViewHelper;
import com.jiangzg.lovenote.model.api.API;
import com.jiangzg.lovenote.model.api.Result;
import com.jiangzg.lovenote.model.entity.Broadcast;
import com.jiangzg.lovenote.model.entity.Coin;
import com.jiangzg.lovenote.model.entity.CommonCount;
import com.jiangzg.lovenote.model.entity.Cooperation;
import com.jiangzg.lovenote.model.entity.MatchPeriod;
import com.jiangzg.lovenote.model.entity.ModelShow;
import com.jiangzg.lovenote.model.entity.Sign;
import com.jiangzg.lovenote.model.entity.Vip;
import com.jiangzg.lovenote.view.BroadcastBanner;
import com.jiangzg.lovenote.view.GSwipeRefreshLayout;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;
import rx.Observable;

public class MoreFragment extends BasePagerFragment<MoreFragment> {

    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.srl)
    GSwipeRefreshLayout srl;
    @BindView(R.id.tvBroadcast)
    TextView tvBroadcast;
    @BindView(R.id.bb)
    BroadcastBanner bb;

    @BindView(R.id.linePay)
    LinearLayout linePay;
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

    @BindView(R.id.lineMatch)
    LinearLayout lineMatch;
    @BindView(R.id.llMatch)
    LinearLayout llMatch;
    @BindView(R.id.cvWife)
    CardView cvWife;
    @BindView(R.id.tvWife)
    TextView tvWife;
    @BindView(R.id.cvLetter)
    CardView cvLetter;
    @BindView(R.id.tvLetter)
    TextView tvLetter;

    @BindView(R.id.lineFeature)
    LinearLayout lineFeature;
    @BindView(R.id.llFeature)
    LinearLayout llFeature;
    @BindView(R.id.cvWish)
    CardView cvWish;
    @BindView(R.id.tvWish)
    TextView tvWish;
    @BindView(R.id.cvPostcard)
    CardView cvPostcard;
    @BindView(R.id.tvPostcard)
    TextView tvPostcard;

    @BindView(R.id.lineMore)
    LinearLayout lineMore;
    @BindView(R.id.llMore)
    LinearLayout llMore;
    @BindView(R.id.cvBaby)
    CardView cvBaby;
    @BindView(R.id.tvBaby)
    TextView tvBaby;

    private MatchPeriod wifePeriod, letterPeriod;

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
        ViewHelper.initTopBar(mActivity, tb, mActivity.getString(R.string.nav_more), false);
        fitToolBar(tb);
        // show
        ModelShow modelShow = SPHelper.getModelShow();
        Cooperation cooperation = SPHelper.getCooperation();
        boolean marketPay = modelShow.isMarketPay();
        boolean moreVip = modelShow.isMoreVip();
        boolean moreCoin = modelShow.isMoreCoin();
        boolean moreMatch = modelShow.isMoreMatch();
        boolean moreFeature = false;
        boolean moreBaby = cooperation.isCatchBabyEnable();
        linePay.setVisibility((moreVip && moreCoin) ? View.VISIBLE : View.GONE);
        cvVip.setVisibility((moreVip && marketPay) ? View.VISIBLE : View.GONE);
        cvCoin.setVisibility(moreCoin ? View.VISIBLE : View.GONE);
        cvSign.setVisibility(moreCoin ? View.VISIBLE : View.GONE);
        lineMatch.setVisibility(moreMatch ? View.VISIBLE : View.GONE);
        llMatch.setVisibility(moreMatch ? View.VISIBLE : View.GONE);
        lineFeature.setVisibility(moreFeature ? View.VISIBLE : View.GONE);
        llFeature.setVisibility(moreFeature ? View.VISIBLE : View.GONE);
        lineMore.setVisibility(moreBaby ? View.VISIBLE : View.GONE);
        llMore.setVisibility(moreBaby ? View.VISIBLE : View.GONE);
        cvBaby.setVisibility(moreBaby ? View.VISIBLE : View.GONE);
        tvBaby.setText(cooperation.getCatchBabyDesc());
        // srl
        srl.setOnRefreshListener(this::refreshData);
        // broadcast
        bb.initView(mActivity);
        initBroadcast(null);
    }

    protected void loadData() {
        // event
        Observable<Vip> busVip = RxBus.register(RxBus.EVENT_VIP_INFO_REFRESH, vip -> refreshData());
        pushBus(RxBus.EVENT_VIP_INFO_REFRESH, busVip);
        Observable<Coin> busCoin = RxBus.register(RxBus.EVENT_COIN_INFO_REFRESH, coin -> refreshData());
        pushBus(RxBus.EVENT_COIN_INFO_REFRESH, busCoin);
        // data
        refreshData();
    }

    @Override
    protected void onFinish(Bundle state) {
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
        CommonCount commonCount = SPHelper.getCommonCount();
        boolean redPoint = (commonCount.getNoticeNewCount() > 0) || (commonCount.getVersionNewCount() > 0);
        inflater.inflate(redPoint ? R.menu.help_settings_point : R.menu.help_settings, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuHelp: // 帮助
                HelpActivity.goActivity(mFragment, HelpActivity.INDEX_MORE_HOME);
                return true;
            case R.id.menuSettings: // 设置
                SettingsActivity.goActivity(mFragment);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick({R.id.cvVip, R.id.cvCoin, R.id.cvSign,
            R.id.cvWife, R.id.cvLetter,
            R.id.cvWish, R.id.cvPostcard,
            R.id.cvBaby})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.cvVip: // 会员
                VipActivity.goActivity(mFragment);
                break;
            case R.id.cvCoin: // 金币
                CoinActivity.goActivity(mFragment);
                break;
            case R.id.cvSign: // 签到
                SignActivity.goActivity(mFragment);
                break;
            case R.id.cvWife: // 照片墙
                if (wifePeriod == null || wifePeriod.getId() <= 0) {
                    MatchWifeActivity.goActivity(mFragment);
                } else {
                    MatchWifeListActivity.goActivity(mFragment, wifePeriod.getId());
                }
                break;
            case R.id.cvLetter: // 情话集
                if (letterPeriod == null || letterPeriod.getId() <= 0) {
                    MatchLetterActivity.goActivity(mFragment);
                } else {
                    MatchLetterListActivity.goActivity(mFragment, letterPeriod.getId());
                }
                break;
            case R.id.cvWish: // 许愿树
                break;
            case R.id.cvPostcard: // 明信片
                break;
            case R.id.cvBaby: // 抓娃娃
                CatchBabyActivity.goActivity(mActivity);
                break;
        }
    }

    private void refreshData() {
        if (!srl.isRefreshing()) {
            srl.setRefreshing(true);
        }
        Call<Result> api = new RetrofitHelper().call(API.class).moreHomeGet();
        RetrofitHelper.enqueue(api, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                srl.setRefreshing(false);
                wifePeriod = data.getWifePeriod();
                letterPeriod = data.getLetterPeriod();
                // view
                initBroadcast(data.getBroadcastList());
                initPay(data.getVip(), data.getCoin(), data.getSign());
                initMatch();
                initFeature();
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                srl.setRefreshing(false);
            }
        });
        pushApi(api);
    }

    private void initBroadcast(List<Broadcast> broadcastList) {
        if (mActivity == null || !mFragment.isAdded()) return; // 防止已经脱离后加载
        if (broadcastList == null || broadcastList.size() <= 0) {
            tvBroadcast.setVisibility(View.VISIBLE);
            bb.setVisibility(View.GONE);
        } else {
            tvBroadcast.setVisibility(View.GONE);
            bb.setVisibility(View.VISIBLE);
            bb.setDataList(broadcastList);
        }
    }

    private void initPay(Vip vip, Coin coin, Sign sign) {
        if (mActivity == null || !mFragment.isAdded()) return; // 防止已经脱离后加载
        // vip
        boolean isVip = (vip != null) && TimeHelper.getJavaTimeByGo(vip.getExpireAt()) >= DateUtils.getCurrentLong();
        tvVip.setText(mActivity.getString(isVip ? R.string.vip_yes : R.string.vip_no));
        // coin
        String coinShow = coin == null ? String.valueOf(0) : ShowHelper.getShowCount2Thousand(coin.getCount());
        tvCoin.setText(coinShow);
        // sign
        tvSign.setText(mActivity.getString(sign == null ? R.string.no_sign : R.string.yes_sign));
    }

    private void initMatch() {
        if (mActivity == null || !mFragment.isAdded()) return; // 防止已经脱离后加载
        if (wifePeriod == null || wifePeriod.getId() <= 0) {
            tvWife.setText(R.string.now_no_activity);
        } else {
            String show = String.format(Locale.getDefault(), mActivity.getString(R.string.the_holder_period), wifePeriod.getPeriod());
            tvWife.setText(show);
        }
        if (letterPeriod == null || letterPeriod.getId() <= 0) {
            tvLetter.setText(R.string.now_no_activity);
        } else {
            String show = String.format(Locale.getDefault(), mActivity.getString(R.string.the_holder_period), letterPeriod.getPeriod());
            tvLetter.setText(show);
        }
    }

    private void initFeature() {
        if (mActivity == null || !mFragment.isAdded()) return; // 防止已经脱离后加载
        tvWish.setText("暂无活动");
        tvPostcard.setText("暂无活动");
    }

}
