package com.jiangzg.mianmian.domain;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by JZG on 2018/7/23.
 * PostKindInfo
 */
public class PostKindInfo implements Parcelable {

    private int id;
    private boolean enable;
    private boolean lonLat;
    private String name;
    private List<PostSubKindInfo> postSubKindInfoList;

    public boolean isLonLat() {
        return lonLat;
    }

    public void setLonLat(boolean lonLat) {
        this.lonLat = lonLat;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

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

    public List<PostSubKindInfo> getPostSubKindInfoList() {
        return postSubKindInfoList;
    }

    public void setPostSubKindInfoList(List<PostSubKindInfo> postSubKindInfoList) {
        this.postSubKindInfoList = postSubKindInfoList;
    }

    public PostKindInfo() {
    }

    protected PostKindInfo(Parcel in) {
        id = in.readInt();
        enable = in.readByte() != 0;
        lonLat = in.readByte() != 0;
        name = in.readString();
        postSubKindInfoList = in.createTypedArrayList(PostSubKindInfo.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeByte((byte) (enable ? 1 : 0));
        dest.writeByte((byte) (lonLat ? 1 : 0));
        dest.writeString(name);
        dest.writeTypedList(postSubKindInfoList);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PostKindInfo> CREATOR = new Creator<PostKindInfo>() {
        @Override
        public PostKindInfo createFromParcel(Parcel in) {
            return new PostKindInfo(in);
        }

        @Override
        public PostKindInfo[] newArray(int size) {
            return new PostKindInfo[size];
        }
    };

}
