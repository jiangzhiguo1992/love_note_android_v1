package com.jiangzg.mianmian.domain;

/**
 * Created by JZG on 2018/3/16.
 * Oss对象
 */
public class OssInfo {

    // sts
    private String securityToken;
    private String accessKeyId;
    private String accessKeySecret;
    // oss
    //private String region;
    //private String endpoint;
    private String domain; // region 和 endpoint 一般不用
    private String bucket;
    private long expireTime;
    private long intervalSec;
    // path
    private String pathLog;
    private String pathSuggest;
    private String pathCoupleAvatar;
    private String pathCoupleWall;
    private String pathBookWhisper;
    private String pathBookDiary;
    private String pathBookAlbum;
    private String pathBookPicture;
    private String pathBookAudio;
    private String pathBookVideo;
    private String pathBookVideoThumb;
    private String pathBookGift;

    public String getPathLog() {
        return pathLog;
    }

    public void setPathLog(String pathLog) {
        this.pathLog = pathLog;
    }

    public long getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(long expireTime) {
        this.expireTime = expireTime;
    }

    public long getIntervalSec() {
        return intervalSec;
    }

    public void setIntervalSec(long intervalSec) {
        this.intervalSec = intervalSec;
    }

    public String getPathBookWhisper() {
        return pathBookWhisper;
    }

    public void setPathBookWhisper(String pathBookWhisper) {
        this.pathBookWhisper = pathBookWhisper;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getSecurityToken() {
        return securityToken;
    }

    public void setSecurityToken(String securityToken) {
        this.securityToken = securityToken;
    }

    public String getAccessKeyId() {
        return accessKeyId;
    }

    public void setAccessKeyId(String accessKeyId) {
        this.accessKeyId = accessKeyId;
    }

    public String getAccessKeySecret() {
        return accessKeySecret;
    }

    public void setAccessKeySecret(String accessKeySecret) {
        this.accessKeySecret = accessKeySecret;
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public String getPathSuggest() {
        return pathSuggest;
    }

    public void setPathSuggest(String pathSuggest) {
        this.pathSuggest = pathSuggest;
    }

    public String getPathCoupleAvatar() {
        return pathCoupleAvatar;
    }

    public void setPathCoupleAvatar(String pathCoupleAvatar) {
        this.pathCoupleAvatar = pathCoupleAvatar;
    }

    public String getPathCoupleWall() {
        return pathCoupleWall;
    }

    public void setPathCoupleWall(String pathCoupleWall) {
        this.pathCoupleWall = pathCoupleWall;
    }

    public String getPathBookAlbum() {
        return pathBookAlbum;
    }

    public void setPathBookAlbum(String pathBookAlbum) {
        this.pathBookAlbum = pathBookAlbum;
    }

    public String getPathBookPicture() {
        return pathBookPicture;
    }

    public void setPathBookPicture(String pathBookPicture) {
        this.pathBookPicture = pathBookPicture;
    }

    public String getPathBookDiary() {
        return pathBookDiary;
    }

    public void setPathBookDiary(String pathBookDiary) {
        this.pathBookDiary = pathBookDiary;
    }

    public String getPathBookGift() {
        return pathBookGift;
    }

    public void setPathBookGift(String pathBookGift) {
        this.pathBookGift = pathBookGift;
    }

    public String getPathBookAudio() {
        return pathBookAudio;
    }

    public void setPathBookAudio(String pathBookAudio) {
        this.pathBookAudio = pathBookAudio;
    }

    public String getPathBookVideo() {
        return pathBookVideo;
    }

    public void setPathBookVideo(String pathBookVideo) {
        this.pathBookVideo = pathBookVideo;
    }

    public String getPathBookVideoThumb() {
        return pathBookVideoThumb;
    }

    public void setPathBookVideoThumb(String pathBookVideoThumb) {
        this.pathBookVideoThumb = pathBookVideoThumb;
    }
}
