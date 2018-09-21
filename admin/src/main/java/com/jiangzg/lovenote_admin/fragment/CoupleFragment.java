package com.jiangzg.lovenote_admin.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.TextView;

import com.jiangzg.base.common.ConstantUtils;
import com.jiangzg.base.time.DateUtils;
import com.jiangzg.lovenote_admin.R;
import com.jiangzg.lovenote_admin.activity.BillListActivity;
import com.jiangzg.lovenote_admin.activity.CoinListActivity;
import com.jiangzg.lovenote_admin.activity.CoupleListActivity;
import com.jiangzg.lovenote_admin.activity.MatchPeriodListActivity;
import com.jiangzg.lovenote_admin.activity.PlaceListActivity;
import com.jiangzg.lovenote_admin.activity.PostCommentListActivity;
import com.jiangzg.lovenote_admin.activity.PostListActivity;
import com.jiangzg.lovenote_admin.activity.SignListActivity;
import com.jiangzg.lovenote_admin.activity.TrendsListActivity;
import com.jiangzg.lovenote_admin.activity.VipListActivity;
import com.jiangzg.lovenote_admin.base.BaseFragment;
import com.jiangzg.lovenote_admin.domain.Coin;
import com.jiangzg.lovenote_admin.domain.FiledInfo;
import com.jiangzg.lovenote_admin.domain.MatchPeriod;
import com.jiangzg.lovenote_admin.domain.Result;
import com.jiangzg.lovenote_admin.domain.Trends;
import com.jiangzg.lovenote_admin.helper.API;
import com.jiangzg.lovenote_admin.helper.RetrofitHelper;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;

public class CoupleFragment extends BaseFragment<CoupleFragment> {

    @BindView(R.id.cvCouple)
    CardView cvCouple;
    @BindView(R.id.tvCouple1)
    TextView tvCouple1;
    @BindView(R.id.tvCouple2)
    TextView tvCouple2;
    @BindView(R.id.tvCouple3)
    TextView tvCouple3;

    @BindView(R.id.cvPlace)
    CardView cvPlace;
    @BindView(R.id.tvPlace1)
    TextView tvPlace1;
    @BindView(R.id.tvPlace2)
    TextView tvPlace2;
    @BindView(R.id.tvPlace3)
    TextView tvPlace3;

    @BindView(R.id.cvBill)
    CardView cvBill;
    @BindView(R.id.tvBill1)
    TextView tvBill1;
    @BindView(R.id.tvBill2)
    TextView tvBill2;
    @BindView(R.id.tvBill3)
    TextView tvBill3;

    @BindView(R.id.cvVip)
    CardView cvVip;
    @BindView(R.id.tvVip1)
    TextView tvVip1;
    @BindView(R.id.tvVip2)
    TextView tvVip2;
    @BindView(R.id.tvVip3)
    TextView tvVip3;

    @BindView(R.id.cvSign)
    CardView cvSign;
    @BindView(R.id.tvSign1)
    TextView tvSign1;
    @BindView(R.id.tvSign2)
    TextView tvSign2;
    @BindView(R.id.tvSign3)
    TextView tvSign3;

    @BindView(R.id.cvCoin)
    CardView cvCoin;
    @BindView(R.id.tvCoin1)
    TextView tvCoin1;
    @BindView(R.id.tvCoin2)
    TextView tvCoin2;
    @BindView(R.id.tvCoin3)
    TextView tvCoin3;

    @BindView(R.id.cvNote)
    CardView cvNote;
    @BindView(R.id.tvNote1)
    TextView tvNote1;
    @BindView(R.id.tvNote2)
    TextView tvNote2;
    @BindView(R.id.tvNote3)
    TextView tvNote3;

    @BindView(R.id.cvPost)
    CardView cvPost;
    @BindView(R.id.tvPost1)
    TextView tvPost1;
    @BindView(R.id.tvPost2)
    TextView tvPost2;
    @BindView(R.id.tvPost3)
    TextView tvPost3;

    @BindView(R.id.cvPostComment)
    CardView cvPostComment;
    @BindView(R.id.tvPostComment1)
    TextView tvPostComment1;
    @BindView(R.id.tvPostComment2)
    TextView tvPostComment2;
    @BindView(R.id.tvPostComment3)
    TextView tvPostComment3;

    @BindView(R.id.cvMatchPeriod)
    CardView cvMatchPeriod;
    @BindView(R.id.tvMatchPeriod1)
    TextView tvMatchPeriod1;
    @BindView(R.id.tvMatchPeriod2)
    TextView tvMatchPeriod2;
    @BindView(R.id.tvMatchPeriod3)
    TextView tvMatchPeriod3;

    public static CoupleFragment newFragment() {
        Bundle bundle = new Bundle();
        // bundle.putData();
        return BaseFragment.newInstance(CoupleFragment.class, bundle);
    }

    @Override
    protected int getView(Bundle data) {
        return R.layout.fragment_couple;
    }

    @Override
    protected void initView(@Nullable Bundle state) {
    }

    @Override
    protected void initData(Bundle state) {
        getCoupleData();
        getPlaceData();
        getBillData();
        getVipData();
        getSignData();
        getCoinData();
        getNoteData();
        getPostData();
        getPostCommentData();
        getMatchWorkData();
    }

    @Override
    protected void onFinish(Bundle state) {
    }

    @OnClick({R.id.cvCouple, R.id.cvPlace, R.id.cvBill, R.id.cvVip, R.id.cvSign, R.id.cvCoin,
            R.id.cvNote, R.id.cvPost, R.id.cvPostComment, R.id.cvMatchPeriod})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.cvCouple: // 配对
                CoupleListActivity.goActivity(mFragment);
                break;
            case R.id.cvPlace: // 位置
                PlaceListActivity.goActivity(mFragment);
                break;
            case R.id.cvBill: // 账单
                BillListActivity.goActivity(mFragment);
                break;
            case R.id.cvVip: // 会员
                VipListActivity.goActivity(mFragment);
                break;
            case R.id.cvSign: // 签到
                SignListActivity.goActivity(mFragment);
                break;
            case R.id.cvCoin: // 金币
                CoinListActivity.goActivity(mFragment);
                break;
            case R.id.cvNote: // 记录
                TrendsListActivity.goActivity(mFragment);
                break;
            case R.id.cvPost: // 话题帖子
                PostListActivity.goActivity(mFragment);
                break;
            case R.id.cvPostComment: // 话题评论
                PostCommentListActivity.goActivity(mFragment);
                break;
            case R.id.cvMatchPeriod: // 比拼
                MatchPeriodListActivity.goActivity(mFragment);
                break;
        }
    }

    private void getCoupleData() {
        long currentLong = DateUtils.getCurrentLong();
        long startH1 = (currentLong - ConstantUtils.HOUR) / 1000;
        long startD1 = (currentLong - ConstantUtils.DAY) / 1000;
        long startD7 = (currentLong - ConstantUtils.DAY * 7) / 1000;
        long endAt = currentLong / 1000;
        Call<Result> callH1 = new RetrofitHelper().call(API.class).coupleTotalGet(startH1, endAt);
        RetrofitHelper.enqueue(callH1, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                tvCouple1.setText("1h：" + data.getTotal());
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                tvCouple1.setText("1h：fail");
            }
        });
        Call<Result> callD1 = new RetrofitHelper().call(API.class).coupleTotalGet(startD1, endAt);
        RetrofitHelper.enqueue(callD1, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                tvCouple2.setText("1d：" + data.getTotal());
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                tvCouple2.setText("1d：fail");
            }
        });
        Call<Result> callD7 = new RetrofitHelper().call(API.class).coupleTotalGet(startD7, endAt);
        RetrofitHelper.enqueue(callD7, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                tvCouple3.setText("7d：" + data.getTotal());
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                tvCouple3.setText("7d：fail");
            }
        });
    }

    private void getPlaceData() {
        long currentLong = DateUtils.getCurrentLong();
        long start = (currentLong - ConstantUtils.DAY) / 1000;
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

    private void getBillData() {
        long currentLong = DateUtils.getCurrentLong();
        long startH1 = (currentLong - ConstantUtils.HOUR) / 1000;
        long startD1 = (currentLong - ConstantUtils.DAY) / 1000;
        long startD7 = (currentLong - ConstantUtils.DAY * 7) / 1000;
        long endAt = currentLong / 1000;
        Call<Result> callH1 = new RetrofitHelper().call(API.class).moreBillAmountGet(startH1, endAt, "", 0, 0, 0);
        RetrofitHelper.enqueue(callH1, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                String amount = String.format(Locale.getDefault(), "%.1f", data.getAmount());
                tvBill1.setText("1h：" + amount);
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                tvBill1.setText("1h：fail");
            }
        });
        Call<Result> callD1 = new RetrofitHelper().call(API.class).moreBillAmountGet(startD1, endAt, "", 0, 0, 0);
        RetrofitHelper.enqueue(callD1, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                String amount = String.format(Locale.getDefault(), "%.1f", data.getAmount());
                tvBill2.setText("1d：" + amount);
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                tvBill2.setText("1d：fail");
            }
        });
        Call<Result> callD7 = new RetrofitHelper().call(API.class).moreBillAmountGet(startD7, endAt, "", 0, 0, 0);
        RetrofitHelper.enqueue(callD7, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                String amount = String.format(Locale.getDefault(), "%.1f", data.getAmount());
                tvBill3.setText("7d：" + amount);
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                tvBill3.setText("7d：fail");
            }
        });
    }

    private void getVipData() {
        long currentLong = DateUtils.getCurrentLong();
        long startH1 = (currentLong - ConstantUtils.HOUR) / 1000;
        long startD1 = (currentLong - ConstantUtils.DAY) / 1000;
        long startD7 = (currentLong - ConstantUtils.DAY * 7) / 1000;
        long endAt = currentLong / 1000;
        Call<Result> callH1 = new RetrofitHelper().call(API.class).moreVipTotalGet(startH1, endAt);
        RetrofitHelper.enqueue(callH1, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                tvVip1.setText("1h：" + data.getTotal());
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                tvVip1.setText("1h：fail");
            }
        });
        Call<Result> callD1 = new RetrofitHelper().call(API.class).moreVipTotalGet(startD1, endAt);
        RetrofitHelper.enqueue(callD1, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                tvVip2.setText("1d：" + data.getTotal());
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                tvVip2.setText("1d：fail");
            }
        });
        Call<Result> callD7 = new RetrofitHelper().call(API.class).moreVipTotalGet(startD7, endAt);
        RetrofitHelper.enqueue(callD7, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                tvVip3.setText("7d：" + data.getTotal());
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                tvVip3.setText("7d：fail");
            }
        });
    }

    private void getSignData() {
        long currentLong = DateUtils.getCurrentLong();
        Calendar c0 = DateUtils.getCalendar(currentLong);
        Calendar c1 = DateUtils.getCalendar(currentLong - ConstantUtils.DAY);
        Calendar c2 = DateUtils.getCalendar(currentLong - ConstantUtils.DAY * 2);
        int year0 = c0.get(Calendar.YEAR);
        int year1 = c1.get(Calendar.YEAR);
        int year2 = c2.get(Calendar.YEAR);
        int month0 = c0.get(Calendar.MONTH) + 1;
        int month1 = c1.get(Calendar.MONTH) + 1;
        int month2 = c2.get(Calendar.MONTH) + 1;
        int day0 = c0.get(Calendar.DAY_OF_MONTH);
        int day1 = c1.get(Calendar.DAY_OF_MONTH);
        int day2 = c2.get(Calendar.DAY_OF_MONTH);
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
    }

    private void getCoinData() {
        long currentLong = DateUtils.getCurrentLong();
        long startH1 = (currentLong - ConstantUtils.HOUR) / 1000;
        long startD1 = (currentLong - ConstantUtils.DAY) / 1000;
        long startD7 = (currentLong - ConstantUtils.DAY * 7) / 1000;
        long endAt = currentLong / 1000;
        Call<Result> callH1 = new RetrofitHelper().call(API.class).moreCoinTotalGet(startH1, endAt, Coin.COIN_KIND_ADD_BY_PLAY_PAY);
        RetrofitHelper.enqueue(callH1, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                tvCoin1.setText("1h：" + data.getTotal());
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                tvCoin1.setText("1h：fail");
            }
        });
        Call<Result> callD1 = new RetrofitHelper().call(API.class).moreCoinTotalGet(startD1, endAt, Coin.COIN_KIND_ADD_BY_PLAY_PAY);
        RetrofitHelper.enqueue(callD1, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                tvCoin2.setText("1d：" + data.getTotal());
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                tvCoin2.setText("1d：fail");
            }
        });
        Call<Result> callD7 = new RetrofitHelper().call(API.class).moreCoinTotalGet(startD7, endAt, Coin.COIN_KIND_ADD_BY_PLAY_PAY);
        RetrofitHelper.enqueue(callD7, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                tvCoin3.setText("7d：" + data.getTotal());
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                tvCoin3.setText("7d：fail");
            }
        });
    }

    private void getNoteData() {
        long currentLong = DateUtils.getCurrentLong();
        long start = (currentLong - ConstantUtils.DAY) / 1000;
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

    private void getPostData() {
        long startH1 = (DateUtils.getCurrentLong() - ConstantUtils.HOUR) / 1000;
        long startD1 = (DateUtils.getCurrentLong() - ConstantUtils.DAY) / 1000;
        long startD7 = (DateUtils.getCurrentLong() - ConstantUtils.DAY * 7) / 1000;
        Call<Result> callSuggestH1 = new RetrofitHelper().call(API.class).setPostTotalGet(startH1);
        RetrofitHelper.enqueue(callSuggestH1, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                tvPost1.setText("1h：" + data.getTotal());
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                tvPost1.setText("1h：fail");
            }
        });
        Call<Result> callSuggestD1 = new RetrofitHelper().call(API.class).setPostTotalGet(startD1);
        RetrofitHelper.enqueue(callSuggestD1, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                tvPost2.setText("1d：" + data.getTotal());
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                tvPost2.setText("1d：fail");
            }
        });
        Call<Result> callSuggestD7 = new RetrofitHelper().call(API.class).setPostTotalGet(startD7);
        RetrofitHelper.enqueue(callSuggestD7, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                tvPost3.setText("7d：" + data.getTotal());
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                tvPost3.setText("7d：fail");
            }
        });
    }

    private void getPostCommentData() {
        long startH1 = (DateUtils.getCurrentLong() - ConstantUtils.HOUR) / 1000;
        long startD1 = (DateUtils.getCurrentLong() - ConstantUtils.DAY) / 1000;
        long startD7 = (DateUtils.getCurrentLong() - ConstantUtils.DAY * 7) / 1000;
        Call<Result> callCommentH1 = new RetrofitHelper().call(API.class).setPostCommentTotalGet(startH1);
        RetrofitHelper.enqueue(callCommentH1, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                tvPostComment1.setText("1h：" + data.getTotal());
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                tvPostComment1.setText("1h：fail");
            }
        });
        Call<Result> callCommentD1 = new RetrofitHelper().call(API.class).setPostCommentTotalGet(startD1);
        RetrofitHelper.enqueue(callCommentD1, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                tvPostComment2.setText("1d：" + data.getTotal());
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                tvPostComment2.setText("1d：fail");
            }
        });
        Call<Result> callCommentD7 = new RetrofitHelper().call(API.class).setPostCommentTotalGet(startD7);
        RetrofitHelper.enqueue(callCommentD7, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                tvPostComment3.setText("7d：" + data.getTotal());
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                tvPostComment3.setText("7d：fail");
            }
        });
    }

    private void getMatchWorkData() {
        long startD1 = (DateUtils.getCurrentLong() - ConstantUtils.DAY) / 1000;
        Call<Result> callWife = new RetrofitHelper().call(API.class).moreMatchWorkTotalGet(startD1, MatchPeriod.MATCH_KIND_WIFE_PICTURE);
        RetrofitHelper.enqueue(callWife, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                tvMatchPeriod1.setText("夫妻相(1d)：" + data.getTotal());
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                tvMatchPeriod1.setText("夫妻相(1d)：fail");
            }
        });
        Call<Result> callLetter = new RetrofitHelper().call(API.class).moreMatchWorkTotalGet(startD1, MatchPeriod.MATCH_KIND_LETTER_SHOW);
        RetrofitHelper.enqueue(callLetter, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                tvMatchPeriod2.setText("情书展(1d)：" + data.getTotal());
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                tvMatchPeriod2.setText("情书展(1d)：fail");
            }
        });
        Call<Result> callDiscuss = new RetrofitHelper().call(API.class).moreMatchWorkTotalGet(startD1, MatchPeriod.MATCH_KIND_DISCUSS_MEET);
        RetrofitHelper.enqueue(callDiscuss, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                tvMatchPeriod3.setText("讨论会(1d)：" + data.getTotal());
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                tvMatchPeriod3.setText("讨论会(1d)：fail");
            }
        });
    }

}
