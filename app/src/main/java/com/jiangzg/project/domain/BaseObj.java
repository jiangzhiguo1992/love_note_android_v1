package com.jiangzg.project.domain;

import java.io.Serializable;

/**
 * Created by gg on 2017/2/27.
 * 基类
 */
public class BaseObj implements Serializable {

    private long updatedAt;
    private long createdAt;
    private int status;

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
