package com.jiangzg.lovenote.activity.couple;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemLongClickListener;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.base.view.ToastUtils;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.activity.more.VipActivity;
import com.jiangzg.lovenote.adapter.WallPaperAdapter;
import com.jiangzg.lovenote.base.BaseActivity;
import com.jiangzg.lovenote.helper.ConsHelper;
import com.jiangzg.lovenote.helper.MediaPickHelper;
import com.jiangzg.lovenote.helper.OssHelper;
import com.jiangzg.lovenote.helper.RecyclerHelper;
import com.jiangzg.lovenote.helper.RetrofitHelper;
import com.jiangzg.lovenote.helper.RxBus;
import com.jiangzg.lovenote.helper.SPHelper;
import com.jiangzg.lovenote.helper.UserHelper;
import com.jiangzg.lovenote.helper.ViewHelper;
import com.jiangzg.lovenote.model.api.API;
import com.jiangzg.lovenote.model.api.Result;
import com.jiangzg.lovenote.model.entity.WallPaper;
import com.jiangzg.lovenote.view.GSwipeRefreshLayout;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import retrofit2.Call;

public class CoupleWallPaperActivity extends BaseActivity<CoupleWallPaperActivity> {

    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.srl)
    GSwipeRefreshLayout srl;
    @BindView(R.id.rv)
    RecyclerView rv;

    private RecyclerHelper recyclerHelper;
    private Call<Result> callUpdate;
    private Call<Result> callGet;

    public static void goActivity(Fragment from) {
        if (UserHelper.isCoupleBreak(SPHelper.getCouple())) {
            CouplePairActivity.goActivity(from);
            return;
        }
        Intent intent = new Intent(from.getActivity(), CoupleWallPaperActivity.class);
        // intent.putExtra();
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_couple_wall_paper;
    }

    @Override
    protected void initView(Intent intent, Bundle state) {
        ViewHelper.initTopBar(mActivity, tb, getString(R.string.wall_paper), true);
        // recycler
        recyclerHelper = new RecyclerHelper(rv)
                .initLayoutManager(new GridLayoutManager(mActivity, 3, LinearLayoutManager.VERTICAL, false))
                .initRefresh(srl, true)
                .initAdapter(new WallPaperAdapter(mActivity))
                .viewEmpty(mActivity, R.layout.list_empty_white, true, true)
                .setAdapter()
                .listenerRefresh(this::refreshData)
                .listenerClick(new OnItemLongClickListener() {
                    @Override
                    public void onSimpleItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                        WallPaperAdapter wallPaperAdapter = (WallPaperAdapter) adapter;
                        wallPaperAdapter.showDeleteDialog(position);
                    }
                });
    }

    @Override
    protected void initData(Intent intent, Bundle state) {
        recyclerHelper.dataRefresh();
    }

    @Override
    protected void onFinish(Bundle state) {
        RetrofitHelper.cancel(callGet);
        RetrofitHelper.cancel(callUpdate);
        RecyclerHelper.release(recyclerHelper);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;
        if (requestCode == ConsHelper.REQUEST_PICTURE) {
            // 相册
            List<String> pathList = MediaPickHelper.getResultFilePathList(mActivity, data);
            if (pathList == null || pathList.size() <= 0) {
                ToastUtils.show(getString(R.string.file_no_exits));
                return;
            }
            ossUploadWall(pathList);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuAdd: // 添加
                addWallPaper();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void refreshData() {
        callGet = new RetrofitHelper().call(API.class).coupleWallPaperGet();
        RetrofitHelper.enqueue(callGet, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                viewRefresh(data);
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                if (recyclerHelper == null) return;
                recyclerHelper.dataFail(false, message);
            }
        });
    }

    private void addWallPaper() {
        if (recyclerHelper == null) return;
        int count = SPHelper.getVipLimit().getWallPaperCount();
        WallPaperAdapter adapter = recyclerHelper.getAdapter();
        if (adapter == null) return;
        List<String> data = adapter.getData();
        if (data.size() >= count) {
            VipActivity.goActivity(mActivity);
            return;
        }
        MediaPickHelper.selectImage(mActivity, count - data.size());
    }

    // oss上传头像
    private void ossUploadWall(List<String> pathList) {
        OssHelper.uploadWall(mActivity, pathList, new OssHelper.OssUploadsCallBack() {
            @Override
            public void success(List<File> sourceList, List<String> ossPathList) {
                apiPushData(ossPathList);
            }

            @Override
            public void failure(List<File> sourceList, String errMsg) {
            }
        });
    }

    private void apiPushData(List<String> ossPathList) {
        if (recyclerHelper == null || recyclerHelper.getAdapter() == null) return;
        WallPaperAdapter adapter = recyclerHelper.getAdapter();
        List<String> objects = new ArrayList<>(adapter.getData());
        objects.addAll(ossPathList);
        WallPaper body = new WallPaper();
        body.setContentImageList(objects);
        // api
        MaterialDialog loading = getLoading(getString(R.string.are_upload), true);
        callUpdate = new RetrofitHelper().call(API.class).coupleWallPaperUpdate(body);
        RetrofitHelper.enqueue(callUpdate, loading, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                SPHelper.setWallPaper(data.getWallPaper());
                viewRefresh(data);
                // event
                RxBus.post(new RxBus.Event<>(ConsHelper.EVENT_WALL_PAPER_REFRESH, data.getWallPaper()));
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
            }
        });
    }

    private void viewRefresh(Result.Data data) {
        if (recyclerHelper == null) return;
        recyclerHelper.viewEmptyShow(data.getShow());
        recyclerHelper.setAdapter();
        WallPaper wallPaper = data.getWallPaper();
        if (wallPaper == null) {
            srl.setRefreshing(false);
            return;
        }
        List<String> imageList = wallPaper.getContentImageList();
        recyclerHelper.dataNew(imageList, 0);
    }

}
