package com.jiangzg.lovenote.adapter;

import android.content.res.ColorStateList;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.widget.ImageView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jiangzg.base.common.ConvertUtils;
import com.jiangzg.base.view.ScreenUtils;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.activity.common.BigImageActivity;
import com.jiangzg.lovenote.domain.MatchPoint;
import com.jiangzg.lovenote.domain.MatchReport;
import com.jiangzg.lovenote.domain.MatchWork;
import com.jiangzg.lovenote.domain.Result;
import com.jiangzg.lovenote.helper.API;
import com.jiangzg.lovenote.helper.CountHelper;
import com.jiangzg.lovenote.helper.DialogHelper;
import com.jiangzg.lovenote.helper.RetrofitHelper;
import com.jiangzg.lovenote.helper.ViewHelper;
import com.jiangzg.lovenote.view.FrescoView;

import retrofit2.Call;

/**
 * Created by JZG on 2018/3/13.
 * wife适配器
 */
public class MatchWifeAdapter extends BaseQuickAdapter<MatchWork, BaseViewHolder> {

    private FragmentActivity mActivity;
    private final int width, height;
    private final ColorStateList colorPrimaryStateList, colorWhiteStateList;

    public MatchWifeAdapter(FragmentActivity activity) {
        super(R.layout.list_item_match_wife);
        mActivity = activity;
        width = ScreenUtils.getScreenWidth(activity) / 2;
        height = ConvertUtils.dp2px(250);
        // color
        int rId = ViewHelper.getColorPrimary(activity);
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

    public void showDeleteDialog(final int position) {
        MatchWork item = getItem(position);
        if (!item.isMine()) return;
        MaterialDialog dialog = DialogHelper.getBuild(mActivity)
                .cancelable(true)
                .canceledOnTouchOutside(true)
                .content(R.string.confirm_del_this_work)
                .positiveText(R.string.confirm_no_wrong)
                .negativeText(R.string.i_think_again)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        delWife(position);
                    }
                })
                .build();
        DialogHelper.showWithAnim(dialog);
    }

    private void delWife(final int position) {
        MatchWork item = getItem(position);
        Call<Result> call = new RetrofitHelper().call(API.class).moreMatchWorkDel(item.getId());
        RetrofitHelper.enqueue(call, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                remove(position);
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
            }
        });
    }

    public void reportAdd(final int position, boolean api) {
        final MatchWork item = getItem(position);
        if (item.isReport()) return;
        item.setReport(true);
        notifyItemChanged(position + getHeaderLayoutCount());
        if (!api) return;
        MatchReport body = new MatchReport();
        body.setMatchWorkId(item.getId());
        Call<Result> call = new RetrofitHelper().call(API.class).moreMatchReportAdd(body);
        RetrofitHelper.enqueue(call, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                reportAdd(position, false);
            }
        });
    }

    public void pointToggle(final int position, boolean api) {
        final MatchWork item = getItem(position);
        boolean newPoint = !item.isPoint();
        int newPointCount = newPoint ? item.getPointCount() + 1 : item.getPointCount() - 1;
        if (newPointCount < 0) {
            newPointCount = 0;
        }
        item.setPoint(newPoint);
        item.setPointCount(newPointCount);
        notifyItemChanged(position + getHeaderLayoutCount());
        if (!api) return;
        MatchPoint body = new MatchPoint();
        body.setMatchWorkId(item.getId());
        Call<Result> call = new RetrofitHelper().call(API.class).moreMatchPointAdd(body);
        RetrofitHelper.enqueue(call, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                pointToggle(position, false);
            }
        });
    }

    public void coinAdd(int position, boolean api) {
        // TODO

    }

}
