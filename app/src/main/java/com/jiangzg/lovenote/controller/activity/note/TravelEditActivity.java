package com.jiangzg.lovenote.controller.activity.note;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import com.jiangzg.lovenote.helper.common.ListHelper;
import com.jiangzg.lovenote.helper.common.RxBus;
import com.jiangzg.lovenote.helper.common.SPHelper;
import com.jiangzg.lovenote.helper.common.TimeHelper;
import com.jiangzg.lovenote.helper.system.RetrofitHelper;
import com.jiangzg.lovenote.helper.view.DialogHelper;
import com.jiangzg.lovenote.helper.view.RecyclerHelper;
import com.jiangzg.lovenote.helper.view.ViewHelper;
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
    @BindView(R.id.llHappenAt)
    LinearLayout llHappenAt;
    @BindView(R.id.tvHappenAt)
    TextView tvHappenAt;
    @BindView(R.id.rvPlace)
    RecyclerView rvPlace;
    @BindView(R.id.rlPlaceAdd)
    RelativeLayout rlPlaceAdd;
    @BindView(R.id.rvAlbum)
    RecyclerView rvAlbum;
    @BindView(R.id.rlAlbumAdd)
    RelativeLayout rlAlbumAdd;
    @BindView(R.id.rvVideo)
    RecyclerView rvVideo;
    @BindView(R.id.rlVideoAdd)
    RelativeLayout rlVideoAdd;
    @BindView(R.id.rvFood)
    RecyclerView rvFood;
    @BindView(R.id.rlFoodAdd)
    RelativeLayout rlFoodAdd;
    @BindView(R.id.rvMovie)
    RecyclerView rvMovie;
    @BindView(R.id.rlMovieAdd)
    RelativeLayout rlMovieAdd;
    @BindView(R.id.rvDiary)
    RecyclerView rvDiary;
    @BindView(R.id.rlDiaryAdd)
    RelativeLayout rlDiaryAdd;

    private Travel travel;
    private RecyclerHelper recyclerPlace;
    private RecyclerHelper recyclerAlbum;
    private RecyclerHelper recyclerVideo;
    private RecyclerHelper recyclerFood;
    private RecyclerHelper recyclerMovie;
    private RecyclerHelper recyclerDiary;

    public static void goActivity(Activity from) {
        Intent intent = new Intent(from, TravelEditActivity.class);
        intent.putExtra("from", BaseActivity.ACT_EDIT_FROM_ADD);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    public static void goActivity(Activity from, Travel travel) {
        if (travel == null) {
            goActivity(from);
            return;
        } else if (!travel.isMine()) {
            ToastUtils.show(from.getString(R.string.can_operation_self_create_note));
            return;
        }
        Intent intent = new Intent(from, TravelEditActivity.class);
        intent.putExtra("from", BaseActivity.ACT_EDIT_FROM_UPDATE);
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
                        showDeleteDialogNoApi(adapter, position);
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
                        showDeleteDialogNoApi(adapter, position);
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
                                showDeleteDialogNoApi(adapter, position);
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
                            case R.id.ivMore: // 编辑
                                foodAdapter.goEditActivity(position);
                                break;
                            case R.id.tvAddress: // 地图显示
                                foodAdapter.goMapShow(position);
                                break;
                        }
                    }
                })
                .listenerClick(new OnItemLongClickListener() {
                    @Override
                    public void onSimpleItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                        showDeleteDialogNoApi(adapter, position);
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
                            case R.id.ivMore: // 编辑
                                movieAdapter.goEditActivity(position);
                                break;
                            case R.id.tvAddress: // 地图显示
                                movieAdapter.goMapShow(position);
                                break;
                        }
                    }
                })
                .listenerClick(new OnItemLongClickListener() {
                    @Override
                    public void onSimpleItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                        showDeleteDialogNoApi(adapter, position);
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
                        showDeleteDialogNoApi(adapter, position);
                    }
                });
        refreshDiaryView();
        // addView
        refreshAddView();
    }

    @Override
    protected void initData(Intent intent, Bundle state) {
        // event
        Observable<TravelPlace> obAddPlace = RxBus.register(RxBus.EVENT_TRAVEL_EDIT_ADD_PLACE, travelPlace -> {
            if (recyclerPlace == null) return;
            List<TravelPlace> placeList = new ArrayList<>();
            placeList.add(travelPlace);
            recyclerPlace.dataAdd(placeList);
            refreshAddView();
        });
        pushBus(RxBus.EVENT_TRAVEL_EDIT_ADD_PLACE, obAddPlace);
        Observable<Album> obSelectAlbum = RxBus.register(RxBus.EVENT_ALBUM_SELECT, album -> {
            if (recyclerAlbum == null) return;
            List<Album> albumList = new ArrayList<>();
            albumList.add(album);
            recyclerAlbum.dataAdd(albumList);
            refreshAddView();
        });
        pushBus(RxBus.EVENT_ALBUM_SELECT, obSelectAlbum);
        Observable<Video> obSelectVideo = RxBus.register(RxBus.EVENT_VIDEO_SELECT, video -> {
            if (recyclerVideo == null) return;
            List<Video> videoList = new ArrayList<>();
            videoList.add(video);
            recyclerVideo.dataAdd(videoList);
            refreshAddView();
        });
        pushBus(RxBus.EVENT_VIDEO_SELECT, obSelectVideo);
        Observable<Food> obSelectFood = RxBus.register(RxBus.EVENT_FOOD_SELECT, food -> {
            if (recyclerFood == null) return;
            List<Food> foodList = new ArrayList<>();
            foodList.add(food);
            recyclerFood.dataAdd(foodList);
            refreshAddView();
        });
        pushBus(RxBus.EVENT_FOOD_SELECT, obSelectFood);
        Observable<Movie> obSelectMovie = RxBus.register(RxBus.EVENT_MOVIE_SELECT, movie -> {
            if (recyclerMovie == null) return;
            List<Movie> movieList = new ArrayList<>();
            movieList.add(movie);
            recyclerMovie.dataAdd(movieList);
            refreshAddView();
        });
        pushBus(RxBus.EVENT_MOVIE_SELECT, obSelectMovie);
        Observable<Diary> obSelectDiary = RxBus.register(RxBus.EVENT_DIARY_SELECT, diary -> {
            if (recyclerDiary == null) return;
            List<Diary> diaryList = new ArrayList<>();
            diaryList.add(diary);
            recyclerDiary.dataAdd(diaryList);
            refreshAddView();
        });
        pushBus(RxBus.EVENT_DIARY_SELECT, obSelectDiary);

        Observable<Album> busAlbumListDelete = RxBus.register(RxBus.EVENT_ALBUM_LIST_ITEM_DELETE, album -> {
            if (recyclerAlbum == null) return;
            ListHelper.removeObjInAdapter(recyclerAlbum.getAdapter(), album);
            if (recyclerAlbum.getAdapter().getData().size() <= 0) {
                // 删除相册
                rlAlbumAdd.setVisibility(View.VISIBLE);
                rvAlbum.setVisibility(View.GONE);
            }
        });
        pushBus(RxBus.EVENT_ALBUM_LIST_ITEM_DELETE, busAlbumListDelete);
        Observable<Album> busAlbumListRefresh = RxBus.register(RxBus.EVENT_ALBUM_LIST_ITEM_REFRESH, album -> {
            if (recyclerAlbum == null) return;
            ListHelper.refreshObjInAdapter(recyclerAlbum.getAdapter(), album);
        });
        pushBus(RxBus.EVENT_ALBUM_LIST_ITEM_REFRESH, busAlbumListRefresh);
        Observable<Food> busFoodListDelete = RxBus.register(RxBus.EVENT_FOOD_LIST_ITEM_DELETE, food -> {
            if (recyclerFood == null) return;
            ListHelper.removeObjInAdapter(recyclerFood.getAdapter(), food);
            if (recyclerFood.getAdapter().getData().size() <= 0) {
                // 删除美食
                rlFoodAdd.setVisibility(View.VISIBLE);
                rvFood.setVisibility(View.GONE);
            }
        });
        pushBus(RxBus.EVENT_FOOD_LIST_ITEM_DELETE, busFoodListDelete);
        Observable<Food> busFoodListRefresh = RxBus.register(RxBus.EVENT_FOOD_LIST_ITEM_REFRESH, food -> {
            if (recyclerFood == null) return;
            ListHelper.refreshObjInAdapter(recyclerFood.getAdapter(), food);
        });
        pushBus(RxBus.EVENT_FOOD_LIST_ITEM_REFRESH, busFoodListRefresh);
        Observable<Movie> busMovieListDelete = RxBus.register(RxBus.EVENT_MOVIE_LIST_ITEM_DELETE, movie -> {
            if (recyclerMovie == null) return;
            ListHelper.removeObjInAdapter(recyclerMovie.getAdapter(), movie);
            if (recyclerMovie.getAdapter().getData().size() <= 0) {
                // 删除电影
                rlMovieAdd.setVisibility(View.VISIBLE);
                rvMovie.setVisibility(View.GONE);
            }
        });
        pushBus(RxBus.EVENT_MOVIE_LIST_ITEM_DELETE, busMovieListDelete);
        Observable<Movie> busMovieListRefresh = RxBus.register(RxBus.EVENT_MOVIE_LIST_ITEM_REFRESH, movie -> {
            if (recyclerMovie == null) return;
            ListHelper.refreshObjInAdapter(recyclerMovie.getAdapter(), movie);
        });
        pushBus(RxBus.EVENT_MOVIE_LIST_ITEM_REFRESH, busMovieListRefresh);
        Observable<Diary> busDiaryListDelete = RxBus.register(RxBus.EVENT_DIARY_LIST_ITEM_DELETE, diary -> {
            if (recyclerDiary == null) return;
            ListHelper.removeObjInAdapter(recyclerDiary.getAdapter(), diary);
            if (recyclerDiary.getAdapter().getData().size() <= 0) {
                // 删除日记
                rlDiaryAdd.setVisibility(View.VISIBLE);
                rvDiary.setVisibility(View.GONE);
            }
        });
        pushBus(RxBus.EVENT_DIARY_LIST_ITEM_DELETE, busDiaryListDelete);
        Observable<Diary> busDiaryListRefresh = RxBus.register(RxBus.EVENT_DIARY_LIST_ITEM_REFRESH, diary -> {
            if (recyclerDiary == null) return;
            ListHelper.refreshObjInAdapter(recyclerDiary.getAdapter(), diary);
        });
        pushBus(RxBus.EVENT_DIARY_LIST_ITEM_REFRESH, busDiaryListRefresh);
    }

    @Override
    protected void onFinish(Bundle state) {
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

    @OnClick({R.id.llHappenAt, R.id.rlPlaceAdd, R.id.rlAlbumAdd, R.id.rlVideoAdd,
            R.id.rlFoodAdd, R.id.rlMovieAdd, R.id.rlDiaryAdd})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.llHappenAt: // 日期
                showDatePicker();
                break;
            case R.id.rlPlaceAdd: // 足迹
                TravelPlaceEditActivity.goActivity(mActivity);
                break;
            case R.id.rlAlbumAdd: // 相册
                AlbumListActivity.goActivityBySelect(mActivity);
                break;
            case R.id.rlVideoAdd: // 视频
                VideoListActivity.goActivityBySelect(mActivity);
                break;
            case R.id.rlFoodAdd: // 美食
                FoodListActivity.goActivityBySelect(mActivity);
                break;
            case R.id.rlMovieAdd: // 电影
                MovieListActivity.goActivityBySelect(mActivity);
                break;
            case R.id.rlDiaryAdd: // 日记
                DiaryListActivity.goActivityBySelect(mActivity);
                break;
        }
    }

    private boolean isFromUpdate() {
        return getIntent().getIntExtra("from", BaseActivity.ACT_EDIT_FROM_ADD) == BaseActivity.ACT_EDIT_FROM_UPDATE;
    }

    private void refreshAddView() {
        if (travel == null) {
            rlPlaceAdd.setVisibility(View.VISIBLE);
            rlAlbumAdd.setVisibility(View.VISIBLE);
            rlVideoAdd.setVisibility(View.VISIBLE);
            rlFoodAdd.setVisibility(View.VISIBLE);
            rlMovieAdd.setVisibility(View.VISIBLE);
            rlDiaryAdd.setVisibility(View.VISIBLE);
            return;
        }
        // place
        int placeCount = SPHelper.getLimit().getTravelPlaceCount();
        if (recyclerPlace == null || recyclerPlace.getAdapter() == null) {
            rlPlaceAdd.setVisibility(View.VISIBLE);
        } else if (recyclerPlace.getAdapter().getData().size() < placeCount) {
            rlPlaceAdd.setVisibility(View.VISIBLE);
        } else {
            rlPlaceAdd.setVisibility(View.GONE);
        }
        // album
        int albumCount = SPHelper.getLimit().getTravelAlbumCount();
        if (recyclerAlbum == null || recyclerAlbum.getAdapter() == null) {
            rlAlbumAdd.setVisibility(View.VISIBLE);
        } else if (recyclerAlbum.getAdapter().getData().size() < albumCount) {
            rlAlbumAdd.setVisibility(View.VISIBLE);
        } else {
            rlAlbumAdd.setVisibility(View.GONE);
        }
        // video
        int videoCount = SPHelper.getLimit().getTravelVideoCount();
        if (recyclerVideo == null || recyclerVideo.getAdapter() == null) {
            rlVideoAdd.setVisibility(View.VISIBLE);
        } else if (recyclerVideo.getAdapter().getData().size() < videoCount) {
            rlVideoAdd.setVisibility(View.VISIBLE);
        } else {
            rlVideoAdd.setVisibility(View.GONE);
        }
        // food
        int foodCount = SPHelper.getLimit().getTravelFoodCount();
        if (recyclerFood == null || recyclerFood.getAdapter() == null) {
            rlFoodAdd.setVisibility(View.VISIBLE);
        } else if (recyclerFood.getAdapter().getData().size() < foodCount) {
            rlFoodAdd.setVisibility(View.VISIBLE);
        } else {
            rlFoodAdd.setVisibility(View.GONE);
        }
        // movie
        int movieCount = SPHelper.getLimit().getTravelMovieCount();
        if (recyclerMovie == null || recyclerMovie.getAdapter() == null) {
            rlMovieAdd.setVisibility(View.VISIBLE);
        } else if (recyclerMovie.getAdapter().getData().size() < movieCount) {
            rlMovieAdd.setVisibility(View.VISIBLE);
        } else {
            rlMovieAdd.setVisibility(View.GONE);
        }
        // diary
        int diaryCount = SPHelper.getLimit().getTravelDiaryCount();
        if (recyclerDiary == null || recyclerDiary.getAdapter() == null) {
            rlDiaryAdd.setVisibility(View.VISIBLE);
        } else if (recyclerDiary.getAdapter().getData().size() < diaryCount) {
            rlDiaryAdd.setVisibility(View.VISIBLE);
        } else {
            rlDiaryAdd.setVisibility(View.GONE);
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
        tvHappenAt.setText(String.format(Locale.getDefault(), getString(R.string.time_colon_space_holder), happen));
    }

    private void showDeleteDialogNoApi(final BaseQuickAdapter adapter, final int position) {
        MaterialDialog dialog = DialogHelper.getBuild(mActivity)
                .cancelable(true)
                .canceledOnTouchOutside(true)
                .content(R.string.confirm_delete_this_note)
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
        Call<Result> api = new RetrofitHelper().call(API.class).noteTravelUpdate(travel);
        RetrofitHelper.enqueue(api, getLoading(true), new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                // event
                Travel travel = data.getTravel();
                RxBus.post(new RxBus.Event<>(RxBus.EVENT_TRAVEL_LIST_ITEM_REFRESH, travel));
                RxBus.post(new RxBus.Event<>(RxBus.EVENT_TRAVEL_DETAIL_REFRESH, travel));
                // finish
                mActivity.finish();
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
            }
        });
        pushApi(api);
    }

    private void addApi(Travel travel) {
        if (travel == null) return;
        Call<Result> api = new RetrofitHelper().call(API.class).noteTravelAdd(travel);
        RetrofitHelper.enqueue(api, getLoading(true), new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                // event
                RxBus.post(new RxBus.Event<>(RxBus.EVENT_TRAVEL_LIST_REFRESH, new ArrayList<>()));
                // finish
                mActivity.finish();
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
            }
        });
        pushApi(api);
    }

}
