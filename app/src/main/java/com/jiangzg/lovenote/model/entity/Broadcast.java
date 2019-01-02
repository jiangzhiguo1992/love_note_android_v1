package com.jiangzg.lovenote.model.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by JZG on 2018/8/7.
 * Broadcast
 */
public class Broadcast extends BaseObj implements Parcelable {

    public static final int TYPE_TEXT = 0; // 文章
    public static final int TYPE_URL = 1; // 网址
    public static final int TYPE_IMAGE = 2; // 图片

    private String title;
    private String cover;
    private long startAt;
    private long endAt;
    private int contentType;
    private String contentText;
    private boolean isEnd;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public long getStartAt() {
        return startAt;
    }

    public void setStartAt(long startAt) {
        this.startAt = startAt;
    }

    public long getEndAt() {
        return endAt;
    }

    public void setEndAt(long endAt) {
        this.endAt = endAt;
    }

    public int getContentType() {
        return contentType;
    }

    public void setContentType(int contentType) {
        this.contentType = contentType;
    }

    public String getContentText() {
        return contentText;
    }

    public void setContentText(String contentText) {
        this.contentText = contentText;
    }

    public boolean isEnd() {
        return isEnd;
    }

    public void setEnd(boolean end) {
        isEnd = end;
    }

    public Broadcast() {
    }

    protected Broadcast(Parcel in) {
        super(in);
        title = in.readString();
        cover = in.readString();
        startAt = in.readLong();
        endAt = in.readLong();
        contentType = in.readInt();
        contentText = in.readString();
        isEnd = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(title);
        dest.writeString(cover);
        dest.writeLong(startAt);
        dest.writeLong(endAt);
        dest.writeInt(contentType);
        dest.writeString(contentText);
        dest.writeByte((byte) (isEnd ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Broadcast> CREATOR = new Creator<Broadcast>() {
        @Override
        public Broadcast createFromParcel(Parcel in) {
            return new Broadcast(in);
        }

        @Override
        public Broadcast[] newArray(int size) {
            return new Broadcast[size];
        }
    };
}
