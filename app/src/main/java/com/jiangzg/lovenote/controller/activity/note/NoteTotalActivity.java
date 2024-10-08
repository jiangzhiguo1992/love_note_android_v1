package com.jiangzg.lovenote.controller.activity.note;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.controller.activity.base.BaseActivity;
import com.jiangzg.lovenote.controller.activity.more.VipActivity;
import com.jiangzg.lovenote.helper.common.SPHelper;
import com.jiangzg.lovenote.helper.system.RetrofitHelper;
import com.jiangzg.lovenote.helper.view.ViewHelper;
import com.jiangzg.lovenote.model.api.API;
import com.jiangzg.lovenote.model.api.Result;
import com.jiangzg.lovenote.model.entity.NoteTotal;
import com.jiangzg.lovenote.view.GSwipeRefreshLayout;

import butterknife.BindView;
import butterknife.OnClick;
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
    @BindView(R.id.tvMovie)
    TextView tvMovie;

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

    public static void goActivity(Context from) {
        if (!SPHelper.getVipLimit().isNoteTotalEnable()) {
            VipActivity.goActivity(from);
            return;
        }
        Intent intent = new Intent(from, NoteTotalActivity.class);
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
        srl.setOnRefreshListener(this::refreshData);
    }

    @Override
    protected void initData(Intent intent, Bundle state) {
        refreshData();
    }

    @Override
    protected void onFinish(Bundle state) {
    }

    private void refreshData() {
        if (!srl.isRefreshing()) {
            srl.setRefreshing(true);
        }
        Call<Result> api = new RetrofitHelper().call(API.class).noteTrendsTotalGet();
        RetrofitHelper.enqueue(api, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                srl.setRefreshing(false);
                refreshView(data.getShow(), data.getNoteTotal());
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                srl.setRefreshing(false);
            }
        });
        pushApi(api);
    }

    private void refreshView(String show, NoteTotal total) {
        if (!StringUtils.isEmpty(show)) {
            tvShow.setVisibility(View.VISIBLE);
            llTotal.setVisibility(View.GONE);
            tvShow.setText(show);
            return;
        }
        tvShow.setVisibility(View.GONE);
        llTotal.setVisibility(View.VISIBLE);
        if (total == null) {
            total = new NoteTotal();
        }
        tvSouvenir.setText(String.valueOf(total.getTotalSouvenir()));
        tvTravel.setText(String.valueOf(total.getTotalTravel()));
        tvWord.setText(String.valueOf(total.getTotalWord()));
        tvAward.setText(String.valueOf(total.getTotalAward()));
        tvDiary.setText(String.valueOf(total.getTotalDiary()));
        tvDream.setText(String.valueOf(total.getTotalDream()));
        tvMovie.setText(String.valueOf(total.getTotalMovie()));
        tvFood.setText(String.valueOf(total.getTotalFood()));
        tvPromise.setText(String.valueOf(total.getTotalPromise()));
        tvGift.setText(String.valueOf(total.getTotalGift()));
        tvAngry.setText(String.valueOf(total.getTotalAngry()));
        tvAudio.setText(String.valueOf(total.getTotalAudio()));
        tvVideo.setText(String.valueOf(total.getTotalVideo()));
        tvPicture.setText(String.valueOf(total.getTotalPicture()));
    }

    @OnClick({R.id.cvSouvenir, R.id.cvWord, R.id.cvDiary, R.id.cvAward, R.id.cvDream, R.id.cvMovie, R.id.cvFood,
            R.id.cvTravel, R.id.cvAngry, R.id.cvGift, R.id.cvPromise, R.id.cvAudio, R.id.cvVideo, R.id.cvPhoto})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.cvSouvenir:
                SouvenirListActivity.goActivity(mActivity);
                break;
            case R.id.cvWord:
                WordListActivity.goActivity(mActivity);
                break;
            case R.id.cvDiary:
                DiaryListActivity.goActivity(mActivity);
                break;
            case R.id.cvAward:
                AwardListActivity.goActivity(mActivity);
                break;
            case R.id.cvDream:
                DreamListActivity.goActivity(mActivity);
                break;
            case R.id.cvMovie:
                MovieListActivity.goActivity(mActivity);
                break;
            case R.id.cvFood:
                FoodListActivity.goActivity(mActivity);
                break;
            case R.id.cvTravel:
                TravelListActivity.goActivity(mActivity);
                break;
            case R.id.cvAngry:
                AngryListActivity.goActivity(mActivity);
                break;
            case R.id.cvGift:
                GiftListActivity.goActivity(mActivity);
                break;
            case R.id.cvPromise:
                PromiseListActivity.goActivity(mActivity);
                break;
            case R.id.cvAudio:
                AudioListActivity.goActivity(mActivity);
                break;
            case R.id.cvVideo:
                VideoListActivity.goActivity(mActivity);
                break;
            case R.id.cvPhoto:
                AlbumListActivity.goActivity(mActivity);
                break;
        }
    }
}
