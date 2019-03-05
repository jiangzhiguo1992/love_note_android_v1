package com.jiangzg.lovenote.model.engine;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by JZG on 2018/12/15.
 */
public class PushExtra implements Parcelable {

    public static final int TYPE_APP = 0;
    public static final int TYPE_SUGGEST = 50;
    public static final int TYPE_COUPLE = 100;
    public static final int TYPE_COUPLE_INFO = 110;
    public static final int TYPE_COUPLE_WALL = 120;
    public static final int TYPE_COUPLE_PLACE = 130;
    public static final int TYPE_COUPLE_WEATHER = 140;
    public static final int TYPE_NOTE = 200;
    public static final int TYPE_NOTE_LOCK = 210;
    public static final int TYPE_NOTE_TRENDS = 211;
    public static final int TYPE_NOTE_TOTAL = 212;
    public static final int TYPE_NOTE_SHY = 220;
    public static final int TYPE_NOTE_MENSES = 221;
    public static final int TYPE_NOTE_SLEEP = 222;
    public static final int TYPE_NOTE_AUDIO = 230;
    public static final int TYPE_NOTE_VIDEO = 231;
    public static final int TYPE_NOTE_ALBUM = 232;
    public static final int TYPE_NOTE_PICTURE = 233;
    public static final int TYPE_NOTE_SOUVENIR = 240;
    public static final int TYPE_NOTE_WISH = 241;
    public static final int TYPE_NOTE_WORD = 250;
    public static final int TYPE_NOTE_DIARY = 251;
    public static final int TYPE_NOTE_AWARD = 252;
    public static final int TYPE_NOTE_AWARD_RULE = 253;
    public static final int TYPE_NOTE_DREAM = 260;
    public static final int TYPE_NOTE_ANGRY = 261;
    public static final int TYPE_NOTE_GIFT = 262;
    public static final int TYPE_NOTE_PROMISE = 263;
    public static final int TYPE_NOTE_PROMISE_BREAK = 264;
    public static final int TYPE_NOTE_TRAVEL = 270;
    public static final int TYPE_NOTE_MOVIE = 271;
    public static final int TYPE_NOTE_FOOD = 272;
    public static final int TYPE_TOPIC = 300;
    public static final int TYPE_TOPIC_MINE = 310;
    public static final int TYPE_TOPIC_COLLECT = 320;
    public static final int TYPE_TOPIC_MESSAGE = 330;
    public static final int TYPE_TOPIC_POST = 340;
    public static final int TYPE_TOPIC_COMMENT = 350;
    public static final int TYPE_MORE = 400;

    private long createAt;
    private long userId;
    private long toUserId;
    private String platform;
    private String title;
    private String contentText;
    private int contentType;
    private long contentId;

    public long getToUserId() {
        return toUserId;
    }

    public void setToUserId(long toUserId) {
        this.toUserId = toUserId;
    }

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

    protected PushExtra(Parcel in) {
        createAt = in.readLong();
        userId = in.readLong();
        toUserId = in.readLong();
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
        dest.writeLong(toUserId);
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

    public static final Creator<PushExtra> CREATOR = new Creator<PushExtra>() {
        @Override
        public PushExtra createFromParcel(Parcel in) {
            return new PushExtra(in);
        }

        @Override
        public PushExtra[] newArray(int size) {
            return new PushExtra[size];
        }
    };
}
