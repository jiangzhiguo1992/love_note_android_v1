package com.jiangzg.lovenote.model.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.jiangzg.lovenote.base.BaseCP;

/**
 * Created by JZG on 2018/7/10.
 * SouvenirVideo
 */
public class SouvenirVideo extends BaseCP implements Parcelable {

    private long souvenirId;
    private long videoId;
    private int year;
    private Video video;

    public long getSouvenirId() {
        return souvenirId;
    }

    public void setSouvenirId(long souvenirId) {
        this.souvenirId = souvenirId;
    }

    public long getVideoId() {
        return videoId;
    }

    public void setVideoId(long videoId) {
        this.videoId = videoId;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public Video getVideo() {
        return video;
    }

    public void setVideo(Video video) {
        this.video = video;
    }

    public SouvenirVideo() {
    }

    protected SouvenirVideo(Parcel in) {
        super(in);
        souvenirId = in.readLong();
        videoId = in.readLong();
        year = in.readInt();
        video = in.readParcelable(Video.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeLong(souvenirId);
        dest.writeLong(videoId);
        dest.writeInt(year);
        dest.writeParcelable(video, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<SouvenirVideo> CREATOR = new Creator<SouvenirVideo>() {
        @Override
        public SouvenirVideo createFromParcel(Parcel in) {
            return new SouvenirVideo(in);
        }

        @Override
        public SouvenirVideo[] newArray(int size) {
            return new SouvenirVideo[size];
        }
    };
}
