package com.jiangzg.lovenote.controller.activity.note;

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
import com.chad.library.adapter.base.listener.OnItemChildLongClickListener;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.controller.activity.base.BaseActivity;
import com.jiangzg.lovenote.controller.adapter.note.VideoAdapter;
import com.jiangzg.lovenote.helper.common.ListHelper;
import com.jiangzg.lovenote.helper.common.OssResHelper;
import com.jiangzg.lovenote.helper.common.RxBus;
import com.jiangzg.lovenote.helper.system.RetrofitHelper;
import com.jiangzg.lovenote.helper.view.RecyclerHelper;
import com.jiangzg.lovenote.helper.view.ViewHelper;
import com.jiangzg.lovenote.model.api.API;
import com.jiangzg.lovenote.model.api.Result;
import com.jiangzg.lovenote.model.entity.Video;
import com.jiangzg.lovenote.view.GSwipeRefreshLayout;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;
import rx.Observable;

public class VideoListActivity extends BaseActivity<VideoListActivity> {

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
        Intent intent = new Intent(from, VideoListActivity.class);
        intent.putExtra("from", BaseActivity.ACT_LIST_FROM_BROWSE);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    public static void goActivity(Fragment from) {
        Intent intent = new Intent(from.getActivity(), VideoListActivity.class);
        intent.putExtra("from", BaseActivity.ACT_LIST_FROM_BROWSE);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    public static void goActivityBySelect(Activity from) {
        Intent intent = new Intent(from, VideoListActivity.class);
        intent.putExtra("from", BaseActivity.ACT_LIST_FROM_SELECT);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_video_list;
    }

    @Override
    protected void initView(Intent intent, Bundle state) {
        String title;
        if (isFromSelect()) {
            title = getString(R.string.please_select_video);
        } else {
            title = getString(R.string.video);
        }
        ViewHelper.initTopBar(mActivity, tb, title, true);
        // recycler
        recyclerHelper = new RecyclerHelper(rv)
                .initLayoutManager(new LinearLayoutManager(mActivity))
                .initRefresh(srl, true)
                .initAdapter(new VideoAdapter(mActivity))
                .viewEmpty(mActivity, R.layout.list_empty_grey, true, true)
                .viewLoadMore(new RecyclerHelper.MoreGreyView())
                .setAdapter()
                .listenerRefresh(() -> getData(false))
                .listenerMore(currentCount -> getData(true))
                .listenerClick(new OnItemChildClickListener() {
                    @Override
                    public void onSimpleItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                        VideoAdapter videoAdapter = (VideoAdapter) adapter;
                        switch (view.getId()) {
                            case R.id.cvVideo:
                                if (isFromSelect()) { // 视频选择
                                    videoAdapter.selectVideo(position);
                                } else { // 播放
                                    videoAdapter.playVideo(position);
                                }
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
                        VideoAdapter videoAdapter = (VideoAdapter) adapter;
                        switch (view.getId()) {
                            case R.id.cvVideo: // 删除
                                videoAdapter.showDeleteDialog(position);
                                break;
                        }
                    }
                });
    }

    @Override
    protected void initData(Intent intent, Bundle state) {
        // event
        Observable<List<Video>> obListRefresh = RxBus.register(RxBus.EVENT_VIDEO_LIST_REFRESH, videoList -> {
            if (recyclerHelper == null) return;
            recyclerHelper.dataRefresh();
        });
        pushBus(RxBus.EVENT_VIDEO_LIST_REFRESH, obListRefresh);
        Observable<Video> obListItemDelete = RxBus.register(RxBus.EVENT_VIDEO_LIST_ITEM_DELETE, video -> {
            if (recyclerHelper == null) return;
            ListHelper.removeObjInAdapter(recyclerHelper.getAdapter(), video);
        });
        pushBus(RxBus.EVENT_VIDEO_LIST_ITEM_DELETE, obListItemDelete);
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
                VideoEditActivity.goActivity(mActivity);
                break;
        }
    }

    private boolean isFromSelect() {
        return getIntent().getIntExtra("from", BaseActivity.ACT_LIST_FROM_BROWSE) == BaseActivity.ACT_LIST_FROM_SELECT;
    }

    private void getData(final boolean more) {
        page = more ? page + 1 : 0;
        // api
        Call<Result> api = new RetrofitHelper().call(API.class).noteVideoListGet(page);
        RetrofitHelper.enqueue(api, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                if (recyclerHelper == null) return;
                List<Video> videoList = data.getVideoList();
                recyclerHelper.dataOk(data.getShow(), videoList, more);
                // 刷新本地资
                List<String> ossKeyList1 = ListHelper.getOssKeyListByVideoThumb(videoList);
                OssResHelper.refreshResWithDelExpire(OssResHelper.TYPE_NOTE_VIDEO_THUMB, ossKeyList1);
                List<String> ossKeyList2 = ListHelper.getOssKeyListByVideo(videoList);
                OssResHelper.refreshResWithDelExpire(OssResHelper.TYPE_NOTE_VIDEO, ossKeyList2);
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
