package com.jiangzg.lovenote_admin.helper;

import com.jiangzg.lovenote_admin.domain.Broadcast;
import com.jiangzg.lovenote_admin.domain.Notice;
import com.jiangzg.lovenote_admin.domain.Result;
import com.jiangzg.lovenote_admin.domain.Version;

import java.util.List;
import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
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

    String HOST = "192.168.18.5:30011";
    //String HOST = "lovenote.api.jiangzhiguo.com";
    String BASE_URL = "http://" + HOST + "/v1/"; // BaseURL最好以/结尾

    @Streaming // 下载大文件(请求需要放在子线程中)
    @Multipart // 上传文件
    @GET("demo/{path}")
    Call<List<Object>> demo(@Url String url, @Path("path") String path, // {path}
                            @Header("key") String key, @HeaderMap Map<String, String> headers,
                            @Query("limit") String limit, @QueryMap Map<String, String> options,
                            @Part("name") String value, @PartMap Map<String, RequestBody> params,
                            @Body Object body, @Body String bodyJson);

    @GET("user/login")
    Call<Result> userLogin(@Query("phone") String phone, @Query("pwd") String pwd);

    @GET("oss?admin=1")
    Call<Result> ossGet();

    @GET("user")
    Call<Result> userGetById(@Query("uid") long uid);

    @GET("user")
    Call<Result> userGetByPhone(@Query("phone") String phone);

    @GET("user?list=1")
    Call<Result> userListGet(@Query("page") int page);

    @GET("user?count=1")
    Call<Result> userCountGet(@Query("create") long create, @Query("sex") int sex, @Query("start") long start, @Query("end") long end);

    @GET("sms?list=1&count=0")
    Call<Result> smsListGet(@Query("start") long start, @Query("end") long end, @Query("phone") String phone, @Query("type") int type, @Query("page") int page);

    @GET("sms?list=0&count=1")
    Call<Result> smsCountGet(@Query("start") long start, @Query("end") long end, @Query("phone") String phone, @Query("type") int type);

    @POST("set/version")
    Call<Result> versionAdd(@Body Version version);

    @DELETE("set/version")
    Call<Result> versionDel(@Query("vid") long vid);

    @GET("set/version?list=1&code=0")
    Call<Result> versionListGet(@Query("page") int page);

    @POST("set/notice")
    Call<Result> noticeAdd(@Body Notice notice);

    @DELETE("set/notice")
    Call<Result> noticeDel(@Query("nid") long nid);

    @GET("set/notice?list=0&all=1")
    Call<Result> noticeListGet(@Query("page") int page);

    @POST("more/broadcast")
    Call<Result> broadcastAdd(@Body Broadcast broadcast);

    @DELETE("more/broadcast")
    Call<Result> broadcastDel(@Query("bid") long nid);

    @GET("more/broadcast?list=1")
    Call<Result> broadcastListGet(@Query("page") int page);

}
