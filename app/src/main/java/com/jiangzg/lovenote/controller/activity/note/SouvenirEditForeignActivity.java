package com.jiangzg.lovenote.controller.activity.note;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.afollestad.materialdialogs.MaterialDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.listener.OnItemLongClickListener;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.controller.activity.base.BaseActivity;
import com.jiangzg.lovenote.controller.adapter.note.AlbumAdapter;
import com.jiangzg.lovenote.controller.adapter.note.DiaryAdapter;
import com.jiangzg.lovenote.controller.adapter.note.FoodAdapter;
import com.jiangzg.lovenote.controller.adapter.note.GiftAdapter;
import com.jiangzg.lovenote.controller.adapter.note.MovieAdapter;
import com.jiangzg.lovenote.controller.adapter.note.TravelAdapter;
import com.jiangzg.lovenote.controller.adapter.note.VideoAdapter;
import com.jiangzg.lovenote.helper.common.ListHelper;
import com.jiangzg.lovenote.helper.common.RxBus;
import com.jiangzg.lovenote.helper.common.SPHelper;
import com.jiangzg.lovenote.helper.system.RetrofitHelper;
import com.jiangzg.lovenote.helper.view.DialogHelper;
import com.jiangzg.lovenote.helper.view.RecyclerHelper;
import com.jiangzg.lovenote.helper.view.ViewHelper;
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
    @BindView(R.id.rvTravel)
    RecyclerView rvTravel;
    @BindView(R.id.llTravelAdd)
    LinearLayout llTravelAdd;
    @BindView(R.id.rvGift)
    RecyclerView rvGift;
    @BindView(R.id.llGiftAdd)
    LinearLayout llGiftAdd;
    @BindView(R.id.rvAlbum)
    RecyclerView rvAlbum;
    @BindView(R.id.llAlbumAdd)
    LinearLayout llAlbumAdd;
    @BindView(R.id.rvVideo)
    RecyclerView rvVideo;
    @BindView(R.id.llVideoAdd)
    LinearLayout llVideoAdd;
    @BindView(R.id.rvFood)
    RecyclerView rvFood;
    @BindView(R.id.llFoodAdd)
    LinearLayout llFoodAdd;
    @BindView(R.id.rvMovie)
    RecyclerView rvMovie;
    @BindView(R.id.llMovieAdd)
    LinearLayout llMovieAdd;
    @BindView(R.id.rvDiary)
    RecyclerView rvDiary;
    @BindView(R.id.llDiaryAdd)
    LinearLayout llDiaryAdd;

    private int year;
    private Souvenir souvenir;
    private RecyclerHelper recyclerTravel;
    private RecyclerHelper recyclerGift;
    private RecyclerHelper recyclerAlbum;
    private RecyclerHelper recyclerVideo;
    private RecyclerHelper recyclerFood;
    private RecyclerHelper recyclerMovie;
    private RecyclerHelper recyclerDiary;

    public static void goActivity(Fragment from, int year, Souvenir souvenir) {
        if (souvenir == null || souvenir.getId() == 0) return;
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
            mActivity.finish();
        }
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
                        showDeleteDialogNoApi(adapter, position);
                    }
                });
        refreshTravelView();
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
                .listenerClick(new OnItemChildClickListener() {
                    @Override
                    public void onSimpleItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                        GiftAdapter giftAdapter = (GiftAdapter) adapter;
                        switch (view.getId()) {
                            case R.id.ivMore: // 编辑
                                giftAdapter.goEditActivity(position);
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
        refreshGiftView();
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
                .initLayoutManager(new GridLayoutManager(mActivity, 2) {
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
                            case R.id.ivLocation: // 地图
                                videoAdapter.goMapShow(position);
                                break;
                        }
                    }
                })
                .listenerClick(new OnItemClickListener() {
                    @Override
                    public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
                        VideoAdapter videoAdapter = (VideoAdapter) adapter;
                        videoAdapter.playVideo(position);
                    }
                })
                .listenerClick(new OnItemLongClickListener() {
                    @Override
                    public void onSimpleItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                        showDeleteDialogNoApi(adapter, position);
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
        Observable<Travel> obSelectTravel = RxBus.register(RxBus.EVENT_TRAVEL_SELECT, travel -> {
            if (recyclerTravel == null) return;
            List<Travel> travelList = new ArrayList<>();
            travelList.add(travel);
            recyclerTravel.dataAdd(travelList);
            refreshAddView();
        });
        pushBus(RxBus.EVENT_TRAVEL_SELECT, obSelectTravel);
        Observable<Gift> obSelectGift = RxBus.register(RxBus.EVENT_GIFT_SELECT, gift -> {
            if (recyclerGift == null) return;
            List<Gift> giftList = new ArrayList<>();
            giftList.add(gift);
            recyclerGift.dataAdd(giftList);
            refreshAddView();
        });
        pushBus(RxBus.EVENT_GIFT_SELECT, obSelectGift);
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
        Observable<Travel> busTravelListDelete = RxBus.register(RxBus.EVENT_TRAVEL_LIST_ITEM_DELETE, travel -> {
            if (recyclerTravel == null) return;
            ListHelper.removeObjInAdapter(recyclerTravel.getAdapter(), travel);
            if (recyclerTravel.getAdapter().getData().size() <= 0) {
                // 删除相册
                llTravelAdd.setVisibility(View.VISIBLE);
            }
        });
        pushBus(RxBus.EVENT_TRAVEL_LIST_ITEM_DELETE, busTravelListDelete);
        Observable<Travel> busTravelListRefresh = RxBus.register(RxBus.EVENT_TRAVEL_LIST_ITEM_REFRESH, travel -> {
            if (recyclerTravel == null) return;
            ListHelper.refreshObjInAdapter(recyclerTravel.getAdapter(), travel);
        });
        pushBus(RxBus.EVENT_TRAVEL_LIST_ITEM_REFRESH, busTravelListRefresh);
        Observable<Gift> busGiftListDelete = RxBus.register(RxBus.EVENT_GIFT_LIST_ITEM_DELETE, gift -> {
            if (recyclerGift == null) return;
            ListHelper.removeObjInAdapter(recyclerGift.getAdapter(), gift);
            if (recyclerGift.getAdapter().getData().size() <= 0) {
                // 删除相册
                llGiftAdd.setVisibility(View.VISIBLE);
            }
        });
        pushBus(RxBus.EVENT_GIFT_LIST_ITEM_DELETE, busGiftListDelete);
        Observable<Gift> busGiftListRefresh = RxBus.register(RxBus.EVENT_GIFT_LIST_ITEM_REFRESH, gift -> {
            if (recyclerGift == null) return;
            ListHelper.refreshObjInAdapter(recyclerGift.getAdapter(), gift);
        });
        pushBus(RxBus.EVENT_GIFT_LIST_ITEM_REFRESH, busGiftListRefresh);
        Observable<Album> busAlbumListDelete = RxBus.register(RxBus.EVENT_ALBUM_LIST_ITEM_DELETE, album -> {
            if (recyclerAlbum == null) return;
            ListHelper.removeObjInAdapter(recyclerAlbum.getAdapter(), album);
            if (recyclerAlbum.getAdapter().getData().size() <= 0) {
                // 删除礼物
                llAlbumAdd.setVisibility(View.VISIBLE);
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
                llFoodAdd.setVisibility(View.VISIBLE);
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
                llMovieAdd.setVisibility(View.VISIBLE);
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
                llDiaryAdd.setVisibility(View.VISIBLE);
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
        RecyclerHelper.release(recyclerTravel);
        RecyclerHelper.release(recyclerGift);
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

    @OnClick({R.id.llTravelAdd, R.id.llGiftAdd, R.id.llAlbumAdd, R.id.llVideoAdd,
            R.id.llFoodAdd, R.id.llMovieAdd, R.id.llDiaryAdd})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.llTravelAdd: // 游记
                TravelListActivity.goActivityBySelect(mActivity);
                break;
            case R.id.llGiftAdd: // 礼物
                GiftListActivity.goActivityBySelect(mActivity);
                break;
            case R.id.llAlbumAdd: // 相册
                AlbumListActivity.goActivityBySelect(mActivity);
                break;
            case R.id.llVideoAdd: // 视频
                VideoListActivity.goActivityBySelect(mActivity);
                break;
            case R.id.llFoodAdd: // 美食
                FoodListActivity.goActivityBySelect(mActivity);
                break;
            case R.id.llMovieAdd: // 电影
                MovieListActivity.goActivityBySelect(mActivity);
                break;
            case R.id.llDiaryAdd: // 日记
                DiaryListActivity.goActivityBySelect(mActivity);
                break;
        }
    }

    private void showDeleteDialogNoApi(final BaseQuickAdapter adapter, final int position) {
        MaterialDialog dialog = DialogHelper.getBuild(mActivity)
                .cancelable(true)
                .canceledOnTouchOutside(true)
                .content(R.string.confirm_remove_this_note)
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
            llTravelAdd.setVisibility(View.VISIBLE);
            llGiftAdd.setVisibility(View.VISIBLE);
            llAlbumAdd.setVisibility(View.VISIBLE);
            llVideoAdd.setVisibility(View.VISIBLE);
            llFoodAdd.setVisibility(View.VISIBLE);
            llMovieAdd.setVisibility(View.VISIBLE);
            llDiaryAdd.setVisibility(View.VISIBLE);
            return;
        }
        int limitCount = SPHelper.getLimit().getSouvenirForeignYearCount();
        // travel
        if (recyclerTravel == null || recyclerTravel.getAdapter() == null) {
            llTravelAdd.setVisibility(View.VISIBLE);
        } else if (recyclerTravel.getAdapter().getData().size() < limitCount) {
            llTravelAdd.setVisibility(View.VISIBLE);
        } else {
            llTravelAdd.setVisibility(View.GONE);
        }
        // gift
        if (recyclerGift == null || recyclerGift.getAdapter() == null) {
            llGiftAdd.setVisibility(View.VISIBLE);
        } else if (recyclerGift.getAdapter().getData().size() < limitCount) {
            llGiftAdd.setVisibility(View.VISIBLE);
        } else {
            llGiftAdd.setVisibility(View.GONE);
        }
        // album
        if (recyclerAlbum == null || recyclerAlbum.getAdapter() == null) {
            llAlbumAdd.setVisibility(View.VISIBLE);
        } else if (recyclerAlbum.getAdapter().getData().size() < limitCount) {
            llAlbumAdd.setVisibility(View.VISIBLE);
        } else {
            llAlbumAdd.setVisibility(View.GONE);
        }
        // video
        if (recyclerVideo == null || recyclerVideo.getAdapter() == null) {
            llVideoAdd.setVisibility(View.VISIBLE);
        } else if (recyclerVideo.getAdapter().getData().size() < limitCount) {
            llVideoAdd.setVisibility(View.VISIBLE);
        } else {
            llVideoAdd.setVisibility(View.GONE);
        }
        // food
        if (recyclerFood == null || recyclerFood.getAdapter() == null) {
            llFoodAdd.setVisibility(View.VISIBLE);
        } else if (recyclerFood.getAdapter().getData().size() < limitCount) {
            llFoodAdd.setVisibility(View.VISIBLE);
        } else {
            llFoodAdd.setVisibility(View.GONE);
        }
        // movie
        if (recyclerMovie == null || recyclerMovie.getAdapter() == null) {
            llMovieAdd.setVisibility(View.VISIBLE);
        } else if (recyclerMovie.getAdapter().getData().size() < limitCount) {
            llMovieAdd.setVisibility(View.VISIBLE);
        } else {
            llMovieAdd.setVisibility(View.GONE);
        }
        // diary
        if (recyclerDiary == null || recyclerDiary.getAdapter() == null) {
            llDiaryAdd.setVisibility(View.VISIBLE);
        } else if (recyclerDiary.getAdapter().getData().size() < limitCount) {
            llDiaryAdd.setVisibility(View.VISIBLE);
        } else {
            llDiaryAdd.setVisibility(View.GONE);
        }
    }

    private void refreshTravelView() {
        if (souvenir == null || recyclerTravel == null) return;
        List<Travel> travelList = ListHelper.getTravelListBySouvenir(souvenir.getSouvenirTravelList(), true);
        recyclerTravel.dataNew(travelList, 0);
    }

    private void refreshGiftView() {
        if (souvenir == null || recyclerGift == null) return;
        List<Gift> giftList = ListHelper.getGiftListBySouvenir(souvenir.getSouvenirGiftList(), true);
        recyclerGift.dataNew(giftList, 0);
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
        // bodyTravel
        if (recyclerTravel != null && recyclerTravel.getAdapter() != null) {
            TravelAdapter adapter = recyclerTravel.getAdapter();
            List<SouvenirTravel> travelList = ListHelper.getSouvenirTravelListByOld(souvenir.getSouvenirTravelList(), adapter.getData());
            body.setSouvenirTravelList(travelList);
        }
        // bodyGift
        if (recyclerGift != null && recyclerGift.getAdapter() != null) {
            GiftAdapter adapter = recyclerGift.getAdapter();
            List<SouvenirGift> giftList = ListHelper.getSouvenirGiftListByOld(souvenir.getSouvenirGiftList(), adapter.getData());
            body.setSouvenirGiftList(giftList);
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
        Call<Result> api = new RetrofitHelper().call(API.class).noteSouvenirUpdateForeign(year, souvenir);
        RetrofitHelper.enqueue(api, getLoading(true), new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                // event
                Souvenir souvenir = data.getSouvenir();
                RxBus.post(new RxBus.Event<>(RxBus.EVENT_SOUVENIR_DETAIL_REFRESH, souvenir));
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
