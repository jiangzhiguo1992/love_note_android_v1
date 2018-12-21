package com.jiangzg.lovenote.adapter;

import android.content.res.ColorStateList;
import android.support.v4.content.ContextCompat;
import android.widget.ImageView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jiangzg.base.view.ViewUtils;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.activity.topic.PostSubCommentListActivity;
import com.jiangzg.lovenote.base.BaseActivity;
import com.jiangzg.lovenote.helper.ConsHelper;
import com.jiangzg.lovenote.helper.CountHelper;
import com.jiangzg.lovenote.helper.DialogHelper;
import com.jiangzg.lovenote.helper.RetrofitHelper;
import com.jiangzg.lovenote.helper.RxBus;
import com.jiangzg.lovenote.helper.TimeHelper;
import com.jiangzg.lovenote.model.api.API;
import com.jiangzg.lovenote.model.api.Result;
import com.jiangzg.lovenote.model.entity.Couple;
import com.jiangzg.lovenote.model.entity.PostComment;
import com.jiangzg.lovenote.model.entity.PostCommentPoint;
import com.jiangzg.lovenote.model.entity.PostCommentReport;
import com.jiangzg.lovenote.view.FrescoAvatarView;

import java.util.Locale;

import retrofit2.Call;

/**
 * Created by JZG on 2018/7/26.
 * 帖子评论适配器
 */
public class PostCommentAdapter extends BaseMultiItemQuickAdapter<PostComment, BaseViewHolder> {

    private final String formatFloor;
    private final int colorPrimary, colorFontGrey;
    private final ColorStateList colorStatePrimary, colorStateIconGrey;
    private BaseActivity mActivity;
    private boolean subComment;

    public PostCommentAdapter(BaseActivity activity, boolean subComment) {
        super(null);
        addItemType(PostComment.KIND_TEXT, R.layout.list_item_post_comment_text);
        addItemType(PostComment.KIND_JAB, R.layout.list_item_post_comment_jab);
        mActivity = activity;
        this.subComment = subComment;
        formatFloor = mActivity.getString(R.string.holder_floor);
        // color
        colorPrimary = ContextCompat.getColor(mActivity, ViewUtils.getColorPrimary(mActivity));
        int colorGrey = ContextCompat.getColor(mActivity, R.color.icon_grey);
        colorFontGrey = ContextCompat.getColor(mActivity, R.color.font_grey);
        colorStatePrimary = ColorStateList.valueOf(colorPrimary);
        colorStateIconGrey = ColorStateList.valueOf(colorGrey);
    }

    @Override
    protected void convert(BaseViewHolder helper, PostComment item) {
        if (item.isScreen()) {
            helper.setVisible(R.id.root, false);
            return;
        }
        helper.setVisible(R.id.root, true);
        // data
        String floor = String.format(Locale.getDefault(), formatFloor, item.getFloor());
        String create = TimeHelper.getTimeShowLine_HM_MD_YMD_ByGo(item.getCreateAt());
        // 官方 我的 ta的
        Couple couple = item.getCouple();
        String contentText = item.getContentText();
        String commentCount = CountHelper.getShowCount2Thousand(item.getSubCommentCount());
        String pointCount = CountHelper.getShowCount2Thousand(item.getPointCount());
        boolean official = item.isOfficial();
        boolean our = item.isOur();
        boolean subComment = item.isSubComment();
        boolean point = item.isPoint();
        boolean report = item.isReport();
        // view
        helper.setVisible(R.id.tvOfficial, official);
        helper.setText(R.id.tvFloor, floor);
        helper.setTextColor(R.id.tvFloor, our ? colorPrimary : colorFontGrey);
        helper.setText(R.id.tvTime, create);
        if (item.getItemType() == PostComment.KIND_TEXT) {
            // text
            if (couple == null) {
                helper.setVisible(R.id.ivAvatarLeft, false);
                helper.setVisible(R.id.ivAvatarRight, false);
            } else {
                helper.setVisible(R.id.ivAvatarLeft, true);
                helper.setVisible(R.id.ivAvatarRight, true);
                FrescoAvatarView ivAvatarLeft = helper.getView(R.id.ivAvatarLeft);
                FrescoAvatarView ivAvatarRight = helper.getView(R.id.ivAvatarRight);
                ivAvatarLeft.setData(couple.getCreatorAvatar());
                ivAvatarRight.setData(couple.getInviteeAvatar());
            }
            helper.setText(R.id.tvContent, contentText);
            helper.setVisible(R.id.llComment, !this.subComment);
            helper.setText(R.id.tvCommentCount, commentCount);
            helper.setText(R.id.tvPointCount, pointCount);
            ImageView ivComment = helper.getView(R.id.ivComment);
            ivComment.setImageTintList(subComment ? colorStatePrimary : colorStateIconGrey);
            ImageView ivPoint = helper.getView(R.id.ivPoint);
            ivPoint.setImageTintList(point ? colorStatePrimary : colorStateIconGrey);
            if (official) {
                helper.setVisible(R.id.llReport, false);
            } else {
                helper.setVisible(R.id.llReport, true);
                ImageView ivReport = helper.getView(R.id.ivReport);
                ivReport.setImageTintList(report ? colorStatePrimary : colorStateIconGrey);
            }
            // listener
            helper.addOnClickListener(R.id.llPoint);
            helper.addOnClickListener(R.id.llReport);
        } else if (item.getItemType() == PostComment.KIND_JAB) {
            // jab
            if (couple == null) {
                helper.setVisible(R.id.cvAvatarLeft, false);
                helper.setVisible(R.id.cvAvatarRight, false);
            } else {
                helper.setVisible(R.id.cvAvatarLeft, true);
                helper.setVisible(R.id.cvAvatarRight, true);
                FrescoAvatarView ivAvatarLeft = helper.getView(R.id.ivAvatarLeft);
                FrescoAvatarView ivAvatarRight = helper.getView(R.id.ivAvatarRight);
                ivAvatarLeft.setData(couple.getCreatorAvatar());
                ivAvatarRight.setData(couple.getInviteeAvatar());
            }
        }
    }

    // 删除评论
    public void showDeleteDialog(final int position) {
        PostComment item = getItem(position);
        if (!item.isMine()) {
            return;
        }
        MaterialDialog dialog = DialogHelper.getBuild(mActivity)
                .cancelable(true)
                .canceledOnTouchOutside(true)
                .content(R.string.confirm_del_comment)
                .positiveText(R.string.confirm_no_wrong)
                .negativeText(R.string.i_think_again)
                .onPositive((dialog1, which) -> delCommentApi(position))
                .build();
        DialogHelper.showWithAnim(dialog);
    }

    private void delCommentApi(final int position) {
        final PostComment item = getItem(position);
        Call<Result> call = new RetrofitHelper().call(API.class).topicPostCommentDel(item.getId());
        MaterialDialog loading = mActivity.getLoading(true);
        RetrofitHelper.enqueue(call, loading, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                remove(position);
                // event
                RxBus.Event<Long> eventPostDetail = new RxBus.Event<>(ConsHelper.EVENT_POST_DETAIL_REFRESH, item.getPostId());
                RxBus.post(eventPostDetail);
                if (subComment) {
                    RxBus.Event<Long> eventPostCommentDetail = new RxBus.Event<>(ConsHelper.EVENT_POST_COMMENT_DETAIL_REFRESH, item.getToCommentId());
                    RxBus.post(eventPostCommentDetail);
                }
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
            }
        });
    }

    public void pointPush(final int position, boolean api) {
        final PostComment item = getItem(position);
        boolean newPoint = !item.isPoint();
        int newPointCount = newPoint ? item.getPointCount() + 1 : item.getPointCount() - 1;
        if (newPointCount < 0) {
            newPointCount = 0;
        }
        item.setPoint(newPoint);
        item.setPointCount(newPointCount);
        notifyItemChanged(position + getHeaderLayoutCount());
        if (!api) return;
        PostCommentPoint body = new PostCommentPoint();
        body.setPostCommentId(item.getId());
        Call<Result> call = new RetrofitHelper().call(API.class).topicPostCommentPointToggle(body);
        RetrofitHelper.enqueue(call, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                pointPush(position, false);
            }
        });
    }

    public void reportPush(final int position, boolean api) {
        final PostComment item = getItem(position);
        if (item.isReport() || item.isOfficial()) return;
        item.setReport(true);
        notifyItemChanged(position + getHeaderLayoutCount());
        if (!api) return;
        PostCommentReport body = new PostCommentReport();
        body.setPostCommentId(item.getId());
        Call<Result> call = new RetrofitHelper().call(API.class).topicPostCommentReportAdd(body);
        RetrofitHelper.enqueue(call, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                reportPush(position, false);
            }
        });
    }

    public void goSubCommentDetail(int position) {
        if (subComment) return;
        PostComment item = getItem(position);
        PostSubCommentListActivity.goActivity(mActivity, item);
    }

}
