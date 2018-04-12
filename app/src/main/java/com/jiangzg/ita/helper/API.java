package com.jiangzg.ita.helper;

import com.jiangzg.ita.domain.Entry;
import com.jiangzg.ita.domain.Result;
import com.jiangzg.ita.domain.RxEvent;
import com.jiangzg.ita.domain.Sms;
import com.jiangzg.ita.domain.Suggest;
import com.jiangzg.ita.domain.SuggestComment;
import com.jiangzg.ita.domain.User;

import java.util.List;
import java.util.Map;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
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

    // 帮助文档
    @GET("help")
    Call<Result> helpGet(@Query("content_type") int contentType);

    // 用户注册
    @POST("user")
    Call<Result> userRegister(@Body User user);

    // 用户登录
    @POST("user/login")
    Call<Result> userLogin(@Body User user);

    // 用户修改
    @PUT("user")
    Call<Result> userModify(@Body User user);

    // 用户查询
    @GET("user")
    Call<Result> userGet(@Query("ta") boolean ta);

    // app开启 welcome login userInfo forget
    @POST("entry")
    Call<Result> entryPush(@Body Entry entry);

    // 版本
    @GET("version")
    Call<Result> checkUpdate(@Query("code") int code);

    // oss
    @GET("oss")
    Call<Result> ossGet();

    // 配对邀请
    @POST("couple")
    Call<Result> coupleInvitee(@Body User user); // 主要用到user里phone

    // 配对更新
    @PUT("couple")
    Call<Result> coupleUpdate(@Body User user); // 主要用到user里的type和couple

    // 配对查询
    @GET("couple")
    Call<Result> coupleGet(@Query("self") boolean self, @Query("phone") String phone,
                           @Query("cid") long cid, @Query("uid") long uid);

    // 意见发布
    @POST("suggest")
    Call<Result> suggestAdd(@Body Suggest suggest);

    // 意见删除
    @DELETE("suggest")
    Call<Result> suggestDel(@Query("sid") long suggestId);

    // 意见单个获取
    @GET("suggest?list=0")
    Call<Result> suggestGet(@Query("sid") long suggestId);

    // 意见列表获取
    @GET("suggest?list=1&sid=0")
    Call<Result> suggestListGet(@Query("page") int page);

    // 意见评论发表
    @POST("suggest/comment")
    Call<Result> suggestCommentAdd(@Body SuggestComment suggestComment);

    // 意见评论删除
    @DELETE("suggest/comment")
    Call<Result> suggestCommentDel(@Query("sid") long suggestCommentId);

    // 意见评论获取
    @GET("suggest/comment")
    Call<Result> suggestCommentListGet(@Query("sid") long suggestId, @Query("page") int page);

    // 意见关注
    @POST("suggest/follow")
    Call<Result> suggestFollowToggle(@Query("sid") long suggestId);


}
