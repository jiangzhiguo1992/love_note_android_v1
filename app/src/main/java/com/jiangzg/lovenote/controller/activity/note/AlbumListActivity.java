package com.jiangzg.lovenote.controller.activity.note;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.controller.activity.base.BaseActivity;
import com.jiangzg.lovenote.controller.adapter.note.AlbumAdapter;
import com.jiangzg.lovenote.helper.common.ListHelper;
import com.jiangzg.lovenote.helper.common.RxBus;
import com.jiangzg.lovenote.helper.system.RetrofitHelper;
import com.jiangzg.lovenote.helper.view.RecyclerHelper;
import com.jiangzg.lovenote.helper.view.ViewHelper;
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
    private int page = 0;

    public static void goActivity(Activity from) {
        Intent intent = new Intent(from, AlbumListActivity.class);
        intent.putExtra("from", BaseActivity.ACT_LIST_FROM_BROWSE);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    public static void goActivity(Fragment from) {
        Intent intent = new Intent(from.getActivity(), AlbumListActivity.class);
        intent.putExtra("from", BaseActivity.ACT_LIST_FROM_BROWSE);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    public static void goActivityBySelect(Activity from) {
        Intent intent = new Intent(from, AlbumListActivity.class);
        intent.putExtra("from", BaseActivity.ACT_LIST_FROM_SELECT);
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
        if (isFromSelect()) {
            title = getString(R.string.please_select_album);
        } else {
            title = getString(R.string.album);
        }
        ViewHelper.initTopBar(mActivity, tb, title, true);
        // recycler
        recyclerHelper = new RecyclerHelper(rv)
                .initLayoutManager(new LinearLayoutManager(mActivity))
                .initRefresh(srl, true)
                .initAdapter(new AlbumAdapter(mActivity))
                .viewEmpty(mActivity, R.layout.list_empty_grey, true, true)
                .viewLoadMore(new RecyclerHelper.MoreGreyView())
                .setAdapter()
                .listenerRefresh(() -> getData(false))
                .listenerMore(currentCount -> getData(true))
                .listenerClick(new OnItemClickListener() {
                    @Override
                    public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
                        AlbumAdapter albumAdapter = (AlbumAdapter) adapter;
                        if (isFromSelect()) {
                            // 相册选择
                            albumAdapter.selectAlbum(position);
                        } else {
                            // 照片列表浏览
                            albumAdapter.goAlbumDetail(position);
                        }
                    }
                });
    }

    @Override
    protected void initData(Intent intent, Bundle state) {
        // event
        Observable<List<Album>> busListRefresh = RxBus.register(RxBus.EVENT_ALBUM_LIST_REFRESH, albumList -> {
            if (recyclerHelper == null) return;
            recyclerHelper.dataRefresh();
        });
        pushBus(RxBus.EVENT_ALBUM_LIST_REFRESH, busListRefresh);
        Observable<Album> busListItemRefresh = RxBus.register(RxBus.EVENT_ALBUM_LIST_ITEM_REFRESH, album -> {
            if (recyclerHelper == null) return;
            ListHelper.refreshObjInAdapter(recyclerHelper.getAdapter(), album);
        });
        pushBus(RxBus.EVENT_ALBUM_LIST_ITEM_REFRESH, busListItemRefresh);
        Observable<Album> busListItemDelete = RxBus.register(RxBus.EVENT_ALBUM_LIST_ITEM_DELETE, album -> {
            if (recyclerHelper == null) return;
            ListHelper.removeObjInAdapter(recyclerHelper.getAdapter(), album);
        });
        pushBus(RxBus.EVENT_ALBUM_LIST_ITEM_DELETE, busListItemDelete);
        Observable<Picture> busPictureSelect = RxBus.register(RxBus.EVENT_PICTURE_SELECT, picture -> {
            if (!mActivity.isFinishing()) {
                mActivity.finish();
            }
        });
        pushBus(RxBus.EVENT_PICTURE_SELECT, busPictureSelect);
        // refresh
        recyclerHelper.dataRefresh();
    }

    @Override
    protected void onFinish(Bundle state) {
        RecyclerHelper.release(recyclerHelper);
    }

    @OnClick({R.id.fabAdd})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.fabAdd: // 添加
                AlbumEditActivity.goActivity(mActivity);
                break;
        }
    }

    private boolean isFromSelect() {
        return getIntent().getIntExtra("from", BaseActivity.ACT_LIST_FROM_BROWSE) == BaseActivity.ACT_LIST_FROM_SELECT;
    }

    private void getData(final boolean more) {
        page = more ? page + 1 : 0;
        // api
        Call<Result> api = new RetrofitHelper().call(API.class).noteAlbumListGet(page);
        RetrofitHelper.enqueue(api, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                if (recyclerHelper == null) return;
                List<Album> albumList = data.getAlbumList();
                recyclerHelper.dataOk(data.getShow(), albumList, more);
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                if (recyclerHelper == null) return;
                recyclerHelper.dataFail(more, message);
            }
        });
        pushApi(api);
    }

}
