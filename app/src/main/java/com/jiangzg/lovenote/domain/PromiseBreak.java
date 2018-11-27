package com.jiangzg.lovenote.domain;

import android.os.Parcel;
import android.os.Parcelable;

import com.jiangzg.lovenote.base.BaseCP;

/**
 * Created by JZG on 2018/6/27.
 * Promise
 */
public class PromiseBreak extends BaseCP implements Parcelable {

    private long promiseId;
    private long happenAt;
    private String contentText;

    public long getPromiseId() {
        return promiseId;
    }

    public void setPromiseId(long promiseId) {
        this.promiseId = promiseId;
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

    public PromiseBreak() {
    }

    protected PromiseBreak(Parcel in) {
        super(in);
        promiseId = in.readLong();
        happenAt = in.readLong();
        contentText = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeLong(promiseId);
        dest.writeLong(happenAt);
        dest.writeString(contentText);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PromiseBreak> CREATOR = new Creator<PromiseBreak>() {
        @Override
        public PromiseBreak createFromParcel(Parcel in) {
            return new PromiseBreak(in);
        }

        @Override
        public PromiseBreak[] newArray(int size) {
            return new PromiseBreak[size];
        }
    };

}
