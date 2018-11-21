package com.jiangzg.lovenote.helper;

import com.jiangzg.lovenote.domain.Album;
import com.jiangzg.lovenote.domain.Angry;
import com.jiangzg.lovenote.domain.Audio;
import com.jiangzg.lovenote.domain.Award;
import com.jiangzg.lovenote.domain.AwardRule;
import com.jiangzg.lovenote.domain.Couple;
import com.jiangzg.lovenote.domain.Diary;
import com.jiangzg.lovenote.domain.Dream;
import com.jiangzg.lovenote.domain.Entry;
import com.jiangzg.lovenote.domain.Food;
import com.jiangzg.lovenote.domain.Gift;
import com.jiangzg.lovenote.domain.Lock;
import com.jiangzg.lovenote.domain.MatchCoin;
import com.jiangzg.lovenote.domain.MatchPoint;
import com.jiangzg.lovenote.domain.MatchReport;
import com.jiangzg.lovenote.domain.MatchWork;
import com.jiangzg.lovenote.domain.Menses;
import com.jiangzg.lovenote.domain.Movie;
import com.jiangzg.lovenote.domain.Picture;
import com.jiangzg.lovenote.domain.PictureList;
import com.jiangzg.lovenote.domain.Place;
import com.jiangzg.lovenote.domain.Post;
import com.jiangzg.lovenote.domain.PostCollect;
import com.jiangzg.lovenote.domain.PostComment;
import com.jiangzg.lovenote.domain.PostCommentPoint;
import com.jiangzg.lovenote.domain.PostCommentReport;
import com.jiangzg.lovenote.domain.PostPoint;
import com.jiangzg.lovenote.domain.PostReport;
import com.jiangzg.lovenote.domain.Promise;
import com.jiangzg.lovenote.domain.PromiseBreak;
import com.jiangzg.lovenote.domain.Result;
import com.jiangzg.lovenote.domain.Shy;
import com.jiangzg.lovenote.domain.Sleep;
import com.jiangzg.lovenote.domain.Sms;
import com.jiangzg.lovenote.domain.Souvenir;
import com.jiangzg.lovenote.domain.Suggest;
import com.jiangzg.lovenote.domain.SuggestComment;
import com.jiangzg.lovenote.domain.SuggestFollow;
import com.jiangzg.lovenote.domain.Travel;
import com.jiangzg.lovenote.domain.User;
import com.jiangzg.lovenote.domain.Video;
import com.jiangzg.lovenote.domain.WallPaper;
import com.jiangzg.lovenote.domain.Whisper;
import com.jiangzg.lovenote.domain.Word;

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

    //String HOST = "192.168.18.7:30011";
    String HOST = "api.fishlife520.com";
    String BASE_URL = "http://" + HOST + "/v1/"; // BaseURL最好以/结尾

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

    // 版本
    @GET("set/version")
    Call<Result> setVersionNewListGet(@Query("code") int code);

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
    Call<Result> setSuggestListGet(@Query("status") int status, @Query("kind") int kind, @Query("page") int page);

    // 意见列表获取
    @GET("set/suggest?mine=1")
    Call<Result> setSuggestListMineGet(@Query("page") int page);

    // 意见列表获取
    @GET("set/suggest?follow=1")
    Call<Result> setSuggestListFollowGet(@Query("page") int page);

    // 意见评论发表
    @POST("set/suggest/comment")
    Call<Result> setSuggestCommentAdd(@Body SuggestComment suggestComment);

    // 意见评论删除
    @DELETE("set/suggest/comment")
    Call<Result> setSuggestCommentDel(@Query("scid") long suggestCommentId);

    // 意见评论获取
    @GET("set/suggest/comment")
    Call<Result> setSuggestCommentListGet(@Query("sid") long suggestId, @Query("page") int page);

    // 意见关注
    @POST("set/suggest/follow")
    Call<Result> setSuggestFollowToggle(@Body SuggestFollow suggestFollow);

    // 配对邀请
    @POST("couple")
    Call<Result> coupleInvitee(@Body User user);

    // 配对更新
    @PUT("couple")
    Call<Result> coupleUpdate(@Query("type") int type, @Body Couple couple);

    // 配对查询
    @GET("couple")
    Call<Result> coupleGet(@Query("self") boolean self, @Query("uid") long uid);

    @GET("couple?list=1&state=1")
    Call<Result> coupleStateListGet(@Query("cid") long cid, @Query("page") int page);

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
    @GET("couple/place?list=1")
    Call<Result> couplePlaceListGet(@Query("page") int page);

    // 天气预报
    @GET("couple/weather?forecast=1")
    Call<Result> coupleWeatherForecastListGet();

    // noteHome获取
    @GET("note/home")
    Call<Result> noteHomeGet(@Query("near") long near);

    // lock上传
    @POST("note/lock")
    Call<Result> noteLockAdd(@Body Lock lock);

    // lock修改密码
    @PUT("note/lock?modify=1")
    Call<Result> noteLockUpdatePwd(@Query("code") String code, @Body Lock lock);

    // lock开关锁
    @PUT("note/lock?toggle=1")
    Call<Result> noteLockToggle(@Body Lock lock);

    // lock获取
    @GET("note/lock")
    Call<Result> noteLockGet();

    // trendsList获取
    @GET("note/trends?list=1")
    Call<Result> noteTrendsListGet(@Query("create") long create, @Query("page") int page);

    // trendsTotal获取
    @GET("note/trends?total=1")
    Call<Result> noteTrendsTotalGet();

    // souvenirList获取
    @GET("note/souvenir?list=1")
    Call<Result> noteSouvenirListGet(@Query("done") boolean done, @Query("page") int page);

    // souvenir获取
    @GET("note/souvenir")
    Call<Result> noteSouvenirGet(@Query("sid") long sid);

    // souvenir上传
    @POST("note/souvenir")
    Call<Result> noteSouvenirAdd(@Body Souvenir souvenir);

    // souvenir删除
    @DELETE("note/souvenir")
    Call<Result> noteSouvenirDel(@Query("sid") long sid);

    // souvenir修改
    @PUT("note/souvenir")
    Call<Result> noteSouvenirUpdateBody(@Body Souvenir souvenir);

    // souvenir修改
    @PUT("note/souvenir")
    Call<Result> noteSouvenirUpdateForeign(@Query("year") int year, @Body Souvenir souvenir);

    // mensesList获取
    @GET("note/menses?date=1")
    Call<Result> noteMensesListGetByDate(@Query("mine") boolean mine, @Query("year") int year, @Query("month") int month);

    // menses获取
    @GET("note/menses?latest=1")
    Call<Result> noteMensesLatestGet();

    // menses上传
    @POST("note/menses")
    Call<Result> noteMensesAdd(@Body Menses menses);

    // shyList获取
    @GET("note/shy?date=1")
    Call<Result> noteShyListGetByDate(@Query("year") int year, @Query("month") int month);

    // shy上传
    @POST("note/shy")
    Call<Result> noteShyAdd(@Body Shy shy);

    // sleepList获取
    @GET("note/sleep?date=1")
    Call<Result> noteSleepListGetByDate(@Query("year") int year, @Query("month") int month);

    // sleep获取
    @GET("note/sleep?latest=1")
    Call<Result> noteSleepLatestGet();

    // sleep上传
    @POST("note/sleep")
    Call<Result> noteSleepAdd(@Body Sleep sleep);

    // word获取
    @GET("note/word?list=1")
    Call<Result> noteWordListGet(@Query("page") int page);

    // word上传
    @POST("note/word")
    Call<Result> noteWordAdd(@Body Word word);

    // word删除
    @DELETE("note/word")
    Call<Result> noteWordDel(@Query("wid") long wid);

    // whisper获取
    @GET("note/whisper?list=1")
    Call<Result> noteWhisperListGet(@Query("channel") String channel, @Query("page") int page);

    // whisper上传
    @POST("note/whisper")
    Call<Result> noteWhisperAdd(@Body Whisper whisper);

    // diaryList获取
    @GET("note/diary?list=1")
    Call<Result> noteDiaryListGet(@Query("who") int who, @Query("page") int page);

    // diary获取
    @GET("note/diary")
    Call<Result> noteDiaryGet(@Query("did") long did);

    // diary上传
    @POST("note/diary")
    Call<Result> noteDiaryAdd(@Body Diary diary);

    // diary删除
    @DELETE("note/diary")
    Call<Result> noteDiaryDel(@Query("did") long did);

    // diary修改
    @PUT("note/diary")
    Call<Result> noteDiaryUpdate(@Body Diary diary);

    // albumList获取
    @GET("note/album?list=1")
    Call<Result> noteAlbumListGet(@Query("page") int page);

    // album获取
    @GET("note/album")
    Call<Result> noteAlbumGet(@Query("aid") long aid);

    // album上传
    @POST("note/album")
    Call<Result> noteAlbumAdd(@Body Album album);

    // album删除
    @DELETE("note/album")
    Call<Result> noteAlbumDel(@Query("aid") long aid);

    // album修改
    @PUT("note/album")
    Call<Result> noteAlbumUpdate(@Body Album Album);

    // picture列表获取
    @GET("note/picture")
    Call<Result> notePictureListGet(@Query("aid") long aid, @Query("page") int page);

    // picture上传
    @POST("note/picture")
    Call<Result> notePictureListAdd(@Body PictureList pictureList);

    // picture删除
    @DELETE("note/picture")
    Call<Result> notePictureDel(@Query("pid") long pid);

    // picture上传
    @PUT("note/picture")
    Call<Result> notePictureUpdate(@Body Picture picture);

    // audioList获取
    @GET("note/audio?list=1")
    Call<Result> noteAudioListGet(@Query("page") int page);

    // audio上传
    @POST("note/audio")
    Call<Result> noteAudioAdd(@Body Audio audio);

    // audio删除
    @DELETE("note/audio")
    Call<Result> noteAudioDel(@Query("aid") long aid);

    // videoList获取
    @GET("note/video?list=1")
    Call<Result> noteVideoListGet(@Query("page") int page);

    // video上传
    @POST("note/video")
    Call<Result> noteVideoAdd(@Body Video video);

    // video删除
    @DELETE("note/video")
    Call<Result> noteVideoDel(@Query("vid") long vid);

    // foodList获取
    @GET("note/food?list=1")
    Call<Result> noteFoodListGet(@Query("page") int page);

    // food上传
    @POST("note/food")
    Call<Result> noteFoodAdd(@Body Food food);

    // food删除
    @DELETE("note/food")
    Call<Result> noteFoodDel(@Query("fid") long fid);

    // food修改
    @PUT("note/food")
    Call<Result> noteFoodUpdate(@Body Food food);

    // travelList获取
    @GET("note/travel?list=1")
    Call<Result> noteTravelListGet(@Query("page") int page);

    // travel获取
    @GET("note/travel")
    Call<Result> noteTravelGet(@Query("tid") long tid);

    // travel上传
    @POST("note/travel")
    Call<Result> noteTravelAdd(@Body Travel travel);

    // travel删除
    @DELETE("note/travel")
    Call<Result> noteTravelDel(@Query("tid") long tid);

    // travel修改
    @PUT("note/travel")
    Call<Result> noteTravelUpdate(@Body Travel travel);

    // giftList获取
    @GET("note/gift?list=1")
    Call<Result> noteGiftListGet(@Query("who") int who, @Query("page") int page);

    // gift上传
    @POST("note/gift")
    Call<Result> noteGiftAdd(@Body Gift gift);

    // gift删除
    @DELETE("note/gift")
    Call<Result> noteGiftDel(@Query("gid") long gid);

    // gift修改
    @PUT("note/gift")
    Call<Result> noteGiftUpdate(@Body Gift gift);

    // promise获取
    @GET("note/promise?list=1")
    Call<Result> notePromiseListGet(@Query("who") int who, @Query("page") int page);

    // promise获取
    @GET("note/promise")
    Call<Result> notePromiseGet(@Query("pid") long pid);

    // promise上传
    @POST("note/promise")
    Call<Result> notePromiseAdd(@Body Promise promise);

    // promise删除
    @DELETE("note/promise")
    Call<Result> notePromiseDel(@Query("pid") long did);

    // promise修改
    @PUT("note/promise")
    Call<Result> notePromiseUpdate(@Body Promise promise);

    // promiseBreak获取
    @GET("note/promise/break")
    Call<Result> notePromiseBreakListGet(@Query("pid") long pid, @Query("page") int page);

    // promiseBreak上传
    @POST("note/promise/break")
    Call<Result> notePromiseBreakAdd(@Body PromiseBreak promiseBreak);

    // promiseBreak删除
    @DELETE("note/promise/break")
    Call<Result> notePromiseBreakDel(@Query("pbid") long pbid);

    // angryList获取
    @GET("note/angry?list=1")
    Call<Result> noteAngryListGet(@Query("who") int who, @Query("page") int page);

    // angry获取
    @GET("note/angry")
    Call<Result> noteAngryGet(@Query("aid") long aid);

    // angry上传
    @POST("note/angry")
    Call<Result> noteAngryAdd(@Body Angry angry);

    // angry删除
    @DELETE("note/angry")
    Call<Result> noteAngryDel(@Query("aid") long aid);

    // angry修改
    @PUT("note/angry")
    Call<Result> noteAngryUpdate(@Body Angry angry);

    // dreamList获取
    @GET("note/dream?list=1")
    Call<Result> noteDreamListGet(@Query("who") int who, @Query("page") int page);

    // dream获取
    @GET("note/dream")
    Call<Result> noteDreamGet(@Query("did") long did);

    // dream上传
    @POST("note/dream")
    Call<Result> noteDreamAdd(@Body Dream dream);

    // dream删除
    @DELETE("note/dream")
    Call<Result> noteDreamDel(@Query("did") long did);

    // dream修改
    @PUT("note/dream")
    Call<Result> noteDreamUpdate(@Body Dream dream);

    // award获取
    @GET("note/award?list=1")
    Call<Result> noteAwardListGet(@Query("who") int who, @Query("page") int page);

    // award获取
    @GET("note/award?score=1")
    Call<Result> noteAwardScoreGet();

    // award上传
    @POST("note/award")
    Call<Result> noteAwardAdd(@Body Award award);

    // award删除
    @DELETE("note/award")
    Call<Result> noteAwardDel(@Query("aid") long aid);

    // awardRule获取
    @GET("note/award/rule?list=1")
    Call<Result> noteAwardRuleListGet(@Query("page") int page);

    // awardRule上传
    @POST("note/award/rule")
    Call<Result> noteAwardRuleAdd(@Body AwardRule awardRule);

    // awardRule删除
    @DELETE("note/award/rule")
    Call<Result> noteAwardRuleDel(@Query("arid") long arid);

    // movieList获取
    @GET("note/movie?list=1")
    Call<Result> noteMovieListGet(@Query("page") int page);

    // movie上传
    @POST("note/movie")
    Call<Result> noteMovieAdd(@Body Movie movie);

    // movie删除
    @DELETE("note/movie")
    Call<Result> noteMovieDel(@Query("mid") long mid);

    // movie修改
    @PUT("note/movie")
    Call<Result> noteMovieUpdate(@Body Movie movie);

    // topicHome获取
    @GET("topic/home")
    Call<Result> topicHomeGet();

    // messageList获取
    @GET("topic/message?mine=1")
    Call<Result> topicMessageListGet(@Query("kind") int kind, @Query("page") int page);

    // post上传
    @POST("topic/post")
    Call<Result> topicPostAdd(@Body Post post);

    // post删除
    @DELETE("topic/post")
    Call<Result> topicPostDel(@Query("pid") long pid);

    //  postList获取
    @GET("topic/post?list=1")
    Call<Result> topicPostListGet(@Query("create") long create, @Query("kind") int kind,
                                  @Query("sub_kind") int subKind, @Query("search") String search,
                                  @Query("official") boolean official, @Query("well") boolean well,
                                  @Query("page") int page);

    // postCollectList获取
    @GET("topic/post?collect=1")
    Call<Result> topicPostCollectListGet(@Query("me") boolean me, @Query("page") int page);

    // postMineList获取
    @GET("topic/post?mine=1")
    Call<Result> topicPostMineListGet(@Query("page") int page);

    // post获取
    @GET("topic/post")
    Call<Result> topicPostGet(@Query("pid") long pid);

    // postRead上传
    @POST("topic/post/read")
    Call<Result> topicPostRead(@Query("pid") long pid);

    // postReport上传
    @POST("topic/post/report")
    Call<Result> topicPostReportAdd(@Body PostReport postReport);

    // postPoint上传
    @POST("topic/post/point")
    Call<Result> topicPostPointToggle(@Body PostPoint postPoint);

    // postCollect上传
    @POST("topic/post/collect")
    Call<Result> topicPostCollectToggle(@Body PostCollect postCollect);

    // postComment上传
    @POST("topic/post/comment")
    Call<Result> topicPostCommentAdd(@Body PostComment postComment);

    // postComment删除
    @DELETE("topic/post/comment")
    Call<Result> topicPostCommentDel(@Query("pcid") long pcid);

    // postCommentList获取
    @GET("topic/post/comment?list=1")
    Call<Result> topicPostCommentListGet(@Query("pid") long pid, @Query("order") int order, @Query("page") int page);

    // postCommentSubList获取
    @GET("topic/post/comment?sub_list=1")
    Call<Result> topicPostCommentSubListGet(@Query("pid") long pid, @Query("tcid") long tcid, @Query("order") int order, @Query("page") int page);

    // postCommentList获取
    @GET("topic/post/comment")
    Call<Result> topicPostCommentUserListGet(@Query("pid") long pid, @Query("uid") long uid, @Query("order") int order, @Query("page") int page);

    // postCommentList获取
    @GET("topic/post/comment")
    Call<Result> topicPostCommentGet(@Query("pcid") long pcid);

    // postCommentReport上传
    @POST("topic/post/comment/report")
    Call<Result> topicPostCommentReportAdd(@Body PostCommentReport postCommentReport);

    // postCommentPoint上传
    @POST("topic/post/comment/point")
    Call<Result> topicPostCommentPointToggle(@Body PostCommentPoint postCommentPoint);

    // moreHome获取
    @GET("more/home")
    Call<Result> moreHomeGet();

    // payBefore获取
    @GET("more/bill?before=1")
    Call<Result> morePayBeforeGet(@Query("pay_platform") int payPlatform, @Query("goods") int goods);

    // payAfter获取
    @POST("more/bill?check=1")
    Call<Result> morePayAfterCheck();

    // vipHome获取
    @GET("more/vip?home=1")
    Call<Result> moreVipHomeGet();

    // vipList获取
    @GET("more/vip?list=1")
    Call<Result> moreVipListGet(@Query("page") int page);

    // coinHome获取
    @GET("more/coin?home=1")
    Call<Result> moreCoinHomeGet();

    // coinList获取
    @GET("more/coin?list=1")
    Call<Result> moreCoinListGet(@Query("page") int page);

    // sign上传
    @POST("more/sign")
    Call<Result> moreSignAdd();

    // signList获取
    @GET("more/sign?date=1")
    Call<Result> moreSignDateGet(@Query("year") int year, @Query("month") int month);

    // matchPeriodList获取
    @GET("more/match/period?list=1")
    Call<Result> moreMatchPeriodListGet(@Query("kind") int kind, @Query("page") int page);

    // matchWork上传
    @POST("more/match/work")
    Call<Result> moreMatchWorkAdd(@Body MatchWork matchWork);

    // matchWork删除
    @DELETE("more/match/work")
    Call<Result> moreMatchWorkDel(@Query("mwid") long mwid);

    // matchWorkList获取
    @GET("more/match/work")
    Call<Result> moreMatchWordListGet(@Query("mpid") long mpid, @Query("order") int order, @Query("page") int page);

    // matchWorkList获取
    @GET("more/match/work?our=1")
    Call<Result> moreMatchWordOurListGet(@Query("kind") int kind, @Query("page") int page);

    // matchReport上传
    @POST("more/match/report")
    Call<Result> moreMatchReportAdd(@Body MatchReport matchReport);

    // matchPoint上传
    @POST("more/match/point")
    Call<Result> moreMatchPointAdd(@Body MatchPoint matchPoint);

    // matchCoin上传
    @POST("more/match/coin")
    Call<Result> moreMatchCoinAdd(@Body MatchCoin matchCoin);

}
