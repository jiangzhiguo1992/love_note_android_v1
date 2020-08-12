package com.jiangzg.lovenote.model.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by JZG on 2018/12/15.
 */
public class CoupleState extends BaseCP implements Parcelable {

    public static final int STATUS_INVITE = 0; // 正在邀请(SelfVisible)
    public static final int STATUS_INVITE_CANCEL = 110; // 邀请者撤回(NoVisible)
    public static final int STATUS_INVITE_REJECT = 120; // 被邀请者拒绝(NoVisible)
    public static final int STATUS_BREAK = 210; // 正在分手(Visible)/已分手(NoVisible)
    public static final int STATUS_BREAK_ACCEPT = 220; // 被分手者同意(NoVisible)
    public static final int STATUS_TOGETHER = 520; // 在一起(Visible)

    private int state;

    public CoupleState() {
    }

    protected CoupleState(Parcel in) {
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

    public static final Creator<CoupleState> CREATOR = new Creator<CoupleState>() {
        @Override
        public CoupleState createFromParcel(Parcel in) {
            return new CoupleState(in);
        }

        @Override
        public CoupleState[] newArray(int size) {
            return new CoupleState[size];
        }
    };

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

}
