package com.jiangzg.lovenote.model.entity;

/**
 * Created by JZG on 2018/7/23.
 * TopicMessage
 */
public class TopicMessage extends BaseCP {

    public static final int KIND_ALL = 0;
    public static final int KIND_OFFICIAL_TEXT = 1;
    public static final int KIND_JAB_IN_POST = 10;
    public static final int KIND_JAB_IN_COMMENT = 11;
    public static final int KIND_POST_BE_REPORT = 20;
    public static final int KIND_POST_BE_POINT = 21;
    public static final int KIND_POST_BE_COLLECT = 22;
    public static final int KIND_POST_BE_COMMENT = 23;
    public static final int KIND_COMMENT_BE_REPLY = 30;
    public static final int KIND_COMMENT_BE_REPORT = 31;
    public static final int KIND_COMMENT_BE_POINT = 32;

    private int kind;
    private String contentText;
    private long contentId;
    // 关联
    private Couple couple;

    public String getContentText() {
        return contentText;
    }

    public void setContentText(String contentText) {
        this.contentText = contentText;
    }

    public int getKind() {
        return kind;
    }

    public void setKind(int kind) {
        this.kind = kind;
    }

    public long getContentId() {
        return contentId;
    }

    public void setContentId(long contentId) {
        this.contentId = contentId;
    }

    public Couple getCouple() {
        return couple;
    }

    public void setCouple(Couple couple) {
        this.couple = couple;
    }
}
