package com.jiangzg.mianmian.domain;

import java.util.List;

/**
 * Created by JZG on 2018/4/12.
 * entry使用
 */
public class SuggestInfo extends BaseObj {

    private List<SuggestStatus> SuggestStatusList;
    private List<SuggestContentType> SuggestContentTypeList;

    public List<SuggestStatus> getSuggestStatusList() {
        return SuggestStatusList;
    }

    public void setSuggestStatusList(List<SuggestStatus> suggestStatusList) {
        SuggestStatusList = suggestStatusList;
    }

    public List<SuggestContentType> getSuggestContentTypeList() {
        return SuggestContentTypeList;
    }

    public void setSuggestContentTypeList(List<SuggestContentType> suggestContentTypeList) {
        SuggestContentTypeList = suggestContentTypeList;
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
