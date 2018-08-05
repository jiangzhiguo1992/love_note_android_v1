package com.jiangzg.lovenote.domain;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by JZG on 2018/7/7.
 * Shy
 */
public class Shy extends BaseCP implements Parcelable {

    private long happenAt;

    public long getHappenAt() {
        return happenAt;
    }

    public void setHappenAt(long happenAt) {
        this.happenAt = happenAt;
    }

    public Shy() {
    }

    protected Shy(Parcel in) {
        super(in);
        happenAt = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeLong(happenAt);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Shy> CREATOR = new Creator<Shy>() {
        @Override
        public Shy createFromParcel(Parcel in) {
            return new Shy(in);
        }

        @Override
        public Shy[] newArray(int size) {
            return new Shy[size];
        }
    };

}
