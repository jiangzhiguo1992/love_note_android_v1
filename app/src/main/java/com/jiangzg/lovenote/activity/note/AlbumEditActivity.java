package com.jiangzg.lovenote.activity.note;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.jiangzg.base.common.ConvertUtils;
import com.jiangzg.base.common.FileUtils;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.base.view.ScreenUtils;
import com.jiangzg.base.view.ToastUtils;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.activity.common.BigImageActivity;
import com.jiangzg.lovenote.base.BaseActivity;
import com.jiangzg.lovenote.helper.ConsHelper;
import com.jiangzg.lovenote.helper.DialogHelper;
import com.jiangzg.lovenote.helper.MediaPickHelper;
import com.jiangzg.lovenote.helper.OssHelper;
import com.jiangzg.lovenote.helper.RetrofitHelper;
import com.jiangzg.lovenote.helper.RxBus;
import com.jiangzg.lovenote.helper.SPHelper;
import com.jiangzg.lovenote.helper.ViewHelper;
import com.jiangzg.lovenote.model.api.API;
import com.jiangzg.lovenote.model.entity.Album;
import com.jiangzg.lovenote.model.entity.Result;
import com.jiangzg.lovenote.view.FrescoView;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnLongClick;
import butterknife.OnTextChanged;
import retrofit2.Call;

public class AlbumEditActivity extends BaseActivity<AlbumEditActivity> {

    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.ivAdd)
    ImageView ivAdd;
    @BindView(R.id.ivAlbum)
    FrescoView ivAlbum;
    @BindView(R.id.etTitle)
    EditText etTitle;

    private Album album;
    private int limitTitleLength;
    private File pictureFile;
    private Call<Result> callAdd;
    private Call<Result> callUpdate;

    public static void goActivity(Activity from) {
        Intent intent = new Intent(from, AlbumEditActivity.class);
        intent.putExtra("from", ConsHelper.ACT_EDIT_FROM_ADD);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    public static void goActivity(Activity from, Album album) {
        if (album == null) {
            goActivity(from);
            return;
        } else if (!album.isMine()) {
            ToastUtils.show(from.getString(R.string.can_operation_self_create_album));
            return;
        }
        Intent intent = new Intent(from, AlbumEditActivity.class);
        intent.putExtra("from", ConsHelper.ACT_EDIT_FROM_UPDATE);
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
        // input
        etTitle.setText(album.getTitle());
        // view
        ViewGroup.LayoutParams layoutParams = ivAlbum.getLayoutParams();
        int width = ScreenUtils.getScreenWidth(mActivity) - (ConvertUtils.dp2px(50) * 2);
        ivAlbum.setWidthAndHeight(width, layoutParams.height);
        ivAlbum.setClickListener(new FrescoView.ClickListener() {
            @Override
            public void onSuccessClick(FrescoView iv) {
                if (album == null) return;
                if (!FileUtils.isFileEmpty(pictureFile)) {
                    BigImageActivity.goActivityByFile(mActivity, pictureFile.getAbsolutePath(), iv);
                } else if (!StringUtils.isEmpty(album.getCover())) {
                    BigImageActivity.goActivityByOss(mActivity, album.getCover(), iv);
                }
            }
        });
    }

    @Override
    protected void initData(Intent intent, Bundle state) {
        refreshDateView();
    }

    @Override
    protected void onFinish(Bundle state) {
        RetrofitHelper.cancel(callAdd);
        RetrofitHelper.cancel(callUpdate);
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
        if (requestCode == ConsHelper.REQUEST_PICTURE) {
            // 相册
            pictureFile = MediaPickHelper.getResultFile(data);
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
            case R.id.menuCommit: // 提交
                checkCover();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnTextChanged({R.id.etTitle})
    public void afterTextChanged(Editable s) {
        onContentInput(s.toString());
    }

    @OnClick({R.id.ivAdd})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ivAdd: // 封面图片
                MediaPickHelper.selectImage(mActivity, 1);
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

    private boolean isFromUpdate() {
        return getIntent().getIntExtra("from", ConsHelper.ACT_EDIT_FROM_ADD) == ConsHelper.ACT_EDIT_FROM_UPDATE;
    }

    private void refreshDateView() {
        if (album == null) return;
        String title = album.getTitle();
        String cover = album.getCover();
        // etTitle
        if (limitTitleLength <= 0) {
            limitTitleLength = SPHelper.getLimit().getAlbumTitleLength();
        }
        String string = getString(R.string.please_input_album_name_dont_over_holder_text);
        String format = String.format(Locale.getDefault(), string, limitTitleLength);
        etTitle.setHint(format);
        etTitle.setText(title);
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
                        setCoverVisible(false);
                    }
                })
                .build();
        DialogHelper.showWithAnim(dialog);
    }

    private void setCoverVisible(boolean show) {
        if (album == null) return;
        if (!show) {
            pictureFile = null;
            album.setCover("");
        }
        ivAdd.setVisibility(show ? View.GONE : View.VISIBLE);
        ivAlbum.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void checkCover() {
        // btn
        if (StringUtils.isEmpty(etTitle.getText().toString().trim())) {
            ToastUtils.show(etTitle.getHint().toString());
            return;
        }
        if (!FileUtils.isFileEmpty(pictureFile)) {
            pushImage(pictureFile);
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
        callAdd = new RetrofitHelper().call(API.class).noteAlbumAdd(album);
        MaterialDialog loading = getLoading(true);
        RetrofitHelper.enqueue(callAdd, loading, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                // event
                RxBus.Event<ArrayList<Album>> event = new RxBus.Event<>(ConsHelper.EVENT_ALBUM_LIST_REFRESH, new ArrayList<Album>());
                RxBus.post(event);
                mActivity.finish();
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
            }
        });
    }

    private void updateApi() {
        if (album == null) return;
        callUpdate = new RetrofitHelper().call(API.class).noteAlbumUpdate(album);
        MaterialDialog loading = getLoading(true);
        RetrofitHelper.enqueue(callUpdate, loading, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                // event
                Album album = data.getAlbum();
                RxBus.Event<Album> event = new RxBus.Event<>(ConsHelper.EVENT_ALBUM_LIST_ITEM_REFRESH, album);
                RxBus.post(event);
                mActivity.finish();
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
            }
        });
    }

}
