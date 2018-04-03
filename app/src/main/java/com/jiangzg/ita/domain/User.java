package com.jiangzg.ita.domain;

import android.os.Parcel;
import android.os.Parcelable;

import com.jiangzg.base.common.EncryptUtils;

/**
 * Created by JiangZhiGuo on 2016/9/30.
 * describe 用户实体类
 */
public class User extends BaseObj implements Parcelable {

    // 用户登录类型
    public static final int LOG_PWD = 1;
    public static final int LOG_VER = 2;
    // 修改类型
    public static final int MODIFY_FORGET = 1;
    public static final int MODIFY_PASSWORD = 2;
    public static final int MODIFY_PHONE = 3;
    public static final int MODIFY_INFO = 4;
    // 性别
    public static final int SEX_GIRL = 1;
    public static final int SEX_BOY = 2;

    private String phone;
    private String password; // 客户端加密
    private int sex; // 0女生，1男生
    private long birthday;
    private String userToken;
    // 实体
    private Couple couple;
    // http
    private String validateCode;
    private int type;
    private String oldPassWord;

    public static User getRegisterBody(String phone, String pwd, String validateCode) {
        User user = new User();
        user.setPhone(phone);
        String md5Pwd = EncryptUtils.encryptMD5ToString(pwd);
        user.setPassword(md5Pwd);
        user.setValidateCode(validateCode);
        return user;
    }

    public static User getForgetBody(String phone, String pwd, String validateCode) {
        User user = new User();
        user.setPhone(phone);
        String md5Pwd = EncryptUtils.encryptMD5ToString(pwd);
        user.setPassword(md5Pwd);
        user.setValidateCode(validateCode);
        user.setType(MODIFY_FORGET);
        return user;
    }

    public static User getLoginBody(String phone, String pwd, String validateCode, int type) {
        User user = new User();
        user.setPhone(phone);
        String md5Pwd = EncryptUtils.encryptMD5ToString(pwd);
        user.setPassword(md5Pwd);
        user.setValidateCode(validateCode);
        user.setType(type);
        return user;
    }

    public static User getPasswordBody(String oldPwd, String newPwd) {
        User user = new User();
        String md5OldPwd = EncryptUtils.encryptMD5ToString(oldPwd);
        user.setOldPassWord(md5OldPwd);
        String md5NewPwd = EncryptUtils.encryptMD5ToString(newPwd);
        user.setPassword(md5NewPwd);
        user.setType(MODIFY_PASSWORD);
        return user;
    }

    public static User getPhoneBody(String phone, String code) {
        User user = new User();
        user.setPhone(phone);
        user.setValidateCode(code);
        user.setType(MODIFY_PHONE);
        return user;
    }

    public static User getInfoBody(int sex, long birthday) {
        User user = new User();
        user.setSex(sex);
        user.setBirthday(birthday);
        user.setType(MODIFY_INFO);
        return user;
    }

    public static User getCoupleUpdate2GoodBody(long coupleId) {
        User user = new User();
        user.setType(Couple.CoupleUpdateGood);
        user.couple = new Couple();
        user.couple.setId(coupleId);
        return user;
    }

    public static User getCoupleUpdate2BadBody(long coupleId) {
        User user = new User();
        user.setType(Couple.CoupleUpdateBad);
        user.couple = new Couple();
        user.couple.setId(coupleId);
        return user;
    }

    public String getMyNameInCp() {
        Couple couple = getCouple();
        if (Couple.isNullCouple(couple)) return "";
        if (this.getId() == couple.getCreatorId()) {
            return couple.getCreatorName();
        } else {
            return couple.getInviteeName();
        }
    }

    public String getTaNameInCp() {
        Couple couple = getCouple();
        if (Couple.isNullCouple(couple)) return "";
        if (this.getId() == couple.getCreatorId()) {
            return couple.getInviteeName();
        } else {
            return couple.getCreatorName();
        }
    }

    public String getMyAvatarInCp() {
        Couple couple = getCouple();
        if (Couple.isNullCouple(couple)) return "";
        if (this.getId() == couple.getCreatorId()) {
            return couple.getCreatorAvatar();
        } else {
            return couple.getInviteeAvatar();
        }
    }

    public String getTaAvatarInCp() {
        Couple couple = getCouple();
        if (Couple.isNullCouple(couple)) return "";
        if (this.getId() == couple.getCreatorId()) {
            return couple.getInviteeAvatar();
        } else {
            return couple.getCreatorAvatar();
        }
    }

    public boolean isCoupleCreator() {
        Couple couple = getCouple();
        if (Couple.isNullCouple(couple)) return false;
        return couple.getCreatorId() == this.getId();
    }

    public String getOldPassWord() {
        return oldPassWord;
    }

    public void setOldPassWord(String oldPassWord) {
        this.oldPassWord = oldPassWord;
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

    public User() {
    }

    protected User(Parcel in) {
        super(in);
        phone = in.readString();
        password = in.readString();
        sex = in.readInt();
        birthday = in.readLong();
        userToken = in.readString();
        couple = in.readParcelable(Couple.class.getClassLoader());
        validateCode = in.readString();
        type = in.readInt();
        oldPassWord = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(phone);
        dest.writeString(password);
        dest.writeInt(sex);
        dest.writeLong(birthday);
        dest.writeString(userToken);
        dest.writeParcelable(couple, flags);
        dest.writeString(validateCode);
        dest.writeInt(type);
        dest.writeString(oldPassWord);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

}
