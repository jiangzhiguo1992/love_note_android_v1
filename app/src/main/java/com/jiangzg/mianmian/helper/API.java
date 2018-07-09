package com.jiangzg.mianmian.helper;

import com.jiangzg.mianmian.domain.Album;
import com.jiangzg.mianmian.domain.Angry;
import com.jiangzg.mianmian.domain.Audio;
import com.jiangzg.mianmian.domain.Award;
import com.jiangzg.mianmian.domain.AwardRule;
import com.jiangzg.mianmian.domain.Diary;
import com.jiangzg.mianmian.domain.Dream;
import com.jiangzg.mianmian.domain.Entry;
import com.jiangzg.mianmian.domain.Food;
import com.jiangzg.mianmian.domain.Gift;
import com.jiangzg.mianmian.domain.Menses;
import com.jiangzg.mianmian.domain.Picture;
import com.jiangzg.mianmian.domain.PictureList;
import com.jiangzg.mianmian.domain.Place;
import com.jiangzg.mianmian.domain.Promise;
import com.jiangzg.mianmian.domain.PromiseBreak;
import com.jiangzg.mianmian.domain.Result;
import com.jiangzg.mianmian.domain.Shy;
import com.jiangzg.mianmian.domain.Sleep;
import com.jiangzg.mianmian.domain.Sms;
import com.jiangzg.mianmian.domain.Suggest;
import com.jiangzg.mianmian.domain.SuggestComment;
import com.jiangzg.mianmian.domain.Travel;
import com.jiangzg.mianmian.domain.User;
import com.jiangzg.mianmian.domain.Video;
import com.jiangzg.mianmian.domain.WallPaper;
import com.jiangzg.mianmian.domain.Whisper;
import com.jiangzg.mianmian.domain.Word;

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
    //String HOST = "47.94.224.110:30011";
    String BASE_URL = "http://" + HOST + "/api/v1/"; // BaseURL最好以/结尾

    @Streaming // 下载大文件(请求需要放在子线程中)
    @Multipart // 上传文件
    @GET("demo/{path}")
    Call<List<Object>> demo(@Url String url, @Path("path") String path, // {path}
                            @Header("key") String key, @HeaderMap Map<String, String> headers,
                            @Query("limit") String limit, @QueryMap Map<String, String> options,
                            @Part("name") String value, @PartMap Map<String, RequestBody> params,
                            @Body Object body, @Body String bodyJson);

    // 短信
    @POST("sms")
    Call<Result> smsSend(@Body Sms sms);

    // app开启 welcome login userInfo forget
    @POST("entry")
    Call<Result> entryPush(@Body Entry entry);

    // oss
    @GET("oss")
    Call<Result> ossGet();

    // 用户注册
    @POST("user")
    Call<Result> userRegister(@Body User user);

    // 用户登录
    @POST("user/login")
    Call<Result> userLogin(@Body User user);

    // 用户修改
    @PUT("user")
    Call<Result> userModify(@Body User user);

    // 用户获取
    @GET("user")
    Call<Result> userGet(@Query("ta") boolean ta);

    // 帮助文档
    @GET("set/help")
    Call<Result> helpGet(@Query("index") int contentType);

    // 版本
    @GET("set/version")
    Call<Result> checkUpdate(@Query("code") int code);

    // 公告列表获取
    @GET("set/notice?list=1")
    Call<Result> noticeListGet(@Query("page") int page);

    // 公告阅读
    @PUT("set/notice")
    Call<Result> noticeRead(@Query("nid") long nid);

    // 意见发布
    @POST("set/suggest")
    Call<Result> suggestAdd(@Body Suggest suggest);

    // 意见删除
    @DELETE("set/suggest")
    Call<Result> suggestDel(@Query("sid") long suggestId);

    // 意见单个获取
    @GET("set/suggest")
    Call<Result> suggestGet(@Query("sid") long suggestId);

    // 意见列表获取
    @GET("set/suggest?list=1")
    Call<Result> suggestListHomeGet(@Query("status") int status, @Query("content_type") int contentType, @Query("page") int page);

    // 意见列表获取
    @GET("set/suggest?list=1&mine=1")
    Call<Result> suggestListMineGet(@Query("page") int page);

    // 意见列表获取
    @GET("set/suggest?list=1&follow=1")
    Call<Result> suggestListFollowGet(@Query("page") int page);

    // 意见评论发表
    @POST("set/suggest/comment")
    Call<Result> suggestCommentAdd(@Body SuggestComment suggestComment);

    // 意见评论删除
    @DELETE("set/suggest/comment")
    Call<Result> suggestCommentDel(@Query("sid") long suggestCommentId);

    // 意见评论获取
    @GET("set/suggest/comment")
    Call<Result> suggestCommentListGet(@Query("sid") long suggestId, @Query("page") int page);

    // 意见关注
    @POST("set/suggest/follow")
    Call<Result> suggestFollowToggle(@Query("sid") long suggestId);

    // 配对邀请
    @POST("couple")
    Call<Result> coupleInvitee(@Body User user);

    // 配对更新
    @PUT("couple")
    Call<Result> coupleUpdate(@Body User user);

    // 配对查询
    @GET("couple")
    Call<Result> coupleGet(@Query("self") boolean self, @Query("uid") long uid);

    // cp首页
    @GET("couple/home")
    Call<Result> coupleHomeGet();

    // 添加墙纸
    @POST("couple/wallPaper")
    Call<Result> coupleWallPaperUpdate(@Body WallPaper wallPaper);

    // 获取墙纸
    @GET("couple/wallPaper")
    Call<Result> coupleWallPaperGet();

    // 推送位置
    @POST("couple/place")
    Call<Result> couplePlacePush(@Body Place place);

    // 获取位置
    @GET("couple/place")
    Call<Result> couplePlaceListGet(@Query("page") int page);

    // 天气预报
    @GET("couple/weather?forecast=1")
    Call<Result> weatherForecastListGet();

    // word获取
    @GET("book/word?list=1")
    Call<Result> wordListGet(@Query("page") int page);

    // word上传
    @POST("book/word")
    Call<Result> wordAdd(@Body Word word);

    // word删除
    @DELETE("book/word")
    Call<Result> wordDel(@Query("wid") long wid);

    // whisper获取
    @GET("book/whisper?list=1")
    Call<Result> whisperListGet(@Query("channel") String channel, @Query("page") int page);

    // whisper上传
    @POST("book/whisper")
    Call<Result> whisperAdd(@Body Whisper whisper);

    // diaryList获取
    @GET("book/diary?did=0&list=1")
    Call<Result> diaryListGet(@Query("who") int who, @Query("page") int page);

    // diary获取
    @GET("book/diary?list=0")
    Call<Result> diaryGet(@Query("did") long did);

    // diary上传
    @POST("book/diary")
    Call<Result> diaryAdd(@Body Diary diary);

    // diary删除
    @DELETE("book/diary")
    Call<Result> diaryDel(@Query("did") long did);

    // diary修改
    @PUT("book/diary")
    Call<Result> diaryUpdate(@Body Diary diary);

    // albumList获取
    @GET("book/album?aid=0&list=1")
    Call<Result> AlbumListGet(@Query("page") int page);

    // album获取
    @GET("book/album?list=0")
    Call<Result> AlbumGet(@Query("aid") long aid);

    // album上传
    @POST("book/album")
    Call<Result> AlbumAdd(@Body Album album);

    // album删除
    @DELETE("book/album")
    Call<Result> AlbumDel(@Query("aid") long aid);

    // album修改
    @PUT("book/album")
    Call<Result> AlbumUpdate(@Body Album Album);

    // picture列表获取
    @GET("book/picture")
    Call<Result> PictureListGet(@Query("aid") long aid, @Query("page") int page);

    // picture上传
    @POST("book/picture")
    Call<Result> pictureListAdd(@Body PictureList pictureList);

    // picture删除
    @DELETE("book/picture")
    Call<Result> pictureDel(@Query("pid") long pid);

    // picture上传
    @PUT("book/picture")
    Call<Result> pictureUpdate(@Body Picture picture);

    // giftList获取
    @GET("book/gift?gid=0&list=1")
    Call<Result> giftListGet(@Query("who") int who, @Query("page") int page);

    // gift上传
    @POST("book/gift")
    Call<Result> giftAdd(@Body Gift gift);

    // gift删除
    @DELETE("book/gift")
    Call<Result> giftDel(@Query("gid") long gid);

    // gift修改
    @PUT("book/gift")
    Call<Result> giftUpdate(@Body Gift gift);

    // promise获取
    @GET("book/promise?pid=0&list=1")
    Call<Result> promiseListGet(@Query("who") int who, @Query("page") int page);

    // promise获取
    @GET("book/promise?list=0")
    Call<Result> promiseGet(@Query("pid") long pid);

    // promise上传
    @POST("book/promise")
    Call<Result> promiseAdd(@Body Promise promise);

    // promise删除
    @DELETE("book/promise")
    Call<Result> promiseDel(@Query("pid") long did);

    // promise修改
    @PUT("book/promise")
    Call<Result> promiseUpdate(@Body Promise promise);

    // promiseBreak获取
    @GET("book/promise/break?pdid=0")
    Call<Result> promiseBreakListGet(@Query("pid") long pid, @Query("page") int page);

    // promiseBreak上传
    @POST("book/promise/break")
    Call<Result> promiseBreakAdd(@Body PromiseBreak promiseBreak);

    // promiseBreak删除
    @DELETE("book/promise/break")
    Call<Result> promiseBreakDel(@Query("pbid") long pbid);

    // angryList获取
    @GET("book/angry?aid=0&list=1")
    Call<Result> angryListGet(@Query("who") int who, @Query("page") int page);

    // angry获取
    @GET("book/angry?list=0")
    Call<Result> angryGet(@Query("aid") long aid);

    // angry上传
    @POST("book/angry")
    Call<Result> angryAdd(@Body Angry angry);

    // angry删除
    @DELETE("book/angry")
    Call<Result> angryDel(@Query("aid") long aid);

    // angry修改
    @PUT("book/angry")
    Call<Result> angryUpdate(@Body Angry angry);

    // dreamList获取
    @GET("book/dream?did=0&list=1")
    Call<Result> dreamListGet(@Query("who") int who, @Query("page") int page);

    // dream获取
    @GET("book/dream?list=0")
    Call<Result> dreamGet(@Query("did") long did);

    // dream上传
    @POST("book/dream")
    Call<Result> dreamAdd(@Body Dream dream);

    // dream删除
    @DELETE("book/dream")
    Call<Result> dreamDel(@Query("did") long did);

    // dream修改
    @PUT("book/dream")
    Call<Result> dreamUpdate(@Body Dream dream);

    // award获取
    @GET("book/award?aid=0&score=0&list=1")
    Call<Result> awardListGet(@Query("who") int who, @Query("page") int page);

    // award获取
    @GET("book/award?aid=0&list=0&score=1")
    Call<Result> awardScoreGet();

    // award上传
    @POST("book/award")
    Call<Result> awardAdd(@Body Award award);

    // award删除
    @DELETE("book/award")
    Call<Result> awardDel(@Query("aid") long aid);

    // awardRule获取
    @GET("book/award/rule?arid=0&list=1")
    Call<Result> awardRuleListGet(@Query("page") int page);

    // awardRule上传
    @POST("book/award/rule")
    Call<Result> awardRuleAdd(@Body AwardRule awardRule);

    // awardRule删除
    @DELETE("book/award/rule")
    Call<Result> awardRuleDel(@Query("arid") long arid);

    // foodList获取
    @GET("book/food?fid=0&list=1")
    Call<Result> foodListGet(@Query("page") int page);

    // food上传
    @POST("book/food")
    Call<Result> foodAdd(@Body Food food);

    // food删除
    @DELETE("book/food")
    Call<Result> foodDel(@Query("fid") long fid);

    // travelList获取
    @GET("book/travel?tid=0&list=1")
    Call<Result> travelListGet(@Query("page") int page);

    // travel获取
    @GET("book/travel?list=0")
    Call<Result> travelGet(@Query("tid") long tid);

    // travel上传
    @POST("book/travel")
    Call<Result> travelAdd(@Body Travel travel);

    // travel删除
    @DELETE("book/travel")
    Call<Result> travelDel(@Query("tid") long tid);

    // travel修改
    @PUT("book/travel")
    Call<Result> travelUpdate(@Body Travel travel);

    // audioList获取
    @GET("book/audio?aid=0&list=1")
    Call<Result> audioListGet(@Query("page") int page);

    // audio上传
    @POST("book/audio")
    Call<Result> audioAdd(@Body Audio audio);

    // audio删除
    @DELETE("book/audio")
    Call<Result> audioDel(@Query("aid") long aid);

    // videoList获取
    @GET("book/video?vid=0&list=1")
    Call<Result> videoListGet(@Query("page") int page);

    // video上传
    @POST("book/video")
    Call<Result> videoAdd(@Body Video video);

    // video删除
    @DELETE("book/video")
    Call<Result> videoDel(@Query("vid") long vid);

    // sleepList获取
    @GET("book/sleep?latest=0&date=1")
    Call<Result> sleepListGetByDate(@Query("year") int year, @Query("month") int month);

    // sleep获取
    @GET("book/sleep?latest=1&date=0")
    Call<Result> sleepLatestGet();

    // sleep上传
    @POST("book/sleep")
    Call<Result> sleepAdd(@Body Sleep sleep);

    // shyList获取
    @GET("book/shy?date=1")
    Call<Result> shyListGetByDate(@Query("year") int year, @Query("month") int month);

    // shy上传
    @POST("book/shy")
    Call<Result> shyAdd(@Body Shy shy);

    // mensesList获取
    @GET("book/menses?latest=0&date=1")
    Call<Result> mensesListGetByDate(@Query("mine") boolean mine, @Query("year") int year, @Query("month") int month);

    // menses获取
    @GET("book/menses?latest=1&date=0")
    Call<Result> mensesLatestGet();

    // menses上传
    @POST("book/menses")
    Call<Result> mensesAdd(@Body Menses menses);

}
