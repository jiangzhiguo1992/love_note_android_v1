package com.jiangzg.lovenote.model.entity;

import java.util.List;

/**
 * Created by JZG on 2018/3/22.
 * 墙纸
 */
public class WallPaper extends BaseObj {

    private List<String> contentImageList;

    public List<String> getContentImageList() {
        return contentImageList;
    }

    public void setContentImageList(List<String> contentImageList) {
        this.contentImageList = contentImageList;
    }

}
