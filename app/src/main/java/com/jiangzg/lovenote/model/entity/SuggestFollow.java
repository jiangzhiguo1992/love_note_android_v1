package com.jiangzg.lovenote.model.entity;

/**
 * Created by JZG on 2018/3/14.
 * 意见关注
 */
public class SuggestFollow extends BaseObj {

    private long suggestId;

    public long getSuggestId() {
        return suggestId;
    }

    public void setSuggestId(long suggestId) {
        this.suggestId = suggestId;
    }

    public SuggestFollow(long suggestId) {
        this.suggestId = suggestId;
    }
}
