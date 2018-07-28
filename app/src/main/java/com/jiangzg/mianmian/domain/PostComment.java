package com.jiangzg.mianmian.domain;

import android.os.Parcel;
import android.os.Parcelable;

import com.chad.library.adapter.base.entity.MultiItemEntity;

/**
 * Created by JZG on 2018/7/23.
 * PostComment
 */
public class PostComment extends BaseCP implements Parcelable, MultiItemEntity {

    public static final int KIND_TEXT = 0;
    public static final int KIND_JAB = 1;

    @Override
    public int getItemType() {
        return kind;
    }

    private long postId;
    private long toCommentId;
    private int floor;
    private int kind;
    private String contentText;
    private boolean official;
    private int subCommentCount;
    private int reportCount;
    private int pointCount;
    // 关联
    private boolean screen;
    private Couple couple;
    private boolean mine;
    private boolean our;
    private boolean subComment;
    private boolean report;
    private boolean point;

    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    public boolean isSubComment() {
        return subComment;
    }

    public void setSubComment(boolean subComment) {
        this.subComment = subComment;
    }

    public int getSubCommentCount() {
        return subCommentCount;
    }

    public void setSubCommentCount(int subCommentCount) {
        this.subCommentCount = subCommentCount;
    }

    public long getPostId() {
        return postId;
    }

    public void setPostId(long postId) {
        this.postId = postId;
    }

    public long getToCommentId() {
        return toCommentId;
    }

    public void setToCommentId(long toCommentId) {
        this.toCommentId = toCommentId;
    }

    public int getKind() {
        return kind;
    }

    public void setKind(int kind) {
        this.kind = kind;
    }

    public String getContentText() {
        return contentText;
    }

    public void setContentText(String contentText) {
        this.contentText = contentText;
    }

    public boolean isOfficial() {
        return official;
    }

    public void setOfficial(boolean official) {
        this.official = official;
    }

    public int getReportCount() {
        return reportCount;
    }

    public void setReportCount(int reportCount) {
        this.reportCount = reportCount;
    }

    public int getPointCount() {
        return pointCount;
    }

    public void setPointCount(int pointCount) {
        this.pointCount = pointCount;
    }

    public boolean isScreen() {
        return screen;
    }

    public void setScreen(boolean screen) {
        this.screen = screen;
    }

    public Couple getCouple() {
        return couple;
    }

    public void setCouple(Couple couple) {
        this.couple = couple;
    }

    @Override
    public boolean isMine() {
        return mine;
    }

    public void setMine(boolean mine) {
        this.mine = mine;
    }

    public boolean isOur() {
        return our;
    }

    public void setOur(boolean our) {
        this.our = our;
    }

    public boolean isReport() {
        return report;
    }

    public void setReport(boolean report) {
        this.report = report;
    }

    public boolean isPoint() {
        return point;
    }

    public void setPoint(boolean point) {
        this.point = point;
    }

    public PostComment() {
    }

    protected PostComment(Parcel in) {
        super(in);
        postId = in.readLong();
        toCommentId = in.readLong();
        floor = in.readInt();
        kind = in.readInt();
        contentText = in.readString();
        official = in.readByte() != 0;
        subCommentCount = in.readInt();
        reportCount = in.readInt();
        pointCount = in.readInt();
        screen = in.readByte() != 0;
        couple = in.readParcelable(Couple.class.getClassLoader());
        mine = in.readByte() != 0;
        our = in.readByte() != 0;
        subComment = in.readByte() != 0;
        report = in.readByte() != 0;
        point = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeLong(postId);
        dest.writeLong(toCommentId);
        dest.writeInt(floor);
        dest.writeInt(kind);
        dest.writeString(contentText);
        dest.writeByte((byte) (official ? 1 : 0));
        dest.writeInt(subCommentCount);
        dest.writeInt(reportCount);
        dest.writeInt(pointCount);
        dest.writeByte((byte) (screen ? 1 : 0));
        dest.writeParcelable(couple, flags);
        dest.writeByte((byte) (mine ? 1 : 0));
        dest.writeByte((byte) (our ? 1 : 0));
        dest.writeByte((byte) (subComment ? 1 : 0));
        dest.writeByte((byte) (report ? 1 : 0));
        dest.writeByte((byte) (point ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PostComment> CREATOR = new Creator<PostComment>() {
        @Override
        public PostComment createFromParcel(Parcel in) {
            return new PostComment(in);
        }

        @Override
        public PostComment[] newArray(int size) {
            return new PostComment[size];
        }
    };

}
