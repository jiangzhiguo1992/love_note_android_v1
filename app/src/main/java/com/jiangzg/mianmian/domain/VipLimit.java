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
    private boolean bookPictureEnable;
    private boolean bookDiaryImageEnable;
    private boolean bookGiftImageEnable;
    private boolean bookTrackImageEnable;
    private boolean bookAudioEnable;
    private boolean bookVideoEnable;
    private long bookAudioTotalSize;
    private long bookVideoTotalSize;
    private boolean topicImageEnable;

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

    public boolean isBookPictureEnable() {
        return bookPictureEnable;
    }

    public void setBookPictureEnable(boolean bookPictureEnable) {
        this.bookPictureEnable = bookPictureEnable;
    }

    public boolean isBookDiaryImageEnable() {
        return bookDiaryImageEnable;
    }

    public void setBookDiaryImageEnable(boolean bookDiaryImageEnable) {
        this.bookDiaryImageEnable = bookDiaryImageEnable;
    }

    public boolean isBookGiftImageEnable() {
        return bookGiftImageEnable;
    }

    public void setBookGiftImageEnable(boolean bookGiftImageEnable) {
        this.bookGiftImageEnable = bookGiftImageEnable;
    }

    public boolean isBookTrackImageEnable() {
        return bookTrackImageEnable;
    }

    public void setBookTrackImageEnable(boolean bookTrackImageEnable) {
        this.bookTrackImageEnable = bookTrackImageEnable;
    }

    public boolean isBookAudioEnable() {
        return bookAudioEnable;
    }

    public void setBookAudioEnable(boolean bookAudioEnable) {
        this.bookAudioEnable = bookAudioEnable;
    }

    public boolean isBookVideoEnable() {
        return bookVideoEnable;
    }

    public void setBookVideoEnable(boolean bookVideoEnable) {
        this.bookVideoEnable = bookVideoEnable;
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
