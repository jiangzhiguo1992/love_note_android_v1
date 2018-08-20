package com.jiangzg.lovenote.domain;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by JZG on 2018/8/19.
 * MatchWork
 */
public class MatchWork extends BaseCP implements Parcelable {

    private long matchPeriodId;
    private int kind;
    private String title;
    private String contentText;
    private String contentImage;
    private int reportCount;
    private int pointCount;
    private int coinCount;
    // 关联
    private boolean screen;
    private boolean mine;
    private Couple couple;
    private boolean our;
    private boolean report;
    private boolean point;
    private boolean coin;

    @Override
    public boolean isMine() {
        return mine;
    }

    public void setMine(boolean mine) {
        this.mine = mine;
    }

    public long getMatchPeriodId() {
        return matchPeriodId;
    }

    public void setMatchPeriodId(long matchPeriodId) {
        this.matchPeriodId = matchPeriodId;
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

    public String getContentText() {
        return contentText;
    }

    public void setContentText(String contentText) {
        this.contentText = contentText;
    }

    public String getContentImage() {
        return contentImage;
    }

    public void setContentImage(String contentImage) {
        this.contentImage = contentImage;
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

    public boolean isScreen() {
        return screen;
    }

    public void setScreen(boolean screen) {
        this.screen = screen;
    }

    public Couple getCouple() {
        return couple;
    }

    public void setCouple(Couple couple) {
        this.couple = couple;
    }

    public boolean isOur() {
        return our;
    }

    public void setOur(boolean our) {
        this.our = our;
    }

    public boolean isReport() {
        return report;
    }

    public void setReport(boolean report) {
        this.report = report;
    }

    public boolean isPoint() {
        return point;
    }

    public void setPoint(boolean point) {
        this.point = point;
    }

    public boolean isCoin() {
        return coin;
    }

    public void setCoin(boolean coin) {
        this.coin = coin;
    }

    public MatchWork() {
    }

    protected MatchWork(Parcel in) {
        super(in);
        matchPeriodId = in.readLong();
        kind = in.readInt();
        title = in.readString();
        contentText = in.readString();
        contentImage = in.readString();
        reportCount = in.readInt();
        pointCount = in.readInt();
        coinCount = in.readInt();
        screen = in.readByte() != 0;
        couple = in.readParcelable(Couple.class.getClassLoader());
        our = in.readByte() != 0;
        report = in.readByte() != 0;
        point = in.readByte() != 0;
        coin = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeLong(matchPeriodId);
        dest.writeInt(kind);
        dest.writeString(title);
        dest.writeString(contentText);
        dest.writeString(contentImage);
        dest.writeInt(reportCount);
        dest.writeInt(pointCount);
        dest.writeInt(coinCount);
        dest.writeByte((byte) (screen ? 1 : 0));
        dest.writeParcelable(couple, flags);
        dest.writeByte((byte) (our ? 1 : 0));
        dest.writeByte((byte) (report ? 1 : 0));
        dest.writeByte((byte) (point ? 1 : 0));
        dest.writeByte((byte) (coin ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MatchWork> CREATOR = new Creator<MatchWork>() {
        @Override
        public MatchWork createFromParcel(Parcel in) {
            return new MatchWork(in);
        }

        @Override
        public MatchWork[] newArray(int size) {
            return new MatchWork[size];
        }
    };
}
