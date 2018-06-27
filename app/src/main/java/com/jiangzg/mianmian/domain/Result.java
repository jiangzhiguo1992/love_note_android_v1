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
        private long total;
        private User user;
        private Limit limit;
        private VipLimit vipLimit;
        private CommonConst commonConst;
        private OssInfo ossInfo;
        private int noticeNoReadCount;
        private Help help;
        private Notice notice;
        private List<Notice> noticeList;
        private Suggest suggest;
        private List<Suggest> suggestList;
        private List<SuggestComment> suggestCommentList;
        private List<Version> versionList;
        private PairCard pairCard;
        private Couple couple;
        private User me;
        private User ta;
        private String myShow;
        private String taShow;
        private int togetherDay;
        private WallPaper wallPaper;
        private Place myPlace;
        private Place taPlace;
        private List<Place> placeList;
        private WeatherToday myWeatherToday;
        private WeatherToday taWeatherToday;
        private List<WeatherForecast> myWeatherForecastList;
        private List<WeatherForecast> taWeatherForecastList;
        private List<Diary> diaryList;
        private Diary diary;
        private List<Word> wordList;
        private Word word;
        private List<Whisper> whisperList;
        private Whisper whisper;
        private Album album;
        private List<Album> albumList;
        private List<Picture> pictureList;
        private Picture picture;
        private List<Dream> dreamList;
        private Dream dream;
        private List<Gift> giftList;
        private Gift gift;
        private List<Promise> promiseList;
        private Promise promise;
        private List<PromiseBreak> promiseBreakList;
        private PromiseBreak promiseBreak;

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

        public WeatherToday getMyWeatherToday() {
            return myWeatherToday;
        }

        public void setMyWeatherToday(WeatherToday myWeatherToday) {
            this.myWeatherToday = myWeatherToday;
        }

        public WeatherToday getTaWeatherToday() {
            return taWeatherToday;
        }

        public void setTaWeatherToday(WeatherToday taWeatherToday) {
            this.taWeatherToday = taWeatherToday;
        }

        public List<WeatherForecast> getMyWeatherForecastList() {
            return myWeatherForecastList;
        }

        public void setMyWeatherForecastList(List<WeatherForecast> myWeatherForecastList) {
            this.myWeatherForecastList = myWeatherForecastList;
        }

        public List<WeatherForecast> getTaWeatherForecastList() {
            return taWeatherForecastList;
        }

        public void setTaWeatherForecastList(List<WeatherForecast> taWeatherForecastList) {
            this.taWeatherForecastList = taWeatherForecastList;
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

        public User getMe() {
            return me;
        }

        public void setMe(User me) {
            this.me = me;
        }

        public User getTa() {
            return ta;
        }

        public void setTa(User ta) {
            this.ta = ta;
        }

        public String getMyShow() {
            return myShow;
        }

        public void setMyShow(String myShow) {
            this.myShow = myShow;
        }

        public String getTaShow() {
            return taShow;
        }

        public void setTaShow(String taShow) {
            this.taShow = taShow;
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

        public Place getMyPlace() {
            return myPlace;
        }

        public void setMyPlace(Place myPlace) {
            this.myPlace = myPlace;
        }

        public Place getTaPlace() {
            return taPlace;
        }

        public void setTaPlace(Place taPlace) {
            this.taPlace = taPlace;
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
