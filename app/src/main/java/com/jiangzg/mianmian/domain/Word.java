package com.jiangzg.mianmian.domain;

import android.os.Parcel;
import android.os.Parcelable;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.jiangzg.mianmian.adapter.WordAdapter;

/**
 * Created by JZG on 2018/4/24.
 * 留言
 */
public class Word extends BaseCP implements Parcelable, MultiItemEntity {

    private String content;

    @Override
    public int getItemType() {
        return isMine() ? WordAdapter.MY : WordAdapter.TA;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Word() {
    }

    protected Word(Parcel in) {
        super(in);
        content = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(content);
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
