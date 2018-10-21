package com.jiangzg.lovenote.receiver;

import android.content.Context;

import com.alibaba.sdk.android.push.MessageReceiver;
import com.alibaba.sdk.android.push.notification.CPushMessage;
import com.jiangzg.base.common.LogUtils;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.lovenote.activity.note.MensesActivity;
import com.jiangzg.lovenote.activity.settings.SuggestDetailActivity;
import com.jiangzg.lovenote.activity.topic.PostCollectActivity;
import com.jiangzg.lovenote.activity.topic.PostDetailActivity;
import com.jiangzg.lovenote.activity.topic.PostMineActivity;
import com.jiangzg.lovenote.activity.topic.PostSubCommentListActivity;
import com.jiangzg.lovenote.activity.topic.TopicMessageActivity;
import com.jiangzg.lovenote.domain.Push;
import com.jiangzg.lovenote.helper.GsonHelper;

import java.util.Map;

/**
 * Created by JZG on 2018/10/19.
 * 阿里推送
 */
public class AliPushReceiver extends MessageReceiver {

    // 收到消息，不会弹窗
    @Override
    public void onMessage(Context context, CPushMessage cPushMessage) {
        LogUtils.i(AliPushReceiver.class, "onMessage", "messageId: " + cPushMessage.getMessageId() + ", title: " + cPushMessage.getTitle() + ", content:" + cPushMessage.getContent());
    }

    // 收到通知，会弹窗
    @Override
    public void onNotification(Context context, String title, String summary, Map<String, String> extraMap) {
        LogUtils.i(AliPushReceiver.class, "onNotification", "title: " + title + ", summary: " + summary + ", extraMap: " + extraMap);
    }

    // 打开通知
    @Override
    public void onNotificationOpened(Context context, String title, String summary, String extraMap) {
        LogUtils.i(AliPushReceiver.class, "onNotificationOpened", "title: " + title + ", summary: " + summary + ", extraMap:" + extraMap);
        if (StringUtils.isEmpty(extraMap)) return;
        if (!extraMap.startsWith("{") || !extraMap.endsWith("}")) return;
        Push push = GsonHelper.get().fromJson(extraMap, Push.class);
        if (push == null) return;
        int contentType = push.getContentType();
        long contentId = push.getContentId();
        if (contentType == Push.TYPE_APP || contentType <= 0) return;
        // 以上都是打开app
        switch (contentType) {
            case Push.TYPE_SUGGEST: // 意见反馈
                SuggestDetailActivity.goActivity(context, contentId);
                break;
            case Push.TYPE_NOTE_MENSES: // 姨妈
                MensesActivity.goActivity(context);
                break;
            case Push.TYPE_TOPIC_MINE: // 话题我的
                PostMineActivity.goActivity(context);
                break;
            case Push.TYPE_TOPIC_COLLECT: // 话题收藏
                PostCollectActivity.goActivity(context);
                break;
            case Push.TYPE_TOPIC_MESSAGE: // 话题消息
                TopicMessageActivity.goActivity(context);
                break;
            case Push.TYPE_TOPIC_POST: // 帖子
                PostDetailActivity.goActivity(context, contentId);
                break;
            case Push.TYPE_TOPIC_COMMENT: // 评论
                PostSubCommentListActivity.goActivity(context, contentId);
                break;
        }
    }

    @Override
    protected void onNotificationClickedWithNoAction(Context context, String title, String summary, String extraMap) {
        LogUtils.i(AliPushReceiver.class, "onNotificationClickedWithNoAction", "title: " + title + ", summary: " + summary + ", extraMap:" + extraMap);
    }

    @Override
    protected void onNotificationReceivedInApp(Context context, String title, String summary, Map<String, String> extraMap, int openType, String openActivity, String openUrl) {
        LogUtils.i(AliPushReceiver.class, "onNotificationReceivedInApp", "title: " + title + ", summary: " + summary + ", extraMap:" + extraMap + ", openType:" + openType + ", openActivity:" + openActivity + ", openUrl:" + openUrl);
    }

    @Override
    protected void onNotificationRemoved(Context context, String messageId) {
        LogUtils.i(AliPushReceiver.class, "onNotificationRemoved", "---");
    }

}
