package com.jiangzg.lovenote.controller.adapter.more;

import android.content.res.ColorStateList;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jiangzg.base.common.ConvertUtils;
import com.jiangzg.base.view.ScreenUtils;
import com.jiangzg.base.view.ViewUtils;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.controller.activity.common.BigImageActivity;
import com.jiangzg.lovenote.helper.common.CountHelper;
import com.jiangzg.lovenote.model.entity.MatchWork;
import com.jiangzg.lovenote.view.FrescoView;

/**
 * Created by JZG on 2018/3/13.
 * wife适配器
 */
public class MatchWifeAdapter extends BaseQuickAdapter<MatchWork, BaseViewHolder> {

    private final int width, height;
    private final ColorStateList colorPrimaryStateList, colorWhiteStateList;
    private FragmentActivity mActivity;

    public MatchWifeAdapter(FragmentActivity activity) {
        super(R.layout.list_item_match_wife);
        mActivity = activity;
        width = ScreenUtils.getScreenWidth(activity) / 2;
        height = ConvertUtils.dp2px(250);
        // color
        int rId = ViewUtils.getColorPrimary(activity);
        int colorPrimary = ContextCompat.getColor(activity, rId);
        int colorWhite = ContextCompat.getColor(activity, R.color.white);
        colorPrimaryStateList = ColorStateList.valueOf(colorPrimary);
        colorWhiteStateList = ColorStateList.valueOf(colorWhite);
    }

    @Override
    protected void convert(BaseViewHolder helper, MatchWork item) {
        helper.setVisible(R.id.tvCover, item.isScreen() || item.isDelete());
        if (item.isScreen()) {
            helper.setVisible(R.id.rlInfo, false);
            helper.setVisible(R.id.tvCover, true);
            helper.setText(R.id.tvCover, R.string.work_already_be_screen);
            return;
        } else if (item.isDelete()) {
            helper.setVisible(R.id.rlInfo, false);
            helper.setVisible(R.id.tvCover, true);
            helper.setText(R.id.tvCover, R.string.work_already_be_delete);
            return;
        }
        helper.setVisible(R.id.rlInfo, true);
        helper.setVisible(R.id.tvCover, false);
        // data
        String contentImage = item.getContentImage();
        String reportCount = CountHelper.getShowCount2Thousand(item.getReportCount());
        String pointCount = CountHelper.getShowCount2Thousand(item.getPointCount());
        String coinCount = CountHelper.getShowCount2Thousand(item.getCoinCount());
        boolean report = item.isReport();
        boolean point = item.isPoint();
        boolean coin = item.isCoin();
        // image
        FrescoView ivWork = helper.getView(R.id.ivWork);
        ivWork.setWidthAndHeight(width, height);
        ivWork.setData(contentImage);
        // count
        helper.setText(R.id.tvReportCount, reportCount);
        helper.setText(R.id.tvPointCount, pointCount);
        helper.setText(R.id.tvCoinCount, coinCount);
        // user
        ImageView ivReport = helper.getView(R.id.ivReport);
        ivReport.setImageTintList(report ? colorPrimaryStateList : colorWhiteStateList);
        ImageView ivPoint = helper.getView(R.id.ivPoint);
        ivPoint.setImageTintList(point ? colorPrimaryStateList : colorWhiteStateList);
        ImageView ivCoin = helper.getView(R.id.ivCoin);
        ivCoin.setImageTintList(coin ? colorPrimaryStateList : colorWhiteStateList);
        // listener
        if (!item.isScreen() && !item.isDelete()) {
            helper.addOnClickListener(R.id.llReport);
            helper.addOnClickListener(R.id.llCoin);
            helper.addOnClickListener(R.id.llPoint);
        }
    }

    public void goWifeDetail(int position) {
        MatchWork item = getItem(position);
        if (item == null || item.isScreen() || item.isDelete()) return;
        String contentImage = item.getContentImage();
        BigImageActivity.goActivityByOss(mActivity, contentImage, null);
    }

}
