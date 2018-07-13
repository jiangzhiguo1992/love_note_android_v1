package com.jiangzg.mianmian.activity.book;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.mianmian.R;
import com.jiangzg.mianmian.activity.settings.HelpActivity;
import com.jiangzg.mianmian.base.BaseActivity;
import com.jiangzg.mianmian.domain.Help;
import com.jiangzg.mianmian.domain.Result;
import com.jiangzg.mianmian.helper.API;
import com.jiangzg.mianmian.helper.RetrofitHelper;
import com.jiangzg.mianmian.helper.ViewHelper;
import com.jiangzg.mianmian.view.GSwipeRefreshLayout;

import butterknife.BindView;
import retrofit2.Call;

public class TrendsTotalActivity extends BaseActivity<TrendsTotalActivity> {

    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.srl)
    GSwipeRefreshLayout srl;
    @BindView(R.id.tvShow)
    TextView tvShow;
    @BindView(R.id.llTotal)
    LinearLayout llTotal;
    @BindView(R.id.tvSouvenir)
    TextView tvSouvenir;
    @BindView(R.id.tvWord)
    TextView tvWord;
    @BindView(R.id.tvDiary)
    TextView tvDiary;
    @BindView(R.id.tvPicture)
    TextView tvPicture;
    @BindView(R.id.tvAudio)
    TextView tvAudio;
    @BindView(R.id.tvVideo)
    TextView tvVideo;
    @BindView(R.id.tvFood)
    TextView tvFood;
    @BindView(R.id.tvTravel)
    TextView tvTravel;
    @BindView(R.id.tvGift)
    TextView tvGift;
    @BindView(R.id.tvPromise)
    TextView tvPromise;
    @BindView(R.id.tvAngry)
    TextView tvAngry;
    @BindView(R.id.tvDream)
    TextView tvDream;
    @BindView(R.id.tvAward)
    TextView tvAward;

    private Call<Result> call;

    public static void goActivity(Activity from) {
        Intent intent = new Intent(from, TrendsTotalActivity.class);
        // intent.putExtra();
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_trends_total;
    }

    @Override
    protected void initView(Intent intent, Bundle state) {
        ViewHelper.initTopBar(mActivity, tb, getString(R.string.statistics), true);
        srl.setEnabled(false);
        // srl
        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData();
            }
        });
    }

    @Override
    protected void initData(Intent intent, Bundle state) {
        refreshData();
    }

    @Override
    protected void onFinish(Bundle state) {
        RetrofitHelper.cancel(call);
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
                HelpActivity.goActivity(mActivity, Help.INDEX_BOOK_TRENDS_TOTAL);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void refreshData() {
        if (!srl.isRefreshing()) {
            srl.setRefreshing(true);
        }
        call = new RetrofitHelper().call(API.class).trendsTotalGet();
        RetrofitHelper.enqueue(call, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                srl.setRefreshing(false);
                refreshView(data);
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                srl.setRefreshing(false);
            }
        });
    }

    private void refreshView(Result.Data data) {
        tvShow.setVisibility(View.GONE);
        llTotal.setVisibility(View.GONE);
        if (data == null) return;
        if (!StringUtils.isEmpty(data.getShow())) {
            tvShow.setVisibility(View.VISIBLE);
            tvShow.setText(data.getShow());
            return;
        }
        llTotal.setVisibility(View.VISIBLE);
        tvSouvenir.setText(String.valueOf(data.getTotalSouvenir()));
        tvWord.setText(String.valueOf(data.getTotalWord()));
        tvDiary.setText(String.valueOf(data.getTotalDiary()));
        tvPicture.setText(String.valueOf(data.getTotalPicture()));
        tvAudio.setText(String.valueOf(data.getTotalAudio()));
        tvVideo.setText(String.valueOf(data.getTotalVideo()));
        tvFood.setText(String.valueOf(data.getTotalFood()));
        tvTravel.setText(String.valueOf(data.getTotalTravel()));
        tvGift.setText(String.valueOf(data.getTotalGift()));
        tvPromise.setText(String.valueOf(data.getTotalPromise()));
        tvAngry.setText(String.valueOf(data.getTotalAngry()));
        tvDream.setText(String.valueOf(data.getTotalDream()));
        tvAward.setText(String.valueOf(data.getTotalAward()));
    }

}
