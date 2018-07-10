package com.jiangzg.mianmian.domain;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by JZG on 2018/7/10.
 * Souvenir
 */
public class Souvenir extends BaseCP implements Parcelable {

    private long happenAt;
    private String title;
    private boolean done;
    private double longitude;
    private double latitude;
    private String address;
    private String cityId;
    // 关联
    private List<SouvenirGift> souvenirGiftList;
    private List<SouvenirTravel> souvenirTravelList;
    private List<SouvenirAlbum> souvenirAlbumList;
    private List<SouvenirVideo> souvenirVideoList;
    private List<SouvenirFood> souvenirFoodList;
    private List<SouvenirDiary> souvenirDiaryList;
    // api-put关联
    private int year;

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public long getHappenAt() {
        return happenAt;
    }

    public void setHappenAt(long happenAt) {
        this.happenAt = happenAt;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
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

    public Souvenir() {
    }

    protected Souvenir(Parcel in) {
        super(in);
        happenAt = in.readLong();
        title = in.readString();
        done = in.readByte() != 0;
        longitude = in.readDouble();
        latitude = in.readDouble();
        address = in.readString();
        cityId = in.readString();
        souvenirGiftList = in.createTypedArrayList(SouvenirGift.CREATOR);
        souvenirTravelList = in.createTypedArrayList(SouvenirTravel.CREATOR);
        souvenirAlbumList = in.createTypedArrayList(SouvenirAlbum.CREATOR);
        souvenirVideoList = in.createTypedArrayList(SouvenirVideo.CREATOR);
        souvenirFoodList = in.createTypedArrayList(SouvenirFood.CREATOR);
        souvenirDiaryList = in.createTypedArrayList(SouvenirDiary.CREATOR);
        year = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeLong(happenAt);
        dest.writeString(title);
        dest.writeByte((byte) (done ? 1 : 0));
        dest.writeDouble(longitude);
        dest.writeDouble(latitude);
        dest.writeString(address);
        dest.writeString(cityId);
        dest.writeTypedList(souvenirGiftList);
        dest.writeTypedList(souvenirTravelList);
        dest.writeTypedList(souvenirAlbumList);
        dest.writeTypedList(souvenirVideoList);
        dest.writeTypedList(souvenirFoodList);
        dest.writeTypedList(souvenirDiaryList);
        dest.writeInt(year);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Souvenir> CREATOR = new Creator<Souvenir>() {
        @Override
        public Souvenir createFromParcel(Parcel in) {
            return new Souvenir(in);
        }

        @Override
        public Souvenir[] newArray(int size) {
            return new Souvenir[size];
        }
    };

}
