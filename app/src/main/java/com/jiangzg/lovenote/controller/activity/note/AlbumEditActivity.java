package com.jiangzg.lovenote.controller.activity.note;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.jiangzg.base.common.FileUtils;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.base.view.ToastUtils;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.controller.activity.base.BaseActivity;
import com.jiangzg.lovenote.controller.adapter.common.ImgSquareEditAdapter;
import com.jiangzg.lovenote.helper.common.OssHelper;
import com.jiangzg.lovenote.helper.common.RxBus;
import com.jiangzg.lovenote.helper.common.SPHelper;
import com.jiangzg.lovenote.helper.media.PickHelper;
import com.jiangzg.lovenote.helper.system.RetrofitHelper;
import com.jiangzg.lovenote.helper.view.RecyclerHelper;
import com.jiangzg.lovenote.helper.view.ViewHelper;
import com.jiangzg.lovenote.model.api.API;
import com.jiangzg.lovenote.model.api.Result;
import com.jiangzg.lovenote.model.entity.Album;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import retrofit2.Call;

public class AlbumEditActivity extends BaseActivity<AlbumEditActivity> {

    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.etTitle)
    EditText etTitle;
    @BindView(R.id.rv)
    RecyclerView rv;

    private Album album;
    private RecyclerHelper recyclerHelper;
    private int limitTitleLength;

    public static void goActivity(Activity from) {
        Intent intent = new Intent(from, AlbumEditActivity.class);
        intent.putExtra("from", BaseActivity.ACT_EDIT_FROM_ADD);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    public static void goActivity(Activity from, Album album) {
        if (album == null) {
            goActivity(from);
            return;
        } else if (!album.isMine()) {
            ToastUtils.show(from.getString(R.string.can_operation_self_create_note));
            return;
        }
        Intent intent = new Intent(from, AlbumEditActivity.class);
        intent.putExtra("from", BaseActivity.ACT_EDIT_FROM_UPDATE);
        intent.putExtra("album", album);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_album_edit;
    }

    @Override
    protected void initView(Intent intent, Bundle state) {
        ViewHelper.initTopBar(mActivity, tb, getString(R.string.album), true);
        // init
        if (isFromUpdate()) {
            album = intent.getParcelableExtra("album");
        }
        if (album == null) {
            album = new Album();
        }
        // title
        String format = getString(R.string.please_input_name_no_over_holder_text);
        String hint = String.format(Locale.getDefault(), format, SPHelper.getLimit().getAlbumTitleLength());
        etTitle.setHint(hint);
        etTitle.setText(album.getTitle());
        // rv
        int spanCount = 3;
        ImgSquareEditAdapter imgAdapter = new ImgSquareEditAdapter(mActivity, spanCount, 1);
        imgAdapter.setOnAddClick(() -> PickHelper.selectImage(mActivity, 1, true));
        if (!StringUtils.isEmpty(album.getCover())) {
            List<String> covers = new ArrayList<>();
            covers.add(album.getCover());
            imgAdapter.setOssData(covers);
        }
        recyclerHelper = new RecyclerHelper(rv)
                .initLayoutManager(new GridLayoutManager(mActivity, spanCount))
                .initAdapter(imgAdapter)
                .setAdapter();
    }

    @Override
    protected void initData(Intent intent, Bundle state) {
    }

    @Override
    protected void onFinish(Bundle state) {
        RecyclerHelper.release(recyclerHelper);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.commit, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;
        if (requestCode == BaseActivity.REQUEST_PICTURE) {
            // 相册
            List<String> pathList = PickHelper.getResultFilePathList(mActivity, data);
            if (pathList == null || pathList.size() <= 0) {
                ToastUtils.show(getString(R.string.file_no_exits));
                return;
            }
            if (recyclerHelper == null) return;
            ImgSquareEditAdapter adapter = recyclerHelper.getAdapter();
            if (adapter == null) return;
            adapter.addFileDataList(pathList);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuCommit: // 提交
                checkCover();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean isFromUpdate() {
        return getIntent().getIntExtra("from", BaseActivity.ACT_EDIT_FROM_ADD) == BaseActivity.ACT_EDIT_FROM_UPDATE;
    }

    private void checkCover() {
        if (album == null) return;
        // title
        String title = etTitle.getText().toString();
        if (StringUtils.isEmpty(title)) {
            ToastUtils.show(etTitle.getHint().toString());
            return;
        } else if (title.length() > SPHelper.getLimit().getAlbumTitleLength()) {
            ToastUtils.show(etTitle.getHint().toString());
            return;
        }
        // cover
        File file = null;
        if (recyclerHelper != null && recyclerHelper.getAdapter() != null) {
            ImgSquareEditAdapter adapter = recyclerHelper.getAdapter();
            List<String> fileData = adapter.getFileData();
            if (fileData != null && fileData.size() > 0) {
                file = FileUtils.getFileByPath(fileData.get(0));
            }
        }
        if (!FileUtils.isFileEmpty(file)) {
            pushImage(file);
        } else {
            commit();
        }
    }

    private void pushImage(File imgFile) {
        if (album == null) return;
        OssHelper.uploadAlbum(mActivity, imgFile, new OssHelper.OssUploadCallBack() {
            @Override
            public void success(File source, String ossPath) {
                album.setCover(ossPath);
                commit();
            }

            @Override
            public void failure(File source, String errMsg) {
            }
        });
    }

    private void commit() {
        if (album == null) return;
        album.setTitle(etTitle.getText().toString().trim());
        if (isFromUpdate()) {
            updateApi();
        } else {
            addApi();
        }
    }

    private void addApi() {
        if (album == null) return;
        Call<Result> api = new RetrofitHelper().call(API.class).noteAlbumAdd(album);
        RetrofitHelper.enqueue(api, getLoading(true), new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                // event
                RxBus.post(new RxBus.Event<>(RxBus.EVENT_ALBUM_LIST_REFRESH, new ArrayList<>()));
                mActivity.finish();
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
            }
        });
        pushApi(api);
    }

    private void updateApi() {
        if (album == null) return;
        Call<Result> api = new RetrofitHelper().call(API.class).noteAlbumUpdate(album);
        RetrofitHelper.enqueue(api, getLoading(true), new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                // event
                RxBus.post(new RxBus.Event<>(RxBus.EVENT_ALBUM_LIST_ITEM_REFRESH, data.getAlbum()));
                mActivity.finish();
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
            }
        });
        pushApi(api);
    }

}
