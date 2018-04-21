package com.jiangzg.ita.domain;

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
    private long expiration;
    private long interval;
    // path
    private String pathVersion;
    private String pathSuggest;
    private String pathCoupleAvatar;
    private String pathCoupleWall;
    private String pathBookAlbum;
    private String pathBookPicture;
    private String pathBookDiary;
    private String pathBookGift;
    private String pathBookAudio;
    private String pathBookVideo;
    private String pathBookThumb;

    public long getInterval() {
        return interval;
    }

    public void setInterval(long interval) {
        this.interval = interval;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    //public String getEndpoint() {
    //    return endpoint;
    //}

    //public void setEndpoint(String endpoint) {
    //    this.endpoint = endpoint;
    //}

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

    //public String getRegion() {
    //    return region;
    //}

    //public void setRegion(String region) {
    //    this.region = region;
    //}

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public long getExpiration() {
        return expiration;
    }

    public void setExpiration(long expiration) {
        this.expiration = expiration;
    }

    public String getPathVersion() {
        return pathVersion;
    }

    public void setPathVersion(String pathVersion) {
        this.pathVersion = pathVersion;
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

    public String getPathBookThumb() {
        return pathBookThumb;
    }

    public void setPathBookThumb(String pathBookThumb) {
        this.pathBookThumb = pathBookThumb;
    }
}
