package com.jiangzg.mianmian.activity.book;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.listener.OnItemLongClickListener;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.base.view.ToastUtils;
import com.jiangzg.mianmian.R;
import com.jiangzg.mianmian.activity.settings.HelpActivity;
import com.jiangzg.mianmian.adapter.AlbumAdapter;
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
import com.jiangzg.mianmian.view.GSwipeRefreshLayout;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;
import rx.Observable;
import rx.functions.Action1;

public class AlbumListActivity extends BaseActivity<AlbumListActivity> {

    private static final int TYPE_BROWSE = 0;
    private static final int TYPE_SELECT_ALBUM = 1;
    private static final int TYPE_SELECT_PICTURE = 2;

    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.srl)
    GSwipeRefreshLayout srl;
    @BindView(R.id.rv)
    RecyclerView rv;
    @BindView(R.id.fabAdd)
    FloatingActionButton fabAdd;

    private RecyclerHelper recyclerHelper;
    private Observable<List<Album>> obListRefresh;
    private Observable<Album> obListItemRefresh;
    private Observable<Picture> obPictureSelect;
    private Call<Result> call;
    private int page;

    public static void goActivity(Activity from) {
        Intent intent = new Intent(from, AlbumListActivity.class);
        intent.putExtra("type", TYPE_BROWSE);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    public static void goActivityBySelectAlbum(Activity from) {
        Intent intent = new Intent(from, AlbumListActivity.class);
        intent.putExtra("type", TYPE_SELECT_ALBUM);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    public static void goActivityBySelectPicture(Activity from) {
        Intent intent = new Intent(from, AlbumListActivity.class);
        intent.putExtra("type", TYPE_SELECT_PICTURE);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        page = 0;
        return R.layout.activity_album_list;
    }

    @Override
    protected void initView(Bundle state) {
        String title;
        if (isSelectAlbum() || isSelectPicture()) {
            title = getString(R.string.please_select_album);
        } else {
            title = getString(R.string.album);
        }
        ViewHelper.initTopBar(mActivity, tb, title, true);
        // recycler
        recyclerHelper = new RecyclerHelper(mActivity)
                .initRecycler(rv)
                .initLayoutManager(new LinearLayoutManager(mActivity))
                .initRefresh(srl, false)
                .initAdapter(new AlbumAdapter(mActivity))
                .viewEmpty(R.layout.list_empty_white, true, true)
                .viewLoadMore(new RecyclerHelper.MoreGreyView())
                .listenerRefresh(new RecyclerHelper.RefreshListener() {
                    @Override
                    public void onRefresh() {
                        getData(false);
                    }
                })
                .listenerMore(new RecyclerHelper.MoreListener() {
                    @Override
                    public void onMore(int currentCount) {
                        getData(true);
                    }
                })
                .listenerClick(new OnItemClickListener() {
                    @Override
                    public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
                        AlbumAdapter albumAdapter = (AlbumAdapter) adapter;
                        if (isSelectAlbum()) {
                            // 相册选择
                            albumAdapter.selectAlbum(position);
                        } else if (isSelectPicture()) {
                            // 照片列表选择
                            albumAdapter.selectPicture(position);
                        } else {
                            // 照片列表浏览
                            albumAdapter.goDetail(position);
                        }
                    }
                })
                .listenerClick(new OnItemLongClickListener() {
                    @Override
                    public void onSimpleItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                        AlbumAdapter albumAdapter = (AlbumAdapter) adapter;
                        albumAdapter.showOperation(position);
                    }
                })
                .listenerClick(new OnItemChildClickListener() {
                    @Override
                    public void onSimpleItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                        AlbumAdapter albumAdapter = (AlbumAdapter) adapter;
                        switch (view.getId()) {
                            case R.id.tvModify: // 修改
                                albumAdapter.goEdit(position);
                                break;
                            case R.id.tvDelete: // 删除
                                albumAdapter.showDeleteDialog(position);
                                break;
                            case R.id.tvCancel: // 取消
                                albumAdapter.hideOperation();
                                break;
                        }
                    }
                });
        // recycler
        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                AlbumAdapter adapter = recyclerHelper.getAdapter();
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
        obListRefresh = RxBus.register(ConsHelper.EVENT_ALBUM_LIST_REFRESH, new Action1<List<Album>>() {
            @Override
            public void call(List<Album> albumList) {
                recyclerHelper.dataRefresh();
            }
        });
        obListItemRefresh = RxBus.register(ConsHelper.EVENT_ALBUM_LIST_ITEM_REFRESH, new Action1<Album>() {
            @Override
            public void call(Album album) {
                ListHelper.refreshIndexInAdapter(recyclerHelper.getAdapter(), album);
            }
        });
        obPictureSelect = RxBus.register(ConsHelper.EVENT_PICTURE_SELECT, new Action1<Picture>() {
            @Override
            public void call(Picture picture) {
                mActivity.finish();
            }
        });
        recyclerHelper.dataRefresh();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!isSelectAlbum() && !isSelectPicture()) {
            getMenuInflater().inflate(R.menu.help, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RetrofitHelper.cancel(call);
        RxBus.unregister(ConsHelper.EVENT_ALBUM_LIST_REFRESH, obListRefresh);
        RxBus.unregister(ConsHelper.EVENT_ALBUM_LIST_ITEM_REFRESH, obListItemRefresh);
        RxBus.unregister(ConsHelper.EVENT_PICTURE_SELECT, obPictureSelect);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuHelp: // 帮助
                HelpActivity.goActivity(mActivity, Help.INDEX_ALBUM_LIST);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick({R.id.fabAdd})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.fabAdd: // 添加
                albumAdd();
                break;
        }
    }

    private boolean isSelectAlbum() {
        return getIntent().getIntExtra("type", TYPE_BROWSE) == TYPE_SELECT_ALBUM;
    }

    private boolean isSelectPicture() {
        return getIntent().getIntExtra("type", TYPE_BROWSE) == TYPE_SELECT_PICTURE;
    }

    private void getData(final boolean more) {
        page = more ? page + 1 : 0;
        // api
        call = new RetrofitHelper().call(API.class).AlbumListGet(page);
        RetrofitHelper.enqueue(call, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                recyclerHelper.viewEmptyShow(data.getShow());
                long total = data.getTotal();
                List<Album> albumList = data.getAlbumList();
                recyclerHelper.dataOk(albumList, total, more);
                // 刷新本地资源
                List<String> ossKeyList = ConvertHelper.getOssKeyListByAlbum(albumList);
                OssResHelper.refreshResWithDelExpire(OssResHelper.TYPE_BOOK_ALBUM, ossKeyList);
            }

            @Override
            public void onFailure(String errMsg) {
                recyclerHelper.dataFail(more, errMsg);
            }
        });
    }

    private void albumAdd() {
        if (recyclerHelper == null || recyclerHelper.getAdapter() == null) return;
        BaseQuickAdapter adapter = recyclerHelper.getAdapter();
        int totalCount = SPHelper.getVipLimit().getAlbumTotalCount();
        if (adapter.getData().size() >= totalCount) {
            String toast;
            if (totalCount <= 0) {
                toast = getString(R.string.now_status_cant_upload_img);
            } else {
                String string = getString(R.string.now_just_upload_holder_album);
                toast = String.format(Locale.getDefault(), string, totalCount);
            }
            ToastUtils.show(toast);
            return;
        }
        AlbumEditActivity.goActivity(mActivity);
    }

}
