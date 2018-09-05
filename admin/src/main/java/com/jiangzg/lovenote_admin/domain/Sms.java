package com.jiangzg.lovenote_admin.domain;

/**
 * Created by JZG on 2018/3/27.
 * 短信
 */
public class Sms extends BaseObj {

    // 验证码类型
    public static final int TYPE_REGISTER = 10;
    public static final int TYPE_LOGIN = 11;
    public static final int TYPE_FORGET = 12;
    public static final int TYPE_PHONE = 13;
    public static final int TYPE_LOCK = 30;

    private String phone;
    private int SendType;
    private String Content;

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

}
