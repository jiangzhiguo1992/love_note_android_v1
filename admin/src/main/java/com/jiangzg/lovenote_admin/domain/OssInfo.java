package com.jiangzg.lovenote_admin.domain;

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
    private String pathVersion;
    private String pathNotice;
    private String pathBroadcast;

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

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
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

    public long getUrlExpireSec() {
        return urlExpireSec;
    }

    public void setUrlExpireSec(long urlExpireSec) {
        this.urlExpireSec = urlExpireSec;
    }

    public String getPathVersion() {
        return pathVersion;
    }

    public void setPathVersion(String pathVersion) {
        this.pathVersion = pathVersion;
    }

    public String getPathNotice() {
        return pathNotice;
    }

    public void setPathNotice(String pathNotice) {
        this.pathNotice = pathNotice;
    }

    public String getPathBroadcast() {
        return pathBroadcast;
    }

    public void setPathBroadcast(String pathBroadcast) {
        this.pathBroadcast = pathBroadcast;
    }

}
