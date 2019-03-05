package com.jiangzg.lovenote.controller.receiver;

import android.content.Context;

import com.alibaba.sdk.android.push.MessageReceiver;
import com.alibaba.sdk.android.push.notification.CPushMessage;
import com.jiangzg.base.common.LogUtils;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.lovenote.controller.activity.note.LockActivity;
import com.jiangzg.lovenote.controller.activity.note.MensesActivity;
import com.jiangzg.lovenote.controller.activity.note.NoteTotalActivity;
import com.jiangzg.lovenote.controller.activity.note.ShyActivity;
import com.jiangzg.lovenote.controller.activity.note.SleepActivity;
import com.jiangzg.lovenote.controller.activity.note.SouvenirListActivity;
import com.jiangzg.lovenote.controller.activity.note.TrendsListActivity;
import com.jiangzg.lovenote.controller.activity.settings.SuggestDetailActivity;
import com.jiangzg.lovenote.controller.activity.topic.PostDetailActivity;
import com.jiangzg.lovenote.controller.activity.topic.PostMineActivity;
import com.jiangzg.lovenote.controller.activity.topic.PostMyRelationActivity;
import com.jiangzg.lovenote.controller.activity.topic.PostSubCommentListActivity;
import com.jiangzg.lovenote.controller.activity.topic.TopicMessageActivity;
import com.jiangzg.lovenote.helper.common.GsonHelper;
import com.jiangzg.lovenote.model.engine.PushExtra;

import java.util.Map;

/**
 * Created by JZG on 2018/10/19.
 * 阿里推送
 */
public class AliPushReceiver extends MessageReceiver {

    // 收到消息，不会弹窗
    @Override
    public void onMessage(Context context, CPushMessage cPushMessage) {
        LogUtils.d(AliPushReceiver.class, "onMessage", "messageId: " + cPushMessage.getMessageId() + ", title: " + cPushMessage.getTitle() + ", content:" + cPushMessage.getContent());
    }

    // 收到通知，会弹窗
    @Override
    public void onNotification(Context context, String title, String summary, Map<String, String> extraMap) {
        LogUtils.d(AliPushReceiver.class, "onNotification", "title: " + title + ", summary: " + summary + ", extraMap: " + extraMap);
    }

    // 打开通知
    @Override
    public void onNotificationOpened(Context context, String title, String summary, String extraMap) {
        LogUtils.d(AliPushReceiver.class, "onNotificationOpened", "title: " + title + ", summary: " + summary + ", extraMap:" + extraMap);
        if (StringUtils.isEmpty(extraMap)) return;
        if (!extraMap.startsWith("{") || !extraMap.endsWith("}")) return;
        PushExtra pushExtra = GsonHelper.get().fromJson(extraMap, PushExtra.class);
        if (pushExtra == null) return;
        int contentType = pushExtra.getContentType();
        long contentId = pushExtra.getContentId();
        if (contentType == PushExtra.TYPE_APP || contentType <= 0) return;
        // 以上都是打开app
        switch (contentType) {
            case PushExtra.TYPE_SUGGEST: // 意见反馈
                SuggestDetailActivity.goActivity(context, contentId);
                break;
            case PushExtra.TYPE_NOTE_LOCK:
                LockActivity.goActivity(context);
                break;
            case PushExtra.TYPE_NOTE_TRENDS:
                TrendsListActivity.goActivity(context);
                break;
            case PushExtra.TYPE_NOTE_TOTAL:
                NoteTotalActivity.goActivity(context);
                break;
            case PushExtra.TYPE_NOTE_SHY: // 羞羞
                ShyActivity.goActivity(context);
                break;
            case PushExtra.TYPE_NOTE_MENSES: // 姨妈
                MensesActivity.goActivity(context);
                break;
            case PushExtra.TYPE_NOTE_SLEEP: // 睡眠
                SleepActivity.goActivity(context);
                break;
            case PushExtra.TYPE_NOTE_SOUVENIR: // 纪念日
                SouvenirListActivity.goActivity(context);
                break;
            case PushExtra.TYPE_TOPIC_MINE: // 话题我的
                PostMineActivity.goActivity(context);
                break;
            case PushExtra.TYPE_TOPIC_COLLECT: // 话题收藏
                PostMyRelationActivity.goActivity(context);
                break;
            case PushExtra.TYPE_TOPIC_MESSAGE: // 话题消息
                TopicMessageActivity.goActivity(context);
                break;
            case PushExtra.TYPE_TOPIC_POST: // 帖子
                PostDetailActivity.goActivity(context, contentId);
                break;
            case PushExtra.TYPE_TOPIC_COMMENT: // 评论
                PostSubCommentListActivity.goActivity(context, contentId);
                break;
        }
    }

    @Override
    protected void onNotificationClickedWithNoAction(Context context, String title, String summary, String extraMap) {
        LogUtils.d(AliPushReceiver.class, "onNotificationClickedWithNoAction", "title: " + title + ", summary: " + summary + ", extraMap:" + extraMap);
    }

    @Override
    protected void onNotificationReceivedInApp(Context context, String title, String summary, Map<String, String> extraMap, int openType, String openActivity, String openUrl) {
        LogUtils.d(AliPushReceiver.class, "onNotificationReceivedInApp", "title: " + title + ", summary: " + summary + ", extraMap:" + extraMap + ", openType:" + openType + ", openActivity:" + openActivity + ", openUrl:" + openUrl);
    }

    @Override
    protected void onNotificationRemoved(Context context, String messageId) {
        LogUtils.d(AliPushReceiver.class, "onNotificationRemoved", "---");
    }

}
