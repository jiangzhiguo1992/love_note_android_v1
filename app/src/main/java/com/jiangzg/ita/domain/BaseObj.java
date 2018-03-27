package com.jiangzg.ita.domain;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by gg on 2017/2/27.
 * 基类
 */
public class BaseObj implements Parcelable {

    public static final int STATUS_NOL = 0;
    public static final int STATUS_DEL = -1;

    protected long id;
    protected int status;
    protected long updatedAt;
    protected long createdAt;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public BaseObj() {
    }

    protected BaseObj(Parcel in) {
        id = in.readLong();
        status = in.readInt();
        updatedAt = in.readLong();
        createdAt = in.readLong();
    }

    public static final Creator<BaseObj> CREATOR = new Creator<BaseObj>() {
        @Override
        public BaseObj createFromParcel(Parcel in) {
            return new BaseObj(in);
        }

        @Override
        public BaseObj[] newArray(int size) {
            return new BaseObj[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeInt(status);
        dest.writeLong(updatedAt);
        dest.writeLong(createdAt);
    }
}
