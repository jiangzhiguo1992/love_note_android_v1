package com.jiangzg.lovenote.model.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by JZG on 2018/8/7.
 * Sign
 */
public class Sign extends BaseCP implements Parcelable {

    private int year;
    private int monthOfYear;
    private int dayOfMonth;
    private int continueDay;

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

    public int getContinueDay() {
        return continueDay;
    }

    public void setContinueDay(int continueDay) {
        this.continueDay = continueDay;
    }

    public Sign() {
    }

    protected Sign(Parcel in) {
        super(in);
        year = in.readInt();
        monthOfYear = in.readInt();
        dayOfMonth = in.readInt();
        continueDay = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(year);
        dest.writeInt(monthOfYear);
        dest.writeInt(dayOfMonth);
        dest.writeInt(continueDay);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Sign> CREATOR = new Creator<Sign>() {
        @Override
        public Sign createFromParcel(Parcel in) {
            return new Sign(in);
        }

        @Override
        public Sign[] newArray(int size) {
            return new Sign[size];
        }
    };
}
