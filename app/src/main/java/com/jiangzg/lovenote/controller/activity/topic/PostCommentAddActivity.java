package com.jiangzg.lovenote.controller.activity.topic;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.base.view.ToastUtils;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.controller.activity.base.BaseActivity;
import com.jiangzg.lovenote.helper.common.ApiHelper;
import com.jiangzg.lovenote.helper.common.RxBus;
import com.jiangzg.lovenote.helper.common.SPHelper;
import com.jiangzg.lovenote.helper.system.RetrofitHelper;
import com.jiangzg.lovenote.helper.view.ViewHelper;
import com.jiangzg.lovenote.model.api.API;
import com.jiangzg.lovenote.model.api.Result;
import com.jiangzg.lovenote.model.entity.PostComment;

import java.util.Locale;

import butterknife.BindView;
import butterknife.OnTextChanged;
import retrofit2.Call;

public class PostCommentAddActivity extends BaseActivity<PostCommentAddActivity> {

    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.tvContentLimit)
    TextView tvContentLimit;
    @BindView(R.id.etContent)
    EditText etContent;

    private long pid, tcid;
    private int limitContentLength;

    public static void goActivity(Activity from, long pid, long tcid) {
        if (pid == 0) return;
        Intent intent = new Intent(from, PostCommentAddActivity.class);
        intent.putExtra("pid", pid);
        intent.putExtra("tcid", tcid);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_post_comment_add;
    }

    @Override
    protected void initView(Intent intent, Bundle state) {
        ViewHelper.initTopBar(mActivity, tb, getString(R.string.comment), true);
        // content
        etContent.setText("");
    }

    @Override
    protected void initData(Intent intent, Bundle state) {
        pid = intent.getLongExtra("pid", 0);
        tcid = intent.getLongExtra("tcid", 0);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuCommit: // 提交
                checkPush();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnTextChanged({R.id.etContent})
    public void afterTextChanged(Editable s) {
        onContentInput(s.toString());
    }

    private void onContentInput(String input) {
        if (limitContentLength <= 0) {
            limitContentLength = SPHelper.getLimit().getPostCommentContentLength();
        }
        int length = input.length();
        if (length > limitContentLength) {
            CharSequence charSequence = input.subSequence(0, limitContentLength);
            etContent.setText(charSequence);
            etContent.setSelection(charSequence.length());
            length = charSequence.length();
        }
        String limitShow = String.format(Locale.getDefault(), getString(R.string.holder_sprit_holder), length, limitContentLength);
        tvContentLimit.setText(limitShow);
    }

    private void checkPush() {
        String content = etContent.getText().toString().trim();
        if (StringUtils.isEmpty(content)) {
            ToastUtils.show(etContent.getHint());
            return;
        }
        PostComment comment = ApiHelper.getPostCommentTextBody(pid, tcid, content);
        // api
        Call<Result> api = new RetrofitHelper().call(API.class).topicPostCommentAdd(comment);
        RetrofitHelper.enqueue(api, getLoading(true), new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                // event
                RxBus.post(new RxBus.Event<>(RxBus.EVENT_POST_DETAIL_REFRESH, pid));
                RxBus.post(new RxBus.Event<>(RxBus.EVENT_POST_COMMENT_LIST_REFRESH, comment));
                if (tcid > 0) {
                    RxBus.post(new RxBus.Event<>(RxBus.EVENT_POST_COMMENT_DETAIL_REFRESH, tcid));
                }
                // finish
                mActivity.finish();
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
            }
        });
        pushApi(api);
    }

}
