package com.jiangzg.mianmian.fragment;

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

import com.jiangzg.mianmian.R;
import com.jiangzg.mianmian.activity.common.SettingsActivity;
import com.jiangzg.mianmian.activity.more.CoinActivity;
import com.jiangzg.mianmian.activity.more.SignActivity;
import com.jiangzg.mianmian.activity.more.VipActivity;
import com.jiangzg.mianmian.activity.settings.HelpActivity;
import com.jiangzg.mianmian.base.BaseFragment;
import com.jiangzg.mianmian.base.BasePagerFragment;
import com.jiangzg.mianmian.base.MyApp;
import com.jiangzg.mianmian.domain.Help;
import com.jiangzg.mianmian.domain.Version;
import com.jiangzg.mianmian.helper.SPHelper;
import com.jiangzg.mianmian.helper.ViewHelper;
import com.jiangzg.mianmian.view.GSwipeRefreshLayout;

import butterknife.BindView;
import butterknife.OnClick;

public class MoreFragment extends BasePagerFragment<MoreFragment> {

    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.srl)
    GSwipeRefreshLayout srl;
    @BindView(R.id.tvVip)
    TextView tvVip;
    @BindView(R.id.cvVip)
    CardView cvVip;
    @BindView(R.id.tvCoin)
    TextView tvCoin;
    @BindView(R.id.cvCoin)
    CardView cvCoin;
    @BindView(R.id.tvSign)
    TextView tvSign;
    @BindView(R.id.cvSign)
    CardView cvSign;
    @BindView(R.id.tvWife)
    TextView tvWife;
    @BindView(R.id.cvWife)
    CardView cvWife;
    @BindView(R.id.tvLetter)
    TextView tvLetter;
    @BindView(R.id.cvLetter)
    CardView cvLetter;
    @BindView(R.id.tvDiscuss)
    TextView tvDiscuss;
    @BindView(R.id.cvDiscuss)
    CardView cvDiscuss;
    @BindView(R.id.tvWish)
    TextView tvWish;
    @BindView(R.id.cvWish)
    CardView cvWish;
    @BindView(R.id.tvPostcard)
    TextView tvPostcard;
    @BindView(R.id.cvPostcard)
    CardView cvPostcard;

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
    }

    protected void loadData() {
        // data
        refreshData();
    }

    @Override
    protected void onFinish(Bundle state) {

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        // menu
        if (isVisibleToUser) tb.invalidate();
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
            R.id.cvWish, R.id.cvPostcard})
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
            case R.id.cvPostcard: // 明信卡
                // TODO
                break;
        }
    }

    private void refreshData() {
        if (!srl.isRefreshing()) {
            srl.setRefreshing(true);
        }
        // TODO
        MyApp.get().getHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                srl.setRefreshing(false);
            }
        }, 1000);
        //call = new RetrofitHelper().call(API.class).noteHomeGet(near);
        //RetrofitHelper.enqueue(call, null, new RetrofitHelper.CallBack() {
        //    @Override
        //    public void onResponse(int code, String message, Result.Data data) {
        //        srl.setRefreshing(false);
        //        lockBack = true;
        //        canLock = data.isCanLock();
        //        isLock = data.isLock();
        //        souvenirLatest = data.getSouvenirLatest();
        //        refreshNoteView();
        //    }
        //
        //    @Override
        //    public void onFailure(int code, String message, Result.Data data) {
        //        srl.setRefreshing(false);
        //    }
        //});
    }

}
