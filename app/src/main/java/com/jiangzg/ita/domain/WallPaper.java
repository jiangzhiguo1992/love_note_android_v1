package com.jiangzg.ita.domain;

import java.util.List;

/**
 * Created by JZG on 2018/3/22.
 * 墙纸
 */
public class WallPaper extends BaseObj {

    private long coupleId;
    private List<String> imageList;

    public long getCoupleId() {
        return coupleId;
    }

    public void setCoupleId(long coupleId) {
        this.coupleId = coupleId;
    }

    public List<String> getImageList() {
        return imageList;
    }

    public void setImageList(List<String> imageList) {
        this.imageList = imageList;
    }

}
