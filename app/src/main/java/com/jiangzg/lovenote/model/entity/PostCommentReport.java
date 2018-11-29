package com.jiangzg.lovenote.model.entity;

import com.jiangzg.lovenote.base.BaseCP;

/**
 * Created by JZG on 2018/7/23.
 * PostCommentReport
 */
public class PostCommentReport extends BaseCP {

    private long postCommentId;

    public long getPostCommentId() {
        return postCommentId;
    }

    public void setPostCommentId(long postCommentId) {
        this.postCommentId = postCommentId;
    }
}
