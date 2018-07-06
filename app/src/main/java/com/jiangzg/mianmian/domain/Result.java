package com.jiangzg.mianmian.domain;

import java.io.Serializable;
import java.util.List;

/**
 * Created by JZG on 2017/12/15.
 * httpResult
 */

public class Result implements Serializable {

    public static final int RESULT_CODE_OK = 0;
    public static final int RESULT_CODE_TOAST = 1;
    public static final int RESULT_CODE_DIALOG = 2;
    public static final int RESULT_CODE_NO_USER_INFO = 3;
    public static final int RESULT_CODE_NO_CP = 4;
    public static final int RESULT_CODE_NO_VIP = 5;

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
        private String showMe;
        private String showTa;
        private long total;
        private long totalMe;
        private long totalTa;
        private User user;
        private Limit limit;
        private VipLimit vipLimit;
        private CommonConst commonConst;
        private OssInfo ossInfo;
        private int noticeNoReadCount;
        private Help help;
        private Notice notice;
        private List<Notice> noticeList;
        private List<Version> versionList;
        private Suggest suggest;
        private List<Suggest> suggestList;
        private List<SuggestComment> suggestCommentList;
        private Couple couple;
        private User userMe;
        private User userTa;
        private PairCard pairCard;
        private int togetherDay;
        private WallPaper wallPaper;
        private Place placeMe;
        private Place placeTa;
        private List<Place> placeList;
        private WeatherToday weatherTodayMe;
        private WeatherToday weatherTodayTa;
        private List<WeatherForecast> weatherForecastListMe;
        private List<WeatherForecast> weatherForecastListTa;
        private Sleep sleep;
        private List<SleepInfo> sleepInfoList;
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
        private Award award;
        private List<Award> awardList;
        private AwardRule awardRule;
        private List<AwardRule> awardRuleList;

        public Sleep getSleep() {
            return sleep;
        }

        public void setSleep(Sleep sleep) {
            this.sleep = sleep;
        }

        public List<SleepInfo> getSleepInfoList() {
            return sleepInfoList;
        }

        public void setSleepInfoList(List<SleepInfo> sleepInfoList) {
            this.sleepInfoList = sleepInfoList;
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

        public long getTotalMe() {
            return totalMe;
        }

        public void setTotalMe(long totalMe) {
            this.totalMe = totalMe;
        }

        public long getTotalTa() {
            return totalTa;
        }

        public void setTotalTa(long totalTa) {
            this.totalTa = totalTa;
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

        public List<WeatherForecast> getWeatherForecastListMe() {
            return weatherForecastListMe;
        }

        public void setWeatherForecastListMe(List<WeatherForecast> weatherForecastListMe) {
            this.weatherForecastListMe = weatherForecastListMe;
        }

        public List<WeatherForecast> getWeatherForecastListTa() {
            return weatherForecastListTa;
        }

        public void setWeatherForecastListTa(List<WeatherForecast> weatherForecastListTa) {
            this.weatherForecastListTa = weatherForecastListTa;
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

        public int getNoticeNoReadCount() {
            return noticeNoReadCount;
        }

        public void setNoticeNoReadCount(int noticeNoReadCount) {
            this.noticeNoReadCount = noticeNoReadCount;
        }

        public CommonConst getCommonConst() {
            return commonConst;
        }

        public void setCommonConst(CommonConst commonConst) {
            this.commonConst = commonConst;
        }

        public User getUserMe() {
            return userMe;
        }

        public void setUserMe(User userMe) {
            this.userMe = userMe;
        }

        public User getUserTa() {
            return userTa;
        }

        public void setUserTa(User userTa) {
            this.userTa = userTa;
        }

        public String getShowMe() {
            return showMe;
        }

        public void setShowMe(String showMe) {
            this.showMe = showMe;
        }

        public String getShowTa() {
            return showTa;
        }

        public void setShowTa(String showTa) {
            this.showTa = showTa;
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

        public Help getHelp() {
            return help;
        }

        public void setHelp(Help help) {
            this.help = help;
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
    }
}
