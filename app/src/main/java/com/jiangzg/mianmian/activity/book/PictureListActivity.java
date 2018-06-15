package com.jiangzg.mianmian.activity.book;

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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.chad.library.adapter.base.listener.OnItemLongClickListener;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.base.view.ToastUtils;
import com.jiangzg.mianmian.R;
import com.jiangzg.mianmian.activity.settings.HelpActivity;
import com.jiangzg.mianmian.adapter.PictureAdapter;
import com.jiangzg.mianmian.base.BaseActivity;
import com.jiangzg.mianmian.domain.Album;
import com.jiangzg.mianmian.domain.Help;
import com.jiangzg.mianmian.domain.Picture;
import com.jiangzg.mianmian.domain.Result;
import com.jiangzg.mianmian.helper.API;
import com.jiangzg.mianmian.helper.ConsHelper;
import com.jiangzg.mianmian.helper.ConvertHelper;
import com.jiangzg.mianmian.helper.ListHelper;
import com.jiangzg.mianmian.helper.OssResHelper;
import com.jiangzg.mianmian.helper.RecyclerHelper;
import com.jiangzg.mianmian.helper.RetrofitHelper;
import com.jiangzg.mianmian.helper.RxBus;
import com.jiangzg.mianmian.helper.SPHelper;
import com.jiangzg.mianmian.helper.ViewHelper;
import com.jiangzg.mianmian.view.GImageView;
import com.jiangzg.mianmian.view.GSwipeRefreshLayout;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;
import rx.Observable;
import rx.functions.Action1;

public class PictureListActivity extends BaseActivity<PictureListActivity> {

    private static boolean isFirstOpen = true;

    private static final int TYPE_BROWSE = 0;
    private static final int TYPE_SELECT = 1;

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
    GImageView ivCover;
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
        intent.putExtra("type", TYPE_BROWSE);
        intent.putExtra("albumId", albumId);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    public static void goActivity(Activity from, Album album) {
        Intent intent = new Intent(from, PictureListActivity.class);
        intent.putExtra("type", TYPE_BROWSE);
        intent.putExtra("album", album);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    public static void goActivityBySelect(Activity from, Album album) {
        Intent intent = new Intent(from, PictureListActivity.class);
        intent.putExtra("type", TYPE_SELECT);
        intent.putExtra("album", album);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        page = 0;
        return R.layout.activity_picture_list;
    }

    @Override
    protected void initView(Bundle state) {
        ViewHelper.initTopBar(mActivity, tb, "", true);
        // recycler
        recyclerHelper = new RecyclerHelper(mActivity)
                .initRecycler(rv)
                .initLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL))
                .initRefresh(srl, false)
                .initAdapter(new PictureAdapter(mActivity))
                .viewEmpty(R.layout.list_empty_grey, true, true)
                .viewLoadMore(new RecyclerHelper.MoreGreyView())
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
                        if (isSelectPicture()) {
                            // 照片选择
                            pictureAdapter.selectPicture(position);
                        } else {
                            // 照片浏览
                            pictureAdapter.showOperation(position);
                        }
                    }
                })
                .listenerClick(new OnItemChildClickListener() {
                    @Override
                    public void onSimpleItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                        PictureAdapter pictureAdapter = (PictureAdapter) adapter;
                        switch (view.getId()) {
                            case R.id.tvLocation: // 地址
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
                PictureAdapter adapter = recyclerHelper.getAdapter();
                if (adapter != null) adapter.hideOperation();
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    @Override
    protected void initData(Bundle state) {
        album = getIntent().getParcelableExtra("album");
        refreshAlbumView();
        obListRefresh = RxBus.register(ConsHelper.EVENT_PICTURE_LIST_REFRESH, new Action1<List<Picture>>() {
            @Override
            public void call(List<Picture> pictures) {
                recyclerHelper.dataRefresh();
            }
        });
        obListItemRefresh = RxBus.register(ConsHelper.EVENT_PICTURE_LIST_ITEM_REFRESH, new Action1<Picture>() {
            @Override
            public void call(Picture picture) {
                ListHelper.refreshIndexInAdapter(recyclerHelper.getAdapter(), picture);
            }
        });
        obListItemDelete = RxBus.register(ConsHelper.EVENT_PICTURE_LIST_ITEM_DELETE, new Action1<Picture>() {
            @Override
            public void call(Picture picture) {
                ListHelper.removeIndexInAdapter(recyclerHelper.getAdapter(), picture);
            }
        });
        recyclerHelper.dataRefresh();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.help, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RetrofitHelper.cancel(callPictureList);
        RetrofitHelper.cancel(callAlbum);
        // event
        RxBus.unregister(ConsHelper.EVENT_PICTURE_LIST_REFRESH, obListRefresh);
        RxBus.unregister(ConsHelper.EVENT_PICTURE_LIST_ITEM_REFRESH, obListItemRefresh);
        RxBus.unregister(ConsHelper.EVENT_PICTURE_LIST_ITEM_DELETE, obListItemDelete);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuHelp: // 帮助
                HelpActivity.goActivity(mActivity, Help.INDEX_PICTURE_LIST);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick({R.id.fabTop, R.id.fabModel, R.id.fabAdd})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.fabTop: // 置顶
                if (rv == null) return;
                rv.smoothScrollToPosition(0);
                break;
            case R.id.fabModel: // 模式
                if (recyclerHelper == null) return;
                PictureAdapter adapter = recyclerHelper.getAdapter();
                if (adapter == null) return;
                adapter.toggleModel();
                break;
            case R.id.fabAdd: // 添加
                int pictureCount = SPHelper.getLimit().getPictureCount();
                if (pictureCount > 0) {
                    PictureEditActivity.goActivity(mActivity, album);
                } else {
                    ToastUtils.show(getString(R.string.now_status_cant_upload_img));
                }
                break;
        }
    }

    private boolean isSelectPicture() {
        return getIntent().getIntExtra("type", TYPE_BROWSE) == TYPE_SELECT;
    }

    private void refreshAlbumView() {
        if (album == null) {
            getAlbum();
            return;
        }
        // data
        String cover = album.getCover();
        String title = album.getTitle();
        String createAt = ConvertHelper.getTimeShowCnSpace_HM_MD_YMD_ByGo(album.getCreateAt());
        String createShow = String.format(Locale.getDefault(), getString(R.string.create_at_colon_space_holder), createAt);
        String updateAt = ConvertHelper.getTimeShowCnSpace_HM_MD_YMD_ByGo(album.getUpdateAt());
        String updateShow = String.format(Locale.getDefault(), getString(R.string.update_at_colon_space_holder), updateAt);
        // view
        if (StringUtils.isEmpty(cover)) {
            ivCover.setVisibility(View.GONE);
        } else {
            ivCover.setVisibility(View.VISIBLE);
            ivCover.setData(cover);
        }
        ctl.setTitle(title);
        tvCreateAt.setText(createShow);
        tvUpdateAt.setText(updateShow);
    }

    private void getPictureList(final boolean more) {
        if (album == null) {
            srl.setRefreshing(false);
            getAlbum();
            return;
        }
        page = more ? page + 1 : 0;
        // api
        callPictureList = new RetrofitHelper().call(API.class).PictureListGet(album.getId(), page);
        RetrofitHelper.enqueue(callPictureList, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                recyclerHelper.viewEmptyShow(data.getShow());
                // data
                long total = data.getTotal();
                List<Picture> pictureList = data.getPictureList();
                recyclerHelper.dataOk(pictureList, total, more);
                // total
                String format = String.format(Locale.getDefault(), mActivity.getString(R.string.total_colon_space_holder_paper), total);
                tvPictureCount.setText(format);
                // 刷新本地资源
                if (isFirstOpen) {
                    isFirstOpen = false;
                    OssResHelper.refreshPictureRes(pictureList);
                }
            }

            @Override
            public void onFailure(String errMsg) {
                recyclerHelper.dataFail(more, errMsg);
            }
        });
    }

    private void getAlbum() {
        long albumId = getIntent().getLongExtra("albumId", 0);
        callAlbum = new RetrofitHelper().call(API.class).AlbumGet(albumId);
        MaterialDialog loading = getLoading(true);
        RetrofitHelper.enqueue(callAlbum, loading, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                album = data.getAlbum();
                refreshAlbumView();
                recyclerHelper.dataRefresh();
            }

            @Override
            public void onFailure(String errMsg) {
                recyclerHelper.dataFail(false, errMsg);
            }
        });
    }

}
