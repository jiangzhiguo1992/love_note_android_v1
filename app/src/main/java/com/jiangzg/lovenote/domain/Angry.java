package com.jiangzg.lovenote.domain;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by JZG on 2018/6/28.
 * Angry
 */
public class Angry extends BaseCP implements Parcelable {

    private long happenId;
    private long happenAt;
    private String contentText;
    private long giftId;
    private long promiseId;
    private Gift gift;
    private Promise promise;

    public long getHappenId() {
        return happenId;
    }

    public void setHappenId(long happenId) {
        this.happenId = happenId;
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

    public long getGiftId() {
        return giftId;
    }

    public void setGiftId(long giftId) {
        this.giftId = giftId;
    }

    public long getPromiseId() {
        return promiseId;
    }

    public void setPromiseId(long promiseId) {
        this.promiseId = promiseId;
    }

    public Gift getGift() {
        return gift;
    }

    public void setGift(Gift gift) {
        this.gift = gift;
    }

    public Promise getPromise() {
        return promise;
    }

    public void setPromise(Promise promise) {
        this.promise = promise;
    }

    public Angry() {
    }

    protected Angry(Parcel in) {
        super(in);
        happenId = in.readLong();
        happenAt = in.readLong();
        contentText = in.readString();
        giftId = in.readLong();
        promiseId = in.readLong();
        gift = in.readParcelable(Gift.class.getClassLoader());
        promise = in.readParcelable(Promise.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeLong(happenId);
        dest.writeLong(happenAt);
        dest.writeString(contentText);
        dest.writeLong(giftId);
        dest.writeLong(promiseId);
        dest.writeParcelable(gift, flags);
        dest.writeParcelable(promise, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Angry> CREATOR = new Creator<Angry>() {
        @Override
        public Angry createFromParcel(Parcel in) {
            return new Angry(in);
        }

        @Override
        public Angry[] newArray(int size) {
            return new Angry[size];
        }
    };

}
