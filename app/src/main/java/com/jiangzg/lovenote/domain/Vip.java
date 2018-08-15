package com.jiangzg.lovenote.domain;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by JZG on 2018/8/7.
 * Vip
 */
public class Vip extends BaseCP implements Parcelable {

    private int fromType;
    private int billPlatform;
    private long billId;
    private int expireDays;
    private long expireAt;

    public int getBillPlatform() {
        return billPlatform;
    }

    public void setBillPlatform(int billPlatform) {
        this.billPlatform = billPlatform;
    }

    public long getBillId() {
        return billId;
    }

    public void setBillId(long billId) {
        this.billId = billId;
    }

    public int getExpireDays() {
        return expireDays;
    }

    public void setExpireDays(int expireDays) {
        this.expireDays = expireDays;
    }

    public long getExpireAt() {
        return expireAt;
    }

    public void setExpireAt(long expireAt) {
        this.expireAt = expireAt;
    }

    public int getFromType() {
        return fromType;
    }

    public void setFromType(int fromType) {
        this.fromType = fromType;
    }

    public Vip() {
    }

    protected Vip(Parcel in) {
        super(in);
        fromType = in.readInt();
        billPlatform = in.readInt();
        billId = in.readLong();
        expireDays = in.readInt();
        expireAt = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(fromType);
        dest.writeInt(billPlatform);
        dest.writeLong(billId);
        dest.writeInt(expireDays);
        dest.writeLong(expireAt);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Vip> CREATOR = new Creator<Vip>() {
        @Override
        public Vip createFromParcel(Parcel in) {
            return new Vip(in);
        }

        @Override
        public Vip[] newArray(int size) {
            return new Vip[size];
        }
    };
}
