package com.jiangzg.project.utils;

import com.android.depend.utils.RetroAPI;
import com.jiangzg.project.domain.Version;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by gg on 2017/4/9.
 * describe Retrofit接口
 */
public interface API extends RetroAPI {

    String HOST = "192.168.0.1";
    String API_HOST = "http://" + HOST + "/";
    String BASE_URL = API_HOST + "api/v1/zh-CN/"; // BaseURL最好以/结尾
    String IMG_URL_ = ""; // 图片前缀
    String WEB_URL_ = ""; // 网站前缀

    @GET("checkUpdate/{version}")
    Call<Version> checkUpdate(@Path("version") int version);

}
