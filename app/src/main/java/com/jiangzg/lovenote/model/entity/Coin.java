package com.jiangzg.lovenote.model.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.jiangzg.lovenote.base.BaseCP;
import com.jiangzg.lovenote.helper.ApiHelper;

/**
 * Created by JZG on 2018/8/7.
 * Coin
 */
public class Coin extends BaseCP implements Parcelable, MultiItemEntity {

    public static final int KIND_ADD_BY_SYS = 10; // +系统变更
    public static final int KIND_ADD_BY_PLAY_PAY = 100; // +商店充值
    public static final int KIND_ADD_BY_SIGN_DAY = 200; // +每日签到
    public static final int KIND_ADD_BY_MATCH_POST = 300; // +参加比拼
    public static final int KIND_SUB_BY_MATCH_UP = -300; // -比拼投币
    public static final int KIND_SUB_BY_WISH_UP = -410; // -许愿投币
    public static final int KIND_SUB_BY_PLANE_UP = -420; // -飞机投币

    private int kind;
    private long billId;
    private int change;
    private int count;

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
