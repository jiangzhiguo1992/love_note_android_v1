package com.jiangzg.lovenote.domain;

import android.os.Parcel;
import android.os.Parcelable;

import com.jiangzg.lovenote.base.BaseCP;

/**
 * Created by JZG on 2018/6/29.
 * AwardRule
 */
public class AwardRule extends BaseCP implements Parcelable {

    private String title;
    private int score;
    private int useCount;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getUseCount() {
        return useCount;
    }

    public void setUseCount(int useCount) {
        this.useCount = useCount;
    }

    public AwardRule() {
    }

    protected AwardRule(Parcel in) {
        super(in);
        title = in.readString();
        score = in.readInt();
        useCount = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(title);
        dest.writeInt(score);
        dest.writeInt(useCount);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<AwardRule> CREATOR = new Creator<AwardRule>() {
        @Override
        public AwardRule createFromParcel(Parcel in) {
            return new AwardRule(in);
        }

        @Override
        public AwardRule[] newArray(int size) {
            return new AwardRule[size];
        }
    };

}
