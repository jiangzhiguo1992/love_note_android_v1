package com.jiangzg.mianmian.domain;

/**
 * Created by JZG on 2018/4/25.
 * Whisper
 */
public class Whisper extends BaseCP {

    private String channel;
    private String content;
    private boolean imgType;

    public boolean isImgType() {
        return imgType;
    }

    public void setImgType(boolean imgType) {
        this.imgType = imgType;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
