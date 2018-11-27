package com.jiangzg.lovenote.domain;

import android.os.Parcel;
import android.os.Parcelable;

import com.jiangzg.lovenote.base.BaseCP;

/**
 * Created by JZG on 2018/7/2.
 * TravelDiary
 */
public class TravelDiary extends BaseCP implements Parcelable {

    private long diaryId;
    private Diary diary;

    public long getDiaryId() {
        return diaryId;
    }

    public void setDiaryId(long diaryId) {
        this.diaryId = diaryId;
    }

    public Diary getDiary() {
        return diary;
    }

    public void setDiary(Diary diary) {
        this.diary = diary;
    }

    public TravelDiary() {
    }

    protected TravelDiary(Parcel in) {
        super(in);
        diaryId = in.readLong();
        diary = in.readParcelable(Diary.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeLong(diaryId);
        dest.writeParcelable(diary, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<TravelDiary> CREATOR = new Creator<TravelDiary>() {
        @Override
        public TravelDiary createFromParcel(Parcel in) {
            return new TravelDiary(in);
        }

        @Override
        public TravelDiary[] newArray(int size) {
            return new TravelDiary[size];
        }
    };

}
