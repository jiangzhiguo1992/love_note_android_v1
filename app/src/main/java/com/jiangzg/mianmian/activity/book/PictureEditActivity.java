package com.jiangzg.mianmian.activity.book;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
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
import com.jiangzg.mianmian.R;
import com.jiangzg.mianmian.activity.common.MapSelectActivity;
import com.jiangzg.mianmian.activity.settings.HelpActivity;
import com.jiangzg.mianmian.adapter.ImgSquareEditAdapter;
import com.jiangzg.mianmian.adapter.ImgSquareShowAdapter;
import com.jiangzg.mianmian.base.BaseActivity;
import com.jiangzg.mianmian.domain.Album;
import com.jiangzg.mianmian.domain.Help;
import com.jiangzg.mianmian.domain.Picture;
import com.jiangzg.mianmian.domain.PictureList;
import com.jiangzg.mianmian.domain.Result;
import com.jiangzg.mianmian.domain.RxEvent;
import com.jiangzg.mianmian.helper.API;
import com.jiangzg.mianmian.helper.ApiHelper;
import com.jiangzg.mianmian.helper.ConsHelper;
import com.jiangzg.mianmian.helper.OssHelper;
import com.jiangzg.mianmian.helper.RecyclerHelper;
import com.jiangzg.mianmian.helper.ResHelper;
import com.jiangzg.mianmian.helper.RetrofitHelper;
import com.jiangzg.mianmian.helper.RxBus;
import com.jiangzg.mianmian.helper.SPHelper;
import com.jiangzg.mianmian.helper.TimeHelper;
import com.jiangzg.mianmian.helper.ViewHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;
import rx.Observable;
import rx.functions.Action1;

public class PictureEditActivity extends BaseActivity<PictureEditActivity> {

    private static final int TYPE_ADD = 0;
    private static final int TYPE_UPDATE = 1;

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
    @BindView(R.id.cvLocation)
    CardView cvLocation;
    @BindView(R.id.tvLocation)
    TextView tvLocation;
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
        Intent intent = new Intent(from, PictureEditActivity.class);
        intent.putExtra("type", TYPE_ADD);
        intent.putExtra("album", album);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    public static void goActivity(Activity from, Album album, Picture picture) {
        if (picture == null) {
            goActivity(from, album);
        } else if (!picture.isMine()) {
            ToastUtils.show(from.getString(R.string.can_operation_self_create_picture));
            return;
        }
        Intent intent = new Intent(from, PictureEditActivity.class);
        intent.putExtra("type", TYPE_UPDATE);
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
    protected void initView(Bundle state) {
        ViewHelper.initTopBar(mActivity, tb, getString(R.string.picture), true);
    }

    @Override
    protected void initData(Bundle state) {
        album = getIntent().getParcelableExtra("album");
        refreshAlbum();
        boolean typeUpdate = isTypeUpdate();
        if (typeUpdate) {
            picture = mActivity.getIntent().getParcelableExtra("picture");
        }
        if (picture == null) {
            picture = new Picture();
        }
        if (picture.getHappenAt() == 0) {
            picture.setHappenAt(TimeHelper.getGoTimeByJava(DateUtils.getCurrentLong()));
        }
        // view
        refreshDateView();
        refreshLocationView();
        // recycler
        if (typeUpdate) {
            // 更新
            ImgSquareShowAdapter imgAdapter = new ImgSquareShowAdapter(mActivity, 1);
            recyclerHelper = new RecyclerHelper(mActivity)
                    .initRecycler(rv)
                    .initLayoutManager(new GridLayoutManager(mActivity, 1))
                    .initAdapter(imgAdapter)
                    .setAdapter();
            List<String> pictureList = new ArrayList<>();
            pictureList.add(picture.getContentImage());
            imgAdapter.setNewData(pictureList);
        } else {
            // 添加
            int pictureLimitCount = SPHelper.getLimit().getPictureCount();
            int spanCount = pictureLimitCount > 3 ? 3 : pictureLimitCount;
            final ImgSquareEditAdapter imgAdapter = new ImgSquareEditAdapter(mActivity, spanCount, pictureLimitCount);
            imgAdapter.setOnAddClick(new ImgSquareEditAdapter.OnAddClickListener() {
                @Override
                public void onAdd() {
                    showImgSelect();
                }
            });
            recyclerHelper = new RecyclerHelper(mActivity)
                    .initRecycler(rv)
                    .initLayoutManager(new GridLayoutManager(mActivity, spanCount))
                    .initAdapter(imgAdapter)
                    .setAdapter();
        }
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
                if (info == null) return;
                picture.setLatitude(info.getLatitude());
                picture.setLongitude(info.getLongitude());
                picture.setAddress(info.getAddress());
                picture.setCityId(info.getCityId());
                refreshLocationView();
            }
        });
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
        // event
        RxBus.unregister(ConsHelper.EVENT_ALBUM_SELECT, obSelectAlbum);
        RxBus.unregister(ConsHelper.EVENT_MAP_SELECT, obSelectMap);
        // 创建成功的cameraFile都要删除
        ResHelper.deleteFileListInBackground(cameraFileList);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuHelp: // 帮助
                HelpActivity.goActivity(mActivity, Help.INDEX_PICTURE_EDIT);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick({R.id.cvAlbum, R.id.cvHappenAt, R.id.cvLocation, R.id.btnCommit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.cvAlbum: // 相册选取
                AlbumListActivity.goActivityBySelectAlbum(mActivity);
                break;
            case R.id.cvHappenAt: // 时间
                showDatePicker();
                break;
            case R.id.cvLocation: // 位置
                MapSelectActivity.goActivity(mActivity, picture.getAddress(), picture.getLongitude(), picture.getLatitude());
                break;
            case R.id.btnCommit: // 提交
                checkCommit();
                break;
        }
    }

    private boolean isTypeUpdate() {
        return getIntent().getIntExtra("type", TYPE_ADD) == TYPE_UPDATE;
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
        Calendar calendar = DateUtils.getCalendar(TimeHelper.getJavaTimeByGo(picture.getHappenAt()));
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog picker = new DatePickerDialog(mActivity, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar instance = DateUtils.getCurrentCalendar();
                instance.set(year, month, dayOfMonth);
                picture.setHappenAt(TimeHelper.getGoTimeByJava(instance.getTimeInMillis()));
                refreshDateView();
            }
        }, year, month, day);
        picker.show();
    }

    private void refreshDateView() {
        String happenShow = TimeHelper.getTimeShowCnSpace_HM_MD_YMD_ByGo(picture.getHappenAt());
        String format = String.format(Locale.getDefault(), getString(R.string.take_camera_in_colon_space_holder), happenShow);
        tvHappenAt.setText(format);
    }

    private void refreshLocationView() {
        String location = StringUtils.isEmpty(picture.getAddress()) ? getString(R.string.now_no) : picture.getAddress();
        tvLocation.setText(location);
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
        if ((album == null || album.getId() == 0) && picture.getAlbumId() == 0) {
            ToastUtils.show(getString(R.string.please_select_album));
            return;
        } else {
            if (album != null && album.getId() != 0) {
                picture.setAlbumId(album.getId());
            }
        }
        if (picture.getHappenAt() == 0) {
            ToastUtils.show(getString(R.string.please_select_time));
            return;
        }
        if (isTypeUpdate()) {
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
        if (ossPathList == null || ossPathList.size() <= 0) return;
        List<Picture> pictureList = new ArrayList<>();
        for (String ossPath : ossPathList) {
            Picture body = ApiHelper.getPictureBody(picture.getAlbumId(), picture.getHappenAt(), ossPath, picture.getLongitude(), picture.getLatitude(), picture.getAddress(), picture.getCityId());
            pictureList.add(body);
        }
        PictureList pictureListBody = new PictureList();
        pictureListBody.setPictureList(pictureList);
        callAdd = new RetrofitHelper().call(API.class).pictureListAdd(pictureListBody);
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
            public void onFailure(String errMsg) {
            }
        });
    }

    private void commitUpdate() {
        callUpdate = new RetrofitHelper().call(API.class).pictureUpdate(picture);
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
            public void onFailure(String errMsg) {
            }
        });
    }
}
