package com.jiangzg.ita.domain;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by JZG on 2017/12/18.
 * Couple
 */

public class Couple extends BaseObj implements Parcelable {

    public static final int CoupleStatusInviteeReject = -2;// 不可见，邀请失败
    public static final int CoupleStatusComplexReject = -1; // 复合才可见，复合失败
    public static final int CoupleStatusBreak = 0; // 自己可见/复合才可见，正在分手/已分手
    public static final int CoupleStatusInvitee = 1;  // 只有自己可见，正在邀请
    public static final int CoupleStatusComplex = 2; // 只有自己可见，正在复合
    public static final int CoupleStatusTogether = 3; // 所有人可见，邀请成功，复合成功

    private long creatorId; //创建者id
    private long inviteeId; //受邀者id
    private String creatorName; //创建者昵称(对方修改)
    private String inviteeName; //受邀者昵称(对方修改)
    private String creatorAvatar; //创建者头像(对方修改)
    private String inviteeAvatar; //受邀者头像(对方修改)

    public long getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(long creatorId) {
        this.creatorId = creatorId;
    }

    public long getInviteeId() {
        return inviteeId;
    }

    public void setInviteeId(long inviteeId) {
        this.inviteeId = inviteeId;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public String getInviteeName() {
        return inviteeName;
    }

    public void setInviteeName(String inviteeName) {
        this.inviteeName = inviteeName;
    }

    public String getCreatorAvatar() {
        return creatorAvatar;
    }

    public void setCreatorAvatar(String creatorAvatar) {
        this.creatorAvatar = creatorAvatar;
    }

    public String getInviteeAvatar() {
        return inviteeAvatar;
    }

    public void setInviteeAvatar(String inviteeAvatar) {
        this.inviteeAvatar = inviteeAvatar;
    }

    public Couple() {
    }

    protected Couple(Parcel in) {
        super(in);
        creatorId = in.readLong();
        inviteeId = in.readLong();
        creatorName = in.readString();
        inviteeName = in.readString();
        creatorAvatar = in.readString();
        inviteeAvatar = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeLong(creatorId);
        dest.writeLong(inviteeId);
        dest.writeString(creatorName);
        dest.writeString(inviteeName);
        dest.writeString(creatorAvatar);
        dest.writeString(inviteeAvatar);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Couple> CREATOR = new Creator<Couple>() {
        @Override
        public Couple createFromParcel(Parcel in) {
            return new Couple(in);
        }

        @Override
        public Couple[] newArray(int size) {
            return new Couple[size];
        }
    };

}
