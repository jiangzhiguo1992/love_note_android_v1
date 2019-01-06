package com.jiangzg.lovenote.controller.adapter.more;

import android.support.v4.app.FragmentActivity;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jiangzg.base.common.DateUtils;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.controller.activity.more.MatchDiscussListActivity;
import com.jiangzg.lovenote.controller.activity.more.MatchLetterListActivity;
import com.jiangzg.lovenote.controller.activity.more.MatchWifeListActivity;
import com.jiangzg.lovenote.helper.common.CountHelper;
import com.jiangzg.lovenote.helper.common.TimeHelper;
import com.jiangzg.lovenote.model.entity.MatchPeriod;

import java.util.Locale;

/**
 * Created by JZG on 2018/3/13.
 * 期数适配器
 */
public class MatchPeriodAdapter extends BaseQuickAdapter<MatchPeriod, BaseViewHolder> {

    private FragmentActivity mActivity;

    public MatchPeriodAdapter(FragmentActivity activity) {
        super(R.layout.list_item_match_period);
        mActivity = activity;
    }

    @Override
    protected void convert(BaseViewHolder helper, MatchPeriod item) {
        // data
        String title = item.getTitle();
        String start = DateUtils.getStr(TimeHelper.getJavaTimeByGo(item.getStartAt()), DateUtils.FORMAT_LINE_M_D_H_M);
        String end = DateUtils.getStr(TimeHelper.getJavaTimeByGo(item.getEndAt()), DateUtils.FORMAT_LINE_M_D_H_M);
        String time = String.format(Locale.getDefault(), mActivity.getString(R.string.holder_space_line_space_holder), start, end);
        String periodShow = String.format(Locale.getDefault(), mActivity.getString(R.string.the_holder_period), item.getPeriod());
        String coinChange = String.format(Locale.getDefault(), mActivity.getString(R.string.go_in_award_colon_holder_coin), item.getCoinChange());
        String workCount = String.format(Locale.getDefault(), mActivity.getString(R.string.total_works_count_colon_holder), CountHelper.getShowCount2Thousand(item.getWorksCount()));
        String coinCount = String.format(Locale.getDefault(), mActivity.getString(R.string.total_coin_count_colon_holder), CountHelper.getShowCount2Thousand(item.getCoinCount()));
        String pointCount = String.format(Locale.getDefault(), mActivity.getString(R.string.total_point_count_colon_holder), CountHelper.getShowCount2Thousand(item.getPointCount()));
        // view
        helper.setText(R.id.tvTitle, title);
        helper.setText(R.id.tvTime, time);
        helper.setText(R.id.tvPeriod, periodShow);
        helper.setText(R.id.tvCoin, coinChange);
        helper.setText(R.id.tvWorksCount, workCount);
        helper.setText(R.id.tvCoinCount, coinCount);
        helper.setText(R.id.tvPointCount, pointCount);
    }

    public void getMatchWorkList(int position) {
        MatchPeriod item = getItem(position);
        switch (item.getKind()) {
            case MatchPeriod.MATCH_KIND_WIFE_PICTURE: // 夫妻相
                MatchWifeListActivity.goActivity(mActivity, item);
                break;
            case MatchPeriod.MATCH_KIND_LETTER_SHOW: // 情书展
                MatchLetterListActivity.goActivity(mActivity, item);
                break;
            case MatchPeriod.MATCH_KIND_DISCUSS_MEET: // 讨论会
                MatchDiscussListActivity.goActivity(mActivity, item);
                break;
        }
    }

}
