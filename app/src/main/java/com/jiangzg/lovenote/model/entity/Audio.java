package com.jiangzg.lovenote.model.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.jiangzg.lovenote.helper.common.ApiHelper;

/**
 * Created by JZG on 2018/7/4.
 * Audio
 */
public class Audio extends BaseCP implements Parcelable, MultiItemEntity {

    private long happenAt;
    private String title;
    private String contentAudio;
    private int duration;

    @Override
    public int getItemType() {
        return isMine() ? ApiHelper.LIST_NOTE_WHO_MY : ApiHelper.LIST_NOTE_WHO_TA;
    }

    public long getHappenAt() {
        return happenAt;
    }

    public void setHappenAt(long happenAt) {
        this.happenAt = happenAt;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContentAudio() {
        return contentAudio;
    }

    public void setContentAudio(String contentAudio) {
        this.contentAudio = contentAudio;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public Audio() {
    }

    protected Audio(Parcel in) {
        super(in);
        happenAt = in.readLong();
        title = in.readString();
        contentAudio = in.readString();
        duration = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeLong(happenAt);
        dest.writeString(title);
        dest.writeString(contentAudio);
        dest.writeInt(duration);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Audio> CREATOR = new Creator<Audio>() {
        @Override
        public Audio createFromParcel(Parcel in) {
            return new Audio(in);
        }

        @Override
        public Audio[] newArray(int size) {
            return new Audio[size];
        }
    };

}
