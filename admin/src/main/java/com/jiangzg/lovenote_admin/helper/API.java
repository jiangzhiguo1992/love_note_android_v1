package com.jiangzg.lovenote_admin.helper;

import com.jiangzg.lovenote_admin.domain.Broadcast;
import com.jiangzg.lovenote_admin.domain.Coin;
import com.jiangzg.lovenote_admin.domain.Notice;
import com.jiangzg.lovenote_admin.domain.Post;
import com.jiangzg.lovenote_admin.domain.Result;
import com.jiangzg.lovenote_admin.domain.Suggest;
import com.jiangzg.lovenote_admin.domain.User;
import com.jiangzg.lovenote_admin.domain.Version;
import com.jiangzg.lovenote_admin.domain.Vip;

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

    @PUT("user")
    Call<Result> userModify(@Query("type") int type, @Body User user);

    @GET("user")
    Call<Result> userGet(@Query("uid") long uid, @Query("phone") String phone);

    @GET("user?list=1")
    Call<Result> userListGet(@Query("page") int page);

    @GET("user?black=1")
    Call<Result> userBlackGet(@Query("page") int page);

    @GET("user?total=1")
    Call<Result> userTotalGet(@Query("start") long start, @Query("end") long end);

    @GET("user?birth=1")
    Call<Result> userBirthGet(@Query("start") long start, @Query("end") long end);

    @GET("sms?list=1")
    Call<Result> smsListGet(@Query("start") long start, @Query("end") long end, @Query("phone") String phone, @Query("type") int type, @Query("page") int page);

    @GET("sms?total=1")
    Call<Result> smsTotalGet(@Query("start") long start, @Query("end") long end, @Query("phone") String phone, @Query("type") int type);

    @GET("entry?list=1")
    Call<Result> entryListGet(@Query("uid") long uid, @Query("page") int page);

    @GET("entry?total=1")
    Call<Result> entryTotalGet(@Query("start") long start, @Query("end") long end);

    @GET("entry?group=1")
    Call<Result> entryGroupGet(@Query("start") long start, @Query("end") long end, @Query("filed") String filed);

    @GET("api?list=1")
    Call<Result> apiListGet(@Query("start") long start, @Query("end") long end, @Query("uid") long uid, @Query("page") int page);

    @GET("api?uri=1")
    Call<Result> apiUriGet(@Query("start") long start, @Query("end") long end);

    @GET("api?total=1")
    Call<Result> apiTotalGet(@Query("start") long start, @Query("end") long end);

    @DELETE("set/suggest")
    Call<Result> setSuggestDel(@Query("sid") long suggestId);

    @PUT("set/suggest")
    Call<Result> setSuggestUpdate(@Body Suggest suggest);

    @GET("set/suggest?follow=1")
    Call<Result> setSuggestFollowListGet(@Query("page") int page);

    @GET("set/suggest?list=1")
    Call<Result> setSuggestListGet(@Query("page") int page);

    @GET("set/suggest")
    Call<Result> setSuggestUserListGet(@Query("uid") long uid, @Query("page") int page);

    @GET("set/suggest?total=1")
    Call<Result> setSuggestTotalGet(@Query("create") long create);

    @DELETE("set/suggest/comment")
    Call<Result> setSuggestCommentDel(@Query("scid") long scid);

    @GET("set/suggest/comment?admin=1")
    Call<Result> setSuggestCommentListGet(@Query("uid") long uid, @Query("sid") long sid, @Query("page") int page);

    @GET("set/suggest/comment?total=1")
    Call<Result> setSuggestCommentTotalGet(@Query("create") long create);

    @GET("couple?list=1&state=0")
    Call<Result> coupleListGet(@Query("uid") long uid, @Query("page") int page);

    @GET("couple?list=1&state=1")
    Call<Result> coupleStateListGet(@Query("cid") long cid, @Query("page") int page);

    @GET("couple?total=1&state=0")
    Call<Result> coupleTotalGet(@Query("start") long start, @Query("end") long end);

    @GET("couple?total=1&state=1")
    Call<Result> coupleStateTotalGet(@Query("start") long start, @Query("end") long end);

    @GET("couple?all=1")
    Call<Result> coupleGet(@Query("uid") long uid, @Query("cid") long cid);

    @GET("couple/place?list=1&admin=1")
    Call<Result> couplePlaceListGet(@Query("uid") long uid, @Query("page") int page);

    @GET("couple/place?group=1")
    Call<Result> couplePlaceGroupGet(@Query("start") long start, @Query("end") long end, @Query("filed") String filed);

    @POST("more/bill?check=1")
    Call<Result> moreBillCheck(@Query("bid") long bid);

    @GET("more/bill?list=1&sync=0")
    Call<Result> moreBillListGet(@Query("uid") long uid, @Query("cid") long cid, @Query("trade_no") String tradeNo, @Query("page") int page);

    @GET("more/bill?list=1&sync=1")
    Call<Result> moreBillSyncListGet(@Query("page") int page);

    @GET("more/bill?amount=1")
    Call<Result> moreBillAmountGet(@Query("start") long start, @Query("end") long end, @Query("platform_os") String platformOs, @Query("platform_pay") int platformPay, @Query("pay_type") int payType, @Query("goods_type") int goodsType);

    @GET("more/bill?total=1")
    Call<Result> moreBillTotalGet(@Query("start") long start, @Query("end") long end, @Query("platform_os") String platformOs, @Query("platform_pay") int platformPay, @Query("pay_type") int payType, @Query("goods_type") int goodsType);

    @POST("more/vip")
    Call<Result> moreVipAdd(@Body Vip vip);

    @GET("more/vip?expire_days=1")
    Call<Result> moreVipExpireDaysListGet(@Query("start") long start, @Query("end") long end);

    @GET("more/vip?total=1")
    Call<Result> moreVipTotalGet(@Query("start") long start, @Query("end") long end);

    @GET("more/vip?list=1&admin=1")
    Call<Result> moreVipListGet(@Query("uid") long uid, @Query("cid") long cid, @Query("bid") long bid, @Query("from_type") int fromType, @Query("page") int page);

    @GET("more/sign?list=1")
    Call<Result> moreSignListGet(@Query("uid") long uid, @Query("cid") long cid, @Query("page") int page);

    @GET("more/sign?total=1")
    Call<Result> moreSignTotalGet(@Query("year") int year, @Query("month") int month, @Query("day") int day);

    @POST("more/coin")
    Call<Result> moreCoinAdd(@Body Coin coin);

    @GET("more/coin?change=1")
    Call<Result> moreCoinChangeListGet(@Query("start") long start, @Query("end") long end);

    @GET("more/coin?total=1")
    Call<Result> moreCoinTotalGet(@Query("start") long start, @Query("end") long end, @Query("kind") int kind);

    @GET("more/coin?list=1&admin=1")
    Call<Result> moreCoinListGet(@Query("uid") long uid, @Query("cid") long cid, @Query("bid") long bid, @Query("kind") int kind, @Query("page") int page);

    @GET("note/trends?list=1&admin=1")
    Call<Result> noteTrendsListGet(@Query("uid") long uid, @Query("cid") long cid, @Query("act_type") int actType, @Query("con_type") int conType, @Query("page") int page);

    @GET("note/trends?group=1")
    Call<Result> noteTrendsConListGet(@Query("start") long start, @Query("end") long end);

    @DELETE("topic/post")
    Call<Result> topicPostDel(@Query("pid") long pid);

    @PUT("topic/post")
    Call<Result> topicPostUpdate(@Body Post post);

    @GET("topic/post?collect=1&me=1")
    Call<Result> topicPostCollectListGet(@Query("page") int page);

    @GET("topic/post?report=1")
    Call<Result> topicPostReportListGet(@Query("page") int page);

    @GET("topic/post?admin=1")
    Call<Result> topicPostListGet(@Query("uid") long uid, @Query("page") int page);

    @DELETE("topic/post/comment")
    Call<Result> topicPostCommentDel(@Query("pcid") long pcid);

    @GET("topic/post/comment?list=1")
    Call<Result> topicPostCommentListGet(@Query("pid") long pid, @Query("order") int order, @Query("page") int page);

    @GET("topic/post/comment?sub_list=1")
    Call<Result> topicPostCommentSubListGet(@Query("pid") long pid, @Query("tcid") long tcid, @Query("order") int order, @Query("page") int page);

    @GET("topic/post/comment")
    Call<Result> topicPostCommentUserListGet(@Query("pid") long pid, @Query("uid") long uid, @Query("order") int order, @Query("page") int page);

    @GET("topic/post/comment?report=1")
    Call<Result> topicPostCommentReportListGet(@Query("pid") long pid, @Query("uid") long uid, @Query("order") int order, @Query("page") int page);

    // TODO match

    @POST("set/version")
    Call<Result> versionAdd(@Body Version version);

    @DELETE("set/version")
    Call<Result> versionDel(@Query("vid") long vid);

    @GET("set/version?list=1")
    Call<Result> versionListGet(@Query("page") int page);

    @POST("set/notice")
    Call<Result> noticeAdd(@Body Notice notice);

    @DELETE("set/notice")
    Call<Result> noticeDel(@Query("nid") long nid);

    @GET("set/notice?all=1")
    Call<Result> noticeListGet(@Query("page") int page);

    @POST("more/broadcast")
    Call<Result> broadcastAdd(@Body Broadcast broadcast);

    @DELETE("more/broadcast")
    Call<Result> broadcastDel(@Query("bid") long nid);

    @GET("more/broadcast?list=1")
    Call<Result> broadcastListGet(@Query("page") int page);

}
