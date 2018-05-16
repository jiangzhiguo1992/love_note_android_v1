package com.jiangzg.mianmian.activity.book;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.jiangzg.base.common.FileUtils;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.base.component.IntentResult;
import com.jiangzg.base.view.PopUtils;
import com.jiangzg.base.view.ToastUtils;
import com.jiangzg.mianmian.R;
import com.jiangzg.mianmian.activity.common.BigImageActivity;
import com.jiangzg.mianmian.activity.settings.HelpActivity;
import com.jiangzg.mianmian.base.BaseActivity;
import com.jiangzg.mianmian.domain.Album;
import com.jiangzg.mianmian.domain.Help;
import com.jiangzg.mianmian.domain.Result;
import com.jiangzg.mianmian.domain.RxEvent;
import com.jiangzg.mianmian.helper.API;
import com.jiangzg.mianmian.helper.ApiHelper;
import com.jiangzg.mianmian.helper.ConsHelper;
import com.jiangzg.mianmian.helper.DialogHelper;
import com.jiangzg.mianmian.helper.OssHelper;
import com.jiangzg.mianmian.helper.ResHelper;
import com.jiangzg.mianmian.helper.RetrofitHelper;
import com.jiangzg.mianmian.helper.RxBus;
import com.jiangzg.mianmian.helper.SPHelper;
import com.jiangzg.mianmian.helper.ViewHelper;
import com.jiangzg.mianmian.view.GImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnLongClick;
import butterknife.OnTextChanged;
import retrofit2.Call;

public class AlbumEditActivity extends BaseActivity<AlbumEditActivity> {

    @BindView(R.id.root)
    RelativeLayout root;
    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.ivAdd)
    ImageView ivAdd;
    @BindView(R.id.ivAlbum)
    GImageView ivAlbum;
    @BindView(R.id.etTitle)
    TextInputEditText etTitle;
    @BindView(R.id.btnCommit)
    Button btnCommit;

    private Album album;
    private int limitTitleLength;
    private File cameraFile;
    private File pictureFile;
    private Call<Result> callAdd;
    private Call<Result> callUpdate;

    public static void goActivity(Activity from) {
        Intent intent = new Intent(from, AlbumEditActivity.class);
        // intent.putExtra();
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    public static void goActivity(Activity from, Album album) {
        Intent intent = new Intent(from, AlbumEditActivity.class);
        intent.putExtra("album", album);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_album_edit;
    }

    @Override
    protected void initView(Bundle state) {
        ViewHelper.initTopBar(mActivity, tb, getString(R.string.album), true);
        album = getIntent().getParcelableExtra("album");
        ivAlbum.setClickListener(new GImageView.ClickListener() {
            @Override
            public void onSuccessClick(GImageView iv) {
                if (!FileUtils.isFileEmpty(cameraFile)) {
                    BigImageActivity.goActivityByFile(mActivity, cameraFile.getAbsolutePath(), iv);
                } else if (!FileUtils.isFileEmpty(pictureFile)) {
                    BigImageActivity.goActivityByFile(mActivity, pictureFile.getAbsolutePath(), iv);
                } else if (album != null && !StringUtils.isEmpty(album.getCover())) {
                    BigImageActivity.goActivityByOss(mActivity, album.getCover(), iv);
                }
            }
        });
    }

    @Override
    protected void initData(Bundle state) {
        refreshDateView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.help, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RetrofitHelper.cancel(callAdd);
        RetrofitHelper.cancel(callUpdate);
        ResHelper.deleteFileInBackground(cameraFile);
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
                setCoverVisible(false);
            } else {
                setCoverVisible(true);
                ivAlbum.setDataFile(cameraFile);
            }
        } else if (requestCode == ConsHelper.REQUEST_PICTURE) {
            // 相册
            pictureFile = IntentResult.getPictureFile(data);
            if (FileUtils.isFileEmpty(pictureFile)) {
                ToastUtils.show(getString(R.string.file_no_exits));
                pictureFile = null;
                setCoverVisible(false);
            } else {
                setCoverVisible(true);
                ivAlbum.setDataFile(pictureFile);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuHelp: // 帮助
                HelpActivity.goActivity(mActivity, Help.TYPE_ALBUM_EDIT);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnTextChanged({R.id.etTitle})
    public void afterTextChanged(Editable s) {
        onContentInput(s.toString());
    }

    @OnClick({R.id.ivAdd, R.id.btnCommit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ivAdd: // 封面图片
                showImgSelect();
                break;
            case R.id.btnCommit: // 提交
                checkCover();
                break;
        }
    }

    @OnLongClick({R.id.ivAlbum})
    public boolean onLongClick(View view) {
        switch (view.getId()) {
            case R.id.ivAlbum: // 删除封面
                showDeleteDialog();
                return true;
        }
        return false;
    }

    private void refreshDateView() {
        String title = "";
        String cover = "";
        if (album != null) {
            title = album.getTitle();
            cover = album.getCover();
        }
        // hint
        if (limitTitleLength <= 0) {
            limitTitleLength = SPHelper.getLimit().getAlbumTitleLength();
        }
        String string = getString(R.string.please_input_album_name_dont_over_holder_text);
        String format = String.format(Locale.getDefault(), string, limitTitleLength);
        etTitle.setHint(format);
        // title
        etTitle.setText(title);
        btnCommit.setEnabled(!StringUtils.isEmpty(title));
        // cover
        if (StringUtils.isEmpty(cover)) {
            setCoverVisible(false);
        } else {
            setCoverVisible(true);
            ivAlbum.setData(cover);
        }
    }

    private void onContentInput(String input) {
        if (limitTitleLength <= 0) {
            limitTitleLength = SPHelper.getLimit().getAlbumTitleLength();
        }
        int length = input.length();
        if (length > limitTitleLength) {
            CharSequence charSequence = input.subSequence(0, limitTitleLength);
            etTitle.setText(charSequence);
            etTitle.setSelection(charSequence.length());
        }
        btnCommit.setEnabled(length > 0);
    }

    private void showImgSelect() {
        cameraFile = ResHelper.newImageOutCacheFile();
        PopupWindow popupWindow = ViewHelper.createPictureCameraPop(mActivity, cameraFile);
        PopUtils.show(popupWindow, root, Gravity.CENTER);
    }

    private void showDeleteDialog() {
        MaterialDialog dialog = DialogHelper.getBuild(mActivity)
                .cancelable(true)
                .canceledOnTouchOutside(true)
                .content(R.string.confirm_delete_this_image)
                .positiveText(R.string.confirm_no_wrong)
                .negativeText(R.string.i_think_again)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        ResHelper.deleteFileInBackground(cameraFile);
                        cameraFile = null;
                        pictureFile = null;
                        setCoverVisible(false);
                    }
                })
                .build();
        DialogHelper.showWithAnim(dialog);
    }

    private void setCoverVisible(boolean show) {
        ivAdd.setVisibility(show ? View.GONE : View.VISIBLE);
        ivAlbum.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void checkCover() {
        if (!FileUtils.isFileEmpty(cameraFile)) {
            pushImage(cameraFile);
        } else if (!FileUtils.isFileEmpty(pictureFile)) {
            pushImage(pictureFile);
        } else {
            commit("");
        }
    }

    private void pushImage(File imgFile) {
        OssHelper.uploadAlbum(mActivity, imgFile, new OssHelper.OssUploadCallBack() {
            @Override
            public void success(File source, String ossPath) {
                commit(ossPath);
            }

            @Override
            public void failure(File source, String errMsg) {
            }
        });
    }

    private void commit(String cover) {
        String title = etTitle.getText().toString().trim();
        if (album == null) {
            Album body = ApiHelper.getAlbumBody(title, cover);
            addApi(body);
        } else {
            album.setTitle(title);
            album.setCover(cover);
            updateApi(album);
        }
    }

    private void addApi(Album body) {
        callAdd = new RetrofitHelper().call(API.class).AlbumAdd(body);
        MaterialDialog loading = getLoading(true);
        RetrofitHelper.enqueue(callAdd, loading, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                RxEvent<ArrayList<Album>> event = new RxEvent<>(ConsHelper.EVENT_ALBUM_LIST_REFRESH, new ArrayList<Album>());
                RxBus.post(event);
                mActivity.finish();
            }

            @Override
            public void onFailure(String errMsg) {
            }
        });
    }

    private void updateApi(Album body) {
        callUpdate = new RetrofitHelper().call(API.class).AlbumUpdate(body);
        MaterialDialog loading = getLoading(true);
        RetrofitHelper.enqueue(callUpdate, loading, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                // event
                Album album = data.getAlbum();
                RxEvent<Album> event = new RxEvent<>(ConsHelper.EVENT_ALBUM_LIST_ITEM_REFRESH, album);
                RxBus.post(event);
                mActivity.finish();
            }

            @Override
            public void onFailure(String errMsg) {
            }
        });
    }

}
