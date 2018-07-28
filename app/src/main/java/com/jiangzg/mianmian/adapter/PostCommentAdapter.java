package com.jiangzg.mianmian.adapter;

import android.content.res.ColorStateList;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jiangzg.base.common.ConvertUtils;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.mianmian.R;
import com.jiangzg.mianmian.activity.topic.PostSubCommentListActivity;
import com.jiangzg.mianmian.base.BaseActivity;
import com.jiangzg.mianmian.domain.Couple;
import com.jiangzg.mianmian.domain.Post;
import com.jiangzg.mianmian.domain.PostComment;
import com.jiangzg.mianmian.domain.PostCommentPoint;
import com.jiangzg.mianmian.domain.PostCommentReport;
import com.jiangzg.mianmian.domain.Result;
import com.jiangzg.mianmian.domain.RxEvent;
import com.jiangzg.mianmian.helper.API;
import com.jiangzg.mianmian.helper.ConsHelper;
import com.jiangzg.mianmian.helper.DialogHelper;
import com.jiangzg.mianmian.helper.ListHelper;
import com.jiangzg.mianmian.helper.RetrofitHelper;
import com.jiangzg.mianmian.helper.RxBus;
import com.jiangzg.mianmian.helper.TimeHelper;
import com.jiangzg.mianmian.helper.ViewHelper;
import com.jiangzg.mianmian.view.FrescoAvatarView;
import com.jiangzg.mianmian.view.GWrapView;

import java.util.List;
import java.util.Locale;

import retrofit2.Call;

/**
 * Created by JZG on 2018/7/26.
 * 帖子评论适配器
 */
public class PostCommentAdapter extends BaseMultiItemQuickAdapter<PostComment, BaseViewHolder> {

    private BaseActivity mActivity;
    private final String formatFloor;
    private final ColorStateList colorStateGrey;
    private final ColorStateList colorStatePrimary;
    private final FrameLayout.LayoutParams mTextLayoutParams;
    private final int dp5, dp2;

    public PostCommentAdapter(BaseActivity activity) {
        super(null);
        addItemType(PostComment.KIND_TEXT, R.layout.list_item_post_comment_text);
        addItemType(PostComment.KIND_JAB, R.layout.list_item_post_comment_jab);
        mActivity = activity;
        formatFloor = mActivity.getString(R.string.holder_floor);
        // color
        int colorGrey = ContextCompat.getColor(mActivity, R.color.icon_grey);
        int colorPrimary = ContextCompat.getColor(mActivity, ViewHelper.getColorPrimary(mActivity));
        colorStateGrey = ColorStateList.valueOf(colorGrey);
        colorStatePrimary = ColorStateList.valueOf(colorPrimary);
        // wrap
        int dp7 = ConvertUtils.dp2px(7);
        dp5 = ConvertUtils.dp2px(5);
        dp2 = ConvertUtils.dp2px(2);
        mTextLayoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mTextLayoutParams.setMarginEnd(dp7);
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
        List<String> tagShowList = ListHelper.getPostCommentTagShowList(item);
        Couple couple = item.getCouple();
        String contentText = item.getContentText();
        String commentCount = item.getSubCommentCount() > 0 ? Post.getShowCount(item.getSubCommentCount()) : mActivity.getString(R.string.comment);
        String pointCount = item.getPointCount() > 0 ? Post.getShowCount(item.getPointCount()) : mActivity.getString(R.string.point);
        boolean official = item.isOfficial();
        boolean subComment = item.isSubComment();
        boolean point = item.isPoint();
        boolean report = item.isReport();
        // view
        helper.setText(R.id.tvFloor, floor);
        helper.setText(R.id.tvTime, create);
        GWrapView wvTag = helper.getView(R.id.wvTag);
        if (tagShowList == null || tagShowList.size() <= 0) {
            wvTag.setVisibility(View.GONE);
        } else {
            wvTag.setVisibility(View.VISIBLE);
            wvTag.removeAllChild();
            for (String tag : tagShowList) {
                View tagView = getTagView(tag);
                if (tagView == null) continue;
                wvTag.addChild(tagView);
            }
        }
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
            helper.setText(R.id.tvPointCount, pointCount);
            helper.setText(R.id.tvCommentCount, commentCount);
            ImageView ivComment = helper.getView(R.id.ivComment);
            ivComment.setImageTintList(subComment ? colorStatePrimary : colorStateGrey);
            ImageView ivPoint = helper.getView(R.id.ivPoint);
            ivPoint.setImageTintList(point ? colorStatePrimary : colorStateGrey);
            if (official) {
                helper.setVisible(R.id.llReport, false);
            } else {
                helper.setVisible(R.id.llReport, true);
                ImageView ivReport = helper.getView(R.id.ivReport);
                ivReport.setImageTintList(report ? colorStatePrimary : colorStateGrey);
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

    private View getTagView(String show) {
        if (StringUtils.isEmpty(show)) return null;
        TextView textView = new TextView(mActivity);
        textView.setLayoutParams(mTextLayoutParams);
        textView.setBackgroundResource(R.drawable.shape_solid_primary_r2);
        //textView.setBackgroundResource(resId);
        textView.setPadding(dp5, dp2, dp5, dp2);
        textView.setGravity(Gravity.CENTER);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            textView.setTextAppearance(R.style.FontWhiteSmall);
        } else {
            textView.setTextAppearance(mActivity, R.style.FontWhiteSmall);
        }
        textView.setText(show);
        return textView;
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
        final PostComment item = getItem(position);
        Call<Result> call = new RetrofitHelper().call(API.class).topicPostCommentDel(item.getId());
        MaterialDialog loading = mActivity.getLoading(true);
        RetrofitHelper.enqueue(call, loading, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                remove(position);
                // event
                RxEvent<Long> event = new RxEvent<>(ConsHelper.EVENT_POST_DETAIL_REFRESH, item.getPostId());
                RxBus.post(event);
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
        Call<Result> call = new RetrofitHelper().call(API.class).topicPostCommentPointAdd(body);
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
        PostComment item = getItem(position);
        PostSubCommentListActivity.goActivity(mActivity, item);
    }

}
