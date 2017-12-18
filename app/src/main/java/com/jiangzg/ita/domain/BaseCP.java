package com.jiangzg.ita.domain;

/**
 * Created by JZG on 2017/12/18.
 * BaseCP
 */

public class BaseCP extends BaseObj {

    private long userId;
    private long coupleId;

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getCoupleId() {
        return coupleId;
    }

    public void setCoupleId(long coupleId) {
        this.coupleId = coupleId;
    }
}
