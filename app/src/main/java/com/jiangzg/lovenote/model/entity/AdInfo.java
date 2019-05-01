package com.jiangzg.lovenote.model.entity;

/**
 * Created by JZG on 2019-04-26.
 */
public class AdInfo {

    private String appId;
    private String topicPostPosId;
    private int topicPostJump;
    private int topicPostStart;

    public int getTopicPostStart() {
        return topicPostStart;
    }

    public void setTopicPostStart(int topicPostStart) {
        this.topicPostStart = topicPostStart;
    }

    public int getTopicPostJump() {
        return topicPostJump;
    }

    public void setTopicPostJump(int topicPostJump) {
        this.topicPostJump = topicPostJump;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getTopicPostPosId() {
        return topicPostPosId;
    }

    public void setTopicPostPosId(String topicPostPosId) {
        this.topicPostPosId = topicPostPosId;
    }
}
