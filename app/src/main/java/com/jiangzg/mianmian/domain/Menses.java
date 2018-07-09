package com.jiangzg.mianmian.domain;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by JZG on 2018/7/8.
 * Menses
 */
public class Menses extends BaseCP implements Parcelable {

    private boolean isStart;

    public boolean isStart() {
        return isStart;
    }

    public void setStart(boolean start) {
        isStart = start;
    }

    public Menses() {
    }

    protected Menses(Parcel in) {
        super(in);
        isStart = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeByte((byte) (isStart ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Menses> CREATOR = new Creator<Menses>() {
        @Override
        public Menses createFromParcel(Parcel in) {
            return new Menses(in);
        }

        @Override
        public Menses[] newArray(int size) {
            return new Menses[size];
        }
    };

}
