package com.jiangzg.lovenote.controller.activity.note;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.jiangzg.base.common.DateUtils;
import com.jiangzg.base.common.FileUtils;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.base.component.ProviderUtils;
import com.jiangzg.base.media.BitmapUtils;
import com.jiangzg.base.media.VideoUtils;
import com.jiangzg.base.system.LocationInfo;
import com.jiangzg.base.view.ToastUtils;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.controller.activity.base.BaseActivity;
import com.jiangzg.lovenote.controller.activity.common.MapSelectActivity;
import com.jiangzg.lovenote.helper.common.OssHelper;
import com.jiangzg.lovenote.helper.common.ResHelper;
import com.jiangzg.lovenote.helper.common.RxBus;
import com.jiangzg.lovenote.helper.common.SPHelper;
import com.jiangzg.lovenote.helper.common.ShowHelper;
import com.jiangzg.lovenote.helper.common.TimeHelper;
import com.jiangzg.lovenote.helper.media.PickHelper;
import com.jiangzg.lovenote.helper.system.RetrofitHelper;
import com.jiangzg.lovenote.helper.view.DialogHelper;
import com.jiangzg.lovenote.helper.view.ViewHelper;
import com.jiangzg.lovenote.main.MyApp;
import com.jiangzg.lovenote.model.api.API;
import com.jiangzg.lovenote.model.api.Result;
import com.jiangzg.lovenote.model.entity.Video;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;
import rx.Observable;

public class VideoEditActivity extends BaseActivity<VideoEditActivity> {

    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.etTitle)
    EditText etTitle;
    @BindView(R.id.llHappenAt)
    LinearLayout llHappenAt;
    @BindView(R.id.tvHappenAt)
    TextView tvHappenAt;
    @BindView(R.id.llAddress)
    LinearLayout llAddress;
    @BindView(R.id.tvAddress)
    TextView tvAddress;
    @BindView(R.id.llDuration)
    LinearLayout llDuration;
    @BindView(R.id.tvDuration)
    TextView tvDuration;

    private Video video;
    private File thumbFile;
    private File videoFile;

    public static void goActivity(Activity from) {
        Intent intent = new Intent(from, VideoEditActivity.class);
        // intent.putExtra();
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_video_edit;
    }

    @Override
    protected void initView(Intent intent, Bundle state) {
        ViewHelper.initTopBar(mActivity, tb, getString(R.string.video), true);
        // init
        video = new Video();
        video.setHappenAt(TimeHelper.getGoTimeByJava(DateUtils.getCurrentLong()));
        // etTitle
        String format = getString(R.string.please_input_title_no_over_holder_text);
        String hint = String.format(Locale.getDefault(), format, SPHelper.getLimit().getVideoTitleLength());
        etTitle.setHint(hint);
        etTitle.setText(video.getTitle());
        // date
        refreshDateView();
        // location
        refreshLocationView();
        // duration
        refreshVideoView();
    }

    @Override
    protected void initData(Intent intent, Bundle state) {
        // event
        Observable<LocationInfo> obSelectMap = RxBus.register(RxBus.EVENT_MAP_SELECT, info -> {
            if (info == null || video == null) return;
            video.setLatitude(info.getLatitude());
            video.setLongitude(info.getLongitude());
            video.setAddress(info.getAddress());
            video.setCityId(info.getCityId());
            refreshLocationView();
        });
        pushBus(RxBus.EVENT_MAP_SELECT, obSelectMap);
    }

    @Override
    protected void onFinish(Bundle state) {
        // 记得删除临时文件
        ResHelper.deleteFileInBackground(thumbFile);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.commit, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK || video == null) return;
        if (requestCode == BaseActivity.REQUEST_VIDEO) {
            // file
            videoFile = PickHelper.getResultFile(mActivity, data);
            if (videoFile == null || FileUtils.isFileEmpty(videoFile)) {
                ToastUtils.show(getString(R.string.file_no_exits));
                return;
            }
            // duration
            Map<String, String> videoInfo = ProviderUtils.getVideoInfo(data == null ? null : data.getData());
            String duration = videoInfo.get(MediaStore.Video.Media.DURATION);
            if (video != null && StringUtils.isNumber(duration) && duration != null) {
                video.setDuration((int) TimeHelper.getGoTimeByJava(Integer.parseInt(duration)));
            }
            duration = VideoUtils.getVideoDuration(videoFile.getAbsolutePath());
            if (video != null && StringUtils.isNumber(duration) && duration != null) {
                video.setDuration((int) TimeHelper.getGoTimeByJava(Integer.parseInt(duration)));
            }
            // thumb
            final MaterialDialog loading = mActivity.getLoading(true);
            loading.show();
            if (!FileUtils.isFileEmpty(thumbFile)) {
                // 临时的上次选的缩略图要删掉
                File delFile = thumbFile;
                thumbFile = null;
                ResHelper.deleteFileInBackground(delFile);
            }
            MyApp.get().getThread().execute(() -> {
                thumbFile = ResHelper.newImageCacheFile();
                FileUtils.createFileByDeleteOldFile(thumbFile);
                Bitmap videoThumbnail = ThumbnailUtils.createVideoThumbnail(videoFile.getAbsolutePath(), MediaStore.Images.Thumbnails.MINI_KIND);
                BitmapUtils.saveBitmap(videoThumbnail, thumbFile.getAbsolutePath(), Bitmap.CompressFormat.JPEG, true);
                MyApp.get().getHandler().post(() -> {
                    loading.dismiss();
                    refreshVideoView();
                });
            });
            // view
            refreshVideoView();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuCommit: // 提交
                checkPush();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick({R.id.llHappenAt, R.id.llAddress, R.id.llDuration})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.llHappenAt: // 日期
                showDateTimePicker();
                break;
            case R.id.llAddress: // 地址
                if (video == null) return;
                MapSelectActivity.goActivity(mActivity, video.getAddress(), video.getLongitude(), video.getLatitude());
                break;
            case R.id.llDuration: // 视频
                PickHelper.selectVideo(mActivity, 1);
                break;
        }
    }

    private void showDateTimePicker() {
        if (video == null) return;
        DialogHelper.showDateTimePicker(mActivity, TimeHelper.getJavaTimeByGo(video.getHappenAt()), time -> {
            video.setHappenAt(TimeHelper.getGoTimeByJava(time));
            refreshDateView();
        });
    }

    private void refreshDateView() {
        if (video == null) return;
        String happen = TimeHelper.getTimeShowLine_HM_MDHM_YMDHM_ByGo(video.getHappenAt());
        tvHappenAt.setText(String.format(Locale.getDefault(), getString(R.string.time_colon_space_holder), happen));
    }

    private void refreshLocationView() {
        if (video == null) return;
        String address = StringUtils.isEmpty(video.getAddress()) ? getString(R.string.now_no) : video.getAddress();
        tvAddress.setText(String.format(Locale.getDefault(), getString(R.string.address_colon_space_holder), address));
    }

    private void refreshVideoView() {
        if (video == null) return;
        // duration
        if (video.getDuration() == 0 || videoFile == null) {
            tvDuration.setText(String.format(Locale.getDefault(), getString(R.string.duration_colon_space_holder), getString(R.string.please_select_video)));
            return;
        }
        String duration = ShowHelper.getDurationShow(video.getDuration());
        tvDuration.setText(String.format(Locale.getDefault(), getString(R.string.duration_colon_space_holder), duration));
    }

    private void checkPush() {
        if (video == null) return;
        String title = etTitle.getText().toString();
        if (StringUtils.isEmpty(title)) {
            ToastUtils.show(etTitle.getHint().toString());
            return;
        } else if (title.length() > SPHelper.getLimit().getVideoTitleLength()) {
            ToastUtils.show(etTitle.getHint().toString());
            return;
        }
        video.setTitle(title);
        if (FileUtils.isFileEmpty(videoFile)) {
            // 没有视频文件
            ToastUtils.show(getString(R.string.please_select_video));
            return;
        }
        if (!FileUtils.isFileEmpty(thumbFile)) {
            // 有缩略图
            ossUploadVideoThumb(thumbFile);
        } else {
            // 没缩略图
            ossUploadVideo(videoFile);
        }
    }

    private void ossUploadVideoThumb(File thumbFile) {
        if (video == null) return;
        OssHelper.uploadVideoThumb(mActivity, thumbFile, new OssHelper.OssUploadCallBack() {
            @Override
            public void success(File source, String ossPath) {
                video.setContentThumb(ossPath);
                ossUploadVideo(videoFile);
            }

            @Override
            public void failure(File source, String errMsg) {
                // 失败了就直接上传video 不要thumb了
                ossUploadVideo(videoFile);
            }
        });
    }

    private void ossUploadVideo(File videoFile) {
        if (video == null) return;
        OssHelper.uploadVideo(mActivity, videoFile, new OssHelper.OssUploadCallBack() {
            @Override
            public void success(File source, String ossPath) {
                video.setContentVideo(ossPath);
                addApi();
            }

            @Override
            public void failure(File source, String errMsg) {
                // 不能删除本地文件，因为不是自己创建的
            }
        });

    }

    private void addApi() {
        if (video == null) return;
        Call<Result> api = new RetrofitHelper().call(API.class).noteVideoAdd(video);
        RetrofitHelper.enqueue(api, getLoading(true), new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                // event
                RxBus.post(new RxBus.Event<>(RxBus.EVENT_VIDEO_LIST_REFRESH, new ArrayList<>()));
                mActivity.finish();
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
            }
        });
        pushApi(api);
    }

}
