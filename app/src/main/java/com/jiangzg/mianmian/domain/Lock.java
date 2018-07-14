package com.jiangzg.mianmian.domain;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by JZG on 2018/7/14.
 * Lock
 */
public class Lock extends BaseCP implements Parcelable {

    private String password;
    private boolean isLock;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isLock() {
        return isLock;
    }

    public void setLock(boolean lock) {
        isLock = lock;
    }

    public Lock() {
    }

    protected Lock(Parcel in) {
        super(in);
        password = in.readString();
        isLock = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(password);
        dest.writeByte((byte) (isLock ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Lock> CREATOR = new Creator<Lock>() {
        @Override
        public Lock createFromParcel(Parcel in) {
            return new Lock(in);
        }

        @Override
        public Lock[] newArray(int size) {
            return new Lock[size];
        }
    };
}
