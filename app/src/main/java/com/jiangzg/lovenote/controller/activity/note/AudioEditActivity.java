package com.jiangzg.lovenote.controller.activity.note;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jiangzg.base.common.DateUtils;
import com.jiangzg.base.common.FileUtils;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.base.component.IntentFactory;
import com.jiangzg.base.component.IntentResult;
import com.jiangzg.base.component.ProviderUtils;
import com.jiangzg.base.media.VideoUtils;
import com.jiangzg.base.system.PermUtils;
import com.jiangzg.base.view.ToastUtils;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.controller.activity.base.BaseActivity;
import com.jiangzg.lovenote.helper.common.OssHelper;
import com.jiangzg.lovenote.helper.common.RxBus;
import com.jiangzg.lovenote.helper.common.SPHelper;
import com.jiangzg.lovenote.helper.common.ShowHelper;
import com.jiangzg.lovenote.helper.common.TimeHelper;
import com.jiangzg.lovenote.helper.system.RetrofitHelper;
import com.jiangzg.lovenote.helper.view.DialogHelper;
import com.jiangzg.lovenote.helper.view.ViewHelper;
import com.jiangzg.lovenote.model.api.API;
import com.jiangzg.lovenote.model.api.Result;
import com.jiangzg.lovenote.model.entity.Audio;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;

public class AudioEditActivity extends BaseActivity<AudioEditActivity> {

    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.etTitle)
    EditText etTitle;
    @BindView(R.id.llHappenAt)
    LinearLayout llHappenAt;
    @BindView(R.id.tvHappenAt)
    TextView tvHappenAt;
    @BindView(R.id.llDuration)
    LinearLayout llDuration;
    @BindView(R.id.tvDuration)
    TextView tvDuration;

    private Audio audio;
    private File audioFile;

    public static void goActivity(Activity from) {
        Intent intent = new Intent(from, AudioEditActivity.class);
        // intent.putExtra();
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_audio_edit;
    }

    @Override
    protected void initView(Intent intent, Bundle state) {
        ViewHelper.initTopBar(mActivity, tb, getString(R.string.audio), true);
        // init
        audio = new Audio();
        if (audio.getHappenAt() == 0) {
            audio.setHappenAt(TimeHelper.getGoTimeByJava(DateUtils.getCurrentLong()));
        }
        // etTitle
        String format = getString(R.string.please_input_title_no_over_holder_text);
        String hint = String.format(Locale.getDefault(), format, SPHelper.getLimit().getAudioTitleLength());
        etTitle.setHint(hint);
        etTitle.setText(audio.getTitle());
        // date
        refreshDateView();
        // duration
        refreshAudioView();
    }

    @Override
    protected void initData(Intent intent, Bundle state) {
    }

    @Override
    protected void onFinish(Bundle state) {
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.commit, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK || audio == null) return;
        if (requestCode == BaseActivity.REQUEST_AUDIO) {
            // file
            audioFile = IntentResult.getAudioFile(data);
            if (audioFile == null || FileUtils.isFileEmpty(audioFile)) {
                ToastUtils.show(getString(R.string.file_no_exits));
                return;
            }
            // duration
            Map<String, String> audioInfo = ProviderUtils.getAudioInfo(data == null ? null : data.getData());
            String duration = audioInfo.get(MediaStore.Audio.Media.DURATION);
            if (audio != null && StringUtils.isNumber(duration) && duration != null) {
                audio.setDuration((int) TimeHelper.getGoTimeByJava(Integer.parseInt(duration)));
            }
            duration = VideoUtils.getVideoDuration(audioFile.getAbsolutePath());
            if (audio != null && StringUtils.isNumber(duration) && duration != null) {
                audio.setDuration((int) TimeHelper.getGoTimeByJava(Integer.parseInt(duration)));
            }
            // view
            refreshAudioView();
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

    @OnClick({R.id.llHappenAt, R.id.llDuration})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.llHappenAt: // 日期
                showDateTimePicker();
                break;
            case R.id.llDuration: // 音频
                selectAudio();
                break;
        }
    }

    private void showDateTimePicker() {
        if (audio == null) return;
        DialogHelper.showDateTimePicker(mActivity, TimeHelper.getJavaTimeByGo(audio.getHappenAt()), time -> {
            audio.setHappenAt(TimeHelper.getGoTimeByJava(time));
            refreshDateView();
        });
    }

    private void refreshDateView() {
        if (audio == null) return;
        String happen = TimeHelper.getTimeShowLine_HM_MDHM_YMDHM_ByGo(audio.getHappenAt());
        tvHappenAt.setText(String.format(Locale.getDefault(), getString(R.string.time_colon_space_holder), happen));
    }

    private void selectAudio() {
        PermUtils.requestPermissions(mActivity, BaseActivity.REQUEST_APP_INFO, PermUtils.appInfo, new PermUtils.OnPermissionListener() {
            @Override
            public void onPermissionGranted(int requestCode, String[] permissions) {
                Intent intent = IntentFactory.getAudio();
                ActivityTrans.startResult(mActivity, intent, BaseActivity.REQUEST_AUDIO);
            }

            @Override
            public void onPermissionDenied(int requestCode, String[] permissions) {
                DialogHelper.showGoPermDialog(mActivity);
            }
        });
    }

    private void refreshAudioView() {
        if (audio == null) return;
        if (audio.getDuration() == 0 || audioFile == null) {
            tvDuration.setText(String.format(Locale.getDefault(), getString(R.string.duration_colon_space_holder), getString(R.string.please_select_audio)));
            return;
        }
        String duration = ShowHelper.getDurationShow(audio.getDuration());
        tvDuration.setText(String.format(Locale.getDefault(), getString(R.string.duration_colon_space_holder), duration));
    }

    private void checkPush() {
        if (audio == null) return;
        String title = etTitle.getText().toString();
        if (StringUtils.isEmpty(title)) {
            ToastUtils.show(etTitle.getHint().toString());
            return;
        } else if (title.length() > SPHelper.getLimit().getAudioTitleLength()) {
            ToastUtils.show(etTitle.getHint().toString());
            return;
        } else if (FileUtils.isFileEmpty(audioFile)) {
            ToastUtils.show(getString(R.string.please_select_audio));
            return;
        }
        audio.setTitle(title);
        ossUploadAudio(audioFile);
    }

    private void ossUploadAudio(File audioFile) {
        if (audio == null) return;
        OssHelper.uploadAudio(mActivity, audioFile, new OssHelper.OssUploadCallBack() {
            @Override
            public void success(File source, String ossPath) {
                audio.setContentAudio(ossPath);
                addApi();
            }

            @Override
            public void failure(File source, String errMsg) {
                // 不能删除本地文件，因为不是自己创建的
            }
        });
    }

    private void addApi() {
        if (audio == null) return;
        Call<Result> api = new RetrofitHelper().call(API.class).noteAudioAdd(audio);
        RetrofitHelper.enqueue(api, getLoading(true), new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                // event
                RxBus.post(new RxBus.Event<>(RxBus.EVENT_AUDIO_LIST_REFRESH, new ArrayList<>()));
                mActivity.finish();
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
            }
        });
        pushApi(api);
    }

}
