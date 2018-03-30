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
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.base.component.IntentSend;
import com.jiangzg.base.media.MediaBitmap;
import com.jiangzg.base.system.PermUtils;
import com.jiangzg.base.view.ToastUtils;
import com.jiangzg.ita.R;
import com.jiangzg.ita.base.BaseActivity;
import com.jiangzg.ita.domain.Help;
import com.jiangzg.ita.domain.Suggest;
import com.jiangzg.ita.helper.ConsHelper;
import com.jiangzg.ita.helper.ViewHelper;
import com.jiangzg.ita.view.GImageView;

import java.io.File;

import butterknife.BindView;
import butterknife.OnClick;

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
    @BindView(R.id.tvAddImage)
    TextView tvAddImage;
    @BindView(R.id.ivImage)
    GImageView ivImage;
    @BindView(R.id.btnPush)
    Button btnPush;

    private int contentType = 0;
    private File pictureFile;

    public static void goActivity(Activity from) {
        Intent intent = new Intent(from, SuggestAddActivity.class);
        // intent.putExtra();
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
                    case R.id.menuHelp: // 评论
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
            ivImage.setVisibility(View.VISIBLE);
            pictureFile = MediaBitmap.getPictureFile(data);
            ivImage.setDataFile(pictureFile);
        }
    }

    @OnClick({R.id.tvType, R.id.tvAddImage, R.id.btnPush})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tvType: // 选分类
                showTypeDialog();
                break;
            case R.id.tvAddImage: // 添加照片
                requestPermission();
                break;
            case R.id.btnPush: // 发布
                push();
                break;
        }
    }

    // 请求文件(相册)获取权限
    private void requestPermission() {
        PermUtils.requestPermissions(mActivity, ConsHelper.REQUEST_APP_INFO, PermUtils.appInfo, new PermUtils.OnPermissionListener() {
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
        new MaterialDialog.Builder(this)
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
                .show();
    }

    private String getSelectShowByType(int contentType) {
        String selectShow;
        switch (contentType) {
            case Suggest.TYPE_BUG:
                selectShow = getString(R.string.program_error);
                break;
            case Suggest.TYPE_FUNCTION:
                selectShow = getString(R.string.function_add);
                break;
            case Suggest.TYPE_EXPERIENCE:
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
            case Suggest.TYPE_FUNCTION:
                selectIndex = 1;
                break;
            case Suggest.TYPE_EXPERIENCE:
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
        int contentType = 0;
        switch (select) {
            case 0:
                contentType = Suggest.TYPE_BUG;
                break;
            case 1:
                contentType = Suggest.TYPE_FUNCTION;
                break;
            case 2:
                contentType = Suggest.TYPE_EXPERIENCE;
                break;
            case 3:
                contentType = Suggest.TYPE_OTHER;
                break;
        }
        return contentType;
    }

    // 发布
    private void push() {
        if (contentType != Suggest.TYPE_BUG && contentType != Suggest.TYPE_FUNCTION && contentType != Suggest.TYPE_EXPERIENCE && contentType != Suggest.TYPE_OTHER) {
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
        // todo api
        ToastUtils.show(getString(R.string.push_success));
        mActivity.finish();
    }

}
