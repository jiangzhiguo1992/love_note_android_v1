package com.jiangzg.lovenote.adapter;

import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jiangzg.base.view.ViewUtils;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.activity.settings.SuggestDetailActivity;
import com.jiangzg.lovenote.helper.CountHelper;
import com.jiangzg.lovenote.helper.TimeHelper;
import com.jiangzg.lovenote.helper.ViewHelper;
import com.jiangzg.lovenote.model.engine.SuggestInfo;
import com.jiangzg.lovenote.model.entity.Suggest;
import com.jiangzg.lovenote.view.GWrapView;

import java.util.Locale;

/**
 * Created by JZG on 2018/3/13.
 * 意见反馈适配器
 */
public class SuggestAdapter extends BaseQuickAdapter<Suggest, BaseViewHolder> {

    private final ColorStateList colorPrimaryStateList;
    private final ColorStateList colorGreyStateList;
    private final String formatCreateAt;
    private final String formatUpdateAt;
    private final String formatFollow;
    private final String formatComment;
    private final String formatTop;
    private final String formatOfficial;
    private final String formatMine;
    private FragmentActivity mActivity;

    public SuggestAdapter(FragmentActivity activity) {
        super(R.layout.list_item_suggest);
        mActivity = activity;
        formatCreateAt = mActivity.getString(R.string.create_at_colon_space_holder);
        formatUpdateAt = mActivity.getString(R.string.update_at_colon_space_holder);
        formatFollow = mActivity.getString(R.string.follow);
        formatComment = mActivity.getString(R.string.comment);
        formatTop = mActivity.getString(R.string.top);
        formatOfficial = mActivity.getString(R.string.administrators);
        formatMine = mActivity.getString(R.string.me_de);
        // color
        int rId = ViewUtils.getColorPrimary(activity);
        int colorPrimary = ContextCompat.getColor(activity, rId);
        int colorGrey = ContextCompat.getColor(activity, R.color.icon_grey);
        colorPrimaryStateList = ColorStateList.valueOf(colorPrimary);
        colorGreyStateList = ColorStateList.valueOf(colorGrey);
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
        long createdAt = item.getCreateAt();
        String create = TimeHelper.getTimeShowLine_HM_MD_YMD_ByGo(createdAt);
        String createShow = String.format(Locale.getDefault(), formatCreateAt, create);
        long updatedAt = item.getUpdateAt();
        String update = TimeHelper.getTimeShowLine_HM_MD_YMD_ByGo(updatedAt);
        String updatedShow = String.format(Locale.getDefault(), formatUpdateAt, update);
        final long followCount = item.getFollowCount();
        String followShow;
        if (followCount <= 0) {
            followShow = formatFollow;
        } else {
            followShow = CountHelper.getShowCount2Thousand(followCount);
        }
        long commentCount = item.getCommentCount();
        String commentShow;
        if (commentCount <= 0) {
            commentShow = formatComment;
        } else {
            commentShow = CountHelper.getShowCount2Thousand(commentCount);
        }
        final boolean follow = item.isFollow();
        boolean comment = item.isComment();
        // view
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
        if (follow) {
            Drawable visibility = ContextCompat.getDrawable(mActivity, R.mipmap.ic_visibility_grey_18dp);
            if (visibility != null) {
                visibility.setTint(ContextCompat.getColor(mActivity, ViewUtils.getColorPrimary(mActivity)));
                helper.setImageDrawable(R.id.ivFollow, visibility);
            }
        } else {
            helper.setImageResource(R.id.ivFollow, R.mipmap.ic_visibility_off_grey_18dp);
        }
        ImageView ivComment = helper.getView(R.id.ivComment);
        if (comment) {
            ivComment.setImageTintList(colorPrimaryStateList);
        } else {
            ivComment.setImageTintList(colorGreyStateList);
        }
    }

    public void goSuggestDetail(int position) {
        Suggest item = getItem(position);
        SuggestDetailActivity.goActivity(mActivity, item);
    }

}
