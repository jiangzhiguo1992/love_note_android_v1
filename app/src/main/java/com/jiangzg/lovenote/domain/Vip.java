package com.jiangzg.lovenote.domain;

import android.os.Parcel;
import android.os.Parcelable;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.base.MyApp;
import com.jiangzg.lovenote.helper.ApiHelper;

/**
 * Created by JZG on 2018/8/7.
 * Vip
 */
public class Vip extends BaseCP implements Parcelable, MultiItemEntity {

    public static final int VIP_FROM_TYPE_USER_BUY = 100; // 用户购买
    public static final int VIP_FROM_TYPE_SYS_SEND = 210; // 系统赠送
    public static final int VIP_FROM_TYPE_SYS_MAKE = 220; // 系统补偿

    public static final int VIP_BILL_PLATFORM_WX = 1; // 微信
    public static final int VIP_BILL_PLATFORM_ALI = 2; // 阿里

    private int fromType;
    private int billPlatform;
    private long billId;
    private int expireDays;
    private long expireAt;

    @Override
    public int getItemType() {
        return isMine() ? ApiHelper.LIST_NOTE_MY : ApiHelper.LIST_NOTE_TA;
    }

    public static String getFromTypeShow(int type) {
        switch (type) {
            case VIP_FROM_TYPE_USER_BUY:
                return MyApp.get().getString(R.string.user_buy);
            case VIP_FROM_TYPE_SYS_SEND:
                return MyApp.get().getString(R.string.sys_send);
            case VIP_FROM_TYPE_SYS_MAKE:
                return MyApp.get().getString(R.string.sys_make);
        }
        return MyApp.get().getString(R.string.unknown_from_type);
    }

    public static String getPlatformShow(int form) {
        switch (form) {
            case VIP_BILL_PLATFORM_WX:
                return MyApp.get().getString(R.string.we_chat);
            case VIP_BILL_PLATFORM_ALI:
                return MyApp.get().getString(R.string.ali_pay);
        }
        return MyApp.get().getString(R.string.unknown_platform);
    }

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
