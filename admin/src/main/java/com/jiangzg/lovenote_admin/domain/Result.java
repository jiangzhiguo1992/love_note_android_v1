package com.jiangzg.lovenote_admin.domain;

import java.io.Serializable;
import java.util.List;

/**
 * Created by JZG on 2017/12/15.
 * httpResult
 */
public class Result implements Serializable {

    public static final int RESULT_CODE_OK = 0;
    public static final int RESULT_CODE_TOAST = 10;
    public static final int RESULT_CODE_DIALOG = 11;
    public static final int RESULT_CODE_NO_USER_INFO = 20;
    public static final int RESULT_CODE_NO_CP = 30;
    public static final int RESULT_CODE_NO_VIP = 40;

    private int code;
    private String message;
    private Data data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public static class Data {

        private String show;
        private long total;
        private User user;
        private OssInfo ossInfo;
        private List<User> userList;
        private List<Sms> smsList;
        private List<Version> versionList;
        private List<Notice> noticeList;
        private List<Broadcast> broadcastList;
        private List<FiledInfo> infoList;
        private List<Entry> entryList;
        private List<Api> apiList;
        private Suggest suggest;
        private List<Suggest> suggestList;
        private SuggestComment suggestComment;
        private List<SuggestComment> suggestCommentList;

        public SuggestComment getSuggestComment() {
            return suggestComment;
        }

        public void setSuggestComment(SuggestComment suggestComment) {
            this.suggestComment = suggestComment;
        }

        public Suggest getSuggest() {
            return suggest;
        }

        public void setSuggest(Suggest suggest) {
            this.suggest = suggest;
        }

        public List<Suggest> getSuggestList() {
            return suggestList;
        }

        public void setSuggestList(List<Suggest> suggestList) {
            this.suggestList = suggestList;
        }

        public List<SuggestComment> getSuggestCommentList() {
            return suggestCommentList;
        }

        public void setSuggestCommentList(List<SuggestComment> suggestCommentList) {
            this.suggestCommentList = suggestCommentList;
        }

        public List<Api> getApiList() {
            return apiList;
        }

        public void setApiList(List<Api> apiList) {
            this.apiList = apiList;
        }

        public List<FiledInfo> getInfoList() {
            return infoList;
        }

        public void setInfoList(List<FiledInfo> infoList) {
            this.infoList = infoList;
        }

        public List<Entry> getEntryList() {
            return entryList;
        }

        public void setEntryList(List<Entry> entryList) {
            this.entryList = entryList;
        }

        public List<User> getUserList() {
            return userList;
        }

        public void setUserList(List<User> userList) {
            this.userList = userList;
        }

        public List<Sms> getSmsList() {
            return smsList;
        }

        public void setSmsList(List<Sms> smsList) {
            this.smsList = smsList;
        }

        public List<Broadcast> getBroadcastList() {
            return broadcastList;
        }

        public void setBroadcastList(List<Broadcast> broadcastList) {
            this.broadcastList = broadcastList;
        }

        public List<Notice> getNoticeList() {
            return noticeList;
        }

        public void setNoticeList(List<Notice> noticeList) {
            this.noticeList = noticeList;
        }

        public String getShow() {
            return show;
        }

        public void setShow(String show) {
            this.show = show;
        }

        public long getTotal() {
            return total;
        }

        public void setTotal(long total) {
            this.total = total;
        }

        public List<Version> getVersionList() {
            return versionList;
        }

        public void setVersionList(List<Version> versionList) {
            this.versionList = versionList;
        }

        public OssInfo getOssInfo() {
            return ossInfo;
        }

        public void setOssInfo(OssInfo ossInfo) {
            this.ossInfo = ossInfo;
        }

        public User getUser() {
            return user;
        }

        public void setUser(User user) {
            this.user = user;
        }

    }
}
