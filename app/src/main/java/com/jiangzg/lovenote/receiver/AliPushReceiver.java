package com.jiangzg.lovenote.receiver;

import android.content.Context;

import com.alibaba.sdk.android.push.MessageReceiver;
import com.alibaba.sdk.android.push.notification.CPushMessage;
import com.jiangzg.base.common.LogUtils;

import java.util.Map;

/**
 * Created by JZG on 2018/10/19.
 * 阿里推送
 */
public class AliPushReceiver extends MessageReceiver {

    @Override
    public void onNotification(Context context, String title, String summary, Map<String, String> extraMap) {
        LogUtils.i(AliPushReceiver.class, "onNotification", "title: " + title + ", summary: " + summary + ", extraMap: " + extraMap);
        // TODO 处理推送通知
    }

    @Override
    public void onMessage(Context context, CPushMessage cPushMessage) {
        LogUtils.i(AliPushReceiver.class, "onMessage", "messageId: " + cPushMessage.getMessageId() + ", title: " + cPushMessage.getTitle() + ", content:" + cPushMessage.getContent());
    }

    @Override
    public void onNotificationOpened(Context context, String title, String summary, String extraMap) {
        LogUtils.i(AliPushReceiver.class, "onNotificationOpened", "title: " + title + ", summary: " + summary + ", extraMap:" + extraMap);
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
