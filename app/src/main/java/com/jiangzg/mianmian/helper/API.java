package com.jiangzg.mianmian.helper;

import com.jiangzg.mianmian.domain.Diary;
import com.jiangzg.mianmian.domain.Entry;
import com.jiangzg.mianmian.domain.Place;
import com.jiangzg.mianmian.domain.Result;
import com.jiangzg.mianmian.domain.RxEvent;
import com.jiangzg.mianmian.domain.Sms;
import com.jiangzg.mianmian.domain.Suggest;
import com.jiangzg.mianmian.domain.SuggestComment;
import com.jiangzg.mianmian.domain.User;
import com.jiangzg.mianmian.domain.WallPaper;

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
    //String HOST = "192.168.1.105:30011";
    //String HOST = "47.94.224.110:30011";
    String BASE_URL = "http://" + HOST + "/api/v1/zh-CN/"; // BaseURL最好以/结尾

    @Streaming // 下载大文件(请求需要放在子线程中)
    @Multipart // 上传文件
    @GET("demo/{path}")
    Call<List<RxEvent>> demo(@Url String url, @Path("path") String path, // {path}
                             @Header("key") String key, @HeaderMap Map<String, String> headers,
                             @Query("limit") String limit, @QueryMap Map<String, String> options,
                             @Part("name") String value, @PartMap Map<String, RequestBody> params,
                             @Body RxEvent event, @Body String requestBody);

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

    // TODO 登录历史获取
    @GET("entry")
    Call<Result> entryListGet();

    // 推送登录位置
    @POST("entry/place")
    Call<Result> entryPlacePush(@Body Entry.EntryPlace entryPlace);

    // 版本
    @GET("version")
    Call<Result> checkUpdate(@Query("code") int code);

    // oss
    @GET("oss")
    Call<Result> ossGet();

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
    @GET("suggest?sid=0&list=1&mine=0&follow=0")
    Call<Result> suggestListHomeGet(@Query("status") int status, @Query("contentType") int contentType, @Query("page") int page);

    // 意见列表获取
    @GET("suggest?sid=0&list=1&mine=1&follow=0")
    Call<Result> suggestListMineGet(@Query("page") int page);

    // 意见列表获取
    @GET("suggest?sid=0&list=1&mine=0&follow=1")
    Call<Result> suggestListFollowGet(@Query("page") int page);

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

    // 添加墙纸
    @POST("couple/wallPaper")
    Call<Result> coupleWallPaperUpdate(@Body WallPaper wallPaper);

    // 获取墙纸
    @GET("couple/wallPaper")
    Call<Result> coupleWallPaperGet();

    // cp首页
    @POST("couple/home")
    Call<Result> coupleHomeGet(@Body Place place);

    // diaryList获取
    @GET("book/diary?did=0&list=1")
    Call<Result> diaryListGet(@Query("who") int who, @Query("page") int page);

    // diary获取
    @GET("book/diary?list=0")
    Call<Result> diaryGet(@Query("did") long did);

    // diary上传
    @POST("book/diary")
    Call<Result> diaryPost(@Body Diary diary);

    // diary删除
    @DELETE("book/diary")
    Call<Result> diaryDel(@Query("did") long did);

    // diary修改
    @PUT("book/diary")
    Call<Result> diaryUpdate(@Body Diary diary);

}
