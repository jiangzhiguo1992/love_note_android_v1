package com.jiangzg.lovenote.controller.activity.note;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
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
import com.chad.library.adapter.base.listener.OnItemLongClickListener;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.base.media.PlayerUtils;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.controller.activity.base.BaseActivity;
import com.jiangzg.lovenote.controller.adapter.note.AudioAdapter;
import com.jiangzg.lovenote.helper.ConsHelper;
import com.jiangzg.lovenote.helper.ListHelper;
import com.jiangzg.lovenote.helper.OssResHelper;
import com.jiangzg.lovenote.helper.RecyclerHelper;
import com.jiangzg.lovenote.helper.RetrofitHelper;
import com.jiangzg.lovenote.helper.RxBus;
import com.jiangzg.lovenote.helper.ViewHelper;
import com.jiangzg.lovenote.model.api.API;
import com.jiangzg.lovenote.model.api.Result;
import com.jiangzg.lovenote.model.entity.Audio;
import com.jiangzg.lovenote.view.GSwipeRefreshLayout;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;
import rx.Observable;

public class AudioListActivity extends BaseActivity<AudioListActivity> {

    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.srl)
    GSwipeRefreshLayout srl;
    @BindView(R.id.rv)
    RecyclerView rv;
    @BindView(R.id.fabAdd)
    FloatingActionButton fabAdd;

    private MediaPlayer mediaPlayer;
    private RecyclerHelper recyclerHelper;
    private Observable<List<Audio>> obListRefresh;
    private Observable<Audio> obListItemDelete;
    private Call<Result> call;
    private int page;

    public static void goActivity(Activity from) {
        Intent intent = new Intent(from, AudioListActivity.class);
        // intent.putExtra();
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    public static void goActivity(Fragment from) {
        Intent intent = new Intent(from.getActivity(), AudioListActivity.class);
        // intent.putExtra();
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_audio_list;
    }

    @Override
    protected void initView(Intent intent, Bundle state) {
        ViewHelper.initTopBar(mActivity, tb, getString(R.string.audio), true);
        // player
        mediaPlayer = PlayerUtils.getMediaPlayer();
        // recycler
        recyclerHelper = new RecyclerHelper(rv)
                .initLayoutManager(new LinearLayoutManager(mActivity))
                .initRefresh(srl, false)
                .initAdapter(new AudioAdapter(mActivity, mediaPlayer))
                .viewEmpty(mActivity, R.layout.list_empty_grey, true, true)
                .viewLoadMore(new RecyclerHelper.MoreGreyView())
                .setAdapter()
                .listenerRefresh(() -> getData(false))
                .listenerMore(currentCount -> getData(true))
                .listenerClick(new OnItemChildClickListener() {
                    @Override
                    public void onSimpleItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                        AudioAdapter audioAdapter = (AudioAdapter) adapter;
                        switch (view.getId()) {
                            case R.id.llContent: // 播放
                                audioAdapter.togglePlayAudio(position);
                                break;
                        }
                    }
                })
                .listenerClick(new OnItemLongClickListener() {
                    @Override
                    public void onSimpleItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                        AudioAdapter audioAdapter = (AudioAdapter) adapter;
                        audioAdapter.showDeleteDialog(position);
                    }
                });
    }

    @Override
    protected void initData(Intent intent, Bundle state) {
        page = 0;
        // event
        obListRefresh = RxBus.register(ConsHelper.EVENT_AUDIO_LIST_REFRESH, audioList -> {
            if (recyclerHelper == null) return;
            recyclerHelper.dataRefresh();
        });
        obListItemDelete = RxBus.register(ConsHelper.EVENT_AUDIO_LIST_ITEM_DELETE, audio -> {
            if (recyclerHelper == null) return;
            ListHelper.removeObjInAdapter(recyclerHelper.getAdapter(), audio);
        });
        // refresh
        recyclerHelper.dataRefresh();
    }

    @Override
    protected void onFinish(Bundle state) {
        RetrofitHelper.cancel(call);
        RxBus.unregister(ConsHelper.EVENT_AUDIO_LIST_REFRESH, obListRefresh);
        RxBus.unregister(ConsHelper.EVENT_AUDIO_LIST_ITEM_DELETE, obListItemDelete);
        RecyclerHelper.release(recyclerHelper);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.add, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (recyclerHelper != null && recyclerHelper.getAdapter() != null) {
            // 被遮挡时 停止播放
            AudioAdapter adapter = recyclerHelper.getAdapter();
            adapter.stopPlay();
        }
        if (isFinishing()) {
            // 退出时 释放资源
            PlayerUtils.destroy(mediaPlayer);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuAdd: // 添加
                AudioEditActivity.goActivity(mActivity);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick({R.id.fabAdd})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.fabAdd: // 添加
                AudioEditActivity.goActivity(mActivity);
                break;
        }
    }

    private void getData(final boolean more) {
        page = more ? page + 1 : 0;
        // api
        call = new RetrofitHelper().call(API.class).noteAudioListGet(page);
        RetrofitHelper.enqueue(call, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                if (recyclerHelper == null) return;
                recyclerHelper.viewEmptyShow(data.getShow());
                List<Audio> audioList = data.getAudioList();
                recyclerHelper.dataOk(audioList, more);
                // 刷新本地资
                List<String> ossKeyList = ListHelper.getOssKeyListByAudio(audioList);
                OssResHelper.refreshResWithDelExpire(OssResHelper.TYPE_NOTE_AUDIO, ossKeyList);
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                if (recyclerHelper == null) return;
                recyclerHelper.dataFail(more, message);
            }
        });
    }

}
