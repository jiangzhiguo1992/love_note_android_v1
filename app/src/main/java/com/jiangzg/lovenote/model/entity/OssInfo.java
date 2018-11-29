package com.jiangzg.lovenote.model.entity;

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
    private String region;
    private String domain;
    private String bucket;
    private long stsExpireTime;
    private long ossRefreshSec;
    private long urlExpireSec;
    // path
    private String pathLog;
    private String pathSuggest;
    private String pathCoupleAvatar;
    private String pathCoupleWall;
    private String pathNoteVideo;
    private String pathNoteVideoThumb;
    private String pathNoteAudio;
    private String pathNoteAlbum;
    private String pathNotePicture;
    private String pathNoteWhisper;
    private String pathNoteDiary;
    private String pathNoteGift;
    private String pathNoteFood;
    private String pathNoteMovie;
    private String pathTopicPost;
    private String pathMoreMatch;

    public String getPathNoteMovie() {
        return pathNoteMovie;
    }

    public void setPathNoteMovie(String pathNoteMovie) {
        this.pathNoteMovie = pathNoteMovie;
    }

    public String getPathMoreMatch() {
        return pathMoreMatch;
    }

    public void setPathMoreMatch(String pathMoreMatch) {
        this.pathMoreMatch = pathMoreMatch;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getPathTopicPost() {
        return pathTopicPost;
    }

    public void setPathTopicPost(String pathTopicPost) {
        this.pathTopicPost = pathTopicPost;
    }

    public long getUrlExpireSec() {
        return urlExpireSec;
    }

    public void setUrlExpireSec(long urlExpireSec) {
        this.urlExpireSec = urlExpireSec;
    }

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

    public long getStsExpireTime() {
        return stsExpireTime;
    }

    public void setStsExpireTime(long stsExpireTime) {
        this.stsExpireTime = stsExpireTime;
    }

    public long getOssRefreshSec() {
        return ossRefreshSec;
    }

    public void setOssRefreshSec(long ossRefreshSec) {
        this.ossRefreshSec = ossRefreshSec;
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
