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
    private int sendType;
    private String content;

    public static String getTypeShow(int type) {
        switch (type) {
            case TYPE_REGISTER:
                return "注册";
            case TYPE_LOGIN:
                return "登录";
            case TYPE_FORGET:
                return "忘记密码";
            case TYPE_PHONE:
                return "修改手机";
            case TYPE_LOCK:
                return "密码锁";
        }
        return String.valueOf(type);
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getSendType() {
        return sendType;
    }

    public void setSendType(int sendType) {
        this.sendType = sendType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
