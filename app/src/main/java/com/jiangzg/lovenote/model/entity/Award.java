package com.jiangzg.lovenote.model.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.jiangzg.lovenote.base.BaseCP;

/**
 * Created by JZG on 2018/6/29.
 * Award
 */
public class Award extends BaseCP implements Parcelable {

    private long happenId;
    private long awardRuleId;
    private long happenAt;
    private String contentText;
    private int scoreChange;

    public int getScoreChange() {
        return scoreChange;
    }

    public void setScoreChange(int scoreChange) {
        this.scoreChange = scoreChange;
    }

    public long getHappenId() {
        return happenId;
    }

    public void setHappenId(long happenId) {
        this.happenId = happenId;
    }

    public long getAwardRuleId() {
        return awardRuleId;
    }

    public void setAwardRuleId(long awardRuleId) {
        this.awardRuleId = awardRuleId;
    }

    public long getHappenAt() {
        return happenAt;
    }

    public void setHappenAt(long happenAt) {
        this.happenAt = happenAt;
    }

    public String getContentText() {
        return contentText;
    }

    public void setContentText(String contentText) {
        this.contentText = contentText;
    }

    public Award() {
    }

    protected Award(Parcel in) {
        super(in);
        happenId = in.readLong();
        awardRuleId = in.readLong();
        happenAt = in.readLong();
        contentText = in.readString();
        scoreChange = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeLong(happenId);
        dest.writeLong(awardRuleId);
        dest.writeLong(happenAt);
        dest.writeString(contentText);
        dest.writeInt(scoreChange);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Award> CREATOR = new Creator<Award>() {
        @Override
        public Award createFromParcel(Parcel in) {
            return new Award(in);
        }

        @Override
        public Award[] newArray(int size) {
            return new Award[size];
        }
    };

}
