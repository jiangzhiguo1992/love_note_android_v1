package com.jiangzg.lovenote.model.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by JZG on 2018/6/26.
 * Gift
 */
public class Gift extends BaseCP implements Parcelable {

    private long receiveId;
    private long happenAt;
    private String title;
    private List<String> contentImageList;

    public long getReceiveId() {
        return receiveId;
    }

    public void setReceiveId(long receiveId) {
        this.receiveId = receiveId;
    }

    public long getHappenAt() {
        return happenAt;
    }

    public void setHappenAt(long happenAt) {
        this.happenAt = happenAt;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getContentImageList() {
        return contentImageList;
    }

    public void setContentImageList(List<String> contentImageList) {
        this.contentImageList = contentImageList;
    }

    public Gift() {
    }

    protected Gift(Parcel in) {
        super(in);
        receiveId = in.readLong();
        happenAt = in.readLong();
        title = in.readString();
        contentImageList = in.createStringArrayList();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeLong(receiveId);
        dest.writeLong(happenAt);
        dest.writeString(title);
        dest.writeStringList(contentImageList);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Gift> CREATOR = new Creator<Gift>() {
        @Override
        public Gift createFromParcel(Parcel in) {
            return new Gift(in);
        }

        @Override
        public Gift[] newArray(int size) {
            return new Gift[size];
        }
    };

}
