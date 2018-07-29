package com.jiangzg.mianmian.domain;

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
    private int suggestContentTextLength;
    private int suggestCommentContentLength;
    // couple
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
    private int pictureCount;
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
    private double postLonLatDiffMax;
    private int postScreenReportCount;
    private int postCommentContentLength;
    private int postCommentScreenReportCount;
    // more
    private int vipExpireDay;

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

    public double getPostLonLatDiffMax() {
        return postLonLatDiffMax;
    }

    public void setPostLonLatDiffMax(double postLonLatDiffMax) {
        this.postLonLatDiffMax = postLonLatDiffMax;
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

    public int getPictureCount() {
        return pictureCount;
    }

    public void setPictureCount(int pictureCount) {
        this.pictureCount = pictureCount;
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

    public int getSuggestContentTextLength() {
        return suggestContentTextLength;
    }

    public void setSuggestContentTextLength(int suggestContentTextLength) {
        this.suggestContentTextLength = suggestContentTextLength;
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

    public int getVipExpireDay() {
        return vipExpireDay;
    }

    public void setVipExpireDay(int vipExpireDay) {
        this.vipExpireDay = vipExpireDay;
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
