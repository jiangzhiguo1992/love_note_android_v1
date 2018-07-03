package com.jiangzg.mianmian.domain;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by JZG on 2018/7/2.
 * TravelVideo
 */
public class TravelVideo extends BaseCP implements Parcelable {

    private long videoId;
    private Video video;

    public long getVideoId() {
        return videoId;
    }

    public void setVideoId(long videoId) {
        this.videoId = videoId;
    }

    public Video getVideo() {
        return video;
    }

    public void setVideo(Video video) {
        this.video = video;
    }

    public TravelVideo() {
    }

    protected TravelVideo(Parcel in) {
        super(in);
        videoId = in.readLong();
        video = in.readParcelable(Video.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeLong(videoId);
        dest.writeParcelable(video, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<TravelVideo> CREATOR = new Creator<TravelVideo>() {
        @Override
        public TravelVideo createFromParcel(Parcel in) {
            return new TravelVideo(in);
        }

        @Override
        public TravelVideo[] newArray(int size) {
            return new TravelVideo[size];
        }
    };

}
