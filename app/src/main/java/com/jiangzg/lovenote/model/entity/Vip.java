package com.jiangzg.lovenote.model.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.jiangzg.lovenote.base.BaseCP;
import com.jiangzg.lovenote.helper.ApiHelper;

/**
 * Created by JZG on 2018/8/7.
 * Vip
 */
public class Vip extends BaseCP implements Parcelable, MultiItemEntity {

    private int fromType;
    private long billId;
    private int expireDays;
    private long expireAt;

    @Override
    public int getItemType() {
        return isMine() ? ApiHelper.LIST_NOTE_MY : ApiHelper.LIST_NOTE_TA;
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
        billId = in.readLong();
        expireDays = in.readInt();
        expireAt = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(fromType);
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
