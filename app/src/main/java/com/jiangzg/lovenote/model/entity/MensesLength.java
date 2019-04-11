package com.jiangzg.lovenote.model.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by JZG on 2019/4/11.
 */
public class MensesLength extends BaseCP implements Parcelable {

    private int cycleDay;
    private int durationDay;

    public int getCycleDay() {
        return cycleDay;
    }

    public void setCycleDay(int cycleDay) {
        this.cycleDay = cycleDay;
    }

    public int getDurationDay() {
        return durationDay;
    }

    public void setDurationDay(int durationDay) {
        this.durationDay = durationDay;
    }

    public MensesLength() {
    }

    protected MensesLength(Parcel in) {
        super(in);
        cycleDay = in.readInt();
        durationDay = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(cycleDay);
        dest.writeInt(durationDay);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MensesLength> CREATOR = new Creator<MensesLength>() {
        @Override
        public MensesLength createFromParcel(Parcel in) {
            return new MensesLength(in);
        }

        @Override
        public MensesLength[] newArray(int size) {
            return new MensesLength[size];
        }
    };

}
