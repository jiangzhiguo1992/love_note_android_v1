package com.jiangzg.ita.domain;

import java.util.List;

/**
 * Created by JZG on 2018/3/14.
 * 意见评论
 */
public class SuggestComment extends BaseObj {

    private long suggestId;
    private boolean mine;
    private List<String> tagList;
    private String contentText;

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

    public List<String> getTagList() {
        return tagList;
    }

    public void setTagList(List<String> tagList) {
        this.tagList = tagList;
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
