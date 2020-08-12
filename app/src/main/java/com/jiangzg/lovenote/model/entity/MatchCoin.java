package com.jiangzg.lovenote.model.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by JZG on 2018/8/19.
 * MatchCoin
 */
public class MatchCoin extends BaseCP implements Parcelable {

    private long matchPeriodId;
    private long matchWorkId;
    private long coinId;
    private int coinCount;

    public long getMatchPeriodId() {
        return matchPeriodId;
    }

    public void setMatchPeriodId(long matchPeriodId) {
        this.matchPeriodId = matchPeriodId;
    }

    public long getMatchWorkId() {
        return matchWorkId;
    }

    public void setMatchWorkId(long matchWorkId) {
        this.matchWorkId = matchWorkId;
    }

    public long getCoinId() {
        return coinId;
    }

    public void setCoinId(long coinId) {
        this.coinId = coinId;
    }

    public int getCoinCount() {
        return coinCount;
    }

    public void setCoinCount(int coinCount) {
        this.coinCount = coinCount;
    }

    public MatchCoin() {
    }

    protected MatchCoin(Parcel in) {
        super(in);
        matchPeriodId = in.readLong();
        matchWorkId = in.readLong();
        coinId = in.readLong();
        coinCount = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeLong(matchPeriodId);
        dest.writeLong(matchWorkId);
        dest.writeLong(coinId);
        dest.writeInt(coinCount);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MatchCoin> CREATOR = new Creator<MatchCoin>() {
        @Override
        public MatchCoin createFromParcel(Parcel in) {
            return new MatchCoin(in);
        }

        @Override
        public MatchCoin[] newArray(int size) {
            return new MatchCoin[size];
        }
    };
}
