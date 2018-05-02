package com.jiangzg.mianmian.domain;

/**
 * Created by JZG on 2018/4/15.
 * VipLimit
 */
public class VipLimit {

    private long expireAt;

    private int coupleWallPaperCount;
    private boolean coupleEntryTraceEnable;
    private boolean coupleTopicTraceEnable;

    private int bookWhisperImageCount;
    private int bookDiaryImageCount;
    private long bookDiaryImageSize;
    private int bookAlbumTotalCount;
    private int bookPictureCount;
    private int bookGiftImageCount;
    private int bookMeetImageCount;
    private long bookAudioTotalSize;
    private long bookVideoTotalSize;

    private boolean topicImageEnable;

    public int getBookAlbumTotalCount() {
        return bookAlbumTotalCount;
    }

    public void setBookAlbumTotalCount(int bookAlbumTotalCount) {
        this.bookAlbumTotalCount = bookAlbumTotalCount;
    }

    public long getBookDiaryImageSize() {
        return bookDiaryImageSize;
    }

    public void setBookDiaryImageSize(long bookDiaryImageSize) {
        this.bookDiaryImageSize = bookDiaryImageSize;
    }

    public int getBookWhisperImageCount() {
        return bookWhisperImageCount;
    }

    public void setBookWhisperImageCount(int bookWhisperImageCount) {
        this.bookWhisperImageCount = bookWhisperImageCount;
    }

    public long getExpireAt() {
        return expireAt;
    }

    public void setExpireAt(long expireAt) {
        this.expireAt = expireAt;
    }

    public int getCoupleWallPaperCount() {
        return coupleWallPaperCount;
    }

    public void setCoupleWallPaperCount(int coupleWallPaperCount) {
        this.coupleWallPaperCount = coupleWallPaperCount;
    }

    public boolean isCoupleEntryTraceEnable() {
        return coupleEntryTraceEnable;
    }

    public void setCoupleEntryTraceEnable(boolean coupleEntryTraceEnable) {
        this.coupleEntryTraceEnable = coupleEntryTraceEnable;
    }

    public boolean isCoupleTopicTraceEnable() {
        return coupleTopicTraceEnable;
    }

    public void setCoupleTopicTraceEnable(boolean coupleTopicTraceEnable) {
        this.coupleTopicTraceEnable = coupleTopicTraceEnable;
    }

    public int getBookPictureCount() {
        return bookPictureCount;
    }

    public void setBookPictureCount(int bookPictureCount) {
        this.bookPictureCount = bookPictureCount;
    }

    public int getBookDiaryImageCount() {
        return bookDiaryImageCount;
    }

    public void setBookDiaryImageCount(int bookDiaryImageCount) {
        this.bookDiaryImageCount = bookDiaryImageCount;
    }

    public int getBookGiftImageCount() {
        return bookGiftImageCount;
    }

    public void setBookGiftImageCount(int bookGiftImageCount) {
        this.bookGiftImageCount = bookGiftImageCount;
    }

    public int getBookMeetImageCount() {
        return bookMeetImageCount;
    }

    public void setBookMeetImageCount(int bookMeetImageCount) {
        this.bookMeetImageCount = bookMeetImageCount;
    }

    public long getBookAudioTotalSize() {
        return bookAudioTotalSize;
    }

    public void setBookAudioTotalSize(long bookAudioTotalSize) {
        this.bookAudioTotalSize = bookAudioTotalSize;
    }

    public long getBookVideoTotalSize() {
        return bookVideoTotalSize;
    }

    public void setBookVideoTotalSize(long bookVideoTotalSize) {
        this.bookVideoTotalSize = bookVideoTotalSize;
    }

    public boolean isTopicImageEnable() {
        return topicImageEnable;
    }

    public void setTopicImageEnable(boolean topicImageEnable) {
        this.topicImageEnable = topicImageEnable;
    }
}
