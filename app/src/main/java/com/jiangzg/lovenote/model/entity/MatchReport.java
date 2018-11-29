package com.jiangzg.lovenote.model.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.jiangzg.lovenote.base.BaseCP;

/**
 * Created by JZG on 2018/8/19.
 * MatchReport
 */
public class MatchReport extends BaseCP implements Parcelable {

    private long matchPeriodId;
    private long matchWorkId;
    private String reason;

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

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public MatchReport() {
    }

    protected MatchReport(Parcel in) {
        super(in);
        matchPeriodId = in.readLong();
        matchWorkId = in.readLong();
        reason = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeLong(matchPeriodId);
        dest.writeLong(matchWorkId);
        dest.writeString(reason);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MatchReport> CREATOR = new Creator<MatchReport>() {
        @Override
        public MatchReport createFromParcel(Parcel in) {
            return new MatchReport(in);
        }

        @Override
        public MatchReport[] newArray(int size) {
            return new MatchReport[size];
        }
    };
}
