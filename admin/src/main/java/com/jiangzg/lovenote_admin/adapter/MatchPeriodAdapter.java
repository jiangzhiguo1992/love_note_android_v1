package com.jiangzg.lovenote_admin.adapter;

import android.support.annotation.NonNull;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jiangzg.base.common.ConstantUtils;
import com.jiangzg.base.time.DateUtils;
import com.jiangzg.lovenote_admin.R;
import com.jiangzg.lovenote_admin.activity.MatchWorkListActivity;
import com.jiangzg.lovenote_admin.base.BaseActivity;
import com.jiangzg.lovenote_admin.domain.MatchPeriod;
import com.jiangzg.lovenote_admin.domain.Result;
import com.jiangzg.lovenote_admin.helper.API;
import com.jiangzg.lovenote_admin.helper.DialogHelper;
import com.jiangzg.lovenote_admin.helper.RetrofitHelper;

import retrofit2.Call;

/**
 * Created by JZG on 2018/3/15.
 * MatchPeriod适配器
 */
public class MatchPeriodAdapter extends BaseQuickAdapter<MatchPeriod, BaseViewHolder> {

    private BaseActivity mActivity;

    public MatchPeriodAdapter(BaseActivity activity) {
        super(R.layout.list_item_match_period);
        mActivity = activity;
    }

    @Override
    protected void convert(BaseViewHolder helper, MatchPeriod item) {
        // data
        String id = "id:" + item.getId();
        //String create = "创建:" + DateUtils.getString(item.getCreateAt() * 1000, ConstantUtils.FORMAT_LINE_Y_M_D_H_M);
        //String update = "更新:" + DateUtils.getString(item.getUpdateAt() * 1000, ConstantUtils.FORMAT_LINE_Y_M_D_H_M);
        String isEnd = "(" + (DateUtils.getCurrentLong() >= item.getStartAt() * 1000 && DateUtils.getCurrentLong() <= item.getEndAt() * 1000) + ")";
        String start = "开始:" + DateUtils.getString(item.getStartAt() * 1000, ConstantUtils.FORMAT_LINE_Y_M_D_H_M);
        String end = "结束:" + DateUtils.getString(item.getEndAt() * 1000, ConstantUtils.FORMAT_LINE_Y_M_D_H_M);
        String period = "期数:" + item.getPeriod();
        String kind = MatchPeriod.getKindShow(item.getKind());
        String coinChange = "奖励金币:" + item.getCoinChange();
        String title = item.getTitle();
        String worksCount = String.valueOf(item.getWorksCount());
        String reportCount = String.valueOf(item.getReportCount());
        String pointCount = String.valueOf(item.getPointCount());
        String coinCount = String.valueOf(item.getCoinCount());
        // view
        helper.setText(R.id.tvId, id);
        //helper.setText(R.id.tvCreate, create);
        //helper.setText(R.id.tvUpdate, update);
        helper.setText(R.id.tvIsEnd, isEnd);
        helper.setText(R.id.tvStart, start);
        helper.setText(R.id.tvEnd, end);
        helper.setText(R.id.tvPeriod, period);
        helper.setText(R.id.tvKind, kind);
        helper.setText(R.id.tvCoinChange, coinChange);
        helper.setText(R.id.tvTitle, title);
        helper.setText(R.id.tvWorksCount, reportCount);
        helper.setText(R.id.tvReportCount, worksCount);
        helper.setText(R.id.tvPointCount, pointCount);
        helper.setText(R.id.tvCoinCount, coinCount);
    }

    public void goMatchWorkList(int position) {
        MatchPeriod item = getItem(position);
        MatchWorkListActivity.goActivity(mActivity, 0, item.getId());
    }

    public void showDeleteDialog(final int position) {
        MaterialDialog dialog = DialogHelper.getBuild(mActivity)
                .cancelable(true)
                .canceledOnTouchOutside(true)
                .content(R.string.confirm_delete_this)
                .positiveText(R.string.confirm_no_wrong)
                .negativeText(R.string.i_think_again)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        delCommentApi(position);
                    }
                })
                .build();
        DialogHelper.showWithAnim(dialog);
    }

    private void delCommentApi(final int position) {
        final MatchPeriod item = getItem(position);
        Call<Result> call = new RetrofitHelper().call(API.class).moreMatchPeriodDel(item.getId());
        MaterialDialog loading = mActivity.getLoading(true);
        RetrofitHelper.enqueue(call, loading, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                remove(position);
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
            }
        });
    }

}
