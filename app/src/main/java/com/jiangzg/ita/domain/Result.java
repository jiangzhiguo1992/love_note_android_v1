package com.jiangzg.ita.domain;

import java.io.Serializable;
import java.util.List;

/**
 * Created by JZG on 2017/12/15.
 * httpResult
 */

public class Result implements Serializable {

    public static final int ResultCodeOK = 0;
    public static final int ResultCodeToast = 1;
    public static final int ResultCodeDialog = 2;
    public static final int ResultCodeNoUserInfo = 3;
    public static final int ResultCodeNoCP = 4;
    public static final int ResultCodeNoVIP = 5;

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
        private List<Version> versionList;
        private Version version;
        private User user;
        private Couple couple;
        private int countDownSec;
        private OssInfo ossInfo;
        private int noticeNoRead;
        private VipPower vipPower;
        private String taPhone;
        private String title;
        private String message;
        private String btnGood;
        private String btnBad;
        private String show;
        private Help help;
        private SuggestInfo suggestInfo;
        private long total;
        private Suggest suggest;
        private List<Suggest> suggestList;

        public long getTotal() {
            return total;
        }

        public void setTotal(long total) {
            this.total = total;
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

        public SuggestInfo getSuggestInfo() {
            return suggestInfo;
        }

        public void setSuggestInfo(SuggestInfo suggestInfo) {
            this.suggestInfo = suggestInfo;
        }

        public String getShow() {
            return show;
        }

        public void setShow(String show) {
            this.show = show;
        }

        public Help getHelp() {
            return help;
        }

        public void setHelp(Help help) {
            this.help = help;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getBtnGood() {
            return btnGood;
        }

        public void setBtnGood(String btnGood) {
            this.btnGood = btnGood;
        }

        public String getBtnBad() {
            return btnBad;
        }

        public void setBtnBad(String btnBad) {
            this.btnBad = btnBad;
        }

        public String getTaPhone() {
            return taPhone;
        }

        public void setTaPhone(String taPhone) {
            this.taPhone = taPhone;
        }

        public VipPower getVipPower() {
            return vipPower;
        }

        public void setVipPower(VipPower vipPower) {
            this.vipPower = vipPower;
        }

        public int getNoticeNoRead() {
            return noticeNoRead;
        }

        public void setNoticeNoRead(int noticeNoRead) {
            this.noticeNoRead = noticeNoRead;
        }

        public OssInfo getOssInfo() {
            return ossInfo;
        }

        public void setOssInfo(OssInfo ossInfo) {
            this.ossInfo = ossInfo;
        }

        public List<Version> getVersionList() {
            return versionList;
        }

        public void setVersionList(List<Version> versionList) {
            this.versionList = versionList;
        }

        public int getCountDownSec() {
            return countDownSec;
        }

        public void setCountDownSec(int countDownSec) {
            this.countDownSec = countDownSec;
        }

        public Version getVersion() {
            return version;
        }

        public void setVersion(Version version) {
            this.version = version;
        }

        public User getUser() {
            return user;
        }

        public void setUser(User user) {
            this.user = user;
        }

        public Couple getCouple() {
            return couple;
        }

        public void setCouple(Couple couple) {
            this.couple = couple;
        }
    }
}
