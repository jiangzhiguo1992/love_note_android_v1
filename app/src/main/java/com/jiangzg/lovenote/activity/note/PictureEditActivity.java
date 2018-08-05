package com.jiangzg.lovenote.activity.note;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.jiangzg.base.common.FileUtils;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.base.component.IntentResult;
import com.jiangzg.base.system.LocationInfo;
import com.jiangzg.base.time.DateUtils;
import com.jiangzg.base.view.PopUtils;
import com.jiangzg.base.view.ToastUtils;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.activity.common.MapSelectActivity;
import com.jiangzg.lovenote.adapter.ImgSquareEditAdapter;
import com.jiangzg.lovenote.base.BaseActivity;
import com.jiangzg.lovenote.domain.Album;
import com.jiangzg.lovenote.domain.Picture;
import com.jiangzg.lovenote.domain.PictureList;
import com.jiangzg.lovenote.domain.Result;
import com.jiangzg.lovenote.domain.RxEvent;
import com.jiangzg.lovenote.helper.API;
import com.jiangzg.lovenote.helper.ApiHelper;
import com.jiangzg.lovenote.helper.ConsHelper;
import com.jiangzg.lovenote.helper.DialogHelper;
import com.jiangzg.lovenote.helper.OssHelper;
import com.jiangzg.lovenote.helper.RecyclerHelper;
import com.jiangzg.lovenote.helper.ResHelper;
import com.jiangzg.lovenote.helper.RetrofitHelper;
import com.jiangzg.lovenote.helper.RxBus;
import com.jiangzg.lovenote.helper.SPHelper;
import com.jiangzg.lovenote.helper.TimeHelper;
import com.jiangzg.lovenote.helper.ViewHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;
import rx.Observable;
import rx.functions.Action1;

public class PictureEditActivity extends BaseActivity<PictureEditActivity> {

    @BindView(R.id.root)
    LinearLayout root;
    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.cvAlbum)
    CardView cvAlbum;
    @BindView(R.id.tvAlbum)
    TextView tvAlbum;
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
    @BindView(R.id.btnCommit)
    Button btnCommit;

    private Album album;
    private Picture picture;
    private RecyclerHelper recyclerHelper;
    private Observable<Album> obSelectAlbum;
    private Observable<LocationInfo> obSelectMap;
    private Call<Result> callAdd;
    private Call<Result> callUpdate;
    private File cameraFile;
    private List<File> cameraFileList;
    private boolean isChangeAlbum;

    public static void goActivity(Activity from, Album album) {
        int pictureCount = SPHelper.getLimit().getPictureCount();
        if (pictureCount <= 0) {
            String string = from.getString(R.string.now_just_upload_holder_picture);
            ToastUtils.show(String.format(Locale.getDefault(), string, pictureCount));
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
            int pictureLimitCount = SPHelper.getLimit().getPictureCount();
            spanCount = pictureLimitCount > 3 ? 3 : pictureLimitCount;
            imgAdapter = new ImgSquareEditAdapter(mActivity, spanCount, pictureLimitCount);
            imgAdapter.setOnAddClick(new ImgSquareEditAdapter.OnAddClickListener() {
                @Override
                public void onAdd() {
                    showImgSelect();
                }
            });
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
        obSelectAlbum = RxBus.register(ConsHelper.EVENT_ALBUM_SELECT, new Action1<Album>() {
            @Override
            public void call(Album album) {
                PictureEditActivity.this.isChangeAlbum = true;
                PictureEditActivity.this.album = album;
                PictureEditActivity.this.refreshAlbum();
            }
        });
        obSelectMap = RxBus.register(ConsHelper.EVENT_MAP_SELECT, new Action1<LocationInfo>() {
            @Override
            public void call(LocationInfo info) {
                if (info == null || picture == null) return;
                picture.setLatitude(info.getLatitude());
                picture.setLongitude(info.getLongitude());
                picture.setAddress(info.getAddress());
                picture.setCityId(info.getCityId());
                refreshLocationView();
            }
        });
    }

    @Override
    protected void onFinish(Bundle state) {
        RetrofitHelper.cancel(callAdd);
        RetrofitHelper.cancel(callUpdate);
        RxBus.unregister(ConsHelper.EVENT_ALBUM_SELECT, obSelectAlbum);
        RxBus.unregister(ConsHelper.EVENT_MAP_SELECT, obSelectMap);
        RecyclerHelper.release(recyclerHelper);
        // 创建成功的cameraFile都要删除
        ResHelper.deleteFileListInBackground(cameraFileList);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK || picture == null) {
            ResHelper.deleteFileInBackground(cameraFile);
            return;
        }
        if (recyclerHelper == null) return;
        ImgSquareEditAdapter adapter = recyclerHelper.getAdapter();
        if (adapter == null) return;
        if (requestCode == ConsHelper.REQUEST_CAMERA) {
            // 拍照
            if (FileUtils.isFileEmpty(cameraFile)) {
                ToastUtils.show(getString(R.string.file_no_exits));
                ResHelper.deleteFileInBackground(cameraFile);
                return;
            }
            adapter.addFileData(cameraFile.getAbsolutePath());
            if (cameraFileList == null) {
                cameraFileList = new ArrayList<>();
            }
            cameraFileList.add(FileUtils.getFileByPath(cameraFile.getAbsolutePath())); // 创建成功的cameraFile都要记录
            cameraFile = null; // 解除引用，防止误删
        } else if (requestCode == ConsHelper.REQUEST_PICTURE) {
            // 相册
            File pictureFile = IntentResult.getPictureFile(data);
            if (pictureFile == null || FileUtils.isFileEmpty(pictureFile)) {
                ToastUtils.show(getString(R.string.file_no_exits));
                return;
            }
            adapter.addFileData(pictureFile.getAbsolutePath());
        }
    }

    @OnClick({R.id.cvAlbum, R.id.cvHappenAt, R.id.cvAddress, R.id.btnCommit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.cvAlbum: // 相册选取
                AlbumListActivity.goActivityBySelectAlbum(mActivity);
                break;
            case R.id.cvHappenAt: // 时间
                showDatePicker();
                break;
            case R.id.cvAddress: // 位置
                if (picture == null) return;
                MapSelectActivity.goActivity(mActivity, picture.getAddress(), picture.getLongitude(), picture.getLatitude());
                break;
            case R.id.btnCommit: // 提交
                checkCommit();
                break;
        }
    }

    private boolean isFromUpdate() {
        return getIntent().getIntExtra("from", ConsHelper.ACT_EDIT_FROM_ADD) == ConsHelper.ACT_EDIT_FROM_UPDATE;
    }

    private void refreshAlbum() {
        if (album == null || album.getId() == 0 || StringUtils.isEmpty(album.getTitle())) {
            tvAlbum.setText(R.string.please_select_album);
        } else {
            String title = String.format(Locale.getDefault(), getString(R.string.album_colon_space_holder), album.getTitle());
            tvAlbum.setText(title);
        }
    }

    private void showDatePicker() {
        if (picture == null) return;
        DialogHelper.showDateTimePicker(mActivity, TimeHelper.getJavaTimeByGo(picture.getHappenAt()), new DialogHelper.OnPickListener() {
            @Override
            public void onPick(long time) {
                picture.setHappenAt(TimeHelper.getGoTimeByJava(time));
                refreshDateView();
            }
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

    private void showImgSelect() {
        int pictureTotalCount = SPHelper.getVipLimit().getPictureTotalCount();
        if (pictureTotalCount > 0) {
            cameraFile = ResHelper.newImageCacheFile();
            PopupWindow popupWindow = ViewHelper.createPictureCameraPop(mActivity, cameraFile);
            PopUtils.show(popupWindow, root, Gravity.CENTER);
        } else {
            String string = getString(R.string.now_just_upload_holder_picture);
            ToastUtils.show(String.format(Locale.getDefault(), string, pictureTotalCount));
        }
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
            Picture body = ApiHelper.getPictureBody(picture.getAlbumId(), picture.getHappenAt(), ossPath, picture.getLongitude(), picture.getLatitude(), picture.getAddress(), picture.getCityId());
            pictureList.add(body);
        }
        PictureList pictureListBody = new PictureList();
        pictureListBody.setPictureList(pictureList);
        callAdd = new RetrofitHelper().call(API.class).notePictureListAdd(pictureListBody);
        MaterialDialog loading = getLoading(true);
        RetrofitHelper.enqueue(callAdd, loading, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                List<Picture> pictureList = data.getPictureList();
                int total = (pictureList == null) ? 0 : pictureList.size();
                String toast = String.format(Locale.getDefault(), mActivity.getString(R.string.success_push_holder_paper_picture), total);
                ToastUtils.show(toast);
                // event
                RxEvent<ArrayList<Album>> eventAlbum = new RxEvent<>(ConsHelper.EVENT_ALBUM_LIST_REFRESH, new ArrayList<Album>());
                RxBus.post(eventAlbum);
                RxEvent<ArrayList<Picture>> eventPicture = new RxEvent<>(ConsHelper.EVENT_PICTURE_LIST_REFRESH, new ArrayList<Picture>());
                RxBus.post(eventPicture);
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
                RxEvent<ArrayList<Album>> eventAlbum = new RxEvent<>(ConsHelper.EVENT_ALBUM_LIST_REFRESH, new ArrayList<Album>());
                RxBus.post(eventAlbum);
                Picture picture = data.getPicture();
                if (isChangeAlbum) {
                    RxEvent<ArrayList<Picture>> eventPicture = new RxEvent<>(ConsHelper.EVENT_PICTURE_LIST_REFRESH, new ArrayList<Picture>());
                    RxBus.post(eventPicture);
                } else {
                    RxEvent<Picture> eventPicture = new RxEvent<>(ConsHelper.EVENT_PICTURE_LIST_ITEM_REFRESH, picture);
                    RxBus.post(eventPicture);
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
