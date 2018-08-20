package com.jiangzg.lovenote.fragment.note;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.activity.note.SouvenirEditForeignActivity;
import com.jiangzg.lovenote.adapter.AlbumAdapter;
import com.jiangzg.lovenote.adapter.DiaryAdapter;
import com.jiangzg.lovenote.adapter.FoodAdapter;
import com.jiangzg.lovenote.adapter.GiftAdapter;
import com.jiangzg.lovenote.adapter.TravelAdapter;
import com.jiangzg.lovenote.adapter.VideoAdapter;
import com.jiangzg.lovenote.base.BaseFragment;
import com.jiangzg.lovenote.base.BasePagerFragment;
import com.jiangzg.lovenote.domain.Album;
import com.jiangzg.lovenote.domain.Diary;
import com.jiangzg.lovenote.domain.Food;
import com.jiangzg.lovenote.domain.Gift;
import com.jiangzg.lovenote.domain.Souvenir;
import com.jiangzg.lovenote.domain.Travel;
import com.jiangzg.lovenote.domain.Video;
import com.jiangzg.lovenote.helper.ListHelper;
import com.jiangzg.lovenote.helper.RecyclerHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

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
    @BindView(R.id.tvDiary)
    TextView tvDiary;
    @BindView(R.id.rvDiary)
    RecyclerView rvDiary;
    @BindView(R.id.btnEdit)
    Button btnEdit;

    private int year;
    private Souvenir souvenir;
    private RecyclerHelper recyclerGift;
    private RecyclerHelper recyclerTravel;
    private RecyclerHelper recyclerAlbum;
    private RecyclerHelper recyclerVideo;
    private RecyclerHelper recyclerFood;
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
            btnEdit.setVisibility(View.GONE);
        } else {
            btnEdit.setVisibility(View.VISIBLE);
        }
        // recycler
        refreshView();
    }

    @Override
    protected void loadData() {
    }

    @Override
    protected void onFinish(Bundle state) {
        RecyclerHelper.release(recyclerGift);
        RecyclerHelper.release(recyclerTravel);
        RecyclerHelper.release(recyclerAlbum);
        RecyclerHelper.release(recyclerVideo);
        RecyclerHelper.release(recyclerFood);
        RecyclerHelper.release(recyclerDiary);
    }

    @OnClick({R.id.btnEdit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnEdit:
                if (souvenir == null) return;
                SouvenirEditForeignActivity.goActivity(mFragment, year, souvenir);
                break;
        }
    }

    private void refreshView() {
        if (souvenir == null) return;
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
                        .setAdapter();
            }
            recyclerGift.dataNew(giftList, 0);
        } else {
            tvGift.setVisibility(View.GONE);
            rvGift.setVisibility(View.GONE);
        }
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
            tvFood.setVisibility(View.GONE);
            rvFood.setVisibility(View.GONE);
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
