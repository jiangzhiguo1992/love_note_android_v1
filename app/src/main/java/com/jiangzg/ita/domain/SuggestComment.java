package com.jiangzg.ita.domain;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by JZG on 2018/3/14.
 * 意见评论
 */
public class SuggestComment extends BaseObj implements Parcelable {

    private boolean mine;
    private boolean official;
    private String contentText;

    public boolean isMine() {
        return mine;
    }

    public void setMine(boolean mine) {
        this.mine = mine;
    }

    public boolean isOfficial() {
        return official;
    }

    public void setOfficial(boolean official) {
        this.official = official;
    }

    public String getContentText() {
        return contentText;
    }

    public void setContentText(String contentText) {
        this.contentText = contentText;
    }

    public SuggestComment() {
    }

    protected SuggestComment(Parcel in) {
        super(in);
        mine = in.readByte() != 0;
        official = in.readByte() != 0;
        contentText = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeByte((byte) (mine ? 1 : 0));
        dest.writeByte((byte) (official ? 1 : 0));
        dest.writeString(contentText);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<SuggestComment> CREATOR = new Creator<SuggestComment>() {
        @Override
        public SuggestComment createFromParcel(Parcel in) {
            return new SuggestComment(in);
        }

        @Override
        public SuggestComment[] newArray(int size) {
            return new SuggestComment[size];
        }
    };

}
