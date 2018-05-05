package com.jiangzg.mianmian.domain;

/**
 * Created by JZG on 2018/5/5.
 * Notice
 */
public class Notice extends BaseObj {

    private String title;
    private String contentText;
    private String contentUrl;
    private boolean read;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContentText() {
        return contentText;
    }

    public void setContentText(String contentText) {
        this.contentText = contentText;
    }

    public String getContentUrl() {
        return contentUrl;
    }

    public void setContentUrl(String contentUrl) {
        this.contentUrl = contentUrl;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }
}
