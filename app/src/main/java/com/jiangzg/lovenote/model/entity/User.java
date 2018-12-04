package com.jiangzg.lovenote.model.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.jiangzg.lovenote.base.BaseObj;
import com.jiangzg.lovenote.helper.UserHelper;

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

    // 名称
    public String getMyNameInCp() {
        return getNameInCp(this.getId());
    }

    public String getTaNameInCp() {
        return getNameInCp(getTaId());
    }

    public String getNameInCp(long uid) {
        Couple couple = getCouple();
        return UserHelper.getName(couple, uid);
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
        return UserHelper.getAvatar(couple, uid);
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
