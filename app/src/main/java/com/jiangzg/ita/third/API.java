package com.jiangzg.ita.third;

import com.jiangzg.ita.domain.Entry;
import com.jiangzg.ita.domain.Result;
import com.jiangzg.ita.domain.RxEvent;
import com.jiangzg.ita.domain.Sms;
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
import retrofit2.http.PUT;
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

    String HOST = "10.0.2.2:30011";
    //String HOST = "47.94.224.110:30011";
    String HTTP_HOST = "http://" + HOST;
    String BASE_URL = HTTP_HOST + "/api/v1/zh-CN/"; // BaseURL最好以/结尾

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
    Call<ResponseBody> download(@Url String url);

    // 短信
    @POST("sms")
    Call<Result> smsSend(@Body Sms sms);

    // 用户注册
    @POST("user")
    Call<Result> userRegister(@Body User user);

    @PUT("user")
    Call<Result> userModify(@Body User user);

    @POST("user/login")
    Call<Result> userLogin(@Body User user);

    // 三个地方会用到 welcome login userInfo
    @POST("entry")
    Call<Result> entryPush(@Body Entry entry);

    @GET("version")
    Call<Result> checkUpdate(@Query("code") int code);

    @GET("oss")
    Call<Result> ossGet();

}
