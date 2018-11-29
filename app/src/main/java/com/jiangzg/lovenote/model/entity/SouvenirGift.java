package com.jiangzg.lovenote.model.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.jiangzg.lovenote.base.BaseCP;

/**
 * Created by JZG on 2018/7/10.
 * SouvenirGift
 */
public class SouvenirGift extends BaseCP implements Parcelable {

    private long souvenirId;
    private long giftId;
    private int year;
    private Gift gift;

    public long getSouvenirId() {
        return souvenirId;
    }

    public void setSouvenirId(long souvenirId) {
        this.souvenirId = souvenirId;
    }

    public long getGiftId() {
        return giftId;
    }

    public void setGiftId(long giftId) {
        this.giftId = giftId;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public Gift getGift() {
        return gift;
    }

    public void setGift(Gift gift) {
        this.gift = gift;
    }

    public SouvenirGift() {
    }

    protected SouvenirGift(Parcel in) {
        super(in);
        souvenirId = in.readLong();
        giftId = in.readLong();
        year = in.readInt();
        gift = in.readParcelable(Gift.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeLong(souvenirId);
        dest.writeLong(giftId);
        dest.writeInt(year);
        dest.writeParcelable(gift, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<SouvenirGift> CREATOR = new Creator<SouvenirGift>() {
        @Override
        public SouvenirGift createFromParcel(Parcel in) {
            return new SouvenirGift(in);
        }

        @Override
        public SouvenirGift[] newArray(int size) {
            return new SouvenirGift[size];
        }
    };
}
