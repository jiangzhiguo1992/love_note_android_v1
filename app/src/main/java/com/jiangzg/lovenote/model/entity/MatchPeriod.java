package com.jiangzg.lovenote.model.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by JZG on 2018/8/19.
 * MatchPeriod
 */
public class MatchPeriod extends BaseObj implements Parcelable {

    public static final int MATCH_KIND_WIFE_PICTURE = 100; // 照片墙
    public static final int MATCH_KIND_LETTER_SHOW = 200; // 情话集

    private long startAt;
    private long endAt;
    private int period;
    private int kind;
    private String title;
    private int coinChange;
    private int worksCount;
    private int reportCount;
    private int pointCount;
    private int coinCount;

    public long getStartAt() {
        return startAt;
    }

    public void setStartAt(long startAt) {
        this.startAt = startAt;
    }

    public long getEndAt() {
        return endAt;
    }

    public void setEndAt(long endAt) {
        this.endAt = endAt;
    }

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    public int getKind() {
        return kind;
    }

    public void setKind(int kind) {
        this.kind = kind;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getCoinChange() {
        return coinChange;
    }

    public void setCoinChange(int coinChange) {
        this.coinChange = coinChange;
    }

    public int getWorksCount() {
        return worksCount;
    }

    public void setWorksCount(int worksCount) {
        this.worksCount = worksCount;
    }

    public int getReportCount() {
        return reportCount;
    }

    public void setReportCount(int reportCount) {
        this.reportCount = reportCount;
    }

    public int getPointCount() {
        return pointCount;
    }

    public void setPointCount(int pointCount) {
        this.pointCount = pointCount;
    }

    public int getCoinCount() {
        return coinCount;
    }

    public void setCoinCount(int coinCount) {
        this.coinCount = coinCount;
    }

    protected MatchPeriod(Parcel in) {
        super(in);
        startAt = in.readLong();
        endAt = in.readLong();
        period = in.readInt();
        kind = in.readInt();
        title = in.readString();
        coinChange = in.readInt();
        worksCount = in.readInt();
        reportCount = in.readInt();
        pointCount = in.readInt();
        coinCount = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeLong(startAt);
        dest.writeLong(endAt);
        dest.writeInt(period);
        dest.writeInt(kind);
        dest.writeString(title);
        dest.writeInt(coinChange);
        dest.writeInt(worksCount);
        dest.writeInt(reportCount);
        dest.writeInt(pointCount);
        dest.writeInt(coinCount);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MatchPeriod> CREATOR = new Creator<MatchPeriod>() {
        @Override
        public MatchPeriod createFromParcel(Parcel in) {
            return new MatchPeriod(in);
        }

        @Override
        public MatchPeriod[] newArray(int size) {
            return new MatchPeriod[size];
        }
    };
}
