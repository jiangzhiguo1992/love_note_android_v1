package com.jiangzg.lovenote.model.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.jiangzg.lovenote.helper.common.ApiHelper;

/**
 * Created by JZG on 2018/4/24.
 * 留言
 */
public class Word extends BaseCP implements Parcelable, MultiItemEntity {

    private String contentText;

    @Override
    public int getItemType() {
        return isMine() ? ApiHelper.LIST_NOTE_MY : ApiHelper.LIST_NOTE_TA;
    }

    public String getContentText() {
        return contentText;
    }

    public void setContentText(String contentText) {
        this.contentText = contentText;
    }

    public Word() {
    }

    protected Word(Parcel in) {
        super(in);
        contentText = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(contentText);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Word> CREATOR = new Creator<Word>() {
        @Override
        public Word createFromParcel(Parcel in) {
            return new Word(in);
        }

        @Override
        public Word[] newArray(int size) {
            return new Word[size];
        }
    };

}
