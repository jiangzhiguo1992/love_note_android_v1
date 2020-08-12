package com.jiangzg.lovenote.model.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by JZG on 2018/5/5.
 * Notice
 */
public class Notice extends BaseObj implements Parcelable {

    public static final int TYPE_TEXT = 0; // 文章
    public static final int TYPE_URL = 1; // 网址
    public static final int TYPE_IMAGE = 2; // 图片

    private String title;
    private int contentType;
    private String contentText;
    private boolean read;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContentText() {
        return contentText;
    }

    public int getContentType() {
        return contentType;
    }

    public void setContentType(int contentType) {
        this.contentType = contentType;
    }

    public void setContentText(String contentText) {
        this.contentText = contentText;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public Notice() {
    }

    protected Notice(Parcel in) {
        super(in);
        title = in.readString();
        contentType = in.readInt();
        contentText = in.readString();
        read = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(title);
        dest.writeInt(contentType);
        dest.writeString(contentText);
        dest.writeByte((byte) (read ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Notice> CREATOR = new Creator<Notice>() {
        @Override
        public Notice createFromParcel(Parcel in) {
            return new Notice(in);
        }

        @Override
        public Notice[] newArray(int size) {
            return new Notice[size];
        }
    };

}
