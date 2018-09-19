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
        private List<FiledInfo> infoList;
        private User user;
        private OssInfo ossInfo;
        private List<User> userList;
        private double birth;
        private List<Sms> smsList;
        private List<Version> versionList;
        private List<Notice> noticeList;
        private List<Broadcast> broadcastList;
        private List<Entry> entryList;
        private List<Api> apiList;
        private Suggest suggest;
        private List<Suggest> suggestList;
        private SuggestComment suggestComment;
        private List<SuggestComment> suggestCommentList;
        private Couple couple;
        private List<Couple> coupleList;
        private List<Couple.State> coupleStateList;
        private List<Place> placeList;
        private List<Bill> billList;
        private double amount;
        private List<Vip> vipList;
        private List<Sign> signList;
        private List<Coin> coinList;
        private List<Trends> trendsList;
        private List<PostKindInfo> postKindInfoList;
        private Post post;
        private List<Post> postList;
        private PostComment postComment;
        private List<PostComment> postCommentList;

        public List<PostKindInfo> getPostKindInfoList() {
            return postKindInfoList;
        }

        public void setPostKindInfoList(List<PostKindInfo> postKindInfoList) {
            this.postKindInfoList = postKindInfoList;
        }

        public Post getPost() {
            return post;
        }

        public void setPost(Post post) {
            this.post = post;
        }

        public List<Post> getPostList() {
            return postList;
        }

        public void setPostList(List<Post> postList) {
            this.postList = postList;
        }

        public PostComment getPostComment() {
            return postComment;
        }

        public void setPostComment(PostComment postComment) {
            this.postComment = postComment;
        }

        public List<PostComment> getPostCommentList() {
            return postCommentList;
        }

        public void setPostCommentList(List<PostComment> postCommentList) {
            this.postCommentList = postCommentList;
        }

        public List<Trends> getTrendsList() {
            return trendsList;
        }

        public void setTrendsList(List<Trends> trendsList) {
            this.trendsList = trendsList;
        }

        public List<Sign> getSignList() {
            return signList;
        }

        public void setSignList(List<Sign> signList) {
            this.signList = signList;
        }

        public List<Place> getPlaceList() {
            return placeList;
        }

        public void setPlaceList(List<Place> placeList) {
            this.placeList = placeList;
        }

        public double getBirth() {
            return birth;
        }

        public void setBirth(double birth) {
            this.birth = birth;
        }

        public List<Vip> getVipList() {
            return vipList;
        }

        public void setVipList(List<Vip> vipList) {
            this.vipList = vipList;
        }

        public List<Coin> getCoinList() {
            return coinList;
        }

        public void setCoinList(List<Coin> coinList) {
            this.coinList = coinList;
        }

        public List<Bill> getBillList() {
            return billList;
        }

        public void setBillList(List<Bill> billList) {
            this.billList = billList;
        }

        public double getAmount() {
            return amount;
        }

        public void setAmount(double amount) {
            this.amount = amount;
        }

        public Couple getCouple() {
            return couple;
        }

        public void setCouple(Couple couple) {
            this.couple = couple;
        }

        public List<Couple> getCoupleList() {
            return coupleList;
        }

        public void setCoupleList(List<Couple> coupleList) {
            this.coupleList = coupleList;
        }

        public List<Couple.State> getCoupleStateList() {
            return coupleStateList;
        }

        public void setCoupleStateList(List<Couple.State> coupleStateList) {
            this.coupleStateList = coupleStateList;
        }

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
