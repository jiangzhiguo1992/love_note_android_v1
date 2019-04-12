package com.jiangzg.lovenote.model.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by JZG on 2019/4/11.
 * Menses 新版 version>=20
 */
public class Menses2 extends BaseCP implements Parcelable {

    private int startAt;
    private int endAt;
    private int startYear;
    private int startMonthOfYear;
    private int startDayOfMonth;
    private int endYear;
    private int endMonthOfYear;
    private int endDayOfMonth;
    // 关联
    private boolean isReal;
    private List<MensesDayInfo> mensesDayInfoList;
    private int safeStartYear;
    private int safeStartMonthOfYear;
    private int safeStartDayOfMonth;
    private int safeEndYear;
    private int safeEndMonthOfYear;
    private int safeEndDayOfMonth;
    private int dangerStartYear;
    private int dangerStartMonthOfYear;
    private int dangerStartDayOfMonth;
    private int dangerEndYear;
    private int dangerEndMonthOfYear;
    private int dangerEndDayOfMonth;
    private int ovulationYear;
    private int ovulationMonthOfYear;
    private int ovulationDayOfMonth;

    public List<MensesDayInfo> getMensesDayInfoList() {
        return mensesDayInfoList;
    }

    public void setMensesDayInfoList(List<MensesDayInfo> mensesDayInfoList) {
        this.mensesDayInfoList = mensesDayInfoList;
    }

    public boolean isReal() {
        return isReal;
    }

    public void setReal(boolean real) {
        isReal = real;
    }

    public int getStartAt() {
        return startAt;
    }

    public void setStartAt(int startAt) {
        this.startAt = startAt;
    }

    public int getEndAt() {
        return endAt;
    }

    public void setEndAt(int endAt) {
        this.endAt = endAt;
    }

    public int getStartYear() {
        return startYear;
    }

    public void setStartYear(int startYear) {
        this.startYear = startYear;
    }

    public int getStartMonthOfYear() {
        return startMonthOfYear;
    }

    public void setStartMonthOfYear(int startMonthOfYear) {
        this.startMonthOfYear = startMonthOfYear;
    }

    public int getStartDayOfMonth() {
        return startDayOfMonth;
    }

    public void setStartDayOfMonth(int startDayOfMonth) {
        this.startDayOfMonth = startDayOfMonth;
    }

    public int getEndYear() {
        return endYear;
    }

    public void setEndYear(int endYear) {
        this.endYear = endYear;
    }

    public int getEndMonthOfYear() {
        return endMonthOfYear;
    }

    public void setEndMonthOfYear(int endMonthOfYear) {
        this.endMonthOfYear = endMonthOfYear;
    }

    public int getEndDayOfMonth() {
        return endDayOfMonth;
    }

    public void setEndDayOfMonth(int endDayOfMonth) {
        this.endDayOfMonth = endDayOfMonth;
    }

    public int getSafeStartYear() {
        return safeStartYear;
    }

    public void setSafeStartYear(int safeStartYear) {
        this.safeStartYear = safeStartYear;
    }

    public int getSafeStartMonthOfYear() {
        return safeStartMonthOfYear;
    }

    public void setSafeStartMonthOfYear(int safeStartMonthOfYear) {
        this.safeStartMonthOfYear = safeStartMonthOfYear;
    }

    public int getSafeStartDayOfMonth() {
        return safeStartDayOfMonth;
    }

    public void setSafeStartDayOfMonth(int safeStartDayOfMonth) {
        this.safeStartDayOfMonth = safeStartDayOfMonth;
    }

    public int getSafeEndYear() {
        return safeEndYear;
    }

    public void setSafeEndYear(int safeEndYear) {
        this.safeEndYear = safeEndYear;
    }

    public int getSafeEndMonthOfYear() {
        return safeEndMonthOfYear;
    }

    public void setSafeEndMonthOfYear(int safeEndMonthOfYear) {
        this.safeEndMonthOfYear = safeEndMonthOfYear;
    }

    public int getSafeEndDayOfMonth() {
        return safeEndDayOfMonth;
    }

    public void setSafeEndDayOfMonth(int safeEndDayOfMonth) {
        this.safeEndDayOfMonth = safeEndDayOfMonth;
    }

    public int getDangerStartYear() {
        return dangerStartYear;
    }

    public void setDangerStartYear(int dangerStartYear) {
        this.dangerStartYear = dangerStartYear;
    }

    public int getDangerStartMonthOfYear() {
        return dangerStartMonthOfYear;
    }

    public void setDangerStartMonthOfYear(int dangerStartMonthOfYear) {
        this.dangerStartMonthOfYear = dangerStartMonthOfYear;
    }

    public int getDangerStartDayOfMonth() {
        return dangerStartDayOfMonth;
    }

    public void setDangerStartDayOfMonth(int dangerStartDayOfMonth) {
        this.dangerStartDayOfMonth = dangerStartDayOfMonth;
    }

    public int getDangerEndYear() {
        return dangerEndYear;
    }

    public void setDangerEndYear(int dangerEndYear) {
        this.dangerEndYear = dangerEndYear;
    }

    public int getDangerEndMonthOfYear() {
        return dangerEndMonthOfYear;
    }

    public void setDangerEndMonthOfYear(int dangerEndMonthOfYear) {
        this.dangerEndMonthOfYear = dangerEndMonthOfYear;
    }

    public int getDangerEndDayOfMonth() {
        return dangerEndDayOfMonth;
    }

    public void setDangerEndDayOfMonth(int dangerEndDayOfMonth) {
        this.dangerEndDayOfMonth = dangerEndDayOfMonth;
    }

    public int getOvulationYear() {
        return ovulationYear;
    }

    public void setOvulationYear(int ovulationYear) {
        this.ovulationYear = ovulationYear;
    }

    public int getOvulationMonthOfYear() {
        return ovulationMonthOfYear;
    }

    public void setOvulationMonthOfYear(int ovulationMonthOfYear) {
        this.ovulationMonthOfYear = ovulationMonthOfYear;
    }

    public int getOvulationDayOfMonth() {
        return ovulationDayOfMonth;
    }

    public void setOvulationDayOfMonth(int ovulationDayOfMonth) {
        this.ovulationDayOfMonth = ovulationDayOfMonth;
    }

    public Menses2() {
    }

    protected Menses2(Parcel in) {
        super(in);
        isReal = in.readByte() != 0;
        startAt = in.readInt();
        endAt = in.readInt();
        startYear = in.readInt();
        startMonthOfYear = in.readInt();
        startDayOfMonth = in.readInt();
        endYear = in.readInt();
        endMonthOfYear = in.readInt();
        endDayOfMonth = in.readInt();
        safeStartYear = in.readInt();
        safeStartMonthOfYear = in.readInt();
        safeStartDayOfMonth = in.readInt();
        safeEndYear = in.readInt();
        safeEndMonthOfYear = in.readInt();
        safeEndDayOfMonth = in.readInt();
        dangerStartYear = in.readInt();
        dangerStartMonthOfYear = in.readInt();
        dangerStartDayOfMonth = in.readInt();
        dangerEndYear = in.readInt();
        dangerEndMonthOfYear = in.readInt();
        dangerEndDayOfMonth = in.readInt();
        ovulationYear = in.readInt();
        ovulationMonthOfYear = in.readInt();
        ovulationDayOfMonth = in.readInt();
        mensesDayInfoList = in.createTypedArrayList(MensesDayInfo.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeByte((byte) (isReal ? 1 : 0));
        dest.writeInt(startAt);
        dest.writeInt(endAt);
        dest.writeInt(startYear);
        dest.writeInt(startMonthOfYear);
        dest.writeInt(startDayOfMonth);
        dest.writeInt(endYear);
        dest.writeInt(endMonthOfYear);
        dest.writeInt(endDayOfMonth);
        dest.writeInt(safeStartYear);
        dest.writeInt(safeStartMonthOfYear);
        dest.writeInt(safeStartDayOfMonth);
        dest.writeInt(safeEndYear);
        dest.writeInt(safeEndMonthOfYear);
        dest.writeInt(safeEndDayOfMonth);
        dest.writeInt(dangerStartYear);
        dest.writeInt(dangerStartMonthOfYear);
        dest.writeInt(dangerStartDayOfMonth);
        dest.writeInt(dangerEndYear);
        dest.writeInt(dangerEndMonthOfYear);
        dest.writeInt(dangerEndDayOfMonth);
        dest.writeInt(ovulationYear);
        dest.writeInt(ovulationMonthOfYear);
        dest.writeInt(ovulationDayOfMonth);
        dest.writeTypedList(mensesDayInfoList);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Menses2> CREATOR = new Creator<Menses2>() {
        @Override
        public Menses2 createFromParcel(Parcel in) {
            return new Menses2(in);
        }

        @Override
        public Menses2[] newArray(int size) {
            return new Menses2[size];
        }
    };

}
