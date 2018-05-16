package com.jiangzg.mianmian.domain;

import java.util.List;

/**
 * Created by JZG on 2018/4/12.
 * entry使用
 */
public class SuggestInfo extends BaseObj {

    private List<SuggestStatus> statusList;
    private List<SuggestContentType> contentTypeList;

    public List<SuggestStatus> getStatusList() {
        return statusList;
    }

    public void setStatusList(List<SuggestStatus> statusList) {
        this.statusList = statusList;
    }

    public List<SuggestContentType> getContentTypeList() {
        return contentTypeList;
    }

    public void setContentTypeList(List<SuggestContentType> contentTypeList) {
        this.contentTypeList = contentTypeList;
    }

    public static class SuggestStatus {
        private int status;
        private String show;

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

    public static class SuggestContentType {
        private int contentType;
        private String show;

        public int getContentType() {
            return contentType;
        }

        public void setContentType(int contentType) {
            this.contentType = contentType;
        }

        public String getShow() {
            return show;
        }

        public void setShow(String show) {
            this.show = show;
        }
    }

}
