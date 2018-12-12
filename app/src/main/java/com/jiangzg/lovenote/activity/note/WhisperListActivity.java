package com.jiangzg.lovenote.activity.note;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.jiangzg.base.common.FileUtils;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.base.system.InputUtils;
import com.jiangzg.base.view.ToastUtils;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.activity.more.VipActivity;
import com.jiangzg.lovenote.activity.settings.HelpActivity;
import com.jiangzg.lovenote.adapter.WhisperAdapter;
import com.jiangzg.lovenote.base.BaseActivity;
import com.jiangzg.lovenote.helper.ApiHelper;
import com.jiangzg.lovenote.helper.ConsHelper;
import com.jiangzg.lovenote.helper.ListHelper;
import com.jiangzg.lovenote.helper.MediaPickHelper;
import com.jiangzg.lovenote.helper.OssHelper;
import com.jiangzg.lovenote.helper.OssResHelper;
import com.jiangzg.lovenote.helper.RecyclerHelper;
import com.jiangzg.lovenote.helper.RetrofitHelper;
import com.jiangzg.lovenote.helper.SPHelper;
import com.jiangzg.lovenote.helper.ViewHelper;
import com.jiangzg.lovenote.model.api.API;
import com.jiangzg.lovenote.model.api.Result;
import com.jiangzg.lovenote.model.entity.Whisper;
import com.jiangzg.lovenote.view.GSwipeRefreshLayout;

import java.io.File;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;
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
    @BindView(R.id.llAddImage)
    LinearLayout llAddImage;
    @BindView(R.id.llAddText)
    LinearLayout llAddText;
    @BindView(R.id.rlComment)
    RelativeLayout rlComment;
    @BindView(R.id.tvCommentLimit)
    TextView tvCommentLimit;
    @BindView(R.id.etComment)
    EditText etComment;

    private String channel;
    private int limitChannelLength;
    private RecyclerHelper recyclerHelper;
    private BottomSheetBehavior behaviorComment;
    private Call<Result> callAdd;
    private Call<Result> callGet;
    private int page, limitContentLength;

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
                .initRefresh(srl, false)
                .initAdapter(new WhisperAdapter(mActivity))
                .viewEmpty(mActivity, R.layout.list_empty_grey, true, true)
                .viewLoadMore(new RecyclerHelper.MoreGreyView())
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
        // text
        etComment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                onCommentInput(s.toString());
            }
        });
        commentShow(false);
        etComment.setText("");
    }

    @Override
    protected void initData(Intent intent, Bundle state) {
        channel = "";
        page = 0;
        recyclerHelper.dataRefresh();
    }

    @Override
    protected void onFinish(Bundle state) {
        RetrofitHelper.cancel(callGet);
        RetrofitHelper.cancel(callAdd);
        RecyclerHelper.release(recyclerHelper);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.help, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        if (behaviorComment.getState() != BottomSheetBehavior.STATE_HIDDEN) {
            behaviorComment.setState(BottomSheetBehavior.STATE_HIDDEN);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;
        if (requestCode == ConsHelper.REQUEST_PICTURE) {
            // 相册
            File pictureFile = MediaPickHelper.getResultFile(data);
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

    @OnClick({R.id.btnChangeChannel, R.id.llAddImage, R.id.llAddText,
            R.id.ivCommentClose, R.id.ivAddCommit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnChangeChannel: // 切换频道
                if (recyclerHelper == null) return;
                recyclerHelper.dataRefresh();
                break;
            case R.id.llAddImage: // 添加图片
                goPicture();
                break;
            case R.id.llAddText: // 添加文字
                commentShow(true);
                break;
            case R.id.ivCommentClose: // 评论关闭
                commentShow(false);
                break;
            case R.id.ivAddCommit: // 评论提交
                commitText();
                break;
        }
    }

    private void getData(final boolean more) {
        page = more ? page + 1 : 0;
        InputUtils.hideSoftInput(etChannel);
        channel = etChannel.getText().toString().trim();
        tvCurrentChannel.setText(getChannelShow());
        callGet = new RetrofitHelper().call(API.class).noteWhisperListGet(channel, page);
        RetrofitHelper.enqueue(callGet, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                if (recyclerHelper == null) return;
                recyclerHelper.viewEmptyShow(data.getShow());
                List<Whisper> whisperList = data.getWhisperList();
                recyclerHelper.dataOk(whisperList, more);
                // 刷新本地资源
                List<String> ossKeyList = ListHelper.getOssKeyListByWhisper(whisperList);
                OssResHelper.refreshResWithDelExpire(OssResHelper.TYPE_NOTE_WHISPER, ossKeyList);
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                if (recyclerHelper == null) return;
                recyclerHelper.dataFail(more, message);
            }
        });
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

    private void onCommentInput(String input) {
        if (input == null) return;
        if (limitContentLength <= 0) {
            limitContentLength = SPHelper.getLimit().getWhisperContentLength();
        }
        int length = input.length();
        if (length > limitContentLength) {
            CharSequence charSequence = input.subSequence(0, limitContentLength);
            etComment.setText(charSequence);
            etComment.setSelection(charSequence.length());
            length = charSequence.length();
        }
        String limitShow = String.format(Locale.getDefault(), getString(R.string.holder_sprit_holder), length, limitContentLength);
        tvCommentLimit.setText(limitShow);
    }

    // 评论视图
    private void commentShow(boolean show) {
        if (behaviorComment == null) {
            behaviorComment = BottomSheetBehavior.from(rlComment);
        }
        int state = show ? BottomSheetBehavior.STATE_COLLAPSED : BottomSheetBehavior.STATE_HIDDEN;
        behaviorComment.setState(state);
        if (!show) InputUtils.hideSoftInput(etComment);
    }

    private void commitText() {
        String content = etComment.getText().toString().trim();
        if (StringUtils.isEmpty(content)) {
            ToastUtils.show(etComment.getHint());
            return;
        }
        Whisper body = ApiHelper.getWhisperBody(channel, false, content);
        api(body);
    }

    // 图片获取
    private void goPicture() {
        if (!SPHelper.getVipLimit().isWhisperImageEnable()) {
            VipActivity.goActivity(mActivity);
            return;
        }
        MediaPickHelper.selectImage(mActivity, 1);
    }

    // 上传
    private void ossUpload(File file) {
        OssHelper.uploadWhisper(mActivity, file, new OssHelper.OssUploadCallBack() {
            @Override
            public void success(File source, String ossPath) {
                Whisper body = ApiHelper.getWhisperBody(channel, true, ossPath);
                api(body);
            }

            @Override
            public void failure(File source, String errMsg) {
            }
        });
    }

    private void api(Whisper whisper) {
        InputUtils.hideSoftInput(etComment);
        callAdd = new RetrofitHelper().call(API.class).noteWhisperAdd(whisper);
        MaterialDialog loading = getLoading(true);
        RetrofitHelper.enqueue(callAdd, loading, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                if (recyclerHelper == null) return;
                // editText
                etComment.setText("");
                commentShow(false);
                // adapter
                WhisperAdapter adapter = recyclerHelper.getAdapter();
                adapter.addData(0, data.getWhisper());
                rv.smoothScrollToPosition(0);
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
            }
        });
    }

}
