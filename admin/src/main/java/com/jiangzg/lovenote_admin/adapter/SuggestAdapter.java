package com.jiangzg.lovenote_admin.adapter;

import android.support.annotation.NonNull;
import android.view.View;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jiangzg.base.common.ConstantUtils;
import com.jiangzg.base.common.ConvertUtils;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.time.DateUtils;
import com.jiangzg.base.view.ScreenUtils;
import com.jiangzg.lovenote_admin.R;
import com.jiangzg.lovenote_admin.activity.SuggestDetailActivity;
import com.jiangzg.lovenote_admin.base.BaseActivity;
import com.jiangzg.lovenote_admin.domain.Result;
import com.jiangzg.lovenote_admin.domain.Suggest;
import com.jiangzg.lovenote_admin.domain.SuggestInfo;
import com.jiangzg.lovenote_admin.helper.API;
import com.jiangzg.lovenote_admin.helper.DialogHelper;
import com.jiangzg.lovenote_admin.helper.RetrofitHelper;
import com.jiangzg.lovenote_admin.helper.ViewHelper;
import com.jiangzg.lovenote_admin.view.FrescoView;
import com.jiangzg.lovenote_admin.view.GWrapView;

import java.util.Locale;

import retrofit2.Call;

/**
 * Created by JZG on 2018/3/13.
 * 意见反馈适配器
 */
public class SuggestAdapter extends BaseQuickAdapter<Suggest, BaseViewHolder> {

    private BaseActivity mActivity;
    private final String formatCreateAt;
    private final String formatUpdateAt;
    private final String formatFollow;
    private final String formatComment;
    private final String formatTop;
    private final String formatOfficial;
    private final String formatMine;
    private final int width, height;

    public SuggestAdapter(BaseActivity activity) {
        super(R.layout.list_item_suggest);
        mActivity = activity;
        formatCreateAt = mActivity.getString(R.string.create_at_colon_space_holder);
        formatUpdateAt = mActivity.getString(R.string.update_at_colon_space_holder);
        formatFollow = mActivity.getString(R.string.follow);
        formatComment = mActivity.getString(R.string.comment);
        formatTop = mActivity.getString(R.string.top);
        formatOfficial = mActivity.getString(R.string.official);
        formatMine = mActivity.getString(R.string.me_de);
        width = ScreenUtils.getScreenWidth(activity);
        height = ConvertUtils.dp2px(200);
    }

    @Override
    protected void convert(BaseViewHolder helper, Suggest item) {
        // data
        boolean top = item.isTop();
        boolean official = item.isOfficial();
        boolean mine = item.isMine();
        String statusShow = SuggestInfo.getStatusShow(item.getStatus());
        String kindShow = SuggestInfo.getKindShow(item.getKind());
        String title = item.getTitle();
        String contentText = item.getContentText();
        String create = DateUtils.getString(item.getCreateAt() * 1000, ConstantUtils.FORMAT_LINE_Y_M_D_H_M);
        String createShow = String.format(Locale.getDefault(), formatCreateAt, create);
        String update = DateUtils.getString(item.getUpdateAt() * 1000, ConstantUtils.FORMAT_LINE_Y_M_D_H_M);
        String updatedShow = String.format(Locale.getDefault(), formatUpdateAt, update);
        final long followCount = item.getFollowCount();
        String followShow;
        if (followCount <= 0) {
            followShow = formatFollow;
        } else {
            followShow = String.valueOf(followCount);
        }
        long commentCount = item.getCommentCount();
        String commentShow;
        if (commentCount <= 0) {
            commentShow = formatComment;
        } else {
            commentShow = String.valueOf(commentCount);
        }
        String contentImage = item.getContentImage();
        // view
        FrescoView ivImage = helper.getView(R.id.ivImage);
        if (StringUtils.isEmpty(contentImage)) {
            ivImage.setVisibility(View.GONE);
        } else {
            ivImage.setVisibility(View.VISIBLE);
            ivImage.setWidthAndHeight(width, height);
            ivImage.setData(contentImage);
        }
        helper.setText(R.id.tvId, "id:" + item.getId());
        helper.setText(R.id.tvUid, "uid:" + item.getUserId());
        GWrapView wvTag = helper.getView(R.id.wvTag);
        wvTag.removeAllChild();
        if (top) {
            View tagTop = ViewHelper.getWrapTextView(mActivity, formatTop);
            wvTag.addChild(tagTop);
        }
        if (official) {
            View tagOfficial = ViewHelper.getWrapTextView(mActivity, formatOfficial);
            wvTag.addChild(tagOfficial);
        }
        View tagStatus = ViewHelper.getWrapTextView(mActivity, statusShow);
        wvTag.addChild(tagStatus);
        View tagKind = ViewHelper.getWrapTextView(mActivity, kindShow);
        wvTag.addChild(tagKind);
        if (mine) {
            View tagMine = ViewHelper.getWrapTextView(mActivity, formatMine);
            wvTag.addChild(tagMine);
        }
        helper.setText(R.id.tvTitle, title);
        helper.setText(R.id.tvContent, contentText);
        helper.setText(R.id.tvCreateAt, createShow);
        helper.setText(R.id.tvUpdateAt, updatedShow);
        helper.setText(R.id.tvFollow, followShow);
        helper.setText(R.id.tvComment, commentShow);
    }

    public void goSuggestDetail(int position) {
        Suggest item = getItem(position);
        SuggestDetailActivity.goActivity(mActivity, item);
    }

    public void showDelDialog(final int position) {
        MaterialDialog dialog = DialogHelper.getBuild(mActivity)
                .content(R.string.confirm_delete_this)
                .cancelable(true)
                .canceledOnTouchOutside(true)
                .positiveText(R.string.confirm_no_wrong)
                .negativeText(R.string.i_think_again)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        delSuggest(position);
                    }
                })
                .build();
        DialogHelper.showWithAnim(dialog);
    }

    // 删除意见
    private void delSuggest(final int position) {
        Suggest item = getItem(position);
        MaterialDialog loading = mActivity.getLoading(true);
        Call<Result> call = new RetrofitHelper().call(API.class).setSuggestDel(item.getId());
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
