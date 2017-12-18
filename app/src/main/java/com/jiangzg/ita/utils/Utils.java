package com.jiangzg.ita.utils;

import com.jiangzg.base.common.StringUtils;

/**
 * Created by gg on 2017/5/3.
 * 常用工具类
 */
public class Utils {

    public static boolean noLogin() {
        String userToken = UserUtils.getUser().getUserToken();
        return StringUtils.isEmpty(userToken);
    }

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
