package com.jiangzg.lovenote.model.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by JZG on 2019/4/11.
 */
public class MensesDay extends BaseCP implements Parcelable {

    private long menses2Id;
    private int year;
    private int monthOfYear;
    private int dayOfMonth;
    private int blood;
    private int pain;
    private int mood;

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

    public long getMenses2Id() {
        return menses2Id;
    }

    public void setMenses2Id(long menses2Id) {
        this.menses2Id = menses2Id;
    }

    public int getMood() {
        return mood;
    }

    public void setMood(int mood) {
        this.mood = mood;
    }

    public int getPain() {
        return pain;
    }

    public void setPain(int pain) {
        this.pain = pain;
    }

    public int getBlood() {
        return blood;
    }

    public void setBlood(int blood) {
        this.blood = blood;
    }

    public MensesDay() {
    }

    protected MensesDay(Parcel in) {
        super(in);
        menses2Id = in.readLong();
        year = in.readInt();
        monthOfYear = in.readInt();
        dayOfMonth = in.readInt();
        blood = in.readInt();
        pain = in.readInt();
        mood = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeLong(menses2Id);
        dest.writeInt(year);
        dest.writeInt(monthOfYear);
        dest.writeInt(dayOfMonth);
        dest.writeInt(blood);
        dest.writeInt(pain);
        dest.writeInt(mood);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MensesDay> CREATOR = new Creator<MensesDay>() {
        @Override
        public MensesDay createFromParcel(Parcel in) {
            return new MensesDay(in);
        }

        @Override
        public MensesDay[] newArray(int size) {
            return new MensesDay[size];
        }
    };

}
