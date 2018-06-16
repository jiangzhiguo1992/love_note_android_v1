package com.jiangzg.mianmian.domain;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by JZG on 2018/3/12.
 * 意见反馈
 */
public class Suggest extends BaseObj implements Parcelable {

    public static final int STATUS_REPLY_NO = 1;
    public static final int STATUS_REPLY_YES = 2;
    public static final int STATUS_ACCEPT_NO = 3;
    public static final int STATUS_ACCEPT_YES = 4;
    public static final int STATUS_HANDLE_ING = 5;
    public static final int STATUS_HANDLE_OVER = 6;

    public static final int TYPE_ALL = 0;
    public static final int TYPE_ERROR = 1;
    public static final int TYPE_FUNCTION = 2;
    public static final int TYPE_OPTIMIZE = 3;
    public static final int TYPE_DEBUNK = 4;

    private String title;
    private int contentType;
    private String contentText;
    private String contentImg;
    // tag标签
    private boolean top;
    private boolean official;
    private boolean mine;
    // 外键
    private boolean follow;
    private boolean comment;
    private long followCount;
    private long commentCount;

    public boolean isOfficial() {
        return official;
    }

    public void setOfficial(boolean official) {
        this.official = official;
    }

    public boolean isTop() {
        return top;
    }

    public void setTop(boolean top) {
        this.top = top;
    }

    public int getContentType() {
        return contentType;
    }

    public void setContentType(int contentType) {
        this.contentType = contentType;
    }

    public boolean isMine() {
        return mine;
    }

    public void setMine(boolean mine) {
        this.mine = mine;
    }

    public String getContentImg() {
        return contentImg;
    }

    public void setContentImg(String contentImg) {
        this.contentImg = contentImg;
    }

    public boolean isComment() {
        return comment;
    }

    public String getContentText() {
        return contentText;
    }

    public void setContentText(String contentText) {
        this.contentText = contentText;
    }

    public void setComment(boolean comment) {
        this.comment = comment;
    }

    public boolean isFollow() {
        return follow;
    }

    public void setFollow(boolean follow) {
        this.follow = follow;
    }

    public long getFollowCount() {
        return followCount;
    }

    public void setFollowCount(long followCount) {
        this.followCount = followCount;
    }

    public long getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(long commentCount) {
        this.commentCount = commentCount;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Suggest() {
    }

    protected Suggest(Parcel in) {
        super(in);
        title = in.readString();
        contentType = in.readInt();
        contentText = in.readString();
        contentImg = in.readString();
        official = in.readByte() != 0;
        top = in.readByte() != 0;
        follow = in.readByte() != 0;
        comment = in.readByte() != 0;
        followCount = in.readLong();
        commentCount = in.readLong();
        mine = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(title);
        dest.writeInt(contentType);
        dest.writeString(contentText);
        dest.writeString(contentImg);
        dest.writeByte((byte) (official ? 1 : 0));
        dest.writeByte((byte) (top ? 1 : 0));
        dest.writeByte((byte) (follow ? 1 : 0));
        dest.writeByte((byte) (comment ? 1 : 0));
        dest.writeLong(followCount);
        dest.writeLong(commentCount);
        dest.writeByte((byte) (mine ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Suggest> CREATOR = new Creator<Suggest>() {
        @Override
        public Suggest createFromParcel(Parcel in) {
            return new Suggest(in);
        }

        @Override
        public Suggest[] newArray(int size) {
            return new Suggest[size];
        }
    };

}
