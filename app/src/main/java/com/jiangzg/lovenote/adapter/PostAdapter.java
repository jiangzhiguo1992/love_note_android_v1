package com.jiangzg.lovenote.adapter;

import android.content.res.ColorStateList;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.activity.topic.PostDetailActivity;
import com.jiangzg.lovenote.helper.CountHelper;
import com.jiangzg.lovenote.helper.DialogHelper;
import com.jiangzg.lovenote.helper.ListHelper;
import com.jiangzg.lovenote.helper.RecyclerHelper;
import com.jiangzg.lovenote.helper.RetrofitHelper;
import com.jiangzg.lovenote.helper.TimeHelper;
import com.jiangzg.lovenote.helper.ViewHelper;
import com.jiangzg.lovenote.model.api.API;
import com.jiangzg.lovenote.model.api.Result;
import com.jiangzg.lovenote.model.entity.Couple;
import com.jiangzg.lovenote.model.entity.Post;
import com.jiangzg.lovenote.model.entity.PostCollect;
import com.jiangzg.lovenote.model.entity.PostKindInfo;
import com.jiangzg.lovenote.view.FrescoAvatarView;
import com.jiangzg.lovenote.view.GWrapView;

import java.util.List;

import retrofit2.Call;

/**
 * Created by JZG on 2018/3/13.
 * 帖子适配器
 */
public class PostAdapter extends BaseQuickAdapter<Post, BaseViewHolder> {

    private final ColorStateList colorPrimaryStateList, colorGreyStateList;
    private final int colorPrimary, colorFontGrey, colorFontBlack;
    private FragmentActivity mActivity;
    private boolean kindShow, subKindShow;


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
    }

    @Override
    protected void convert(BaseViewHolder helper, Post item) {
        helper.setVisible(R.id.tvCover, item.isScreen() || item.isDelete());
        if (item.isScreen()) {
            helper.setVisible(R.id.llInfo, false);
            helper.setVisible(R.id.tvCover, true);
            helper.setText(R.id.tvCover, R.string.post_already_be_screen);
            return;
        } else if (item.isDelete()) {
            helper.setVisible(R.id.llInfo, false);
            helper.setVisible(R.id.tvCover, true);
            helper.setText(R.id.tvCover, R.string.post_already_be_delete);
            return;
        }
        helper.setVisible(R.id.llInfo, true);
        helper.setVisible(R.id.tvCover, false);
        // data
        boolean isOur = item.isOur();
        Couple couple = item.getCouple();
        PostKindInfo kindInfo = ListHelper.getPostKindInfo(item.getKind());
        List<String> tagShowList = ListHelper.getPostTagListShow(kindInfo, item, kindShow, subKindShow);
        String update = TimeHelper.getTimeShowLine_HM_MD_YMD_ByGo(item.getUpdateAt());
        String title = item.getTitle();
        String contentText = item.getContentText();
        List<String> imageList = item.getContentImageList();
        String pointCount = CountHelper.getShowCount2Thousand(item.getPointCount());
        String collectCount = CountHelper.getShowCount2Thousand(item.getCollectCount());
        String commentCount = CountHelper.getShowCount2Thousand(item.getCommentCount());
        boolean read = item.isRead();
        boolean point = item.isPoint();
        boolean collect = item.isCollect();
        boolean comment = item.isComment();
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
        // time
        helper.setText(R.id.tvUpdateAt, update);
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
        // couple
        if (couple == null) {
            helper.setVisible(R.id.llCouple, false);
        } else {
            helper.setVisible(R.id.llCouple, true);
            helper.setVisible(R.id.llCoupleName, false);
            helper.setVisible(R.id.rlCoupleAvatar, false);
            String creatorName = couple.getCreatorName();
            String inviteeName = couple.getInviteeName();
            if (!StringUtils.isEmpty(creatorName) || !StringUtils.isEmpty(inviteeName)) {
                helper.setVisible(R.id.llCoupleName, true);
                helper.setText(R.id.tvNameLeft, creatorName);
                helper.setText(R.id.tvNameRight, inviteeName);
                if (isOur) {
                    if (item.getUserId() == couple.getCreatorId()) {
                        helper.setTextColor(R.id.tvNameLeft, colorPrimary);
                        helper.setTextColor(R.id.tvNameRight, colorFontGrey);
                    } else {
                        helper.setTextColor(R.id.tvNameLeft, colorFontGrey);
                        helper.setTextColor(R.id.tvNameRight, colorPrimary);
                    }
                }
            }
            String creatorAvatar = couple.getCreatorAvatar();
            String inviteeAvatar = couple.getInviteeAvatar();
            if (!StringUtils.isEmpty(creatorAvatar) || !StringUtils.isEmpty(inviteeAvatar)) {
                helper.setVisible(R.id.rlCoupleAvatar, true);
                FrescoAvatarView ivAvatarLeft = helper.getView(R.id.ivAvatarLeft);
                FrescoAvatarView ivAvatarRight = helper.getView(R.id.ivAvatarRight);
                ivAvatarLeft.setData(couple.getCreatorAvatar());
                ivAvatarRight.setData(couple.getInviteeAvatar());
            }
        }
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
        // listener 不要了，点击区域有bug
        //if (!item.isScreen() && !item.isDelete()) helper.addOnClickListener(R.id.rvImage);
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
                .onPositive((dialog1, which) -> delCollect(position))
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
