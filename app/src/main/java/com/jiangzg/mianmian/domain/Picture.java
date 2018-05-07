package com.jiangzg.mianmian.domain;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by JZG on 2018/5/7.
 * Picture
 */
public class Picture extends BaseCP implements Parcelable{

    private long albumId;
    private long happenAt;
    private String content;
    private double longitude;
    private double latitude;
    private String address;
    private int cityId;

    public long getAlbumId() {
        return albumId;
    }

    public void setAlbumId(long albumId) {
        this.albumId = albumId;
    }

    public long getHappenAt() {
        return happenAt;
    }

    public void setHappenAt(long happenAt) {
        this.happenAt = happenAt;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    public Picture() {
    }

    protected Picture(Parcel in) {
        super(in);
        albumId = in.readLong();
        happenAt = in.readLong();
        content = in.readString();
        longitude = in.readDouble();
        latitude = in.readDouble();
        address = in.readString();
        cityId = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeLong(albumId);
        dest.writeLong(happenAt);
        dest.writeString(content);
        dest.writeDouble(longitude);
        dest.writeDouble(latitude);
        dest.writeString(address);
        dest.writeInt(cityId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Picture> CREATOR = new Creator<Picture>() {
        @Override
        public Picture createFromParcel(Parcel in) {
            return new Picture(in);
        }

        @Override
        public Picture[] newArray(int size) {
            return new Picture[size];
        }
    };

}
