package com.jiangzg.mianmian.activity.book;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.jiangzg.base.common.FileUtils;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.base.component.IntentFactory;
import com.jiangzg.base.component.IntentResult;
import com.jiangzg.base.component.ProviderUtils;
import com.jiangzg.base.system.PermUtils;
import com.jiangzg.base.time.DateUtils;
import com.jiangzg.base.time.TimeUnit;
import com.jiangzg.base.view.ToastUtils;
import com.jiangzg.mianmian.R;
import com.jiangzg.mianmian.activity.settings.HelpActivity;
import com.jiangzg.mianmian.base.BaseActivity;
import com.jiangzg.mianmian.domain.Album;
import com.jiangzg.mianmian.domain.Audio;
import com.jiangzg.mianmian.domain.Help;
import com.jiangzg.mianmian.domain.Result;
import com.jiangzg.mianmian.domain.RxEvent;
import com.jiangzg.mianmian.helper.API;
import com.jiangzg.mianmian.helper.ConsHelper;
import com.jiangzg.mianmian.helper.DialogHelper;
import com.jiangzg.mianmian.helper.OssHelper;
import com.jiangzg.mianmian.helper.RetrofitHelper;
import com.jiangzg.mianmian.helper.RxBus;
import com.jiangzg.mianmian.helper.SPHelper;
import com.jiangzg.mianmian.helper.TimeHelper;
import com.jiangzg.mianmian.helper.ViewHelper;

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
    @BindView(R.id.cvHappenAt)
    CardView cvHappenAt;
    @BindView(R.id.tvHappenAt)
    TextView tvHappenAt;
    @BindView(R.id.cvAudio)
    CardView cvAudio;
    @BindView(R.id.tvAudio)
    TextView tvAudio;
    @BindView(R.id.btnPublish)
    Button btnPublish;

    private Audio audio;
    private Call<Result> callAdd;
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
    protected void initView(Bundle state) {
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
        // date
        refreshDateView();
        // input
        etTitle.setText(audio.getTitle());
    }

    @Override
    protected void initData(Bundle state) {

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
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;
        if (requestCode == ConsHelper.REQUEST_AUDIO) {
            // file
            audioFile = IntentResult.getAudioFile(data);
            // duration
            Map<String, String> audioInfo = ProviderUtils.getAudioInfo(data == null ? null : data.getData());
            String duration = audioInfo.get(MediaStore.Audio.Media.DURATION);
            if (StringUtils.isNumber(duration)) {
                audio.setDuration((int) TimeHelper.getGoTimeByJava(Integer.parseInt(duration)));
            }
            // view
            refreshAudioView();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuHelp: // 帮助
                HelpActivity.goActivity(mActivity, Help.INDEX_AUDIO_EDIT);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick({R.id.cvHappenAt, R.id.cvAudio, R.id.btnPublish})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.cvHappenAt: // 日期
                showDateTimePicker();
                break;
            case R.id.cvAudio: // 音频
                selectAudio();
                break;
            case R.id.btnPublish: // 发表
                checkPush();
                break;
        }
    }

    private void showDateTimePicker() {
        DialogHelper.showDateTimePicker(mActivity, TimeHelper.getJavaTimeByGo(audio.getHappenAt()), new DialogHelper.OnPickListener() {
            @Override
            public void onPick(long time) {
                audio.setHappenAt(TimeHelper.getGoTimeByJava(time));
                refreshDateView();
            }
        });
    }

    private void refreshDateView() {
        String happen = TimeHelper.getTimeShowCn_HM_MD_YMD_ByGo(audio.getHappenAt());
        tvHappenAt.setText(happen);
    }

    private void selectAudio() {
        PermUtils.requestPermissions(mActivity, ConsHelper.REQUEST_APP_INFO, PermUtils.appInfo, new PermUtils.OnPermissionListener() {
            @Override
            public void onPermissionGranted(int requestCode, String[] permissions) {
                Intent intent = IntentFactory.getAudio();
                ActivityTrans.startResult(mActivity, intent, ConsHelper.REQUEST_AUDIO);
            }

            @Override
            public void onPermissionDenied(int requestCode, String[] permissions) {
                DialogHelper.showGoPermDialog(mActivity);
            }
        });
    }

    private void refreshAudioView() {
        String year = mActivity.getString(R.string.year);
        String month = mActivity.getString(R.string.month);
        String dayT = mActivity.getString(R.string.dayT);
        String hour = mActivity.getString(R.string.hour_short);
        String minute = mActivity.getString(R.string.minute_short);
        String second = mActivity.getString(R.string.second);
        TimeUnit timeUnit = TimeUnit.convertTime2Unit(TimeHelper.getJavaTimeByGo(audio.getDuration()));
        String duration = timeUnit.getAllShow(true, true, true, true, true, true, year, month, dayT, hour, minute, second);
        if (StringUtils.isEmpty(duration) && audioFile == null) {
            tvAudio.setText(R.string.please_select_audio);
            return;
        }
        duration = StringUtils.isEmpty(duration) ? "--" : duration;
        tvAudio.setText(duration);
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
        callAdd = new RetrofitHelper().call(API.class).audioAdd(audio);
        MaterialDialog loading = getLoading(true);
        RetrofitHelper.enqueue(callAdd, loading, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                // event
                RxEvent<ArrayList<Audio>> event = new RxEvent<>(ConsHelper.EVENT_AUDIO_LIST_REFRESH, new ArrayList<Audio>());
                RxBus.post(event);
                mActivity.finish();
            }

            @Override
            public void onFailure(String errMsg) {
            }
        });
    }


}
