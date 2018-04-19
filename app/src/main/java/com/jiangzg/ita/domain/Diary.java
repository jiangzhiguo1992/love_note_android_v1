package com.jiangzg.ita.domain;

import java.util.List;

/**
 * Created by JZG on 2018/4/19.
 * Diary
 */
public class Diary extends BaseCP {

    private long happenAt;
    private String content;
    private List<String> imageList;

    public long getHappenAt() {
        return happenAt;
    }

    public void setHappenAt(long happenAt) {
        this.happenAt = happenAt;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<String> getImageList() {
        return imageList;
    }

    public void setImageList(List<String> imageList) {
        this.imageList = imageList;
    }
}
