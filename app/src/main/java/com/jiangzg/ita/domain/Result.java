package com.jiangzg.ita.domain;

/**
 * Created by JZG on 2017/12/15.
 * httpResult
 */

public class Result {

    public static final int ResultStatusSuc = 0;
    public static final int ResultStatusNoCp = 1;
    public static final int ResultCodeToast = 2;
    public static final int ResultCodeDialog = 3;

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
        private Version version;
        private User user;
        private Couple couple;

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
