package com.jiangzg.ita.domain;

/**
 * Created by JZG on 2018/3/14.
 * 意见评论
 */
public class SuggestComment extends BaseObj {

    private boolean mine;
    private boolean official;
    private String contentText;

    public boolean isMine() {
        return mine;
    }

    public void setMine(boolean mine) {
        this.mine = mine;
    }

    public boolean isOfficial() {
        return official;
    }

    public void setOfficial(boolean official) {
        this.official = official;
    }

    public String getContentText() {
        return contentText;
    }

    public void setContentText(String contentText) {
        this.contentText = contentText;
    }
}
