package com.jiangzg.lovenote.domain;

import android.os.Parcel;
import android.os.Parcelable;

import com.jiangzg.lovenote.base.BaseCP;

/**
 * Created by JZG on 2018/7/7.
 * Shy
 */
public class Shy extends BaseCP implements Parcelable {

    private int year;
    private int monthOfYear;
    private int dayOfMonth;
    private long happenAt;

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonthOfYear() {
        return monthOfYear;
    }

    public void setMonthOfYear(int monthOfYear) {
        this.monthOfYear = monthOfYear;
    }

    public int getDayOfMonth() {
        return dayOfMonth;
    }

    public void setDayOfMonth(int dayOfMonth) {
        this.dayOfMonth = dayOfMonth;
    }

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
        year = in.readInt();
        monthOfYear = in.readInt();
        dayOfMonth = in.readInt();
        happenAt = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(year);
        dest.writeInt(monthOfYear);
        dest.writeInt(dayOfMonth);
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
