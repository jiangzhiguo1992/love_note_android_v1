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
import com.jiangzg.ita.activity.common.SuggestDetailActivity;
import com.jiangzg.ita.domain.Suggest;
import com.jiangzg.ita.helper.ConvertHelper;
import com.jiangzg.ita.helper.ViewHelper;
import com.jiangzg.ita.view.GWrapView;

/**
 * Created by JZG on 2018/3/13.
 * 意见反馈适配器
 */
public class SuggestListAdapter extends BaseQuickAdapter<Suggest, BaseViewHolder> {

    private FragmentActivity mActivity;
    private final FrameLayout.LayoutParams mTextLayoutParams;
    private final int dp5;
    private final int dp2;
    private final ColorStateList colorPrimaryStateList;
    private final ColorStateList colorGreyStateList;

    public SuggestListAdapter(FragmentActivity activity) {
        super(R.layout.list_item_suggest_list);
        mActivity = activity;
        // color
        int rId = ViewHelper.getColorPrimary(activity);
        int colorPrimary = ContextCompat.getColor(activity, rId);
        int colorGrey = ContextCompat.getColor(activity, R.color.icon_grey);
        colorPrimaryStateList = ColorStateList.valueOf(colorPrimary);
        colorGreyStateList = ColorStateList.valueOf(colorGrey);
        // wrap
        int dp7 = ConvertUtils.dp2px(7);
        dp5 = ConvertUtils.dp2px(5);
        dp2 = ConvertUtils.dp2px(2);
        mTextLayoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mTextLayoutParams.setMarginEnd(dp7);
    }

    @Override
    protected void convert(BaseViewHolder helper, Suggest item) {
        // data
        String title = item.getTitle();
        long createdAt = item.getCreatedAt();
        String create = ConvertHelper.ConvertSecond2Day(createdAt);
        String createShow = String.format(mActivity.getString(R.string.create_at_colon_holder), create);
        long updatedAt = item.getUpdatedAt();
        String update = ConvertHelper.ConvertSecond2Day(updatedAt);
        String updatedShow = String.format(mActivity.getString(R.string.update_at_colon_holder), update);
        final int followCount = item.getFollowCount();
        String followShow;
        if (followCount <= 0) {
            followShow = mActivity.getString(R.string.follow);
        } else {
            followShow = String.valueOf(followCount);
        }
        int commentCount = item.getCommentCount();
        String commentShow;
        if (commentCount <= 0) {
            commentShow = mActivity.getString(R.string.comment);
        } else {
            commentShow = String.valueOf(commentCount);
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
        helper.setText(R.id.tvFollow, followShow);
        helper.setText(R.id.tvComment, commentShow);
        if (follow) {
            helper.setImageResource(R.id.ivFollow, R.drawable.ic_visibility_on_primary);
        } else {
            helper.setImageResource(R.id.ivFollow, R.drawable.ic_visibility_off_grey);
        }
        ImageView ivComment = helper.getView(R.id.ivComment);
        if (comment) {
            ivComment.setImageTintList(colorPrimaryStateList);
        } else {
            ivComment.setImageTintList(colorGreyStateList);
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
    }

    private View getTagView(String show, @DrawableRes int resId) {
        TextView textView = new TextView(mActivity);
        textView.setLayoutParams(mTextLayoutParams);
        textView.setBackgroundResource(resId);
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
        SuggestDetailActivity.goActivity(mActivity, item);
    }

}
