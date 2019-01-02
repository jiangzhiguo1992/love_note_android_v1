package com.jiangzg.lovenote.model.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by JZG on 2018/4/19.
 * Diary
 */
public class Diary extends BaseCP implements Parcelable {

    private long happenAt;
    private String contentText;
    private List<String> contentImageList;
    private int readCount;

    public int getReadCount() {
        return readCount;
    }

    public void setReadCount(int readCount) {
        this.readCount = readCount;
    }

    public long getHappenAt() {
        return happenAt;
    }

    public void setHappenAt(long happenAt) {
        this.happenAt = happenAt;
    }

    public String getContentText() {
        return contentText;
    }

    public void setContentText(String contentText) {
        this.contentText = contentText;
    }

    public List<String> getContentImageList() {
        return contentImageList;
    }

    public void setContentImageList(List<String> contentImageList) {
        this.contentImageList = contentImageList;
    }

    public Diary() {
    }

    protected Diary(Parcel in) {
        super(in);
        happenAt = in.readLong();
        contentText = in.readString();
        contentImageList = in.createStringArrayList();
        readCount = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeLong(happenAt);
        dest.writeString(contentText);
        dest.writeStringList(contentImageList);
        dest.writeInt(readCount);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Diary> CREATOR = new Creator<Diary>() {
        @Override
        public Diary createFromParcel(Parcel in) {
            return new Diary(in);
        }

        @Override
        public Diary[] newArray(int size) {
            return new Diary[size];
        }
    };

}
