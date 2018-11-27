package com.jiangzg.lovenote.domain;

import com.jiangzg.lovenote.base.BaseCP;

/**
 * Created by JZG on 2018/7/23.
 * PostCommentPoint
 */
public class PostCommentPoint extends BaseCP {

    private long postCommentId;

    public long getPostCommentId() {
        return postCommentId;
    }

    public void setPostCommentId(long postCommentId) {
        this.postCommentId = postCommentId;
    }
}
