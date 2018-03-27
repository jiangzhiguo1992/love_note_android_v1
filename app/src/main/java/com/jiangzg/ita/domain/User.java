package com.jiangzg.ita.domain;

import com.jiangzg.base.common.EncryptUtils;

/**
 * Created by JiangZhiGuo on 2016/9/30.
 * describe 用户实体类
 */
public class User extends BaseObj {

    // 用户登录类型
    public static final int LOG_PWD = 1;
    public static final int LOG_VER = 2;

    // 性别
    public static final int SEX_GIRL = 1;
    public static final int SEX_BOY = 2;

    private String phone;
    private String password; // 客户端加密
    private int sex; // 0女生，1男生
    private long birthday;
    private String userToken;
    //实体
    private Couple couple;
    //http
    private String validateCode;
    private int type;

    public static User getRegister(String phone, String pwd, String validateCode) {
        User user = new User();
        user.setPhone(phone);
        String md5Pwd = EncryptUtils.encryptMD5ToString(pwd);
        user.setPassword(md5Pwd);
        user.setValidateCode(validateCode);
        return user;
    }

    public static User getLogin(String phone, String pwd, String validateCode, int type) {
        User user = new User();
        user.setPhone(phone);
        String md5Pwd = EncryptUtils.encryptMD5ToString(pwd);
        user.setPassword(md5Pwd);
        user.setValidateCode(validateCode);
        user.setType(type);
        return user;
    }

    public String getValidateCode() {
        return validateCode;
    }

    public void setValidateCode(String validateCode) {
        this.validateCode = validateCode;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public long getBirthday() {
        return birthday;
    }

    public void setBirthday(long birthday) {
        this.birthday = birthday;
    }

    public Couple getCouple() {
        return couple;
    }

    public void setCouple(Couple couple) {
        this.couple = couple;
    }

    public String getUserToken() {
        return userToken;
    }

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }
}
