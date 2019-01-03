package com.jiangzg.lovenote.activity.settings;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.jiangzg.base.common.FileUtils;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.base.view.DialogUtils;
import com.jiangzg.base.view.ScreenUtils;
import com.jiangzg.base.view.ToastUtils;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.activity.base.BaseActivity;
import com.jiangzg.lovenote.activity.common.BigImageActivity;
import com.jiangzg.lovenote.helper.ConsHelper;
import com.jiangzg.lovenote.helper.DialogHelper;
import com.jiangzg.lovenote.helper.MediaPickHelper;
import com.jiangzg.lovenote.helper.OssHelper;
import com.jiangzg.lovenote.helper.RetrofitHelper;
import com.jiangzg.lovenote.helper.RxBus;
import com.jiangzg.lovenote.helper.SPHelper;
import com.jiangzg.lovenote.helper.ViewHelper;
import com.jiangzg.lovenote.model.api.API;
import com.jiangzg.lovenote.model.api.Result;
import com.jiangzg.lovenote.model.engine.SuggestInfo;
import com.jiangzg.lovenote.model.entity.Suggest;
import com.jiangzg.lovenote.view.FrescoNativeView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;

public class SuggestAddActivity extends BaseActivity<SuggestAddActivity> {

    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.tvKind)
    TextView tvKind;
    @BindView(R.id.tvTitleLimit)
    TextView tvTitleLimit;
    @BindView(R.id.etTitle)
    EditText etTitle;
    @BindView(R.id.tvContentLimit)
    TextView tvContentLimit;
    @BindView(R.id.etContent)
    EditText etContent;
    @BindView(R.id.tvImageToggle)
    TextView tvImageToggle;
    @BindView(R.id.ivImage)
    FrescoNativeView ivImage;

    private int kind = 0;
    private File pictureFile;
    private List<SuggestInfo.SuggestKind> suggestKindList;
    private int limitTitleLength;
    private int limitContentLength;
    private Call<Result> call;

    public static void goActivity(Activity from) {
        Intent intent = new Intent(from, SuggestAddActivity.class);
        // intent.putExtra();
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_suggest_add;
    }

    @Override
    protected void initView(Intent intent, Bundle state) {
        ViewHelper.initTopBar(mActivity, tb, getString(R.string.i_want_feedback), true);
        // input
        etTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                onTitleInput(s.toString());
            }
        });
        etTitle.setText("");
        etContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                onContentInput(s.toString());
            }
        });
        etContent.setText("");
        // imageView
        ViewGroup.LayoutParams layoutParams = ivImage.getLayoutParams();
        ivImage.setWidthAndHeight(ScreenUtils.getScreenWidth(mActivity), layoutParams.height);
        ivImage.setSuccessClickListener(iv -> {
            if (!FileUtils.isFileEmpty(pictureFile)) {
                BigImageActivity.goActivityByFile(mActivity, pictureFile.getAbsolutePath(), iv);
            }
        });
    }

    @Override
    protected void initData(Intent intent, Bundle state) {
        SuggestInfo suggestInfo = SuggestInfo.getInstance();
        suggestKindList = suggestInfo.getKindList();
    }

    @Override
    protected void onFinish(Bundle state) {
        RetrofitHelper.cancel(call);
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
            pictureFile = MediaPickHelper.getResultFile(mActivity, data);
            if (FileUtils.isFileEmpty(pictureFile)) {
                ToastUtils.show(getString(R.string.file_no_exits));
                pictureFile = null;
                ivImage.setVisibility(View.GONE);
                tvImageToggle.setText(R.string.click_me_add_image);
            } else {
                ivImage.setVisibility(View.VISIBLE);
                ivImage.setDataFile(pictureFile);
                tvImageToggle.setText(R.string.click_me_to_del_image);
            }
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

    @OnClick({R.id.tvKind, R.id.tvImageToggle})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tvKind: // 选分类
                showKindDialog();
                break;
            case R.id.tvImageToggle: // 操作照片
                toggleImage();
                break;
        }
    }

    private void toggleImage() {
        if (FileUtils.isFileEmpty(pictureFile)) {
            pictureFile = null;
            MediaPickHelper.selectImage(mActivity, 1);
        } else {
            cancelImage();
        }
    }

    // 取消图片
    private void cancelImage() {
        tvImageToggle.setText(R.string.click_me_add_image);
        ivImage.setVisibility(View.GONE);
        // 释放图片资源
        pictureFile = null;
    }

    private void onTitleInput(String s) {
        if (s == null) return;
        if (limitTitleLength <= 0) {
            limitTitleLength = SPHelper.getLimit().getSuggestTitleLength();
        }
        int length = s.length();
        if (length > limitTitleLength) {
            CharSequence charSequence = s.subSequence(0, limitTitleLength);
            etTitle.setText(charSequence);
            etTitle.setSelection(charSequence.length());
            length = charSequence.length();
        }
        String limitShow = String.format(Locale.getDefault(), getString(R.string.holder_sprit_holder), length, limitTitleLength);
        tvTitleLimit.setText(limitShow);
    }

    private void onContentInput(String s) {
        if (s == null) return;
        if (limitContentLength <= 0) {
            limitContentLength = SPHelper.getLimit().getSuggestContentLength();
        }
        int length = s.length();
        if (length > limitContentLength) {
            CharSequence charSequence = s.subSequence(0, limitContentLength);
            etContent.setText(charSequence);
            etContent.setSelection(charSequence.length());
            length = charSequence.length();
        }
        String limitShow = String.format(Locale.getDefault(), getString(R.string.holder_sprit_holder), length, limitContentLength);
        tvContentLimit.setText(limitShow);
    }

    private void showKindDialog() {
        CharSequence[] items = new CharSequence[suggestKindList.size() - 1];
        for (int i = 1; i < suggestKindList.size(); i++) {
            SuggestInfo.SuggestKind kind = suggestKindList.get(i);
            // 第一个是全部，不要
            items[i - 1] = kind.getShow();
        }
        int kindIndex = getKindIndex(kind);
        MaterialDialog dialog = DialogHelper.getBuild(mActivity)
                .cancelable(true)
                .canceledOnTouchOutside(true)
                .title(R.string.please_select_classify)
                .items(items)
                .itemsCallbackSingleChoice(kindIndex - 1, (dialog1, view, which, text) -> {
                    // 第一个忽略
                    SuggestInfo.SuggestKind suggestKind = suggestKindList.get(which + 1);
                    kind = suggestKind.getKind();
                    tvKind.setText(suggestKind.getShow());
                    DialogUtils.dismiss(dialog1);
                    return true;
                })
                .build();
        DialogHelper.showWithAnim(dialog);
    }

    // 检查格式
    private void checkPush() {
        if (kind <= suggestKindList.get(0).getKind()) {
            // 第一个是全部
            ToastUtils.show(getString(R.string.please_select_classify));
            return;
        }
        String title = etTitle.getText().toString();
        if (StringUtils.isEmpty(title)) {
            ToastUtils.show(getString(R.string.please_input_title));
            return;
        }
        String content = etContent.getText().toString();
        if (StringUtils.isEmpty(content)) {
            ToastUtils.show(getString(R.string.please_input_content));
            return;
        }
        if (!FileUtils.isFileEmpty(pictureFile)) {
            pushImage(pictureFile);
        } else {
            pushSuggest("");
        }
    }

    private void pushImage(File imgFile) {
        OssHelper.uploadSuggest(mActivity, imgFile, new OssHelper.OssUploadCallBack() {
            @Override
            public void success(File source, String ossPath) {
                pushSuggest(ossPath);
            }

            @Override
            public void failure(File source, String errMsg) {
            }
        });
    }

    // 发布
    private void pushSuggest(String imgPath) {
        Suggest body = new Suggest();
        body.setTitle(etTitle.getText().toString());
        body.setKind(kind);
        body.setContentText(etContent.getText().toString());
        body.setContentImage(imgPath);
        // api
        MaterialDialog loading = mActivity.getLoading(false);
        call = new RetrofitHelper().call(API.class).setSuggestAdd(body);
        RetrofitHelper.enqueue(call, loading, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                RxBus.post(new RxBus.Event<>(ConsHelper.EVENT_SUGGEST_LIST_REFRESH, new ArrayList<>()));
                mActivity.finish();
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
            }
        });
    }

    private int getKindIndex(int kind) {
        SuggestInfo info = SuggestInfo.getInstance();
        List<SuggestInfo.SuggestKind> kindList = info.getKindList();
        // 不要全部
        for (int i = 1; i < kindList.size(); i++) {
            SuggestInfo.SuggestKind s = kindList.get(i);
            if (s.getKind() == kind) {
                return i;
            }
        }
        return 0;
    }

}
