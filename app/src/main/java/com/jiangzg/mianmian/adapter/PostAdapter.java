package com.jiangzg.mianmian.adapter;

import android.content.res.ColorStateList;
import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.jiangzg.mianmian.activity.topic.PostDetailActivity;
import com.jiangzg.mianmian.domain.Couple;
import com.jiangzg.mianmian.domain.Post;
import com.jiangzg.mianmian.helper.ListHelper;
import com.jiangzg.mianmian.helper.RecyclerHelper;
import com.jiangzg.mianmian.helper.TimeHelper;
import com.jiangzg.mianmian.helper.ViewHelper;
import com.jiangzg.mianmian.view.FrescoAvatarView;
import com.jiangzg.mianmian.view.GWrapView;

import java.util.List;
import java.util.Locale;

/**
 * Created by JZG on 2018/3/13.
 * Topic适配器
 */
public class PostAdapter extends BaseQuickAdapter<Post, BaseViewHolder> {

    private FragmentActivity mActivity;
    private boolean kindShow, subKindShow;
    private final ColorStateList colorPrimaryStateList, colorGreyStateList;
    private final FrameLayout.LayoutParams mTextLayoutParams;
    private final int dp5, dp2;
    private final String formatCreateAt, formatUpdateAt;
    private final int colorPrimary, colorFontGrey, colorFontBlack;


    public PostAdapter(FragmentActivity activity, boolean kindShow, boolean subKindShow) {
        super(R.layout.list_item_post);
        mActivity = activity;
        this.kindShow = kindShow;
        this.subKindShow = subKindShow;
        // color
        int rId = ViewHelper.getColorPrimary(activity);
        int colorIconGrey = ContextCompat.getColor(activity, R.color.icon_grey);
        colorPrimary = ContextCompat.getColor(activity, rId);
        colorFontGrey = ContextCompat.getColor(activity, R.color.font_grey);
        colorFontBlack = ContextCompat.getColor(activity, R.color.font_black);
        colorGreyStateList = ColorStateList.valueOf(colorIconGrey);
        colorPrimaryStateList = ColorStateList.valueOf(colorPrimary);
        // wrap
        int dp7 = ConvertUtils.dp2px(7);
        dp5 = ConvertUtils.dp2px(5);
        dp2 = ConvertUtils.dp2px(2);
        mTextLayoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mTextLayoutParams.setMarginEnd(dp7);
        // format
        formatCreateAt = mActivity.getString(R.string.create_at_colon_space_holder);
        formatUpdateAt = mActivity.getString(R.string.update_at_colon_space_holder);
    }

    @Override
    protected void convert(BaseViewHolder helper, Post item) {
        // data
        boolean isOur = item.isOur();
        Couple couple = item.getCouple();
        List<String> tagShowList = ListHelper.getPostTagShowList(item, kindShow, subKindShow);
        String title = item.getTitle();
        String contentText = item.getContentText();
        List<String> imageList = item.getContentImageList();
        String address = item.getAddress();
        String create = TimeHelper.getTimeShowLine_HM_MD_YMD_ByGo(item.getCreateAt());
        String createShow = String.format(Locale.getDefault(), formatCreateAt, create);
        String update = TimeHelper.getTimeShowLine_HM_MD_YMD_ByGo(item.getUpdateAt());
        String updatedShow = String.format(Locale.getDefault(), formatUpdateAt, update);
        String pointCount = item.getPointCount() > 0 ? Post.getShowCount(item.getPointCount()) : mActivity.getString(R.string.point);
        String collectCount = item.getCollectCount() > 0 ? Post.getShowCount(item.getCollectCount()) : mActivity.getString(R.string.collect);
        String commentCount = item.getCommentCount() > 0 ? Post.getShowCount(item.getCommentCount()) : mActivity.getString(R.string.comment);
        boolean read = item.isRead();
        boolean report = item.isReport();
        boolean point = item.isPoint();
        boolean collect = item.isCollect();
        boolean comment = item.isComment();
        // couple
        if (couple == null) {
            helper.setVisible(R.id.llCouple, false);
        } else {
            helper.setVisible(R.id.llCouple, true);
            helper.setText(R.id.tvNameLeft, couple.getCreatorName());
            helper.setText(R.id.tvNameRight, couple.getInviteeName());
            if (isOur) {
                if (item.getUserId() == couple.getCreatorId()) {
                    helper.setTextColor(R.id.tvNameLeft, colorPrimary);
                    helper.setTextColor(R.id.tvNameRight, colorFontGrey);
                } else {
                    helper.setTextColor(R.id.tvNameLeft, colorFontGrey);
                    helper.setTextColor(R.id.tvNameRight, colorPrimary);
                }
            }
            FrescoAvatarView ivAvatarLeft = helper.getView(R.id.ivAvatarLeft);
            FrescoAvatarView ivAvatarRight = helper.getView(R.id.ivAvatarRight);
            ivAvatarLeft.setData(couple.getCreatorAvatar());
            ivAvatarRight.setData(couple.getInviteeAvatar());
        }
        // tag
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
        // title
        helper.setVisible(R.id.tvTitle, !StringUtils.isEmpty(title));
        helper.setText(R.id.tvTitle, title);
        // content
        helper.setVisible(R.id.tvContent, !StringUtils.isEmpty(contentText));
        helper.setText(R.id.tvContent, contentText);
        // rvImage
        RecyclerView rvImage = helper.getView(R.id.rvImage);
        if (imageList == null || imageList.size() <= 0) {
            rvImage.setVisibility(View.GONE);
        } else {
            rvImage.setVisibility(View.VISIBLE);
            ImgSquareShowAdapter adapter = new ImgSquareShowAdapter(mActivity, 3);
            new RecyclerHelper(rvImage)
                    .initLayoutManager(new GridLayoutManager(mActivity, 3))
                    .initAdapter(adapter)
                    .setAdapter()
                    .dataNew(imageList, 0);
            adapter.setVisibleLimit(3);
        }
        // address
        helper.setVisible(R.id.tvAddress, !StringUtils.isEmpty(address));
        helper.setText(R.id.tvAddress, address);
        // time
        helper.setText(R.id.tvCreateAt, createShow);
        helper.setText(R.id.tvUpdateAt, updatedShow);
        // count
        helper.setText(R.id.tvPoint, pointCount);
        helper.setText(R.id.tvCollect, collectCount);
        helper.setText(R.id.tvComment, commentCount);
        // user
        helper.setTextColor(R.id.tvTitle, read ? colorFontGrey : colorFontBlack);
        if (report) wvTag.addChild(getTagView(mActivity.getString(R.string.already_report)));
        ImageView ivPoint = helper.getView(R.id.ivPoint);
        ivPoint.setImageTintList(point ? colorPrimaryStateList : colorGreyStateList);
        ImageView ivCollect = helper.getView(R.id.ivCollect);
        ivCollect.setImageTintList(collect ? colorPrimaryStateList : colorGreyStateList);
        ImageView ivComment = helper.getView(R.id.ivComment);
        ivComment.setImageTintList(comment ? colorPrimaryStateList : colorGreyStateList);
        // listener
        helper.addOnClickListener(R.id.rvImage);
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

    public void goPostDetail(int position) {
        Post item = getItem(position);
        if (!item.isRead()) {
            item.setRead(true);
            notifyItemChanged(position);
        }
        PostDetailActivity.goActivity(mActivity, item);
    }

}
