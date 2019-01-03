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
import com.jiangzg.lovenote_admin.activity.MatchPeriodListActivity;
import com.jiangzg.lovenote_admin.activity.PostCommentListActivity;
import com.jiangzg.lovenote_admin.activity.PostListActivity;
import com.jiangzg.lovenote_admin.activity.SuggestCommentListActivity;
import com.jiangzg.lovenote_admin.activity.SuggestListActivity;
import com.jiangzg.lovenote_admin.activity.VipListActivity;
import com.jiangzg.lovenote_admin.base.BaseFragment;
import com.jiangzg.lovenote_admin.domain.Coin;
import com.jiangzg.lovenote_admin.domain.MatchPeriod;
import com.jiangzg.lovenote_admin.domain.Result;
import com.jiangzg.lovenote_admin.helper.API;
import com.jiangzg.lovenote_admin.helper.RetrofitHelper;

import java.util.Calendar;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;

public class CoupleFragment extends BaseFragment<CoupleFragment> {

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

    @BindView(R.id.cvVip)
    CardView cvVip;
    @BindView(R.id.tvVip1)
    TextView tvVip1;
    @BindView(R.id.tvVip2)
    TextView tvVip2;
    @BindView(R.id.tvVip3)
    TextView tvVip3;

    @BindView(R.id.cvCoin)
    CardView cvCoin;
    @BindView(R.id.tvCoin1)
    TextView tvCoin1;
    @BindView(R.id.tvCoin2)
    TextView tvCoin2;
    @BindView(R.id.tvCoin3)
    TextView tvCoin3;

    @BindView(R.id.cvBill)
    CardView cvBill;
    @BindView(R.id.tvBill1)
    TextView tvBill1;
    @BindView(R.id.tvBill2)
    TextView tvBill2;
    @BindView(R.id.tvBill3)
    TextView tvBill3;

    @BindView(R.id.cvMatchPeriod)
    CardView cvMatchPeriod;
    @BindView(R.id.tvMatchPeriod1)
    TextView tvMatchPeriod1;
    @BindView(R.id.tvMatchPeriod2)
    TextView tvMatchPeriod2;
    @BindView(R.id.tvMatchPeriod3)
    TextView tvMatchPeriod3;

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
        getSuggestData();
        getSuggestCommentData();
        getVipData();
        getCoinData();
        getBillData();
        getMatchWorkData();
        getPostData();
        getPostCommentData();
    }

    @Override
    protected void onFinish(Bundle state) {
    }

    @OnClick({R.id.cvSuggest, R.id.cvSuggestComment, R.id.cvVip, R.id.cvCoin,
            R.id.cvBill, R.id.cvMatchPeriod, R.id.cvPost, R.id.cvPostComment})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.cvSuggest: // 意见反馈(帖子)
                SuggestListActivity.goActivity(mFragment);
                break;
            case R.id.cvSuggestComment: // 意见反馈(评论)
                SuggestCommentListActivity.goActivity(mFragment);
                break;
            case R.id.cvVip: // 会员
                VipListActivity.goActivity(mFragment);
                break;
            case R.id.cvCoin: // 金币
                CoinListActivity.goActivity(mFragment);
                break;
            case R.id.cvBill: // 账单
                BillListActivity.goActivity(mFragment);
                break;
            case R.id.cvMatchPeriod: // 比拼
                MatchPeriodListActivity.goActivity(mFragment);
                break;
            case R.id.cvPost: // 话题帖子
                PostListActivity.goActivity(mFragment);
                break;
            case R.id.cvPostComment: // 话题评论
                PostCommentListActivity.goActivity(mFragment);
                break;
        }
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

    private void getVipData() {
        long current = DateUtils.getCurrentLong() / 1000;
        Calendar calendar = DateUtils.getCurrentCal();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        long d1Start = calendar.getTimeInMillis() / 1000;
        long d2Start = d1Start - 60 * 60 * 24;
        long d3Start = d2Start - 60 * 60 * 24;
        Call<Result> callH1 = new RetrofitHelper().call(API.class).moreVipTotalGet(d1Start, current);
        RetrofitHelper.enqueue(callH1, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                tvVip1.setText("今：" + data.getTotal());
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                tvVip1.setText("今：fail");
            }
        });
        Call<Result> callD1 = new RetrofitHelper().call(API.class).moreVipTotalGet(d2Start, d1Start);
        RetrofitHelper.enqueue(callD1, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                tvVip2.setText("昨：" + data.getTotal());
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                tvVip2.setText("昨：fail");
            }
        });
        Call<Result> callD7 = new RetrofitHelper().call(API.class).moreVipTotalGet(d3Start, d2Start);
        RetrofitHelper.enqueue(callD7, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                tvVip3.setText("前：" + data.getTotal());
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                tvVip3.setText("前：fail");
            }
        });
    }

    private void getCoinData() {
        long current = DateUtils.getCurrentLong() / 1000;
        Calendar calendar = DateUtils.getCurrentCal();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        long d1Start = calendar.getTimeInMillis() / 1000;
        long d2Start = d1Start - 60 * 60 * 24;
        long d3Start = d2Start - 60 * 60 * 24;
        Call<Result> callH1 = new RetrofitHelper().call(API.class).moreCoinTotalGet(d1Start, current, Coin.COIN_KIND_ADD_BY_PLAY_PAY);
        RetrofitHelper.enqueue(callH1, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                tvCoin1.setText("今：" + data.getTotal());
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                tvCoin1.setText("今：fail");
            }
        });
        Call<Result> callD1 = new RetrofitHelper().call(API.class).moreCoinTotalGet(d2Start, d1Start, Coin.COIN_KIND_ADD_BY_PLAY_PAY);
        RetrofitHelper.enqueue(callD1, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                tvCoin2.setText("昨：" + data.getTotal());
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                tvCoin2.setText("昨：fail");
            }
        });
        Call<Result> callD7 = new RetrofitHelper().call(API.class).moreCoinTotalGet(d3Start, d2Start, Coin.COIN_KIND_ADD_BY_PLAY_PAY);
        RetrofitHelper.enqueue(callD7, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                tvCoin3.setText("前：" + data.getTotal());
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                tvCoin3.setText("前：fail");
            }
        });
    }

    private void getBillData() {
        long current = DateUtils.getCurrentLong() / 1000;
        Calendar calendar = DateUtils.getCurrentCal();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        long d1Start = calendar.getTimeInMillis() / 1000;
        long d2Start = d1Start - 60 * 60 * 24;
        long d3Start = d2Start - 60 * 60 * 24;
        Call<Result> callH1 = new RetrofitHelper().call(API.class).moreBillAmountGet(d1Start, current, "", 0, 0, 0);
        RetrofitHelper.enqueue(callH1, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                String amount = String.format(Locale.getDefault(), "%.1f", data.getAmount());
                tvBill1.setText("今：" + amount);
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                tvBill1.setText("今：fail");
            }
        });
        Call<Result> callD1 = new RetrofitHelper().call(API.class).moreBillAmountGet(d2Start, d1Start, "", 0, 0, 0);
        RetrofitHelper.enqueue(callD1, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                String amount = String.format(Locale.getDefault(), "%.1f", data.getAmount());
                tvBill2.setText("昨：" + amount);
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                tvBill2.setText("昨：fail");
            }
        });
        Call<Result> callD7 = new RetrofitHelper().call(API.class).moreBillAmountGet(d3Start, d2Start, "", 0, 0, 0);
        RetrofitHelper.enqueue(callD7, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                String amount = String.format(Locale.getDefault(), "%.1f", data.getAmount());
                tvBill3.setText("前：" + amount);
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                tvBill3.setText("前：fail");
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

    private void getPostData() {
        long startH1 = (DateUtils.getCurrentLong() - ConstantUtils.HOUR) / 1000;
        long startD1 = (DateUtils.getCurrentLong() - ConstantUtils.DAY) / 1000;
        long startD7 = (DateUtils.getCurrentLong() - ConstantUtils.DAY * 3) / 1000;
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
                tvPost3.setText("3d：" + data.getTotal());
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                tvPost3.setText("3d：fail");
            }
        });
    }

    private void getPostCommentData() {
        long startH1 = (DateUtils.getCurrentLong() - ConstantUtils.HOUR) / 1000;
        long startD1 = (DateUtils.getCurrentLong() - ConstantUtils.DAY) / 1000;
        long startD7 = (DateUtils.getCurrentLong() - ConstantUtils.DAY * 3) / 1000;
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
                tvPostComment3.setText("3d：" + data.getTotal());
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                tvPostComment3.setText("3d：fail");
            }
        });
    }

}
