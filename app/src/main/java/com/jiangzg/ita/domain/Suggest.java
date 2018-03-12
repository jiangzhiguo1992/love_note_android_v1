package com.jiangzg.ita.domain;

/**
 * Created by JZG on 2018/3/12.
 * 意见反馈
 */
public class Suggest extends BaseObj {

    private boolean top;
    private String title;
    private String content;
    private String contentType;
    private String adminReply;
    private boolean watch;
    private boolean comment;
    private int watchCount;
    private int commentCount;

    public boolean isWatch() {
        return watch;
    }

    public void setWatch(boolean watch) {
        this.watch = watch;
    }

    public boolean isComment() {
        return comment;
    }

    public void setComment(boolean comment) {
        this.comment = comment;
    }

    public int getWatchCount() {
        return watchCount;
    }

    public void setWatchCount(int watchCount) {
        this.watchCount = watchCount;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public boolean isTop() {
        return top;
    }

    public void setTop(boolean top) {
        this.top = top;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getAdminReply() {
        return adminReply;
    }

    public void setAdminReply(String adminReply) {
        this.adminReply = adminReply;
    }
}
