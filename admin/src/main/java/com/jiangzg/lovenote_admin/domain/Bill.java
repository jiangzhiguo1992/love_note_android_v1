package com.jiangzg.lovenote_admin.domain;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by JZG on 2018/9/13.
 */
public class Bill extends BaseCP implements Parcelable {

    private String platformOs;
    private int platformPay;
    private int payType;
    private double payAmount;
    private String tradeNo;
    private boolean tradePay;
    private int goodsType;
    private boolean goodsOut;

    public String getPlatformOs() {
        return platformOs;
    }

    public void setPlatformOs(String platformOs) {
        this.platformOs = platformOs;
    }

    public int getPlatformPay() {
        return platformPay;
    }

    public void setPlatformPay(int platformPay) {
        this.platformPay = platformPay;
    }

    public int getPayType() {
        return payType;
    }

    public void setPayType(int payType) {
        this.payType = payType;
    }

    public double getPayAmount() {
        return payAmount;
    }

    public void setPayAmount(double payAmount) {
        this.payAmount = payAmount;
    }

    public String getTradeNo() {
        return tradeNo;
    }

    public void setTradeNo(String tradeNo) {
        this.tradeNo = tradeNo;
    }

    public boolean isTradePay() {
        return tradePay;
    }

    public void setTradePay(boolean tradePay) {
        this.tradePay = tradePay;
    }

    public int getGoodsType() {
        return goodsType;
    }

    public void setGoodsType(int goodsType) {
        this.goodsType = goodsType;
    }

    public boolean isGoodsOut() {
        return goodsOut;
    }

    public void setGoodsOut(boolean goodsOut) {
        this.goodsOut = goodsOut;
    }

    public Bill() {
    }

    protected Bill(Parcel in) {
        super(in);
        platformOs = in.readString();
        platformPay = in.readInt();
        payType = in.readInt();
        payAmount = in.readDouble();
        tradeNo = in.readString();
        tradePay = in.readByte() != 0;
        goodsType = in.readInt();
        goodsOut = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(platformOs);
        dest.writeInt(platformPay);
        dest.writeInt(payType);
        dest.writeDouble(payAmount);
        dest.writeString(tradeNo);
        dest.writeByte((byte) (tradePay ? 1 : 0));
        dest.writeInt(goodsType);
        dest.writeByte((byte) (goodsOut ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Bill> CREATOR = new Creator<Bill>() {
        @Override
        public Bill createFromParcel(Parcel in) {
            return new Bill(in);
        }

        @Override
        public Bill[] newArray(int size) {
            return new Bill[size];
        }
    };
}
