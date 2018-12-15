package com.jiangzg.lovenote.model.api;

import com.jiangzg.lovenote.model.entity.Album;
import com.jiangzg.lovenote.model.entity.Angry;
import com.jiangzg.lovenote.model.entity.Audio;
import com.jiangzg.lovenote.model.entity.Award;
import com.jiangzg.lovenote.model.entity.AwardRule;
import com.jiangzg.lovenote.model.entity.AwardScore;
import com.jiangzg.lovenote.model.entity.Broadcast;
import com.jiangzg.lovenote.model.entity.Coin;
import com.jiangzg.lovenote.model.entity.CommonConst;
import com.jiangzg.lovenote.model.entity.CommonCount;
import com.jiangzg.lovenote.model.entity.Couple;
import com.jiangzg.lovenote.model.entity.CoupleState;
import com.jiangzg.lovenote.model.entity.Diary;
import com.jiangzg.lovenote.model.entity.Dream;
import com.jiangzg.lovenote.model.entity.Food;
import com.jiangzg.lovenote.model.entity.Gift;
import com.jiangzg.lovenote.model.entity.Limit;
import com.jiangzg.lovenote.model.entity.Lock;
import com.jiangzg.lovenote.model.entity.MatchCoin;
import com.jiangzg.lovenote.model.entity.MatchPeriod;
import com.jiangzg.lovenote.model.entity.MatchPoint;
import com.jiangzg.lovenote.model.entity.MatchReport;
import com.jiangzg.lovenote.model.entity.MatchWork;
import com.jiangzg.lovenote.model.entity.Menses;
import com.jiangzg.lovenote.model.entity.MensesInfo;
import com.jiangzg.lovenote.model.entity.ModelShow;
import com.jiangzg.lovenote.model.entity.Movie;
import com.jiangzg.lovenote.model.entity.NoteTotal;
import com.jiangzg.lovenote.model.entity.Notice;
import com.jiangzg.lovenote.model.entity.OrderBefore;
import com.jiangzg.lovenote.model.entity.OssInfo;
import com.jiangzg.lovenote.model.entity.PairCard;
import com.jiangzg.lovenote.model.entity.Picture;
import com.jiangzg.lovenote.model.entity.Place;
import com.jiangzg.lovenote.model.entity.Post;
import com.jiangzg.lovenote.model.entity.PostCollect;
import com.jiangzg.lovenote.model.entity.PostComment;
import com.jiangzg.lovenote.model.entity.PostCommentPoint;
import com.jiangzg.lovenote.model.entity.PostCommentReport;
import com.jiangzg.lovenote.model.entity.PostKindInfo;
import com.jiangzg.lovenote.model.entity.PostPoint;
import com.jiangzg.lovenote.model.entity.PostReport;
import com.jiangzg.lovenote.model.entity.Promise;
import com.jiangzg.lovenote.model.entity.PromiseBreak;
import com.jiangzg.lovenote.model.entity.PushInfo;
import com.jiangzg.lovenote.model.entity.Shy;
import com.jiangzg.lovenote.model.entity.Sign;
import com.jiangzg.lovenote.model.entity.Sleep;
import com.jiangzg.lovenote.model.entity.Souvenir;
import com.jiangzg.lovenote.model.entity.Suggest;
import com.jiangzg.lovenote.model.entity.SuggestComment;
import com.jiangzg.lovenote.model.entity.TopicMessage;
import com.jiangzg.lovenote.model.entity.Travel;
import com.jiangzg.lovenote.model.entity.Trends;
import com.jiangzg.lovenote.model.entity.User;
import com.jiangzg.lovenote.model.entity.Version;
import com.jiangzg.lovenote.model.entity.Video;
import com.jiangzg.lovenote.model.entity.Vip;
import com.jiangzg.lovenote.model.entity.VipLimit;
import com.jiangzg.lovenote.model.entity.WallPaper;
import com.jiangzg.lovenote.model.entity.WeatherForecastInfo;
import com.jiangzg.lovenote.model.entity.WeatherToday;
import com.jiangzg.lovenote.model.entity.Whisper;
import com.jiangzg.lovenote.model.entity.Word;

import java.io.Serializable;
import java.util.List;

/**
 * Created by JZG on 2017/12/15.
 * httpResult
 */

public class Result implements Serializable {

    public static final int RESULT_CODE_OK = 0;
    public static final int RESULT_CODE_TOAST = 10;
    public static final int RESULT_CODE_DIALOG = 11;
    public static final int RESULT_CODE_NO_USER_INFO = 20;
    public static final int RESULT_CODE_NO_CP = 30;

    private int code;
    private String message;
    private Data data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public static class Data {

        private String show;
        private long total;
        private CommonConst commonConst;
        private CommonCount commonCount;
        private ModelShow modelShow;
        private Limit limit;
        private VipLimit vipLimit;
        private VipLimit vipYesLimit;
        private VipLimit vipNoLimit;
        private OssInfo ossInfo;
        private PushInfo pushInfo;
        private User user;
        private Notice notice;
        private List<Notice> noticeList;
        private List<Version> versionList;
        private Suggest suggest;
        private List<Suggest> suggestList;
        private List<SuggestComment> suggestCommentList;
        // couple
        private Couple couple;
        private List<CoupleState> coupleStateList;
        private PairCard pairCard;
        private int togetherDay;
        private WallPaper wallPaper;
        private Place placeMe;
        private Place placeTa;
        private List<Place> placeList;
        private WeatherToday weatherTodayMe;
        private WeatherToday weatherTodayTa;
        private WeatherForecastInfo weatherForecastMe;
        private WeatherForecastInfo weatherForecastTa;
        // note
        private Lock lock;
        private List<Trends> trendsList;
        private NoteTotal noteTotal;
        private Souvenir souvenirLatest;
        private Souvenir souvenir;
        private List<Souvenir> souvenirList;
        private Menses menses;
        private MensesInfo mensesInfo;
        private Menses mensesMe;
        private Menses mensesTa;
        private List<Menses> mensesList;
        private Shy shy;
        private List<Shy> shyList;
        private Sleep sleep;
        private Sleep sleepMe;
        private Sleep sleepTa;
        private List<Sleep> sleepList;
        private Word word;
        private List<Word> wordList;
        private Whisper whisper;
        private List<Whisper> whisperList;
        private Diary diary;
        private List<Diary> diaryList;
        private Album album;
        private List<Album> albumList;
        private Picture picture;
        private List<Picture> pictureList;
        private Audio audio;
        private List<Audio> audioList;
        private Video video;
        private List<Video> videoList;
        private Food food;
        private List<Food> foodList;
        private Travel travel;
        private List<Travel> travelList;
        private Gift gift;
        private List<Gift> giftList;
        private Promise promise;
        private List<Promise> promiseList;
        private PromiseBreak promiseBreak;
        private List<PromiseBreak> promiseBreakList;
        private Angry angry;
        private List<Angry> angryList;
        private Dream dream;
        private List<Dream> dreamList;
        private AwardScore awardScoreMe;
        private AwardScore awardScoreTa;
        private Award award;
        private List<Award> awardList;
        private AwardRule awardRule;
        private List<AwardRule> awardRuleList;
        private Movie movie;
        private List<Movie> movieList;
        // topic
        private List<PostKindInfo> postKindInfoList;
        private List<TopicMessage> topicMessageList;
        private Post post;
        private List<Post> postList;
        private PostReport postReport;
        private PostPoint postPoint;
        private PostCollect postCollect;
        private PostComment postComment;
        private List<PostComment> postCommentList;
        private PostCommentReport postCommentReport;
        private PostCommentPoint postCommentPoint;
        // more
        private List<Broadcast> broadcastList;
        private OrderBefore orderBefore;
        private Vip vip;
        private List<Vip> vipList;
        private Coin coin;
        private List<Coin> coinList;
        private Sign sign;
        private List<Sign> signList;
        private MatchPeriod wifePeriod;
        private MatchPeriod letterPeriod;
        private MatchPeriod discussPeriod;
        private List<MatchPeriod> matchPeriodList;
        private MatchWork matchWork;
        private List<MatchWork> matchWorkList;
        private MatchReport matchReport;
        private MatchPoint matchPoint;
        private MatchCoin matchCoin;

        public List<CoupleState> getCoupleStateList() {
            return coupleStateList;
        }

        public void setCoupleStateList(List<CoupleState> coupleStateList) {
            this.coupleStateList = coupleStateList;
        }

        public Movie getMovie() {
            return movie;
        }

        public void setMovie(Movie movie) {
            this.movie = movie;
        }

        public List<Movie> getMovieList() {
            return movieList;
        }

        public void setMovieList(List<Movie> movieList) {
            this.movieList = movieList;
        }

        public ModelShow getModelShow() {
            return modelShow;
        }

        public void setModelShow(ModelShow modelShow) {
            this.modelShow = modelShow;
        }

        public PushInfo getPushInfo() {
            return pushInfo;
        }

        public void setPushInfo(PushInfo pushInfo) {
            this.pushInfo = pushInfo;
        }

        public MensesInfo getMensesInfo() {
            return mensesInfo;
        }

        public void setMensesInfo(MensesInfo mensesInfo) {
            this.mensesInfo = mensesInfo;
        }

        public WeatherForecastInfo getWeatherForecastMe() {
            return weatherForecastMe;
        }

        public void setWeatherForecastMe(WeatherForecastInfo weatherForecastMe) {
            this.weatherForecastMe = weatherForecastMe;
        }

        public WeatherForecastInfo getWeatherForecastTa() {
            return weatherForecastTa;
        }

        public void setWeatherForecastTa(WeatherForecastInfo weatherForecastTa) {
            this.weatherForecastTa = weatherForecastTa;
        }

        public CommonCount getCommonCount() {
            return commonCount;
        }

        public void setCommonCount(CommonCount commonCount) {
            this.commonCount = commonCount;
        }

        public MatchPeriod getWifePeriod() {
            return wifePeriod;
        }

        public void setWifePeriod(MatchPeriod wifePeriod) {
            this.wifePeriod = wifePeriod;
        }

        public MatchPeriod getLetterPeriod() {
            return letterPeriod;
        }

        public void setLetterPeriod(MatchPeriod letterPeriod) {
            this.letterPeriod = letterPeriod;
        }

        public MatchPeriod getDiscussPeriod() {
            return discussPeriod;
        }

        public void setDiscussPeriod(MatchPeriod discussPeriod) {
            this.discussPeriod = discussPeriod;
        }

        public List<MatchPeriod> getMatchPeriodList() {
            return matchPeriodList;
        }

        public void setMatchPeriodList(List<MatchPeriod> matchPeriodList) {
            this.matchPeriodList = matchPeriodList;
        }

        public MatchWork getMatchWork() {
            return matchWork;
        }

        public void setMatchWork(MatchWork matchWork) {
            this.matchWork = matchWork;
        }

        public List<MatchWork> getMatchWorkList() {
            return matchWorkList;
        }

        public void setMatchWorkList(List<MatchWork> matchWorkList) {
            this.matchWorkList = matchWorkList;
        }

        public MatchReport getMatchReport() {
            return matchReport;
        }

        public void setMatchReport(MatchReport matchReport) {
            this.matchReport = matchReport;
        }

        public MatchPoint getMatchPoint() {
            return matchPoint;
        }

        public void setMatchPoint(MatchPoint matchPoint) {
            this.matchPoint = matchPoint;
        }

        public MatchCoin getMatchCoin() {
            return matchCoin;
        }

        public void setMatchCoin(MatchCoin matchCoin) {
            this.matchCoin = matchCoin;
        }

        public OrderBefore getOrderBefore() {
            return orderBefore;
        }

        public void setOrderBefore(OrderBefore orderBefore) {
            this.orderBefore = orderBefore;
        }

        public VipLimit getVipYesLimit() {
            return vipYesLimit;
        }

        public void setVipYesLimit(VipLimit vipYesLimit) {
            this.vipYesLimit = vipYesLimit;
        }

        public VipLimit getVipNoLimit() {
            return vipNoLimit;
        }

        public void setVipNoLimit(VipLimit vipNoLimit) {
            this.vipNoLimit = vipNoLimit;
        }

        public List<Sign> getSignList() {
            return signList;
        }

        public void setSignList(List<Sign> signList) {
            this.signList = signList;
        }

        public List<Coin> getCoinList() {
            return coinList;
        }

        public void setCoinList(List<Coin> coinList) {
            this.coinList = coinList;
        }

        public List<Vip> getVipList() {
            return vipList;
        }

        public void setVipList(List<Vip> vipList) {
            this.vipList = vipList;
        }

        public List<Broadcast> getBroadcastList() {
            return broadcastList;
        }

        public void setBroadcastList(List<Broadcast> broadcastList) {
            this.broadcastList = broadcastList;
        }

        public Vip getVip() {
            return vip;
        }

        public void setVip(Vip vip) {
            this.vip = vip;
        }

        public Coin getCoin() {
            return coin;
        }

        public void setCoin(Coin coin) {
            this.coin = coin;
        }

        public Sign getSign() {
            return sign;
        }

        public void setSign(Sign sign) {
            this.sign = sign;
        }

        public List<TopicMessage> getTopicMessageList() {
            return topicMessageList;
        }

        public void setTopicMessageList(List<TopicMessage> topicMessageList) {
            this.topicMessageList = topicMessageList;
        }

        public Post getPost() {
            return post;
        }

        public void setPost(Post post) {
            this.post = post;
        }

        public List<Post> getPostList() {
            return postList;
        }

        public void setPostList(List<Post> postList) {
            this.postList = postList;
        }

        public PostReport getPostReport() {
            return postReport;
        }

        public void setPostReport(PostReport postReport) {
            this.postReport = postReport;
        }

        public PostPoint getPostPoint() {
            return postPoint;
        }

        public void setPostPoint(PostPoint postPoint) {
            this.postPoint = postPoint;
        }

        public PostCollect getPostCollect() {
            return postCollect;
        }

        public void setPostCollect(PostCollect postCollect) {
            this.postCollect = postCollect;
        }

        public PostComment getPostComment() {
            return postComment;
        }

        public void setPostComment(PostComment postComment) {
            this.postComment = postComment;
        }

        public List<PostComment> getPostCommentList() {
            return postCommentList;
        }

        public void setPostCommentList(List<PostComment> postCommentList) {
            this.postCommentList = postCommentList;
        }

        public PostCommentReport getPostCommentReport() {
            return postCommentReport;
        }

        public void setPostCommentReport(PostCommentReport postCommentReport) {
            this.postCommentReport = postCommentReport;
        }

        public PostCommentPoint getPostCommentPoint() {
            return postCommentPoint;
        }

        public void setPostCommentPoint(PostCommentPoint postCommentPoint) {
            this.postCommentPoint = postCommentPoint;
        }

        public List<PostKindInfo> getPostKindInfoList() {
            return postKindInfoList;
        }

        public void setPostKindInfoList(List<PostKindInfo> postKindInfoList) {
            this.postKindInfoList = postKindInfoList;
        }

        public Lock getLock() {
            return lock;
        }

        public void setLock(Lock lock) {
            this.lock = lock;
        }

        public Souvenir getSouvenirLatest() {
            return souvenirLatest;
        }

        public void setSouvenirLatest(Souvenir souvenirLatest) {
            this.souvenirLatest = souvenirLatest;
        }

        public List<Trends> getTrendsList() {
            return trendsList;
        }

        public void setTrendsList(List<Trends> trendsList) {
            this.trendsList = trendsList;
        }

        public NoteTotal getNoteTotal() {
            return noteTotal;
        }

        public void setNoteTotal(NoteTotal noteTotal) {
            this.noteTotal = noteTotal;
        }

        public Souvenir getSouvenir() {
            return souvenir;
        }

        public void setSouvenir(Souvenir souvenir) {
            this.souvenir = souvenir;
        }

        public List<Souvenir> getSouvenirList() {
            return souvenirList;
        }

        public void setSouvenirList(List<Souvenir> souvenirList) {
            this.souvenirList = souvenirList;
        }

        public Menses getMenses() {
            return menses;
        }

        public void setMenses(Menses menses) {
            this.menses = menses;
        }

        public Menses getMensesMe() {
            return mensesMe;
        }

        public void setMensesMe(Menses mensesMe) {
            this.mensesMe = mensesMe;
        }

        public Menses getMensesTa() {
            return mensesTa;
        }

        public void setMensesTa(Menses mensesTa) {
            this.mensesTa = mensesTa;
        }

        public List<Menses> getMensesList() {
            return mensesList;
        }

        public void setMensesList(List<Menses> mensesList) {
            this.mensesList = mensesList;
        }

        public Shy getShy() {
            return shy;
        }

        public void setShy(Shy shy) {
            this.shy = shy;
        }

        public List<Shy> getShyList() {
            return shyList;
        }

        public void setShyList(List<Shy> shyList) {
            this.shyList = shyList;
        }

        public Sleep getSleepMe() {
            return sleepMe;
        }

        public void setSleepMe(Sleep sleepMe) {
            this.sleepMe = sleepMe;
        }

        public Sleep getSleepTa() {
            return sleepTa;
        }

        public void setSleepTa(Sleep sleepTa) {
            this.sleepTa = sleepTa;
        }

        public List<Sleep> getSleepList() {
            return sleepList;
        }

        public void setSleepList(List<Sleep> sleepList) {
            this.sleepList = sleepList;
        }

        public Audio getAudio() {
            return audio;
        }

        public void setAudio(Audio audio) {
            this.audio = audio;
        }

        public List<Audio> getAudioList() {
            return audioList;
        }

        public void setAudioList(List<Audio> audioList) {
            this.audioList = audioList;
        }

        public Video getVideo() {
            return video;
        }

        public void setVideo(Video video) {
            this.video = video;
        }

        public List<Video> getVideoList() {
            return videoList;
        }

        public void setVideoList(List<Video> videoList) {
            this.videoList = videoList;
        }

        public List<Travel> getTravelList() {
            return travelList;
        }

        public void setTravelList(List<Travel> travelList) {
            this.travelList = travelList;
        }

        public Travel getTravel() {
            return travel;
        }

        public void setTravel(Travel travel) {
            this.travel = travel;
        }

        public List<Food> getFoodList() {
            return foodList;
        }

        public void setFoodList(List<Food> foodList) {
            this.foodList = foodList;
        }

        public Food getFood() {
            return food;
        }

        public void setFood(Food food) {
            this.food = food;
        }

        public AwardScore getAwardScoreMe() {
            return awardScoreMe;
        }

        public void setAwardScoreMe(AwardScore awardScoreMe) {
            this.awardScoreMe = awardScoreMe;
        }

        public AwardScore getAwardScoreTa() {
            return awardScoreTa;
        }

        public void setAwardScoreTa(AwardScore awardScoreTa) {
            this.awardScoreTa = awardScoreTa;
        }

        public List<Award> getAwardList() {
            return awardList;
        }

        public void setAwardList(List<Award> awardList) {
            this.awardList = awardList;
        }

        public Award getAward() {
            return award;
        }

        public void setAward(Award award) {
            this.award = award;
        }

        public List<AwardRule> getAwardRuleList() {
            return awardRuleList;
        }

        public void setAwardRuleList(List<AwardRule> awardRuleList) {
            this.awardRuleList = awardRuleList;
        }

        public AwardRule getAwardRule() {
            return awardRule;
        }

        public void setAwardRule(AwardRule awardRule) {
            this.awardRule = awardRule;
        }

        public List<Angry> getAngryList() {
            return angryList;
        }

        public void setAngryList(List<Angry> angryList) {
            this.angryList = angryList;
        }

        public Angry getAngry() {
            return angry;
        }

        public void setAngry(Angry angry) {
            this.angry = angry;
        }

        public List<Promise> getPromiseList() {
            return promiseList;
        }

        public void setPromiseList(List<Promise> promiseList) {
            this.promiseList = promiseList;
        }

        public Promise getPromise() {
            return promise;
        }

        public void setPromise(Promise promise) {
            this.promise = promise;
        }

        public List<PromiseBreak> getPromiseBreakList() {
            return promiseBreakList;
        }

        public void setPromiseBreakList(List<PromiseBreak> promiseBreakList) {
            this.promiseBreakList = promiseBreakList;
        }

        public PromiseBreak getPromiseBreak() {
            return promiseBreak;
        }

        public void setPromiseBreak(PromiseBreak promiseBreak) {
            this.promiseBreak = promiseBreak;
        }

        public List<Gift> getGiftList() {
            return giftList;
        }

        public void setGiftList(List<Gift> giftList) {
            this.giftList = giftList;
        }

        public Gift getGift() {
            return gift;
        }

        public void setGift(Gift gift) {
            this.gift = gift;
        }

        public List<Dream> getDreamList() {
            return dreamList;
        }

        public void setDreamList(List<Dream> dreamList) {
            this.dreamList = dreamList;
        }

        public Dream getDream() {
            return dream;
        }

        public void setDream(Dream dream) {
            this.dream = dream;
        }

        public List<Place> getPlaceList() {
            return placeList;
        }

        public void setPlaceList(List<Place> placeList) {
            this.placeList = placeList;
        }

        public WeatherToday getWeatherTodayMe() {
            return weatherTodayMe;
        }

        public void setWeatherTodayMe(WeatherToday weatherTodayMe) {
            this.weatherTodayMe = weatherTodayMe;
        }

        public WeatherToday getWeatherTodayTa() {
            return weatherTodayTa;
        }

        public void setWeatherTodayTa(WeatherToday weatherTodayTa) {
            this.weatherTodayTa = weatherTodayTa;
        }

        public int getTogetherDay() {
            return togetherDay;
        }

        public void setTogetherDay(int togetherDay) {
            this.togetherDay = togetherDay;
        }

        public PairCard getPairCard() {
            return pairCard;
        }

        public void setPairCard(PairCard pairCard) {
            this.pairCard = pairCard;
        }

        public List<Picture> getPictureList() {
            return pictureList;
        }

        public void setPictureList(List<Picture> pictureList) {
            this.pictureList = pictureList;
        }

        public Picture getPicture() {
            return picture;
        }

        public void setPicture(Picture picture) {
            this.picture = picture;
        }

        public List<Notice> getNoticeList() {
            return noticeList;
        }

        public void setNoticeList(List<Notice> noticeList) {
            this.noticeList = noticeList;
        }

        public Notice getNotice() {
            return notice;
        }

        public void setNotice(Notice notice) {
            this.notice = notice;
        }

        public CommonConst getCommonConst() {
            return commonConst;
        }

        public void setCommonConst(CommonConst commonConst) {
            this.commonConst = commonConst;
        }

        public Album getAlbum() {
            return album;
        }

        public void setAlbum(Album album) {
            this.album = album;
        }

        public List<Album> getAlbumList() {
            return albumList;
        }

        public void setAlbumList(List<Album> albumList) {
            this.albumList = albumList;
        }

        public List<Whisper> getWhisperList() {
            return whisperList;
        }

        public void setWhisperList(List<Whisper> whisperList) {
            this.whisperList = whisperList;
        }

        public Whisper getWhisper() {
            return whisper;
        }

        public void setWhisper(Whisper whisper) {
            this.whisper = whisper;
        }

        public List<Word> getWordList() {
            return wordList;
        }

        public void setWordList(List<Word> wordList) {
            this.wordList = wordList;
        }

        public Word getWord() {
            return word;
        }

        public void setWord(Word word) {
            this.word = word;
        }

        public Diary getDiary() {
            return diary;
        }

        public void setDiary(Diary diary) {
            this.diary = diary;
        }

        public List<Diary> getDiaryList() {
            return diaryList;
        }

        public void setDiaryList(List<Diary> diaryList) {
            this.diaryList = diaryList;
        }

        public Place getPlaceMe() {
            return placeMe;
        }

        public void setPlaceMe(Place placeMe) {
            this.placeMe = placeMe;
        }

        public Place getPlaceTa() {
            return placeTa;
        }

        public void setPlaceTa(Place placeTa) {
            this.placeTa = placeTa;
        }

        public WallPaper getWallPaper() {
            return wallPaper;
        }

        public void setWallPaper(WallPaper wallPaper) {
            this.wallPaper = wallPaper;
        }

        public VipLimit getVipLimit() {
            return vipLimit;
        }

        public void setVipLimit(VipLimit vipLimit) {
            this.vipLimit = vipLimit;
        }

        public Limit getLimit() {
            return limit;
        }

        public void setLimit(Limit limit) {
            this.limit = limit;
        }

        public List<SuggestComment> getSuggestCommentList() {
            return suggestCommentList;
        }

        public void setSuggestCommentList(List<SuggestComment> suggestCommentList) {
            this.suggestCommentList = suggestCommentList;
        }

        public long getTotal() {
            return total;
        }

        public void setTotal(long total) {
            this.total = total;
        }

        public Suggest getSuggest() {
            return suggest;
        }

        public void setSuggest(Suggest suggest) {
            this.suggest = suggest;
        }

        public List<Suggest> getSuggestList() {
            return suggestList;
        }

        public void setSuggestList(List<Suggest> suggestList) {
            this.suggestList = suggestList;
        }

        public String getShow() {
            return show;
        }

        public void setShow(String show) {
            this.show = show;
        }

        public OssInfo getOssInfo() {
            return ossInfo;
        }

        public void setOssInfo(OssInfo ossInfo) {
            this.ossInfo = ossInfo;
        }

        public List<Version> getVersionList() {
            return versionList;
        }

        public void setVersionList(List<Version> versionList) {
            this.versionList = versionList;
        }

        public User getUser() {
            return user;
        }

        public void setUser(User user) {
            this.user = user;
        }

        public Couple getCouple() {
            return couple;
        }

        public void setCouple(Couple couple) {
            this.couple = couple;
        }

        public Sleep getSleep() {
            return sleep;
        }

        public void setSleep(Sleep sleep) {
            this.sleep = sleep;
        }
    }
}
