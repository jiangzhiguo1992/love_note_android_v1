package com.jiangzg.mianmian.domain;

import android.os.Parcel;
import android.os.Parcelable;

import com.jiangzg.mianmian.R;
import com.jiangzg.mianmian.base.MyApp;
import com.jiangzg.mianmian.helper.ListHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by JZG on 2018/7/23.
 * Post
 */
public class Post extends BaseCP implements Parcelable {

    private int kind;
    private int subKind;
    private String title;
    private String contentText;
    private double longitude;
    private double latitude;
    private String address;
    private String cityId;
    private boolean top;
    private boolean official;
    private boolean well;
    private int reportCount;
    private int readCount;
    private int pointCount;
    private int collectCount;
    private int commentCount;
    // 关联
    private List<String> contentImageList;
    private boolean screen;
    private boolean hot;
    private Couple couple;
    private boolean mine;
    private boolean our;
    private boolean report;
    private boolean read;
    private boolean point;
    private boolean collect;
    private boolean comment;

    public static String getShowCount(int count) {
        int unit = 10000; // 万
        if (count < unit) {
            return String.valueOf(count);
        }
        return String.format(Locale.getDefault(), MyApp.get().getString(R.string.holder_thousand), count / unit);
    }

    public Couple getCouple() {
        return couple;
    }

    public void setCouple(Couple couple) {
        this.couple = couple;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public int getKind() {
        return kind;
    }

    public void setKind(int kind) {
        this.kind = kind;
    }

    public int getSubKind() {
        return subKind;
    }

    public void setSubKind(int subKind) {
        this.subKind = subKind;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContentText() {
        return contentText;
    }

    public void setContentText(String contentText) {
        this.contentText = contentText;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    public boolean isTop() {
        return top;
    }

    public void setTop(boolean top) {
        this.top = top;
    }

    public boolean isOfficial() {
        return official;
    }

    public void setOfficial(boolean official) {
        this.official = official;
    }

    public boolean isWell() {
        return well;
    }

    public void setWell(boolean well) {
        this.well = well;
    }

    public int getReportCount() {
        return reportCount;
    }

    public void setReportCount(int reportCount) {
        this.reportCount = reportCount;
    }

    public int getReadCount() {
        return readCount;
    }

    public void setReadCount(int readCount) {
        this.readCount = readCount;
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

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public List<String> getContentImageList() {
        return contentImageList;
    }

    public void setContentImageList(List<String> contentImageList) {
        this.contentImageList = contentImageList;
    }

    public boolean isScreen() {
        return screen;
    }

    public void setScreen(boolean screen) {
        this.screen = screen;
    }

    public boolean isHot() {
        return hot;
    }

    public void setHot(boolean hot) {
        this.hot = hot;
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

    public boolean isCollect() {
        return collect;
    }

    public void setCollect(boolean collect) {
        this.collect = collect;
    }

    public boolean isComment() {
        return comment;
    }

    public void setComment(boolean comment) {
        this.comment = comment;
    }

    public Post() {

    }

    protected Post(Parcel in) {
        super(in);
        kind = in.readInt();
        subKind = in.readInt();
        title = in.readString();
        contentText = in.readString();
        longitude = in.readDouble();
        latitude = in.readDouble();
        address = in.readString();
        cityId = in.readString();
        top = in.readByte() != 0;
        official = in.readByte() != 0;
        well = in.readByte() != 0;
        reportCount = in.readInt();
        readCount = in.readInt();
        pointCount = in.readInt();
        collectCount = in.readInt();
        commentCount = in.readInt();
        contentImageList = in.createStringArrayList();
        screen = in.readByte() != 0;
        hot = in.readByte() != 0;
        couple = in.readParcelable(Couple.class.getClassLoader());
        mine = in.readByte() != 0;
        our = in.readByte() != 0;
        report = in.readByte() != 0;
        read = in.readByte() != 0;
        point = in.readByte() != 0;
        collect = in.readByte() != 0;
        comment = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(kind);
        dest.writeInt(subKind);
        dest.writeString(title);
        dest.writeString(contentText);
        dest.writeDouble(longitude);
        dest.writeDouble(latitude);
        dest.writeString(address);
        dest.writeString(cityId);
        dest.writeByte((byte) (top ? 1 : 0));
        dest.writeByte((byte) (official ? 1 : 0));
        dest.writeByte((byte) (well ? 1 : 0));
        dest.writeInt(reportCount);
        dest.writeInt(readCount);
        dest.writeInt(pointCount);
        dest.writeInt(collectCount);
        dest.writeInt(commentCount);
        dest.writeStringList(contentImageList);
        dest.writeByte((byte) (screen ? 1 : 0));
        dest.writeByte((byte) (hot ? 1 : 0));
        dest.writeParcelable(couple, flags);
        dest.writeByte((byte) (mine ? 1 : 0));
        dest.writeByte((byte) (our ? 1 : 0));
        dest.writeByte((byte) (report ? 1 : 0));
        dest.writeByte((byte) (read ? 1 : 0));
        dest.writeByte((byte) (point ? 1 : 0));
        dest.writeByte((byte) (collect ? 1 : 0));
        dest.writeByte((byte) (comment ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Post> CREATOR = new Creator<Post>() {
        @Override
        public Post createFromParcel(Parcel in) {
            return new Post(in);
        }

        @Override
        public Post[] newArray(int size) {
            return new Post[size];
        }
    };

}
