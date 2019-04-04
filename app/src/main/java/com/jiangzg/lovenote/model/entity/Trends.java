package com.jiangzg.lovenote.model.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.jiangzg.lovenote.helper.common.ApiHelper;

/**
 * Created by JZG on 2018/7/13.
 * Trends
 */
public class Trends extends BaseCP implements Parcelable, MultiItemEntity {

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
    public static final int TRENDS_CON_TYPE_WORD = 500; // 留言
    public static final int TRENDS_CON_TYPE_WHISPER = 510; // 耳语
    public static final int TRENDS_CON_TYPE_DIARY = 520; // 日记
    public static final int TRENDS_CON_TYPE_AWARD = 530;// 奖励
    public static final int TRENDS_CON_TYPE_AWARD_RULE = 540;// 约定
    public static final int TRENDS_CON_TYPE_DREAM = 550;// 梦境
    public static final int TRENDS_CON_TYPE_FOOD = 560; // 美食
    public static final int TRENDS_CON_TYPE_TRAVEL = 570; // 游记
    public static final int TRENDS_CON_TYPE_GIFT = 580; // 礼物
    public static final int TRENDS_CON_TYPE_PROMISE = 590; // 承诺
    public static final int TRENDS_CON_TYPE_ANGRY = 600;// 生气
    public static final int TRENDS_CON_TYPE_MOVIE = 610;// 电影
    // 内容Id
    public static final int TRENDS_CON_ID_LIST = 0; // 列表信息

    private int actionType;
    private int contentType;
    private long contentId;

    @Override
    public int getItemType() {
        return isMine() ? ApiHelper.LIST_NOTE_WHO_MY : ApiHelper.LIST_NOTE_WHO_TA;
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
