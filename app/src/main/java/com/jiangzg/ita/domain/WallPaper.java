package com.jiangzg.ita.domain;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by JZG on 2018/3/22.
 * 墙纸
 */
public class WallPaper extends BaseObj implements Parcelable {

    private long coupleId;
    private List<String> imageList;

    public long getCoupleId() {
        return coupleId;
    }

    public void setCoupleId(long coupleId) {
        this.coupleId = coupleId;
    }

    public List<String> getImageList() {
        return imageList;
    }

    public void setImageList(List<String> imageList) {
        this.imageList = imageList;
    }

    public WallPaper() {
    }

    protected WallPaper(Parcel in) {
        super(in);
        coupleId = in.readLong();
        imageList = in.createStringArrayList();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeLong(coupleId);
        dest.writeStringList(imageList);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<WallPaper> CREATOR = new Creator<WallPaper>() {
        @Override
        public WallPaper createFromParcel(Parcel in) {
            return new WallPaper(in);
        }

        @Override
        public WallPaper[] newArray(int size) {
            return new WallPaper[size];
        }
    };

}
