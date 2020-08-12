package com.jiangzg.lovenote_admin.domain;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by JZG on 2018/9/13.
 */
public class Bill extends BaseCP implements Parcelable {

    public static final String BILL_PLATFORM_OS_ANDROID = "android";
    public static final String BILL_PLATFORM_OS_IOS = "ios";
    public static final int BILL_PLATFORM_PAY_ALI = 100;
    public static final int BILL_PLATFORM_PAY_WX = 200;
    public static final int BILL_PAY_TYPE_APP = 1; // app
    public static final int BILL_GOODS_VIP_1 = 1101;
    public static final int BILL_GOODS_VIP_2 = 1201;
    public static final int BILL_GOODS_VIP_3 = 1301;
    public static final int BILL_GOODS_COIN_1 = 2101;
    public static final int BILL_GOODS_COIN_2 = 2201;
    public static final int BILL_GOODS_COIN_3 = 2301;

    private String platformOs;
    private int platformPay;
    private int payType;
    private double payAmount;
    private String tradeNo;
    private boolean tradePay;
    private int goodsType;
    private boolean goodsOut;

    public static String getPayTypeShow(int type) {
        if (type == BILL_PAY_TYPE_APP) {
            return "app";
        }
        return String.valueOf(type);
    }

    public static String getPlatformPayShow(int type) {
        if (type == BILL_PLATFORM_PAY_ALI) {
            return "支付宝";
        } else if (type == BILL_PLATFORM_PAY_WX) {
            return "微信";
        }
        return String.valueOf(type);
    }

    public static String getGoodsTypeShow(int type) {
        if (type == BILL_GOODS_VIP_1) {
            return "会员1";
        } else if (type == BILL_GOODS_VIP_2) {
            return "会员2";
        } else if (type == BILL_GOODS_VIP_3) {
            return "会员3";
        } else if (type == BILL_GOODS_COIN_1) {
            return "金币1";
        } else if (type == BILL_GOODS_COIN_2) {
            return "金币2";
        } else if (type == BILL_GOODS_COIN_3) {
            return "金币3";
        }
        return String.valueOf(type);
    }

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
