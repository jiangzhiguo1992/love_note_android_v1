package com.jiangzg.mianmian.domain;

import com.jiangzg.mianmian.R;
import com.jiangzg.mianmian.base.MyApp;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JZG on 2018/4/12.
 * entry使用
 */
public class SuggestInfo extends BaseObj {

    private List<SuggestStatus> statusList;
    private List<SuggestType> typeList;

    public static SuggestInfo getInstance() {
        SuggestInfo info = new SuggestInfo();
        // status
        List<SuggestStatus> statusList = new ArrayList<>();
        statusList.add(new SuggestStatus(STATUS_VISIBLE, MyApp.get().getString(R.string.all)));
        statusList.add(new SuggestStatus(Suggest.STATUS_REPLY_NO, MyApp.get().getString(R.string.no_reply)));
        statusList.add(new SuggestStatus(Suggest.STATUS_REPLY_YES, MyApp.get().getString(R.string.already_reply)));
        statusList.add(new SuggestStatus(Suggest.STATUS_ACCEPT_NO, MyApp.get().getString(R.string.no_accept)));
        statusList.add(new SuggestStatus(Suggest.STATUS_ACCEPT_YES, MyApp.get().getString(R.string.already_accept)));
        statusList.add(new SuggestStatus(Suggest.STATUS_HANDLE_ING, MyApp.get().getString(R.string.handle_ing)));
        statusList.add(new SuggestStatus(Suggest.STATUS_HANDLE_OVER, MyApp.get().getString(R.string.handle_over)));
        info.setStatusList(statusList);
        // type
        List<SuggestType> typeList = new ArrayList<>();
        typeList.add(new SuggestType(Suggest.TYPE_ALL, MyApp.get().getString(R.string.all)));
        typeList.add(new SuggestType(Suggest.TYPE_ERROR, MyApp.get().getString(R.string.program_error)));
        typeList.add(new SuggestType(Suggest.TYPE_FUNCTION, MyApp.get().getString(R.string.function_add)));
        typeList.add(new SuggestType(Suggest.TYPE_OPTIMIZE, MyApp.get().getString(R.string.experience_optimize)));
        typeList.add(new SuggestType(Suggest.TYPE_DEBUNK, MyApp.get().getString(R.string.just_debunk)));
        info.setTypeList(typeList);
        return info;
    }

    public static String getStatusShow(int status) {
        SuggestInfo info = getInstance();
        List<SuggestStatus> statusList = info.getStatusList();
        for (SuggestStatus s : statusList) {
            if (s.getStatus() == status) {
                return s.getShow();
            }
        }
        return "";
    }

    public static String getTypeShow(int type) {
        SuggestInfo info = getInstance();
        List<SuggestType> typeList = info.getTypeList();
        for (SuggestType t : typeList) {
            if (t.getType() == type) {
                return t.getShow();
            }
        }
        return "";
    }

    public List<SuggestStatus> getStatusList() {
        return statusList;
    }

    public void setStatusList(List<SuggestStatus> statusList) {
        this.statusList = statusList;
    }

    public List<SuggestType> getTypeList() {
        return typeList;
    }

    public void setTypeList(List<SuggestType> typeList) {
        this.typeList = typeList;
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

    public static class SuggestType {
        private int type;
        private String show;

        public SuggestType(int type, String show) {
            this.type = type;
            this.show = show;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getShow() {
            return show;
        }

        public void setShow(String show) {
            this.show = show;
        }
    }

}
