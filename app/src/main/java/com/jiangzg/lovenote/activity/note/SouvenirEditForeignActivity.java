package com.jiangzg.lovenote.activity.note;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.chad.library.adapter.base.listener.OnItemChildLongClickListener;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.listener.OnItemLongClickListener;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.base.view.ToastUtils;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.adapter.AlbumAdapter;
import com.jiangzg.lovenote.adapter.DiaryAdapter;
import com.jiangzg.lovenote.adapter.FoodAdapter;
import com.jiangzg.lovenote.adapter.GiftAdapter;
import com.jiangzg.lovenote.adapter.MovieAdapter;
import com.jiangzg.lovenote.adapter.TravelAdapter;
import com.jiangzg.lovenote.adapter.VideoAdapter;
import com.jiangzg.lovenote.base.BaseActivity;
import com.jiangzg.lovenote.helper.ConsHelper;
import com.jiangzg.lovenote.helper.DialogHelper;
import com.jiangzg.lovenote.helper.ListHelper;
import com.jiangzg.lovenote.helper.RecyclerHelper;
import com.jiangzg.lovenote.helper.RetrofitHelper;
import com.jiangzg.lovenote.helper.RxBus;
import com.jiangzg.lovenote.helper.SPHelper;
import com.jiangzg.lovenote.helper.ViewHelper;
import com.jiangzg.lovenote.model.api.API;
import com.jiangzg.lovenote.model.api.Result;
import com.jiangzg.lovenote.model.entity.Album;
import com.jiangzg.lovenote.model.entity.Diary;
import com.jiangzg.lovenote.model.entity.Food;
import com.jiangzg.lovenote.model.entity.Gift;
import com.jiangzg.lovenote.model.entity.Movie;
import com.jiangzg.lovenote.model.entity.Souvenir;
import com.jiangzg.lovenote.model.entity.SouvenirAlbum;
import com.jiangzg.lovenote.model.entity.SouvenirDiary;
import com.jiangzg.lovenote.model.entity.SouvenirFood;
import com.jiangzg.lovenote.model.entity.SouvenirGift;
import com.jiangzg.lovenote.model.entity.SouvenirMovie;
import com.jiangzg.lovenote.model.entity.SouvenirTravel;
import com.jiangzg.lovenote.model.entity.SouvenirVideo;
import com.jiangzg.lovenote.model.entity.Travel;
import com.jiangzg.lovenote.model.entity.Video;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;
import rx.Observable;

public class SouvenirEditForeignActivity extends BaseActivity<SouvenirEditForeignActivity> {

    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.rvGift)
    RecyclerView rvGift;
    @BindView(R.id.cvGiftAdd)
    CardView cvGiftAdd;
    @BindView(R.id.rvTravel)
    RecyclerView rvTravel;
    @BindView(R.id.cvTravelAdd)
    CardView cvTravelAdd;
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

    private int year;
    private Souvenir souvenir;
    private RecyclerHelper recyclerGift;
    private RecyclerHelper recyclerTravel;
    private RecyclerHelper recyclerAlbum;
    private RecyclerHelper recyclerVideo;
    private RecyclerHelper recyclerFood;
    private RecyclerHelper recyclerMovie;
    private RecyclerHelper recyclerDiary;
    private Observable<Gift> obSelectGift;
    private Observable<Travel> obSelectTravel;
    private Observable<Album> obSelectAlbum;
    private Observable<Video> obSelectVideo;
    private Observable<Food> obSelectFood;
    private Observable<Movie> obSelectMovie;
    private Observable<Diary> obSelectDiary;
    private Call<Result> call;

    public static void goActivity(Fragment from, int year, Souvenir souvenir) {
        if (souvenir == null || !souvenir.isMine()) {
            ToastUtils.show(from.getString(R.string.can_operation_self_create_souvenir));
            return;
        }
        Intent intent = new Intent(from.getActivity(), SouvenirEditForeignActivity.class);
        intent.putExtra("year", year);
        intent.putExtra("souvenir", souvenir);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_souvenir_edit_foreign;
    }

    @Override
    protected void initView(Intent intent, Bundle state) {
        year = intent.getIntExtra("year", 0);
        ViewHelper.initTopBar(mActivity, tb, String.valueOf(year), true);
        // init
        souvenir = intent.getParcelableExtra("souvenir");
        if (souvenir == null) {
            souvenir = new Souvenir();
        }
        // gift
        recyclerGift = new RecyclerHelper(rvGift)
                .initLayoutManager(new LinearLayoutManager(mActivity) {
                    @Override
                    public boolean canScrollVertically() {
                        return false;
                    }
                })
                .initAdapter(new GiftAdapter(mActivity))
                .setAdapter()
                .listenerClick(new OnItemLongClickListener() {
                    @Override
                    public void onSimpleItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                        showDeleteDialogNoApi(adapter, position, R.string.confirm_remove_this_gift);
                    }
                });
        refreshGiftView();
        // travel
        recyclerTravel = new RecyclerHelper(rvTravel)
                .initLayoutManager(new LinearLayoutManager(mActivity) {
                    @Override
                    public boolean canScrollVertically() {
                        return false;
                    }
                })
                .initAdapter(new TravelAdapter(mActivity))
                .setAdapter()
                .listenerClick(new OnItemClickListener() {
                    @Override
                    public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
                        TravelAdapter travelAdapter = (TravelAdapter) adapter;
                        travelAdapter.goTravelDetail(position);
                    }
                })
                .listenerClick(new OnItemLongClickListener() {
                    @Override
                    public void onSimpleItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                        showDeleteDialogNoApi(adapter, position, R.string.confirm_remove_this_travel);
                    }
                });
        refreshTravelView();
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
        obSelectGift = RxBus.register(ConsHelper.EVENT_GIFT_SELECT, gift -> {
            if (recyclerGift == null) return;
            List<Gift> giftList = new ArrayList<>();
            giftList.add(gift);
            recyclerGift.dataAdd(giftList);
            refreshAddView();
        });
        obSelectTravel = RxBus.register(ConsHelper.EVENT_TRAVEL_SELECT, travel -> {
            if (recyclerTravel == null) return;
            List<Travel> travelList = new ArrayList<>();
            travelList.add(travel);
            recyclerTravel.dataAdd(travelList);
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
        RetrofitHelper.cancel(call);
        RxBus.unregister(ConsHelper.EVENT_GIFT_SELECT, obSelectGift);
        RxBus.unregister(ConsHelper.EVENT_TRAVEL_SELECT, obSelectTravel);
        RxBus.unregister(ConsHelper.EVENT_ALBUM_SELECT, obSelectAlbum);
        RxBus.unregister(ConsHelper.EVENT_VIDEO_SELECT, obSelectVideo);
        RxBus.unregister(ConsHelper.EVENT_FOOD_SELECT, obSelectFood);
        RxBus.unregister(ConsHelper.EVENT_MOVIE_SELECT, obSelectMovie);
        RxBus.unregister(ConsHelper.EVENT_DIARY_SELECT, obSelectDiary);
        RecyclerHelper.release(recyclerGift);
        RecyclerHelper.release(recyclerTravel);
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

    @OnClick({R.id.cvGiftAdd, R.id.cvTravelAdd, R.id.cvAlbumAdd, R.id.cvVideoAdd,
            R.id.cvFoodAdd, R.id.cvMovieAdd, R.id.cvDiaryAdd})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.cvGiftAdd: // 礼物
                GiftListActivity.goActivityBySelect(mActivity);
                break;
            case R.id.cvTravelAdd: // 游记
                TravelListActivity.goActivityBySelect(mActivity);
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

    private void refreshAddView() {
        if (souvenir == null) {
            cvGiftAdd.setVisibility(View.VISIBLE);
            cvTravelAdd.setVisibility(View.VISIBLE);
            cvAlbumAdd.setVisibility(View.VISIBLE);
            cvVideoAdd.setVisibility(View.VISIBLE);
            cvFoodAdd.setVisibility(View.VISIBLE);
            cvMovieAdd.setVisibility(View.VISIBLE);
            cvDiaryAdd.setVisibility(View.VISIBLE);
            return;
        }
        int limitCount = SPHelper.getLimit().getSouvenirForeignYearCount();
        // gift
        if (recyclerGift == null || recyclerGift.getAdapter() == null) {
            cvGiftAdd.setVisibility(View.VISIBLE);
        } else if (recyclerGift.getAdapter().getData().size() < limitCount) {
            cvGiftAdd.setVisibility(View.VISIBLE);
        } else {
            cvGiftAdd.setVisibility(View.GONE);
        }
        // travel
        if (recyclerTravel == null || recyclerTravel.getAdapter() == null) {
            cvTravelAdd.setVisibility(View.VISIBLE);
        } else if (recyclerTravel.getAdapter().getData().size() < limitCount) {
            cvTravelAdd.setVisibility(View.VISIBLE);
        } else {
            cvTravelAdd.setVisibility(View.GONE);
        }
        // album
        if (recyclerAlbum == null || recyclerAlbum.getAdapter() == null) {
            cvAlbumAdd.setVisibility(View.VISIBLE);
        } else if (recyclerAlbum.getAdapter().getData().size() < limitCount) {
            cvAlbumAdd.setVisibility(View.VISIBLE);
        } else {
            cvAlbumAdd.setVisibility(View.GONE);
        }
        // video
        if (recyclerVideo == null || recyclerVideo.getAdapter() == null) {
            cvVideoAdd.setVisibility(View.VISIBLE);
        } else if (recyclerVideo.getAdapter().getData().size() < limitCount) {
            cvVideoAdd.setVisibility(View.VISIBLE);
        } else {
            cvVideoAdd.setVisibility(View.GONE);
        }
        // food
        if (recyclerFood == null || recyclerFood.getAdapter() == null) {
            cvFoodAdd.setVisibility(View.VISIBLE);
        } else if (recyclerFood.getAdapter().getData().size() < limitCount) {
            cvFoodAdd.setVisibility(View.VISIBLE);
        } else {
            cvFoodAdd.setVisibility(View.GONE);
        }
        // movie
        if (recyclerMovie == null || recyclerMovie.getAdapter() == null) {
            cvMovieAdd.setVisibility(View.VISIBLE);
        } else if (recyclerMovie.getAdapter().getData().size() < limitCount) {
            cvMovieAdd.setVisibility(View.VISIBLE);
        } else {
            cvMovieAdd.setVisibility(View.GONE);
        }
        // diary
        if (recyclerDiary == null || recyclerDiary.getAdapter() == null) {
            cvDiaryAdd.setVisibility(View.VISIBLE);
        } else if (recyclerDiary.getAdapter().getData().size() < limitCount) {
            cvDiaryAdd.setVisibility(View.VISIBLE);
        } else {
            cvDiaryAdd.setVisibility(View.GONE);
        }
    }

    private void refreshGiftView() {
        if (souvenir == null || recyclerGift == null) return;
        List<Gift> giftList = ListHelper.getGiftListBySouvenir(souvenir.getSouvenirGiftList(), true);
        recyclerGift.dataNew(giftList, 0);
    }

    private void refreshTravelView() {
        if (souvenir == null || recyclerTravel == null) return;
        List<Travel> travelList = ListHelper.getTravelListBySouvenir(souvenir.getSouvenirTravelList(), true);
        recyclerTravel.dataNew(travelList, 0);
    }

    private void refreshAlbumView() {
        if (souvenir == null || recyclerAlbum == null) return;
        List<Album> albumList = ListHelper.getAlbumListBySouvenir(souvenir.getSouvenirAlbumList(), true);
        recyclerAlbum.dataNew(albumList, 0);
    }

    private void refreshVideoView() {
        if (souvenir == null || recyclerVideo == null) return;
        List<Video> videoList = ListHelper.getVideoListBySouvenir(souvenir.getSouvenirVideoList(), true);
        recyclerVideo.dataNew(videoList, 0);
    }

    private void refreshFoodView() {
        if (souvenir == null || recyclerFood == null) return;
        ArrayList<Food> foodList = ListHelper.getFoodListBySouvenir(souvenir.getSouvenirFoodList(), true);
        recyclerFood.dataNew(foodList, 0);
    }

    private void refreshMovieView() {
        if (souvenir == null || recyclerMovie == null) return;
        ArrayList<Movie> movieList = ListHelper.getMovieListBySouvenir(souvenir.getSouvenirMovieList(), true);
        recyclerMovie.dataNew(movieList, 0);
    }

    private void refreshDiaryView() {
        if (souvenir == null || recyclerDiary == null) return;
        ArrayList<Diary> diaryList = ListHelper.getDiaryListBySouvenir(souvenir.getSouvenirDiaryList(), true);
        recyclerDiary.dataNew(diaryList, 0);
    }

    private void checkPush() {
        if (souvenir == null) return;
        // 封装body
        Souvenir body = new Souvenir();
        body.setId(souvenir.getId());
        // bodyGift
        if (recyclerGift != null && recyclerGift.getAdapter() != null) {
            GiftAdapter adapter = recyclerGift.getAdapter();
            List<SouvenirGift> giftList = ListHelper.getSouvenirGiftListByOld(souvenir.getSouvenirGiftList(), adapter.getData());
            body.setSouvenirGiftList(giftList);
        }
        // bodyTravel
        if (recyclerTravel != null && recyclerTravel.getAdapter() != null) {
            TravelAdapter adapter = recyclerTravel.getAdapter();
            List<SouvenirTravel> travelList = ListHelper.getSouvenirTravelListByOld(souvenir.getSouvenirTravelList(), adapter.getData());
            body.setSouvenirTravelList(travelList);
        }
        // bodyAlbum
        if (recyclerAlbum != null && recyclerAlbum.getAdapter() != null) {
            AlbumAdapter adapter = recyclerAlbum.getAdapter();
            List<SouvenirAlbum> albumList = ListHelper.getSouvenirAlbumListByOld(souvenir.getSouvenirAlbumList(), adapter.getData());
            body.setSouvenirAlbumList(albumList);
        }
        // bodyVideo
        if (recyclerVideo != null && recyclerVideo.getAdapter() != null) {
            VideoAdapter adapter = recyclerVideo.getAdapter();
            List<SouvenirVideo> videoList = ListHelper.getSouvenirVideoListByOld(souvenir.getSouvenirVideoList(), adapter.getData());
            body.setSouvenirVideoList(videoList);
        }
        // bodyFood
        if (recyclerFood != null && recyclerFood.getAdapter() != null) {
            FoodAdapter adapter = recyclerFood.getAdapter();
            List<SouvenirFood> foodList = ListHelper.getSouvenirFoodListByOld(souvenir.getSouvenirFoodList(), adapter.getData());
            body.setSouvenirFoodList(foodList);
        }
        // bodyMovie
        if (recyclerMovie != null && recyclerMovie.getAdapter() != null) {
            MovieAdapter adapter = recyclerMovie.getAdapter();
            List<SouvenirMovie> movieList = ListHelper.getSouvenirMovieListByOld(souvenir.getSouvenirMovieList(), adapter.getData());
            body.setSouvenirMovieList(movieList);
        }
        // bodyDiary
        if (recyclerDiary != null && recyclerDiary.getAdapter() != null) {
            DiaryAdapter adapter = recyclerDiary.getAdapter();
            List<SouvenirDiary> diaryList = ListHelper.getSouvenirDiaryListByOld(souvenir.getSouvenirDiaryList(), adapter.getData());
            body.setSouvenirDiaryList(diaryList);
        }
        updateApi(body);
    }

    private void updateApi(Souvenir souvenir) {
        if (souvenir == null) return;
        call = new RetrofitHelper().call(API.class).noteSouvenirUpdateForeign(year, souvenir);
        MaterialDialog loading = getLoading(false);
        RetrofitHelper.enqueue(call, loading, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                // event
                Souvenir souvenir = data.getSouvenir();
                RxBus.post(new RxBus.Event<>(ConsHelper.EVENT_SOUVENIR_DETAIL_REFRESH, souvenir));
                // finish
                mActivity.finish();
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
            }
        });
    }

}
