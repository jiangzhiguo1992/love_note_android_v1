package com.jiangzg.lovenote.controller.adapter.topic;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.afollestad.materialdialogs.MaterialDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jiangzg.base.common.DateUtils;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.controller.activity.base.BaseActivity;
import com.jiangzg.lovenote.controller.activity.topic.PostDetailActivity;
import com.jiangzg.lovenote.controller.adapter.common.ImgSquareShowAdapter;
import com.jiangzg.lovenote.helper.common.CountHelper;
import com.jiangzg.lovenote.helper.common.SPHelper;
import com.jiangzg.lovenote.helper.common.TimeHelper;
import com.jiangzg.lovenote.helper.system.AdHelper;
import com.jiangzg.lovenote.helper.system.RetrofitHelper;
import com.jiangzg.lovenote.helper.view.DialogHelper;
import com.jiangzg.lovenote.helper.view.RecyclerHelper;
import com.jiangzg.lovenote.model.api.API;
import com.jiangzg.lovenote.model.api.Result;
import com.jiangzg.lovenote.model.entity.Couple;
import com.jiangzg.lovenote.model.entity.Post;
import com.jiangzg.lovenote.model.entity.PostCollect;
import com.jiangzg.lovenote.view.FrescoAvatarView;
import com.qq.e.ads.nativ.ADSize;
import com.qq.e.ads.nativ.NativeExpressADView;

import java.util.List;

import retrofit2.Call;

/**
 * Created by JZG on 2018/3/13.
 * 帖子适配器
 */
public class PostAdapter extends BaseQuickAdapter<Post, BaseViewHolder> {

    private BaseActivity mActivity;
    private final int colorFontGrey, colorFontBlack;
    private boolean adShow;
    private List<NativeExpressADView> adViewList;
    // TODO close

    public PostAdapter(BaseActivity activity, boolean ad) {
        super(R.layout.list_item_post);
        mActivity = activity;
        // color
        colorFontGrey = ContextCompat.getColor(activity, R.color.font_grey);
        colorFontBlack = ContextCompat.getColor(activity, R.color.font_black);
        // ad
        adShow = ad && AdHelper.canAd(activity) && !SPHelper.getVipLimit().isAdvertiseHide();
        if (adShow) {
            AdHelper.createAd(activity, ADSize.FULL_WIDTH, ADSize.AUTO_HEIGHT, 5, viewList -> {
                PostAdapter.this.adDestroy();
                adViewList = viewList;
                for (int i = 0; i < PostAdapter.this.getData().size(); i++) {
                    if (isAdPosition(i)) {
                        PostAdapter.this.notifyItemChanged(i + getHeaderLayoutCount());
                    }
                }
            });
        }
    }

    public void adDestroy() {
        if (adViewList == null || adViewList.size() <= 0) return;
        for (NativeExpressADView view : adViewList) {
            AdHelper.destroy(view);
        }
    }

    private boolean isAdPosition(int position) {
        if (position <= 1) {
            return false;
        }
        return (position + 6) % 10 == 0; // 5 - 15 - 25
    }

    private int getAdIndex(int position) {
        if (adViewList == null || adViewList.size() <= 0) return -1;
        int index = position / 10;
        while (index >= adViewList.size()) {
            position = position - adViewList.size();
        }
        return index;
    }

    @Override
    protected void convert(BaseViewHolder helper, Post item) {
        CardView root = helper.getView(R.id.root);
        int tagAd = 111;
        int position = helper.getLayoutPosition() - getHeaderLayoutCount();
        int adIndex = getAdIndex(position);
        if (adShow && isAdPosition(position) && adIndex >= 0) {
            helper.setVisible(R.id.llInfo, false);
            helper.setVisible(R.id.tvCover, false);
            NativeExpressADView adView = adViewList.get(adIndex);
            if (adView != null) {
                ViewParent parent = adView.getParent();
                if (parent != null) {
                    ((ViewGroup) parent).removeView(adView);
                }
                adView.setTag(tagAd);
                root.addView(adView);
                adView.render();
            }
            return;
        }
        NativeExpressADView adView = root.findViewWithTag(tagAd);
        if (adView != null) {
            root.removeView(adView);
        }
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
        Couple couple = item.getCouple();
        String title = item.getTitle();
        boolean read = item.isRead();
        String contentText = item.getContentText();
        List<String> imageList = item.getContentImageList();
        String time = CountHelper.getBetweenTimeGoneShow(DateUtils.getCurrentLong() - TimeHelper.getJavaTimeByGo(item.getUpdateAt()));
        String pointCount = CountHelper.getShowCount2Thousand(item.getPointCount());
        String collectCount = CountHelper.getShowCount2Thousand(item.getCollectCount());
        String commentCount = CountHelper.getShowCount2Thousand(item.getCommentCount());
        // title
        helper.setVisible(R.id.tvTitle, !StringUtils.isEmpty(title));
        helper.setText(R.id.tvTitle, title);
        helper.setTextColor(R.id.tvTitle, read ? colorFontGrey : colorFontBlack);
        // content
        helper.setVisible(R.id.tvContent, !StringUtils.isEmpty(contentText));
        helper.setText(R.id.tvContent, contentText);
        // rvImage
        RecyclerView rvImage = helper.getView(R.id.rvImage);
        if (imageList == null || imageList.size() <= 0) {
            rvImage.setVisibility(View.GONE);
        } else {
            rvImage.setVisibility(View.VISIBLE);
            int spanCount = 3;
            ImgSquareShowAdapter adapter = new ImgSquareShowAdapter(mActivity, spanCount);
            new RecyclerHelper(rvImage)
                    .initLayoutManager(new GridLayoutManager(mActivity, spanCount))
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
            FrescoAvatarView ivAvatarLeft = helper.getView(R.id.ivAvatarLeft);
            FrescoAvatarView ivAvatarRight = helper.getView(R.id.ivAvatarRight);
            ivAvatarLeft.setData(couple.getCreatorAvatar());
            ivAvatarRight.setData(couple.getInviteeAvatar());
        }
        // time
        helper.setText(R.id.tvUpdateAt, time);
        // count
        helper.setText(R.id.tvPoint, pointCount);
        helper.setText(R.id.tvCollect, collectCount);
        helper.setText(R.id.tvComment, commentCount);
        // listener 不要了，点击区域有bug
        //if (!item.isScreen() && !item.isDelete()) helper.addOnClickListener(R.id.rvImage);
    }

    public void goPostDetail(int position) {
        if (isAdPosition(position)) {
            return;
        }
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
                .content(R.string.confirm_del_this_collect)
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
        // api
        Call<Result> api = new RetrofitHelper().call(API.class).topicPostCollectToggle(postCollect);
        RetrofitHelper.enqueue(api, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                remove(position);
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
            }
        });
        mActivity.pushApi(api);
    }

}
