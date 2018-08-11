package com.jiangzg.lovenote.activity.note;

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

import com.afollestad.materialdialogs.MaterialDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.chad.library.adapter.base.listener.OnItemLongClickListener;
import com.jiangzg.base.common.ConvertUtils;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.base.view.ScreenUtils;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.adapter.PictureAdapter;
import com.jiangzg.lovenote.base.BaseActivity;
import com.jiangzg.lovenote.domain.Album;
import com.jiangzg.lovenote.domain.Picture;
import com.jiangzg.lovenote.domain.Result;
import com.jiangzg.lovenote.helper.API;
import com.jiangzg.lovenote.helper.ConsHelper;
import com.jiangzg.lovenote.helper.ListHelper;
import com.jiangzg.lovenote.helper.OssResHelper;
import com.jiangzg.lovenote.helper.RecyclerHelper;
import com.jiangzg.lovenote.helper.RetrofitHelper;
import com.jiangzg.lovenote.helper.RxBus;
import com.jiangzg.lovenote.helper.TimeHelper;
import com.jiangzg.lovenote.helper.ViewHelper;
import com.jiangzg.lovenote.view.FrescoView;
import com.jiangzg.lovenote.view.GSwipeRefreshLayout;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;
import rx.Observable;
import rx.functions.Action1;

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
    private Call<Result> callPictureList;
    private Call<Result> callAlbum;
    private Observable<List<Picture>> obListRefresh;
    private Observable<Picture> obListItemRefresh;
    private Observable<Picture> obListItemDelete;
    private int page;

    public static void goActivity(Activity from, long albumId) {
        Intent intent = new Intent(from, PictureListActivity.class);
        intent.putExtra("from", ConsHelper.ACT_LIST_FROM_BROWSE);
        intent.putExtra("albumId", albumId);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    public static void goActivity(Activity from, Album album) {
        Intent intent = new Intent(from, PictureListActivity.class);
        intent.putExtra("from", ConsHelper.ACT_LIST_FROM_BROWSE);
        intent.putExtra("album", album);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    public static void goActivityBySelect(Activity from, Album album) {
        Intent intent = new Intent(from, PictureListActivity.class);
        intent.putExtra("from", ConsHelper.ACT_LIST_FROM_SELECT);
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
            getAlbum(intent.getLongExtra("albumId", 0));
        }
        // recycler
        recyclerHelper = new RecyclerHelper(rv)
                .initLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL))
                .initRefresh(srl, false)
                .initAdapter(new PictureAdapter(mActivity))
                .viewEmpty(mActivity, R.layout.list_empty_grey, true, true)
                .viewLoadMore(new RecyclerHelper.MoreGreyView())
                .setAdapter()
                .listenerRefresh(new RecyclerHelper.RefreshListener() {
                    @Override
                    public void onRefresh() {
                        getPictureList(false);
                    }
                })
                .listenerMore(new RecyclerHelper.MoreListener() {
                    @Override
                    public void onMore(int currentCount) {
                        getPictureList(true);
                    }
                })
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
        obListRefresh = RxBus.register(ConsHelper.EVENT_PICTURE_LIST_REFRESH, new Action1<List<Picture>>() {
            @Override
            public void call(List<Picture> pictureList) {
                if (recyclerHelper == null) return;
                recyclerHelper.dataRefresh();
            }
        });
        obListItemRefresh = RxBus.register(ConsHelper.EVENT_PICTURE_LIST_ITEM_REFRESH, new Action1<Picture>() {
            @Override
            public void call(Picture picture) {
                if (recyclerHelper == null) return;
                ListHelper.refreshObjInAdapter(recyclerHelper.getAdapter(), picture);
            }
        });
        obListItemDelete = RxBus.register(ConsHelper.EVENT_PICTURE_LIST_ITEM_DELETE, new Action1<Picture>() {
            @Override
            public void call(Picture picture) {
                if (recyclerHelper == null) return;
                ListHelper.removeObjInAdapter(recyclerHelper.getAdapter(), picture);
                if (album == null) return;
                album.setPictureCount(album.getPictureCount() - 1);
                refreshAlbumView();
            }
        });
        // refresh
        recyclerHelper.dataRefresh();
    }

    @Override
    protected void onFinish(Bundle state) {
        RetrofitHelper.cancel(callPictureList);
        RetrofitHelper.cancel(callAlbum);
        RxBus.unregister(ConsHelper.EVENT_PICTURE_LIST_REFRESH, obListRefresh);
        RxBus.unregister(ConsHelper.EVENT_PICTURE_LIST_ITEM_REFRESH, obListItemRefresh);
        RxBus.unregister(ConsHelper.EVENT_PICTURE_LIST_ITEM_DELETE, obListItemDelete);
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
        return getIntent().getIntExtra("from", ConsHelper.ACT_LIST_FROM_BROWSE) == ConsHelper.ACT_LIST_FROM_SELECT;
    }

    private void refreshAlbumView() {
        if (album == null) return;
        // data
        String cover = album.getCover();
        String title = album.getTitle();
        String count = String.format(Locale.getDefault(), mActivity.getString(R.string.holder_paper), album.getPictureCount() < 0 ? 0 : album.getPictureCount());
        String createAt = TimeHelper.getTimeShowCn_HM_MD_YMD_ByGo(album.getCreateAt());
        String createShow = String.format(Locale.getDefault(), getString(R.string.create_at_colon_space_holder), createAt);
        String updateAt = TimeHelper.getTimeShowCn_HM_MD_YMD_ByGo(album.getUpdateAt());
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
        callPictureList = new RetrofitHelper().call(API.class).notePictureListGet(album.getId(), page);
        RetrofitHelper.enqueue(callPictureList, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                if (recyclerHelper == null) return;
                recyclerHelper.viewEmptyShow(data.getShow());
                // data
                List<Picture> pictureList = data.getPictureList();
                recyclerHelper.dataOk(pictureList, more);
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
    }

    private void getAlbum(long albumId) {
        callAlbum = new RetrofitHelper().call(API.class).noteAlbumGet(albumId);
        MaterialDialog loading = getLoading(true);
        RetrofitHelper.enqueue(callAlbum, loading, new RetrofitHelper.CallBack() {
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
    }

}
