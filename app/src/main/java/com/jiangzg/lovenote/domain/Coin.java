package com.jiangzg.lovenote.domain;

import android.os.Parcel;
import android.os.Parcelable;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.base.MyApp;
import com.jiangzg.lovenote.helper.ApiHelper;

/**
 * Created by JZG on 2018/8/7.
 * Coin
 */
public class Coin extends BaseCP implements Parcelable, MultiItemEntity {

    public static final int COIN_KIND_ADD_BY_PLAY_PAY = 100; // +商店充值
    public static final int COIN_KIND_ADD_BY_SIGN_DAY = 200; // +每日签到
    public static final int COIN_KIND_ADD_BY_WIFE_POST = 310; // +发表夫妻
    public static final int COIN_KIND_ADD_BY_LETTER_POST = 320; // +发表情书
    public static final int COIN_KIND_ADD_BY_DISCUSS_POST = 330; // +发表讨论
    public static final int COIN_KIND_SUB_BY_WIFE_UP = -310; // -夫妻投币
    public static final int COIN_KIND_SUB_BY_LETTER_UP = -320; // -情书投币
    public static final int COIN_KIND_SUB_BY_DISCUSS_UP = -330; // -讨论投币
    public static final int COIN_KIND_SUB_BY_WISH_POST = -410; // -发表许愿
    public static final int COIN_KIND_SUB_BY_WISH_BLESS = -411; // -许愿祝福
    public static final int COIN_KIND_SUB_BY_PLANE_POST = -420; // -发表飞机
    public static final int COIN_KIND_SUB_BY_PLANE_BLESS = -421; // -飞机祝福

    private int kind;
    private long billId;
    private int change;
    private int count;

    public static String getKindShow(int form) {
        switch (form) {
            case COIN_KIND_ADD_BY_PLAY_PAY:
                return MyApp.get().getString(R.string.play_pay);
            case COIN_KIND_ADD_BY_SIGN_DAY:
                return MyApp.get().getString(R.string.every_day_sign);
            case COIN_KIND_ADD_BY_WIFE_POST:
                return MyApp.get().getString(R.string.go_in_spae) + MyApp.get().getString(R.string.nav_wife);
            case COIN_KIND_ADD_BY_LETTER_POST:
                return MyApp.get().getString(R.string.go_in_spae) + MyApp.get().getString(R.string.nav_letter);
            case COIN_KIND_ADD_BY_DISCUSS_POST:
                return MyApp.get().getString(R.string.go_in_spae) + MyApp.get().getString(R.string.nav_discuss);
            case COIN_KIND_SUB_BY_WIFE_UP:
                return MyApp.get().getString(R.string.throw_coin_space) + MyApp.get().getString(R.string.nav_wife);
            case COIN_KIND_SUB_BY_LETTER_UP:
                return MyApp.get().getString(R.string.throw_coin_space) + MyApp.get().getString(R.string.nav_letter);
            case COIN_KIND_SUB_BY_DISCUSS_UP:
                return MyApp.get().getString(R.string.throw_coin_space) + MyApp.get().getString(R.string.nav_discuss);
            case COIN_KIND_SUB_BY_WISH_POST:
                return MyApp.get().getString(R.string.go_in_spae) + MyApp.get().getString(R.string.nav_wish);
            case COIN_KIND_SUB_BY_WISH_BLESS:
                return MyApp.get().getString(R.string.bless_space) + MyApp.get().getString(R.string.nav_wish);
            case COIN_KIND_SUB_BY_PLANE_POST:
                return MyApp.get().getString(R.string.go_in_spae) + MyApp.get().getString(R.string.nav_plane);
            case COIN_KIND_SUB_BY_PLANE_BLESS:
                return MyApp.get().getString(R.string.bless_space) + MyApp.get().getString(R.string.nav_plane);
        }
        return MyApp.get().getString(R.string.unknown_kind);
    }

    @Override
    public int getItemType() {
        return isMine() ? ApiHelper.LIST_NOTE_MY : ApiHelper.LIST_NOTE_TA;
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
