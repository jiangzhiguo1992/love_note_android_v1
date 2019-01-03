package com.jiangzg.lovenote_admin.adapter;

import android.support.annotation.NonNull;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jiangzg.base.common.ConvertUtils;
import com.jiangzg.base.common.DateUtils;
import com.jiangzg.base.view.ScreenUtils;
import com.jiangzg.lovenote_admin.R;
import com.jiangzg.lovenote_admin.activity.UserDetailActivity;
import com.jiangzg.lovenote_admin.base.BaseActivity;
import com.jiangzg.lovenote_admin.domain.MatchPeriod;
import com.jiangzg.lovenote_admin.domain.MatchWork;
import com.jiangzg.lovenote_admin.domain.Result;
import com.jiangzg.lovenote_admin.helper.API;
import com.jiangzg.lovenote_admin.helper.DialogHelper;
import com.jiangzg.lovenote_admin.helper.RetrofitHelper;
import com.jiangzg.lovenote_admin.view.FrescoView;

import retrofit2.Call;

/**
 * Created by JZG on 2018/3/15.
 * 意见反馈评论适配器
 */
public class MatchWorkAdapter extends BaseQuickAdapter<MatchWork, BaseViewHolder> {

    private BaseActivity mActivity;
    private final int width, height;

    public MatchWorkAdapter(BaseActivity activity) {
        super(R.layout.list_item_match_work);
        mActivity = activity;
        width = ScreenUtils.getScreenWidth(activity);
        height = ConvertUtils.dp2px(200);
    }

    @Override
    protected void convert(BaseViewHolder helper, MatchWork item) {
        // data
        String id = "id:" + item.getId();
        String uid = "uid:" + item.getUserId();
        String cid = "cid:" + item.getCoupleId();
        String mpid = "mpid:" + item.getMatchPeriodId();
        String kind = MatchPeriod.getKindShow(item.getKind());
        String create = "创建:" + DateUtils.getStr(item.getCreateAt() * 1000, DateUtils.FORMAT_LINE_Y_M_D_H_M);
        String update = "更新:" + DateUtils.getStr(item.getUpdateAt() * 1000, DateUtils.FORMAT_LINE_Y_M_D_H_M);
        String title = item.getTitle();
        String contentText = item.getContentText();
        String contentImage = item.getContentImage();
        String reportCount = String.valueOf(item.getReportCount());
        String coinCount = String.valueOf(item.getCoinCount());
        String pointCount = String.valueOf(item.getPointCount());
        // view
        helper.setText(R.id.tvId, id);
        helper.setText(R.id.tvUid, uid);
        helper.setText(R.id.tvCid, cid);
        helper.setText(R.id.tvMpid, mpid);
        helper.setText(R.id.tvKind, kind);
        helper.setText(R.id.tvKind, kind);
        helper.setText(R.id.tvCreate, create);
        helper.setText(R.id.tvUpdate, update);
        helper.setText(R.id.tvTitle, title);
        helper.setText(R.id.tvContent, contentText);
        helper.setText(R.id.tvReport, reportCount);
        helper.setText(R.id.tvCoin, coinCount);
        helper.setText(R.id.tvPoint, pointCount);
        // iv
        if (item.getKind() != MatchPeriod.MATCH_KIND_WIFE_PICTURE) {
            helper.setVisible(R.id.tvTitle, true);
            helper.setVisible(R.id.tvContent, true);
            helper.setVisible(R.id.ivWorks, false);
        } else {
            helper.setVisible(R.id.tvTitle, false);
            helper.setVisible(R.id.tvContent, false);
            helper.setVisible(R.id.ivWorks, true);
            FrescoView ivWorks = helper.getView(R.id.ivWorks);
            ivWorks.setWidthAndHeight(width, height);
            ivWorks.setData(contentImage);
        }
    }

    // 删除评论
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
        final MatchWork item = getItem(position);
        Call<Result> call = new RetrofitHelper().call(API.class).moreMatchWorkDel(item.getId());
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

    public void goUserDetail(int position) {
        MatchWork item = getItem(position);
        UserDetailActivity.goActivity(mActivity, item.getUserId());
    }

}
