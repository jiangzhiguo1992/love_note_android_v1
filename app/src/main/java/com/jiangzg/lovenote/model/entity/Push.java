package com.jiangzg.lovenote.model.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by JZG on 2018/10/21.
 * Push
 */
public class Push implements Parcelable {

    public static final int TYPE_APP = 0; // 打开app
    public static final int TYPE_SUGGEST = 50;// 打开suggest
    public static final int TYPE_COUPLE = 100;// 打开couple
    public static final int TYPE_NOTE = 200; // 打开note
    public static final int TYPE_NOTE_LOCK = 210;// 打开密码锁
    public static final int TYPE_NOTE_TRENDS = 211;// 打开动态
    public static final int TYPE_NOTE_TOTAL = 212;// 打开统计
    public static final int TYPE_NOTE_RECYCLE = 213;// 打开回收箱
    public static final int TYPE_NOTE_SHY = 220; // 打开羞羞
    public static final int TYPE_NOTE_MENSES = 221;// 打开姨妈
    public static final int TYPE_NOTE_SLEEP = 222;// 打开睡眠
    public static final int TYPE_NOTE_SOUVENIR = 230; // 打开纪念日
    public static final int TYPE_TOPIC = 300;// 打开topic
    public static final int TYPE_TOPIC_MINE = 310;// 打开我的
    public static final int TYPE_TOPIC_COLLECT = 320;// 打开收藏
    public static final int TYPE_TOPIC_MESSAGE = 330;// 打开消息
    public static final int TYPE_TOPIC_POST = 340;// 打开post
    public static final int TYPE_TOPIC_COMMENT = 350;// 打开postComment
    public static final int TYPE_MORE = 400;// 打开more

    private long createAt;
    private long userId;
    private String platform;
    private String title;
    private String contentText;
    private int contentType;
    private long contentId;

    public long getCreateAt() {
        return createAt;
    }

    public void setCreateAt(long createAt) {
        this.createAt = createAt;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContentText() {
        return contentText;
    }

    public void setContentText(String contentText) {
        this.contentText = contentText;
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

    protected Push(Parcel in) {
        createAt = in.readLong();
        userId = in.readLong();
        platform = in.readString();
        title = in.readString();
        contentText = in.readString();
        contentType = in.readInt();
        contentId = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(createAt);
        dest.writeLong(userId);
        dest.writeString(platform);
        dest.writeString(title);
        dest.writeString(contentText);
        dest.writeInt(contentType);
        dest.writeLong(contentId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Push> CREATOR = new Creator<Push>() {
        @Override
        public Push createFromParcel(Parcel in) {
            return new Push(in);
        }

        @Override
        public Push[] newArray(int size) {
            return new Push[size];
        }
    };
}
