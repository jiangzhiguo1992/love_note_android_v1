package com.jiangzg.lovenote.model.entity;

import com.jiangzg.lovenote.base.BaseObj;

import java.util.List;

/**
 * Created by JZG on 2018/4/12.
 * suggestInfo
 */
public class SuggestInfo extends BaseObj {

    private List<SuggestStatus> statusList;
    private List<SuggestKind> kindList;

    public List<SuggestStatus> getStatusList() {
        return statusList;
    }

    public void setStatusList(List<SuggestStatus> statusList) {
        this.statusList = statusList;
    }

    public List<SuggestKind> getKindList() {
        return kindList;
    }

    public void setKindList(List<SuggestKind> kindList) {
        this.kindList = kindList;
    }

    public static class SuggestStatus {
        private int status;
        private String show;

        public SuggestStatus(int status, String show) {
            this.status = status;
            this.show = show;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getShow() {
            return show;
        }

        public void setShow(String show) {
            this.show = show;
        }
    }

    public static class SuggestKind {
        private int kind;
        private String show;

        public SuggestKind(int kind, String show) {
            this.kind = kind;
            this.show = show;
        }

        public int getKind() {
            return kind;
        }

        public void setKind(int kind) {
            this.kind = kind;
        }

        public String getShow() {
            return show;
        }

        public void setShow(String show) {
            this.show = show;
        }
    }

}
