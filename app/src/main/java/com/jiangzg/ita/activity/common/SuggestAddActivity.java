package com.jiangzg.ita.activity.common;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.jiangzg.base.component.activity.ActivityTrans;
import com.jiangzg.ita.R;
import com.jiangzg.ita.base.BaseActivity;
import com.jiangzg.ita.utils.ViewUtils;

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
    ImageView ivImage;
    @BindView(R.id.btnPush)
    Button btnPush;

    private int titleLimit = 20;
    private int contentLimit = 200;

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
        ViewUtils.initTopBar(mActivity, tb, getString(R.string.i_want_push_suggest), true);


        etTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
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
        });
    }

    @Override
    protected void initData(Bundle state) {

    }

    @OnClick({R.id.tvType, R.id.tvAddImage, R.id.btnPush})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tvType: // todo 选分类

                break;
            case R.id.tvAddImage: // todo 添加照片

                break;
            case R.id.btnPush: // todo 发布

                break;
        }
    }

}
