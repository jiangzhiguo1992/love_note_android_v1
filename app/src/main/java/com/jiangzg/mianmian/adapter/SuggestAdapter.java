package com.jiangzg.mianmian.adapter;

import android.content.res.ColorStateList;
import android.os.Build;
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
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.mianmian.R;
import com.jiangzg.mianmian.activity.settings.SuggestDetailActivity;
import com.jiangzg.mianmian.domain.Suggest;
import com.jiangzg.mianmian.domain.SuggestInfo;
import com.jiangzg.mianmian.helper.ConvertHelper;
import com.jiangzg.mianmian.helper.ViewHelper;
import com.jiangzg.mianmian.view.GWrapView;

import java.util.Locale;

/**
 * Created by JZG on 2018/3/13.
 * 意见反馈适配器
 */
public class SuggestAdapter extends BaseQuickAdapter<Suggest, BaseViewHolder> {

    private FragmentActivity mActivity;
    private final FrameLayout.LayoutParams mTextLayoutParams;
    private final int dp5;
    private final int dp2;
    private final ColorStateList colorPrimaryStateList;
    private final ColorStateList colorGreyStateList;

    public SuggestAdapter(FragmentActivity activity) {
        super(R.layout.list_item_suggest);
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
        boolean top = item.isTop();
        boolean official = item.isOfficial();
        boolean mine = item.isMine();
        String statusShow = item.getStatus() > 0 ? SuggestInfo.getStatusShow(item.getStatus()) : "";
        String typeShow = SuggestInfo.getTypeShow(item.getContentType());
        String title = item.getTitle();
        String contentText = item.getContentText();
        long createdAt = item.getCreateAt();
        String create = ConvertHelper.getTimeShowLine_HM_MD_YMD_ByGo(createdAt);
        String createShow = String.format(Locale.getDefault(), mActivity.getString(R.string.create_at_colon_space_holder), create);
        long updatedAt = item.getUpdateAt();
        String update = ConvertHelper.getTimeShowLine_HM_MD_YMD_ByGo(updatedAt);
        String updatedShow = String.format(Locale.getDefault(), mActivity.getString(R.string.update_at_colon_space_holder), update);
        final long followCount = item.getFollowCount();
        String followShow;
        if (followCount <= 0) {
            followShow = mActivity.getString(R.string.follow);
        } else {
            followShow = String.valueOf(followCount);
        }
        long commentCount = item.getCommentCount();
        String commentShow;
        if (commentCount <= 0) {
            commentShow = mActivity.getString(R.string.comment);
        } else {
            commentShow = String.valueOf(commentCount);
        }
        final boolean follow = item.isFollow();
        boolean comment = item.isComment();
        // view
        GWrapView wvTag = helper.getView(R.id.wvTag);
        wvTag.removeAllChild();
        if (top) {
            View tagTop = getTagView(mActivity.getString(R.string.top));
            wvTag.addChild(tagTop);
        }
        if (official) {
            View tagOfficial = getTagView(mActivity.getString(R.string.official));
            wvTag.addChild(tagOfficial);
        }
        if (mine) {
            View tagMine = getTagView(mActivity.getString(R.string.me_de));
            wvTag.addChild(tagMine);
        }
        View tagStatus = getTagView(statusShow);
        wvTag.addChild(tagStatus);
        View tagType = getTagView(typeShow);
        wvTag.addChild(tagType);
        helper.setText(R.id.tvTitle, title);
        helper.setText(R.id.tvContent, contentText);
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
    }

    private View getTagView(String show) {
        if (StringUtils.isEmpty(show)) return null;
        TextView textView = new TextView(mActivity);
        textView.setLayoutParams(mTextLayoutParams);
        textView.setBackgroundResource(R.drawable.shape_solid_primary_r2);
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
