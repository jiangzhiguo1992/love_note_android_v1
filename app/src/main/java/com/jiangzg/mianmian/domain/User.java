package com.jiangzg.mianmian.domain;

import android.os.Parcel;
import android.os.Parcelable;

import com.jiangzg.base.common.StringUtils;
import com.jiangzg.mianmian.R;
import com.jiangzg.mianmian.base.MyApp;
import com.jiangzg.mianmian.helper.SPHelper;

/**
 * Created by JiangZhiGuo on 2016/9/30.
 * describe 用户实体类
 */
public class User extends BaseObj implements Parcelable {

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

    // 是否登录
    public static boolean noLogin() {
        String userToken = SPHelper.getMe().getUserToken();
        return StringUtils.isEmpty(userToken);
    }

    // 信息是否完善
    public static boolean canUserInfo() {
        User user = SPHelper.getMe();
        int sex = user.getSex();
        long birthday = user.getBirthday();
        boolean noSex = sex != User.SEX_BOY && sex != User.SEX_GIRL;
        boolean noBirth = birthday == 0;
        return noSex || noBirth;
    }

    // 性别
    public String getSexShow() {
        return getSexShow(this.getSex());
    }

    public static String getSexShow(int sex) {
        if (sex == User.SEX_GIRL) {
            return MyApp.get().getString(R.string.girl);
        } else if (sex == User.SEX_BOY) {
            return MyApp.get().getString(R.string.boy);
        }
        return "";
    }

    public int getSexResCircleSmall() {
        return getSexResCircleSmall(this.getSex());
    }

    public static int getSexResCircleSmall(int sex) {
        if (sex == User.SEX_BOY) {
            return R.mipmap.ic_sex_boy_circle;
        } else if (sex == User.SEX_GIRL) {
            return R.mipmap.ic_sex_girl_circle;
        }
        return 0;
    }

    // ta的id
    public long getTaId() {
        Couple couple = getCouple();
        return Couple.getTaId(couple, this.getId());
    }

    // 名称
    public String getMyNameInCp() {
        return getNameInCp(this.getId());
    }

    public String getTaNameInCp() {
        return getNameInCp(getTaId());
    }

    public String getNameInCp(long uid) {
        Couple couple = getCouple();
        return Couple.getName(couple, uid);
    }

    // 头像
    public String getMyAvatarInCp() {
        return getAvatarInCp(this.getId());
    }

    public String getTaAvatarInCp() {
        return getAvatarInCp(getTaId());
    }

    public String getAvatarInCp(long uid) {
        Couple couple = getCouple();
        return Couple.getAvatar(couple, uid);
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
