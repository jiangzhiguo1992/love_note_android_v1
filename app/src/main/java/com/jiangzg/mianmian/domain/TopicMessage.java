package com.jiangzg.mianmian.domain;

/**
 * Created by JZG on 2018/7/23.
 * TopicMessage
 */
public class TopicMessage extends BaseCP {

    private int kind;
    private long contentId;
    // 关联
    private Couple couple;

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
