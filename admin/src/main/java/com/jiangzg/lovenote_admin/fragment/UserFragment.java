package com.jiangzg.lovenote_admin.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.TextView;

import com.jiangzg.lovenote_admin.R;
import com.jiangzg.lovenote_admin.activity.SmsActivity;
import com.jiangzg.lovenote_admin.base.BaseFragment;

import butterknife.BindView;
import butterknife.OnClick;

public class UserFragment extends BaseFragment<UserFragment> {

    @BindView(R.id.tvUserDayNew)
    TextView tvUserDayNew;
    @BindView(R.id.tvUserWeekNew)
    TextView tvUserWeekNew;
    @BindView(R.id.cvUserNew)
    CardView cvUserNew;
    @BindView(R.id.tvUserDayActive)
    TextView tvUserDayActive;
    @BindView(R.id.tvUserWeekActive)
    TextView tvUserWeekActive;
    @BindView(R.id.cvUserActive)
    CardView cvUserActive;
    @BindView(R.id.tvSmsDaySend)
    TextView tvSmsDaySend;
    @BindView(R.id.tvSmsWeekSend)
    TextView tvSmsWeekSend;
    @BindView(R.id.cvSms)
    CardView cvSms;
    @BindView(R.id.tvApiHour)
    TextView tvApiHour;
    @BindView(R.id.tvApiDay)
    TextView tvApiDay;
    @BindView(R.id.cvApi)
    CardView cvApi;
    @BindView(R.id.tvSuggestDayNew)
    TextView tvSuggestDayNew;
    @BindView(R.id.tvSuggestWeekNew)
    TextView tvSuggestWeekNew;
    @BindView(R.id.cvSuggest)
    CardView cvSuggest;
    @BindView(R.id.tvSuggestCommentDayNew)
    TextView tvSuggestCommentDayNew;
    @BindView(R.id.tvSuggestCommentWeekNew)
    TextView tvSuggestCommentWeekNew;
    @BindView(R.id.cvSuggestComment)
    CardView cvSuggestComment;

    public static UserFragment newFragment() {
        Bundle bundle = new Bundle();
        // bundle.putData();
        return BaseFragment.newInstance(UserFragment.class, bundle);
    }

    @Override
    protected int getView(Bundle data) {
        return R.layout.fragment_user;
    }

    @Override
    protected void initView(@Nullable Bundle state) {
    }

    @Override
    protected void initData(Bundle state) {
        // TODO
    }

    @Override
    protected void onFinish(Bundle state) {
    }

    @OnClick({R.id.cvUserNew, R.id.cvUserActive,
            R.id.cvSms, R.id.cvApi,
            R.id.cvSuggest, R.id.cvSuggestComment})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.cvUserNew: // 注册用户
                // TODO
                break;
            case R.id.cvUserActive: // 活跃用户
                // TODO
                break;
            case R.id.cvSms: // 短信发送
                SmsActivity.goActivity(mFragment);
                break;
            case R.id.cvApi: // 用户行为
                // TODO
                break;
            case R.id.cvSuggest: // 意见帖子
                // TODO
                break;
            case R.id.cvSuggestComment: // 意见评论
                // TODO
                break;
        }
    }

}
