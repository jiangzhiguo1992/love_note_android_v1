package com.jiangzg.lovenote_admin.domain;

import android.os.Parcel;
import android.os.Parcelable;

import com.jiangzg.base.common.StringUtils;
import com.jiangzg.lovenote_admin.R;
import com.jiangzg.lovenote_admin.base.MyApp;

/**
 * Created by JZG on 2017/12/18.
 * Couple
 */

public class Couple extends BaseObj implements Parcelable {

    public static final int STATUS_INVITE = 0; // 正在邀请(SelfVisible)
    public static final int STATUS_INVITE_CANCEL = 110; // 邀请者撤回(NoVisible)
    public static final int STATUS_INVITE_REJECT = 120; // 被邀请者拒绝(NoVisible)
    public static final int STATUS_BREAK = 210; // 正在分手(Visible)/已分手(NoVisible)
    public static final int STATUS_BREAK_ACCEPT = 220; // 被分手者同意(NoVisible)
    public static final int STATUS_TOGETHER = 520; // 在一起(Visible)

    private long creatorId; // 创建者id
    private long inviteeId; // 受邀者id
    private String creatorName; // 创建者昵称(对方修改)
    private String inviteeName; // 受邀者昵称(对方修改)
    private String creatorAvatar; // 创建者头像(对方修改)
    private String inviteeAvatar; // 受邀者头像(对方修改)
    private State state; // cp状态

    public static boolean isEmpty(Couple couple) {
        return (couple == null || couple.getId() == 0 || couple.getCreatorId() == 0 || couple.getInviteeId() == 0);
    }

    public static long getTaId(Couple couple, long mid) {
        if (Couple.isEmpty(couple)) return 0;
        if (mid == couple.getCreatorId()) {
            return couple.getInviteeId();
        } else {
            return couple.getCreatorId();
        }
    }

    public static String getName(Couple couple, long uid) {
        if (Couple.isEmpty(couple)) return "";
        if (uid == couple.getCreatorId()) {
            String creatorName = couple.getCreatorName();
            return StringUtils.isEmpty(creatorName) ? MyApp.get().getString(R.string.now_null_nickname) : creatorName;
        } else {
            String inviteeName = couple.getInviteeName();
            return StringUtils.isEmpty(inviteeName) ? MyApp.get().getString(R.string.now_null_nickname) : inviteeName;
        }
    }

    public static String getAvatar(Couple couple, long uid) {
        if (Couple.isEmpty(couple)) return "";
        if (uid == couple.getCreatorId()) {
            return couple.getCreatorAvatar();
        } else {
            return couple.getInviteeAvatar();
        }
    }

    public static String getStateShow(int state) {
        switch (state) {
            case STATUS_INVITE:
                return "正在邀请";
            case STATUS_INVITE_CANCEL:
                return "邀请者撤回";
            case STATUS_INVITE_REJECT:
                return "被邀请者拒绝";
            case STATUS_BREAK:
                return "正在分手";
            case STATUS_BREAK_ACCEPT:
                return "被分手者同意";
            case STATUS_TOGETHER:
                return "在一起";
        }
        return String.valueOf(state);
    }

    public static class State extends BaseCP implements Parcelable {

        private int state;

        public State() {
        }

        protected State(Parcel in) {
            super(in);
            state = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeInt(state);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<State> CREATOR = new Creator<State>() {
            @Override
            public State createFromParcel(Parcel in) {
                return new State(in);
            }

            @Override
            public State[] newArray(int size) {
                return new State[size];
            }
        };

        public int getState() {
            return state;
        }

        public void setState(int state) {
            this.state = state;
        }

    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
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
        state = in.readParcelable(State.class.getClassLoader());
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
