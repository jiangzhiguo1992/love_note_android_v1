package com.jiangzg.lovenote.adapter;

import android.graphics.drawable.Drawable;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jiangzg.base.view.ViewUtils;
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
import com.jiangzg.lovenote.activity.note.MovieListActivity;
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
import com.jiangzg.lovenote.helper.ApiHelper;
import com.jiangzg.lovenote.helper.SPHelper;
import com.jiangzg.lovenote.helper.TimeHelper;
import com.jiangzg.lovenote.helper.UserHelper;
import com.jiangzg.lovenote.main.MyApp;
import com.jiangzg.lovenote.model.entity.Couple;
import com.jiangzg.lovenote.model.entity.Trends;
import com.jiangzg.lovenote.view.FrescoAvatarView;

/**
 * Created by JZG on 2018/3/12.
 * 动态
 */
public class TrendsAdapter extends BaseMultiItemQuickAdapter<Trends, BaseViewHolder> {

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
    private final Drawable dMovie;
    private BaseActivity mActivity;

    public TrendsAdapter(BaseActivity activity) {
        super(null);
        addItemType(ApiHelper.LIST_NOTE_MY, R.layout.list_item_trends_right);
        addItemType(ApiHelper.LIST_NOTE_TA, R.layout.list_item_trends_left);
        mActivity = activity;
        couple = SPHelper.getCouple();
        // conDrawable
        dSouvenir = ViewUtils.getDrawable(mActivity, R.mipmap.ic_note_souvenir_24dp);
        dMenses = ViewUtils.getDrawable(mActivity, R.mipmap.ic_note_menses_24dp);
        dShy = ViewUtils.getDrawable(mActivity, R.mipmap.ic_note_shy_24dp);
        dSleep = ViewUtils.getDrawable(mActivity, R.mipmap.ic_note_sleep_24dp);
        dWord = ViewUtils.getDrawable(mActivity, R.mipmap.ic_note_word_24dp);
        dWhisper = ViewUtils.getDrawable(mActivity, R.mipmap.ic_note_whisper_24dp);
        dDiary = ViewUtils.getDrawable(mActivity, R.mipmap.ic_note_diary_24dp);
        dPhoto = ViewUtils.getDrawable(mActivity, R.mipmap.ic_note_album_24dp);
        dAudio = ViewUtils.getDrawable(mActivity, R.mipmap.ic_note_audio_24dp);
        dVideo = ViewUtils.getDrawable(mActivity, R.mipmap.ic_note_video_24dp);
        dFood = ViewUtils.getDrawable(mActivity, R.mipmap.ic_note_food_24dp);
        dTravel = ViewUtils.getDrawable(mActivity, R.mipmap.ic_note_travel_24dp);
        dGift = ViewUtils.getDrawable(mActivity, R.mipmap.ic_note_gift_24dp);
        dPromise = ViewUtils.getDrawable(mActivity, R.mipmap.ic_note_promise_24dp);
        dAngry = ViewUtils.getDrawable(mActivity, R.mipmap.ic_note_angry_24dp);
        dDream = ViewUtils.getDrawable(mActivity, R.mipmap.ic_note_dream_24dp);
        dAward = ViewUtils.getDrawable(mActivity, R.mipmap.ic_note_award_24dp);
        dMovie = ViewUtils.getDrawable(mActivity, R.mipmap.ic_note_movie_24dp);
    }

    @Override
    protected void convert(BaseViewHolder helper, Trends item) {
        // data
        String createAt = TimeHelper.getTimeShowLine_HM_MDHM_YMDHM_ByGo(item.getCreateAt());
        String avatar = UserHelper.getAvatar(couple, item.getUserId());
        // view
        helper.setText(R.id.tvCreateAt, createAt);
        FrescoAvatarView ivAvatar = helper.getView(R.id.ivAvatar);
        ivAvatar.setData(avatar);
        TextView tvContent = helper.getView(R.id.tvContent);
        tvContent.setText(getTrendsActShow(item.getActionType(), item.getContentId()));
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
            case Trends.TRENDS_CON_TYPE_MOVIE: // 电影
                return dMovie;
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
            case Trends.TRENDS_CON_TYPE_MOVIE: // 电影
                MovieListActivity.goActivity(mActivity);
                break;
        }
    }

    private String getTrendsActShow(int act, long conId) {
        switch (act) {
            case Trends.TRENDS_ACT_TYPE_INSERT: // 添加
                return MyApp.get().getString(R.string.add);
            case Trends.TRENDS_ACT_TYPE_DELETE: // 删除
                return MyApp.get().getString(R.string.delete);
            case Trends.TRENDS_ACT_TYPE_UPDATE: // 修改
                return MyApp.get().getString(R.string.modify);
            case Trends.TRENDS_ACT_TYPE_QUERY: // 进入/浏览
                if (conId <= Trends.TRENDS_CON_ID_LIST) {
                    return MyApp.get().getString(R.string.go_in);
                } else {
                    return MyApp.get().getString(R.string.browse);
                }
        }
        return MyApp.get().getString(R.string.un_know);
    }

}
