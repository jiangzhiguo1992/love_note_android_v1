package com.jiangzg.mianmian.activity.book;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.jiangzg.base.common.FileUtils;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.base.component.IntentFactory;
import com.jiangzg.base.component.IntentResult;
import com.jiangzg.base.component.ProviderUtils;
import com.jiangzg.base.media.BitmapUtils;
import com.jiangzg.base.media.VideoUtils;
import com.jiangzg.base.system.LocationInfo;
import com.jiangzg.base.system.PermUtils;
import com.jiangzg.base.time.DateUtils;
import com.jiangzg.base.view.ToastUtils;
import com.jiangzg.mianmian.R;
import com.jiangzg.mianmian.activity.common.MapSelectActivity;
import com.jiangzg.mianmian.activity.settings.HelpActivity;
import com.jiangzg.mianmian.base.BaseActivity;
import com.jiangzg.mianmian.base.MyApp;
import com.jiangzg.mianmian.domain.Help;
import com.jiangzg.mianmian.domain.Result;
import com.jiangzg.mianmian.domain.RxEvent;
import com.jiangzg.mianmian.domain.Video;
import com.jiangzg.mianmian.helper.API;
import com.jiangzg.mianmian.helper.ConsHelper;
import com.jiangzg.mianmian.helper.DialogHelper;
import com.jiangzg.mianmian.helper.OssHelper;
import com.jiangzg.mianmian.helper.ResHelper;
import com.jiangzg.mianmian.helper.RetrofitHelper;
import com.jiangzg.mianmian.helper.RxBus;
import com.jiangzg.mianmian.helper.SPHelper;
import com.jiangzg.mianmian.helper.TimeHelper;
import com.jiangzg.mianmian.helper.ViewHelper;
import com.jiangzg.mianmian.view.FrescoView;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;
import rx.Observable;
import rx.functions.Action1;

public class VideoEditActivity extends BaseActivity<VideoEditActivity> {

    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.etTitle)
    EditText etTitle;
    @BindView(R.id.cvHappenAt)
    CardView cvHappenAt;
    @BindView(R.id.tvHappenAt)
    TextView tvHappenAt;
    @BindView(R.id.cvAddress)
    CardView cvAddress;
    @BindView(R.id.tvAddress)
    TextView tvAddress;
    @BindView(R.id.cvVideo)
    CardView cvVideo;
    @BindView(R.id.ivThumb)
    FrescoView ivThumb;
    @BindView(R.id.tvSelect)
    TextView tvSelect;
    @BindView(R.id.ivPlay)
    ImageView ivPlay;
    @BindView(R.id.btnPublish)
    Button btnPublish;

    private Video video;
    private Call<Result> callAdd;
    private Observable<LocationInfo> obSelectMap;
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
        // date
        refreshDateView();
        // location
        refreshLocationView();
        // video
        refreshVideoView();
        // input
        etTitle.setText(video.getTitle());
    }

    @Override
    protected void initData(Intent intent, Bundle state) {
        // event
        obSelectMap = RxBus.register(ConsHelper.EVENT_MAP_SELECT, new Action1<LocationInfo>() {
            @Override
            public void call(LocationInfo info) {
                if (info == null) return;
                video.setLatitude(info.getLatitude());
                video.setLongitude(info.getLongitude());
                video.setAddress(info.getAddress());
                video.setCityId(info.getCityId());
                refreshLocationView();
            }
        });
    }

    @Override
    protected void onFinish(Bundle state) {
        RetrofitHelper.cancel(callAdd);
        RxBus.unregister(ConsHelper.EVENT_MAP_SELECT, obSelectMap);
        // 记得删除临时文件
        ResHelper.deleteFileInBackground(thumbFile);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.help, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;
        if (requestCode == ConsHelper.REQUEST_VIDEO) {
            // file
            videoFile = IntentResult.getVideoFile(data);
            // duration
            Map<String, String> videoInfo = ProviderUtils.getVideoInfo(data == null ? null : data.getData());
            String duration = videoInfo.get(MediaStore.Video.Media.DURATION);
            if (StringUtils.isNumber(duration)) {
                video.setDuration((int) TimeHelper.getGoTimeByJava(Integer.parseInt(duration)));
            }
            if (video.getDuration() <= 0 && videoFile != null) {
                duration = VideoUtils.getVideoDuration(videoFile.getAbsolutePath());
                if (StringUtils.isNumber(duration)) {
                    video.setDuration((int) TimeHelper.getGoTimeByJava(Integer.parseInt(duration)));
                }
            }
            // view
            refreshVideoView();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuHelp: // 帮助
                HelpActivity.goActivity(mActivity, Help.INDEX_BOOK_VIDEO_EDIT);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick({R.id.cvHappenAt, R.id.cvAddress, R.id.cvVideo, R.id.btnPublish})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.cvHappenAt: // 日期
                showDateTimePicker();
                break;
            case R.id.cvAddress: // 地址
                MapSelectActivity.goActivity(mActivity, video.getAddress(), video.getLongitude(), video.getLatitude());
                break;
            case R.id.cvVideo: // 视频
                selectVideo();
                break;
            case R.id.btnPublish: // 发表
                checkPush();
                break;
        }
    }

    private void showDateTimePicker() {
        DialogHelper.showDateTimePicker(mActivity, TimeHelper.getJavaTimeByGo(video.getHappenAt()), new DialogHelper.OnPickListener() {
            @Override
            public void onPick(long time) {
                video.setHappenAt(TimeHelper.getGoTimeByJava(time));
                refreshDateView();
            }
        });
    }

    private void refreshDateView() {
        String happen = TimeHelper.getTimeShowLine_HM_MDHM_YMDHM_ByGo(video.getHappenAt());
        tvHappenAt.setText(happen);
    }

    private void refreshLocationView() {
        String location = StringUtils.isEmpty(video.getAddress()) ? getString(R.string.now_no) : video.getAddress();
        tvAddress.setText(location);
    }

    private void selectVideo() {
        PermUtils.requestPermissions(mActivity, ConsHelper.REQUEST_APP_INFO, PermUtils.appInfo, new PermUtils.OnPermissionListener() {
            @Override
            public void onPermissionGranted(int requestCode, String[] permissions) {
                Intent intent = IntentFactory.getVideo();
                ActivityTrans.startResult(mActivity, intent, ConsHelper.REQUEST_VIDEO);
            }

            @Override
            public void onPermissionDenied(int requestCode, String[] permissions) {
                DialogHelper.showGoPermDialog(mActivity);
            }
        });
    }

    private void refreshVideoView() {
        ivThumb.setVisibility(View.GONE);
        if (FileUtils.isFileEmpty(videoFile)) {
            ivPlay.setVisibility(View.GONE);
            tvSelect.setVisibility(View.VISIBLE);
            return;
        }
        ivPlay.setVisibility(View.VISIBLE);
        tvSelect.setVisibility(View.GONE);
        final MaterialDialog loading = mActivity.getLoading(true);
        loading.show();
        // 缩略图
        if (!FileUtils.isFileEmpty(thumbFile)) {
            // 临时的上次选的缩略图要删掉
            File delFile = thumbFile;
            thumbFile = null;
            ResHelper.deleteFileInBackground(delFile);
        }
        MyApp.get().getThread().execute(new Runnable() {
            @Override
            public void run() {
                thumbFile = ResHelper.newImageCacheFile();
                FileUtils.createFileByDeleteOldFile(thumbFile);
                Bitmap videoThumbnail = ThumbnailUtils.createVideoThumbnail(videoFile.getAbsolutePath(), MediaStore.Images.Thumbnails.MINI_KIND);
                BitmapUtils.saveBitmap(videoThumbnail, thumbFile.getAbsolutePath(), Bitmap.CompressFormat.JPEG, true);
                MyApp.get().getHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        loading.dismiss();
                        if (FileUtils.isFileEmpty(thumbFile)) {
                            return;
                        }
                        ivThumb.setVisibility(View.VISIBLE);
                        ivThumb.setDataFile(thumbFile);
                    }
                });
            }
        });
    }

    private void checkPush() {
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
        callAdd = new RetrofitHelper().call(API.class).videoAdd(video);
        MaterialDialog loading = getLoading(true);
        RetrofitHelper.enqueue(callAdd, loading, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                // event
                RxEvent<ArrayList<Video>> event = new RxEvent<>(ConsHelper.EVENT_VIDEO_LIST_REFRESH, new ArrayList<Video>());
                RxBus.post(event);
                mActivity.finish();
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
            }
        });
    }

}
