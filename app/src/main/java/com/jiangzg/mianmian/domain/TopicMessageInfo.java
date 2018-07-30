package com.jiangzg.mianmian.domain;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by JZG on 2018/7/23.
 * TopicMessageInfo
 */
public class TopicMessageInfo implements Parcelable {

    private int id;
    private String name;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    protected TopicMessageInfo(Parcel in) {
        id = in.readInt();
        name = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<TopicMessageInfo> CREATOR = new Creator<TopicMessageInfo>() {
        @Override
        public TopicMessageInfo createFromParcel(Parcel in) {
            return new TopicMessageInfo(in);
        }

        @Override
        public TopicMessageInfo[] newArray(int size) {
            return new TopicMessageInfo[size];
        }
    };

}
