package com.jiangzg.lovenote.model.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.jiangzg.lovenote.base.BaseCP;

/**
 * Created by JZG on 2018/8/19.
 * MatchPoint
 */
public class MatchPoint extends BaseCP implements Parcelable {

    private long matchPeriodId;
    private long matchWorkId;

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

    public MatchPoint() {
    }

    protected MatchPoint(Parcel in) {
        super(in);
        matchPeriodId = in.readLong();
        matchWorkId = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeLong(matchPeriodId);
        dest.writeLong(matchWorkId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MatchPoint> CREATOR = new Creator<MatchPoint>() {
        @Override
        public MatchPoint createFromParcel(Parcel in) {
            return new MatchPoint(in);
        }

        @Override
        public MatchPoint[] newArray(int size) {
            return new MatchPoint[size];
        }
    };
}
