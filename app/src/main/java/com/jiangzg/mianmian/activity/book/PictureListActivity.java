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
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.base.view.ToastUtils;
import com.jiangzg.mianmian.R;
import com.jiangzg.mianmian.adapter.PictureAdapter;
import com.jiangzg.mianmian.base.BaseActivity;
import com.jiangzg.mianmian.domain.Album;
import com.jiangzg.mianmian.domain.Picture;
import com.jiangzg.mianmian.domain.Result;
import com.jiangzg.mianmian.helper.API;
import com.jiangzg.mianmian.helper.ConsHelper;
import com.jiangzg.mianmian.helper.ConvertHelper;
import com.jiangzg.mianmian.helper.ListHelper;
import com.jiangzg.mianmian.helper.RecyclerHelper;
import com.jiangzg.mianmian.helper.RetrofitHelper;
import com.jiangzg.mianmian.helper.RxBus;
import com.jiangzg.mianmian.helper.SPHelper;
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
    @BindView(R.id.tvTitle)
    TextView tvTitle;
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

    @BindView(R.id.fabAdd)
    FloatingActionButton fabAdd;

    private Album album;
    private RecyclerHelper recyclerHelper;
    private Call<Result> callPictureList;
    private int page;
    private Observable<List<Picture>> obListRefresh;
    private Observable<Picture> obListItemRefresh;

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

        //使用CollapsingToolbarLayout必须把title设置到CollapsingToolbarLayout上，设置到Toolbar上则不会显示
        ctl.setTitle("压缩后的标题");
        ////通过CollapsingToolbarLayout修改字体颜色
        //mCollapsingToolbarLayout.setExpandedTitleColor(Color.WHITE);//设置还没收缩时状态下字体颜色
        //mCollapsingToolbarLayout.setCollapsedTitleTextColor(Color.GREEN);//设置收缩后Toolbar上字体的颜色

        // recycler
        recyclerHelper = new RecyclerHelper(mActivity)
                .initRecycler(rv)
                .initLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL))
                .initRefresh(srl, false)
                .initAdapter(new PictureAdapter(mActivity))
                .viewEmpty(R.layout.list_empty_white, true, true) // TODO 背景是封面的虚化
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
                .listenerClick(new OnItemClickListener() {
                    @Override
                    public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
                        PictureAdapter pictureAdapter = (PictureAdapter) adapter;
                        pictureAdapter.goDetail(position);
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
        recyclerHelper.dataRefresh();
    }

    // TODO
    //@Override
    //public boolean onCreateOptionsMenu(Menu menu) {
    //    getMenuInflater().inflate(R.menu.help, menu);
    //    return super.onCreateOptionsMenu(menu);
    //}

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RetrofitHelper.cancel(callPictureList);
        // event
        RxBus.unregister(ConsHelper.EVENT_PICTURE_LIST_REFRESH, obListRefresh);
        RxBus.unregister(ConsHelper.EVENT_PICTURE_LIST_ITEM_REFRESH, obListItemRefresh);
    }

    // TODO
    //@Override
    //public boolean onOptionsItemSelected(MenuItem item) {
    //    switch (item.getItemId()) {
    //        case R.id.menuHelp: // 帮助
    //            HelpActivity.goActivity(mActivity, Help.TYPE_DIARY_LIST);
    //            return true;
    //    }
    //    return super.onOptionsItemSelected(item);
    //}

    @OnClick({R.id.fabAdd})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.fabAdd: // 添加 TODO 位置
                int limitImages = SPHelper.getLimit().getPictureLimitCount();
                if (limitImages > 0) {
                    PictureEditActivity.goActivity(mActivity, album);
                } else {
                    ToastUtils.show(getString(R.string.now_status_cant_upload_img));
                }
                break;
        }
    }

    private void refreshAlbumView() {
        if (album == null) return;
        // data
        String cover = album.getCover();
        String title = album.getTitle();
        String createAt = ConvertHelper.getTimeShowCnSpace_HM_MD_YMD_ByGo(album.getCreateAt());
        String createShow = String.format(Locale.getDefault(), getString(R.string.create_at_colon_space_holder), createAt);
        String updateAt = ConvertHelper.getTimeShowCnSpace_HM_MD_YMD_ByGo(album.getUpdateAt());
        String updateShow = String.format(Locale.getDefault(), getString(R.string.update_at_colon_space_holder), updateAt);
        // view
        ivCover.setDataOss(cover);
        tvTitle.setText(title);
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
        callPictureList = new RetrofitHelper().call(API.class).PictureListGet(album.getId(), page);
        RetrofitHelper.enqueue(callPictureList, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                recyclerHelper.viewEmptyShow(data.getShow());
                long total = data.getTotal();
                List<Picture> pictureList = data.getPictureList();
                recyclerHelper.dataOk(pictureList, total, more);
            }

            @Override
            public void onFailure(String errMsg) {
                recyclerHelper.dataFail(more, errMsg);
            }
        });
    }

    private void getAlbum() {
        // TODO loading
    }

}
