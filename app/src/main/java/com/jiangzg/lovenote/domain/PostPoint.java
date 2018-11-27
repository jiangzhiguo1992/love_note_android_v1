package com.jiangzg.lovenote.domain;

import com.jiangzg.lovenote.base.BaseCP;

/**
 * Created by JZG on 2018/7/23.
 * PostPoint
 */
public class PostPoint extends BaseCP {

    private long postId;

    public long getPostId() {
        return postId;
    }

    public void setPostId(long postId) {
        this.postId = postId;
    }
}
