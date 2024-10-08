package com.jiangzg.lovenote.model.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.jiangzg.lovenote.helper.common.SPHelper;

/**
 * Created by JZG on 2017/12/18.
 * BaseCP
 */
public class BaseCP extends BaseObj implements Parcelable {

    private long userId;
    private long coupleId;

    public boolean isMine() {
        User me = SPHelper.getMe();
        if (me == null) return false;
        return getUserId() == me.getId();
    }

    public boolean isMine(long mid) {
        return getUserId() == mid;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getCoupleId() {
        return coupleId;
    }

    public void setCoupleId(long coupleId) {
        this.coupleId = coupleId;
    }

    public BaseCP() {
    }

    protected BaseCP(Parcel in) {
        super(in);
        userId = in.readLong();
        coupleId = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeLong(userId);
        dest.writeLong(coupleId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<BaseCP> CREATOR = new Creator<BaseCP>() {
        @Override
        public BaseCP createFromParcel(Parcel in) {
            return new BaseCP(in);
        }

        @Override
        public BaseCP[] newArray(int size) {
            return new BaseCP[size];
        }
    };

}
