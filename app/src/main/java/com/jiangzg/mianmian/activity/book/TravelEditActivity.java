package com.jiangzg.mianmian.activity.book;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.chad.library.adapter.base.listener.OnItemChildLongClickListener;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.listener.OnItemLongClickListener;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.base.time.DateUtils;
import com.jiangzg.base.view.ToastUtils;
import com.jiangzg.mianmian.R;
import com.jiangzg.mianmian.activity.settings.HelpActivity;
import com.jiangzg.mianmian.adapter.AlbumAdapter;
import com.jiangzg.mianmian.adapter.DiaryAdapter;
import com.jiangzg.mianmian.adapter.FoodAdapter;
import com.jiangzg.mianmian.adapter.TravelPlaceAdapter;
import com.jiangzg.mianmian.adapter.VideoAdapter;
import com.jiangzg.mianmian.base.BaseActivity;
import com.jiangzg.mianmian.domain.Album;
import com.jiangzg.mianmian.domain.Diary;
import com.jiangzg.mianmian.domain.Food;
import com.jiangzg.mianmian.domain.Help;
import com.jiangzg.mianmian.domain.Result;
import com.jiangzg.mianmian.domain.RxEvent;
import com.jiangzg.mianmian.domain.Travel;
import com.jiangzg.mianmian.domain.TravelAlbum;
import com.jiangzg.mianmian.domain.TravelDiary;
import com.jiangzg.mianmian.domain.TravelFood;
import com.jiangzg.mianmian.domain.TravelPlace;
import com.jiangzg.mianmian.domain.TravelVideo;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;
import rx.Observable;
import rx.functions.Action1;

public class TravelEditActivity extends BaseActivity<TravelEditActivity> {

    private static final int TYPE_ADD = 0;
    private static final int TYPE_UPDATE = 1;

    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.etTitle)
    EditText etTitle;
    @BindView(R.id.cvHappenAt)
    CardView cvHappenAt;
    @BindView(R.id.tvHappenAt)
    TextView tvHappenAt;
    @BindView(R.id.rvPlace)
    RecyclerView rvPlace;
    @BindView(R.id.cvPlaceAdd)
    CardView cvPlaceAdd;
    @BindView(R.id.rvAlbum)
    RecyclerView rvAlbum;
    @BindView(R.id.cvAlbumAdd)
    CardView cvAlbumAdd;
    @BindView(R.id.rvVideo)
    RecyclerView rvVideo;
    @BindView(R.id.cvVideoAdd)
    CardView cvVideoAdd;
    @BindView(R.id.rvFood)
    RecyclerView rvFood;
    @BindView(R.id.cvFoodAdd)
    CardView cvFoodAdd;
    @BindView(R.id.rvDiary)
    RecyclerView rvDiary;
    @BindView(R.id.cvDiaryAdd)
    CardView cvDiaryAdd;

    private Travel travel;
    private RecyclerHelper recyclerPlace;
    private RecyclerHelper recyclerAlbum;
    private RecyclerHelper recyclerVideo;
    private RecyclerHelper recyclerFood;
    private RecyclerHelper recyclerDiary;
    private Observable<TravelPlace> obAddPlace;
    private Observable<Album> obSelectAlbum;
    private Observable<Video> obSelectVideo;
    private Observable<Food> obSelectFood;
    private Observable<Diary> obSelectDiary;
    private Call<Result> callAdd;
    private Call<Result> callUpdate;

    public static void goActivity(Activity from) {
        Intent intent = new Intent(from, TravelEditActivity.class);
        intent.putExtra("type", TYPE_ADD);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    public static void goActivity(Activity from, Travel travel) {
        if (travel == null) {
            goActivity(from);
            return;
        } else if (!travel.isMine()) {
            ToastUtils.show(from.getString(R.string.can_operation_self_create_travel));
            return;
        }
        Intent intent = new Intent(from, TravelEditActivity.class);
        intent.putExtra("type", TYPE_UPDATE);
        intent.putExtra("travel", travel);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_travel_edit;
    }

    @Override
    protected void initView(Intent intent, Bundle state) {
        ViewHelper.initTopBar(mActivity, tb, getString(R.string.travel), true);
        // init
        if (isTypeUpdate()) {
            travel = intent.getParcelableExtra("travel");
        }
        if (travel == null) {
            travel = new Travel();
        }
        if (travel.getHappenAt() == 0) {
            travel.setHappenAt(TimeHelper.getGoTimeByJava(DateUtils.getCurrentLong()));
        }
        // etTitle
        String format = getString(R.string.please_input_title_no_over_holder_text);
        String hint = String.format(Locale.getDefault(), format, SPHelper.getLimit().getTravelTitleLength());
        etTitle.setHint(hint);
        etTitle.setText(travel.getTitle());
        // date
        refreshDateView();
        // place
        recyclerPlace = new RecyclerHelper(rvPlace)
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
                })
                .listenerClick(new OnItemLongClickListener() {
                    @Override
                    public void onSimpleItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                        TravelPlaceAdapter placeAdapter = (TravelPlaceAdapter) adapter;
                        placeAdapter.showDeleteDialogNoApi(position);
                    }
                });
        refreshPlaceView();
        // album
        recyclerAlbum = new RecyclerHelper(rvAlbum)
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
                })
                .listenerClick(new OnItemLongClickListener() {
                    @Override
                    public void onSimpleItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                        AlbumAdapter albumAdapter = (AlbumAdapter) adapter;
                        albumAdapter.showDeleteDialogNoApi(position);
                    }
                });
        refreshAlbumView();
        // video
        recyclerVideo = new RecyclerHelper(rvVideo)
                .initLayoutManager(new LinearLayoutManager(mActivity) {
                    @Override
                    public boolean canScrollVertically() {
                        return false;
                    }
                })
                .initAdapter(new VideoAdapter(mActivity))
                .setAdapter()
                .listenerClick(new OnItemChildClickListener() {
                    @Override
                    public void onSimpleItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                        VideoAdapter videoAdapter = (VideoAdapter) adapter;
                        switch (view.getId()) {
                            case R.id.cvVideo: // 播放
                                videoAdapter.playVideo(position);
                                break;
                            case R.id.tvAddress: // 地图
                                videoAdapter.goMapShow(position);
                                break;
                        }
                    }
                })
                .listenerClick(new OnItemChildLongClickListener() {
                    @Override
                    public void onSimpleItemChildLongClick(BaseQuickAdapter adapter, View view, int position) {
                        VideoAdapter videoAdapter = (VideoAdapter) adapter;
                        switch (view.getId()) {
                            case R.id.cvVideo: // 删除
                                videoAdapter.showDeleteDialogNoApi(position);
                                break;
                        }
                    }
                });
        refreshVideoView();
        // food
        recyclerFood = new RecyclerHelper(rvFood)
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
                })
                .listenerClick(new OnItemLongClickListener() {
                    @Override
                    public void onSimpleItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                        FoodAdapter foodAdapter = (FoodAdapter) adapter;
                        foodAdapter.showDeleteDialogNoApi(position);
                    }
                });
        refreshFoodView();
        // diary
        recyclerDiary = new RecyclerHelper(rvDiary)
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
                })
                .listenerClick(new OnItemLongClickListener() {
                    @Override
                    public void onSimpleItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                        DiaryAdapter diaryAdapter = (DiaryAdapter) adapter;
                        diaryAdapter.showDeleteDialogNoApi(position);
                    }
                });
        refreshDiaryView();
    }

    @Override
    protected void initData(Intent intent, Bundle state) {
        // event
        obAddPlace = RxBus.register(ConsHelper.EVENT_TRAVEL_EDIT_ADD_PLACE, new Action1<TravelPlace>() {
            @Override
            public void call(TravelPlace travelPlace) {
                if (recyclerPlace == null) return;
                List<TravelPlace> placeList = new ArrayList<>();
                placeList.add(travelPlace);
                recyclerPlace.dataAdd(placeList);
            }
        });
        obSelectAlbum = RxBus.register(ConsHelper.EVENT_ALBUM_SELECT, new Action1<Album>() {
            @Override
            public void call(Album album) {
                if (recyclerAlbum == null) return;
                List<Album> albumList = new ArrayList<>();
                albumList.add(album);
                recyclerAlbum.dataAdd(albumList);
            }
        });
        obSelectVideo = RxBus.register(ConsHelper.EVENT_VIDEO_SELECT, new Action1<Video>() {
            @Override
            public void call(Video video) {
                if (recyclerVideo == null) return;
                List<Video> videoList = new ArrayList<>();
                videoList.add(video);
                recyclerVideo.dataAdd(videoList);
            }
        });
        obSelectFood = RxBus.register(ConsHelper.EVENT_FOOD_SELECT, new Action1<Food>() {
            @Override
            public void call(Food food) {
                if (recyclerFood == null) return;
                List<Food> foodList = new ArrayList<>();
                foodList.add(food);
                recyclerFood.dataAdd(foodList);
            }
        });
        obSelectDiary = RxBus.register(ConsHelper.EVENT_DIARY_SELECT, new Action1<Diary>() {
            @Override
            public void call(Diary diary) {
                if (recyclerDiary == null) return;
                List<Diary> diaryList = new ArrayList<>();
                diaryList.add(diary);
                recyclerDiary.dataAdd(diaryList);
            }
        });
    }

    @Override
    protected void onFinish(Bundle state) {
        RecyclerHelper.release(recyclerPlace);
        RecyclerHelper.release(recyclerAlbum);
        RecyclerHelper.release(recyclerVideo);
        RecyclerHelper.release(recyclerFood);
        RecyclerHelper.release(recyclerDiary);
        RetrofitHelper.cancel(callAdd);
        RetrofitHelper.cancel(callUpdate);
        RxBus.unregister(ConsHelper.EVENT_TRAVEL_EDIT_ADD_PLACE, obAddPlace);
        RxBus.unregister(ConsHelper.EVENT_ALBUM_SELECT, obSelectAlbum);
        RxBus.unregister(ConsHelper.EVENT_VIDEO_SELECT, obSelectVideo);
        RxBus.unregister(ConsHelper.EVENT_FOOD_SELECT, obSelectFood);
        RxBus.unregister(ConsHelper.EVENT_DIARY_SELECT, obSelectDiary);
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
                HelpActivity.goActivity(mActivity, Help.INDEX_TRAVEL_EDIT);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick({R.id.cvPlaceAdd, R.id.cvAlbumAdd, R.id.cvVideoAdd, R.id.cvFoodAdd, R.id.cvDiaryAdd,
            R.id.cvHappenAt, R.id.btnPublish})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.cvPlaceAdd: // 足迹
                TravelPlaceEditActivity.goActivity(mActivity);
                break;
            case R.id.cvAlbumAdd: // 相册
                AlbumListActivity.goActivityBySelectAlbum(mActivity);
                break;
            case R.id.cvVideoAdd: // 视频
                VideoListActivity.goActivityBySelect(mActivity);
                break;
            case R.id.cvFoodAdd: // 美食
                FoodListActivity.goActivityBySelect(mActivity);
                break;
            case R.id.cvDiaryAdd: // 日记
                DiaryListActivity.goActivityBySelect(mActivity);
                break;
            case R.id.cvHappenAt: // 日期
                showDatePicker();
                break;
            case R.id.btnPublish: // 发表
                checkPush();
                break;
        }
    }

    private boolean isTypeUpdate() {
        return getIntent().getIntExtra("type", TYPE_ADD) == TYPE_UPDATE;
    }

    private void refreshPlaceView() {
        if (travel == null || recyclerPlace == null) return;
        List<TravelPlace> placeList = new ArrayList<>();
        placeList.addAll(travel.getTravelPlaceList() == null ? new ArrayList<TravelPlace>() : travel.getTravelPlaceList()); // 不能用引用
        recyclerPlace.dataNew(placeList, 0);
    }

    private void refreshAlbumView() {
        if (travel == null || recyclerAlbum == null) return;
        List<Album> albumList = ListHelper.getAlbumListByTravel(travel.getTravelAlbumList(), true);
        recyclerAlbum.dataNew(albumList, 0);
    }

    private void refreshVideoView() {
        if (travel == null || recyclerVideo == null) return;
        List<Video> videoList = ListHelper.getVideoListByTravel(travel.getTravelVideoList(), true);
        recyclerVideo.dataNew(videoList, 0);
    }

    private void refreshFoodView() {
        if (travel == null || recyclerFood == null) return;
        ArrayList<Food> foodList = ListHelper.getFoodListByTravel(travel.getTravelFoodList(), true);
        recyclerFood.dataNew(foodList, 0);
    }

    private void refreshDiaryView() {
        if (travel == null || recyclerDiary == null) return;
        ArrayList<Diary> diaryList = ListHelper.getDiaryListByTravel(travel.getTravelDiaryList(), true);
        recyclerDiary.dataNew(diaryList, 0);
    }

    private void showDatePicker() {
        DialogHelper.showDatePicker(mActivity, TimeHelper.getJavaTimeByGo(travel.getHappenAt()), new DialogHelper.OnPickListener() {
            @Override
            public void onPick(long time) {
                travel.setHappenAt(TimeHelper.getGoTimeByJava(time));
                refreshDateView();
            }
        });
    }

    private void refreshDateView() {
        String happen = TimeHelper.getTimeShowCn_HM_MD_YMD_ByGo(travel.getHappenAt());
        tvHappenAt.setText(happen);
    }

    private void checkPush() {
        if (travel == null) return;
        String title = etTitle.getText().toString();
        if (StringUtils.isEmpty(title)) {
            ToastUtils.show(etTitle.getHint().toString());
            return;
        } else if (title.length() > SPHelper.getLimit().getTravelTitleLength()) {
            ToastUtils.show(etTitle.getHint().toString());
            return;
        }
        travel.setTitle(title);
        // 封装body
        Travel body = new Travel();
        body.setId(travel.getId());
        body.setTitle(travel.getTitle());
        body.setHappenAt(travel.getHappenAt());
        // bodyPlace
        if (recyclerPlace != null && recyclerPlace.getAdapter() != null) {
            TravelPlaceAdapter adapter = recyclerPlace.getAdapter();
            List<TravelPlace> placeList = ListHelper.getTravelPlaceListByOld(travel.getTravelPlaceList(), adapter.getData());
            body.setTravelPlaceList(placeList);
        }
        // bodyAlbum
        if (recyclerAlbum != null && recyclerAlbum.getAdapter() != null) {
            AlbumAdapter adapter = recyclerAlbum.getAdapter();
            List<TravelAlbum> albumList = ListHelper.getTravelAlbumListByOld(travel.getTravelAlbumList(), adapter.getData());
            body.setTravelAlbumList(albumList);
        }
        // bodyVideo
        if (recyclerVideo != null && recyclerVideo.getAdapter() != null) {
            VideoAdapter adapter = recyclerVideo.getAdapter();
            List<TravelVideo> videoList = ListHelper.getTravelVideoListByOld(travel.getTravelVideoList(), adapter.getData());
            body.setTravelVideoList(videoList);
        }
        // bodyFood
        if (recyclerFood != null && recyclerFood.getAdapter() != null) {
            FoodAdapter adapter = recyclerFood.getAdapter();
            List<TravelFood> foodList = ListHelper.getTravelFoodListByOld(travel.getTravelFoodList(), adapter.getData());
            body.setTravelFoodList(foodList);
        }
        // bodyDiary
        if (recyclerDiary != null && recyclerDiary.getAdapter() != null) {
            DiaryAdapter adapter = recyclerDiary.getAdapter();
            List<TravelDiary> diaryList = ListHelper.getTravelDiaryListByOld(travel.getTravelDiaryList(), adapter.getData());
            body.setTravelDiaryList(diaryList);
        }
        if (isTypeUpdate()) {
            updateApi(body);
        } else {
            addApi(body);
        }
    }

    private void updateApi(Travel travel) {
        MaterialDialog loading = getLoading(false);
        callUpdate = new RetrofitHelper().call(API.class).travelUpdate(travel);
        RetrofitHelper.enqueue(callUpdate, loading, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                // event
                Travel travel = data.getTravel();
                RxEvent<Travel> eventList = new RxEvent<>(ConsHelper.EVENT_TRAVEL_LIST_ITEM_REFRESH, travel);
                RxBus.post(eventList);
                RxEvent<Travel> eventSingle = new RxEvent<>(ConsHelper.EVENT_TRAVEL_DETAIL_REFRESH, travel);
                RxBus.post(eventSingle);
                // finish
                mActivity.finish();
            }

            @Override
            public void onFailure(String errMsg) {
            }
        });
    }

    private void addApi(Travel travel) {
        MaterialDialog loading = getLoading(false);
        callAdd = new RetrofitHelper().call(API.class).travelAdd(travel);
        RetrofitHelper.enqueue(callAdd, loading, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                // event
                RxEvent<ArrayList<Travel>> event = new RxEvent<>(ConsHelper.EVENT_TRAVEL_LIST_REFRESH, new ArrayList<Travel>());
                RxBus.post(event);
                // finish
                mActivity.finish();
            }

            @Override
            public void onFailure(String errMsg) {
            }
        });
    }

}
