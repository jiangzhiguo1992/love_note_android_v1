package com.jiangzg.lovenote.model.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.jiangzg.lovenote.base.BaseCP;

/**
 * Created by JZG on 2018/7/2.
 * TravelMovie
 */
public class TravelMovie extends BaseCP implements Parcelable {

    private long movieId;
    private Movie movie;

    public long getMovieId() {
        return movieId;
    }

    public void setMovieId(long movieId) {
        this.movieId = movieId;
    }

    public Movie getMovie() {
        return movie;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }

    public TravelMovie() {
    }

    protected TravelMovie(Parcel in) {
        super(in);
        movieId = in.readLong();
        movie = in.readParcelable(Movie.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeLong(movieId);
        dest.writeParcelable(movie, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<TravelMovie> CREATOR = new Creator<TravelMovie>() {
        @Override
        public TravelMovie createFromParcel(Parcel in) {
            return new TravelMovie(in);
        }

        @Override
        public TravelMovie[] newArray(int size) {
            return new TravelMovie[size];
        }
    };

}
