package com.jiangzg.lovenote.model.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by JZG on 2018/7/2.
 * TravelFood
 */
public class TravelFood extends BaseCP implements Parcelable {

    private long foodId;
    private Food food;

    public long getFoodId() {
        return foodId;
    }

    public void setFoodId(long foodId) {
        this.foodId = foodId;
    }

    public Food getFood() {
        return food;
    }

    public void setFood(Food food) {
        this.food = food;
    }

    public TravelFood() {
    }

    protected TravelFood(Parcel in) {
        super(in);
        foodId = in.readLong();
        food = in.readParcelable(Food.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeLong(foodId);
        dest.writeParcelable(food, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<TravelFood> CREATOR = new Creator<TravelFood>() {
        @Override
        public TravelFood createFromParcel(Parcel in) {
            return new TravelFood(in);
        }

        @Override
        public TravelFood[] newArray(int size) {
            return new TravelFood[size];
        }
    };

}
