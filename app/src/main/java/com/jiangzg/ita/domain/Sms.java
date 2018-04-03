package com.jiangzg.ita.domain;

/**
 * Created by JZG on 2018/3/27.
 * 短信
 */
public class Sms extends BaseObj {

    // 验证码类型
    public static final int TYPE_REGISTER = 1;
    public static final int TYPE_LOGIN = 2;
    public static final int TYPE_FORGET = 3;
    public static final int TYPE_PHONE = 4;

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
