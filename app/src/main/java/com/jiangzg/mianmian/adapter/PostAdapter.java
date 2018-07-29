package com.jiangzg.mianmian.adapter;

import android.content.res.ColorStateList;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.mianmian.R;
import com.jiangzg.mianmian.activity.topic.PostDetailActivity;
import com.jiangzg.mianmian.domain.Couple;
import com.jiangzg.mianmian.domain.Post;
import com.jiangzg.mianmian.domain.PostCollect;
import com.jiangzg.mianmian.domain.PostComment;
import com.jiangzg.mianmian.domain.Result;
import com.jiangzg.mianmian.helper.API;
import com.jiangzg.mianmian.helper.DialogHelper;
import com.jiangzg.mianmian.helper.ListHelper;
import com.jiangzg.mianmian.helper.RecyclerHelper;
import com.jiangzg.mianmian.helper.RetrofitHelper;
import com.jiangzg.mianmian.helper.TimeHelper;
import com.jiangzg.mianmian.helper.ViewHelper;
import com.jiangzg.mianmian.view.FrescoAvatarView;
import com.jiangzg.mianmian.view.GWrapView;

import java.util.List;
import java.util.Locale;

import retrofit2.Call;

/**
 * Created by JZG on 2018/3/13.
 * Topic适配器
 */
public class PostAdapter extends BaseQuickAdapter<Post, BaseViewHolder> {

    private FragmentActivity mActivity;
    private boolean kindShow, subKindShow;
    private final ColorStateList colorPrimaryStateList, colorGreyStateList;
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
        // format
        formatCreateAt = mActivity.getString(R.string.create_at_colon_space_holder);
        formatUpdateAt = mActivity.getString(R.string.update_at_colon_space_holder);
    }

    @Override
    protected void convert(BaseViewHolder helper, Post item) {
        helper.setVisible(R.id.tvCover, item.isScreen() || item.isDelete());
        if (item.isScreen()) {
            helper.setVisible(R.id.tvCover, true);
            helper.setVisible(R.id.llInfo, false);
            helper.setText(R.id.tvCover, R.string.post_already_be_screen);
            return;
        } else if (item.isDelete()) {
            helper.setVisible(R.id.tvCover, true);
            helper.setVisible(R.id.llInfo, false);
            helper.setText(R.id.tvCover, R.string.post_already_be_delete);
            return;
        }
        helper.setVisible(R.id.tvCover, false);
        helper.setVisible(R.id.llInfo, true);
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
                View tagView = ViewHelper.getWrapTextView(mActivity, tag);
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
        ImageView ivPoint = helper.getView(R.id.ivPoint);
        ivPoint.setImageTintList(point ? colorPrimaryStateList : colorGreyStateList);
        ImageView ivCollect = helper.getView(R.id.ivCollect);
        ivCollect.setImageTintList(collect ? colorPrimaryStateList : colorGreyStateList);
        ImageView ivComment = helper.getView(R.id.ivComment);
        ivComment.setImageTintList(comment ? colorPrimaryStateList : colorGreyStateList);
        // listener
        if (!item.isScreen() && !item.isDelete()) helper.addOnClickListener(R.id.rvImage);
    }

    public void goPostDetail(int position) {
        Post item = getItem(position);
        if (item == null || item.isScreen() || item.isDelete()) return;
        if (!item.isRead()) {
            item.setRead(true);
            notifyItemChanged(position);
        }
        PostDetailActivity.goActivity(mActivity, item);
    }

    public void showCollectDeleteDialog(final int position) {
        MaterialDialog dialog = DialogHelper.getBuild(mActivity)
                .cancelable(true)
                .canceledOnTouchOutside(true)
                .content(R.string.confirm_del_collect)
                .positiveText(R.string.confirm_no_wrong)
                .negativeText(R.string.i_think_again)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        delCollect(position);
                    }
                })
                .build();
        DialogHelper.showWithAnim(dialog);
    }

    private void delCollect(final int position) {
        Post item = getItem(position);
        PostCollect postCollect = new PostCollect();
        postCollect.setPostId(item.getId());
        Call<Result> callCollect = new RetrofitHelper().call(API.class).topicPostCollectToggle(postCollect);
        RetrofitHelper.enqueue(callCollect, null, new RetrofitHelper.CallBack() {
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
