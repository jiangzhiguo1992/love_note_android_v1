package com.jiangzg.lovenote.controller.activity.note;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.jiangzg.base.common.FileUtils;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.base.system.InputUtils;
import com.jiangzg.base.view.ToastUtils;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.controller.activity.base.BaseActivity;
import com.jiangzg.lovenote.controller.activity.more.VipActivity;
import com.jiangzg.lovenote.controller.activity.settings.HelpActivity;
import com.jiangzg.lovenote.controller.adapter.note.WhisperAdapter;
import com.jiangzg.lovenote.helper.common.OssHelper;
import com.jiangzg.lovenote.helper.common.SPHelper;
import com.jiangzg.lovenote.helper.media.PickHelper;
import com.jiangzg.lovenote.helper.system.RetrofitHelper;
import com.jiangzg.lovenote.helper.view.RecyclerHelper;
import com.jiangzg.lovenote.helper.view.ViewHelper;
import com.jiangzg.lovenote.model.api.API;
import com.jiangzg.lovenote.model.api.Result;
import com.jiangzg.lovenote.model.entity.Whisper;
import com.jiangzg.lovenote.view.GSwipeRefreshLayout;

import java.io.File;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import retrofit2.Call;

public class WhisperListActivity extends BaseActivity<WhisperListActivity> {

    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.tvCurrentChannel)
    TextView tvCurrentChannel;
    @BindView(R.id.etChannel)
    EditText etChannel;
    @BindView(R.id.btnChangeChannel)
    Button btnChangeChannel;
    @BindView(R.id.rv)
    RecyclerView rv;
    @BindView(R.id.srl)
    GSwipeRefreshLayout srl;
    @BindView(R.id.ivImgAdd)
    ImageView ivImgAdd;
    @BindView(R.id.ivTextAdd)
    ImageView ivTextAdd;
    @BindView(R.id.etContent)
    EditText etContent;

    private String channel;
    private int limitChannelLength;
    private RecyclerHelper recyclerHelper;
    private int page = 0, limitContentLength;

    public static void goActivity(Activity from) {
        Intent intent = new Intent(from, WhisperListActivity.class);
        // intent.putExtra();
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    public static void goActivity(Fragment from) {
        Intent intent = new Intent(from.getActivity(), WhisperListActivity.class);
        // intent.putExtra();
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_whisper_list;
    }

    @Override
    protected void initView(Intent intent, Bundle state) {
        ViewHelper.initTopBar(mActivity, tb, getString(R.string.whisper), true);
        // recycler
        recyclerHelper = new RecyclerHelper(rv)
                .initLayoutManager(new LinearLayoutManager(mActivity))
                .initRefresh(srl, true)
                .initAdapter(new WhisperAdapter(mActivity))
                .viewEmpty(mActivity, R.layout.list_empty_grey, true, true)
                .viewLoadMore(new RecyclerHelper.MoreGreyView())
                .viewAnim(BaseQuickAdapter.ALPHAIN)
                .setAdapter()
                .listenerRefresh(() -> getData(false))
                .listenerMore(currentCount -> getData(true));
        // channel
        etChannel.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                onChannelInput(s.toString());
            }
        });
        onChannelInput("");
        // editTextHint
        limitContentLength = SPHelper.getLimit().getWhisperContentLength();
        String format = String.format(Locale.getDefault(), getString(R.string.please_input_content_no_over_holder_text), limitContentLength);
        etContent.setHint(format);
        etContent.setText("");
    }

    @Override
    protected void initData(Intent intent, Bundle state) {
        channel = "";
        recyclerHelper.dataRefresh();
    }

    @Override
    protected void onFinish(Bundle state) {
        RecyclerHelper.release(recyclerHelper);
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
        if (requestCode == BaseActivity.REQUEST_PICTURE) {
            // 相册
            File pictureFile = PickHelper.getResultFile(mActivity, data);
            if (FileUtils.isFileEmpty(pictureFile)) {
                ToastUtils.show(getString(R.string.file_no_exits));
                return;
            }
            ossUpload(pictureFile);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuHelp: // 帮助
                HelpActivity.goActivity(mActivity, HelpActivity.INDEX_NOTE_WHISPER);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnTextChanged({R.id.etContent})
    public void afterTextChanged(Editable s) {
        onContentInput(s.toString());
    }

    @OnClick({R.id.btnChangeChannel, R.id.ivImgAdd, R.id.ivTextAdd})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnChangeChannel: // 切换频道
                if (recyclerHelper == null) return;
                recyclerHelper.dataRefresh();
                break;
            case R.id.ivImgAdd: // 添加图片
                goPicture();
                break;
            case R.id.ivTextAdd: // 添加文字
                commitText();
                break;
        }
    }

    private void getData(final boolean more) {
        page = more ? page + 1 : 0;
        InputUtils.hideSoftInput(etChannel);
        channel = etChannel.getText().toString().trim();
        tvCurrentChannel.setText(getChannelShow());
        // api
        Call<Result> api = new RetrofitHelper().call(API.class).noteWhisperListGet(channel, page);
        RetrofitHelper.enqueue(api, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                if (recyclerHelper == null) return;
                List<Whisper> whisperList = data.getWhisperList();
                recyclerHelper.dataOk(data.getShow(), whisperList, more);
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                if (recyclerHelper == null) return;
                recyclerHelper.dataFail(more, message);
            }
        });
        pushApi(api);
    }

    private String getChannelShow() {
        String channelShow = StringUtils.isEmpty(channel) ? getString(R.string.now_no) : channel;
        return String.format(Locale.getDefault(), getString(R.string.current_channel_colon_space_holder), channelShow);
    }

    private void onChannelInput(String input) {
        if (StringUtils.isEmpty(input)) {
            btnChangeChannel.setEnabled(false);
        } else {
            btnChangeChannel.setEnabled(true);
        }
        if (limitChannelLength <= 0) {
            limitChannelLength = SPHelper.getLimit().getWhisperChannelLength();
        }
        int length = input.length();
        if (length > limitChannelLength) {
            CharSequence charSequence = input.subSequence(0, limitChannelLength);
            etChannel.setText(charSequence);
            etChannel.setSelection(charSequence.length());
        }
    }

    private void onContentInput(String input) {
        if (StringUtils.isEmpty(input)) {
            ivTextAdd.setEnabled(false);
        } else {
            ivTextAdd.setEnabled(true);
        }
        int length = input.length();
        if (length > limitContentLength) {
            CharSequence charSequence = input.subSequence(0, limitContentLength);
            etContent.setText(charSequence);
            etContent.setSelection(charSequence.length());
        }
    }

    private void commitText() {
        String content = etContent.getText().toString().trim();
        if (StringUtils.isEmpty(content)) {
            ToastUtils.show(etContent.getHint());
            return;
        }
        Whisper body = new Whisper();
        body.setChannel(channel);
        body.setImage(false);
        body.setContent(content);
        checkApi(body);
    }

    // 图片获取
    private void goPicture() {
        if (!SPHelper.getVipLimit().isWhisperImageEnable()) {
            VipActivity.goActivity(mActivity);
            return;
        }
        PickHelper.selectImage(mActivity, 1, true);
    }

    // 上传
    private void ossUpload(File file) {
        OssHelper.uploadWhisper(mActivity, file, new OssHelper.OssUploadCallBack() {
            @Override
            public void success(File source, String ossPath) {
                Whisper body = new Whisper();
                body.setChannel(channel);
                body.setImage(true);
                body.setContent(ossPath);
                checkApi(body);
            }

            @Override
            public void failure(File source, String errMsg) {
            }
        });
    }

    private void checkApi(Whisper whisper) {
        // api
        Call<Result> api = new RetrofitHelper().call(API.class).noteWhisperAdd(whisper);
        RetrofitHelper.enqueue(api, getLoading(true), new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                if (recyclerHelper == null) return;
                // editText
                etContent.setText("");
                InputUtils.hideSoftInput(etContent);
                // adapter
                WhisperAdapter adapter = recyclerHelper.getAdapter();
                adapter.addData(0, data.getWhisper());
                rv.smoothScrollToPosition(0);
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
            }
        });
        pushApi(api);
    }

}
