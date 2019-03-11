package com.jiangzg.lovenote.controller.activity.note;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
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
import com.jiangzg.base.common.ConvertUtils;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.base.view.ScreenUtils;
import com.jiangzg.base.view.ToastUtils;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.controller.activity.base.BaseActivity;
import com.jiangzg.lovenote.controller.adapter.note.PictureSectionAdapter;
import com.jiangzg.lovenote.helper.common.RxBus;
import com.jiangzg.lovenote.helper.common.TimeHelper;
import com.jiangzg.lovenote.helper.media.FrescoHelper;
import com.jiangzg.lovenote.helper.system.RetrofitHelper;
import com.jiangzg.lovenote.helper.view.DialogHelper;
import com.jiangzg.lovenote.helper.view.RecyclerHelper;
import com.jiangzg.lovenote.helper.view.ViewHelper;
import com.jiangzg.lovenote.model.api.API;
import com.jiangzg.lovenote.model.api.Result;
import com.jiangzg.lovenote.model.engine.PictureSection;
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
    @BindView(R.id.fabModel)
    FloatingActionButton fabModel;
    @BindView(R.id.fabAdd)
    FloatingActionButton fabAdd;

    private Album album;
    private RecyclerHelper recyclerHelper;
    private int page = 0;

    public static void goActivity(Context from, long albumId) {
        Intent intent = new Intent(from, PictureListActivity.class);
        intent.putExtra("from", BaseActivity.ACT_LIST_FROM_BROWSE);
        intent.putExtra("albumId", albumId);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

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
                .initLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL))
                .initRefresh(srl, true)
                //.initAdapter(new PictureAdapter(mActivity))
                .initAdapter(new PictureSectionAdapter(mActivity))
                .viewEmpty(mActivity, R.layout.list_empty_grey, true, true)
                .viewLoadMore(new RecyclerHelper.MoreGreyView())
                .viewAnim(BaseQuickAdapter.SCALEIN)
                .setAdapter()
                .listenerRefresh(() -> getPictureList(false))
                .listenerMore(currentCount -> getPictureList(true))
                .listenerClick(new OnItemLongClickListener() {
                    @Override
                    public void onSimpleItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                        PictureSectionAdapter pictureAdapter = (PictureSectionAdapter) adapter;
                        pictureAdapter.showDeleteDialog(position);
                    }
                })
                .listenerClick(new OnItemChildClickListener() {
                    @Override
                    public void onSimpleItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                        PictureSectionAdapter pictureAdapter = (PictureSectionAdapter) adapter;
                        switch (view.getId()) {
                            case R.id.tvAddress: // 地址
                                pictureAdapter.onLocationClick(position);
                                break;
                            //case R.id.tvModifyHorizontal: // 修改
                            //case R.id.tvModifyVertical:
                            //    pictureAdapter.goEdit(position, album);
                            //    break;
                            //case R.id.tvDeleteHorizontal: // 删除
                            //case R.id.tvDeleteVertical:
                            //    pictureAdapter.showDeleteDialog(position);
                            //    break;
                            //case R.id.tvCancelHorizontal: // 取消
                            //case R.id.tvCancelVertical:
                            //    pictureAdapter.hideOperation();
                            //    break;
                        }
                    }
                });
        // recycler
        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (recyclerHelper == null) return;
                //PictureAdapter adapter = recyclerHelper.getAdapter();
                //if (adapter != null) adapter.hideOperation();
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
            // 老版的，需要用的时候记得修改
            //if (recyclerHelper == null) return;
            //ListHelper.refreshObjInAdapter(recyclerHelper.getAdapter(), picture);
        });
        pushBus(RxBus.EVENT_PICTURE_LIST_ITEM_REFRESH, obListItemRefresh);
        @SuppressLint("Range")
        Observable<Picture> obListItemDelete = RxBus.register(RxBus.EVENT_PICTURE_LIST_ITEM_DELETE, picture -> {
            if (album != null) {
                album.setPictureCount(album.getPictureCount() - 1);
                refreshAlbumView();
            }
            if (recyclerHelper == null) return;
            PictureSectionAdapter adapter = recyclerHelper.getAdapter();
            if (adapter == null) return;
            List<PictureSection> dataList = adapter.getData();
            if (dataList.size() <= 0) return;
            if (picture == null || picture.getId() == 0) return;
            int index = -1;
            for (int i = 0; i < dataList.size(); i++) {
                PictureSection ps = dataList.get(i);
                if (ps == null || ps.isHeader) continue;
                Picture p = ps.t;
                if (p == null) continue;
                if (p.getId() == picture.getId()) {
                    // 找到了
                    index = i;
                }
            }
            if (index < 0 || index >= dataList.size()) return;
            adapter.remove(index);
            rv.scrollBy(0, 0); // 必须要滚动一下才行
        });
        pushBus(RxBus.EVENT_PICTURE_LIST_ITEM_DELETE, obListItemDelete);
        // refresh
        recyclerHelper.dataRefresh();
    }

    @Override
    protected void onFinish(Bundle state) {
        RecyclerHelper.release(recyclerHelper);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.del_edit, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuEdit: // 编辑
                if (album == null) return true;
                AlbumEditActivity.goActivity(mActivity, album);
                return true;
            case R.id.menuDel: // 删除
                showDeleteDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick({R.id.fabModel, R.id.fabAdd})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.fabModel: // 模式
                if (recyclerHelper == null || recyclerHelper.getAdapter() == null) return;
                PictureSectionAdapter adapter = recyclerHelper.getAdapter();
                adapter.toggleModel();
                break;
            case R.id.fabAdd: // 添加
                PictureEditActivity.goActivity(mActivity, album);
                break;
        }
    }

    private void refreshAlbum(long albumId) {
        Call<Result> api = new RetrofitHelper().call(API.class).noteAlbumGet(albumId);
        RetrofitHelper.enqueue(api, getLoading(true), new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                if (recyclerHelper == null) return;
                if (data.getAlbum() == null) return;
                album = data.getAlbum();
                refreshAlbumView();
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                if (recyclerHelper == null) return;
                recyclerHelper.dataFail(false, message);
            }
        });
        pushApi(api);
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
                PictureSectionAdapter adapter = recyclerHelper.getAdapter();
                List<PictureSection> sectionList = adapter.getSectionList(!more, data.getPictureList());
                recyclerHelper.dataOk(data.getShow(), sectionList, more);
                rv.scrollBy(0, 0); // 必须要滚动一下才行
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                if (recyclerHelper == null) return;
                recyclerHelper.dataFail(more, message);
            }
        });
        pushApi(api);
    }

    public void showDeleteDialog() {
        if (album == null || !album.isMine()) {
            ToastUtils.show(mActivity.getString(R.string.can_operation_self_create_note));
            return;
        }
        MaterialDialog dialog = DialogHelper.getBuild(mActivity)
                .cancelable(true)
                .canceledOnTouchOutside(true)
                .title(R.string.confirm_delete_this_note)
                .positiveText(R.string.confirm)
                .onPositive((dialog1, which) -> delApi())
                .negativeText(R.string.cancel)
                .build();
        DialogHelper.showWithAnim(dialog);
    }

    private void delApi() {
        if (album == null) return;
        Call<Result> api = new RetrofitHelper().call(API.class).noteAlbumDel(album.getId());
        RetrofitHelper.enqueue(api, mActivity.getLoading(true), new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                // event
                RxBus.post(new RxBus.Event<>(RxBus.EVENT_ALBUM_LIST_ITEM_DELETE, album));
                // finish
                mActivity.finish();
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
            }
        });
        mActivity.pushApi(api);
    }

}
