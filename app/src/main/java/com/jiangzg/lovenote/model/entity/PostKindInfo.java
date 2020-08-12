package com.jiangzg.lovenote.model.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by JZG on 2018/7/23.
 * PostKindInfo
 */
public class PostKindInfo implements Parcelable {

    private int kind;
    private boolean enable;
    private String name;
    private List<PostSubKindInfo> postSubKindInfoList;
    private TopicInfo topicInfo;

    public TopicInfo getTopicInfo() {
        return topicInfo;
    }

    public void setTopicInfo(TopicInfo topicInfo) {
        this.topicInfo = topicInfo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getKind() {
        return kind;
    }

    public void setKind(int kind) {
        this.kind = kind;
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
        kind = in.readInt();
        enable = in.readByte() != 0;
        name = in.readString();
        postSubKindInfoList = in.createTypedArrayList(PostSubKindInfo.CREATOR);
        topicInfo = in.readParcelable(TopicInfo.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(kind);
        dest.writeByte((byte) (enable ? 1 : 0));
        dest.writeString(name);
        dest.writeTypedList(postSubKindInfoList);
        dest.writeParcelable(topicInfo, flags);
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
