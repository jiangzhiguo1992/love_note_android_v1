package com.jiangzg.ita.domain;

import android.os.Parcel;
import android.os.Parcelable;

import com.jiangzg.ita.R;
import com.jiangzg.ita.base.MyApp;
import com.jiangzg.ita.helper.CheckHelper;

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

    public String getSexShow() {
        int sex = getSex();
        if (sex == SEX_GIRL) {
            return MyApp.get().getString(R.string.girl);
        } else if (sex == SEX_BOY) {
            return MyApp.get().getString(R.string.boy);
        }
        return "";
    }

    public String getMyNameInCp() {
        Couple couple = getCouple();
        if (CheckHelper.isNullCouple(couple)) return "";
        if (this.getId() == couple.getCreatorId()) {
            return couple.getCreatorName();
        } else {
            return couple.getInviteeName();
        }
    }

    public String getTaNameInCp() {
        Couple couple = getCouple();
        if (CheckHelper.isNullCouple(couple)) return "";
        if (this.getId() == couple.getCreatorId()) {
            return couple.getInviteeName();
        } else {
            return couple.getCreatorName();
        }
    }

    public String getMyAvatarInCp() {
        Couple couple = getCouple();
        if (CheckHelper.isNullCouple(couple)) return "";
        if (this.getId() == couple.getCreatorId()) {
            return couple.getCreatorAvatar();
        } else {
            return couple.getInviteeAvatar();
        }
    }

    public String getTaAvatarInCp() {
        Couple couple = getCouple();
        if (CheckHelper.isNullCouple(couple)) return "";
        if (this.getId() == couple.getCreatorId()) {
            return couple.getInviteeAvatar();
        } else {
            return couple.getCreatorAvatar();
        }
    }

    public boolean isCoupleCreator() {
        Couple couple = getCouple();
        return !CheckHelper.isNullCouple(couple) && couple.getCreatorId() == this.getId();
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
