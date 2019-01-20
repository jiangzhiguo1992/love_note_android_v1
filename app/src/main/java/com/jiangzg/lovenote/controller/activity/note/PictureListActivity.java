package com.jiangzg.lovenote.controller.activity.note;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.chad.library.adapter.base.listener.OnItemLongClickListener;
import com.jiangzg.base.common.ConvertUtils;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.base.view.ScreenUtils;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.controller.activity.base.BaseActivity;
import com.jiangzg.lovenote.controller.adapter.note.PictureAdapter;
import com.jiangzg.lovenote.helper.common.ListHelper;
import com.jiangzg.lovenote.helper.common.OssResHelper;
import com.jiangzg.lovenote.helper.common.RxBus;
import com.jiangzg.lovenote.helper.common.TimeHelper;
import com.jiangzg.lovenote.helper.media.FrescoHelper;
import com.jiangzg.lovenote.helper.system.RetrofitHelper;
import com.jiangzg.lovenote.helper.view.RecyclerHelper;
import com.jiangzg.lovenote.helper.view.ViewHelper;
import com.jiangzg.lovenote.model.api.API;
import com.jiangzg.lovenote.model.api.Result;
import com.jiangzg.lovenote.model.entity.Album;
import com.jiangzg.lovenote.model.entity.Picture;
import com.jiangzg.lovenote.view.FrescoView;
import com.jiangzg.lovenote.view.GSwipeRefreshLayout;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;
import rx.Observable;

public class PictureListActivity extends BaseActivity<PictureListActivity> {

    @BindView(R.id.root)
    CoordinatorLayout root;
    @BindView(R.id.abl)
    AppBarLayout abl;
    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.ctl)
    CollapsingToolbarLayout ctl;

    @BindView(R.id.rlHead)
    RelativeLayout rlHead;
    @BindView(R.id.ivCover)
    FrescoView ivCover;
    @BindView(R.id.tvPictureCount)
    TextView tvPictureCount;
    @BindView(R.id.tvCreateAt)
    TextView tvCreateAt;
    @BindView(R.id.tvUpdateAt)
    TextView tvUpdateAt;

    @BindView(R.id.rv)
    RecyclerView rv;
    @BindView(R.id.srl)
    GSwipeRefreshLayout srl;

    @BindView(R.id.fabTop)
    FloatingActionButton fabTop;
    @BindView(R.id.fabModel)
    FloatingActionButton fabModel;
    @BindView(R.id.fabAdd)
    FloatingActionButton fabAdd;

    private Album album;
    private RecyclerHelper recyclerHelper;
    private int page;

    public static void goActivity(Activity from, long albumId) {
        Intent intent = new Intent(from, PictureListActivity.class);
        intent.putExtra("from", BaseActivity.ACT_LIST_FROM_BROWSE);
        intent.putExtra("albumId", albumId);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    public static void goActivity(Activity from, Album album) {
        Intent intent = new Intent(from, PictureListActivity.class);
        intent.putExtra("from", BaseActivity.ACT_LIST_FROM_BROWSE);
        intent.putExtra("album", album);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    public static void goActivityBySelect(Activity from, Album album) {
        Intent intent = new Intent(from, PictureListActivity.class);
        intent.putExtra("from", BaseActivity.ACT_LIST_FROM_SELECT);
        intent.putExtra("album", album);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_picture_list;
    }

    @Override
    protected void initView(Intent intent, Bundle state) {
        ViewHelper.initTopBar(mActivity, tb, "", true);
        // init
        album = intent.getParcelableExtra("album");
        if (album == null) {
            refreshAlbum(intent.getLongExtra("albumId", 0));
        }
        // recycler
        recyclerHelper = new RecyclerHelper(rv)
                .initLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL))
                .initRefresh(srl, false)
                .initAdapter(new PictureAdapter(mActivity))
                .viewEmpty(mActivity, R.layout.list_empty_grey, true, true)
                .viewLoadMore(new RecyclerHelper.MoreGreyView())
                .setAdapter()
                .listenerRefresh(() -> getPictureList(false))
                .listenerMore(currentCount -> getPictureList(true))
                .listenerClick(new OnItemLongClickListener() {
                    @Override
                    public void onSimpleItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                        PictureAdapter pictureAdapter = (PictureAdapter) adapter;
                        if (isFromSelectPicture()) {
                            // 照片选择
                            pictureAdapter.selectPicture(position);
                        } else {
                            // 照片操作
                            pictureAdapter.showOperation(position);
                        }
                    }
                })
                .listenerClick(new OnItemChildClickListener() {
                    @Override
                    public void onSimpleItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                        PictureAdapter pictureAdapter = (PictureAdapter) adapter;
                        switch (view.getId()) {
                            case R.id.tvAddress: // 地址
                                pictureAdapter.onLocationClick(position);
                                break;
                            case R.id.tvModifyHorizontal: // 修改
                            case R.id.tvModifyVertical:
                                pictureAdapter.goEdit(position, album);
                                break;
                            case R.id.tvDeleteHorizontal: // 删除
                            case R.id.tvDeleteVertical:
                                pictureAdapter.showDeleteDialog(position);
                                break;
                            case R.id.tvCancelHorizontal: // 取消
                            case R.id.tvCancelVertical:
                                pictureAdapter.hideOperation();
                                break;
                        }
                    }
                });
        // recycler
        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (recyclerHelper == null) return;
                PictureAdapter adapter = recyclerHelper.getAdapter();
                if (adapter != null) adapter.hideOperation();
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    // 停止滑动
                    FrescoHelper.imageResume();
                } else if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    // 手指触碰
                    FrescoHelper.imagePause();
                } else if (newState == RecyclerView.SCROLL_STATE_SETTLING) {
                    // 惯性滑动
                    FrescoHelper.imagePause();
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
        // view
        refreshAlbumView();
    }

    @Override
    protected void initData(Intent intent, Bundle state) {
        page = 0;
        // event
        Observable<Album> obAlbumRefresh = RxBus.register(RxBus.EVENT_ALBUM_DETAIL_REFRESH, a -> {
            if (album == null) return;
            refreshAlbum(album.getId());
        });
        pushBus(RxBus.EVENT_ALBUM_DETAIL_REFRESH, obAlbumRefresh);
        Observable<List<Picture>> obListRefresh = RxBus.register(RxBus.EVENT_PICTURE_LIST_REFRESH, pictureList -> {
            if (recyclerHelper == null) return;
            recyclerHelper.dataRefresh();
        });
        pushBus(RxBus.EVENT_PICTURE_LIST_REFRESH, obListRefresh);
        Observable<Picture> obListItemRefresh = RxBus.register(RxBus.EVENT_PICTURE_LIST_ITEM_REFRESH, picture -> {
            if (recyclerHelper == null) return;
            ListHelper.refreshObjInAdapter(recyclerHelper.getAdapter(), picture);
        });
        pushBus(RxBus.EVENT_PICTURE_LIST_ITEM_REFRESH, obListItemRefresh);
        Observable<Picture> obListItemDelete = RxBus.register(RxBus.EVENT_PICTURE_LIST_ITEM_DELETE, picture -> {
            if (recyclerHelper == null) return;
            ListHelper.removeObjInAdapter(recyclerHelper.getAdapter(), picture);
            if (album == null) return;
            album.setPictureCount(album.getPictureCount() - 1);
            refreshAlbumView();
        });
        pushBus(RxBus.EVENT_PICTURE_LIST_ITEM_DELETE, obListItemDelete);
        // refresh
        recyclerHelper.dataRefresh();
    }

    @Override
    protected void onFinish(Bundle state) {
        RecyclerHelper.release(recyclerHelper);
    }

    @OnClick({R.id.fabTop, R.id.fabModel, R.id.fabAdd})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.fabTop: // 置顶
                if (rv == null) return;
                rv.smoothScrollToPosition(0);
                break;
            case R.id.fabModel: // 模式
                if (recyclerHelper == null || recyclerHelper.getAdapter() == null) return;
                PictureAdapter adapter = recyclerHelper.getAdapter();
                adapter.toggleModel();
                break;
            case R.id.fabAdd: // 添加
                PictureEditActivity.goActivity(mActivity, album);
                break;
        }
    }

    private boolean isFromSelectPicture() {
        return getIntent().getIntExtra("from", BaseActivity.ACT_LIST_FROM_BROWSE) == BaseActivity.ACT_LIST_FROM_SELECT;
    }

    private void refreshAlbumView() {
        if (album == null) return;
        // data
        String cover = album.getCover();
        String title = album.getTitle();
        String count = String.format(Locale.getDefault(), mActivity.getString(R.string.holder_paper), album.getPictureCount() < 0 ? 0 : album.getPictureCount());
        String createAt = TimeHelper.getTimeShowLocal_HM_MD_YMD_ByGo(album.getCreateAt());
        String createShow = String.format(Locale.getDefault(), getString(R.string.create_at_colon_space_holder), createAt);
        String updateAt = TimeHelper.getTimeShowLocal_HM_MD_YMD_ByGo(album.getUpdateAt());
        String updateShow = String.format(Locale.getDefault(), getString(R.string.update_at_colon_space_holder), updateAt);
        // view
        if (StringUtils.isEmpty(cover)) {
            ivCover.setVisibility(View.GONE);
        } else {
            ivCover.setVisibility(View.VISIBLE);
            int width = ScreenUtils.getScreenRealWidth(mActivity);
            int height = ConvertUtils.dp2px(250);
            ivCover.setWidthAndHeight(width, height);
            ivCover.setData(cover);
        }
        ctl.setTitle(title);
        tvPictureCount.setText(count);
        tvCreateAt.setText(createShow);
        tvUpdateAt.setText(updateShow);
    }

    private void getPictureList(final boolean more) {
        if (album == null) {
            srl.setRefreshing(false);
            return;
        }
        page = more ? page + 1 : 0;
        // api
        Call<Result> api = new RetrofitHelper().call(API.class).notePictureListGet(album.getId(), page);
        RetrofitHelper.enqueue(api, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                if (recyclerHelper == null) return;
                List<Picture> pictureList = data.getPictureList();
                recyclerHelper.dataOk(data.getShow(), pictureList, more);
                // 刷新本地资源
                List<String> ossKeyList = ListHelper.getOssKeyListByPicture(pictureList);
                OssResHelper.refreshResWithDelExpire(OssResHelper.TYPE_NOTE_PICTURE, ossKeyList);
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                if (recyclerHelper == null) return;
                recyclerHelper.dataFail(more, message);
            }
        });
        pushApi(api);
    }

    private void refreshAlbum(long albumId) {
        Call<Result> api = new RetrofitHelper().call(API.class).noteAlbumGet(albumId);
        RetrofitHelper.enqueue(api, getLoading(true), new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                if (recyclerHelper == null) return;
                album = data.getAlbum();
                if (album == null) return;
                refreshAlbumView();
                recyclerHelper.dataRefresh();
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                if (recyclerHelper == null) return;
                recyclerHelper.dataFail(false, message);
            }
        });
        pushApi(api);
    }

}
