package com.jiangzg.ita.domain;

import java.util.List;

/**
 * Created by JZG on 2017/12/15.
 * httpResult
 */

public class Result {

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
