package com.jiangzg.mianmian.activity.note;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.chad.library.adapter.base.listener.OnItemChildLongClickListener;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.listener.OnItemLongClickListener;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.base.view.ToastUtils;
import com.jiangzg.mianmian.R;
import com.jiangzg.mianmian.activity.settings.HelpActivity;
import com.jiangzg.mianmian.adapter.AlbumAdapter;
import com.jiangzg.mianmian.adapter.DiaryAdapter;
import com.jiangzg.mianmian.adapter.FoodAdapter;
import com.jiangzg.mianmian.adapter.GiftAdapter;
import com.jiangzg.mianmian.adapter.TravelAdapter;
import com.jiangzg.mianmian.adapter.VideoAdapter;
import com.jiangzg.mianmian.base.BaseActivity;
import com.jiangzg.mianmian.domain.Album;
import com.jiangzg.mianmian.domain.Diary;
import com.jiangzg.mianmian.domain.Food;
import com.jiangzg.mianmian.domain.Gift;
import com.jiangzg.mianmian.domain.Help;
import com.jiangzg.mianmian.domain.Result;
import com.jiangzg.mianmian.domain.RxEvent;
import com.jiangzg.mianmian.domain.Souvenir;
import com.jiangzg.mianmian.domain.SouvenirAlbum;
import com.jiangzg.mianmian.domain.SouvenirDiary;
import com.jiangzg.mianmian.domain.SouvenirFood;
import com.jiangzg.mianmian.domain.SouvenirGift;
import com.jiangzg.mianmian.domain.SouvenirTravel;
import com.jiangzg.mianmian.domain.SouvenirVideo;
import com.jiangzg.mianmian.domain.Travel;
import com.jiangzg.mianmian.domain.Video;
import com.jiangzg.mianmian.helper.API;
import com.jiangzg.mianmian.helper.ConsHelper;
import com.jiangzg.mianmian.helper.DialogHelper;
import com.jiangzg.mianmian.helper.ListHelper;
import com.jiangzg.mianmian.helper.RecyclerHelper;
import com.jiangzg.mianmian.helper.RetrofitHelper;
import com.jiangzg.mianmian.helper.RxBus;
import com.jiangzg.mianmian.helper.SPHelper;
import com.jiangzg.mianmian.helper.ViewHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;
import rx.Observable;
import rx.functions.Action1;

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
    @BindView(R.id.rvDiary)
    RecyclerView rvDiary;
    @BindView(R.id.cvDiaryAdd)
    CardView cvDiaryAdd;
    @BindView(R.id.btnPublish)
    Button btnPublish;

    private int year;
    private Souvenir souvenir;
    private RecyclerHelper recyclerGift;
    private RecyclerHelper recyclerTravel;
    private RecyclerHelper recyclerAlbum;
    private RecyclerHelper recyclerVideo;
    private RecyclerHelper recyclerFood;
    private RecyclerHelper recyclerDiary;
    private Observable<Gift> obSelectGift;
    private Observable<Travel> obSelectTravel;
    private Observable<Album> obSelectAlbum;
    private Observable<Video> obSelectVideo;
    private Observable<Food> obSelectFood;
    private Observable<Diary> obSelectDiary;
    private Call<Result> call;

    public static void goActivity(Activity from, int year, Souvenir souvenir) {
        if (souvenir == null || !souvenir.isMine()) {
            ToastUtils.show(from.getString(R.string.can_operation_self_create_souvenir));
            return;
        }
        Intent intent = new Intent(from, SouvenirEditForeignActivity.class);
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
        obSelectGift = RxBus.register(ConsHelper.EVENT_GIFT_SELECT, new Action1<Gift>() {
            @Override
            public void call(Gift gift) {
                if (recyclerGift == null) return;
                List<Gift> giftList = new ArrayList<>();
                giftList.add(gift);
                recyclerGift.dataAdd(giftList);
                refreshAddView();
            }
        });
        obSelectTravel = RxBus.register(ConsHelper.EVENT_TRAVEL_SELECT, new Action1<Travel>() {
            @Override
            public void call(Travel travel) {
                if (recyclerTravel == null) return;
                List<Travel> travelList = new ArrayList<>();
                travelList.add(travel);
                recyclerTravel.dataAdd(travelList);
                refreshAddView();
            }
        });
        obSelectAlbum = RxBus.register(ConsHelper.EVENT_ALBUM_SELECT, new Action1<Album>() {
            @Override
            public void call(Album album) {
                if (recyclerAlbum == null) return;
                List<Album> albumList = new ArrayList<>();
                albumList.add(album);
                recyclerAlbum.dataAdd(albumList);
                refreshAddView();
            }
        });
        obSelectVideo = RxBus.register(ConsHelper.EVENT_VIDEO_SELECT, new Action1<Video>() {
            @Override
            public void call(Video video) {
                if (recyclerVideo == null) return;
                List<Video> videoList = new ArrayList<>();
                videoList.add(video);
                recyclerVideo.dataAdd(videoList);
                refreshAddView();
            }
        });
        obSelectFood = RxBus.register(ConsHelper.EVENT_FOOD_SELECT, new Action1<Food>() {
            @Override
            public void call(Food food) {
                if (recyclerFood == null) return;
                List<Food> foodList = new ArrayList<>();
                foodList.add(food);
                recyclerFood.dataAdd(foodList);
                refreshAddView();
            }
        });
        obSelectDiary = RxBus.register(ConsHelper.EVENT_DIARY_SELECT, new Action1<Diary>() {
            @Override
            public void call(Diary diary) {
                if (recyclerDiary == null) return;
                List<Diary> diaryList = new ArrayList<>();
                diaryList.add(diary);
                recyclerDiary.dataAdd(diaryList);
                refreshAddView();
            }
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
        RxBus.unregister(ConsHelper.EVENT_DIARY_SELECT, obSelectDiary);
        RecyclerHelper.release(recyclerGift);
        RecyclerHelper.release(recyclerTravel);
        RecyclerHelper.release(recyclerAlbum);
        RecyclerHelper.release(recyclerVideo);
        RecyclerHelper.release(recyclerFood);
        RecyclerHelper.release(recyclerDiary);
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
                HelpActivity.goActivity(mActivity, Help.INDEX_NOTE_SOUVENIR_FOREIGN_EDIT);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick({R.id.cvGiftAdd, R.id.cvTravelAdd, R.id.cvAlbumAdd,
            R.id.cvVideoAdd, R.id.cvFoodAdd, R.id.cvDiaryAdd,
            R.id.btnPublish})
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
            case R.id.cvDiaryAdd: // 日记
                DiaryListActivity.goActivityBySelect(mActivity);
                break;
            case R.id.btnPublish: // 发表
                checkPush();
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
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        adapter.remove(position);
                        refreshAddView();
                    }
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
                RxEvent<Souvenir> eventSingle = new RxEvent<>(ConsHelper.EVENT_SOUVENIR_DETAIL_REFRESH, souvenir);
                RxBus.post(eventSingle);
                // finish
                mActivity.finish();
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
            }
        });
    }

}
