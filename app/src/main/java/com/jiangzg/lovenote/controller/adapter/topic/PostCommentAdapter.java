package com.jiangzg.lovenote.controller.adapter.topic;

import android.content.res.ColorStateList;
import android.support.v4.content.ContextCompat;
import android.widget.ImageView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jiangzg.base.common.DateUtils;
import com.jiangzg.base.view.ViewUtils;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.controller.activity.base.BaseActivity;
import com.jiangzg.lovenote.controller.activity.topic.PostCommentDetailActivity;
import com.jiangzg.lovenote.helper.common.RxBus;
import com.jiangzg.lovenote.helper.common.ShowHelper;
import com.jiangzg.lovenote.helper.common.TimeHelper;
import com.jiangzg.lovenote.helper.common.UserHelper;
import com.jiangzg.lovenote.helper.system.RetrofitHelper;
import com.jiangzg.lovenote.helper.view.DialogHelper;
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
public class PostCommentAdapter extends BaseQuickAdapter<PostComment, BaseViewHolder> {

    private final int colorPrimary, colorFontBlack;
    private final ColorStateList colorStatePrimary, colorStateHint;
    private BaseActivity mActivity;
    private boolean isSubComment;

    public PostCommentAdapter(BaseActivity activity, boolean subComment) {
        super(R.layout.list_item_post_comment);
        mActivity = activity;
        this.isSubComment = subComment;
        // color
        colorPrimary = ContextCompat.getColor(mActivity, ViewUtils.getColorPrimary(mActivity));
        int colorHint = ContextCompat.getColor(mActivity, R.color.font_hint);
        colorFontBlack = ContextCompat.getColor(mActivity, R.color.font_black);
        colorStatePrimary = ColorStateList.valueOf(colorPrimary);
        colorStateHint = ColorStateList.valueOf(colorHint);
    }

    @Override
    protected void convert(BaseViewHolder helper, PostComment item) {
        if (item.isDelete() || item.isScreen()) {
            helper.setVisible(R.id.root, false);
            return;
        }
        helper.setVisible(R.id.root, true);
        // data
        Couple couple = item.getCouple();
        String avatar = UserHelper.getAvatar(couple, item.getUserId());
        String name = item.isOfficial() ? mActivity.getString(R.string.administrators) : UserHelper.getName(couple, item.getUserId(), true);
        String floor = String.format(Locale.getDefault(), mActivity.getString(R.string.holder_floor), item.getFloor());
        String time = ShowHelper.getBetweenTimeGoneShow(DateUtils.getCurrentLong() - TimeHelper.getJavaTimeByGo(item.getCreateAt()));
        String contentText = item.getContentText();
        String jabAvatar;
        String commentCount = ShowHelper.getShowCount2Thousand(item.getSubCommentCount());
        String pointCount = ShowHelper.getShowCount2Thousand(item.getPointCount());
        boolean report = item.isReport();
        boolean point = item.isPoint();
        boolean subComment = item.isSubComment();
        // top
        if (couple == null) {
            helper.setVisible(R.id.rlTop, false);
            jabAvatar = "";
        } else {
            helper.setVisible(R.id.rlTop, true);
            long jabId = (couple.getCreatorId() == item.getUserId()) ? couple.getInviteeId() : couple.getCreatorId();
            jabAvatar = UserHelper.getAvatar(couple, jabId);
            FrescoAvatarView ivAvatar = helper.getView(R.id.ivAvatar);
            ivAvatar.setData(avatar);
            helper.setText(R.id.tvName, name);
            helper.setText(R.id.tvFloor, floor);
            helper.setText(R.id.tvTime, time);
        }
        // center
        if (item.getKind() != PostComment.KIND_JAB) {
            helper.setText(R.id.tvContent, contentText);
            helper.setTextColor(R.id.tvContent, colorFontBlack);
            helper.setVisible(R.id.ivJab, false);
        } else {
            helper.setText(R.id.tvContent, mActivity.getString(R.string.jab_a_little));
            helper.setTextColor(R.id.tvContent, colorPrimary);
            helper.setVisible(R.id.ivJab, true);
            FrescoAvatarView ivJab = helper.getView(R.id.ivJab);
            ivJab.setData(jabAvatar);
        }
        // bottom
        ImageView ivReport = helper.getView(R.id.ivReport);
        ivReport.setImageTintList(report ? colorStatePrimary : colorStateHint);
        ImageView ivPoint = helper.getView(R.id.ivPoint);
        ivPoint.setImageTintList(point ? colorStatePrimary : colorStateHint);
        ImageView ivComment = helper.getView(R.id.ivComment);
        ivComment.setImageTintList(subComment ? colorStatePrimary : colorStateHint);
        helper.setText(R.id.tvCommentCount, commentCount);
        helper.setText(R.id.tvPointCount, pointCount);
        helper.setVisible(R.id.llComment, !this.isSubComment);
        // listener
        helper.addOnClickListener(R.id.llPoint);
        helper.addOnClickListener(R.id.llReport);
    }

    public void pointPush(final int position, boolean isApi) {
        final PostComment item = getItem(position);
        boolean newPoint = !item.isPoint();
        int newPointCount = newPoint ? item.getPointCount() + 1 : item.getPointCount() - 1;
        if (newPointCount < 0) {
            newPointCount = 0;
        }
        item.setPoint(newPoint);
        item.setPointCount(newPointCount);
        notifyItemChanged(position + getHeaderLayoutCount());
        if (!isApi) return;
        PostCommentPoint body = new PostCommentPoint();
        body.setPostCommentId(item.getId());
        // api
        Call<Result> api = new RetrofitHelper().call(API.class).topicPostCommentPointToggle(body);
        RetrofitHelper.enqueue(api, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                pointPush(position, false);
            }
        });
        mActivity.pushApi(api);
    }

    public void showReportDialog(final int position, boolean isApi) {
        MaterialDialog dialog = DialogHelper.getBuild(mActivity)
                .content(R.string.confirm_report_this_comment)
                .cancelable(true)
                .canceledOnTouchOutside(true)
                .positiveText(R.string.confirm_no_wrong)
                .negativeText(R.string.i_think_again)
                .onPositive((dialog1, which) -> reportPush(position, isApi))
                .build();
        DialogHelper.showWithAnim(dialog);
    }

    public void reportPush(final int position, boolean isApi) {
        final PostComment item = getItem(position);
        if (item.isReport() || item.isOfficial()) return;
        item.setReport(true);
        notifyItemChanged(position + getHeaderLayoutCount());
        if (!isApi) return;
        PostCommentReport body = new PostCommentReport();
        body.setPostCommentId(item.getId());
        // api
        Call<Result> api = new RetrofitHelper().call(API.class).topicPostCommentReportAdd(body);
        RetrofitHelper.enqueue(api, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                reportPush(position, false);
            }
        });
        mActivity.pushApi(api);
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
                .content(R.string.confirm_del_this_comment)
                .positiveText(R.string.confirm_no_wrong)
                .negativeText(R.string.i_think_again)
                .onPositive((dialog1, which) -> delCommentApi(position))
                .build();
        DialogHelper.showWithAnim(dialog);
    }

    private void delCommentApi(final int position) {
        final PostComment item = getItem(position);
        Call<Result> api = new RetrofitHelper().call(API.class).topicPostCommentDel(item.getId());
        RetrofitHelper.enqueue(api, mActivity.getLoading(true), new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                remove(position);
                // event
                RxBus.post(new RxBus.Event<>(RxBus.EVENT_POST_DETAIL_REFRESH, item.getPostId()));
                if (isSubComment) {
                    RxBus.post(new RxBus.Event<>(RxBus.EVENT_POST_COMMENT_DETAIL_REFRESH, item.getToCommentId()));
                }
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
            }
        });
        mActivity.pushApi(api);
    }

    public void goSubCommentDetail(int position) {
        if (isSubComment) return;
        PostComment item = getItem(position);
        PostCommentDetailActivity.goActivity(mActivity, item);
    }

}
