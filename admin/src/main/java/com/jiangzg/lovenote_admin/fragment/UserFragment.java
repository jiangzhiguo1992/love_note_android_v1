package com.jiangzg.lovenote_admin.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.TextView;

import com.jiangzg.base.common.ConstantUtils;
import com.jiangzg.base.time.DateUtils;
import com.jiangzg.lovenote_admin.R;
import com.jiangzg.lovenote_admin.activity.ApiListActivity;
import com.jiangzg.lovenote_admin.activity.EntryListActivity;
import com.jiangzg.lovenote_admin.activity.SmsListActivity;
import com.jiangzg.lovenote_admin.activity.SuggestCommentListActivity;
import com.jiangzg.lovenote_admin.activity.SuggestListActivity;
import com.jiangzg.lovenote_admin.activity.UserListActivity;
import com.jiangzg.lovenote_admin.base.BaseFragment;
import com.jiangzg.lovenote_admin.domain.Result;
import com.jiangzg.lovenote_admin.helper.API;
import com.jiangzg.lovenote_admin.helper.ApiHelper;
import com.jiangzg.lovenote_admin.helper.RetrofitHelper;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;

public class UserFragment extends BaseFragment<UserFragment> {

    @BindView(R.id.cvUser)
    CardView cvUser;
    @BindView(R.id.tvUser1)
    TextView tvUser1;
    @BindView(R.id.tvUser2)
    TextView tvUser2;
    @BindView(R.id.tvUser3)
    TextView tvUser3;
    @BindView(R.id.tvUser4)
    TextView tvUser4;

    @BindView(R.id.cvSms)
    CardView cvSms;
    @BindView(R.id.tvSms1)
    TextView tvSms1;
    @BindView(R.id.tvSms2)
    TextView tvSms2;
    @BindView(R.id.tvSms3)
    TextView tvSms3;
    @BindView(R.id.tvSms4)
    TextView tvSms4;

    @BindView(R.id.cvEntry)
    CardView cvEntry;
    @BindView(R.id.tvEntry1)
    TextView tvEntry1;
    @BindView(R.id.tvEntry2)
    TextView tvEntry2;
    @BindView(R.id.tvEntry3)
    TextView tvEntry3;
    @BindView(R.id.tvEntry4)
    TextView tvEntry4;
    @BindView(R.id.tvEntry5)
    TextView tvEntry5;
    @BindView(R.id.tvEntry6)
    TextView tvEntry6;

    @BindView(R.id.cvApi)
    CardView cvApi;
    @BindView(R.id.tvApi1)
    TextView tvApi1;
    @BindView(R.id.tvApi2)
    TextView tvApi2;
    @BindView(R.id.tvApi3)
    TextView tvApi3;
    @BindView(R.id.tvApi4)
    TextView tvApi4;
    @BindView(R.id.tvApi5)
    TextView tvApi5;
    @BindView(R.id.tvApi6)
    TextView tvApi6;

    @BindView(R.id.cvSuggest)
    CardView cvSuggest;
    @BindView(R.id.tvSuggest1)
    TextView tvSuggest1;
    @BindView(R.id.tvSuggest2)
    TextView tvSuggest2;
    @BindView(R.id.tvSuggest3)
    TextView tvSuggest3;

    @BindView(R.id.cvSuggestComment)
    CardView cvSuggestComment;
    @BindView(R.id.tvSuggestComment1)
    TextView tvSuggestComment1;
    @BindView(R.id.tvSuggestComment2)
    TextView tvSuggestComment2;
    @BindView(R.id.tvSuggestComment3)
    TextView tvSuggestComment3;

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
        getSuggestCommentData();
    }

    @Override
    protected void onFinish(Bundle state) {
    }

    @OnClick({R.id.cvUser, R.id.cvSms,
            R.id.cvEntry, R.id.cvApi,
            R.id.cvSuggest, R.id.cvSuggestComment})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.cvUser: // 注册用户
                UserListActivity.goActivity(mFragment);
                break;
            case R.id.cvSms: // 短信发送
                SmsListActivity.goActivity(mFragment);
                break;
            case R.id.cvEntry: // 活跃用户
                EntryListActivity.goActivity(mFragment);
                break;
            case R.id.cvApi: // 用户行为
                ApiListActivity.goActivity(mFragment);
                break;
            case R.id.cvSuggest: // 意见反馈(帖子)
                SuggestListActivity.goActivity(mFragment);
                break;
            case R.id.cvSuggestComment: // 意见反馈(评论)
                SuggestCommentListActivity.goActivity(mFragment);
                break;
        }
    }

    private void getUserData() {
        long current = DateUtils.getCurrentLong() / 1000;
        long startH1 = (DateUtils.getCurrentLong() - ConstantUtils.HOUR) / 1000;
        Calendar calendar = DateUtils.getCurrentCalendar();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        long d1Start = calendar.getTimeInMillis() / 1000;
        long d2Start = d1Start - 60 * 60 * 24;
        long d3Start = d2Start - 60 * 60 * 24;
        Call<Result> callH1 = new RetrofitHelper().call(API.class).userTotalGet(startH1, current);
        RetrofitHelper.enqueue(callH1, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                tvUser1.setText("时：" + data.getTotal());
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                tvUser1.setText("时：fail");
            }
        });
        Call<Result> callD1 = new RetrofitHelper().call(API.class).userTotalGet(d1Start, current);
        RetrofitHelper.enqueue(callD1, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                tvUser2.setText("今：" + data.getTotal());
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                tvUser2.setText("今：fail");
            }
        });
        Call<Result> callD7 = new RetrofitHelper().call(API.class).userTotalGet(d2Start, d1Start);
        RetrofitHelper.enqueue(callD7, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                tvUser3.setText("昨：" + data.getTotal());
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                tvUser3.setText("昨：fail");
            }
        });
        Call<Result> callD8 = new RetrofitHelper().call(API.class).userTotalGet(d3Start, d2Start);
        RetrofitHelper.enqueue(callD8, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                tvUser4.setText("前：" + data.getTotal());
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                tvUser4.setText("前：fail");
            }
        });
    }

    private void getSmsData() {
        long current = DateUtils.getCurrentLong() / 1000;
        long startH1 = (DateUtils.getCurrentLong() - ConstantUtils.HOUR) / 1000;
        Calendar calendar = DateUtils.getCurrentCalendar();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        long d1Start = calendar.getTimeInMillis() / 1000;
        long d2Start = d1Start - 60 * 60 * 24;
        long d3Start = d2Start - 60 * 60 * 24;
        Call<Result> callH1 = new RetrofitHelper().call(API.class).smsTotalGet(startH1, current, "", ApiHelper.LIST_SMS_TYPE[0]);
        RetrofitHelper.enqueue(callH1, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                tvSms1.setText("时：" + data.getTotal());
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                tvSms1.setText("时：fail");
            }
        });
        Call<Result> callD1 = new RetrofitHelper().call(API.class).smsTotalGet(d1Start, current, "", ApiHelper.LIST_SMS_TYPE[0]);
        RetrofitHelper.enqueue(callD1, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                tvSms2.setText("今：" + data.getTotal());
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                tvSms2.setText("今：fail");
            }
        });
        Call<Result> callD7 = new RetrofitHelper().call(API.class).smsTotalGet(d2Start, d1Start, "", ApiHelper.LIST_SMS_TYPE[0]);
        RetrofitHelper.enqueue(callD7, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                tvSms3.setText("昨：" + data.getTotal());
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                tvSms3.setText("昨：fail");
            }
        });
        Call<Result> callD8 = new RetrofitHelper().call(API.class).smsTotalGet(d3Start, d2Start, "", ApiHelper.LIST_SMS_TYPE[0]);
        RetrofitHelper.enqueue(callD8, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                tvSms4.setText("前：" + data.getTotal());
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                tvSms4.setText("前：fail");
            }
        });
    }

    private void getEntryData() {
        long current = DateUtils.getCurrentLong() / 1000;
        long startH1 = (DateUtils.getCurrentLong() - ConstantUtils.HOUR) / 1000;
        Calendar calendar = DateUtils.getCurrentCalendar();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        long d1Start = calendar.getTimeInMillis() / 1000;
        long d2Start = d1Start - 60 * 60 * 24;
        long d3Start = d2Start - 60 * 60 * 24;
        long d4Start = d3Start - 60 * 60 * 24;
        long d5Start = d4Start - 60 * 60 * 24;
        Call<Result> callH1 = new RetrofitHelper().call(API.class).entryTotalGet(startH1, current);
        RetrofitHelper.enqueue(callH1, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                tvEntry1.setText("时：" + data.getTotal());
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                tvEntry1.setText("时：fail");
            }
        });
        Call<Result> callD1 = new RetrofitHelper().call(API.class).entryTotalGet(d1Start, current);
        RetrofitHelper.enqueue(callD1, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                tvEntry2.setText("今：" + data.getTotal());
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                tvEntry2.setText("今：fail");
            }
        });
        Call<Result> callD7 = new RetrofitHelper().call(API.class).entryTotalGet(d2Start, d1Start);
        RetrofitHelper.enqueue(callD7, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                tvEntry3.setText("昨：" + data.getTotal());
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                tvEntry3.setText("昨：fail");
            }
        });
        Call<Result> callD8 = new RetrofitHelper().call(API.class).entryTotalGet(d3Start, d2Start);
        RetrofitHelper.enqueue(callD8, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                tvEntry4.setText("前：" + data.getTotal());
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                tvEntry4.setText("前：fail");
            }
        });
        Call<Result> callD9 = new RetrofitHelper().call(API.class).entryTotalGet(d4Start, d3Start);
        RetrofitHelper.enqueue(callD9, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                tvEntry5.setText("大：" + data.getTotal());
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                tvEntry5.setText("大：fail");
            }
        });
        Call<Result> callD10 = new RetrofitHelper().call(API.class).entryTotalGet(d5Start, d4Start);
        RetrofitHelper.enqueue(callD10, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                tvEntry6.setText("巨：" + data.getTotal());
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                tvEntry6.setText("巨：fail");
            }
        });
    }

    private void getApiData() {
        long current = DateUtils.getCurrentLong() / 1000;
        long startH1 = (DateUtils.getCurrentLong() - ConstantUtils.HOUR) / 1000;
        Calendar calendar = DateUtils.getCurrentCalendar();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        long d1Start = calendar.getTimeInMillis() / 1000;
        long d2Start = d1Start - 60 * 60 * 24;
        long d3Start = d2Start - 60 * 60 * 24;
        long d4Start = d3Start - 60 * 60 * 24;
        long d5Start = d4Start - 60 * 60 * 24;
        Call<Result> callH1 = new RetrofitHelper().call(API.class).apiTotalGet(startH1, current);
        RetrofitHelper.enqueue(callH1, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                tvApi1.setText("时：" + data.getTotal());
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                tvApi1.setText("时：fail");
            }
        });
        Call<Result> callH5 = new RetrofitHelper().call(API.class).apiTotalGet(d1Start, current);
        RetrofitHelper.enqueue(callH5, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                tvApi2.setText("今：" + data.getTotal());
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                tvApi2.setText("今：fail");
            }
        });
        Call<Result> callD1 = new RetrofitHelper().call(API.class).apiTotalGet(d2Start, d1Start);
        RetrofitHelper.enqueue(callD1, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                tvApi3.setText("昨：" + data.getTotal());
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                tvApi3.setText("昨：fail");
            }
        });
        Call<Result> callD2 = new RetrofitHelper().call(API.class).apiTotalGet(d3Start, d2Start);
        RetrofitHelper.enqueue(callD2, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                tvApi4.setText("前：" + data.getTotal());
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                tvApi4.setText("前：fail");
            }
        });
        Call<Result> callD9 = new RetrofitHelper().call(API.class).apiTotalGet(d4Start, d3Start);
        RetrofitHelper.enqueue(callD9, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                tvApi5.setText("大：" + data.getTotal());
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                tvApi5.setText("大：fail");
            }
        });
        Call<Result> callD10 = new RetrofitHelper().call(API.class).apiTotalGet(d5Start, d4Start);
        RetrofitHelper.enqueue(callD10, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                tvApi6.setText("巨：" + data.getTotal());
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                tvApi6.setText("巨：fail");
            }
        });
    }

    private void getSuggestData() {
        long startH1 = (DateUtils.getCurrentLong() - ConstantUtils.HOUR) / 1000;
        long startD1 = (DateUtils.getCurrentLong() - ConstantUtils.DAY) / 1000;
        long startD7 = (DateUtils.getCurrentLong() - ConstantUtils.DAY * 3) / 1000;
        Call<Result> callSuggestH1 = new RetrofitHelper().call(API.class).setSuggestTotalGet(startH1);
        RetrofitHelper.enqueue(callSuggestH1, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                tvSuggest1.setText("1h：" + data.getTotal());
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                tvSuggest1.setText("1h：fail");
            }
        });
        Call<Result> callSuggestD1 = new RetrofitHelper().call(API.class).setSuggestTotalGet(startD1);
        RetrofitHelper.enqueue(callSuggestD1, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                tvSuggest2.setText("1d：" + data.getTotal());
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                tvSuggest2.setText("1d：fail");
            }
        });
        Call<Result> callSuggestD7 = new RetrofitHelper().call(API.class).setSuggestTotalGet(startD7);
        RetrofitHelper.enqueue(callSuggestD7, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                tvSuggest3.setText("3d：" + data.getTotal());
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                tvSuggest3.setText("3d：fail");
            }
        });
    }

    private void getSuggestCommentData() {
        long startH1 = (DateUtils.getCurrentLong() - ConstantUtils.HOUR) / 1000;
        long startD1 = (DateUtils.getCurrentLong() - ConstantUtils.DAY) / 1000;
        long startD7 = (DateUtils.getCurrentLong() - ConstantUtils.DAY * 3) / 1000;
        Call<Result> callCommentH1 = new RetrofitHelper().call(API.class).setSuggestCommentTotalGet(startH1);
        RetrofitHelper.enqueue(callCommentH1, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                tvSuggestComment1.setText("1h：" + data.getTotal());
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                tvSuggestComment1.setText("1h：fail");
            }
        });
        Call<Result> callCommentD1 = new RetrofitHelper().call(API.class).setSuggestCommentTotalGet(startD1);
        RetrofitHelper.enqueue(callCommentD1, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                tvSuggestComment2.setText("1d：" + data.getTotal());
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                tvSuggestComment2.setText("1d：fail");
            }
        });
        Call<Result> callCommentD7 = new RetrofitHelper().call(API.class).setSuggestCommentTotalGet(startD7);
        RetrofitHelper.enqueue(callCommentD7, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                tvSuggestComment3.setText("3d：" + data.getTotal());
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                tvSuggestComment3.setText("3d：fail");
            }
        });
    }

}
