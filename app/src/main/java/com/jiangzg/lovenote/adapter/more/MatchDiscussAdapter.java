package com.jiangzg.lovenote.adapter.more;

import android.content.res.ColorStateList;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jiangzg.base.view.ViewUtils;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.helper.CountHelper;
import com.jiangzg.lovenote.model.entity.MatchWork;

/**
 * Created by JZG on 2018/3/13.
 * discuss适配器
 */
public class MatchDiscussAdapter extends BaseQuickAdapter<MatchWork, BaseViewHolder> {

    //private FragmentActivity mActivity;
    private final ColorStateList colorPrimaryStateList, colorGreyStateList;

    public MatchDiscussAdapter(FragmentActivity activity) {
        super(R.layout.list_item_match_discuss);
        //mActivity = activity;
        // color
        int rId = ViewUtils.getColorPrimary(activity);
        int colorPrimary = ContextCompat.getColor(activity, rId);
        int colorGrey = ContextCompat.getColor(activity, R.color.icon_grey);
        colorPrimaryStateList = ColorStateList.valueOf(colorPrimary);
        colorGreyStateList = ColorStateList.valueOf(colorGrey);
    }

    @Override
    protected void convert(BaseViewHolder helper, MatchWork item) {
        helper.setVisible(R.id.tvCover, item.isScreen() || item.isDelete());
        if (item.isScreen()) {
            helper.setVisible(R.id.llInfo, false);
            helper.setVisible(R.id.tvCover, true);
            helper.setText(R.id.tvCover, R.string.work_already_be_screen);
            return;
        } else if (item.isDelete()) {
            helper.setVisible(R.id.llInfo, false);
            helper.setVisible(R.id.tvCover, true);
            helper.setText(R.id.tvCover, R.string.work_already_be_delete);
            return;
        }
        helper.setVisible(R.id.llInfo, true);
        helper.setVisible(R.id.tvCover, false);
        // data
        String content = item.getContentText();
        String reportCount = CountHelper.getShowCount2Thousand(item.getReportCount());
        String pointCount = CountHelper.getShowCount2Thousand(item.getPointCount());
        String coinCount = CountHelper.getShowCount2Thousand(item.getCoinCount());
        boolean report = item.isReport();
        boolean point = item.isPoint();
        boolean coin = item.isCoin();
        // content
        helper.setText(R.id.tvContent, content);
        // count
        helper.setText(R.id.tvReportCount, reportCount);
        helper.setText(R.id.tvPointCount, pointCount);
        helper.setText(R.id.tvCoinCount, coinCount);
        // user
        ImageView ivReport = helper.getView(R.id.ivReport);
        ivReport.setImageTintList(report ? colorPrimaryStateList : colorGreyStateList);
        ImageView ivPoint = helper.getView(R.id.ivPoint);
        ivPoint.setImageTintList(point ? colorPrimaryStateList : colorGreyStateList);
        ImageView ivCoin = helper.getView(R.id.ivCoin);
        ivCoin.setImageTintList(coin ? colorPrimaryStateList : colorGreyStateList);
        // listener
        if (!item.isScreen() && !item.isDelete()) {
            helper.addOnClickListener(R.id.llReport);
            helper.addOnClickListener(R.id.llCoin);
            helper.addOnClickListener(R.id.llPoint);
        }
    }

}
