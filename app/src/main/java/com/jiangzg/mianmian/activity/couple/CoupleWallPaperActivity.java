package com.jiangzg.mianmian.activity.couple;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.afollestad.materialdialogs.MaterialDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemLongClickListener;
import com.jiangzg.base.common.FileUtils;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.base.component.IntentResult;
import com.jiangzg.base.view.PopUtils;
import com.jiangzg.base.view.ToastUtils;
import com.jiangzg.mianmian.R;
import com.jiangzg.mianmian.activity.settings.HelpActivity;
import com.jiangzg.mianmian.adapter.WallPaperAdapter;
import com.jiangzg.mianmian.base.BaseActivity;
import com.jiangzg.mianmian.domain.Help;
import com.jiangzg.mianmian.domain.Result;
import com.jiangzg.mianmian.domain.RxEvent;
import com.jiangzg.mianmian.domain.WallPaper;
import com.jiangzg.mianmian.helper.API;
import com.jiangzg.mianmian.helper.ApiHelper;
import com.jiangzg.mianmian.helper.ConsHelper;
import com.jiangzg.mianmian.helper.OssHelper;
import com.jiangzg.mianmian.helper.RecyclerHelper;
import com.jiangzg.mianmian.helper.ResHelper;
import com.jiangzg.mianmian.helper.RetrofitHelper;
import com.jiangzg.mianmian.helper.RxBus;
import com.jiangzg.mianmian.helper.SPHelper;
import com.jiangzg.mianmian.helper.ViewHelper;
import com.jiangzg.mianmian.view.GSwipeRefreshLayout;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import retrofit2.Call;

public class CoupleWallPaperActivity extends BaseActivity<CoupleWallPaperActivity> {

    @BindView(R.id.root)
    LinearLayout root;
    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.srl)
    GSwipeRefreshLayout srl;
    @BindView(R.id.rv)
    RecyclerView rv;

    private RecyclerHelper recyclerHelper;
    private Call<Result> callUpdate;
    private Call<Result> callGet;
    private File cameraFile;

    public static void goActivity(Fragment from) {
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
    protected void initView(Bundle state) {
        ViewHelper.initTopBar(mActivity, tb, getString(R.string.wall_paper), true);
        // recycler
        recyclerHelper = new RecyclerHelper(mActivity)
                .initRecycler(rv)
                .initLayoutManager(new GridLayoutManager(mActivity, 3, LinearLayoutManager.VERTICAL, false))
                .initRefresh(srl, true)
                .initAdapter(new WallPaperAdapter(mActivity))
                .viewEmpty(R.layout.list_empty_white, true, true)
                .setAdapter()
                .listenerRefresh(new RecyclerHelper.RefreshListener() {
                    @Override
                    public void onRefresh() {
                        refreshData();
                    }
                })
                .listenerClick(new OnItemLongClickListener() {
                    @Override
                    public void onSimpleItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                        WallPaperAdapter wallPaperAdapter = (WallPaperAdapter) adapter;
                        wallPaperAdapter.showDeleteDialog(position);
                    }
                });
    }

    @Override
    protected void initData(Bundle state) {
        recyclerHelper.dataRefresh();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.help_add, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RetrofitHelper.cancel(callGet);
        RetrofitHelper.cancel(callUpdate);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            ResHelper.deleteFileInBackground(cameraFile);
            return;
        }
        if (requestCode == ConsHelper.REQUEST_CAMERA) {
            // 拍照
            if (FileUtils.isFileEmpty(cameraFile)) {
                ToastUtils.show(getString(R.string.file_no_exits));
                ResHelper.deleteFileInBackground(cameraFile);
                return;
            }
            ossUploadWall(cameraFile);
        } else if (requestCode == ConsHelper.REQUEST_PICTURE) {
            // 相册
            File pictureFile = IntentResult.getPictureFile(data);
            if (FileUtils.isFileEmpty(pictureFile)) {
                ToastUtils.show(getString(R.string.file_no_exits));
                return;
            }
            ossUploadWall(pictureFile);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuHelp: // 帮助
                HelpActivity.goActivity(mActivity, Help.INDEX_WALL_PAPER_ADD);
                return true;
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
            public void onFailure(String errMsg) {
                recyclerHelper.dataFail(false, errMsg);
            }
        });
    }

    private void addWallPaper() {
        int count = SPHelper.getVipLimit().getWallPaperCount();
        if (recyclerHelper == null) return;
        WallPaperAdapter adapter = recyclerHelper.getAdapter();
        if (adapter == null) return;
        List<String> data = adapter.getData();
        if (data.size() >= count) {
            String format = String.format(Locale.getDefault(), getString(R.string.now_just_can_push_holder_img), count);
            ToastUtils.show(format);
            return;
        }
        showImgSelect();
    }

    public void showImgSelect() {
        cameraFile = ResHelper.newImageCacheFile();
        PopupWindow popupWindow = ViewHelper.createPictureCameraPop(mActivity, cameraFile);
        PopUtils.show(popupWindow, root, Gravity.CENTER);
    }

    // oss上传头像
    private void ossUploadWall(File file) {
        OssHelper.uploadWall(mActivity, file, new OssHelper.OssUploadCallBack() {
            @Override
            public void success(File source, String ossPath) {
                apiPushData(ossPath);
                ResHelper.deleteFileInBackground(cameraFile);
            }

            @Override
            public void failure(File source, String errMsg) {
                ResHelper.deleteFileInBackground(cameraFile);
            }
        });
    }

    private void apiPushData(String ossPath) {
        WallPaperAdapter adapter = recyclerHelper.getAdapter();
        List<String> objects = new ArrayList<>();
        objects.addAll(adapter.getData());
        objects.add(ossPath);
        WallPaper body = ApiHelper.getWallPaperUpdateBody(objects);
        callUpdate = new RetrofitHelper().call(API.class).coupleWallPaperUpdate(body);
        MaterialDialog loading = getLoading(getString(R.string.are_upload), true);
        RetrofitHelper.enqueue(callUpdate, loading, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                SPHelper.setWallPaper(data.getWallPaper());
                viewRefresh(data);
                // event
                RxEvent<WallPaper> event = new RxEvent<>(ConsHelper.EVENT_WALL_PAPER_REFRESH, data.getWallPaper());
                RxBus.post(event);
            }

            @Override
            public void onFailure(String errMsg) {
            }
        });
    }

    private void viewRefresh(Result.Data data) {
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
