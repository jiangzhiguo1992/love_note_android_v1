package com.jiangzg.ita.domain;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by JZG on 2018/3/27.
 * 短信
 */
public class Sms extends BaseObj implements Parcelable {
    // 验证码类型
    public static final int TYPE_REGISTER = 1;
    public static final int TYPE_LOGIN = 2;
    public static final int TYPE_FORGET = 3;
    public static final int TYPE_PHONE = 4;

    private String phone;
    private int SendType;
    private String Content;

    public static Sms getLoginBody(String phone) {
        Sms sms = new Sms();
        sms.setSendType(TYPE_LOGIN);
        sms.setPhone(phone);
        return sms;
    }

    public static Sms getRegisterBody(String phone) {
        Sms sms = new Sms();
        sms.setSendType(TYPE_REGISTER);
        sms.setPhone(phone);
        return sms;
    }

    public static Sms getForgetBody(String phone) {
        Sms sms = new Sms();
        sms.setSendType(TYPE_FORGET);
        sms.setPhone(phone);
        return sms;
    }

    public static Sms getPhoneBody(String phone) {
        Sms sms = new Sms();
        sms.setSendType(TYPE_PHONE);
        sms.setPhone(phone);
        return sms;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getSendType() {
        return SendType;
    }

    public void setSendType(int sendType) {
        SendType = sendType;
    }

    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        Content = content;
    }

    public Sms() {
    }

    protected Sms(Parcel in) {
        super(in);
        phone = in.readString();
        SendType = in.readInt();
        Content = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(phone);
        dest.writeInt(SendType);
        dest.writeString(Content);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Sms> CREATOR = new Creator<Sms>() {
        @Override
        public Sms createFromParcel(Parcel in) {
            return new Sms(in);
        }

        @Override
        public Sms[] newArray(int size) {
            return new Sms[size];
        }
    };

}
