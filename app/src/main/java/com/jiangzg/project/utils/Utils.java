package com.jiangzg.project.utils;

/**
 * Created by gg on 2017/5/3.
 * 常用工具类
 */
public class Utils {

    public static String getImgUrl(String url) {
        String foreUrl = "";
        String imgUrl;
        if (url.startsWith("http")) {
            imgUrl = url;
        } else {
            imgUrl = foreUrl + url;
        }
        return imgUrl;
    }

}
