package com.jiangzg.mianmian.helper;

import com.jiangzg.mianmian.domain.Album;
import com.jiangzg.mianmian.domain.Angry;
import com.jiangzg.mianmian.domain.Audio;
import com.jiangzg.mianmian.domain.Award;
import com.jiangzg.mianmian.domain.AwardRule;
import com.jiangzg.mianmian.domain.Couple;
import com.jiangzg.mianmian.domain.Diary;
import com.jiangzg.mianmian.domain.Dream;
import com.jiangzg.mianmian.domain.Entry;
import com.jiangzg.mianmian.domain.Food;
import com.jiangzg.mianmian.domain.Gift;
import com.jiangzg.mianmian.domain.Lock;
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
import com.jiangzg.mianmian.domain.Souvenir;
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
    Call<Result> userRegister(@Query("code") String code, @Body User user);

    // 用户修改
    @PUT("user")
    Call<Result> userModify(@Query("type") int type, @Query("code") String code, @Query("old_pwd") String oldPwd, @Body User user);

    // 用户获取ta
    @GET("user?ta=1")
    Call<Result> userGetTa();

    // 用户登录
    @POST("user/login")
    Call<Result> userLogin(@Query("type") int type, @Query("code") String code, @Body User user);

    // 帮助文档
    @GET("set/help")
    Call<Result> setHelpGet(@Query("index") int contentType);

    // 版本
    @GET("set/version")
    Call<Result> setCheckUpdate(@Query("code") int code);

    // 公告列表获取
    @GET("set/notice?list=1")
    Call<Result> setNoticeListGet(@Query("page") int page);

    // 公告阅读
    @PUT("set/notice")
    Call<Result> setNoticeRead(@Query("nid") long nid);

    // 意见发布
    @POST("set/suggest")
    Call<Result> setSuggestAdd(@Body Suggest suggest);

    // 意见删除
    @DELETE("set/suggest")
    Call<Result> setSuggestDel(@Query("sid") long suggestId);

    // 意见单个获取
    @GET("set/suggest")
    Call<Result> setSuggestGet(@Query("sid") long suggestId);

    // 意见列表获取
    @GET("set/suggest?list=1")
    Call<Result> setSuggestListHomeGet(@Query("status") int status, @Query("content_type") int contentType, @Query("page") int page);

    // 意见列表获取
    @GET("set/suggest?list=1&mine=1")
    Call<Result> setSuggestListMineGet(@Query("page") int page);

    // 意见列表获取
    @GET("set/suggest?list=1&follow=1")
    Call<Result> setSuggestListFollowGet(@Query("page") int page);

    // 意见评论发表
    @POST("set/suggest/comment")
    Call<Result> setSuggestCommentAdd(@Body SuggestComment suggestComment);

    // 意见评论删除
    @DELETE("set/suggest/comment")
    Call<Result> setSuggestCommentDel(@Query("sid") long suggestCommentId);

    // 意见评论获取
    @GET("set/suggest/comment")
    Call<Result> setSuggestCommentListGet(@Query("sid") long suggestId, @Query("page") int page);

    // 意见关注
    @POST("set/suggest/follow")
    Call<Result> setSuggestFollowToggle(@Query("sid") long suggestId);

    // 配对邀请
    @POST("couple")
    Call<Result> coupleInvitee(@Body User user);

    // 配对更新
    @PUT("couple")
    Call<Result> coupleUpdate(@Query("type") int type, @Body Couple couple);

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
    Call<Result> coupleWeatherForecastListGet();

    // bookHome获取
    @GET("book/home")
    Call<Result> bookHomeGet(@Query("near") long near);

    // lock上传
    @POST("book/lock")
    Call<Result> bookLockAdd(@Body Lock lock);

    // lock修改密码
    @PUT("book/lock?modify=1&toggle=0")
    Call<Result> bookLockUpdatePwd(@Query("code") String code, @Body Lock lock);

    // lock开关锁
    @PUT("book/lock?toggle=1&modify=0")
    Call<Result> bookLockToggle(@Body Lock lock);

    // lock获取
    @GET("book/lock")
    Call<Result> bookLockGet();

    // trendsList获取
    @GET("book/trends?trends=1&total=0")
    Call<Result> bookTrendsListGet(@Query("create") long create, @Query("page") int page);

    // trendsTotal获取
    @GET("book/trends?trends=0&total=1")
    Call<Result> bookTrendsTotalGet();

    // souvenirList获取
    @GET("book/souvenir?sid=0&list=1")
    Call<Result> bookSouvenirListGet(@Query("done") boolean done);

    // souvenir获取
    @GET("book/souvenir?list=0")
    Call<Result> bookSouvenirGet(@Query("sid") long sid);

    // souvenir上传
    @POST("book/souvenir")
    Call<Result> bookSouvenirAdd(@Body Souvenir souvenir);

    // souvenir删除
    @DELETE("book/souvenir")
    Call<Result> bookSouvenirDel(@Query("sid") long sid);

    // souvenir修改
    @PUT("book/souvenir?year=0")
    Call<Result> bookSouvenirUpdateBody(@Body Souvenir souvenir);

    // souvenir修改
    @PUT("book/souvenir")
    Call<Result> bookSouvenirUpdateForeign(@Query("year") int year, @Body Souvenir souvenir);

    // mensesList获取
    @GET("book/menses?latest=0&date=1")
    Call<Result> bookMensesListGetByDate(@Query("mine") boolean mine, @Query("year") int year, @Query("month") int month);

    // menses获取
    @GET("book/menses?latest=1&date=0")
    Call<Result> bookMensesLatestGet();

    // menses上传
    @POST("book/menses")
    Call<Result> bookMensesAdd(@Body Menses menses);

    // shyList获取
    @GET("book/shy?date=1")
    Call<Result> bookShyListGetByDate(@Query("year") int year, @Query("month") int month);

    // shy上传
    @POST("book/shy")
    Call<Result> bookShyAdd(@Body Shy shy);

    // sleepList获取
    @GET("book/sleep?latest=0&date=1")
    Call<Result> bookSleepListGetByDate(@Query("year") int year, @Query("month") int month);

    // sleep获取
    @GET("book/sleep?latest=1&date=0")
    Call<Result> bookSleepLatestGet();

    // sleep上传
    @POST("book/sleep")
    Call<Result> bookSleepAdd(@Body Sleep sleep);

    // word获取
    @GET("book/word?list=1")
    Call<Result> bookWordListGet(@Query("page") int page);

    // word上传
    @POST("book/word")
    Call<Result> bookWordAdd(@Body Word word);

    // word删除
    @DELETE("book/word")
    Call<Result> bookWordDel(@Query("wid") long wid);

    // whisper获取
    @GET("book/whisper?list=1")
    Call<Result> bookWhisperListGet(@Query("channel") String channel, @Query("page") int page);

    // whisper上传
    @POST("book/whisper")
    Call<Result> bookWhisperAdd(@Body Whisper whisper);

    // diaryList获取
    @GET("book/diary?did=0&list=1")
    Call<Result> bookDiaryListGet(@Query("who") int who, @Query("page") int page);

    // diary获取
    @GET("book/diary?list=0")
    Call<Result> bookDiaryGet(@Query("did") long did);

    // diary上传
    @POST("book/diary")
    Call<Result> bookDiaryAdd(@Body Diary diary);

    // diary删除
    @DELETE("book/diary")
    Call<Result> bookDiaryDel(@Query("did") long did);

    // diary修改
    @PUT("book/diary")
    Call<Result> bookDiaryUpdate(@Body Diary diary);

    // albumList获取
    @GET("book/album?aid=0&list=1")
    Call<Result> bookAlbumListGet(@Query("page") int page);

    // album获取
    @GET("book/album?list=0")
    Call<Result> bookAlbumGet(@Query("aid") long aid);

    // album上传
    @POST("book/album")
    Call<Result> bookAlbumAdd(@Body Album album);

    // album删除
    @DELETE("book/album")
    Call<Result> bookAlbumDel(@Query("aid") long aid);

    // album修改
    @PUT("book/album")
    Call<Result> bookAlbumUpdate(@Body Album Album);

    // picture列表获取
    @GET("book/picture")
    Call<Result> bookPictureListGet(@Query("aid") long aid, @Query("page") int page);

    // picture上传
    @POST("book/picture")
    Call<Result> bookPictureListAdd(@Body PictureList pictureList);

    // picture删除
    @DELETE("book/picture")
    Call<Result> bookPictureDel(@Query("pid") long pid);

    // picture上传
    @PUT("book/picture")
    Call<Result> bookPictureUpdate(@Body Picture picture);

    // audioList获取
    @GET("book/audio?aid=0&list=1")
    Call<Result> bookAudioListGet(@Query("page") int page);

    // audio上传
    @POST("book/audio")
    Call<Result> bookAudioAdd(@Body Audio audio);

    // audio删除
    @DELETE("book/audio")
    Call<Result> bookAudioDel(@Query("aid") long aid);

    // videoList获取
    @GET("book/video?vid=0&list=1")
    Call<Result> bookVideoListGet(@Query("page") int page);

    // video上传
    @POST("book/video")
    Call<Result> bookVideoAdd(@Body Video video);

    // video删除
    @DELETE("book/video")
    Call<Result> bookVideoDel(@Query("vid") long vid);

    // foodList获取
    @GET("book/food?fid=0&list=1")
    Call<Result> bookFoodListGet(@Query("page") int page);

    // food上传
    @POST("book/food")
    Call<Result> bookFoodAdd(@Body Food food);

    // food删除
    @DELETE("book/food")
    Call<Result> bookFoodDel(@Query("fid") long fid);

    // travelList获取
    @GET("book/travel?tid=0&list=1")
    Call<Result> bookTravelListGet(@Query("page") int page);

    // travel获取
    @GET("book/travel?list=0")
    Call<Result> bookTravelGet(@Query("tid") long tid);

    // travel上传
    @POST("book/travel")
    Call<Result> bookTravelAdd(@Body Travel travel);

    // travel删除
    @DELETE("book/travel")
    Call<Result> bookTravelDel(@Query("tid") long tid);

    // travel修改
    @PUT("book/travel")
    Call<Result> bookTravelUpdate(@Body Travel travel);

    // giftList获取
    @GET("book/gift?gid=0&list=1")
    Call<Result> bookGiftListGet(@Query("who") int who, @Query("page") int page);

    // gift上传
    @POST("book/gift")
    Call<Result> bookGiftAdd(@Body Gift gift);

    // gift删除
    @DELETE("book/gift")
    Call<Result> bookGiftDel(@Query("gid") long gid);

    // gift修改
    @PUT("book/gift")
    Call<Result> bookGiftUpdate(@Body Gift gift);

    // promise获取
    @GET("book/promise?pid=0&list=1")
    Call<Result> bookPromiseListGet(@Query("who") int who, @Query("page") int page);

    // promise获取
    @GET("book/promise?list=0")
    Call<Result> bookPromiseGet(@Query("pid") long pid);

    // promise上传
    @POST("book/promise")
    Call<Result> bookPromiseAdd(@Body Promise promise);

    // promise删除
    @DELETE("book/promise")
    Call<Result> bookPromiseDel(@Query("pid") long did);

    // promise修改
    @PUT("book/promise")
    Call<Result> bookPromiseUpdate(@Body Promise promise);

    // promiseBreak获取
    @GET("book/promise/break?pdid=0")
    Call<Result> bookPromiseBreakListGet(@Query("pid") long pid, @Query("page") int page);

    // promiseBreak上传
    @POST("book/promise/break")
    Call<Result> bookPromiseBreakAdd(@Body PromiseBreak promiseBreak);

    // promiseBreak删除
    @DELETE("book/promise/break")
    Call<Result> bookPromiseBreakDel(@Query("pbid") long pbid);

    // angryList获取
    @GET("book/angry?aid=0&list=1")
    Call<Result> bookAngryListGet(@Query("who") int who, @Query("page") int page);

    // angry获取
    @GET("book/angry?list=0")
    Call<Result> bookAngryGet(@Query("aid") long aid);

    // angry上传
    @POST("book/angry")
    Call<Result> bookAngryAdd(@Body Angry angry);

    // angry删除
    @DELETE("book/angry")
    Call<Result> bookAngryDel(@Query("aid") long aid);

    // angry修改
    @PUT("book/angry")
    Call<Result> bookAngryUpdate(@Body Angry angry);

    // dreamList获取
    @GET("book/dream?did=0&list=1")
    Call<Result> bookDreamListGet(@Query("who") int who, @Query("page") int page);

    // dream获取
    @GET("book/dream?list=0")
    Call<Result> bookDreamGet(@Query("did") long did);

    // dream上传
    @POST("book/dream")
    Call<Result> bookDreamAdd(@Body Dream dream);

    // dream删除
    @DELETE("book/dream")
    Call<Result> bookDreamDel(@Query("did") long did);

    // dream修改
    @PUT("book/dream")
    Call<Result> bookDreamUpdate(@Body Dream dream);

    // award获取
    @GET("book/award?aid=0&score=0&list=1")
    Call<Result> bookAwardListGet(@Query("who") int who, @Query("page") int page);

    // award获取
    @GET("book/award?aid=0&list=0&score=1")
    Call<Result> bookAwardScoreGet();

    // award上传
    @POST("book/award")
    Call<Result> bookAwardAdd(@Body Award award);

    // award删除
    @DELETE("book/award")
    Call<Result> bookAwardDel(@Query("aid") long aid);

    // awardRule获取
    @GET("book/award/rule?arid=0&list=1")
    Call<Result> bookAwardRuleListGet(@Query("page") int page);

    // awardRule上传
    @POST("book/award/rule")
    Call<Result> bookAwardRuleAdd(@Body AwardRule awardRule);

    // awardRule删除
    @DELETE("book/award/rule")
    Call<Result> bookAwardRuleDel(@Query("arid") long arid);

}
