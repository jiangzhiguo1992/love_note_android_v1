package com.jiangzg.mianmian.domain;

/**
 * Created by JZG on 2018/7/23.
 * TopicMessage
 */
public class TopicMessage extends BaseCP {

    public static final int KIND_ALL = 0;
    public static final int KIND_OFFICIAL_TEXT = 1;
    public static final int KIND_POST_BE_REPORT = 10;
    public static final int KIND_POST_BE_POINT = 11;
    public static final int KIND_POST_BE_COLLECT = 12;
    public static final int KIND_POST_BE_COMMENT = 13;
    public static final int KIND_COMMENT_BE_REPLY = 20;
    public static final int KIND_COMMENT_BE_REPORT = 21;
    public static final int KIND_COMMENT_BE_POINT = 22;

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
