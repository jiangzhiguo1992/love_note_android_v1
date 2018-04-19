package com.jiangzg.ita.domain;

import java.util.List;

/**
 * Created by JZG on 2018/3/22.
 * 墙纸
 */
public class WallPaper extends BaseObj {

    private List<String> imageList;

    public List<String> getImageList() {
        return imageList;
    }

    public void setImageList(List<String> imageList) {
        this.imageList = imageList;
    }

}
