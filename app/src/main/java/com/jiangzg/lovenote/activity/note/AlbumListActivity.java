package com.jiangzg.lovenote.activity.note;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
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
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.adapter.AlbumAdapter;
import com.jiangzg.lovenote.base.BaseActivity;
import com.jiangzg.lovenote.helper.ConsHelper;
import com.jiangzg.lovenote.helper.ListHelper;
import com.jiangzg.lovenote.helper.OssResHelper;
import com.jiangzg.lovenote.helper.RecyclerHelper;
import com.jiangzg.lovenote.helper.RetrofitHelper;
import com.jiangzg.lovenote.helper.RxBus;
import com.jiangzg.lovenote.helper.ViewHelper;
import com.jiangzg.lovenote.model.api.API;
import com.jiangzg.lovenote.model.api.Result;
import com.jiangzg.lovenote.model.entity.Album;
import com.jiangzg.lovenote.model.entity.Picture;
import com.jiangzg.lovenote.view.GSwipeRefreshLayout;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;
import rx.Observable;

public class AlbumListActivity extends BaseActivity<AlbumListActivity> {

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
        intent.putExtra("from", ConsHelper.ACT_LIST_FROM_BROWSE);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    public static void goActivity(Fragment from) {
        Intent intent = new Intent(from.getActivity(), AlbumListActivity.class);
        intent.putExtra("from", ConsHelper.ACT_LIST_FROM_BROWSE);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    public static void goActivityBySelectAlbum(Activity from) {
        Intent intent = new Intent(from, AlbumListActivity.class);
        intent.putExtra("from", ConsHelper.ACT_LIST_FROM_SELECT_ALBUM);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    public static void goActivityBySelectPicture(Activity from) {
        Intent intent = new Intent(from, AlbumListActivity.class);
        intent.putExtra("from", ConsHelper.ACT_LIST_FROM_SELECT_PICTURE);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_album_list;
    }

    @Override
    protected void initView(Intent intent, Bundle state) {
        String title;
        if (isFromSelectAlbum() || isFromSelectPicture()) {
            title = getString(R.string.please_select_album);
        } else {
            title = getString(R.string.album);
        }
        ViewHelper.initTopBar(mActivity, tb, title, true);
        // recycler
        recyclerHelper = new RecyclerHelper(rv)
                .initLayoutManager(new LinearLayoutManager(mActivity))
                .initRefresh(srl, false)
                .initAdapter(new AlbumAdapter(mActivity))
                .viewEmpty(mActivity, R.layout.list_empty_white, true, true)
                .viewLoadMore(new RecyclerHelper.MoreGreyView())
                .setAdapter()
                .listenerRefresh(() -> getData(false))
                .listenerMore(currentCount -> getData(true))
                .listenerClick(new OnItemClickListener() {
                    @Override
                    public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
                        AlbumAdapter albumAdapter = (AlbumAdapter) adapter;
                        if (isFromSelectAlbum()) {
                            // 相册选择
                            albumAdapter.selectAlbum(position);
                        } else if (isFromSelectPicture()) {
                            // 照片列表选择
                            albumAdapter.selectPicture(position);
                        } else {
                            // 照片列表浏览
                            albumAdapter.goAlbumDetail(position);
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
                if (recyclerHelper == null) return;
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
    protected void initData(Intent intent, Bundle state) {
        page = 0;
        // event
        obListRefresh = RxBus.register(ConsHelper.EVENT_ALBUM_LIST_REFRESH, albumList -> {
            if (recyclerHelper == null) return;
            recyclerHelper.dataRefresh();
        });
        obListItemRefresh = RxBus.register(ConsHelper.EVENT_ALBUM_LIST_ITEM_REFRESH, album -> {
            if (recyclerHelper == null) return;
            ListHelper.refreshObjInAdapter(recyclerHelper.getAdapter(), album);
        });
        obPictureSelect = RxBus.register(ConsHelper.EVENT_PICTURE_SELECT, picture -> {
            if (!mActivity.isFinishing()) {
                mActivity.finish();
            }
        });
        // refresh
        recyclerHelper.dataRefresh();
    }

    @Override
    protected void onFinish(Bundle state) {
        RetrofitHelper.cancel(call);
        RxBus.unregister(ConsHelper.EVENT_ALBUM_LIST_REFRESH, obListRefresh);
        RxBus.unregister(ConsHelper.EVENT_ALBUM_LIST_ITEM_REFRESH, obListItemRefresh);
        RxBus.unregister(ConsHelper.EVENT_PICTURE_SELECT, obPictureSelect);
        RecyclerHelper.release(recyclerHelper);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.add, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuAdd: // 添加
                AlbumEditActivity.goActivity(mActivity);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick({R.id.fabAdd})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.fabAdd: // 添加
                AlbumEditActivity.goActivity(mActivity);
                break;
        }
    }

    private boolean isFromSelectAlbum() {
        return getIntent().getIntExtra("from", ConsHelper.ACT_LIST_FROM_BROWSE) == ConsHelper.ACT_LIST_FROM_SELECT_ALBUM;
    }

    private boolean isFromSelectPicture() {
        return getIntent().getIntExtra("from", ConsHelper.ACT_LIST_FROM_BROWSE) == ConsHelper.ACT_LIST_FROM_SELECT_PICTURE;
    }

    private void getData(final boolean more) {
        page = more ? page + 1 : 0;
        // api
        call = new RetrofitHelper().call(API.class).noteAlbumListGet(page);
        RetrofitHelper.enqueue(call, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                if (recyclerHelper == null) return;
                recyclerHelper.viewEmptyShow(data.getShow());
                List<Album> albumList = data.getAlbumList();
                recyclerHelper.dataOk(albumList, more);
                // 刷新本地资源
                List<String> ossKeyList = ListHelper.getOssKeyListByAlbum(albumList);
                OssResHelper.refreshResWithDelExpire(OssResHelper.TYPE_NOTE_ALBUM, ossKeyList);
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                if (recyclerHelper == null) return;
                recyclerHelper.dataFail(more, message);
            }
        });
    }

}
