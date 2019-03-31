package com.jiangzg.lovenote.model.entity;

/**
 * Created by JZG on 2018/4/15.
 * VipLimit
 */
public class VipLimit {

    // couple
    private long wallPaperSize;
    private int wallPaperCount;
    // note
    private boolean noteTotalEnable;
    private int souvenirCount;
    private long videoSize;
    private long audioSize;
    private long pictureSize;
    private boolean pictureOriginal;
    private long diaryImageSize;
    private int diaryImageCount;
    private boolean whisperImageEnable;
    private int giftImageCount;
    private int foodImageCount;
    private int movieImageCount;
    // topic
    private int topicPostImageCount;

    public long getPictureSize() {
        return pictureSize;
    }

    public void setPictureSize(long pictureSize) {
        this.pictureSize = pictureSize;
    }

    public boolean isPictureOriginal() {
        return pictureOriginal;
    }

    public void setPictureOriginal(boolean pictureOriginal) {
        this.pictureOriginal = pictureOriginal;
    }

    public int getMovieImageCount() {
        return movieImageCount;
    }

    public void setMovieImageCount(int movieImageCount) {
        this.movieImageCount = movieImageCount;
    }

    public long getWallPaperSize() {
        return wallPaperSize;
    }

    public void setWallPaperSize(long wallPaperSize) {
        this.wallPaperSize = wallPaperSize;
    }

    public boolean isWhisperImageEnable() {
        return whisperImageEnable;
    }

    public void setWhisperImageEnable(boolean whisperImageEnable) {
        this.whisperImageEnable = whisperImageEnable;
    }

    public boolean isNoteTotalEnable() {
        return noteTotalEnable;
    }

    public void setNoteTotalEnable(boolean noteTotalEnable) {
        this.noteTotalEnable = noteTotalEnable;
    }

    public int getSouvenirCount() {
        return souvenirCount;
    }

    public void setSouvenirCount(int souvenirCount) {
        this.souvenirCount = souvenirCount;
    }

    public int getFoodImageCount() {
        return foodImageCount;
    }

    public void setFoodImageCount(int foodImageCount) {
        this.foodImageCount = foodImageCount;
    }

    public long getAudioSize() {
        return audioSize;
    }

    public void setAudioSize(long audioSize) {
        this.audioSize = audioSize;
    }

    public long getVideoSize() {
        return videoSize;
    }

    public void setVideoSize(long videoSize) {
        this.videoSize = videoSize;
    }

    public long getDiaryImageSize() {
        return diaryImageSize;
    }

    public void setDiaryImageSize(long diaryImageSize) {
        this.diaryImageSize = diaryImageSize;
    }

    public int getWallPaperCount() {
        return wallPaperCount;
    }

    public void setWallPaperCount(int wallPaperCount) {
        this.wallPaperCount = wallPaperCount;
    }

    public int getDiaryImageCount() {
        return diaryImageCount;
    }

    public void setDiaryImageCount(int diaryImageCount) {
        this.diaryImageCount = diaryImageCount;
    }

    public int getGiftImageCount() {
        return giftImageCount;
    }

    public void setGiftImageCount(int giftImageCount) {
        this.giftImageCount = giftImageCount;
    }

    public int getTopicPostImageCount() {
        return topicPostImageCount;
    }

    public void setTopicPostImageCount(int topicPostImageCount) {
        this.topicPostImageCount = topicPostImageCount;
    }
}
