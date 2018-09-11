package com.jiangzg.lovenote_admin.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.TextView;

import com.jiangzg.base.common.ConstantUtils;
import com.jiangzg.base.time.DateUtils;
import com.jiangzg.lovenote_admin.R;
import com.jiangzg.lovenote_admin.activity.ApiActivity;
import com.jiangzg.lovenote_admin.activity.EntryActivity;
import com.jiangzg.lovenote_admin.activity.SmsActivity;
import com.jiangzg.lovenote_admin.activity.SuggestListActivity;
import com.jiangzg.lovenote_admin.activity.UserListActivity;
import com.jiangzg.lovenote_admin.base.BaseFragment;
import com.jiangzg.lovenote_admin.domain.Result;
import com.jiangzg.lovenote_admin.helper.API;
import com.jiangzg.lovenote_admin.helper.ApiHelper;
import com.jiangzg.lovenote_admin.helper.RetrofitHelper;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;

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
    @BindView(R.id.tvSuggestCommentDayNew)
    TextView tvSuggestCommentDayNew;
    @BindView(R.id.cvSuggest)
    CardView cvSuggest;

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
        getUserData();
        getSmsData();
        getEntryData();
        getApiData();
        getSuggestData();
    }

    @Override
    protected void onFinish(Bundle state) {
    }

    @OnClick({R.id.cvUserNew, R.id.cvSms, R.id.cvUserActive, R.id.cvApi, R.id.cvSuggest})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.cvUserNew: // 注册用户
                UserListActivity.goActivity(mFragment);
                break;
            case R.id.cvSms: // 短信发送
                SmsActivity.goActivity(mFragment);
                break;
            case R.id.cvUserActive: // 活跃用户
                EntryActivity.goActivity(mFragment);
                break;
            case R.id.cvApi: // 用户行为
                ApiActivity.goActivity(mFragment);
                break;
            case R.id.cvSuggest: // 意见反馈
                SuggestListActivity.goActivity(mFragment);
                break;
        }
    }

    private void getUserData() {
        long currentLong = DateUtils.getCurrentLong();
        long startDay = (currentLong - ConstantUtils.DAY) / 1000;
        long startWeek = (currentLong - ConstantUtils.DAY * 7) / 1000;
        Call<Result> callDay = new RetrofitHelper().call(API.class).userTotalGet(startDay, 0, 0, 0);
        RetrofitHelper.enqueue(callDay, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                tvUserDayNew.setText("天：" + data.getTotal());
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                tvUserDayNew.setText("天：fail");
            }
        });
        Call<Result> callWeek = new RetrofitHelper().call(API.class).userTotalGet(startWeek, 0, 0, 0);
        RetrofitHelper.enqueue(callWeek, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                tvUserWeekNew.setText("周：" + data.getTotal());
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                tvUserWeekNew.setText("周：fail");
            }
        });
    }

    private void getSmsData() {
        long currentLong = DateUtils.getCurrentLong();
        long startDay = (currentLong - ConstantUtils.DAY) / 1000;
        long startWeek = (currentLong - ConstantUtils.DAY * 7) / 1000;
        long endAt = currentLong / 1000;
        Call<Result> callDay = new RetrofitHelper().call(API.class).smsTotalGet(startDay, endAt, "", ApiHelper.LIST_SMS_TYPE[0]);
        RetrofitHelper.enqueue(callDay, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                tvSmsDaySend.setText("天：" + data.getTotal());
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                tvSmsDaySend.setText("天：fail");
            }
        });
        Call<Result> callWeek = new RetrofitHelper().call(API.class).smsTotalGet(startWeek, endAt, "", ApiHelper.LIST_SMS_TYPE[0]);
        RetrofitHelper.enqueue(callWeek, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                tvSmsWeekSend.setText("周：" + data.getTotal());
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                tvSmsWeekSend.setText("周：fail");
            }
        });
    }

    private void getEntryData() {
        long end = DateUtils.getCurrentLong();
        long startDay = (end - ConstantUtils.DAY) / 1000;
        long startWeek = (end - ConstantUtils.DAY * 7) / 1000;
        Call<Result> callDay = new RetrofitHelper().call(API.class).entryTotalGet(startDay, end);
        RetrofitHelper.enqueue(callDay, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                tvUserDayActive.setText("天：" + data.getTotal());
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                tvUserDayActive.setText("天：fail");
            }
        });
        Call<Result> callWeek = new RetrofitHelper().call(API.class).entryTotalGet(startWeek, end);
        RetrofitHelper.enqueue(callWeek, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                tvUserWeekActive.setText("周：" + data.getTotal());
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                tvUserWeekActive.setText("周：fail");
            }
        });
    }

    private void getApiData() {
        long end = DateUtils.getCurrentLong();
        long startHour = (end - ConstantUtils.HOUR) / 1000;
        long startDay = (end - ConstantUtils.DAY) / 1000;
        Call<Result> callHour = new RetrofitHelper().call(API.class).apiTotalGet(startHour, end);
        RetrofitHelper.enqueue(callHour, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                tvApiHour.setText("时：" + data.getTotal());
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                tvApiHour.setText("时：fail");
            }
        });
        Call<Result> callDay = new RetrofitHelper().call(API.class).apiTotalGet(startDay, end);
        RetrofitHelper.enqueue(callDay, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                tvApiDay.setText("天：" + data.getTotal());
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                tvApiDay.setText("天：fail");
            }
        });
    }

    private void getSuggestData() {
        long startDay = (DateUtils.getCurrentLong() - ConstantUtils.DAY) / 1000;
        Call<Result> callSuggest = new RetrofitHelper().call(API.class).setSuggestTotalGet(startDay);
        RetrofitHelper.enqueue(callSuggest, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                tvSuggestDayNew.setText("帖子：" + data.getTotal());
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                tvSuggestDayNew.setText("帖子：fail");
            }
        });
        Call<Result> callSuggestComment = new RetrofitHelper().call(API.class).setSuggestCommentTotalGet(startDay);
        RetrofitHelper.enqueue(callSuggestComment, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                tvSuggestCommentDayNew.setText("评论：" + data.getTotal());
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                tvSuggestCommentDayNew.setText("评论：fail");
            }
        });
    }

}
