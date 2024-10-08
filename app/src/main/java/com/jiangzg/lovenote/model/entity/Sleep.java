package com.jiangzg.lovenote.model.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by JZG on 2018/7/6.
 * Sleep
 */
public class Sleep extends BaseCP implements Parcelable {

    private int year;
    private int monthOfYear;
    private int dayOfMonth;
    private boolean isSleep;

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

    public boolean isSleep() {
        return isSleep;
    }

    public void setSleep(boolean sleep) {
        isSleep = sleep;
    }

    public Sleep() {
    }

    protected Sleep(Parcel in) {
        super(in);
        year = in.readInt();
        monthOfYear = in.readInt();
        dayOfMonth = in.readInt();
        isSleep = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(year);
        dest.writeInt(monthOfYear);
        dest.writeInt(dayOfMonth);
        dest.writeByte((byte) (isSleep ? 1 : 0));
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
