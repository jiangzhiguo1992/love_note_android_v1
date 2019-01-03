package com.jiangzg.lovenote_admin.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.TextView;

import com.jiangzg.base.common.DateUtils;
import com.jiangzg.base.common.TimeUnit;
import com.jiangzg.lovenote_admin.R;
import com.jiangzg.lovenote_admin.activity.ApiListActivity;
import com.jiangzg.lovenote_admin.activity.CoupleListActivity;
import com.jiangzg.lovenote_admin.activity.EntryListActivity;
import com.jiangzg.lovenote_admin.activity.PlaceListActivity;
import com.jiangzg.lovenote_admin.activity.SignListActivity;
import com.jiangzg.lovenote_admin.activity.SmsListActivity;
import com.jiangzg.lovenote_admin.activity.TrendsListActivity;
import com.jiangzg.lovenote_admin.activity.UserListActivity;
import com.jiangzg.lovenote_admin.base.BaseFragment;
import com.jiangzg.lovenote_admin.domain.FiledInfo;
import com.jiangzg.lovenote_admin.domain.Result;
import com.jiangzg.lovenote_admin.domain.Trends;
import com.jiangzg.lovenote_admin.helper.API;
import com.jiangzg.lovenote_admin.helper.ApiHelper;
import com.jiangzg.lovenote_admin.helper.RetrofitHelper;

import java.util.Calendar;
import java.util.List;

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
    @BindView(R.id.tvUser5)
    TextView tvUser5;
    @BindView(R.id.tvUser6)
    TextView tvUser6;

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
    @BindView(R.id.tvSms5)
    TextView tvSms5;
    @BindView(R.id.tvSms6)
    TextView tvSms6;

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

    @BindView(R.id.cvCouple)
    CardView cvCouple;
    @BindView(R.id.tvCouple1)
    TextView tvCouple1;
    @BindView(R.id.tvCouple2)
    TextView tvCouple2;
    @BindView(R.id.tvCouple3)
    TextView tvCouple3;
    @BindView(R.id.tvCouple4)
    TextView tvCouple4;
    @BindView(R.id.tvCouple5)
    TextView tvCouple5;

    @BindView(R.id.cvSign)
    CardView cvSign;
    @BindView(R.id.tvSign1)
    TextView tvSign1;
    @BindView(R.id.tvSign2)
    TextView tvSign2;
    @BindView(R.id.tvSign3)
    TextView tvSign3;
    @BindView(R.id.tvSign4)
    TextView tvSign4;
    @BindView(R.id.tvSign5)
    TextView tvSign5;

    @BindView(R.id.cvNote)
    CardView cvNote;
    @BindView(R.id.tvNote1)
    TextView tvNote1;
    @BindView(R.id.tvNote2)
    TextView tvNote2;
    @BindView(R.id.tvNote3)
    TextView tvNote3;
    @BindView(R.id.tvNote4)
    TextView tvNote4;
    @BindView(R.id.tvNote5)
    TextView tvNote5;

    @BindView(R.id.cvPlace)
    CardView cvPlace;
    @BindView(R.id.tvPlace1)
    TextView tvPlace1;
    @BindView(R.id.tvPlace2)
    TextView tvPlace2;
    @BindView(R.id.tvPlace3)
    TextView tvPlace3;
    @BindView(R.id.tvPlace4)
    TextView tvPlace4;
    @BindView(R.id.tvPlace5)
    TextView tvPlace5;

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
        getCoupleData();
        getSignData();
        getNoteData();
        getPlaceData();
    }

    @Override
    protected void onFinish(Bundle state) {
    }

    @OnClick({R.id.cvUser, R.id.cvSms, R.id.cvEntry, R.id.cvApi,
            R.id.cvCouple, R.id.cvSign, R.id.cvNote, R.id.cvPlace})
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
            case R.id.cvCouple: // 配对
                CoupleListActivity.goActivity(mFragment);
                break;
            case R.id.cvSign: // 签到
                SignListActivity.goActivity(mFragment);
                break;
            case R.id.cvNote: // 记录
                TrendsListActivity.goActivity(mFragment);
                break;
            case R.id.cvPlace: // 位置
                PlaceListActivity.goActivity(mFragment);
                break;
        }
    }

    private void getUserData() {
        long current = DateUtils.getCurrentLong() / 1000;
        long startH1 = (DateUtils.getCurrentLong() - TimeUnit.HOUR) / 1000;
        Calendar calendar = DateUtils.getCurrentCal();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        long d1Start = calendar.getTimeInMillis() / 1000;
        long d2Start = d1Start - 60 * 60 * 24;
        long d3Start = d2Start - 60 * 60 * 24;
        long d4Start = d3Start - 60 * 60 * 24;
        long d5Start = d4Start - 60 * 60 * 24;
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
        Call<Result> callD9 = new RetrofitHelper().call(API.class).userTotalGet(d4Start, d3Start);
        RetrofitHelper.enqueue(callD9, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                tvUser5.setText("大：" + data.getTotal());
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                tvUser5.setText("大：fail");
            }
        });
        Call<Result> callD10 = new RetrofitHelper().call(API.class).userTotalGet(d5Start, d4Start);
        RetrofitHelper.enqueue(callD10, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                tvUser6.setText("巨：" + data.getTotal());
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                tvUser6.setText("巨：fail");
            }
        });
    }

    private void getEntryData() {
        long current = DateUtils.getCurrentLong() / 1000;
        long startH1 = (DateUtils.getCurrentLong() - TimeUnit.HOUR) / 1000;
        Calendar calendar = DateUtils.getCurrentCal();
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

    private void getSmsData() {
        long current = DateUtils.getCurrentLong() / 1000;
        long startH1 = (DateUtils.getCurrentLong() - TimeUnit.HOUR) / 1000;
        Calendar calendar = DateUtils.getCurrentCal();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        long d1Start = calendar.getTimeInMillis() / 1000;
        long d2Start = d1Start - 60 * 60 * 24;
        long d3Start = d2Start - 60 * 60 * 24;
        long d4Start = d3Start - 60 * 60 * 24;
        long d5Start = d4Start - 60 * 60 * 24;
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
        Call<Result> callD9 = new RetrofitHelper().call(API.class).smsTotalGet(d4Start, d3Start, "", ApiHelper.LIST_SMS_TYPE[0]);
        RetrofitHelper.enqueue(callD9, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                tvSms5.setText("大：" + data.getTotal());
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                tvSms5.setText("大：fail");
            }
        });
        Call<Result> callD10 = new RetrofitHelper().call(API.class).smsTotalGet(d5Start, d4Start, "", ApiHelper.LIST_SMS_TYPE[0]);
        RetrofitHelper.enqueue(callD10, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                tvSms6.setText("巨：" + data.getTotal());
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                tvSms6.setText("巨：fail");
            }
        });
    }

    private void getApiData() {
        long current = DateUtils.getCurrentLong() / 1000;
        long startH1 = (DateUtils.getCurrentLong() - TimeUnit.HOUR) / 1000;
        Calendar calendar = DateUtils.getCurrentCal();
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

    private void getCoupleData() {
        long current = DateUtils.getCurrentLong() / 1000;
        //long startH1 = (DateUtils.getCurrentLong() - ConstantUtils.HOUR) / 1000;
        Calendar calendar = DateUtils.getCurrentCal();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        long d1Start = calendar.getTimeInMillis() / 1000;
        long d2Start = d1Start - 60 * 60 * 24;
        long d3Start = d2Start - 60 * 60 * 24;
        long d4Start = d3Start - 60 * 60 * 24;
        long d5Start = d4Start - 60 * 60 * 24;
        //Call<Result> callH1 = new RetrofitHelper().call(API.class).coupleTotalGet(startH1, current);
        //RetrofitHelper.enqueue(callH1, null, new RetrofitHelper.CallBack() {
        //    @Override
        //    public void onResponse(int code, String message, Result.Data data) {
        //        tvCouple1.setText("时：" + data.getTotal());
        //    }
        //
        //    @Override
        //    public void onFailure(int code, String message, Result.Data data) {
        //        tvCouple1.setText("时：fail");
        //    }
        //});
        Call<Result> callD1 = new RetrofitHelper().call(API.class).coupleTotalGet(d1Start, current);
        RetrofitHelper.enqueue(callD1, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                tvCouple1.setText("今：" + data.getTotal());
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                tvCouple1.setText("今：fail");
            }
        });
        Call<Result> callD7 = new RetrofitHelper().call(API.class).coupleTotalGet(d2Start, d1Start);
        RetrofitHelper.enqueue(callD7, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                tvCouple2.setText("昨：" + data.getTotal());
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                tvCouple2.setText("昨：fail");
            }
        });
        Call<Result> callD8 = new RetrofitHelper().call(API.class).coupleTotalGet(d3Start, d2Start);
        RetrofitHelper.enqueue(callD8, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                tvCouple3.setText("前：" + data.getTotal());
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                tvCouple3.setText("前：fail");
            }
        });
        Call<Result> callD9 = new RetrofitHelper().call(API.class).coupleTotalGet(d4Start, d3Start);
        RetrofitHelper.enqueue(callD9, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                tvCouple4.setText("大：" + data.getTotal());
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                tvCouple4.setText("大：fail");
            }
        });
        Call<Result> callD10 = new RetrofitHelper().call(API.class).coupleTotalGet(d5Start, d4Start);
        RetrofitHelper.enqueue(callD10, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                tvCouple5.setText("巨：" + data.getTotal());
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                tvCouple5.setText("巨：fail");
            }
        });
    }

    private void getSignData() {
        long currentLong = DateUtils.getCurrentLong();
        Calendar c0 = DateUtils.getCal(currentLong);
        Calendar c1 = DateUtils.getCal(currentLong - TimeUnit.DAY);
        Calendar c2 = DateUtils.getCal(currentLong - TimeUnit.DAY * 2);
        Calendar c3 = DateUtils.getCal(currentLong - TimeUnit.DAY * 3);
        Calendar c4 = DateUtils.getCal(currentLong - TimeUnit.DAY * 4);
        int year0 = c0.get(Calendar.YEAR);
        int year1 = c1.get(Calendar.YEAR);
        int year2 = c2.get(Calendar.YEAR);
        int year3 = c3.get(Calendar.YEAR);
        int year4 = c4.get(Calendar.YEAR);
        int month0 = c0.get(Calendar.MONTH) + 1;
        int month1 = c1.get(Calendar.MONTH) + 1;
        int month2 = c2.get(Calendar.MONTH) + 1;
        int month3 = c3.get(Calendar.MONTH) + 1;
        int month4 = c4.get(Calendar.MONTH) + 1;
        int day0 = c0.get(Calendar.DAY_OF_MONTH);
        int day1 = c1.get(Calendar.DAY_OF_MONTH);
        int day2 = c2.get(Calendar.DAY_OF_MONTH);
        int day3 = c3.get(Calendar.DAY_OF_MONTH);
        int day4 = c4.get(Calendar.DAY_OF_MONTH);
        Call<Result> call0 = new RetrofitHelper().call(API.class).moreSignTotalGet(year0, month0, day0);
        RetrofitHelper.enqueue(call0, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                tvSign1.setText("今：" + data.getTotal());
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                tvSign1.setText("今：fail");
            }
        });
        Call<Result> call1 = new RetrofitHelper().call(API.class).moreSignTotalGet(year1, month1, day1);
        RetrofitHelper.enqueue(call1, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                tvSign2.setText("昨：" + data.getTotal());
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                tvSign2.setText("昨：fail");
            }
        });
        Call<Result> call2 = new RetrofitHelper().call(API.class).moreSignTotalGet(year2, month2, day2);
        RetrofitHelper.enqueue(call2, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                tvSign3.setText("前：" + data.getTotal());
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                tvSign3.setText("前：fail");
            }
        });
        Call<Result> call3 = new RetrofitHelper().call(API.class).moreSignTotalGet(year3, month3, day3);
        RetrofitHelper.enqueue(call3, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                tvSign4.setText("大：" + data.getTotal());
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                tvSign4.setText("大：fail");
            }
        });
        Call<Result> call4 = new RetrofitHelper().call(API.class).moreSignTotalGet(year4, month4, day4);
        RetrofitHelper.enqueue(call4, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                tvSign5.setText("巨：" + data.getTotal());
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                tvSign5.setText("巨：fail");
            }
        });
    }

    private void getNoteData() {
        long currentLong = DateUtils.getCurrentLong();
        long start = (currentLong - TimeUnit.DAY) / 1000;
        long end = currentLong / 1000;
        Call<Result> call = new RetrofitHelper().call(API.class).noteTrendsConListGet(start, end);
        RetrofitHelper.enqueue(call, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                List<FiledInfo> infoList = data.getInfoList();
                if (infoList == null || infoList.size() <= 0) return;
                for (int i = 0; i < infoList.size(); i++) {
                    FiledInfo filedInfo = infoList.get(i);
                    String name = Trends.getContentShow(Integer.parseInt(filedInfo.getName()));
                    long count = filedInfo.getCount();
                    String show = name + "：" + count;
                    if (i == 0) {
                        tvNote1.setText(show);
                    } else if (i == 1) {
                        tvNote2.setText(show);
                    } else if (i == 2) {
                        tvNote3.setText(show);
                    } else if (i == 3) {
                        tvNote4.setText(show);
                    } else if (i == 4) {
                        tvNote5.setText(show);
                    } else {
                        break;
                    }
                }
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
            }
        });
    }

    private void getPlaceData() {
        long currentLong = DateUtils.getCurrentLong();
        long start = (currentLong - TimeUnit.DAY) / 1000;
        long end = currentLong / 1000;
        Call<Result> call = new RetrofitHelper().call(API.class).couplePlaceGroupGet(start, end, "city");
        RetrofitHelper.enqueue(call, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                List<FiledInfo> infoList = data.getInfoList();
                if (infoList == null || infoList.size() <= 0) return;
                for (int i = 0; i < infoList.size(); i++) {
                    FiledInfo filedInfo = infoList.get(i);
                    String name = filedInfo.getName();
                    long count = filedInfo.getCount();
                    String show = name + "：" + count;
                    if (i == 0) {
                        tvPlace1.setText(show);
                    } else if (i == 1) {
                        tvPlace2.setText(show);
                    } else if (i == 2) {
                        tvPlace3.setText(show);
                    } else if (i == 3) {
                        tvPlace4.setText(show);
                    } else if (i == 4) {
                        tvPlace5.setText(show);
                    } else {
                        break;
                    }
                }
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
            }
        });
    }

}
