package com.jiangzg.mianmian.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.mianmian.R;
import com.jiangzg.mianmian.activity.topic.PostDetailActivity;
import com.jiangzg.mianmian.activity.topic.PostSubCommentListActivity;
import com.jiangzg.mianmian.base.BaseActivity;
import com.jiangzg.mianmian.domain.Couple;
import com.jiangzg.mianmian.domain.TopicMessage;
import com.jiangzg.mianmian.helper.TimeHelper;
import com.jiangzg.mianmian.view.FrescoAvatarView;

/**
 * Created by JZG on 2018/3/12.
 * 话题消息
 */
public class TopicMessageAdapter extends BaseQuickAdapter<TopicMessage, BaseViewHolder> {

    private BaseActivity mActivity;

    public TopicMessageAdapter(BaseActivity activity) {
        super(R.layout.list_item_topic_message);
        mActivity = activity;
    }

    @Override
    protected void convert(BaseViewHolder helper, TopicMessage item) {
        // data
        Couple couple = item.getCouple();
        String time = TimeHelper.getTimeShowLine_HM_MDHM_YMDHM_ByGo(item.getCreateAt());
        String contentText = StringUtils.isEmpty(item.getContentText()) ? mActivity.getString(R.string.receive_new_message) : item.getContentText();
        // view
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
        helper.setText(R.id.tvTime, time);
        helper.setText(R.id.tvContent, contentText);
    }

    public void goSomeDetail(int position) {
        TopicMessage item = getItem(position);
        int kind = item.getKind();
        long contentId = item.getContentId();
        if (kind == TopicMessage.KIND_ALL || kind == TopicMessage.KIND_OFFICIAL_TEXT) return;
        switch (kind) {
            case TopicMessage.KIND_POST_BE_REPORT: // 举报
            case TopicMessage.KIND_POST_BE_POINT: // 点赞
            case TopicMessage.KIND_POST_BE_COLLECT: // 收藏
            case TopicMessage.KIND_POST_BE_COMMENT: // 评论
                PostDetailActivity.goActivity(mActivity, contentId);
                return;
            case TopicMessage.KIND_COMMENT_BE_REPLY: // 回复
            case TopicMessage.KIND_COMMENT_BE_REPORT: // 举报
            case TopicMessage.KIND_COMMENT_BE_POINT: // 点赞
                PostSubCommentListActivity.goActivity(mActivity, contentId);
        }
    }

}
