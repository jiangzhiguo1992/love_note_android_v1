package com.jiangzg.lovenote.domain;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by JZG on 2018/7/23.
 * PostSubKindInfo
 */
public class PostSubKindInfo implements Parcelable {

    private int kind;
    private boolean enable;
    private String name;
    private boolean push;
    private boolean anonymous;

    public boolean isAnonymous() {
        return anonymous;
    }

    public void setAnonymous(boolean anonymous) {
        this.anonymous = anonymous;
    }

    public boolean isPush() {
        return push;
    }

    public void setPush(boolean push) {
        this.push = push;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PostSubKindInfo() {
    }

    protected PostSubKindInfo(Parcel in) {
        kind = in.readInt();
        enable = in.readByte() != 0;
        push = in.readByte() != 0;
        anonymous = in.readByte() != 0;
        name = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(kind);
        dest.writeByte((byte) (enable ? 1 : 0));
        dest.writeByte((byte) (push ? 1 : 0));
        dest.writeByte((byte) (anonymous ? 1 : 0));
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
