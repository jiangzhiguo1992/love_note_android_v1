package com.jiangzg.ita.adapter;

import android.content.res.ColorStateList;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jiangzg.base.common.ConvertUtils;
import com.jiangzg.ita.R;
import com.jiangzg.ita.domain.Suggest;
import com.jiangzg.ita.utils.TimeUtils;
import com.jiangzg.ita.utils.ViewUtils;
import com.jiangzg.ita.view.GWrapView;

/**
 * Created by JZG on 2018/3/13.
 * 意见反馈适配器
 */
public class SuggestAdapter extends BaseQuickAdapter<Suggest, BaseViewHolder> {

    private FragmentActivity mActivity;
    private final int colorGrey;
    private final int colorPrimary;

    public SuggestAdapter(FragmentActivity activity) {
        super(R.layout.list_item_suggest);
        mActivity = activity;
        colorGrey = ContextCompat.getColor(activity, R.color.icon_grey);
        int rId = ViewUtils.getColorPrimary(activity);
        colorPrimary = ContextCompat.getColor(activity, rId);
    }

    @Override
    protected void convert(BaseViewHolder helper, Suggest item) {
        // data
        String title = item.getTitle();
        long createdAt = item.getCreatedAt();
        String create = TimeUtils.getSuggestShowBySecond(createdAt);
        String createShow = String.format(mActivity.getString(R.string.create_at_holder), create);
        long updatedAt = item.getUpdatedAt();
        String update = TimeUtils.getSuggestShowBySecond(updatedAt);
        String updatedShow = String.format(mActivity.getString(R.string.update_at_holder), update);
        final int followCount = item.getFollowCount();
        String followShow;
        if (followCount <= 0) {
            followShow = mActivity.getString(R.string.follow);
        } else {
            followShow = followCount + "";
        }
        int commentCount = item.getCommentCount();
        String commentShow;
        if (commentCount <= 0) {
            commentShow = mActivity.getString(R.string.comment);
        } else {
            commentShow = commentCount + "";
        }
        final boolean follow = item.isFollow();
        boolean comment = item.isComment();
        boolean official = item.isOfficial();
        boolean top = item.isTop();
        String typeShow = item.getTypeShow();
        String statusShow = item.getStatusShow();
        // view
        helper.setText(R.id.tvTitle, title);
        helper.setText(R.id.tvCreateAt, createShow);
        helper.setText(R.id.tvUpdateAt, updatedShow);
        helper.setText(R.id.tvWatch, followShow);
        helper.setText(R.id.tvComment, commentShow);
        if (follow) {
            helper.setImageResource(R.id.ivWatch, R.drawable.ic_visibility_on_primary);
        } else {
            helper.setImageResource(R.id.ivWatch, R.drawable.ic_visibility_off_grey);
        }
        ImageView ivComment = helper.getView(R.id.ivComment);
        if (comment) {
            ivComment.setImageTintList(ColorStateList.valueOf(colorPrimary));
        } else {
            ivComment.setImageTintList(ColorStateList.valueOf(colorGrey));
        }

        GWrapView wvTag = helper.getView(R.id.wvTag);
        wvTag.removeAllChild();
        if (official) {
            wvTag.addChild(getTagView(mActivity.getString(R.string.official), R.drawable.shape_r2_solid_red));
        }
        if (top) {
            wvTag.addChild(getTagView(mActivity.getString(R.string.top), R.drawable.shape_r2_solid_orange));
        }
        wvTag.addChild(getTagView(typeShow, R.drawable.shape_r2_solid_blue));
        wvTag.addChild(getTagView(statusShow, R.drawable.shape_r2_solid_green));
        // listener
        helper.addOnClickListener(R.id.llTitle);
        helper.addOnClickListener(R.id.llWatch);
        helper.addOnClickListener(R.id.llComment);
    }

    private View getTagView(String show, @DrawableRes int resId) {
        TextView textView = new TextView(mActivity);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int dp7 = ConvertUtils.dp2px(7);
        layoutParams.setMarginEnd(dp7);
        textView.setLayoutParams(layoutParams);
        textView.setBackgroundResource(resId);
        int dp5 = ConvertUtils.dp2px(5);
        int dp2 = ConvertUtils.dp2px(2);
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

    public void goSuggestDetail(int position) {
        Suggest item = getItem(position);
        // todo 详情页跳转
    }

    public void toggleWatch(int position) {
        Suggest item = getItem(position);
        boolean follow = item.isFollow();
        int followCount = item.getFollowCount();
        boolean willWatch = !follow;
        item.setFollow(willWatch);
        int willWatchCount;
        if (willWatch) {
            willWatchCount = followCount + 1;
        } else {
            willWatchCount = followCount - 1;
        }
        if (willWatchCount <= 0) {
            willWatchCount = 0;
        }
        item.setFollowCount(willWatchCount);
        notifyItemChanged(position + getHeaderLayoutCount());
        // todo api
    }

    public void comment(int position) {
        // todo 详情页跳转 位置到评论
    }
}
