package com.jiangzg.lovenote.activity.note;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.activity.more.VipActivity;
import com.jiangzg.lovenote.base.BaseActivity;
import com.jiangzg.lovenote.domain.Result;
import com.jiangzg.lovenote.helper.API;
import com.jiangzg.lovenote.helper.RetrofitHelper;
import com.jiangzg.lovenote.helper.SPHelper;
import com.jiangzg.lovenote.helper.ViewHelper;
import com.jiangzg.lovenote.view.GSwipeRefreshLayout;

import butterknife.BindView;
import retrofit2.Call;

public class NoteTotalActivity extends BaseActivity<NoteTotalActivity> {

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

    public static void goActivity(Fragment from) {
        if (!SPHelper.getVipLimit().isNoteTotalEnable()) {
            VipActivity.goActivity(from);
            return;
        }
        Intent intent = new Intent(from.getActivity(), NoteTotalActivity.class);
        // intent.putExtra();
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_note_total;
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

    private void refreshData() {
        if (!srl.isRefreshing()) {
            srl.setRefreshing(true);
        }
        call = new RetrofitHelper().call(API.class).noteTrendsTotalGet();
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
