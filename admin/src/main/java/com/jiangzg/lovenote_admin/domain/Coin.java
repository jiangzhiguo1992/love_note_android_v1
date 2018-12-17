package com.jiangzg.lovenote_admin.domain;

import android.os.Parcel;
import android.os.Parcelable;

import com.jiangzg.lovenote_admin.R;
import com.jiangzg.lovenote_admin.base.MyApp;

/**
 * Created by JZG on 2018/8/7.
 * Coin
 */
public class Coin extends BaseCP implements Parcelable {

    // add
    public static final int COIN_KIND_ADD_BY_SYS = 10; // +系统变更
    public static final int COIN_KIND_ADD_BY_PLAY_PAY = 100; // +商店充值
    public static final int COIN_KIND_ADD_BY_SIGN_DAY = 200; // +每日签到
    public static final int COIN_KIND_ADD_BY_MATCH_POST = 300; // +参加比拼
    public static final int COIN_KIND_SUB_BY_MATCH_UP = -300; // -比拼投币
    public static final int COIN_KIND_SUB_BY_WISH_UP = -410; // -许愿投币
    public static final int COIN_KIND_SUB_BY_CARD_UP = -420; // -卡片投币

    private int kind;
    private long billId;
    private int change;
    private int count;

    public static String getKindShow(int form) {
        switch (form) {
            case COIN_KIND_ADD_BY_SYS:
                return MyApp.get().getString(R.string.sys_change);
            case COIN_KIND_ADD_BY_PLAY_PAY:
                return MyApp.get().getString(R.string.pay);
            case COIN_KIND_ADD_BY_SIGN_DAY:
                return MyApp.get().getString(R.string.sign);
            case COIN_KIND_ADD_BY_MATCH_POST:
                return MyApp.get().getString(R.string.nav_match);
            case COIN_KIND_SUB_BY_MATCH_UP:
                return MyApp.get().getString(R.string.nav_match);
            case COIN_KIND_SUB_BY_WISH_UP:
                return MyApp.get().getString(R.string.nav_wish);
            case COIN_KIND_SUB_BY_CARD_UP:
                return MyApp.get().getString(R.string.nav_postcard);
        }
        return MyApp.get().getString(R.string.unknown_kind);
    }

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
