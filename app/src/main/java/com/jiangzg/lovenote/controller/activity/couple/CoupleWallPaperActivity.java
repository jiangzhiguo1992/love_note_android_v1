package com.jiangzg.lovenote.controller.activity.couple;

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

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemLongClickListener;
import com.jiangzg.base.common.FileUtils;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.base.component.IntentFactory;
import com.jiangzg.base.view.ToastUtils;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.controller.activity.base.BaseActivity;
import com.jiangzg.lovenote.controller.activity.more.VipActivity;
import com.jiangzg.lovenote.controller.adapter.couple.WallPaperAdapter;
import com.jiangzg.lovenote.helper.common.OssHelper;
import com.jiangzg.lovenote.helper.common.ResHelper;
import com.jiangzg.lovenote.helper.common.RxBus;
import com.jiangzg.lovenote.helper.common.SPHelper;
import com.jiangzg.lovenote.helper.common.UserHelper;
import com.jiangzg.lovenote.helper.media.PickHelper;
import com.jiangzg.lovenote.helper.system.RetrofitHelper;
import com.jiangzg.lovenote.helper.view.RecyclerHelper;
import com.jiangzg.lovenote.helper.view.ViewHelper;
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
    private File cropFile;

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
                .initRefresh(srl, false)
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
        RecyclerHelper.release(recyclerHelper);
        // 创建成功的cropFile都要删除
        ResHelper.deleteFileInBackground(cropFile);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            ResHelper.deleteFileInBackground(cropFile);
            return;
        }
        if (requestCode == BaseActivity.REQUEST_PICTURE) {
            // 相册
            File pictureFile = PickHelper.getResultFile(mActivity, data);
            if (FileUtils.isFileEmpty(pictureFile)) {
                ToastUtils.show(getString(R.string.file_no_exits));
                return;
            }
            String extension = FileUtils.getFileExtension(pictureFile);
            if (!StringUtils.isEmpty(extension) && extension.contains("gif")) {
                ossUploadWall(pictureFile);
                return;
            }
            cropFile = ResHelper.newImageCacheFile();
            Intent intent = IntentFactory.getImageCrop(ResHelper.getFileProviderAuth(), pictureFile, cropFile);
            ActivityTrans.startResult(mActivity, intent, BaseActivity.REQUEST_CROP);
        } else if (requestCode == BaseActivity.REQUEST_CROP) {
            // 裁剪
            ossUploadWall(null);
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
        Call<Result> api = new RetrofitHelper().call(API.class).coupleWallPaperGet();
        RetrofitHelper.enqueue(api, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                WallPaper wallPaper = data.getWallPaper();
                if (wallPaper == null) wallPaper = new WallPaper();
                SPHelper.setWallPaper(wallPaper);
                if (recyclerHelper == null) return;
                recyclerHelper.dataOk(data.getShow(), wallPaper.getContentImageList(), false);
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                if (recyclerHelper == null) return;
                recyclerHelper.dataFail(false, message);
            }
        });
        pushApi(api);
    }

    private void addWallPaper() {
        if (recyclerHelper == null) return;
        int limit = SPHelper.getVipLimit().getWallPaperCount();
        List<String> data = SPHelper.getWallPaper().getContentImageList();
        if (data != null && data.size() >= limit) {
            VipActivity.goActivity(mActivity);
            return;
        }
        PickHelper.selectImage(mActivity, 1, true);
    }

    private void ossUploadWall(final File picture) {
        File uploadFile = FileUtils.isFileEmpty(picture) ? cropFile : picture;
        OssHelper.uploadWall(mActivity, uploadFile, new OssHelper.OssUploadCallBack() {
            @Override
            public void success(File source, String ossPath) {
                addWallPaper(ossPath);
                ResHelper.deleteFileInBackground(cropFile);

            }

            @Override
            public void failure(File source, String errMsg) {
                ResHelper.deleteFileInBackground(cropFile);
            }
        });
    }

    private void addWallPaper(String ossPath) {
        WallPaper body = SPHelper.getWallPaper();
        if (body.getContentImageList() == null) {
            body.setContentImageList(new ArrayList<>());
        }
        body.getContentImageList().add(ossPath);
        // api
        Call<Result> api = new RetrofitHelper().call(API.class).coupleWallPaperUpdate(body);
        RetrofitHelper.enqueue(api, getLoading(true), new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                WallPaper wallPaper = data.getWallPaper();
                if (wallPaper == null) wallPaper = new WallPaper();
                SPHelper.setWallPaper(wallPaper);
                if (recyclerHelper == null) return;
                recyclerHelper.dataOk(data.getShow(), wallPaper.getContentImageList(), false);
                // event
                RxBus.post(new RxBus.Event<>(RxBus.EVENT_WALL_PAPER_REFRESH, wallPaper));
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
            }
        });
        pushApi(api);
    }

}
