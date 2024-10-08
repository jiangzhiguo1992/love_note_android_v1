package com.jiangzg.lovenote.model.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by JZG on 2018/7/8.
 * Menses
 */
public class Menses extends BaseCP implements Parcelable {

    private boolean isMe;
    private int year;
    private int monthOfYear;
    private int dayOfMonth;
    private boolean isStart;

    public boolean isMe() {
        return isMe;
    }

    public void setMe(boolean me) {
        isMe = me;
    }

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
        isMe = in.readByte() != 0;
        year = in.readInt();
        monthOfYear = in.readInt();
        dayOfMonth = in.readInt();
        isStart = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeByte((byte) (isMe ? 1 : 0));
        dest.writeInt(year);
        dest.writeInt(monthOfYear);
        dest.writeInt(dayOfMonth);
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
