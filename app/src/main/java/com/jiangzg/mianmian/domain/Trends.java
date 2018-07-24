package com.jiangzg.mianmian.domain;

import android.os.Parcel;
import android.os.Parcelable;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.jiangzg.mianmian.helper.ApiHelper;

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
    public static final int TRENDS_CON_TYPE_PICTURE = 330; // 照片
    public static final int TRENDS_CON_TYPE_WORD = 400; // 留言
    public static final int TRENDS_CON_TYPE_WHISPER = 410; // 耳语
    public static final int TRENDS_CON_TYPE_DIARY = 420; // 日记
    public static final int TRENDS_CON_TYPE_AWARD = 430;// 奖励
    public static final int TRENDS_CON_TYPE_AWARD_RULE = 440;// 奖励规则
    public static final int TRENDS_CON_TYPE_DREAM = 450;// 梦境
    public static final int TRENDS_CON_TYPE_FOOD = 460; // 美食
    public static final int TRENDS_CON_TYPE_TRAVEL = 470; // 游记
    public static final int TRENDS_CON_TYPE_GIFT = 480; // 礼物
    public static final int TRENDS_CON_TYPE_PROMISE = 490; // 承诺
    public static final int TRENDS_CON_TYPE_ANGRY = 500;// 生气
    // 内容Id
    public static final int TRENDS_CON_ID_LIST = 0; // 列表信息

    private int actionType;
    private int contentType;
    private long contentId;

    @Override
    public int getItemType() {
        return isMine() ? ApiHelper.LIST_NOTE_MY : ApiHelper.LIST_NOTE_TA;
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
