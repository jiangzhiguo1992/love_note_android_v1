package com.jiangzg.mianmian.activity.book;

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

import com.afollestad.materialdialogs.MaterialDialog;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.base.view.ToastUtils;
import com.jiangzg.mianmian.R;
import com.jiangzg.mianmian.activity.settings.HelpActivity;
import com.jiangzg.mianmian.base.BaseActivity;
import com.jiangzg.mianmian.domain.AwardRule;
import com.jiangzg.mianmian.domain.Help;
import com.jiangzg.mianmian.domain.Result;
import com.jiangzg.mianmian.domain.RxEvent;
import com.jiangzg.mianmian.helper.API;
import com.jiangzg.mianmian.helper.ConsHelper;
import com.jiangzg.mianmian.helper.RetrofitHelper;
import com.jiangzg.mianmian.helper.RxBus;
import com.jiangzg.mianmian.helper.SPHelper;
import com.jiangzg.mianmian.helper.ViewHelper;

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
    @BindView(R.id.btnPublish)
    Button btnPublish;

    private AwardRule awardRule;
    private Call<Result> callAdd;
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
        // input
        etTitle.setText(awardRule.getTitle());
    }

    @Override
    protected void initData(Intent intent, Bundle state) {
    }

    @Override
    protected void onFinish(Bundle state) {
        RetrofitHelper.cancel(callAdd);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.help, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuHelp: // 帮助
                HelpActivity.goActivity(mActivity, Help.INDEX_AWARD_RULE_EDIT);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnTextChanged({R.id.etTitle})
    public void afterTextChanged(Editable s) {
        onTitleInput(s.toString());
    }

    @OnClick({R.id.btnScoreAdd, R.id.btnScoreSub, R.id.btnPublish})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnScoreAdd: // +
                changeScore(true);
                break;
            case R.id.btnScoreSub: // -
                changeScore(false);
                break;
            case R.id.btnPublish: // 发表
                addApi();
                break;
        }
    }

    private void changeScore(boolean add) {
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
        tvScore.setText(String.valueOf(awardRule.getScore()));
    }

    private void onTitleInput(String input) {
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
        if (awardRule.getScore() == 0) {
            ToastUtils.show(getString(R.string.please_select_score));
            return;
        } else if (StringUtils.isEmpty(awardRule.getTitle())) {
            ToastUtils.show(etTitle.getHint().toString());
            return;
        }
        MaterialDialog loading = getLoading(false);
        callAdd = new RetrofitHelper().call(API.class).awardRuleAdd(awardRule);
        RetrofitHelper.enqueue(callAdd, loading, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                // event
                RxEvent<ArrayList<AwardRule>> event = new RxEvent<>(ConsHelper.EVENT_AWARD_RULE_LIST_REFRESH, new ArrayList<AwardRule>());
                RxBus.post(event);
                // finish
                mActivity.finish();
            }

            @Override
            public void onFailure(String errMsg) {
            }
        });
    }

}
