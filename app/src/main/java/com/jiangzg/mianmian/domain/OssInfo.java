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
    private String pathNoteWhisper;
    private String pathNoteDiary;
    private String pathNoteAlbum;
    private String pathNotePicture;
    private String pathNoteAudio;
    private String pathNoteVideo;
    private String pathNoteVideoThumb;
    private String pathNoteFood;
    private String pathNoteGift;

    public String getPathNoteFood() {
        return pathNoteFood;
    }

    public void setPathNoteFood(String pathNoteFood) {
        this.pathNoteFood = pathNoteFood;
    }

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

    public String getPathNoteWhisper() {
        return pathNoteWhisper;
    }

    public void setPathNoteWhisper(String pathNoteWhisper) {
        this.pathNoteWhisper = pathNoteWhisper;
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

    public String getPathNoteAlbum() {
        return pathNoteAlbum;
    }

    public void setPathNoteAlbum(String pathNoteAlbum) {
        this.pathNoteAlbum = pathNoteAlbum;
    }

    public String getPathNotePicture() {
        return pathNotePicture;
    }

    public void setPathNotePicture(String pathNotePicture) {
        this.pathNotePicture = pathNotePicture;
    }

    public String getPathNoteDiary() {
        return pathNoteDiary;
    }

    public void setPathNoteDiary(String pathNoteDiary) {
        this.pathNoteDiary = pathNoteDiary;
    }

    public String getPathNoteGift() {
        return pathNoteGift;
    }

    public void setPathNoteGift(String pathNoteGift) {
        this.pathNoteGift = pathNoteGift;
    }

    public String getPathNoteAudio() {
        return pathNoteAudio;
    }

    public void setPathNoteAudio(String pathNoteAudio) {
        this.pathNoteAudio = pathNoteAudio;
    }

    public String getPathNoteVideo() {
        return pathNoteVideo;
    }

    public void setPathNoteVideo(String pathNoteVideo) {
        this.pathNoteVideo = pathNoteVideo;
    }

    public String getPathNoteVideoThumb() {
        return pathNoteVideoThumb;
    }

    public void setPathNoteVideoThumb(String pathNoteVideoThumb) {
        this.pathNoteVideoThumb = pathNoteVideoThumb;
    }
}
