package com.jiangzg.lovenote.controller.activity.note;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.afollestad.materialdialogs.MaterialDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.chad.library.adapter.base.listener.OnItemChildLongClickListener;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.listener.OnItemLongClickListener;
import com.jiangzg.base.common.DateUtils;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.base.view.ToastUtils;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.controller.activity.base.BaseActivity;
import com.jiangzg.lovenote.controller.adapter.note.AlbumAdapter;
import com.jiangzg.lovenote.controller.adapter.note.DiaryAdapter;
import com.jiangzg.lovenote.controller.adapter.note.FoodAdapter;
import com.jiangzg.lovenote.controller.adapter.note.MovieAdapter;
import com.jiangzg.lovenote.controller.adapter.note.TravelPlaceAdapter;
import com.jiangzg.lovenote.controller.adapter.note.VideoAdapter;
import com.jiangzg.lovenote.helper.ConsHelper;
import com.jiangzg.lovenote.helper.DialogHelper;
import com.jiangzg.lovenote.helper.ListHelper;
import com.jiangzg.lovenote.helper.RecyclerHelper;
import com.jiangzg.lovenote.helper.RetrofitHelper;
import com.jiangzg.lovenote.helper.RxBus;
import com.jiangzg.lovenote.helper.SPHelper;
import com.jiangzg.lovenote.helper.TimeHelper;
import com.jiangzg.lovenote.helper.ViewHelper;
import com.jiangzg.lovenote.model.api.API;
import com.jiangzg.lovenote.model.api.Result;
import com.jiangzg.lovenote.model.entity.Album;
import com.jiangzg.lovenote.model.entity.Diary;
import com.jiangzg.lovenote.model.entity.Food;
import com.jiangzg.lovenote.model.entity.Movie;
import com.jiangzg.lovenote.model.entity.Travel;
import com.jiangzg.lovenote.model.entity.TravelAlbum;
import com.jiangzg.lovenote.model.entity.TravelDiary;
import com.jiangzg.lovenote.model.entity.TravelFood;
import com.jiangzg.lovenote.model.entity.TravelMovie;
import com.jiangzg.lovenote.model.entity.TravelPlace;
import com.jiangzg.lovenote.model.entity.TravelVideo;
import com.jiangzg.lovenote.model.entity.Video;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;
import rx.Observable;

public class TravelEditActivity extends BaseActivity<TravelEditActivity> {

    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.etTitle)
    EditText etTitle;
    @BindView(R.id.btnHappenAt)
    Button btnHappenAt;
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
    @BindView(R.id.rvMovie)
    RecyclerView rvMovie;
    @BindView(R.id.cvMovieAdd)
    CardView cvMovieAdd;
    @BindView(R.id.rvDiary)
    RecyclerView rvDiary;
    @BindView(R.id.cvDiaryAdd)
    CardView cvDiaryAdd;

    private Travel travel;
    private RecyclerHelper recyclerPlace;
    private RecyclerHelper recyclerAlbum;
    private RecyclerHelper recyclerVideo;
    private RecyclerHelper recyclerFood;
    private RecyclerHelper recyclerMovie;
    private RecyclerHelper recyclerDiary;
    private Observable<TravelPlace> obAddPlace;
    private Observable<Album> obSelectAlbum;
    private Observable<Video> obSelectVideo;
    private Observable<Food> obSelectFood;
    private Observable<Movie> obSelectMovie;
    private Observable<Diary> obSelectDiary;
    private Call<Result> callAdd;
    private Call<Result> callUpdate;

    public static void goActivity(Activity from) {
        Intent intent = new Intent(from, TravelEditActivity.class);
        intent.putExtra("from", ConsHelper.ACT_EDIT_FROM_ADD);
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
        intent.putExtra("from", ConsHelper.ACT_EDIT_FROM_UPDATE);
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
        if (isFromUpdate()) {
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
                        showDeleteDialogNoApi(adapter, position, R.string.confirm_delete_this_track);
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
                        showDeleteDialogNoApi(adapter, position, R.string.confirm_remove_this_album);
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
                        switch (view.getId()) {
                            case R.id.cvVideo: // 删除
                                showDeleteDialogNoApi(adapter, position, R.string.confirm_remove_this_video);
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
                        showDeleteDialogNoApi(adapter, position, R.string.confirm_remove_this_food);
                    }
                });
        refreshFoodView();
        // movie
        recyclerMovie = new RecyclerHelper(rvMovie)
                .initLayoutManager(new LinearLayoutManager(mActivity) {
                    @Override
                    public boolean canScrollVertically() {
                        return false;
                    }
                })
                .initAdapter(new MovieAdapter(mActivity))
                .setAdapter()
                .listenerClick(new OnItemChildClickListener() {
                    @Override
                    public void onSimpleItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                        MovieAdapter movieAdapter = (MovieAdapter) adapter;
                        switch (view.getId()) {
                            case R.id.tvAddress:
                                movieAdapter.goMapShow(position);
                                break;
                        }
                    }
                })
                .listenerClick(new OnItemLongClickListener() {
                    @Override
                    public void onSimpleItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                        showDeleteDialogNoApi(adapter, position, R.string.confirm_remove_this_movie);
                    }
                });
        refreshMovieView();
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
                        showDeleteDialogNoApi(adapter, position, R.string.confirm_remove_this_diary);
                    }
                });
        refreshDiaryView();
        // addView
        refreshAddView();
    }

    @Override
    protected void initData(Intent intent, Bundle state) {
        // event
        obAddPlace = RxBus.register(ConsHelper.EVENT_TRAVEL_EDIT_ADD_PLACE, travelPlace -> {
            if (recyclerPlace == null) return;
            List<TravelPlace> placeList = new ArrayList<>();
            placeList.add(travelPlace);
            recyclerPlace.dataAdd(placeList);
            refreshAddView();
        });
        obSelectAlbum = RxBus.register(ConsHelper.EVENT_ALBUM_SELECT, album -> {
            if (recyclerAlbum == null) return;
            List<Album> albumList = new ArrayList<>();
            albumList.add(album);
            recyclerAlbum.dataAdd(albumList);
            refreshAddView();
        });
        obSelectVideo = RxBus.register(ConsHelper.EVENT_VIDEO_SELECT, video -> {
            if (recyclerVideo == null) return;
            List<Video> videoList = new ArrayList<>();
            videoList.add(video);
            recyclerVideo.dataAdd(videoList);
            refreshAddView();
        });
        obSelectFood = RxBus.register(ConsHelper.EVENT_FOOD_SELECT, food -> {
            if (recyclerFood == null) return;
            List<Food> foodList = new ArrayList<>();
            foodList.add(food);
            recyclerFood.dataAdd(foodList);
            refreshAddView();
        });
        obSelectMovie = RxBus.register(ConsHelper.EVENT_MOVIE_SELECT, movie -> {
            if (recyclerMovie == null) return;
            List<Movie> movieList = new ArrayList<>();
            movieList.add(movie);
            recyclerMovie.dataAdd(movieList);
            refreshAddView();
        });
        obSelectDiary = RxBus.register(ConsHelper.EVENT_DIARY_SELECT, diary -> {
            if (recyclerDiary == null) return;
            List<Diary> diaryList = new ArrayList<>();
            diaryList.add(diary);
            recyclerDiary.dataAdd(diaryList);
            refreshAddView();
        });
    }

    @Override
    protected void onFinish(Bundle state) {
        RetrofitHelper.cancel(callAdd);
        RetrofitHelper.cancel(callUpdate);
        RxBus.unregister(ConsHelper.EVENT_TRAVEL_EDIT_ADD_PLACE, obAddPlace);
        RxBus.unregister(ConsHelper.EVENT_ALBUM_SELECT, obSelectAlbum);
        RxBus.unregister(ConsHelper.EVENT_VIDEO_SELECT, obSelectVideo);
        RxBus.unregister(ConsHelper.EVENT_FOOD_SELECT, obSelectFood);
        RxBus.unregister(ConsHelper.EVENT_MOVIE_SELECT, obSelectMovie);
        RxBus.unregister(ConsHelper.EVENT_DIARY_SELECT, obSelectDiary);
        RecyclerHelper.release(recyclerPlace);
        RecyclerHelper.release(recyclerAlbum);
        RecyclerHelper.release(recyclerVideo);
        RecyclerHelper.release(recyclerFood);
        RecyclerHelper.release(recyclerMovie);
        RecyclerHelper.release(recyclerDiary);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.commit, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuCommit: // 提交
                checkPush();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick({R.id.btnHappenAt, R.id.cvPlaceAdd, R.id.cvAlbumAdd, R.id.cvVideoAdd,
            R.id.cvFoodAdd, R.id.cvMovieAdd, R.id.cvDiaryAdd})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnHappenAt: // 日期
                showDatePicker();
                break;
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
            case R.id.cvMovieAdd: // 电影
                MovieListActivity.goActivityBySelect(mActivity);
                break;
            case R.id.cvDiaryAdd: // 日记
                DiaryListActivity.goActivityBySelect(mActivity);
                break;
        }
    }

    private boolean isFromUpdate() {
        return getIntent().getIntExtra("from", ConsHelper.ACT_EDIT_FROM_ADD) == ConsHelper.ACT_EDIT_FROM_UPDATE;
    }

    private void refreshAddView() {
        if (travel == null) {
            cvPlaceAdd.setVisibility(View.VISIBLE);
            cvAlbumAdd.setVisibility(View.VISIBLE);
            cvVideoAdd.setVisibility(View.VISIBLE);
            cvFoodAdd.setVisibility(View.VISIBLE);
            cvMovieAdd.setVisibility(View.VISIBLE);
            cvDiaryAdd.setVisibility(View.VISIBLE);
            return;
        }
        // place
        int placeCount = SPHelper.getLimit().getTravelPlaceCount();
        if (recyclerPlace == null || recyclerPlace.getAdapter() == null) {
            cvPlaceAdd.setVisibility(View.VISIBLE);
        } else if (recyclerPlace.getAdapter().getData().size() < placeCount) {
            cvPlaceAdd.setVisibility(View.VISIBLE);
        } else {
            cvPlaceAdd.setVisibility(View.GONE);
        }
        // album
        int albumCount = SPHelper.getLimit().getTravelAlbumCount();
        if (recyclerAlbum == null || recyclerAlbum.getAdapter() == null) {
            cvAlbumAdd.setVisibility(View.VISIBLE);
        } else if (recyclerAlbum.getAdapter().getData().size() < albumCount) {
            cvAlbumAdd.setVisibility(View.VISIBLE);
        } else {
            cvAlbumAdd.setVisibility(View.GONE);
        }
        // video
        int videoCount = SPHelper.getLimit().getTravelVideoCount();
        if (recyclerVideo == null || recyclerVideo.getAdapter() == null) {
            cvVideoAdd.setVisibility(View.VISIBLE);
        } else if (recyclerVideo.getAdapter().getData().size() < videoCount) {
            cvVideoAdd.setVisibility(View.VISIBLE);
        } else {
            cvVideoAdd.setVisibility(View.GONE);
        }
        // food
        int foodCount = SPHelper.getLimit().getTravelFoodCount();
        if (recyclerFood == null || recyclerFood.getAdapter() == null) {
            cvFoodAdd.setVisibility(View.VISIBLE);
        } else if (recyclerFood.getAdapter().getData().size() < foodCount) {
            cvFoodAdd.setVisibility(View.VISIBLE);
        } else {
            cvFoodAdd.setVisibility(View.GONE);
        }
        // movie
        int movieCount = SPHelper.getLimit().getTravelMovieCount();
        if (recyclerMovie == null || recyclerMovie.getAdapter() == null) {
            cvMovieAdd.setVisibility(View.VISIBLE);
        } else if (recyclerMovie.getAdapter().getData().size() < movieCount) {
            cvMovieAdd.setVisibility(View.VISIBLE);
        } else {
            cvMovieAdd.setVisibility(View.GONE);
        }
        // diary
        int diaryCount = SPHelper.getLimit().getTravelDiaryCount();
        if (recyclerDiary == null || recyclerDiary.getAdapter() == null) {
            cvDiaryAdd.setVisibility(View.VISIBLE);
        } else if (recyclerDiary.getAdapter().getData().size() < diaryCount) {
            cvDiaryAdd.setVisibility(View.VISIBLE);
        } else {
            cvDiaryAdd.setVisibility(View.GONE);
        }
    }

    private void refreshPlaceView() {
        if (travel == null || recyclerPlace == null) return;
        List<TravelPlace> travelPlaces = travel.getTravelPlaceList() == null ? new ArrayList<>() : travel.getTravelPlaceList();
        // 不能直接引用，得转移
        List<TravelPlace> placeList = new ArrayList<>(travelPlaces);
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

    private void refreshMovieView() {
        if (travel == null || recyclerMovie == null) return;
        ArrayList<Movie> movieList = ListHelper.getMovieListByTravel(travel.getTravelMovieList(), true);
        recyclerMovie.dataNew(movieList, 0);
    }

    private void refreshDiaryView() {
        if (travel == null || recyclerDiary == null) return;
        ArrayList<Diary> diaryList = ListHelper.getDiaryListByTravel(travel.getTravelDiaryList(), true);
        recyclerDiary.dataNew(diaryList, 0);
    }

    private void showDatePicker() {
        if (travel == null) return;
        DialogHelper.showDatePicker(mActivity, TimeHelper.getJavaTimeByGo(travel.getHappenAt()), time -> {
            travel.setHappenAt(TimeHelper.getGoTimeByJava(time));
            refreshDateView();
        });
    }

    private void refreshDateView() {
        if (travel == null) return;
        String happen = TimeHelper.getTimeShowLocal_HM_MD_YMD_ByGo(travel.getHappenAt());
        btnHappenAt.setText(happen);
    }

    private void showDeleteDialogNoApi(final BaseQuickAdapter adapter, final int position, @StringRes int contentRes) {
        MaterialDialog dialog = DialogHelper.getBuild(mActivity)
                .cancelable(true)
                .canceledOnTouchOutside(true)
                .content(contentRes)
                .positiveText(R.string.confirm_no_wrong)
                .negativeText(R.string.i_think_again)
                .onPositive((dialog1, which) -> {
                    adapter.remove(position);
                    refreshAddView();
                })
                .build();
        DialogHelper.showWithAnim(dialog);
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
        // bodyMovie
        if (recyclerMovie != null && recyclerMovie.getAdapter() != null) {
            MovieAdapter adapter = recyclerMovie.getAdapter();
            List<TravelMovie> movieList = ListHelper.getTravelMovieListByOld(travel.getTravelMovieList(), adapter.getData());
            body.setTravelMovieList(movieList);
        }
        // bodyDiary
        if (recyclerDiary != null && recyclerDiary.getAdapter() != null) {
            DiaryAdapter adapter = recyclerDiary.getAdapter();
            List<TravelDiary> diaryList = ListHelper.getTravelDiaryListByOld(travel.getTravelDiaryList(), adapter.getData());
            body.setTravelDiaryList(diaryList);
        }
        if (isFromUpdate()) {
            updateApi(body);
        } else {
            addApi(body);
        }
    }

    private void updateApi(Travel travel) {
        if (travel == null) return;
        callUpdate = new RetrofitHelper().call(API.class).noteTravelUpdate(travel);
        MaterialDialog loading = getLoading(false);
        RetrofitHelper.enqueue(callUpdate, loading, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                // event
                Travel travel = data.getTravel();
                RxBus.post(new RxBus.Event<>(ConsHelper.EVENT_TRAVEL_LIST_ITEM_REFRESH, travel));
                RxBus.post(new RxBus.Event<>(ConsHelper.EVENT_TRAVEL_DETAIL_REFRESH, travel));
                // finish
                mActivity.finish();
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
            }
        });
    }

    private void addApi(Travel travel) {
        if (travel == null) return;
        callAdd = new RetrofitHelper().call(API.class).noteTravelAdd(travel);
        MaterialDialog loading = getLoading(false);
        RetrofitHelper.enqueue(callAdd, loading, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                // event
                RxBus.post(new RxBus.Event<>(ConsHelper.EVENT_TRAVEL_LIST_REFRESH, new ArrayList<>()));
                // finish
                mActivity.finish();
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
            }
        });
    }

}
