package com.jiangzg.lovenote.domain;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by JZG on 2018/7/10.
 * SouvenirTravel
 */
public class SouvenirTravel extends BaseCP implements Parcelable {

    private long souvenirId;
    private long travelId;
    private int year;
    private Travel travel;

    public long getSouvenirId() {
        return souvenirId;
    }

    public void setSouvenirId(long souvenirId) {
        this.souvenirId = souvenirId;
    }

    public long getTravelId() {
        return travelId;
    }

    public void setTravelId(long travelId) {
        this.travelId = travelId;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public Travel getTravel() {
        return travel;
    }

    public void setTravel(Travel travel) {
        this.travel = travel;
    }

    public SouvenirTravel() {
    }

    protected SouvenirTravel(Parcel in) {
        super(in);
        souvenirId = in.readLong();
        travelId = in.readLong();
        year = in.readInt();
        travel = in.readParcelable(Travel.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeLong(souvenirId);
        dest.writeLong(travelId);
        dest.writeInt(year);
        dest.writeParcelable(travel, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<SouvenirTravel> CREATOR = new Creator<SouvenirTravel>() {
        @Override
        public SouvenirTravel createFromParcel(Parcel in) {
            return new SouvenirTravel(in);
        }

        @Override
        public SouvenirTravel[] newArray(int size) {
            return new SouvenirTravel[size];
        }
    };

}
