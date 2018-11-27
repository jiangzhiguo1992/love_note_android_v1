package com.jiangzg.lovenote.domain;

import android.os.Parcel;
import android.os.Parcelable;

import com.jiangzg.lovenote.base.BaseCP;

/**
 * Created by JZG on 2018/7/10.
 * SouvenirFood
 */
public class SouvenirFood extends BaseCP implements Parcelable {

    private long souvenirId;
    private long foodId;
    private int year;
    private Food food;

    public long getSouvenirId() {
        return souvenirId;
    }

    public void setSouvenirId(long souvenirId) {
        this.souvenirId = souvenirId;
    }

    public long getFoodId() {
        return foodId;
    }

    public void setFoodId(long foodId) {
        this.foodId = foodId;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public Food getFood() {
        return food;
    }

    public void setFood(Food food) {
        this.food = food;
    }

    public SouvenirFood() {
    }

    protected SouvenirFood(Parcel in) {
        super(in);
        souvenirId = in.readLong();
        foodId = in.readLong();
        year = in.readInt();
        food = in.readParcelable(Food.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeLong(souvenirId);
        dest.writeLong(foodId);
        dest.writeInt(year);
        dest.writeParcelable(food, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<SouvenirFood> CREATOR = new Creator<SouvenirFood>() {
        @Override
        public SouvenirFood createFromParcel(Parcel in) {
            return new SouvenirFood(in);
        }

        @Override
        public SouvenirFood[] newArray(int size) {
            return new SouvenirFood[size];
        }
    };
}
