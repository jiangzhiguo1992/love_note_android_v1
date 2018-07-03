package com.jiangzg.mianmian.domain;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by JZG on 2018/7/2.
 * TravelAlbum
 */
public class TravelAlbum extends BaseCP implements Parcelable {

    private long albumId;
    private Album album;

    public long getAlbumId() {
        return albumId;
    }

    public void setAlbumId(long albumId) {
        this.albumId = albumId;
    }

    public Album getAlbum() {
        return album;
    }

    public void setAlbum(Album album) {
        this.album = album;
    }

    public TravelAlbum() {
    }

    protected TravelAlbum(Parcel in) {
        super(in);
        albumId = in.readLong();
        album = in.readParcelable(Album.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeLong(albumId);
        dest.writeParcelable(album, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<TravelAlbum> CREATOR = new Creator<TravelAlbum>() {
        @Override
        public TravelAlbum createFromParcel(Parcel in) {
            return new TravelAlbum(in);
        }

        @Override
        public TravelAlbum[] newArray(int size) {
            return new TravelAlbum[size];
        }
    };

}
