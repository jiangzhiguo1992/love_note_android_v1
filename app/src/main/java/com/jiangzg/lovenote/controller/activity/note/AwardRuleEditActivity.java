package com.jiangzg.lovenote.controller.activity.note;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.base.view.ToastUtils;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.controller.activity.base.BaseActivity;
import com.jiangzg.lovenote.helper.common.RxBus;
import com.jiangzg.lovenote.helper.common.SPHelper;
import com.jiangzg.lovenote.helper.system.RetrofitHelper;
import com.jiangzg.lovenote.helper.view.ViewHelper;
import com.jiangzg.lovenote.model.api.API;
import com.jiangzg.lovenote.model.api.Result;
import com.jiangzg.lovenote.model.entity.AwardRule;

import java.util.ArrayList;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import retrofit2.Call;

public class AwardRuleEditActivity extends BaseActivity<AwardRuleEditActivity> {

    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.btnScoreAdd)
    Button btnScoreAdd;
    @BindView(R.id.btnScoreSub)
    Button btnScoreSub;
    @BindView(R.id.tvScore)
    TextView tvScore;
    @BindView(R.id.etTitle)
    EditText etTitle;
    @BindView(R.id.tvTitleLimit)
    TextView tvTitleLimit;

    private AwardRule awardRule;
    private int limitTitleLength;
    private int scoreMax;

    public static void goActivity(Activity from) {
        Intent intent = new Intent(from, AwardRuleEditActivity.class);
        // intent.putExtra();
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_award_rule_edit;
    }

    @Override
    protected void initView(Intent intent, Bundle state) {
        ViewHelper.initTopBar(mActivity, tb, getString(R.string.award_rule), true);
        // init
        awardRule = new AwardRule();
        // score
        refreshScoreView();
        // etTitle
        etTitle.setText(awardRule.getTitle());
    }

    @Override
    protected void initData(Intent intent, Bundle state) {
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
                addApi();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnTextChanged({R.id.etTitle})
    public void afterTextChanged(Editable s) {
        onTitleInput(s.toString());
    }

    @OnClick({R.id.btnScoreAdd, R.id.btnScoreSub})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnScoreAdd: // +
                changeScore(true);
                break;
            case R.id.btnScoreSub: // -
                changeScore(false);
                break;
        }
    }

    private void changeScore(boolean add) {
        if (awardRule == null) return;
        if (scoreMax == 0) {
            scoreMax = SPHelper.getLimit().getAwardRuleScoreMax();
        }
        int score = awardRule.getScore();
        if (add) {
            awardRule.setScore(++score);
        } else {
            awardRule.setScore(--score);
        }
        if (awardRule.getScore() < 0) {
            btnScoreSub.setEnabled(Math.abs(score) < scoreMax);
        } else {
            btnScoreAdd.setEnabled(Math.abs(score) < scoreMax);
        }
        refreshScoreView();
    }

    private void refreshScoreView() {
        if (awardRule == null) return;
        tvScore.setText(String.valueOf(awardRule.getScore()));
    }

    private void onTitleInput(String input) {
        if (awardRule == null) return;
        if (limitTitleLength <= 0) {
            limitTitleLength = SPHelper.getLimit().getAwardRuleTitleLength();
        }
        int length = input.length();
        if (length > limitTitleLength) {
            CharSequence charSequence = input.subSequence(0, limitTitleLength);
            etTitle.setText(charSequence);
            etTitle.setSelection(charSequence.length());
            length = charSequence.length();
        }
        String limitShow = String.format(Locale.getDefault(), getString(R.string.holder_sprit_holder), length, limitTitleLength);
        tvTitleLimit.setText(limitShow);
        // 设置进去
        awardRule.setTitle(etTitle.getText().toString());
    }

    private void addApi() {
        if (awardRule == null) return;
        if (awardRule.getScore() == 0) {
            ToastUtils.show(getString(R.string.please_select_score));
            return;
        } else if (StringUtils.isEmpty(awardRule.getTitle())) {
            ToastUtils.show(etTitle.getHint().toString());
            return;
        }
        // api
        Call<Result> api = new RetrofitHelper().call(API.class).noteAwardRuleAdd(awardRule);
        RetrofitHelper.enqueue(api, getLoading(true), new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                // event
                RxBus.post(new RxBus.Event<>(RxBus.EVENT_AWARD_RULE_LIST_REFRESH, new ArrayList<>()));
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
