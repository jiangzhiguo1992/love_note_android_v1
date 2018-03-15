package com.jiangzg.ita.domain;

import com.jiangzg.base.component.application.AppContext;
import com.jiangzg.ita.R;

import java.util.List;

/**
 * Created by JZG on 2018/3/12.
 * 意见反馈
 */
public class Suggest extends BaseObj {

    // status
    public static final int STATUE_REPLY_NO = 1; // 未回复
    public static final int STATUE_REPLY_YES = 2; // 已回复
    public static final int STATUE_ACCEPT_NO = 3; // 未采纳
    public static final int STATUE_ACCEPT_YES = 4; // 已采纳
    public static final int STATUE_HANDLE_ING = 5; // 处理中
    public static final int STATUE_HANDLE_OVER = 6; // 处理完
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
                show = AppContext.get().getString(R.string.program_error);
                break;
            case TYPE_FUNCTION:
                show = AppContext.get().getString(R.string.function_add);
                break;
            case TYPE_EXPERIENCE:
                show = AppContext.get().getString(R.string.experience_optimize);
                break;
            case TYPE_OTHER:
            default:
                show = AppContext.get().getString(R.string.other);
                break;
        }
        return show;
    }

    public String getStatusShow() {
        String show;
        switch (status) {
            default:
            case STATUE_REPLY_NO:
                show = AppContext.get().getString(R.string.no_reply);
                break;
            case STATUE_REPLY_YES:
                show = AppContext.get().getString(R.string.already_reply);
                break;
            case STATUE_ACCEPT_NO:
                show = AppContext.get().getString(R.string.no_accept);
                break;
            case STATUE_ACCEPT_YES:
                show = AppContext.get().getString(R.string.already_accept);
                break;
            case STATUE_HANDLE_ING:
                show = AppContext.get().getString(R.string.handle_ing);
                break;
            case STATUE_HANDLE_OVER:
                show = AppContext.get().getString(R.string.handle_over);
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
