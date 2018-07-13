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
    public static final int TRENDS_CON_TYPE_SOUVENIR = 10; // 纪念日
    public static final int TRENDS_CON_TYPE_WISH = 11; // 愿望清单
    public static final int TRENDS_CON_TYPE_MENSES = 20; // 姨妈
    public static final int TRENDS_CON_TYPE_SHY = 21; // 羞羞
    public static final int TRENDS_CON_TYPE_SLEEP = 22; // 睡眠
    public static final int TRENDS_CON_TYPE_WORD = 30; // 留言
    public static final int TRENDS_CON_TYPE_WHISPER = 31; // 耳语
    public static final int TRENDS_CON_TYPE_DIARY = 32; // 日记
    public static final int TRENDS_CON_TYPE_ALBUM = 33; // 相册
    public static final int TRENDS_CON_TYPE_PICTURE = 34; // 照片
    public static final int TRENDS_CON_TYPE_AUDIO = 40; // 音频
    public static final int TRENDS_CON_TYPE_VIDEO = 41; // 视频
    public static final int TRENDS_CON_TYPE_FOOD = 42; // 美食
    public static final int TRENDS_CON_TYPE_TRAVEL = 43; // 游记
    public static final int TRENDS_CON_TYPE_GIFT = 50; // 礼物
    public static final int TRENDS_CON_TYPE_PROMISE = 51; // 承诺
    public static final int TRENDS_CON_TYPE_ANGRY = 52;// 生气
    public static final int TRENDS_CON_TYPE_DREAM = 53;// 梦境
    public static final int TRENDS_CON_TYPE_AWARD = 60;// 奖励
    public static final int TRENDS_CON_TYPE_AWARD_RULE = 61;// 奖励规则
    // 内容Id
    public static final int TRENDS_CON_ID_LIST = 0; // 列表信息

    private int actionType;
    private int contentType;
    private long contentId;

    @Override
    public int getItemType() {
        return isMine() ? ApiHelper.LIST_MY : ApiHelper.LIST_TA;
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
