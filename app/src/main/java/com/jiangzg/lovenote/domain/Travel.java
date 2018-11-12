package com.jiangzg.lovenote.domain;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by JZG on 2018/7/2.
 * Travel
 */
public class Travel extends BaseCP implements Parcelable {

    private long happenAt;
    private String title;
    // 实体
    private List<TravelPlace> travelPlaceList;
    private List<TravelAlbum> travelAlbumList;
    private List<TravelVideo> travelVideoList;
    private List<TravelFood> travelFoodList;
    private List<TravelMovie> travelMovieList;
    private List<TravelDiary> travelDiaryList;

    public List<TravelMovie> getTravelMovieList() {
        return travelMovieList;
    }

    public void setTravelMovieList(List<TravelMovie> travelMovieList) {
        this.travelMovieList = travelMovieList;
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

    public List<TravelPlace> getTravelPlaceList() {
        return travelPlaceList;
    }

    public void setTravelPlaceList(List<TravelPlace> travelPlaceList) {
        this.travelPlaceList = travelPlaceList;
    }

    public List<TravelAlbum> getTravelAlbumList() {
        return travelAlbumList;
    }

    public void setTravelAlbumList(List<TravelAlbum> travelAlbumList) {
        this.travelAlbumList = travelAlbumList;
    }

    public List<TravelVideo> getTravelVideoList() {
        return travelVideoList;
    }

    public void setTravelVideoList(List<TravelVideo> travelVideoList) {
        this.travelVideoList = travelVideoList;
    }

    public List<TravelFood> getTravelFoodList() {
        return travelFoodList;
    }

    public void setTravelFoodList(List<TravelFood> travelFoodList) {
        this.travelFoodList = travelFoodList;
    }

    public List<TravelDiary> getTravelDiaryList() {
        return travelDiaryList;
    }

    public void setTravelDiaryList(List<TravelDiary> travelDiaryList) {
        this.travelDiaryList = travelDiaryList;
    }

    public Travel() {
    }

    protected Travel(Parcel in) {
        super(in);
        happenAt = in.readLong();
        title = in.readString();
        travelPlaceList = in.createTypedArrayList(TravelPlace.CREATOR);
        travelAlbumList = in.createTypedArrayList(TravelAlbum.CREATOR);
        travelVideoList = in.createTypedArrayList(TravelVideo.CREATOR);
        travelFoodList = in.createTypedArrayList(TravelFood.CREATOR);
        travelMovieList = in.createTypedArrayList(TravelMovie.CREATOR);
        travelDiaryList = in.createTypedArrayList(TravelDiary.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeLong(happenAt);
        dest.writeString(title);
        dest.writeTypedList(travelPlaceList);
        dest.writeTypedList(travelAlbumList);
        dest.writeTypedList(travelVideoList);
        dest.writeTypedList(travelFoodList);
        dest.writeTypedList(travelMovieList);
        dest.writeTypedList(travelDiaryList);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Travel> CREATOR = new Creator<Travel>() {
        @Override
        public Travel createFromParcel(Parcel in) {
            return new Travel(in);
        }

        @Override
        public Travel[] newArray(int size) {
            return new Travel[size];
        }
    };

}
