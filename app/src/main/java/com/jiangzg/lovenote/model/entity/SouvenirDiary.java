package com.jiangzg.lovenote.model.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by JZG on 2018/7/10.
 * SouvenirDiary
 */
public class SouvenirDiary extends BaseCP implements Parcelable {

    private long souvenirId;
    private long diaryId;
    private int year;
    private Diary diary;

    public long getSouvenirId() {
        return souvenirId;
    }

    public void setSouvenirId(long souvenirId) {
        this.souvenirId = souvenirId;
    }

    public long getDiaryId() {
        return diaryId;
    }

    public void setDiaryId(long diaryId) {
        this.diaryId = diaryId;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public Diary getDiary() {
        return diary;
    }

    public void setDiary(Diary diary) {
        this.diary = diary;
    }

    public SouvenirDiary() {

    }

    protected SouvenirDiary(Parcel in) {
        super(in);
        souvenirId = in.readLong();
        diaryId = in.readLong();
        year = in.readInt();
        diary = in.readParcelable(Diary.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeLong(souvenirId);
        dest.writeLong(diaryId);
        dest.writeInt(year);
        dest.writeParcelable(diary, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<SouvenirDiary> CREATOR = new Creator<SouvenirDiary>() {
        @Override
        public SouvenirDiary createFromParcel(Parcel in) {
            return new SouvenirDiary(in);
        }

        @Override
        public SouvenirDiary[] newArray(int size) {
            return new SouvenirDiary[size];
        }
    };
}
