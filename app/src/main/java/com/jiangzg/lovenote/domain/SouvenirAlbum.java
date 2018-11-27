package com.jiangzg.lovenote.domain;

import android.os.Parcel;
import android.os.Parcelable;

import com.jiangzg.lovenote.base.BaseCP;

/**
 * Created by JZG on 2018/7/10.
 * SouvenirAlbum
 */
public class SouvenirAlbum extends BaseCP implements Parcelable {

    private long souvenirId;
    private long albumId;
    private int year;
    private Album album;

    public long getSouvenirId() {
        return souvenirId;
    }

    public void setSouvenirId(long souvenirId) {
        this.souvenirId = souvenirId;
    }

    public long getAlbumId() {
        return albumId;
    }

    public void setAlbumId(long albumId) {
        this.albumId = albumId;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public Album getAlbum() {
        return album;
    }

    public void setAlbum(Album album) {
        this.album = album;
    }

    public SouvenirAlbum() {
    }

    protected SouvenirAlbum(Parcel in) {
        super(in);
        souvenirId = in.readLong();
        albumId = in.readLong();
        year = in.readInt();
        album = in.readParcelable(Album.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeLong(souvenirId);
        dest.writeLong(albumId);
        dest.writeInt(year);
        dest.writeParcelable(album, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<SouvenirAlbum> CREATOR = new Creator<SouvenirAlbum>() {
        @Override
        public SouvenirAlbum createFromParcel(Parcel in) {
            return new SouvenirAlbum(in);
        }

        @Override
        public SouvenirAlbum[] newArray(int size) {
            return new SouvenirAlbum[size];
        }
    };
}
