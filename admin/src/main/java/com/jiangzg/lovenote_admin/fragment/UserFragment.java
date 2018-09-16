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
import com.jiangzg.lovenote_admin.activity.SuggestFollowActivity;
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

    @BindView(R.id.cvUser)
    CardView cvUser;
    @BindView(R.id.tvUser1)
    TextView tvUser1;
    @BindView(R.id.tvUser2)
    TextView tvUser2;
    @BindView(R.id.tvUser3)
    TextView tvUser3;

    @BindView(R.id.cvEntry)
    CardView cvEntry;
    @BindView(R.id.tvEntry1)
    TextView tvEntry1;
    @BindView(R.id.tvEntry2)
    TextView tvEntry2;
    @BindView(R.id.tvEntry3)
    TextView tvEntry3;

    @BindView(R.id.cvSms)
    CardView cvSms;
    @BindView(R.id.tvSms1)
    TextView tvSms1;
    @BindView(R.id.tvSms2)
    TextView tvSms2;
    @BindView(R.id.tvSms3)
    TextView tvSms3;

    @BindView(R.id.cvApi)
    CardView cvApi;
    @BindView(R.id.tvApi1)
    TextView tvApi1;
    @BindView(R.id.tvApi2)
    TextView tvApi2;
    @BindView(R.id.tvApi3)
    TextView tvApi3;

    @BindView(R.id.cvSuggest)
    CardView cvSuggest;
    @BindView(R.id.tvSuggest1)
    TextView tvSuggest1;
    @BindView(R.id.tvSuggest2)
    TextView tvSuggest2;
    @BindView(R.id.tvComment1)
    TextView tvComment1;
    @BindView(R.id.tvComment2)
    TextView tvComment2;

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

    @OnClick({R.id.cvUser, R.id.cvSms, R.id.cvEntry, R.id.cvApi, R.id.cvSuggest})
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
            case R.id.cvSuggest: // 意见反馈
                SuggestFollowActivity.goActivity(mFragment);
                break;
        }
    }

    private void getUserData() {
        long currentLong = DateUtils.getCurrentLong();
        long startH1 = (currentLong - ConstantUtils.HOUR) / 1000;
        long startD1 = (currentLong - ConstantUtils.DAY) / 1000;
        long startD7 = (currentLong - ConstantUtils.DAY * 7) / 1000;
        long endAt = currentLong / 1000;
        Call<Result> callH1 = new RetrofitHelper().call(API.class).userTotalGet(startH1, endAt);
        RetrofitHelper.enqueue(callH1, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                tvUser1.setText("1h：" + data.getTotal());
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                tvUser1.setText("1h：fail");
            }
        });
        Call<Result> callD1 = new RetrofitHelper().call(API.class).userTotalGet(startD1, endAt);
        RetrofitHelper.enqueue(callD1, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                tvUser2.setText("1d：" + data.getTotal());
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                tvUser2.setText("1d：fail");
            }
        });
        Call<Result> callD7 = new RetrofitHelper().call(API.class).userTotalGet(startD7, endAt);
        RetrofitHelper.enqueue(callD7, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                tvUser3.setText("7d：" + data.getTotal());
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                tvUser3.setText("7d：fail");
            }
        });
    }

    private void getSmsData() {
        long currentLong = DateUtils.getCurrentLong();
        long startH1 = (currentLong - ConstantUtils.HOUR) / 1000;
        long startD1 = (currentLong - ConstantUtils.DAY) / 1000;
        long startD7 = (currentLong - ConstantUtils.DAY * 7) / 1000;
        long endAt = currentLong / 1000;
        Call<Result> callH1 = new RetrofitHelper().call(API.class).smsTotalGet(startH1, endAt, "", ApiHelper.LIST_SMS_TYPE[0]);
        RetrofitHelper.enqueue(callH1, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                tvSms1.setText("1h：" + data.getTotal());
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                tvSms1.setText("1h：fail");
            }
        });
        Call<Result> callD1 = new RetrofitHelper().call(API.class).smsTotalGet(startD1, endAt, "", ApiHelper.LIST_SMS_TYPE[0]);
        RetrofitHelper.enqueue(callD1, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                tvSms2.setText("1d：" + data.getTotal());
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                tvSms2.setText("1d：fail");
            }
        });
        Call<Result> callD7 = new RetrofitHelper().call(API.class).smsTotalGet(startD7, endAt, "", ApiHelper.LIST_SMS_TYPE[0]);
        RetrofitHelper.enqueue(callD7, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                tvSms3.setText("7d：" + data.getTotal());
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                tvSms3.setText("7d：fail");
            }
        });
    }

    private void getEntryData() {
        long currentLong = DateUtils.getCurrentLong();
        long startH1 = (currentLong - ConstantUtils.HOUR) / 1000;
        long startD1 = (currentLong - ConstantUtils.DAY) / 1000;
        long startD7 = (currentLong - ConstantUtils.DAY * 7) / 1000;
        long endAt = currentLong / 1000;
        Call<Result> callH1 = new RetrofitHelper().call(API.class).entryTotalGet(startH1, endAt);
        RetrofitHelper.enqueue(callH1, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                tvEntry1.setText("1h：" + data.getTotal());
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                tvEntry1.setText("1h：fail");
            }
        });
        Call<Result> callD1 = new RetrofitHelper().call(API.class).entryTotalGet(startD1, endAt);
        RetrofitHelper.enqueue(callD1, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                tvEntry2.setText("1d：" + data.getTotal());
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                tvEntry2.setText("1d：fail");
            }
        });
        Call<Result> callD7 = new RetrofitHelper().call(API.class).entryTotalGet(startD7, endAt);
        RetrofitHelper.enqueue(callD7, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                tvEntry3.setText("7d：" + data.getTotal());
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                tvEntry3.setText("7d：fail");
            }
        });
    }

    private void getApiData() {
        long currentLong = DateUtils.getCurrentLong();
        long startH1 = (currentLong - ConstantUtils.HOUR) / 1000;
        long startH5 = (currentLong - ConstantUtils.HOUR * 5) / 1000;
        long startD1 = (currentLong - ConstantUtils.DAY) / 1000;
        long endAt = currentLong / 1000;
        Call<Result> callH1 = new RetrofitHelper().call(API.class).apiTotalGet(startH1, endAt);
        RetrofitHelper.enqueue(callH1, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                tvApi1.setText("1h：" + data.getTotal());
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                tvApi1.setText("1h：fail");
            }
        });
        Call<Result> callH5 = new RetrofitHelper().call(API.class).apiTotalGet(startH5, endAt);
        RetrofitHelper.enqueue(callH5, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                tvApi2.setText("5h：" + data.getTotal());
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                tvApi2.setText("5h：fail");
            }
        });
        Call<Result> callD1 = new RetrofitHelper().call(API.class).apiTotalGet(startD1, endAt);
        RetrofitHelper.enqueue(callD1, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                tvApi3.setText("1d：" + data.getTotal());
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                tvApi3.setText("1d：fail");
            }
        });
    }

    private void getSuggestData() {
        long startH1 = (DateUtils.getCurrentLong() - ConstantUtils.HOUR) / 1000;
        long startD1 = (DateUtils.getCurrentLong() - ConstantUtils.DAY) / 1000;
        Call<Result> callSuggestH1 = new RetrofitHelper().call(API.class).setSuggestTotalGet(startH1);
        RetrofitHelper.enqueue(callSuggestH1, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                tvSuggest1.setText("帖子(1h)：" + data.getTotal());
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                tvSuggest1.setText("帖子(1h)：fail");
            }
        });
        Call<Result> callSuggestD1 = new RetrofitHelper().call(API.class).setSuggestTotalGet(startD1);
        RetrofitHelper.enqueue(callSuggestD1, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                tvSuggest2.setText("帖子(1d)：" + data.getTotal());
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                tvSuggest2.setText("帖子(1d)：fail");
            }
        });
        Call<Result> callCommentH1 = new RetrofitHelper().call(API.class).setSuggestCommentTotalGet(startH1);
        RetrofitHelper.enqueue(callCommentH1, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                tvComment1.setText("评论(1h)：" + data.getTotal());
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                tvComment1.setText("评论(1h)：fail");
            }
        });
        Call<Result> callCommentD1 = new RetrofitHelper().call(API.class).setSuggestCommentTotalGet(startD1);
        RetrofitHelper.enqueue(callCommentD1, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                tvComment2.setText("评论(1d)：" + data.getTotal());
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                tvComment2.setText("评论(1d)：fail");
            }
        });
    }

}
