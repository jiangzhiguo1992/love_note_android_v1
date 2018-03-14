package com.jiangzg.ita.domain;

import java.util.List;

/**
 * Created by JZG on 2018/3/12.
 * 意见反馈
 */
public class Suggest extends BaseObj {

    // status
    public static final int STATUE_START = 1; // 未回复
    public static final int STATUE_REPLY = 2; // 已回复
    public static final int STATUE_REFUSE = 3; // 未采纳
    public static final int STATUE_ACCEPT = 4; // 已采纳
    public static final int STATUE_DEVELOP = 5; // 处理中
    public static final int STATUE_COMPLETE = 6; // 处理完
    // contentType
    public static final int TYPE_BUG = 1; // 程序错误
    public static final int TYPE_FUNCTION = 2; // 功能添加
    public static final int TYPE_EXPERIENCE = 3; // 体验优化
    public static final int TYPE_OTHER = 4; // 其他

    private String title; // list
    private String contentText; // detail
    private String contentImgUrl; // detail
    private int contentType; // list
    private int followCount; // list
    private int commentCount; // list
    private boolean official; // list
    private boolean top; // list
    private List<SuggestComment> commentList; // detail
    private boolean follow; // list + user
    private boolean comment; // list + user

    public String getTypeShow() {
        String show;
        switch (contentType) {
            case TYPE_BUG:
                show = "程序错误";
                break;
            case TYPE_FUNCTION:
                show = "功能添加";
                break;
            case TYPE_EXPERIENCE:
                show = "体验优化";
                break;
            case TYPE_OTHER:
            default:
                show = "其他";
                break;
        }
        return show;
    }

    public String getStatusShow() {
        String show;
        switch (status) {
            default:
            case STATUE_START:
                show = "未回复";
                break;
            case STATUE_REPLY:
                show = "已回复";
                break;
            case STATUE_REFUSE:
                show = "未采纳";
                break;
            case STATUE_ACCEPT:
                show = "已采纳";
                break;
            case STATUE_DEVELOP:
                show = "处理中";
                break;
            case STATUE_COMPLETE:
                show = "处理完";
                break;
        }
        return show;
    }

    public boolean isOfficial() {
        return official;
    }

    public void setOfficial(boolean official) {
        this.official = official;
    }

    public String getContentImgUrl() {
        return contentImgUrl;
    }

    public void setContentImgUrl(String contentImgUrl) {
        this.contentImgUrl = contentImgUrl;
    }

    public List<SuggestComment> getCommentList() {
        return commentList;
    }

    public void setCommentList(List<SuggestComment> commentList) {
        this.commentList = commentList;
    }

    public boolean isComment() {
        return comment;
    }

    public String getContentText() {
        return contentText;
    }

    public void setContentText(String contentText) {
        this.contentText = contentText;
    }

    public void setComment(boolean comment) {
        this.comment = comment;
    }

    public boolean isFollow() {
        return follow;
    }

    public void setFollow(boolean follow) {
        this.follow = follow;
    }

    public int getFollowCount() {
        return followCount;
    }

    public void setFollowCount(int followCount) {
        this.followCount = followCount;
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

    public int getContentType() {
        return contentType;
    }

    public void setContentType(int contentType) {
        this.contentType = contentType;
    }
}
