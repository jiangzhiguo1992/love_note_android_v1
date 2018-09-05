package com.jiangzg.lovenote_admin.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.afollestad.materialdialogs.MaterialDialog;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.lovenote_admin.R;
import com.jiangzg.lovenote_admin.base.BaseActivity;
import com.jiangzg.lovenote_admin.domain.Notice;
import com.jiangzg.lovenote_admin.domain.Result;
import com.jiangzg.lovenote_admin.helper.API;
import com.jiangzg.lovenote_admin.helper.RetrofitHelper;
import com.jiangzg.lovenote_admin.helper.ViewHelper;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;

public class NoticeEditActivity extends BaseActivity<NoticeEditActivity> {

    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.etTitle)
    EditText etTitle;
    @BindView(R.id.rgType)
    RadioGroup rgType;
    @BindView(R.id.rbTypeText)
    RadioButton rbTypeText;
    @BindView(R.id.rbTypeUrl)
    RadioButton rbTypeUrl;
    @BindView(R.id.rbTypeImg)
    RadioButton rbTypeImg;
    @BindView(R.id.etContent)
    EditText etContent;
    @BindView(R.id.btnPush)
    Button btnPush;

    public static void goActivity(Fragment from) {
        Intent intent = new Intent(from.getActivity(), NoticeEditActivity.class);
        // intent.putExtra();
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_notice_edit;
    }

    @Override
    protected void initView(Intent intent, Bundle state) {
        ViewHelper.initTopBar(mActivity, tb, "notice", true);
        rbTypeText.setChecked(true);
    }

    @Override
    protected void initData(Intent intent, Bundle state) {
    }

    @Override
    protected void onFinish(Bundle state) {
    }

    @OnClick({R.id.btnPush})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnPush: // 发布
                push();
                break;
        }
    }

    private void push() {
        Notice notice = new Notice();
        if (rbTypeText.isChecked()) {
            notice.setContentType(Notice.TYPE_TEXT);
        } else if (rbTypeUrl.isChecked()) {
            notice.setContentType(Notice.TYPE_URL);
        } else if (rbTypeImg.isChecked()) {
            notice.setContentType(Notice.TYPE_IMAGE);
        } else {
            return;
        }
        notice.setTitle(etTitle.getText().toString());
        notice.setContentText(etContent.getText().toString());
        MaterialDialog loading = getLoading(false);
        Call<Result> call = new RetrofitHelper().call(API.class).noticeAdd(notice);
        RetrofitHelper.enqueue(call, loading, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                mActivity.finish();
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
            }
        });
    }

}
