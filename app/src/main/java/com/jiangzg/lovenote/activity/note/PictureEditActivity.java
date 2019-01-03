package com.jiangzg.lovenote.activity.note;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.base.system.LocationInfo;
import com.jiangzg.base.time.DateUtils;
import com.jiangzg.base.view.ToastUtils;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.activity.base.BaseActivity;
import com.jiangzg.lovenote.activity.common.MapSelectActivity;
import com.jiangzg.lovenote.adapter.common.ImgSquareEditAdapter;
import com.jiangzg.lovenote.helper.ConsHelper;
import com.jiangzg.lovenote.helper.DialogHelper;
import com.jiangzg.lovenote.helper.MediaPickHelper;
import com.jiangzg.lovenote.helper.OssHelper;
import com.jiangzg.lovenote.helper.RecyclerHelper;
import com.jiangzg.lovenote.helper.RetrofitHelper;
import com.jiangzg.lovenote.helper.RxBus;
import com.jiangzg.lovenote.helper.SPHelper;
import com.jiangzg.lovenote.helper.TimeHelper;
import com.jiangzg.lovenote.helper.ViewHelper;
import com.jiangzg.lovenote.model.api.API;
import com.jiangzg.lovenote.model.api.Result;
import com.jiangzg.lovenote.model.entity.Album;
import com.jiangzg.lovenote.model.entity.Picture;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;
import rx.Observable;

public class PictureEditActivity extends BaseActivity<PictureEditActivity> {

    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.btnAlbum)
    Button btnAlbum;
    @BindView(R.id.rv)
    RecyclerView rv;
    @BindView(R.id.cvHappenAt)
    CardView cvHappenAt;
    @BindView(R.id.tvHappenAt)
    TextView tvHappenAt;
    @BindView(R.id.cvAddress)
    CardView cvAddress;
    @BindView(R.id.tvAddress)
    TextView tvAddress;

    private Album album;
    private Picture picture;
    private RecyclerHelper recyclerHelper;
    private Observable<Album> obSelectAlbum;
    private Observable<LocationInfo> obSelectMap;
    private Call<Result> callAdd;
    private Call<Result> callUpdate;
    private boolean isChangeAlbum;

    public static void goActivity(Activity from, Album album) {
        if (SPHelper.getLimit().getPicturePushCount() <= 0) {
            ToastUtils.show(from.getString(R.string.refuse_image_upload));
            return;
        }
        Intent intent = new Intent(from, PictureEditActivity.class);
        intent.putExtra("from", ConsHelper.ACT_EDIT_FROM_ADD);
        intent.putExtra("album", album);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    public static void goActivity(Activity from, Album album, Picture picture) {
        if (picture == null) {
            goActivity(from, album);
            return;
        } else if (!picture.isMine()) {
            ToastUtils.show(from.getString(R.string.can_operation_self_create_picture));
            return;
        }
        Intent intent = new Intent(from, PictureEditActivity.class);
        intent.putExtra("from", ConsHelper.ACT_EDIT_FROM_UPDATE);
        intent.putExtra("album", album);
        intent.putExtra("picture", picture);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        isChangeAlbum = false;
        return R.layout.activity_picture_edit;
    }

    @Override
    protected void initView(Intent intent, Bundle state) {
        ViewHelper.initTopBar(mActivity, tb, getString(R.string.picture), true);
        // init
        album = intent.getParcelableExtra("album");
        boolean fromUpdate = isFromUpdate();
        if (fromUpdate) {
            picture = intent.getParcelableExtra("picture");
        }
        if (picture == null) {
            picture = new Picture();
        }
        if (picture.getHappenAt() == 0) {
            picture.setHappenAt(TimeHelper.getGoTimeByJava(DateUtils.getCurrentLong()));
        }
        // view
        refreshAlbum();
        refreshDateView();
        refreshLocationView();
        // recycler
        ImgSquareEditAdapter imgAdapter;
        int spanCount;
        if (fromUpdate) {
            // 更新
            spanCount = 1;
            imgAdapter = new ImgSquareEditAdapter(mActivity, spanCount, spanCount);
            // 不能删除
            imgAdapter.setCanDel(false);
            // 更新的话，把数据加载进去
            List<String> pictureList = new ArrayList<>();
            pictureList.add(picture.getContentImage());
            imgAdapter.setOssData(pictureList);
        } else {
            // 添加
            int pictureLimitCount = SPHelper.getLimit().getPicturePushCount();
            spanCount = pictureLimitCount > 3 ? 3 : pictureLimitCount;
            imgAdapter = new ImgSquareEditAdapter(mActivity, spanCount, pictureLimitCount);
            imgAdapter.setOnAddClick(this::goPicture);
        }
        if (spanCount > 0) {
            rv.setVisibility(View.VISIBLE);
            if (recyclerHelper == null) {
                recyclerHelper = new RecyclerHelper(rv)
                        .initLayoutManager(new GridLayoutManager(mActivity, spanCount))
                        .initAdapter(imgAdapter)
                        .setAdapter();
            }
        } else {
            rv.setVisibility(View.GONE);
        }
    }

    @Override
    protected void initData(Intent intent, Bundle state) {
        // event
        obSelectAlbum = RxBus.register(ConsHelper.EVENT_ALBUM_SELECT, album -> {
            PictureEditActivity.this.isChangeAlbum = true;
            PictureEditActivity.this.album = album;
            PictureEditActivity.this.refreshAlbum();
        });
        obSelectMap = RxBus.register(ConsHelper.EVENT_MAP_SELECT, info -> {
            if (info == null || picture == null) return;
            picture.setLatitude(info.getLatitude());
            picture.setLongitude(info.getLongitude());
            picture.setAddress(info.getAddress());
            picture.setCityId(info.getCityId());
            refreshLocationView();
        });
    }

    @Override
    protected void onFinish(Bundle state) {
        RetrofitHelper.cancel(callAdd);
        RetrofitHelper.cancel(callUpdate);
        RxBus.unregister(ConsHelper.EVENT_ALBUM_SELECT, obSelectAlbum);
        RxBus.unregister(ConsHelper.EVENT_MAP_SELECT, obSelectMap);
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
        if (requestCode == ConsHelper.REQUEST_PICTURE) {
            // 相册
            List<String> pathList = MediaPickHelper.getResultFilePathList(mActivity, data);
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
                checkCommit();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick({R.id.btnAlbum, R.id.cvHappenAt, R.id.cvAddress})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnAlbum: // 相册选取
                AlbumListActivity.goActivityBySelectAlbum(mActivity);
                break;
            case R.id.cvHappenAt: // 时间
                showDatePicker();
                break;
            case R.id.cvAddress: // 位置
                if (picture == null) return;
                MapSelectActivity.goActivity(mActivity, picture.getAddress(), picture.getLongitude(), picture.getLatitude());
                break;
        }
    }

    private boolean isFromUpdate() {
        return getIntent().getIntExtra("from", ConsHelper.ACT_EDIT_FROM_ADD) == ConsHelper.ACT_EDIT_FROM_UPDATE;
    }

    private void refreshAlbum() {
        if (album == null || album.getId() == 0 || StringUtils.isEmpty(album.getTitle())) {
            btnAlbum.setText(R.string.please_select_album);
        } else {
            String title = String.format(Locale.getDefault(), getString(R.string.album_colon_space_holder), album.getTitle());
            btnAlbum.setText(title);
        }
    }

    private void showDatePicker() {
        if (picture == null) return;
        DialogHelper.showDateTimePicker(mActivity, TimeHelper.getJavaTimeByGo(picture.getHappenAt()), time -> {
            picture.setHappenAt(TimeHelper.getGoTimeByJava(time));
            refreshDateView();
        });
    }

    private void refreshDateView() {
        if (picture == null) return;
        String happenShow = TimeHelper.getTimeShowLine_HM_MDHM_YMDHM_ByGo(picture.getHappenAt());
        String format = String.format(Locale.getDefault(), getString(R.string.take_camera_in_colon_space_holder), happenShow);
        tvHappenAt.setText(format);
    }

    private void refreshLocationView() {
        if (picture == null) return;
        String location = StringUtils.isEmpty(picture.getAddress()) ? getString(R.string.now_no) : picture.getAddress();
        tvAddress.setText(location);
    }

    private void goPicture() {
        if (SPHelper.getVipLimit().getPictureTotalCount() <= 0) {
            ToastUtils.show(mActivity.getString(R.string.refuse_image_upload));
            return;
        }
        if (recyclerHelper == null || recyclerHelper.getAdapter() == null) return;
        int pushCount = SPHelper.getLimit().getPicturePushCount();
        ImgSquareEditAdapter imgAdapter = recyclerHelper.getAdapter();
        int maxCount = pushCount - imgAdapter.getOssData().size() - imgAdapter.getFileData().size();
        MediaPickHelper.selectImage(mActivity, maxCount);
    }

    private void checkCommit() {
        if (picture == null) return;
        if ((album == null || album.getId() == 0) && picture.getAlbumId() == 0) {
            ToastUtils.show(getString(R.string.please_select_album));
            return;
        } else {
            if (album != null && album.getId() != 0) {
                picture.setAlbumId(album.getId());
            }
        }
        if (isFromUpdate()) {
            // 更新
            commitUpdate();
        } else {
            // 添加
            List<String> fileData = null;
            if (recyclerHelper != null && recyclerHelper.getAdapter() != null) {
                ImgSquareEditAdapter adapter = recyclerHelper.getAdapter();
                fileData = adapter.getFileData();
            }
            if (fileData == null || fileData.size() <= 0) {
                ToastUtils.show(getString(R.string.picture_where));
                return;
            }
            uploadPictureList(fileData);
        }
    }

    private void uploadPictureList(List<String> fileData) {
        if (picture == null) return;
        OssHelper.uploadPicture(mActivity, fileData, new OssHelper.OssUploadsCallBack() {
            @Override
            public void success(List<File> sourceList, List<String> ossPathList) {
                commitAdd(ossPathList);
            }

            @Override
            public void failure(List<File> sourceList, String errMsg) {

            }
        });
    }

    private void commitAdd(List<String> ossPathList) {
        if (picture == null) return;
        if (ossPathList == null || ossPathList.size() <= 0) return;
        List<Picture> pictureList = new ArrayList<>();
        for (String ossPath : ossPathList) {
            Picture body = new Picture();
            body.setAlbumId(picture.getAlbumId());
            body.setHappenAt(picture.getHappenAt());
            body.setContentImage(ossPath);
            body.setLongitude(picture.getLongitude());
            body.setLatitude(picture.getLatitude());
            body.setAddress(picture.getAddress());
            body.setCityId(picture.getCityId());
            pictureList.add(body);
        }
        Album body = new Album();
        body.setPictureList(pictureList);
        callAdd = new RetrofitHelper().call(API.class).notePictureListAdd(body);
        MaterialDialog loading = getLoading(true);
        RetrofitHelper.enqueue(callAdd, loading, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                List<Picture> pictureList = data.getPictureList();
                int total = (pictureList == null) ? 0 : pictureList.size();
                String toast = String.format(Locale.getDefault(), mActivity.getString(R.string.success_push_holder_paper_picture), total);
                ToastUtils.show(toast);
                // event
                RxBus.post(new RxBus.Event<>(ConsHelper.EVENT_ALBUM_LIST_REFRESH, new ArrayList<>()));
                RxBus.post(new RxBus.Event<>(ConsHelper.EVENT_ALBUM_DETAIL_REFRESH, album));
                RxBus.post(new RxBus.Event<>(ConsHelper.EVENT_PICTURE_LIST_REFRESH, new ArrayList<>()));
                // finish
                mActivity.finish();
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
            }
        });
    }

    private void commitUpdate() {
        if (picture == null) return;
        callUpdate = new RetrofitHelper().call(API.class).notePictureUpdate(picture);
        MaterialDialog loading = getLoading(true);
        RetrofitHelper.enqueue(callUpdate, loading, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                // event
                RxBus.post(new RxBus.Event<>(ConsHelper.EVENT_ALBUM_LIST_REFRESH, new ArrayList<>()));
                RxBus.post(new RxBus.Event<>(ConsHelper.EVENT_ALBUM_DETAIL_REFRESH, album));
                Picture picture = data.getPicture();
                if (isChangeAlbum) {
                    RxBus.post(new RxBus.Event<>(ConsHelper.EVENT_PICTURE_LIST_REFRESH, new ArrayList<>()));
                } else {
                    RxBus.post(new RxBus.Event<>(ConsHelper.EVENT_PICTURE_LIST_ITEM_REFRESH, picture));
                }
                // finish
                mActivity.finish();
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
            }
        });
    }
}
