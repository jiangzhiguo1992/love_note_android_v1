package com.jiangzg.mianmian.activity.book;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.base.view.ToastUtils;
import com.jiangzg.mianmian.R;
import com.jiangzg.mianmian.activity.settings.HelpActivity;
import com.jiangzg.mianmian.adapter.AlbumAdapter;
import com.jiangzg.mianmian.adapter.DiaryAdapter;
import com.jiangzg.mianmian.adapter.FoodAdapter;
import com.jiangzg.mianmian.adapter.TravelPlaceAdapter;
import com.jiangzg.mianmian.base.BaseActivity;
import com.jiangzg.mianmian.domain.Album;
import com.jiangzg.mianmian.domain.Couple;
import com.jiangzg.mianmian.domain.Diary;
import com.jiangzg.mianmian.domain.Food;
import com.jiangzg.mianmian.domain.Help;
import com.jiangzg.mianmian.domain.Result;
import com.jiangzg.mianmian.domain.RxEvent;
import com.jiangzg.mianmian.domain.Travel;
import com.jiangzg.mianmian.domain.TravelPlace;
import com.jiangzg.mianmian.domain.Video;
import com.jiangzg.mianmian.helper.API;
import com.jiangzg.mianmian.helper.ConsHelper;
import com.jiangzg.mianmian.helper.DialogHelper;
import com.jiangzg.mianmian.helper.ListHelper;
import com.jiangzg.mianmian.helper.RecyclerHelper;
import com.jiangzg.mianmian.helper.RetrofitHelper;
import com.jiangzg.mianmian.helper.RxBus;
import com.jiangzg.mianmian.helper.SPHelper;
import com.jiangzg.mianmian.helper.TimeHelper;
import com.jiangzg.mianmian.helper.ViewHelper;
import com.jiangzg.mianmian.view.GSwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import retrofit2.Call;
import rx.Observable;
import rx.functions.Action1;

public class TravelDetailActivity extends BaseActivity<TravelDetailActivity> {

    private static final int FROM_NONE = 0;
    private static final int FROM_ID = 1;
    private static final int FROM_ALL = 2;

    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.srl)
    GSwipeRefreshLayout srl;
    @BindView(R.id.tvTitle)
    TextView tvTitle;
    @BindView(R.id.tvHappenAt)
    TextView tvHappenAt;
    @BindView(R.id.tvCreator)
    TextView tvCreator;
    @BindView(R.id.llPlace)
    LinearLayout llPlace;
    @BindView(R.id.rvPlace)
    RecyclerView rvPlace;
    @BindView(R.id.llAlbum)
    LinearLayout llAlbum;
    @BindView(R.id.rvAlbum)
    RecyclerView rvAlbum;
    @BindView(R.id.llVideo)
    LinearLayout llVideo;
    @BindView(R.id.rvVideo)
    RecyclerView rvVideo;
    @BindView(R.id.llFood)
    LinearLayout llFood;
    @BindView(R.id.rvFood)
    RecyclerView rvFood;
    @BindView(R.id.llDiary)
    LinearLayout llDiary;
    @BindView(R.id.rvDiary)
    RecyclerView rvDiary;

    private Travel travel;
    private Call<Result> callGet;
    private Call<Result> callDel;
    private Observable<Travel> obDetailRefresh;
    private RecyclerHelper recyclerPlace;
    private RecyclerHelper recyclerAlbum;
    private RecyclerHelper recyclerVideo;
    private RecyclerHelper recyclerFood;
    private RecyclerHelper recyclerDiary;

    public static void goActivity(Activity from, Travel travel) {
        Intent intent = new Intent(from, TravelDetailActivity.class);
        intent.putExtra("from", FROM_ALL);
        intent.putExtra("travel", travel);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    public static void goActivity(Activity from, long tid) {
        Intent intent = new Intent(from, TravelDetailActivity.class);
        intent.putExtra("from", FROM_ID);
        intent.putExtra("tid", tid);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_travel_detail;
    }

    @Override
    protected void initView(Bundle state) {
        ViewHelper.initTopBar(mActivity, tb, getString(R.string.travel), true);
        srl.setEnabled(false);
        // init
        Intent intent = getIntent();
        int from = intent.getIntExtra("from", FROM_NONE);
        if (from == FROM_ALL) {
            travel = getIntent().getParcelableExtra("travel");
            refreshView();
            // 没有详情页的，可以不加
            if (travel != null) {
                refreshTravel(travel.getId());
            }
        } else if (from == FROM_ID) {
            long tid = intent.getLongExtra("tid", 0);
            refreshTravel(tid);
        }
    }

    @Override
    protected void initData(Bundle state) {
        // event
        obDetailRefresh = RxBus.register(ConsHelper.EVENT_TRAVEL_DETAIL_REFRESH, new Action1<Travel>() {
            @Override
            public void call(Travel travel) {
                if (TravelDetailActivity.this.travel == null) return;
                refreshTravel(TravelDetailActivity.this.travel.getId());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.help_del_edit, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RetrofitHelper.cancel(callGet);
        RetrofitHelper.cancel(callDel);
        RxBus.unregister(ConsHelper.EVENT_TRAVEL_DETAIL_REFRESH, obDetailRefresh);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuHelp: // 帮助
                HelpActivity.goActivity(mActivity, Help.INDEX_TRAVEL_DETAIL);
                return true;
            case R.id.menuEdit: // 编辑
                if (travel == null) return true;
                TravelEditActivity.goActivity(mActivity, travel);
                return true;
            case R.id.menuDel: // 删除
                showDelDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void refreshTravel(long tid) {
        if (!srl.isRefreshing()) {
            srl.setRefreshing(true);
        }
        callGet = new RetrofitHelper().call(API.class).travelGet(tid);
        RetrofitHelper.enqueue(callGet, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                srl.setRefreshing(false);
                travel = data.getTravel();
                refreshView();
            }

            @Override
            public void onFailure(String errMsg) {
                srl.setRefreshing(false);
            }
        });
    }

    private void refreshView() {
        if (travel == null) return;
        // headData
        Couple couple = SPHelper.getCouple();
        String title = travel.getTitle();
        String happen = TimeHelper.getTimeShowCn_MD_YMD_ByGo(travel.getHappenAt());
        String happenShow = String.format(Locale.getDefault(), mActivity.getString(R.string.time_colon_space_holder), happen);
        String name = Couple.getName(couple, travel.getUserId());
        String creator = String.format(Locale.getDefault(), mActivity.getString(R.string.creator_colon_space_holder), name);
        // headView
        tvTitle.setText(title);
        tvHappenAt.setText(happenShow);
        tvCreator.setText(creator);
        // place
        List<TravelPlace> placeList = travel.getTravelPlaceList();
        if (placeList != null && placeList.size() > 0) {
            llPlace.setVisibility(View.VISIBLE);
            rvPlace.setVisibility(View.VISIBLE);
            if (recyclerPlace == null) {
                recyclerPlace = new RecyclerHelper(mActivity)
                        .initRecycler(rvPlace)
                        .initLayoutManager(new LinearLayoutManager(mActivity) {
                            @Override
                            public boolean canScrollVertically() {
                                return false;
                            }
                        })
                        .initAdapter(new TravelPlaceAdapter(mActivity))
                        .setAdapter()
                        .listenerClick(new OnItemClickListener() {
                            @Override
                            public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
                                TravelPlaceAdapter placeAdapter = (TravelPlaceAdapter) adapter;
                                placeAdapter.goMapShow(position);
                            }
                        });
            }
            recyclerPlace.dataNew(placeList, 0);
        } else {
            llPlace.setVisibility(View.GONE);
            rvPlace.setVisibility(View.GONE);
        }
        // album
        List<Album> albumList = ListHelper.getAlbumListByTravel(travel.getTravelAlbumList());
        if (albumList != null && albumList.size() > 0) {
            llAlbum.setVisibility(View.VISIBLE);
            rvAlbum.setVisibility(View.VISIBLE);
            if (recyclerAlbum == null) {
                recyclerAlbum = new RecyclerHelper(mActivity)
                        .initRecycler(rvAlbum)
                        .initLayoutManager(new LinearLayoutManager(mActivity) {
                            @Override
                            public boolean canScrollVertically() {
                                return false;
                            }
                        })
                        .initAdapter(new AlbumAdapter(mActivity))
                        .setAdapter()
                        .listenerClick(new OnItemClickListener() {
                            @Override
                            public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
                                AlbumAdapter albumAdapter = (AlbumAdapter) adapter;
                                albumAdapter.goAlbumDetail(position);
                            }
                        });
            }
            recyclerAlbum.dataNew(albumList, 0);
        } else {
            llAlbum.setVisibility(View.GONE);
            rvAlbum.setVisibility(View.GONE);
        }
        // video
        ArrayList<Video> videoList = ListHelper.getVideoListByTravel(travel.getTravelVideoList());
        if (videoList != null && videoList.size() > 0) {
            llVideo.setVisibility(View.VISIBLE);
            rvVideo.setVisibility(View.VISIBLE);
            if (recyclerVideo == null) {
                // TODO
            }
            //recyclerVideo.dataNew(videoList,0);
        } else {
            llVideo.setVisibility(View.GONE);
            rvVideo.setVisibility(View.GONE);
        }
        // food
        ArrayList<Food> foodList = ListHelper.getFoodListByTravel(travel.getTravelFoodList());
        if (foodList != null && foodList.size() > 0) {
            llFood.setVisibility(View.VISIBLE);
            rvFood.setVisibility(View.VISIBLE);
            if (recyclerFood == null) {
                recyclerFood = new RecyclerHelper(mActivity)
                        .initRecycler(rvFood)
                        .initLayoutManager(new LinearLayoutManager(mActivity) {
                            @Override
                            public boolean canScrollVertically() {
                                return false;
                            }
                        })
                        .initAdapter(new FoodAdapter(mActivity))
                        .setAdapter()
                        .listenerClick(new OnItemChildClickListener() {
                            @Override
                            public void onSimpleItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                                FoodAdapter foodAdapter = (FoodAdapter) adapter;
                                switch (view.getId()) {
                                    case R.id.tvAddress:
                                        foodAdapter.goMapShow(position);
                                        break;
                                }
                            }
                        });
            }
            recyclerFood.dataNew(foodList, 0);
        } else {
            llFood.setVisibility(View.GONE);
            rvFood.setVisibility(View.GONE);
        }
        // diary
        ArrayList<Diary> diaryList = ListHelper.getDiaryListByTravel(travel.getTravelDiaryList());
        if (diaryList != null && diaryList.size() > 0) {
            llDiary.setVisibility(View.VISIBLE);
            rvDiary.setVisibility(View.VISIBLE);
            if (recyclerDiary == null) {
                recyclerDiary = new RecyclerHelper(mActivity)
                        .initRecycler(rvDiary)
                        .initLayoutManager(new LinearLayoutManager(mActivity) {
                            @Override
                            public boolean canScrollVertically() {
                                return false;
                            }
                        })
                        .initAdapter(new DiaryAdapter(mActivity))
                        .setAdapter()
                        .listenerClick(new OnItemClickListener() {
                            @Override
                            public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
                                DiaryAdapter diaryAdapter = (DiaryAdapter) adapter;
                                diaryAdapter.goDiaryDetail(position);
                            }
                        });
            }
            recyclerDiary.dataNew(diaryList, 0);
        } else {
            llDiary.setVisibility(View.GONE);
            rvDiary.setVisibility(View.GONE);
        }
    }

    private void showDelDialog() {
        if (travel == null || !travel.isMine()) {
            ToastUtils.show(mActivity.getString(R.string.can_operation_self_create_travel));
            return;
        }
        MaterialDialog dialog = DialogHelper.getBuild(mActivity)
                .content(R.string.confirm_delete_this_travel)
                .cancelable(true)
                .canceledOnTouchOutside(false)
                .positiveText(R.string.confirm_no_wrong)
                .negativeText(R.string.i_think_again)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        delTravel();
                    }
                })
                .build();
        DialogHelper.showWithAnim(dialog);
    }

    private void delTravel() {
        if (travel == null) return;
        MaterialDialog loading = getLoading(getString(R.string.are_deleting), true);
        callDel = new RetrofitHelper().call(API.class).travelDel(travel.getId());
        RetrofitHelper.enqueue(callDel, loading, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                // ListItemDelete
                RxEvent<Travel> event = new RxEvent<>(ConsHelper.EVENT_TRAVEL_LIST_ITEM_DELETE, travel);
                RxBus.post(event);
                mActivity.finish();
            }

            @Override
            public void onFailure(String errMsg) {
            }
        });
    }

}
