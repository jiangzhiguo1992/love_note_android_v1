package com.jiangzg.lovenote.model.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by JZG on 2018/6/27.
 * Promise
 */
public class Promise extends BaseCP implements Parcelable {

    private long happenId;
    private long happenAt;
    private String contentText;
    private int breakCount;

    public long getHappenId() {
        return happenId;
    }

    public void setHappenId(long happenId) {
        this.happenId = happenId;
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

    public int getBreakCount() {
        return breakCount;
    }

    public void setBreakCount(int breakCount) {
        this.breakCount = breakCount;
    }

    public Promise() {
    }

    protected Promise(Parcel in) {
        super(in);
        happenId = in.readLong();
        happenAt = in.readLong();
        contentText = in.readString();
        breakCount = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeLong(happenId);
        dest.writeLong(happenAt);
        dest.writeString(contentText);
        dest.writeInt(breakCount);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Promise> CREATOR = new Creator<Promise>() {
        @Override
        public Promise createFromParcel(Parcel in) {
            return new Promise(in);
        }

        @Override
        public Promise[] newArray(int size) {
            return new Promise[size];
        }
    };

}
