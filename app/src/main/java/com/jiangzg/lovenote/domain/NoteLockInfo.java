package com.jiangzg.lovenote.domain;

/**
 * Created by JZG on 2018/10/13.
 * NoteLockInfo
 */
public class NoteLockInfo {

    private boolean canLock;
    private boolean isLock;

    public boolean isCanLock() {
        return canLock;
    }

    public void setCanLock(boolean canLock) {
        this.canLock = canLock;
    }

    public boolean isLock() {
        return isLock;
    }

    public void setLock(boolean lock) {
        isLock = lock;
    }
}
