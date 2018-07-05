package com.jiangzg.mianmian.activity.settings;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.jiangzg.base.common.FileUtils;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.base.component.IntentResult;
import com.jiangzg.base.view.DialogUtils;
import com.jiangzg.base.view.PopUtils;
import com.jiangzg.base.view.ScreenUtils;
import com.jiangzg.base.view.ToastUtils;
import com.jiangzg.mianmian.R;
import com.jiangzg.mianmian.activity.common.BigImageActivity;
import com.jiangzg.mianmian.base.BaseActivity;
import com.jiangzg.mianmian.domain.Help;
import com.jiangzg.mianmian.domain.Result;
import com.jiangzg.mianmian.domain.RxEvent;
import com.jiangzg.mianmian.domain.Suggest;
import com.jiangzg.mianmian.domain.SuggestInfo;
import com.jiangzg.mianmian.helper.API;
import com.jiangzg.mianmian.helper.ApiHelper;
import com.jiangzg.mianmian.helper.ConsHelper;
import com.jiangzg.mianmian.helper.DialogHelper;
import com.jiangzg.mianmian.helper.OssHelper;
import com.jiangzg.mianmian.helper.ResHelper;
import com.jiangzg.mianmian.helper.RetrofitHelper;
import com.jiangzg.mianmian.helper.RxBus;
import com.jiangzg.mianmian.helper.SPHelper;
import com.jiangzg.mianmian.helper.ViewHelper;
import com.jiangzg.mianmian.view.FrescoNativeView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;

public class SuggestAddActivity extends BaseActivity<SuggestAddActivity> {

    @BindView(R.id.root)
    LinearLayout root;
    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.tvType)
    TextView tvType;
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
    @BindView(R.id.btnPush)
    Button btnPush;

    private int contentType = 0;
    private File cameraFile;
    private File pictureFile;
    private List<SuggestInfo.SuggestType> suggestTypeList;
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
        ViewHelper.initTopBar(mActivity, tb, getString(R.string.i_want_push_suggest), true);
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
        ivImage.setSuccessClickListener(new FrescoNativeView.onSuccessClickListener() {
            @Override
            public void onClick(FrescoNativeView iv) {
                if (!FileUtils.isFileEmpty(cameraFile)) {
                    BigImageActivity.goActivityByFile(mActivity, cameraFile.getAbsolutePath(), iv);
                } else if (!FileUtils.isFileEmpty(pictureFile)) {
                    BigImageActivity.goActivityByFile(mActivity, pictureFile.getAbsolutePath(), iv);
                }
            }
        });
    }

    @Override
    protected void initData(Intent intent, Bundle state) {
        SuggestInfo suggestInfo = SuggestInfo.getInstance();
        suggestTypeList = suggestInfo.getTypeList();
    }

    @Override
    protected void onFinish(Bundle state) {
        RetrofitHelper.cancel(call);
        ResHelper.deleteFileInBackground(cameraFile);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.help, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            ResHelper.deleteFileInBackground(cameraFile);
            return;
        }
        if (requestCode == ConsHelper.REQUEST_CAMERA) {
            // 拍照
            if (FileUtils.isFileEmpty(cameraFile)) {
                ToastUtils.show(getString(R.string.file_no_exits));
                ResHelper.deleteFileInBackground(cameraFile);
                ivImage.setVisibility(View.GONE);
                tvImageToggle.setText(R.string.click_me_add_image);
            } else {
                ivImage.setVisibility(View.VISIBLE);
                ivImage.setDataFile(cameraFile);
                tvImageToggle.setText(R.string.click_me_to_del_image);
            }
        } else if (requestCode == ConsHelper.REQUEST_PICTURE) {
            // 相册
            pictureFile = IntentResult.getPictureFile(data);
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
            case R.id.menuHelp: // 帮助
                HelpActivity.goActivity(mActivity, Help.INDEX_SUGGEST_ADD);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick({R.id.tvType, R.id.tvImageToggle, R.id.btnPush})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tvType: // 选分类
                showStatusDialog();
                break;
            case R.id.tvImageToggle: // 操作照片
                toggleImage();
                break;
            case R.id.btnPush: // 发布
                checkPush();
                break;
        }
    }

    private void toggleImage() {
        if (FileUtils.isFileEmpty(cameraFile) && FileUtils.isFileEmpty(pictureFile)) {
            cameraFile = null;
            pictureFile = null;
            showImgSelect();
        } else {
            cancelImage();
        }
    }

    private void showImgSelect() {
        cameraFile = ResHelper.newImageCacheFile();
        PopupWindow popupWindow = ViewHelper.createPictureCameraPop(mActivity, cameraFile);
        PopUtils.show(popupWindow, root, Gravity.CENTER);
    }

    // 取消图片
    private void cancelImage() {
        tvImageToggle.setText(R.string.click_me_add_image);
        ivImage.setVisibility(View.GONE);
        // 释放图片资源
        ResHelper.deleteFileInBackground(cameraFile);
        pictureFile = null;
    }

    private void onTitleInput(String s) {
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
        if (limitContentLength <= 0) {
            limitContentLength = SPHelper.getLimit().getSuggestContentTextLength();
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

    private void showStatusDialog() {
        // 第一个是全部，不要
        CharSequence[] items = new CharSequence[suggestTypeList.size() - 1];
        for (int i = 1; i < suggestTypeList.size(); i++) {
            SuggestInfo.SuggestType contentType = suggestTypeList.get(i);
            int index = contentType.getType() - 1;
            String show = contentType.getShow();
            items[index] = show;
        }
        MaterialDialog dialog = DialogHelper.getBuild(mActivity)
                .cancelable(true)
                .canceledOnTouchOutside(true)
                .title(R.string.please_choose_classify)
                .items(items)
                .itemsCallbackSingleChoice(contentType - 1, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        // 第一个忽略
                        SuggestInfo.SuggestType suggestType = suggestTypeList.get(which + 1);
                        contentType = suggestType.getType();
                        tvType.setText(suggestType.getShow());
                        DialogUtils.dismiss(dialog);
                        return true;
                    }
                })
                .build();
        DialogHelper.showWithAnim(dialog);
    }

    // 检查格式
    private void checkPush() {
        if (contentType <= suggestTypeList.get(0).getType()) {
            // 第一个是全部
            ToastUtils.show(getString(R.string.please_choose_classify));
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
        if (!FileUtils.isFileEmpty(cameraFile)) {
            pushImage(cameraFile);
        } else if (!FileUtils.isFileEmpty(pictureFile)) {
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
        String title = etTitle.getText().toString();
        String content = etContent.getText().toString();
        Suggest body = ApiHelper.getSuggestAddBody(title, contentType, content, imgPath);
        MaterialDialog loading = mActivity.getLoading(false);
        call = new RetrofitHelper().call(API.class).suggestAdd(body);
        RetrofitHelper.enqueue(call, loading, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                RxEvent<ArrayList<Suggest>> event = new RxEvent<>(ConsHelper.EVENT_SUGGEST_LIST_REFRESH, new ArrayList<Suggest>());
                RxBus.post(event);
                mActivity.finish();
            }

            @Override
            public void onFailure(String errMsg) {
            }
        });
    }

}
