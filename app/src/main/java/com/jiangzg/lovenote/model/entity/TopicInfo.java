package com.jiangzg.lovenote.model.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by JZG on 2018/7/30.
 * TopicInfo
 */
public class TopicInfo extends BaseObj implements Parcelable {

    private int kind;
    private int year;
    private int dayOfYear;
    private int postCount;
    private int browseCount;
    private int commentCount;
    private int reportCount;
    private int pointCount;
    private int collectCount;

    public int getKind() {
        return kind;
    }

    public void setKind(int kind) {
        this.kind = kind;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getDayOfYear() {
        return dayOfYear;
    }

    public void setDayOfYear(int dayOfYear) {
        this.dayOfYear = dayOfYear;
    }

    public int getBrowseCount() {
        return browseCount;
    }

    public void setBrowseCount(int browseCount) {
        this.browseCount = browseCount;
    }

    public int getPostCount() {
        return postCount;
    }

    public void setPostCount(int postCount) {
        this.postCount = postCount;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
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

    public int getCollectCount() {
        return collectCount;
    }

    public void setCollectCount(int collectCount) {
        this.collectCount = collectCount;
    }

    protected TopicInfo(Parcel in) {
        super(in);
        kind = in.readInt();
        year = in.readInt();
        dayOfYear = in.readInt();
        browseCount = in.readInt();
        postCount = in.readInt();
        commentCount = in.readInt();
        reportCount = in.readInt();
        pointCount = in.readInt();
        collectCount = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(kind);
        dest.writeInt(year);
        dest.writeInt(dayOfYear);
        dest.writeInt(browseCount);
        dest.writeInt(postCount);
        dest.writeInt(commentCount);
        dest.writeInt(reportCount);
        dest.writeInt(pointCount);
        dest.writeInt(collectCount);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<TopicInfo> CREATOR = new Creator<TopicInfo>() {
        @Override
        public TopicInfo createFromParcel(Parcel in) {
            return new TopicInfo(in);
        }

        @Override
        public TopicInfo[] newArray(int size) {
            return new TopicInfo[size];
        }
    };

}
