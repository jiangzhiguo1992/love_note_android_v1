package com.jiangzg.ita.third;

import com.jiangzg.ita.domain.Result;
import com.jiangzg.ita.domain.RxEvent;
import com.jiangzg.ita.domain.User;

import java.util.List;
import java.util.Map;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.HeaderMap;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * Created by JiangZhiGuo on 2016/10/14.
 * describe Retrofit接口
 */
public interface API {

    //String HOST = "192.168.1.82:30011";
    String HOST = "47.94.224.110";
    String HTTP_HOST = "http://" + HOST + "/ita/";
    String BASE_URL = HTTP_HOST + "api/v1/zh-CN/"; // BaseURL最好以/结尾
    String IMG_URL_ = ""; // 图片前缀
    String WEB_URL_ = ""; // 网站前缀

    @Streaming // 下载大文件(请求需要放在子线程中)
    @Multipart // 上传文件
    @GET("demo/{path}")
    Call<List<RxEvent>> demo(@Url String url, @Path("path") String path, // {path}
                             @Header("key") String key, @HeaderMap Map<String, String> headers,
                             @Query("limit") String limit, @QueryMap Map<String, String> options,
                             @Part("name") String value, @PartMap Map<String, RequestBody> params,
                             @Body RxEvent event, @Body String requestBody);

    @Streaming
    @GET
    Call<ResponseBody> downloadLargeFile(@Url String url);

    @GET("version")
    Call<Result> checkUpdate(@Query("code") int limit);

    @POST("user/login")
    Call<Result> userLogin(@Body User user);

    @POST("validate/{type}")
    Call<Result> validate(@Path("type") int type, @Body User user);

}
