package com.jiangzg.lovenote.model.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by JZG on 2019/4/11.
 */
public class MensesDayInfo extends BaseCP implements Parcelable {

    private int blood;
    private int pain;
    private int mood;

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

    public MensesDayInfo() {
    }

    protected MensesDayInfo(Parcel in) {
        super(in);
        blood = in.readInt();
        pain = in.readInt();
        mood = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(blood);
        dest.writeInt(pain);
        dest.writeInt(mood);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MensesDayInfo> CREATOR = new Creator<MensesDayInfo>() {
        @Override
        public MensesDayInfo createFromParcel(Parcel in) {
            return new MensesDayInfo(in);
        }

        @Override
        public MensesDayInfo[] newArray(int size) {
            return new MensesDayInfo[size];
        }
    };

}
