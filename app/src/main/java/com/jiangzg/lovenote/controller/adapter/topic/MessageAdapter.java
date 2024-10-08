package com.jiangzg.lovenote.controller.adapter.topic;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jiangzg.base.common.DateUtils;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.controller.activity.base.BaseActivity;
import com.jiangzg.lovenote.controller.activity.topic.PostCommentDetailActivity;
import com.jiangzg.lovenote.controller.activity.topic.PostDetailActivity;
import com.jiangzg.lovenote.helper.common.ShowHelper;
import com.jiangzg.lovenote.helper.common.TimeHelper;
import com.jiangzg.lovenote.model.entity.Couple;
import com.jiangzg.lovenote.model.entity.TopicMessage;
import com.jiangzg.lovenote.view.FrescoAvatarView;

/**
 * Created by JZG on 2018/3/12.
 * 话题消息
 */
public class MessageAdapter extends BaseQuickAdapter<TopicMessage, BaseViewHolder> {

    private BaseActivity mActivity;

    public MessageAdapter(BaseActivity activity) {
        super(R.layout.list_item_topic_message);
        mActivity = activity;
    }

    @Override
    protected void convert(BaseViewHolder helper, TopicMessage item) {
        // data
        Couple couple = item.getCouple();
        String time = ShowHelper.getBetweenTimeGoneShow(DateUtils.getCurrentLong() - TimeHelper.getJavaTimeByGo(item.getUpdateAt()));
        String contentText = StringUtils.isEmpty(item.getContentText()) ? mActivity.getString(R.string.receive_new_message) : item.getContentText();
        // view
        FrescoAvatarView ivAvatarLeft = helper.getView(R.id.ivAvatarLeft);
        FrescoAvatarView ivAvatarRight = helper.getView(R.id.ivAvatarRight);
        ivAvatarLeft.setData(couple == null ? "" : couple.getCreatorAvatar());
        ivAvatarRight.setData(couple == null ? "" : couple.getInviteeAvatar());
        helper.setText(R.id.tvTime, time);
        helper.setText(R.id.tvContent, contentText);
    }

    public void goSomeDetail(int position) {
        TopicMessage item = getItem(position);
        int kind = item.getKind();
        long contentId = item.getContentId();
        if (kind == TopicMessage.KIND_ALL || kind == TopicMessage.KIND_OFFICIAL_TEXT) return;
        switch (kind) {
            case TopicMessage.KIND_JAB_IN_POST: // 被戳
            case TopicMessage.KIND_POST_BE_REPORT: // 举报
            case TopicMessage.KIND_POST_BE_POINT: // 点赞
            case TopicMessage.KIND_POST_BE_COLLECT: // 收藏
            case TopicMessage.KIND_POST_BE_COMMENT: // 评论
                PostDetailActivity.goActivity(mActivity, contentId);
                return;
            case TopicMessage.KIND_JAB_IN_COMMENT: // 被戳
            case TopicMessage.KIND_COMMENT_BE_REPLY: // 回复
            case TopicMessage.KIND_COMMENT_BE_REPORT: // 举报
            case TopicMessage.KIND_COMMENT_BE_POINT: // 点赞
                PostCommentDetailActivity.goActivity(mActivity, contentId);
        }
    }

}
