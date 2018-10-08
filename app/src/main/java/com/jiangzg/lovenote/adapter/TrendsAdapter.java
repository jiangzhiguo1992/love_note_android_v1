package com.jiangzg.lovenote.adapter;

import android.graphics.drawable.Drawable;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.activity.note.AlbumListActivity;
import com.jiangzg.lovenote.activity.note.AngryDetailActivity;
import com.jiangzg.lovenote.activity.note.AngryListActivity;
import com.jiangzg.lovenote.activity.note.AudioListActivity;
import com.jiangzg.lovenote.activity.note.AwardListActivity;
import com.jiangzg.lovenote.activity.note.AwardRuleListActivity;
import com.jiangzg.lovenote.activity.note.DiaryDetailActivity;
import com.jiangzg.lovenote.activity.note.DiaryListActivity;
import com.jiangzg.lovenote.activity.note.DreamDetailActivity;
import com.jiangzg.lovenote.activity.note.DreamListActivity;
import com.jiangzg.lovenote.activity.note.FoodListActivity;
import com.jiangzg.lovenote.activity.note.GiftListActivity;
import com.jiangzg.lovenote.activity.note.MensesActivity;
import com.jiangzg.lovenote.activity.note.PictureListActivity;
import com.jiangzg.lovenote.activity.note.PromiseDetailActivity;
import com.jiangzg.lovenote.activity.note.PromiseListActivity;
import com.jiangzg.lovenote.activity.note.ShyActivity;
import com.jiangzg.lovenote.activity.note.SleepActivity;
import com.jiangzg.lovenote.activity.note.SouvenirDetailDoneActivity;
import com.jiangzg.lovenote.activity.note.SouvenirDetailWishActivity;
import com.jiangzg.lovenote.activity.note.SouvenirListActivity;
import com.jiangzg.lovenote.activity.note.TravelDetailActivity;
import com.jiangzg.lovenote.activity.note.TravelListActivity;
import com.jiangzg.lovenote.activity.note.VideoListActivity;
import com.jiangzg.lovenote.activity.note.WhisperListActivity;
import com.jiangzg.lovenote.activity.note.WordListActivity;
import com.jiangzg.lovenote.base.BaseActivity;
import com.jiangzg.lovenote.domain.Couple;
import com.jiangzg.lovenote.domain.Trends;
import com.jiangzg.lovenote.helper.ApiHelper;
import com.jiangzg.lovenote.helper.SPHelper;
import com.jiangzg.lovenote.helper.TimeHelper;
import com.jiangzg.lovenote.helper.ViewHelper;
import com.jiangzg.lovenote.view.FrescoAvatarView;

/**
 * Created by JZG on 2018/3/12.
 * 动态
 */
public class TrendsAdapter extends BaseMultiItemQuickAdapter<Trends, BaseViewHolder> {

    private BaseActivity mActivity;
    private final Couple couple;
    private final Drawable dSouvenir;
    private final Drawable dMenses;
    private final Drawable dShy;
    private final Drawable dSleep;
    private final Drawable dWord;
    private final Drawable dWhisper;
    private final Drawable dDiary;
    private final Drawable dPhoto;
    private final Drawable dAudio;
    private final Drawable dVideo;
    private final Drawable dFood;
    private final Drawable dTravel;
    private final Drawable dGift;
    private final Drawable dPromise;
    private final Drawable dAngry;
    private final Drawable dDream;
    private final Drawable dAward;

    public TrendsAdapter(BaseActivity activity) {
        super(null);
        addItemType(ApiHelper.LIST_NOTE_MY, R.layout.list_item_trends_right);
        addItemType(ApiHelper.LIST_NOTE_TA, R.layout.list_item_trends_left);
        mActivity = activity;
        couple = SPHelper.getCouple();
        // conDrawable
        dSouvenir = ViewHelper.getDrawable(mActivity, R.drawable.ic_souvenir_pink_dark);
        ViewHelper.getDrawable(mActivity, R.drawable.ic_trends_blue_dark);
        dMenses = ViewHelper.getDrawable(mActivity, R.drawable.ic_menses_red_dark);
        dShy = ViewHelper.getDrawable(mActivity, R.drawable.ic_shy_yellow);
        dSleep = ViewHelper.getDrawable(mActivity, R.drawable.ic_sleep_teal_dark);
        dWord = ViewHelper.getDrawable(mActivity, R.drawable.ic_word_indigo_dark);
        dWhisper = ViewHelper.getDrawable(mActivity, R.drawable.ic_whisper_orange);
        dDiary = ViewHelper.getDrawable(mActivity, R.drawable.ic_diary_pink_accent);
        dPhoto = ViewHelper.getDrawable(mActivity, R.drawable.ic_photo_green);
        dAudio = ViewHelper.getDrawable(mActivity, R.drawable.ic_audio_teal_accent);
        dVideo = ViewHelper.getDrawable(mActivity, R.drawable.ic_video_purple_accent);
        dFood = ViewHelper.getDrawable(mActivity, R.drawable.ic_food_yellow_dark);
        dTravel = ViewHelper.getDrawable(mActivity, R.drawable.ic_travel_red);
        dGift = ViewHelper.getDrawable(mActivity, R.drawable.ic_gift_lime);
        dPromise = ViewHelper.getDrawable(mActivity, R.drawable.ic_promise_blue);
        dAngry = ViewHelper.getDrawable(mActivity, R.drawable.ic_angry_purple_dark);
        dDream = ViewHelper.getDrawable(mActivity, R.drawable.ic_dream_brown);
        dAward = ViewHelper.getDrawable(mActivity, R.drawable.ic_award_indigo_accent);
    }

    @Override
    protected void convert(BaseViewHolder helper, Trends item) {
        // data
        String createAt = TimeHelper.getTimeShowLine_HM_MDHM_YMDHM_ByGo(item.getCreateAt());
        String avatar = Couple.getAvatar(couple, item.getUserId());
        // view
        helper.setText(R.id.tvCreateAt, createAt);
        FrescoAvatarView ivAvatar = helper.getView(R.id.ivAvatar);
        ivAvatar.setData(avatar);
        TextView tvContent = helper.getView(R.id.tvContent);
        tvContent.setText(Trends.getActShow(item.getActionType(), item.getContentId()));
        Drawable dContent = getContentDrawable(item);
        tvContent.setCompoundDrawables(null, null, dContent, null);
        // listener
        helper.addOnClickListener(R.id.cvContent);
    }

    private Drawable getContentDrawable(Trends item) {
        if (item == null) return null;
        switch (item.getContentType()) {
            case Trends.TRENDS_CON_TYPE_SOUVENIR: // 纪念日
            case Trends.TRENDS_CON_TYPE_WISH: // 愿望清单
                return dSouvenir;
            case Trends.TRENDS_CON_TYPE_SHY: // 羞羞
                return dShy;
            case Trends.TRENDS_CON_TYPE_MENSES: // 姨妈
                return dMenses;
            case Trends.TRENDS_CON_TYPE_SLEEP: // 睡眠
                return dSleep;
            case Trends.TRENDS_CON_TYPE_AUDIO: // 音频
                return dAudio;
            case Trends.TRENDS_CON_TYPE_VIDEO: // 视频
                return dVideo;
            case Trends.TRENDS_CON_TYPE_ALBUM: // 相册
                return dPhoto;
            case Trends.TRENDS_CON_TYPE_WORD: // 留言
                return dWord;
            case Trends.TRENDS_CON_TYPE_WHISPER: // 耳语
                return dWhisper;
            case Trends.TRENDS_CON_TYPE_DIARY: // 日记
                return dDiary;
            case Trends.TRENDS_CON_TYPE_AWARD: // 奖励
            case Trends.TRENDS_CON_TYPE_AWARD_RULE: // 规则
                return dAward;
            case Trends.TRENDS_CON_TYPE_DREAM: // 梦境
                return dDream;
            case Trends.TRENDS_CON_TYPE_FOOD: // 美食
                return dFood;
            case Trends.TRENDS_CON_TYPE_TRAVEL: // 游记
                return dTravel;
            case Trends.TRENDS_CON_TYPE_GIFT: // 礼物
                return dGift;
            case Trends.TRENDS_CON_TYPE_PROMISE: // 承诺
                return dPromise;
            case Trends.TRENDS_CON_TYPE_ANGRY: // 生气
                return dAngry;
        }
        return null;
    }

    public void goSomeDetail(int position) {
        Trends item = getItem(position);
        int actionType = item.getActionType();
        int contentType = item.getContentType();
        long contentId = item.getContentId();
        if (actionType != Trends.TRENDS_ACT_TYPE_INSERT
                && actionType != Trends.TRENDS_ACT_TYPE_UPDATE
                && actionType != Trends.TRENDS_ACT_TYPE_QUERY) {
            // 删除不能跳转
            return;
        }
        switch (contentType) {
            case Trends.TRENDS_CON_TYPE_SOUVENIR: // 纪念日
                if (contentId <= Trends.TRENDS_CON_ID_LIST) {
                    SouvenirListActivity.goActivity(mActivity);
                } else {
                    SouvenirDetailDoneActivity.goActivity(mActivity, contentId);
                }
                break;
            case Trends.TRENDS_CON_TYPE_WISH: // 愿望清单
                if (contentId <= Trends.TRENDS_CON_ID_LIST) {
                    SouvenirListActivity.goActivity(mActivity);
                } else {
                    SouvenirDetailWishActivity.goActivity(mActivity, contentId);
                }
                break;
            case Trends.TRENDS_CON_TYPE_SHY: // 羞羞
                ShyActivity.goActivity(mActivity);
                break;
            case Trends.TRENDS_CON_TYPE_MENSES: // 姨妈
                MensesActivity.goActivity(mActivity);
                break;
            case Trends.TRENDS_CON_TYPE_SLEEP: // 睡眠
                SleepActivity.goActivity(mActivity);
                break;
            case Trends.TRENDS_CON_TYPE_AUDIO: // 音频
                AudioListActivity.goActivity(mActivity);
                break;
            case Trends.TRENDS_CON_TYPE_VIDEO: // 视频
                VideoListActivity.goActivity(mActivity);
                break;
            case Trends.TRENDS_CON_TYPE_ALBUM: // 相册
                if (contentId <= Trends.TRENDS_CON_ID_LIST) {
                    AlbumListActivity.goActivity(mActivity);
                } else {
                    PictureListActivity.goActivity(mActivity, contentId);
                }
                break;
            case Trends.TRENDS_CON_TYPE_WORD: // 留言
                WordListActivity.goActivity(mActivity);
                break;
            case Trends.TRENDS_CON_TYPE_WHISPER: // 耳语
                WhisperListActivity.goActivity(mActivity);
                break;
            case Trends.TRENDS_CON_TYPE_DIARY: // 日记
                if (contentId <= Trends.TRENDS_CON_ID_LIST) {
                    DiaryListActivity.goActivity(mActivity);
                } else {
                    DiaryDetailActivity.goActivity(mActivity, contentId);
                }
                break;
            case Trends.TRENDS_CON_TYPE_AWARD: // 奖励
                AwardListActivity.goActivity(mActivity);
                break;
            case Trends.TRENDS_CON_TYPE_AWARD_RULE: // 奖励规则
                AwardRuleListActivity.goActivity(mActivity);
                break;
            case Trends.TRENDS_CON_TYPE_DREAM: // 梦境
                if (contentId <= Trends.TRENDS_CON_ID_LIST) {
                    DreamListActivity.goActivity(mActivity);
                } else {
                    DreamDetailActivity.goActivity(mActivity, contentId);
                }
                break;
            case Trends.TRENDS_CON_TYPE_GIFT: // 礼物
                GiftListActivity.goActivity(mActivity);
                break;
            case Trends.TRENDS_CON_TYPE_FOOD: // 美食
                FoodListActivity.goActivity(mActivity);
                break;
            case Trends.TRENDS_CON_TYPE_TRAVEL: // 游记
                if (contentId <= Trends.TRENDS_CON_ID_LIST) {
                    TravelListActivity.goActivity(mActivity);
                } else {
                    TravelDetailActivity.goActivity(mActivity, contentId);
                }
                break;
            case Trends.TRENDS_CON_TYPE_PROMISE: // 承诺
                if (contentId <= Trends.TRENDS_CON_ID_LIST) {
                    PromiseListActivity.goActivity(mActivity);
                } else {
                    PromiseDetailActivity.goActivity(mActivity, contentId);
                }
                break;
            case Trends.TRENDS_CON_TYPE_ANGRY: // 生气
                if (contentId <= Trends.TRENDS_CON_ID_LIST) {
                    AngryListActivity.goActivity(mActivity);
                } else {
                    AngryDetailActivity.goActivity(mActivity, contentId);
                }
                break;
        }
    }

}
