package com.jiangzg.mianmian.domain;

import java.util.List;

/**
 * Created by JZG on 2018/7/23.
 * PostKindInfo
 */
public class PostKindInfo {

    private int id;
    private boolean enable;
    private String name;
    private List<PostSubKindInfo> postSubKindInfoList;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public List<PostSubKindInfo> getPostSubKindInfoList() {
        return postSubKindInfoList;
    }

    public void setPostSubKindInfoList(List<PostSubKindInfo> postSubKindInfoList) {
        this.postSubKindInfoList = postSubKindInfoList;
    }
}
