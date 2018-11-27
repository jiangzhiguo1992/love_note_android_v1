package com.jiangzg.lovenote.base;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by gg on 2017/2/27.
 * 基类
 */
public class BaseObj implements Parcelable {

    // status 一般用不到，都在后台处理
    public static final int STATUS_VISIBLE = 0;
    public static final int STATUS_DELETE = -1;

    protected long id;
    protected int status;
    protected long updateAt;
    protected long createAt;

    public boolean isDelete() {
        return status <= STATUS_DELETE;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getCreateAt() {
        return createAt;
    }

    public void setCreateAt(long createdAt) {
        this.createAt = createdAt;
    }

    public long getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(long updateAt) {
        this.updateAt = updateAt;
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
        updateAt = in.readLong();
        createAt = in.readLong();
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
        dest.writeLong(updateAt);
        dest.writeLong(createAt);
    }
}
