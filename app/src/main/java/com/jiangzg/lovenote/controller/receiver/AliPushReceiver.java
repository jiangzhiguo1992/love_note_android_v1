package com.jiangzg.lovenote.controller.receiver;

import android.content.Context;

import com.alibaba.sdk.android.push.MessageReceiver;
import com.alibaba.sdk.android.push.notification.CPushMessage;
import com.jiangzg.base.common.LogUtils;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.lovenote.controller.activity.couple.CoupleInfoActivity;
import com.jiangzg.lovenote.controller.activity.couple.CouplePlaceActivity;
import com.jiangzg.lovenote.controller.activity.couple.CoupleWallPaperActivity;
import com.jiangzg.lovenote.controller.activity.couple.CoupleWeatherActivity;
import com.jiangzg.lovenote.controller.activity.note.AngryDetailActivity;
import com.jiangzg.lovenote.controller.activity.note.AudioListActivity;
import com.jiangzg.lovenote.controller.activity.note.AwardListActivity;
import com.jiangzg.lovenote.controller.activity.note.AwardRuleListActivity;
import com.jiangzg.lovenote.controller.activity.note.DiaryDetailActivity;
import com.jiangzg.lovenote.controller.activity.note.DreamDetailActivity;
import com.jiangzg.lovenote.controller.activity.note.FoodListActivity;
import com.jiangzg.lovenote.controller.activity.note.GiftListActivity;
import com.jiangzg.lovenote.controller.activity.note.LockActivity;
import com.jiangzg.lovenote.controller.activity.note.MensesActivity;
import com.jiangzg.lovenote.controller.activity.note.MovieListActivity;
import com.jiangzg.lovenote.controller.activity.note.NoteTotalActivity;
import com.jiangzg.lovenote.controller.activity.note.PictureListActivity;
import com.jiangzg.lovenote.controller.activity.note.PromiseDetailActivity;
import com.jiangzg.lovenote.controller.activity.note.ShyActivity;
import com.jiangzg.lovenote.controller.activity.note.SleepActivity;
import com.jiangzg.lovenote.controller.activity.note.SouvenirDetailDoneActivity;
import com.jiangzg.lovenote.controller.activity.note.SouvenirDetailWishActivity;
import com.jiangzg.lovenote.controller.activity.note.TravelDetailActivity;
import com.jiangzg.lovenote.controller.activity.note.TrendsListActivity;
import com.jiangzg.lovenote.controller.activity.note.VideoListActivity;
import com.jiangzg.lovenote.controller.activity.note.WordListActivity;
import com.jiangzg.lovenote.controller.activity.settings.SuggestDetailActivity;
import com.jiangzg.lovenote.controller.activity.topic.PostDetailActivity;
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
            case PushExtra.TYPE_COUPLE_INFO: // 信息
                CoupleInfoActivity.goActivity(context);
                break;
            case PushExtra.TYPE_COUPLE_WALL: // 墙纸
                CoupleWallPaperActivity.goActivity(context);
                break;
            case PushExtra.TYPE_COUPLE_PLACE: // 地址
                CouplePlaceActivity.goActivity(context);
                break;
            case PushExtra.TYPE_COUPLE_WEATHER: // 天气
                CoupleWeatherActivity.goActivity(context);
                break;
            case PushExtra.TYPE_NOTE_LOCK: // 锁
                LockActivity.goActivity(context);
                break;
            case PushExtra.TYPE_NOTE_TRENDS: // 动态
                TrendsListActivity.goActivity(context);
                break;
            case PushExtra.TYPE_NOTE_TOTAL: // 统计
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
            case PushExtra.TYPE_NOTE_AUDIO: // 音频
                AudioListActivity.goActivity(context);
                break;
            case PushExtra.TYPE_NOTE_VIDEO: // 视频
                VideoListActivity.goActivity(context);
                break;
            case PushExtra.TYPE_NOTE_ALBUM: // 相册
            case PushExtra.TYPE_NOTE_PICTURE: // 照片
                PictureListActivity.goActivity(context, contentId);
                break;
            case PushExtra.TYPE_NOTE_SOUVENIR: // 纪念日
                SouvenirDetailDoneActivity.goActivity(context, contentId);
                break;
            case PushExtra.TYPE_NOTE_WISH: // 愿望清单
                SouvenirDetailWishActivity.goActivity(context, contentId);
                break;
            case PushExtra.TYPE_NOTE_WORD: // 留言
                WordListActivity.goActivity(context);
                break;
            case PushExtra.TYPE_NOTE_AWARD: // 奖励
                AwardListActivity.goActivity(context);
                break;
            case PushExtra.TYPE_NOTE_AWARD_RULE: // 约定
                AwardRuleListActivity.goActivity(context);
                break;
            case PushExtra.TYPE_NOTE_DIARY: // 日记
                DiaryDetailActivity.goActivity(context, contentId);
                break;
            case PushExtra.TYPE_NOTE_DREAM: // 梦境
                DreamDetailActivity.goActivity(context, contentId);
                break;
            case PushExtra.TYPE_NOTE_ANGRY: // 生气
                AngryDetailActivity.goActivity(context, contentId);
                break;
            case PushExtra.TYPE_NOTE_GIFT: // 礼物
                GiftListActivity.goActivity(context);
                break;
            case PushExtra.TYPE_NOTE_PROMISE: // 承诺
            case PushExtra.TYPE_NOTE_PROMISE_BREAK: // 承诺违背
                PromiseDetailActivity.goActivity(context, contentId);
                break;
            case PushExtra.TYPE_NOTE_TRAVEL: // 游记
                TravelDetailActivity.goActivity(context, contentId);
                break;
            case PushExtra.TYPE_NOTE_MOVIE: // 电影
                MovieListActivity.goActivity(context);
                break;
            case PushExtra.TYPE_NOTE_FOOD: // 美食
                FoodListActivity.goActivity(context);
                break;
            case PushExtra.TYPE_TOPIC_MINE: // 话题我的
                PostMyRelationActivity.goActivity(context);
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
