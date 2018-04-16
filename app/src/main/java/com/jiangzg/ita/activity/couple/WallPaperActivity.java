package com.jiangzg.ita.activity.couple;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.afollestad.materialdialogs.MaterialDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemLongClickListener;
import com.jiangzg.base.common.LogUtils;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.base.component.IntentResult;
import com.jiangzg.base.component.IntentSend;
import com.jiangzg.base.view.PopUtils;
import com.jiangzg.base.view.ScreenUtils;
import com.jiangzg.base.view.ToastUtils;
import com.jiangzg.ita.R;
import com.jiangzg.ita.activity.common.HelpActivity;
import com.jiangzg.ita.adapter.WallPaperAdapter;
import com.jiangzg.ita.base.BaseActivity;
import com.jiangzg.ita.domain.Help;
import com.jiangzg.ita.domain.Result;
import com.jiangzg.ita.domain.WallPaper;
import com.jiangzg.ita.helper.API;
import com.jiangzg.ita.helper.ApiHelper;
import com.jiangzg.ita.helper.ConsHelper;
import com.jiangzg.ita.helper.OssHelper;
import com.jiangzg.ita.helper.PopHelper;
import com.jiangzg.ita.helper.RecyclerHelper;
import com.jiangzg.ita.helper.ResHelper;
import com.jiangzg.ita.helper.RetrofitHelper;
import com.jiangzg.ita.helper.ViewHelper;
import com.jiangzg.ita.view.GSwipeRefreshLayout;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import retrofit2.Call;

public class WallPaperActivity extends BaseActivity<WallPaperActivity> {

    @BindView(R.id.root)
    LinearLayout root;
    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.srl)
    GSwipeRefreshLayout srl;
    @BindView(R.id.rv)
    RecyclerView rv;

    private RecyclerHelper recyclerHelper;
    private File cameraFile;
    private File cropFile;

    public static void goActivity(Activity from) {
        Intent intent = new Intent(from, WallPaperActivity.class);
        // intent.putExtra();
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_wall_paper;
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
                .viewEmpty(R.layout.list_empty_common, true, true)
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
        // menu
        tb.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menuHelp: // 评论
                        HelpActivity.goActivity(mActivity, Help.TYPE_WALL_PAPER_ADD);
                        break;
                    case R.id.menuAdd: // 添加
                        showImgSelect();
                        break;
                }
                return true;
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            ResHelper.deleteFileInBackground(cameraFile);
            ResHelper.deleteFileInBackground(cropFile);
            return;
        }
        if (requestCode == ConsHelper.REQUEST_CAMERA) {
            // 拍照
            goCropActivity(cameraFile);
        } else if (requestCode == ConsHelper.REQUEST_PICTURE) {
            // 相册
            File pictureFile = IntentResult.getPictureFile(data);
            goCropActivity(pictureFile);
        } else if (requestCode == ConsHelper.REQUEST_CROP) {
            // 裁剪
            ResHelper.deleteFileInBackground(cameraFile);
            ossUploadWall();
        }
    }

    private void refreshData() {
        Call<Result> call = new RetrofitHelper().call(API.class).coupleWallPaperGet();
        RetrofitHelper.enqueue(call, null, new RetrofitHelper.CallBack() {
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

    public void showImgSelect() {
        cameraFile = ResHelper.createJPEGInCache();
        PopupWindow popupWindow = PopHelper.createPictureCamera(mActivity, cameraFile);
        PopUtils.show(popupWindow, root);
    }

    private void goCropActivity(File source) {
        if (source != null) {
            cropFile = ResHelper.createJPEGInCache();
            int screenWidth = ScreenUtils.getScreenRealWidth(mActivity);
            int screenHeight = ScreenUtils.getScreenRealHeight(mActivity);
            Intent intent = IntentSend.getCrop(source, cropFile, screenWidth, screenHeight);
            ActivityTrans.startResult(mActivity, intent, ConsHelper.REQUEST_CROP);
        } else {
            ToastUtils.show(getString(R.string.picture_get_fail));
            LogUtils.w(LOG_TAG, "IntentResult.getPictureFile: fail");
        }
    }

    // oss上传头像
    private void ossUploadWall() {
        OssHelper.uploadWall(mActivity, cropFile, new OssHelper.OssUploadCallBack() {
            @Override
            public void success(String ossPath) {
                apiPushData(ossPath);
            }

            @Override
            public void failure(String ossPath) {
            }
        });
    }

    private void apiPushData(String ossPath) {
        WallPaperAdapter adapter = recyclerHelper.getAdapter();
        List<String> objects = new ArrayList<>();
        objects.addAll(adapter.getData());
        objects.add(ossPath);
        WallPaper body = ApiHelper.getWallPaperUpdateBody(objects);
        Call<Result> call = new RetrofitHelper().call(API.class).coupleWallPaperUpdate(body);
        MaterialDialog loading = getLoading(getString(R.string.are_upload), true);
        RetrofitHelper.enqueue(call, loading, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                viewRefresh(data);
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
        List<String> imageList = wallPaper.getImageList();
        recyclerHelper.dataNew(imageList);
    }

}
