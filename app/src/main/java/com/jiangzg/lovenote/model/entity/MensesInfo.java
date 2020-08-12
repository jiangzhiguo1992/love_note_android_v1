package com.jiangzg.lovenote.model.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by JZG on 2018/10/13.
 * MensesInfo
 */
public class MensesInfo implements Parcelable {

    private boolean canMe;
    private boolean canTa;
    private MensesLength mensesLengthMe;
    private MensesLength mensesLengthTa;

    public boolean isCanMe() {
        return canMe;
    }

    public void setCanMe(boolean canMe) {
        this.canMe = canMe;
    }

    public boolean isCanTa() {
        return canTa;
    }

    public void setCanTa(boolean canTa) {
        this.canTa = canTa;
    }

    public MensesLength getMensesLengthMe() {
        return mensesLengthMe;
    }

    public void setMensesLengthMe(MensesLength mensesLengthMe) {
        this.mensesLengthMe = mensesLengthMe;
    }

    public MensesLength getMensesLengthTa() {
        return mensesLengthTa;
    }

    public void setMensesLengthTa(MensesLength mensesLengthTa) {
        this.mensesLengthTa = mensesLengthTa;
    }

    public MensesInfo() {
    }

    protected MensesInfo(Parcel in) {
        canMe = in.readByte() != 0;
        canTa = in.readByte() != 0;
        mensesLengthMe = in.readParcelable(MensesLength.class.getClassLoader());
        mensesLengthTa = in.readParcelable(MensesLength.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (canMe ? 1 : 0));
        dest.writeByte((byte) (canTa ? 1 : 0));
        dest.writeParcelable(mensesLengthMe, flags);
        dest.writeParcelable(mensesLengthTa, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MensesInfo> CREATOR = new Creator<MensesInfo>() {
        @Override
        public MensesInfo createFromParcel(Parcel in) {
            return new MensesInfo(in);
        }

        @Override
        public MensesInfo[] newArray(int size) {
            return new MensesInfo[size];
        }
    };
}
