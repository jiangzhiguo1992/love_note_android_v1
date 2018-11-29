package com.jiangzg.lovenote.model.entity;

import com.jiangzg.lovenote.base.BaseCP;

/**
 * Created by JZG on 2018/8/17.
 * AwardScore
 */
public class AwardScore extends BaseCP {

    private int changeCount;
    private long totalScore;

    public int getChangeCount() {
        return changeCount;
    }

    public void setChangeCount(int changeCount) {
        this.changeCount = changeCount;
    }

    public long getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(long totalScore) {
        this.totalScore = totalScore;
    }
}
