package com.jiangzg.lovenote_admin.domain;

import android.os.Parcel;
import android.os.Parcelable;

import com.jiangzg.lovenote_admin.R;
import com.jiangzg.lovenote_admin.base.MyApp;

/**
 * Created by JZG on 2018/7/13.
 * Trends
 */
public class Trends extends BaseCP implements Parcelable {

    // 操作类型
    public static final int TRENDS_ACT_TYPE_INSERT = 1; // 添加
    public static final int TRENDS_ACT_TYPE_DELETE = 2; // 删除
    public static final int TRENDS_ACT_TYPE_UPDATE = 3; // 修改
    public static final int TRENDS_ACT_TYPE_QUERY = 4; // 查看
    // 内容类型
    public static final int TRENDS_CON_TYPE_SOUVENIR = 100; // 纪念日
    public static final int TRENDS_CON_TYPE_WISH = 110; // 愿望清单
    public static final int TRENDS_CON_TYPE_SHY = 200; // 羞羞
    public static final int TRENDS_CON_TYPE_MENSES = 210; // 姨妈
    public static final int TRENDS_CON_TYPE_SLEEP = 220; // 睡眠
    public static final int TRENDS_CON_TYPE_AUDIO = 300; // 音频
    public static final int TRENDS_CON_TYPE_VIDEO = 310; // 视频
    public static final int TRENDS_CON_TYPE_ALBUM = 320; // 相册
    public static final int TRENDS_CON_TYPE_PICTURE = 330; // 照片
    public static final int TRENDS_CON_TYPE_WORD = 500; // 留言
    public static final int TRENDS_CON_TYPE_WHISPER = 510; // 耳语
    public static final int TRENDS_CON_TYPE_DIARY = 520; // 日记
    public static final int TRENDS_CON_TYPE_AWARD = 530;// 奖励
    public static final int TRENDS_CON_TYPE_AWARD_RULE = 540;// 奖励规则
    public static final int TRENDS_CON_TYPE_DREAM = 550;// 梦境
    public static final int TRENDS_CON_TYPE_FOOD = 560; // 美食
    public static final int TRENDS_CON_TYPE_TRAVEL = 570; // 游记
    public static final int TRENDS_CON_TYPE_GIFT = 580; // 礼物
    public static final int TRENDS_CON_TYPE_PROMISE = 590; // 承诺
    public static final int TRENDS_CON_TYPE_ANGRY = 600;// 生气
    // 内容Id
    public static final int TRENDS_CON_ID_LIST = 0; // 列表信息

    private int actionType;
    private int contentType;
    private long contentId;

    public static String getActShow(int act, long conId) {
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

    public static String getContentShow(int conType) {
        switch (conType) {
            case Trends.TRENDS_CON_TYPE_SOUVENIR: // 纪念日
                return MyApp.get().getString(R.string.souvenir);
            case Trends.TRENDS_CON_TYPE_WISH: // 愿望清单
                return MyApp.get().getString(R.string.wish_list);
            case Trends.TRENDS_CON_TYPE_SHY: // 羞羞
                return MyApp.get().getString(R.string.shy);
            case Trends.TRENDS_CON_TYPE_MENSES: // 姨妈
                return MyApp.get().getString(R.string.menses);
            case Trends.TRENDS_CON_TYPE_SLEEP: // 睡眠
                return MyApp.get().getString(R.string.sleep);
            case Trends.TRENDS_CON_TYPE_AUDIO: // 音频
                return MyApp.get().getString(R.string.audio);
            case Trends.TRENDS_CON_TYPE_VIDEO: // 视频
                return MyApp.get().getString(R.string.video);
            case Trends.TRENDS_CON_TYPE_ALBUM: // 相册
                return MyApp.get().getString(R.string.album);
            case Trends.TRENDS_CON_TYPE_PICTURE: // 照片
                return MyApp.get().getString(R.string.picture);
            case Trends.TRENDS_CON_TYPE_WORD: // 留言
                return MyApp.get().getString(R.string.word);
            case Trends.TRENDS_CON_TYPE_WHISPER: // 耳语
                return MyApp.get().getString(R.string.whisper);
            case Trends.TRENDS_CON_TYPE_DIARY: // 日记
                return MyApp.get().getString(R.string.diary);
            case Trends.TRENDS_CON_TYPE_AWARD: // 奖励
                return MyApp.get().getString(R.string.award);
            case Trends.TRENDS_CON_TYPE_AWARD_RULE: // 规则
                return MyApp.get().getString(R.string.award_rule);
            case Trends.TRENDS_CON_TYPE_DREAM: // 梦境
                return MyApp.get().getString(R.string.dream);
            case Trends.TRENDS_CON_TYPE_FOOD: // 美食
                return MyApp.get().getString(R.string.food);
            case Trends.TRENDS_CON_TYPE_TRAVEL: // 游记
                return MyApp.get().getString(R.string.travel);
            case Trends.TRENDS_CON_TYPE_GIFT: // 礼物
                return MyApp.get().getString(R.string.gift);
            case Trends.TRENDS_CON_TYPE_PROMISE: // 承诺
                return MyApp.get().getString(R.string.promise);
            case Trends.TRENDS_CON_TYPE_ANGRY: // 生气
                return MyApp.get().getString(R.string.angry);
        }
        return MyApp.get().getString(R.string.un_know);
    }

    public int getActionType() {
        return actionType;
    }

    public void setActionType(int actionType) {
        this.actionType = actionType;
    }

    public int getContentType() {
        return contentType;
    }

    public void setContentType(int contentType) {
        this.contentType = contentType;
    }

    public long getContentId() {
        return contentId;
    }

    public void setContentId(long contentId) {
        this.contentId = contentId;
    }

    public Trends() {
    }

    protected Trends(Parcel in) {
        super(in);
        actionType = in.readInt();
        contentType = in.readInt();
        contentId = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(actionType);
        dest.writeInt(contentType);
        dest.writeLong(contentId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Trends> CREATOR = new Creator<Trends>() {
        @Override
        public Trends createFromParcel(Parcel in) {
            return new Trends(in);
        }

        @Override
        public Trends[] newArray(int size) {
            return new Trends[size];
        }
    };

}
