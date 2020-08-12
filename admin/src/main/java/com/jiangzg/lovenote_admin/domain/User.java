package com.jiangzg.lovenote_admin.domain;

import android.os.Parcel;
import android.os.Parcelable;

import com.jiangzg.lovenote_admin.R;
import com.jiangzg.lovenote_admin.base.MyApp;

/**
 * Created by JiangZhiGuo on 2016/9/30.
 * describe 用户实体类
 */
public class User extends BaseObj implements Parcelable {

    // 性别
    public static final int SEX_GIRL = 1;
    public static final int SEX_BOY = 2;

    private String phone;
    private String password;
    private int sex;
    private long birthday;
    private String userToken;
    // 实体
    private Couple couple;

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
        return "人妖";
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
