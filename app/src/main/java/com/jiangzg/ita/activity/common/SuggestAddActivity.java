package com.jiangzg.ita.activity.common;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.jiangzg.base.common.FileUtils;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.base.component.IntentResult;
import com.jiangzg.base.component.IntentSend;
import com.jiangzg.base.system.PermUtils;
import com.jiangzg.base.view.ToastUtils;
import com.jiangzg.ita.R;
import com.jiangzg.ita.base.BaseActivity;
import com.jiangzg.ita.domain.Help;
import com.jiangzg.ita.domain.Result;
import com.jiangzg.ita.domain.Suggest;
import com.jiangzg.ita.helper.API;
import com.jiangzg.ita.helper.ApiHelper;
import com.jiangzg.ita.helper.ConsHelper;
import com.jiangzg.ita.helper.DialogHelper;
import com.jiangzg.ita.helper.OssHelper;
import com.jiangzg.ita.helper.RetrofitHelper;
import com.jiangzg.ita.helper.ViewHelper;
import com.jiangzg.ita.view.GImageView;

import java.io.File;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;

public class SuggestAddActivity extends BaseActivity<SuggestAddActivity> {

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
    GImageView ivImage;
    @BindView(R.id.btnPush)
    Button btnPush;

    private int contentType = -1;
    private File pictureFile;

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
    protected void initView(Bundle state) {
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
        onTitleInput("");
        onContentInput("");
        // menu
        tb.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menuHelp: // 帮助
                        HelpActivity.goActivity(mActivity, Help.TYPE_SUGGEST_ADD);
                        break;
                }
                return true;
            }
        });
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == ConsHelper.REQUEST_PICTURE) {
            pictureFile = IntentResult.getPictureFile(data);
            ivImage.setVisibility(View.VISIBLE);
            ivImage.setDataFile(pictureFile);
            tvImageToggle.setText(R.string.click_me_to_del_image);
        }
    }

    @OnClick({R.id.tvType, R.id.tvImageToggle, R.id.btnPush})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tvType: // 选分类
                showTypeDialog();
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
        if (FileUtils.isFileEmpty(pictureFile)) {
            pictureFile = null;
            requestPermission();
        } else {
            cancelImage();
        }
    }

    // 请求文件(相册)获取权限
    private void requestPermission() {
        PermUtils.requestPermissions(mActivity, ConsHelper.REQUEST_PICTURE, PermUtils.picture, new PermUtils.OnPermissionListener() {
            @Override
            public void onPermissionGranted(int requestCode, String[] permissions) {
                Intent picture = IntentSend.getPicture();
                ActivityTrans.startResult(mActivity, picture, ConsHelper.REQUEST_PICTURE);
            }

            @Override
            public void onPermissionDenied(int requestCode, String[] permissions) {
            }
        });
    }

    // 取消图片
    private void cancelImage() {
        tvImageToggle.setText(R.string.click_me_add_image);
        ivImage.setVisibility(View.GONE);
        pictureFile = null;
    }

    private void onTitleInput(String s) {
        int titleLimit = 20;
        int length = s.length();
        if (length > titleLimit) {
            CharSequence charSequence = s.subSequence(0, titleLimit);
            etTitle.setText(charSequence);
            etTitle.setSelection(charSequence.length());
            length = charSequence.length();
        }
        String limitShow = String.format(getString(R.string.holder_sprit_holder), length, titleLimit);
        tvTitleLimit.setText(limitShow);
    }

    private void onContentInput(String s) {
        int contentLimit = 200;
        int length = s.length();
        if (length > contentLimit) {
            CharSequence charSequence = s.subSequence(0, contentLimit);
            etContent.setText(charSequence);
            etContent.setSelection(charSequence.length());
            length = charSequence.length();
        }
        String limitShow = String.format(getString(R.string.holder_sprit_holder), length, contentLimit);
        tvContentLimit.setText(limitShow);
    }

    private void showTypeDialog() {
        String bug = getString(R.string.program_error);
        String func = getString(R.string.function_add);
        String optimize = getString(R.string.experience_optimize);
        String other = getString(R.string.other);
        CharSequence[] items = new CharSequence[]{bug, func, optimize, other};
        int selectIndex = getItemsSelectByType(contentType);
        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .title(R.string.please_choose_classify)
                .items(items)
                .itemsCallbackSingleChoice(selectIndex, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        contentType = getTypeByItemSelect(which);
                        tvType.setText(getSelectShowByType(contentType));
                        return true;
                    }
                })
                .positiveText(R.string.i_choose_ok)
                .negativeText(R.string.i_think_again)
                .build();
        DialogHelper.setAnim(dialog);
        DialogHelper.show(dialog);
    }

    private String getSelectShowByType(int contentType) {
        String selectShow;
        switch (contentType) {
            case Suggest.TYPE_BUG:
                selectShow = getString(R.string.program_error);
                break;
            case Suggest.TYPE_FUNC:
                selectShow = getString(R.string.function_add);
                break;
            case Suggest.TYPE_TASTE:
                selectShow = getString(R.string.experience_optimize);
                break;
            case Suggest.TYPE_OTHER:
                selectShow = getString(R.string.other);
                break;
            default:
                selectShow = getString(R.string.click_me_choose_type);
                break;
        }
        return selectShow;
    }

    private int getItemsSelectByType(int contentType) {
        int selectIndex;
        switch (contentType) {
            case Suggest.TYPE_BUG:
                selectIndex = 0;
                break;
            case Suggest.TYPE_FUNC:
                selectIndex = 1;
                break;
            case Suggest.TYPE_TASTE:
                selectIndex = 2;
                break;
            case Suggest.TYPE_OTHER:
                selectIndex = 3;
                break;
            default:
                selectIndex = -1;
                break;
        }
        return selectIndex;
    }

    private int getTypeByItemSelect(int select) {
        int contentType;
        switch (select) {
            case 0:
                contentType = Suggest.TYPE_BUG;
                break;
            case 1:
                contentType = Suggest.TYPE_FUNC;
                break;
            case 2:
                contentType = Suggest.TYPE_TASTE;
                break;
            case 3:
            default:
                contentType = Suggest.TYPE_OTHER;
                break;
        }
        return contentType;
    }

    // 检查格式
    private void checkPush() {
        if (contentType < 0) {
            ToastUtils.show(getString(R.string.please_choose_classify));
            return;
        }
        String title = etTitle.getText().toString();
        if (title.trim().length() <= 0) {
            ToastUtils.show(getString(R.string.please_input_title));
            return;
        }
        String content = etContent.getText().toString();
        if (content.trim().length() <= 0) {
            ToastUtils.show(getString(R.string.please_input_content));
            return;
        }
        if (pictureFile != null) {
            pushImage();
        } else {
            pushSuggest("");
        }
    }

    private void pushImage() {
        OssHelper.uploadSuggest(mActivity, pictureFile, new OssHelper.OssUploadCallBack() {
            @Override
            public void success(String ossPath) {
                pushSuggest(ossPath);
            }

            @Override
            public void failure(String ossPath) {
            }
        });
    }

    // 发布
    private void pushSuggest(String imgPath) {
        String title = etTitle.getText().toString();
        String content = etContent.getText().toString();
        Suggest body = ApiHelper.getSuggestAddBody(title, contentType, content, imgPath);
        MaterialDialog loading = mActivity.getLoading(false);
        Call<Result> call = new RetrofitHelper().call(API.class).suggestAdd(body);
        RetrofitHelper.enqueue(call, loading, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                // todo refreshList/gotoDetail
                mActivity.finish();
            }

            @Override
            public void onFailure() {
            }
        });
    }

}
