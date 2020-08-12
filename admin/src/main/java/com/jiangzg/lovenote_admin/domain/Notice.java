package com.jiangzg.lovenote_admin.domain;

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

    public static String getTypeShow(int type) {
        switch (type) {
            case TYPE_TEXT:
                return "文章";
            case TYPE_URL:
                return "网址";
            case TYPE_IMAGE:
                return "图片";
        }
        return String.valueOf(type);
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

    public int getContentType() {
        return contentType;
    }

    public void setContentType(int contentType) {
        this.contentType = contentType;
    }

    public void setContentText(String contentText) {
        this.contentText = contentText;
    }

    public Notice() {
    }

    protected Notice(Parcel in) {
        super(in);
        title = in.readString();
        contentType = in.readInt();
        contentText = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(title);
        dest.writeInt(contentType);
        dest.writeString(contentText);
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
