package com.jiangzg.lovenote.model.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.jiangzg.lovenote.base.BaseCP;

/**
 * Created by JZG on 2018/7/2.
 * TravelPlace
 */
public class TravelPlace extends BaseCP implements Parcelable {

    private long happenAt;
    private String contentText;
    private double longitude;
    private double latitude;
    private String address;
    private String cityId;

    public long getHappenAt() {
        return happenAt;
    }

    public void setHappenAt(long happenAt) {
        this.happenAt = happenAt;
    }

    public String getContentText() {
        return contentText;
    }

    public void setContentText(String contentText) {
        this.contentText = contentText;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    public TravelPlace() {
    }

    protected TravelPlace(Parcel in) {
        super(in);
        happenAt = in.readLong();
        contentText = in.readString();
        longitude = in.readDouble();
        latitude = in.readDouble();
        address = in.readString();
        cityId = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeLong(happenAt);
        dest.writeString(contentText);
        dest.writeDouble(longitude);
        dest.writeDouble(latitude);
        dest.writeString(address);
        dest.writeString(cityId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<TravelPlace> CREATOR = new Creator<TravelPlace>() {
        @Override
        public TravelPlace createFromParcel(Parcel in) {
            return new TravelPlace(in);
        }

        @Override
        public TravelPlace[] newArray(int size) {
            return new TravelPlace[size];
        }
    };

}
