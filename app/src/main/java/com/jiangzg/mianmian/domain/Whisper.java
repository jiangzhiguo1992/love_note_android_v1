package com.jiangzg.mianmian.domain;

import android.os.Parcel;
import android.os.Parcelable;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.jiangzg.mianmian.helper.ApiHelper;

/**
 * Created by JZG on 2018/4/25.
 * Whisper
 */
public class Whisper extends BaseCP implements Parcelable, MultiItemEntity {

    private String channel;
    private String content;
    private boolean isImage;

    @Override
    public int getItemType() {
        return isMine() ? ApiHelper.LIST_MY : ApiHelper.LIST_TA;
    }

    public boolean isImage() {
        return isImage;
    }

    public void setImage(boolean image) {
        this.isImage = image;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Whisper() {
    }

    protected Whisper(Parcel in) {
        super(in);
        channel = in.readString();
        content = in.readString();
        isImage = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(channel);
        dest.writeString(content);
        dest.writeByte((byte) (isImage ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Whisper> CREATOR = new Creator<Whisper>() {
        @Override
        public Whisper createFromParcel(Parcel in) {
            return new Whisper(in);
        }

        @Override
        public Whisper[] newArray(int size) {
            return new Whisper[size];
        }
    };

}
