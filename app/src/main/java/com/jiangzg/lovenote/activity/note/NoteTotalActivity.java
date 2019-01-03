package com.jiangzg.lovenote.activity.note;

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
import com.jiangzg.lovenote.activity.base.BaseActivity;
import com.jiangzg.lovenote.activity.more.VipActivity;
import com.jiangzg.lovenote.helper.RetrofitHelper;
import com.jiangzg.lovenote.helper.SPHelper;
import com.jiangzg.lovenote.helper.ViewHelper;
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
        NoteTotal noteTotal = data.getNoteTotal();
        if (noteTotal == null) return;
        tvSouvenir.setText(String.valueOf(noteTotal.getTotalSouvenir()));
        tvWord.setText(String.valueOf(noteTotal.getTotalWord()));
        tvDiary.setText(String.valueOf(noteTotal.getTotalDiary()));
        tvPicture.setText(String.valueOf(noteTotal.getTotalPicture()));
        tvAudio.setText(String.valueOf(noteTotal.getTotalAudio()));
        tvVideo.setText(String.valueOf(noteTotal.getTotalVideo()));
        tvFood.setText(String.valueOf(noteTotal.getTotalFood()));
        tvTravel.setText(String.valueOf(noteTotal.getTotalTravel()));
        tvGift.setText(String.valueOf(noteTotal.getTotalGift()));
        tvPromise.setText(String.valueOf(noteTotal.getTotalPromise()));
        tvAngry.setText(String.valueOf(noteTotal.getTotalAngry()));
        tvDream.setText(String.valueOf(noteTotal.getTotalDream()));
        tvAward.setText(String.valueOf(noteTotal.getTotalAward()));
        tvMovie.setText(String.valueOf(noteTotal.getTotalMovie()));
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
