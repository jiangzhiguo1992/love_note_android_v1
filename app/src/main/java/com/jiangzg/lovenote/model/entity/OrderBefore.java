package com.jiangzg.lovenote.model.entity;

/**
 * Created by JZG on 2018/10/13.
 * OrderBefore
 */
public class OrderBefore {

    private int platform;
    private String aliOrder;
    private WXOrder wxOrder;

    public int getPlatform() {
        return platform;
    }

    public void setPlatform(int platform) {
        this.platform = platform;
    }

    public String getAliOrder() {
        return aliOrder;
    }

    public void setAliOrder(String aliOrder) {
        this.aliOrder = aliOrder;
    }

    public WXOrder getWxOrder() {
        return wxOrder;
    }

    public void setWxOrder(WXOrder wxOrder) {
        this.wxOrder = wxOrder;
    }
}
