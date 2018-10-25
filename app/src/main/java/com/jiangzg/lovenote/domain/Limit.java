package com.jiangzg.lovenote.domain;

/**
 * Created by JZG on 2018/4/13.
 * Limit
 */
public class Limit {

    // common
    private int smsCodeLength;
    private int smsEffectSec;
    private int smsBetweenSec;
    private int smsMaxSec;
    private int smsMaxCount;
    // settings
    private int suggestTitleLength;
    private int suggestContentLength;
    private int suggestCommentContentLength;
    // couple
    private long coupleInviteIntervalSec;
    private long coupleBreakNeedSec;
    private long coupleBreakSec;
    private int coupleNameLength;
    // note
    private long noteResExpireSec;
    private int noteLockLength;
    private int souvenirTitleLength;
    private int souvenirForeignYearCount;
    private int travelPlaceCount;
    private int travelVideoCount;
    private int travelFoodCount;
    private int travelAlbumCount;
    private int travelDiaryCount;
    private int audioTitleLength;
    private int videoTitleLength;
    private int albumTitleLength;
    private int picturePushCount;
    private int wordContentLength;
    private int whisperContentLength;
    private int whisperChannelLength;
    private int diaryContentLength;
    private int awardContentLength;
    private int awardRuleTitleLength;
    private int awardRuleScoreMax;
    private int dreamContentLength;
    private int giftTitleLength;
    private int foodTitleLength;
    private int foodContentLength;
    private int travelTitleLength;
    private int travelPlaceContentLength;
    private int angryContentLength;
    private int promiseContentLength;
    private int promiseBreakContentLength;
    // topic
    private int postTitleLength;
    private int postContentLength;
    private int postScreenReportCount;
    private int postCommentContentLength;
    private int postCommentScreenReportCount;
    // more
    private String payVipGoods1Title;
    private int payVipGoods1Days;
    private String payVipGoods1Amount;
    private String payVipGoods2Title;
    private int payVipGoods2Days;
    private String payVipGoods2Amount;
    private String payVipGoods3Title;
    private int payVipGoods3Days;
    private String payVipGoods3Amount;
    private String payCoinGoods1Title;
    private int payCoinGoods1Count;
    private String payCoinGoods1Amount;
    private String payCoinGoods2Title;
    private int payCoinGoods2Count;
    private String payCoinGoods2Amount;
    private String payCoinGoods3Title;
    private int payCoinGoods3Count;
    private String payCoinGoods3Amount;
    private int coinSignMinCount;
    private int coinSignMaxCount;
    private int coinSignIncreaseCount;
    private int coinWishPerDayCount;
    private int coinPlanePerDayCount;
    private int matchWorkScreenReportCount;
    private int matchWorkTitleLength;
    private int matchWorkContentLength;

    public int getMatchWorkScreenReportCount() {
        return matchWorkScreenReportCount;
    }

    public void setMatchWorkScreenReportCount(int matchWorkScreenReportCount) {
        this.matchWorkScreenReportCount = matchWorkScreenReportCount;
    }

    public int getMatchWorkTitleLength() {
        return matchWorkTitleLength;
    }

    public void setMatchWorkTitleLength(int matchWorkTitleLength) {
        this.matchWorkTitleLength = matchWorkTitleLength;
    }

    public int getMatchWorkContentLength() {
        return matchWorkContentLength;
    }

    public void setMatchWorkContentLength(int matchWorkContentLength) {
        this.matchWorkContentLength = matchWorkContentLength;
    }

    public String getPayCoinGoods1Title() {
        return payCoinGoods1Title;
    }

    public void setPayCoinGoods1Title(String payCoinGoods1Title) {
        this.payCoinGoods1Title = payCoinGoods1Title;
    }

    public int getPayCoinGoods1Count() {
        return payCoinGoods1Count;
    }

    public void setPayCoinGoods1Count(int payCoinGoods1Count) {
        this.payCoinGoods1Count = payCoinGoods1Count;
    }

    public String getPayCoinGoods1Amount() {
        return payCoinGoods1Amount;
    }

    public void setPayCoinGoods1Amount(String payCoinGoods1Amount) {
        this.payCoinGoods1Amount = payCoinGoods1Amount;
    }

    public String getPayCoinGoods2Title() {
        return payCoinGoods2Title;
    }

    public void setPayCoinGoods2Title(String payCoinGoods2Title) {
        this.payCoinGoods2Title = payCoinGoods2Title;
    }

    public int getPayCoinGoods2Count() {
        return payCoinGoods2Count;
    }

    public void setPayCoinGoods2Count(int payCoinGoods2Count) {
        this.payCoinGoods2Count = payCoinGoods2Count;
    }

    public String getPayCoinGoods2Amount() {
        return payCoinGoods2Amount;
    }

    public void setPayCoinGoods2Amount(String payCoinGoods2Amount) {
        this.payCoinGoods2Amount = payCoinGoods2Amount;
    }

    public String getPayCoinGoods3Title() {
        return payCoinGoods3Title;
    }

    public void setPayCoinGoods3Title(String payCoinGoods3Title) {
        this.payCoinGoods3Title = payCoinGoods3Title;
    }

    public int getPayCoinGoods3Count() {
        return payCoinGoods3Count;
    }

    public void setPayCoinGoods3Count(int payCoinGoods3Count) {
        this.payCoinGoods3Count = payCoinGoods3Count;
    }

    public String getPayCoinGoods3Amount() {
        return payCoinGoods3Amount;
    }

    public void setPayCoinGoods3Amount(String payCoinGoods3Amount) {
        this.payCoinGoods3Amount = payCoinGoods3Amount;
    }

    public int getPayVipGoods1Days() {
        return payVipGoods1Days;
    }

    public void setPayVipGoods1Days(int payVipGoods1Days) {
        this.payVipGoods1Days = payVipGoods1Days;
    }

    public int getPayVipGoods2Days() {
        return payVipGoods2Days;
    }

    public void setPayVipGoods2Days(int payVipGoods2Days) {
        this.payVipGoods2Days = payVipGoods2Days;
    }

    public int getPayVipGoods3Days() {
        return payVipGoods3Days;
    }

    public void setPayVipGoods3Days(int payVipGoods3Days) {
        this.payVipGoods3Days = payVipGoods3Days;
    }

    public String getPayVipGoods1Title() {
        return payVipGoods1Title;
    }

    public void setPayVipGoods1Title(String payVipGoods1Title) {
        this.payVipGoods1Title = payVipGoods1Title;
    }

    public String getPayVipGoods1Amount() {
        return payVipGoods1Amount;
    }

    public void setPayVipGoods1Amount(String payVipGoods1Amount) {
        this.payVipGoods1Amount = payVipGoods1Amount;
    }

    public String getPayVipGoods2Title() {
        return payVipGoods2Title;
    }

    public void setPayVipGoods2Title(String payVipGoods2Title) {
        this.payVipGoods2Title = payVipGoods2Title;
    }

    public String getPayVipGoods2Amount() {
        return payVipGoods2Amount;
    }

    public void setPayVipGoods2Amount(String payVipGoods2Amount) {
        this.payVipGoods2Amount = payVipGoods2Amount;
    }

    public String getPayVipGoods3Title() {
        return payVipGoods3Title;
    }

    public void setPayVipGoods3Title(String payVipGoods3Title) {
        this.payVipGoods3Title = payVipGoods3Title;
    }

    public String getPayVipGoods3Amount() {
        return payVipGoods3Amount;
    }

    public void setPayVipGoods3Amount(String payVipGoods3Amount) {
        this.payVipGoods3Amount = payVipGoods3Amount;
    }

    public int getCoinSignMinCount() {
        return coinSignMinCount;
    }

    public void setCoinSignMinCount(int coinSignMinCount) {
        this.coinSignMinCount = coinSignMinCount;
    }

    public int getCoinSignMaxCount() {
        return coinSignMaxCount;
    }

    public void setCoinSignMaxCount(int coinSignMaxCount) {
        this.coinSignMaxCount = coinSignMaxCount;
    }

    public int getCoinSignIncreaseCount() {
        return coinSignIncreaseCount;
    }

    public void setCoinSignIncreaseCount(int coinSignIncreaseCount) {
        this.coinSignIncreaseCount = coinSignIncreaseCount;
    }

    public int getCoinWishPerDayCount() {
        return coinWishPerDayCount;
    }

    public void setCoinWishPerDayCount(int coinWishPerDayCount) {
        this.coinWishPerDayCount = coinWishPerDayCount;
    }

    public int getCoinPlanePerDayCount() {
        return coinPlanePerDayCount;
    }

    public void setCoinPlanePerDayCount(int coinPlanePerDayCount) {
        this.coinPlanePerDayCount = coinPlanePerDayCount;
    }

    public long getCoupleInviteIntervalSec() {
        return coupleInviteIntervalSec;
    }

    public void setCoupleInviteIntervalSec(long coupleInviteIntervalSec) {
        this.coupleInviteIntervalSec = coupleInviteIntervalSec;
    }

    public long getCoupleBreakNeedSec() {
        return coupleBreakNeedSec;
    }

    public void setCoupleBreakNeedSec(long coupleBreakNeedSec) {
        this.coupleBreakNeedSec = coupleBreakNeedSec;
    }

    public int getPostCommentScreenReportCount() {
        return postCommentScreenReportCount;
    }

    public void setPostCommentScreenReportCount(int postCommentScreenReportCount) {
        this.postCommentScreenReportCount = postCommentScreenReportCount;
    }

    public int getPostTitleLength() {
        return postTitleLength;
    }

    public void setPostTitleLength(int postTitleLength) {
        this.postTitleLength = postTitleLength;
    }

    public int getPostContentLength() {
        return postContentLength;
    }

    public void setPostContentLength(int postContentLength) {
        this.postContentLength = postContentLength;
    }

    public int getPostScreenReportCount() {
        return postScreenReportCount;
    }

    public void setPostScreenReportCount(int postScreenReportCount) {
        this.postScreenReportCount = postScreenReportCount;
    }

    public int getPostCommentContentLength() {
        return postCommentContentLength;
    }

    public void setPostCommentContentLength(int postCommentContentLength) {
        this.postCommentContentLength = postCommentContentLength;
    }

    public int getFoodContentLength() {
        return foodContentLength;
    }

    public void setFoodContentLength(int foodContentLength) {
        this.foodContentLength = foodContentLength;
    }

    public int getTravelPlaceCount() {
        return travelPlaceCount;
    }

    public void setTravelPlaceCount(int travelPlaceCount) {
        this.travelPlaceCount = travelPlaceCount;
    }

    public int getTravelVideoCount() {
        return travelVideoCount;
    }

    public void setTravelVideoCount(int travelVideoCount) {
        this.travelVideoCount = travelVideoCount;
    }

    public int getTravelFoodCount() {
        return travelFoodCount;
    }

    public void setTravelFoodCount(int travelFoodCount) {
        this.travelFoodCount = travelFoodCount;
    }

    public int getTravelAlbumCount() {
        return travelAlbumCount;
    }

    public void setTravelAlbumCount(int travelAlbumCount) {
        this.travelAlbumCount = travelAlbumCount;
    }

    public int getTravelDiaryCount() {
        return travelDiaryCount;
    }

    public void setTravelDiaryCount(int travelDiaryCount) {
        this.travelDiaryCount = travelDiaryCount;
    }

    public int getNoteLockLength() {
        return noteLockLength;
    }

    public void setNoteLockLength(int noteLockLength) {
        this.noteLockLength = noteLockLength;
    }

    public int getSouvenirForeignYearCount() {
        return souvenirForeignYearCount;
    }

    public void setSouvenirForeignYearCount(int souvenirForeignYearCount) {
        this.souvenirForeignYearCount = souvenirForeignYearCount;
    }

    public int getFoodTitleLength() {
        return foodTitleLength;
    }

    public void setFoodTitleLength(int foodTitleLength) {
        this.foodTitleLength = foodTitleLength;
    }

    public int getAwardContentLength() {
        return awardContentLength;
    }

    public void setAwardContentLength(int awardContentLength) {
        this.awardContentLength = awardContentLength;
    }

    public int getAwardRuleTitleLength() {
        return awardRuleTitleLength;
    }

    public void setAwardRuleTitleLength(int awardRuleTitleLength) {
        this.awardRuleTitleLength = awardRuleTitleLength;
    }

    public int getAwardRuleScoreMax() {
        return awardRuleScoreMax;
    }

    public void setAwardRuleScoreMax(int awardRuleScoreMax) {
        this.awardRuleScoreMax = awardRuleScoreMax;
    }

    public int getPromiseBreakContentLength() {
        return promiseBreakContentLength;
    }

    public void setPromiseBreakContentLength(int promiseBreakContentLength) {
        this.promiseBreakContentLength = promiseBreakContentLength;
    }

    public long getCoupleBreakSec() {
        return coupleBreakSec;
    }

    public void setCoupleBreakSec(long coupleBreakSec) {
        this.coupleBreakSec = coupleBreakSec;
    }

    public int getSmsCodeLength() {
        return smsCodeLength;
    }

    public void setSmsCodeLength(int smsCodeLength) {
        this.smsCodeLength = smsCodeLength;
    }

    public long getNoteResExpireSec() {
        return noteResExpireSec;
    }

    public void setNoteResExpireSec(long noteResExpireSec) {
        this.noteResExpireSec = noteResExpireSec;
    }

    public int getPicturePushCount() {
        return picturePushCount;
    }

    public void setPicturePushCount(int picturePushCount) {
        this.picturePushCount = picturePushCount;
    }

    public int getWhisperChannelLength() {
        return whisperChannelLength;
    }

    public void setWhisperChannelLength(int whisperChannelLength) {
        this.whisperChannelLength = whisperChannelLength;
    }

    public int getSmsEffectSec() {
        return smsEffectSec;
    }

    public void setSmsEffectSec(int smsEffectSec) {
        this.smsEffectSec = smsEffectSec;
    }

    public int getSmsBetweenSec() {
        return smsBetweenSec;
    }

    public void setSmsBetweenSec(int smsBetweenSec) {
        this.smsBetweenSec = smsBetweenSec;
    }

    public int getSmsMaxSec() {
        return smsMaxSec;
    }

    public void setSmsMaxSec(int smsMaxSec) {
        this.smsMaxSec = smsMaxSec;
    }

    public int getSmsMaxCount() {
        return smsMaxCount;
    }

    public void setSmsMaxCount(int smsMaxCount) {
        this.smsMaxCount = smsMaxCount;
    }

    public int getSuggestTitleLength() {
        return suggestTitleLength;
    }

    public void setSuggestTitleLength(int suggestTitleLength) {
        this.suggestTitleLength = suggestTitleLength;
    }

    public int getSuggestContentLength() {
        return suggestContentLength;
    }

    public void setSuggestContentLength(int suggestContentLength) {
        this.suggestContentLength = suggestContentLength;
    }

    public int getSuggestCommentContentLength() {
        return suggestCommentContentLength;
    }

    public void setSuggestCommentContentLength(int suggestCommentContentLength) {
        this.suggestCommentContentLength = suggestCommentContentLength;
    }

    public int getCoupleNameLength() {
        return coupleNameLength;
    }

    public void setCoupleNameLength(int coupleNameLength) {
        this.coupleNameLength = coupleNameLength;
    }

    public int getSouvenirTitleLength() {
        return souvenirTitleLength;
    }

    public void setSouvenirTitleLength(int souvenirTitleLength) {
        this.souvenirTitleLength = souvenirTitleLength;
    }

    public int getWhisperContentLength() {
        return whisperContentLength;
    }

    public void setWhisperContentLength(int whisperContentLength) {
        this.whisperContentLength = whisperContentLength;
    }

    public int getWordContentLength() {
        return wordContentLength;
    }

    public void setWordContentLength(int wordContentLength) {
        this.wordContentLength = wordContentLength;
    }

    public int getDiaryContentLength() {
        return diaryContentLength;
    }

    public void setDiaryContentLength(int diaryContentLength) {
        this.diaryContentLength = diaryContentLength;
    }

    public int getTravelTitleLength() {
        return travelTitleLength;
    }

    public void setTravelTitleLength(int travelTitleLength) {
        this.travelTitleLength = travelTitleLength;
    }

    public int getTravelPlaceContentLength() {
        return travelPlaceContentLength;
    }

    public void setTravelPlaceContentLength(int travelPlaceContentLength) {
        this.travelPlaceContentLength = travelPlaceContentLength;
    }

    public int getAlbumTitleLength() {
        return albumTitleLength;
    }

    public void setAlbumTitleLength(int albumTitleLength) {
        this.albumTitleLength = albumTitleLength;
    }

    public int getPromiseContentLength() {
        return promiseContentLength;
    }

    public void setPromiseContentLength(int promiseContentLength) {
        this.promiseContentLength = promiseContentLength;
    }

    public int getAudioTitleLength() {
        return audioTitleLength;
    }

    public void setAudioTitleLength(int audioTitleLength) {
        this.audioTitleLength = audioTitleLength;
    }

    public int getVideoTitleLength() {
        return videoTitleLength;
    }

    public void setVideoTitleLength(int videoTitleLength) {
        this.videoTitleLength = videoTitleLength;
    }

    public int getGiftTitleLength() {
        return giftTitleLength;
    }

    public void setGiftTitleLength(int giftTitleLength) {
        this.giftTitleLength = giftTitleLength;
    }

    public int getDreamContentLength() {
        return dreamContentLength;
    }

    public void setDreamContentLength(int dreamContentLength) {
        this.dreamContentLength = dreamContentLength;
    }

    public int getAngryContentLength() {
        return angryContentLength;
    }

    public void setAngryContentLength(int angryContentLength) {
        this.angryContentLength = angryContentLength;
    }

}
