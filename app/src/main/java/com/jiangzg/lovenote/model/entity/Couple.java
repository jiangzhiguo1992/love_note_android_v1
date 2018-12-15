package com.jiangzg.lovenote.model.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.jiangzg.lovenote.base.BaseObj;

/**
 * Created by JZG on 2017/12/18.
 * Couple
 */

public class Couple extends BaseObj implements Parcelable {

    private long creatorId;
    private long inviteeId;
    private String creatorName;
    private String inviteeName;
    private String creatorAvatar;
    private String inviteeAvatar;
    private CoupleState state;

    public CoupleState getState() {
        return state;
    }

    public void setState(CoupleState state) {
        this.state = state;
    }

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
        state = in.readParcelable(CoupleState.class.getClassLoader());
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
        dest.writeParcelable(state, flags);
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
