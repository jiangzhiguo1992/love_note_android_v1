package com.jiangzg.lovenote.model.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by JZG on 2018/7/10.
 * SouvenirMovie
 */
public class SouvenirMovie extends BaseCP implements Parcelable {

    private long souvenirId;
    private long movieId;
    private int year;
    private Movie movie;

    public long getSouvenirId() {
        return souvenirId;
    }

    public void setSouvenirId(long souvenirId) {
        this.souvenirId = souvenirId;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

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

    public SouvenirMovie() {
    }

    protected SouvenirMovie(Parcel in) {
        super(in);
        souvenirId = in.readLong();
        movieId = in.readLong();
        year = in.readInt();
        movie = in.readParcelable(Movie.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeLong(souvenirId);
        dest.writeLong(movieId);
        dest.writeInt(year);
        dest.writeParcelable(movie, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<SouvenirMovie> CREATOR = new Creator<SouvenirMovie>() {
        @Override
        public SouvenirMovie createFromParcel(Parcel in) {
            return new SouvenirMovie(in);
        }

        @Override
        public SouvenirMovie[] newArray(int size) {
            return new SouvenirMovie[size];
        }
    };
}
