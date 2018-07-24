package com.jiangzg.mianmian.domain;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by JZG on 2018/7/23.
 * PostSubKindInfo
 */
public class PostSubKindInfo implements Parcelable {

    private int id;
    private boolean enable;
    private String name;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PostSubKindInfo() {
    }

    protected PostSubKindInfo(Parcel in) {
        id = in.readInt();
        enable = in.readByte() != 0;
        name = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeByte((byte) (enable ? 1 : 0));
        dest.writeString(name);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PostSubKindInfo> CREATOR = new Creator<PostSubKindInfo>() {
        @Override
        public PostSubKindInfo createFromParcel(Parcel in) {
            return new PostSubKindInfo(in);
        }

        @Override
        public PostSubKindInfo[] newArray(int size) {
            return new PostSubKindInfo[size];
        }
    };

}
