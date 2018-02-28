package com.jiangzg.ita.domain;

import java.io.Serializable;

/**
 * Created by gg on 2017/2/27.
 * 基类
 */
public class BaseObj implements Serializable {

    public static final int STATUS_NOL = 0;
    public static final int STATUS_DEL = -1;

    private long id;
    private int status;
    private long updatedAt;
    private long createdAt;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
