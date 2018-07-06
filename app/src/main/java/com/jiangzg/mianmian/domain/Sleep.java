package com.jiangzg.mianmian.domain;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by JZG on 2018/7/6.
 * Sleep
 */
public class Sleep extends BaseCP implements Parcelable {

    private long isSleep;

    public long getIsSleep() {
        return isSleep;
    }

    public void setIsSleep(long isSleep) {
        this.isSleep = isSleep;
    }

    public Sleep() {
    }

    protected Sleep(Parcel in) {
        super(in);
        isSleep = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeLong(isSleep);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Sleep> CREATOR = new Creator<Sleep>() {
        @Override
        public Sleep createFromParcel(Parcel in) {
            return new Sleep(in);
        }

        @Override
        public Sleep[] newArray(int size) {
            return new Sleep[size];
        }
    };

}
