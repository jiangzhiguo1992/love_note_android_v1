package com.jiangzg.ita.utils;

import com.jiangzg.depend.utils.RetroAPI;
import com.jiangzg.ita.domain.Version;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by gg on 2017/4/9.
 * describe Retrofit接口
 */
public interface API extends RetroAPI {

    String HOST = "192.168.0.1";
    String HTTP_HOST = "http://" + HOST + "/";
    String BASE_URL = HTTP_HOST + "api/v1/zh-CN/"; // BaseURL最好以/结尾
    String IMG_URL_ = ""; // 图片前缀
    String WEB_URL_ = ""; // 网站前缀

    @GET("checkUpdate/{version}")
    Call<Version> checkUpdate(@Path("version") int version);

}
