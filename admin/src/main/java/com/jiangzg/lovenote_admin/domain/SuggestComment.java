package com.jiangzg.lovenote_admin.domain;

/**
 * Created by JZG on 2018/3/14.
 * 意见评论
 */
public class SuggestComment extends BaseObj {

    private long suggestId;
    private String contentText;
    // tag标签
    private boolean official;
    private boolean mine;

    public boolean isOfficial() {
        return official;
    }

    public void setOfficial(boolean official) {
        this.official = official;
    }

    public long getSuggestId() {
        return suggestId;
    }

    public void setSuggestId(long suggestId) {
        this.suggestId = suggestId;
    }

    public boolean isMine() {
        return mine;
    }

    public void setMine(boolean mine) {
        this.mine = mine;
    }

    public String getContentText() {
        return contentText;
    }

    public void setContentText(String contentText) {
        this.contentText = contentText;
    }

    public SuggestComment() {
    }

}
