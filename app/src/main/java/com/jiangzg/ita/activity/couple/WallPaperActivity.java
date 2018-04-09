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
import com.chad.library.adapter.base.listener.OnItemClickListener;
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
import com.jiangzg.ita.base.MyApp;
import com.jiangzg.ita.domain.Help;
import com.jiangzg.ita.domain.WallPaper;
import com.jiangzg.ita.helper.ConsHelper;
import com.jiangzg.ita.helper.OssHelper;
import com.jiangzg.ita.helper.PopHelper;
import com.jiangzg.ita.helper.RecyclerHelper;
import com.jiangzg.ita.helper.ResHelper;
import com.jiangzg.ita.helper.ViewHelper;
import com.jiangzg.ita.view.GImageView;
import com.jiangzg.ita.view.GSwipeRefreshLayout;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

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
                .initRefresh(srl, false)
                .initAdapter(new WallPaperAdapter(mActivity))
                .listenerRefresh(new RecyclerHelper.RefreshListener() {
                    @Override
                    public void onRefresh() {
                        getData();
                    }
                })
                .listenerClick(new OnItemClickListener() {
                    @Override
                    public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
                        WallPaperAdapter wallPaperAdapter = (WallPaperAdapter) adapter;
                        GImageView ivWallPaper = view.findViewById(R.id.ivWallPaper);
                        wallPaperAdapter.goImgScreen(position, ivWallPaper);
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
            ResHelper.deleteFileInBackground(cameraFile, true);
            ResHelper.deleteFileInBackground(cropFile, false);
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
            ResHelper.deleteFileInBackground(cameraFile, true);
            ossUploadWall();
        }
    }

    private void getData() {
        // todo api
        MyApp.get().getHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                WallPaper wallPaper = new WallPaper();
                List<String> list = new ArrayList<>();
                wallPaper.setImageList(list);

                recyclerHelper.dataNew(list);
            }

        }, 1000);
    }

    public void showImgSelect() {
        // todo 检查数量 vip?
        cameraFile = ResHelper.createJPEGInCache();
        PopupWindow popupWindow = PopHelper.createAlbumCamera(mActivity, cameraFile);
        PopUtils.show(popupWindow, root);
    }

    private void goCropActivity(File source) {
        if (source != null) {
            cropFile = ResHelper.createJPEGInCache();
            int screenWidth = ScreenUtils.getScreenWidth(mActivity);
            int screenHeight = ScreenUtils.getScreenHeight(mActivity);
            Intent intent = IntentSend.getCrop(source, cropFile, screenWidth, screenHeight);
            ActivityTrans.startResult(mActivity, intent, ConsHelper.REQUEST_CROP);
        } else {
            ToastUtils.show(getString(R.string.picture_get_fail));
            LogUtils.w(LOG_TAG, "IntentResult.getPictureFile: fail");
        }
    }

    // oss上传头像
    private void ossUploadWall() {
        MaterialDialog process = getProcess();
        OssHelper.uploadWall(process, cropFile, new OssHelper.OssCallBack() {
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
        // todo api
        // todo refresh data
    }

}
