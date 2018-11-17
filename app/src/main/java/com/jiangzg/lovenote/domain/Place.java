package com.jiangzg.lovenote.domain;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by JZG on 2018/4/17.
 * Place
 */
public class Place extends BaseObj implements Parcelable {

    private long userId;
    private double longitude;
    private double latitude;
    private String address;
    private String country;
    private String province;
    private String city;
    private String district;
    private String street;
    private String cityId;

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
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

    public Place() {
    }

    protected Place(Parcel in) {
        super(in);
        longitude = in.readDouble();
        latitude = in.readDouble();
        address = in.readString();
        country = in.readString();
        province = in.readString();
        city = in.readString();
        district = in.readString();
        street = in.readString();
        cityId = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeDouble(longitude);
        dest.writeDouble(latitude);
        dest.writeString(address);
        dest.writeString(country);
        dest.writeString(province);
        dest.writeString(city);
        dest.writeString(district);
        dest.writeString(street);
        dest.writeString(cityId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Place> CREATOR = new Creator<Place>() {
        @Override
        public Place createFromParcel(Parcel in) {
            return new Place(in);
        }

        @Override
        public Place[] newArray(int size) {
            return new Place[size];
        }
    };

}
