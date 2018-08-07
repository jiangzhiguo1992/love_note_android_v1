package com.jiangzg.lovenote.domain;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by JZG on 2018/8/7.
 * Coin
 */
public class Coin extends BaseCP implements Parcelable {

    //CoinKindAddByPlayPay     = 100 // +商店充值
    //CoinKindAddBySignDay     = 110 // +每日签到
    //CoinKindAddByWifePost    = 200 // +发表夫妻
    //CoinKindSubByWifeUp      = 201 // -夫妻推送
    //CoinKindAddByLetterPost  = 210 // +发表情书
    //CoinKindSubByLetterUp    = 211 // -情书推送
    //CoinKindAddByDiscussPost = 220 // +发表讨论
    //CoinKindSubByDiscussUp   = 221 // -讨论推送
    //CoinKindWishByPost       = 300 // -发表许愿
    //CoinKindWishByBless      = 301 // -许愿祝福
    //CoinKindPlaneByPost      = 310 // -发表飞机
    //CoinKindPlaneByBless     = 311 // -飞机祝福

    private int kind;
    private long billId;
    private int change;
    private int count;

    public int getKind() {
        return kind;
    }

    public void setKind(int kind) {
        this.kind = kind;
    }

    public long getBillId() {
        return billId;
    }

    public void setBillId(long billId) {
        this.billId = billId;
    }

    public int getChange() {
        return change;
    }

    public void setChange(int change) {
        this.change = change;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public Coin() {
    }

    protected Coin(Parcel in) {
        super(in);
        kind = in.readInt();
        billId = in.readLong();
        change = in.readInt();
        count = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(kind);
        dest.writeLong(billId);
        dest.writeInt(change);
        dest.writeInt(count);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Coin> CREATOR = new Creator<Coin>() {
        @Override
        public Coin createFromParcel(Parcel in) {
            return new Coin(in);
        }

        @Override
        public Coin[] newArray(int size) {
            return new Coin[size];
        }
    };
}
