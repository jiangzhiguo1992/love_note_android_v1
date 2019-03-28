package com.jiangzg.lovenote.controller.fragment.note;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.controller.activity.note.SouvenirEditForeignActivity;
import com.jiangzg.lovenote.controller.adapter.note.AlbumAdapter;
import com.jiangzg.lovenote.controller.adapter.note.DiaryAdapter;
import com.jiangzg.lovenote.controller.adapter.note.FoodAdapter;
import com.jiangzg.lovenote.controller.adapter.note.GiftAdapter;
import com.jiangzg.lovenote.controller.adapter.note.MovieAdapter;
import com.jiangzg.lovenote.controller.adapter.note.TravelAdapter;
import com.jiangzg.lovenote.controller.adapter.note.VideoAdapter;
import com.jiangzg.lovenote.controller.fragment.base.BaseFragment;
import com.jiangzg.lovenote.controller.fragment.base.BasePagerFragment;
import com.jiangzg.lovenote.helper.common.ListHelper;
import com.jiangzg.lovenote.helper.common.RxBus;
import com.jiangzg.lovenote.helper.view.RecyclerHelper;
import com.jiangzg.lovenote.model.entity.Album;
import com.jiangzg.lovenote.model.entity.Diary;
import com.jiangzg.lovenote.model.entity.Food;
import com.jiangzg.lovenote.model.entity.Gift;
import com.jiangzg.lovenote.model.entity.Movie;
import com.jiangzg.lovenote.model.entity.Souvenir;
import com.jiangzg.lovenote.model.entity.Travel;
import com.jiangzg.lovenote.model.entity.Video;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Observable;

public class SouvenirForeignFragment extends BasePagerFragment<SouvenirForeignFragment> {

    @BindView(R.id.tvGift)
    TextView tvGift;
    @BindView(R.id.rvGift)
    RecyclerView rvGift;
    @BindView(R.id.tvTravel)
    TextView tvTravel;
    @BindView(R.id.rvTravel)
    RecyclerView rvTravel;
    @BindView(R.id.tvAlbum)
    TextView tvAlbum;
    @BindView(R.id.rvAlbum)
    RecyclerView rvAlbum;
    @BindView(R.id.tvVideo)
    TextView tvVideo;
    @BindView(R.id.rvVideo)
    RecyclerView rvVideo;
    @BindView(R.id.tvFood)
    TextView tvFood;
    @BindView(R.id.rvFood)
    RecyclerView rvFood;
    @BindView(R.id.tvMovie)
    TextView tvMovie;
    @BindView(R.id.rvMovie)
    RecyclerView rvMovie;
    @BindView(R.id.tvDiary)
    TextView tvDiary;
    @BindView(R.id.rvDiary)
    RecyclerView rvDiary;
    @BindView(R.id.rlEdit)
    RelativeLayout rlEdit;

    private int year;
    private Souvenir souvenir;
    private RecyclerHelper recyclerTravel;
    private RecyclerHelper recyclerGift;
    private RecyclerHelper recyclerAlbum;
    private RecyclerHelper recyclerVideo;
    private RecyclerHelper recyclerFood;
    private RecyclerHelper recyclerMovie;
    private RecyclerHelper recyclerDiary;

    public static SouvenirForeignFragment newFragment(int year, Souvenir souvenir) {
        Bundle bundle = new Bundle();
        bundle.putInt("year", year);
        bundle.putParcelable("souvenir", souvenir);
        return BaseFragment.newInstance(SouvenirForeignFragment.class, bundle);
    }

    @Override
    protected int getView(Bundle data) {
        year = data.getInt("year");
        souvenir = data.getParcelable("souvenir");
        return R.layout.fragment_souvenir_foreign;
    }

    @Override
    protected void initView(@Nullable Bundle state) {
        // btn
        if (souvenir == null || !souvenir.isMine()) {
            rlEdit.setVisibility(View.GONE);
        } else {
            rlEdit.setVisibility(View.VISIBLE);
        }
        // recycler
        refreshView();
    }

    @Override
    protected void loadData() {
        // event
        Observable<Travel> busTravelListDelete = RxBus.register(RxBus.EVENT_TRAVEL_LIST_ITEM_DELETE, travel -> {
            if (recyclerTravel == null) return;
            ListHelper.removeObjInAdapter(recyclerTravel.getAdapter(), travel);
            if (recyclerTravel.getAdapter().getData().size() <= 0) {
                // 删除游记
                tvTravel.setVisibility(View.GONE);
                rvTravel.setVisibility(View.GONE);
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
                // 删除礼物
                tvGift.setVisibility(View.GONE);
                rvGift.setVisibility(View.GONE);
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
                // 删除相册
                tvAlbum.setVisibility(View.GONE);
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
                tvFood.setVisibility(View.GONE);
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
                tvMovie.setVisibility(View.GONE);
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
                tvDiary.setVisibility(View.GONE);
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
        RecyclerHelper.release(recyclerTravel);
        RecyclerHelper.release(recyclerGift);
        RecyclerHelper.release(recyclerAlbum);
        RecyclerHelper.release(recyclerVideo);
        RecyclerHelper.release(recyclerFood);
        RecyclerHelper.release(recyclerMovie);
        RecyclerHelper.release(recyclerDiary);
    }

    @OnClick({R.id.rlEdit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rlEdit: // 点我编辑
                if (souvenir == null) return;
                SouvenirEditForeignActivity.goActivity(mFragment, year, souvenir);
                break;
        }
    }

    private void refreshView() {
        if (souvenir == null) return;
        // travel
        List<Travel> travelList = ListHelper.getTravelListBySouvenir(souvenir.getSouvenirTravelList(), false);
        if (travelList != null && travelList.size() > 0) {
            tvTravel.setVisibility(View.VISIBLE);
            rvTravel.setVisibility(View.VISIBLE);
            if (recyclerTravel == null) {
                recyclerTravel = new RecyclerHelper(rvTravel)
                        .initLayoutManager(new LinearLayoutManager(mActivity) {
                            @Override
                            public boolean canScrollVertically() {
                                return false;
                            }
                        })
                        .initAdapter(new TravelAdapter(mActivity))
                        .viewAnim()
                        .setAdapter()
                        .listenerClick(new OnItemClickListener() {
                            @Override
                            public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
                                TravelAdapter travelAdapter = (TravelAdapter) adapter;
                                travelAdapter.goTravelDetail(position);
                            }
                        });
            }
            recyclerTravel.dataNew(travelList, 0);
        } else {
            tvTravel.setVisibility(View.GONE);
            rvTravel.setVisibility(View.GONE);
        }
        // gift
        List<Gift> giftList = ListHelper.getGiftListBySouvenir(souvenir.getSouvenirGiftList(), false);
        if (giftList != null && giftList.size() > 0) {
            tvGift.setVisibility(View.VISIBLE);
            rvGift.setVisibility(View.VISIBLE);
            if (recyclerGift == null) {
                recyclerGift = new RecyclerHelper(rvGift)
                        .initLayoutManager(new LinearLayoutManager(mActivity) {
                            @Override
                            public boolean canScrollVertically() {
                                return false;
                            }
                        })
                        .initAdapter(new GiftAdapter(mActivity))
                        .viewAnim()
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
                        });
            }
            recyclerGift.dataNew(giftList, 0);
        } else {
            tvGift.setVisibility(View.GONE);
            rvGift.setVisibility(View.GONE);
        }
        // album
        List<Album> albumList = ListHelper.getAlbumListBySouvenir(souvenir.getSouvenirAlbumList(), false);
        if (albumList != null && albumList.size() > 0) {
            tvAlbum.setVisibility(View.VISIBLE);
            rvAlbum.setVisibility(View.VISIBLE);
            if (recyclerAlbum == null) {
                recyclerAlbum = new RecyclerHelper(rvAlbum)
                        .initLayoutManager(new LinearLayoutManager(mActivity) {
                            @Override
                            public boolean canScrollVertically() {
                                return false;
                            }
                        })
                        .initAdapter(new AlbumAdapter(mActivity))
                        .viewAnim()
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
            tvAlbum.setVisibility(View.GONE);
            rvAlbum.setVisibility(View.GONE);
        }
        // video
        ArrayList<Video> videoList = ListHelper.getVideoListBySouvenir(souvenir.getSouvenirVideoList(), false);
        if (videoList != null && videoList.size() > 0) {
            tvVideo.setVisibility(View.VISIBLE);
            rvVideo.setVisibility(View.VISIBLE);
            if (recyclerVideo == null) {
                recyclerVideo = new RecyclerHelper(rvVideo)
                        .initLayoutManager(new LinearLayoutManager(mActivity) {
                            @Override
                            public boolean canScrollVertically() {
                                return false;
                            }
                        })
                        .initAdapter(new VideoAdapter(mActivity))
                        .viewAnim()
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
                        });
            }
            recyclerVideo.dataNew(videoList, 0);
        } else {
            tvVideo.setVisibility(View.GONE);
            rvVideo.setVisibility(View.GONE);
        }
        // food
        ArrayList<Food> foodList = ListHelper.getFoodListBySouvenir(souvenir.getSouvenirFoodList(), false);
        if (foodList != null && foodList.size() > 0) {
            tvFood.setVisibility(View.VISIBLE);
            rvFood.setVisibility(View.VISIBLE);
            if (recyclerFood == null) {
                recyclerFood = new RecyclerHelper(rvFood)
                        .initLayoutManager(new LinearLayoutManager(mActivity) {
                            @Override
                            public boolean canScrollVertically() {
                                return false;
                            }
                        })
                        .initAdapter(new FoodAdapter(mActivity))
                        .viewAnim()
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
                        });
            }
            recyclerFood.dataNew(foodList, 0);
        } else {
            tvFood.setVisibility(View.GONE);
            rvFood.setVisibility(View.GONE);
        }
        // movie
        ArrayList<Movie> movieList = ListHelper.getMovieListBySouvenir(souvenir.getSouvenirMovieList(), false);
        if (movieList != null && movieList.size() > 0) {
            tvMovie.setVisibility(View.VISIBLE);
            rvMovie.setVisibility(View.VISIBLE);
            if (recyclerMovie == null) {
                recyclerMovie = new RecyclerHelper(rvMovie)
                        .initLayoutManager(new LinearLayoutManager(mActivity) {
                            @Override
                            public boolean canScrollVertically() {
                                return false;
                            }
                        })
                        .initAdapter(new MovieAdapter(mActivity))
                        .viewAnim()
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
                        });
            }
            recyclerMovie.dataNew(movieList, 0);
        } else {
            tvMovie.setVisibility(View.GONE);
            rvMovie.setVisibility(View.GONE);
        }
        // diary
        ArrayList<Diary> diaryList = ListHelper.getDiaryListBySouvenir(souvenir.getSouvenirDiaryList(), false);
        if (diaryList != null && diaryList.size() > 0) {
            tvDiary.setVisibility(View.VISIBLE);
            rvDiary.setVisibility(View.VISIBLE);
            if (recyclerDiary == null) {
                recyclerDiary = new RecyclerHelper(rvDiary)
                        .initLayoutManager(new LinearLayoutManager(mActivity) {
                            @Override
                            public boolean canScrollVertically() {
                                return false;
                            }
                        })
                        .initAdapter(new DiaryAdapter(mActivity))
                        .viewAnim()
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
            tvDiary.setVisibility(View.GONE);
            rvDiary.setVisibility(View.GONE);
        }
    }

}
