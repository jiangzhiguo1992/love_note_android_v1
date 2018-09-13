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
import com.jiangzg.lovenote_admin.activity.VipListActivity;
import com.jiangzg.lovenote_admin.base.BaseFragment;
import com.jiangzg.lovenote_admin.domain.Result;
import com.jiangzg.lovenote_admin.helper.API;
import com.jiangzg.lovenote_admin.helper.RetrofitHelper;

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

    @BindView(R.id.cvMatch)
    CardView cvMatch;
    @BindView(R.id.tvMatch1)
    TextView tvMatch1;
    @BindView(R.id.tvMatch2)
    TextView tvMatch2;
    @BindView(R.id.tvMatch3)
    TextView tvMatch3;

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
        // TODO bill
        // TODO note
        // TODO post
        // TODO match
    }

    @Override
    protected void onFinish(Bundle state) {
    }

    @OnClick({R.id.cvCouple, R.id.cvBill, R.id.cvVip, R.id.cvCoin, R.id.cvNote, R.id.cvPost, R.id.cvMatch})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.cvCouple: // 配对
                CoupleListActivity.goActivity(mFragment);
                break;
            case R.id.cvBill: // 账单
                BillListActivity.goActivity(mFragment);
                break;
            case R.id.cvVip: // 会员
                VipListActivity.goActivity(mFragment);
                break;
            case R.id.cvCoin: // 金币
                CoinListActivity.goActivity(mFragment);
                break;
            case R.id.cvNote: // 记录
                // TODO
                break;
            case R.id.cvPost: // 话题
                // TODO
                break;
            case R.id.cvMatch: // 比拼
                // TODO
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

}
