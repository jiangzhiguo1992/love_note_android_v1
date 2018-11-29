package com.jiangzg.lovenote.model.entity;

import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.base.BaseObj;
import com.jiangzg.lovenote.base.MyApp;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JZG on 2018/4/12.
 * entry使用
 */
public class SuggestInfo extends BaseObj {

    private List<SuggestStatus> statusList;
    private List<SuggestKind> kindList;

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
        // kind
        List<SuggestKind> kindList = new ArrayList<>();
        kindList.add(new SuggestKind(Suggest.KIND_ALL, MyApp.get().getString(R.string.all)));
        kindList.add(new SuggestKind(Suggest.KIND_ERROR, MyApp.get().getString(R.string.program_error)));
        kindList.add(new SuggestKind(Suggest.KIND_FUNCTION, MyApp.get().getString(R.string.function_add)));
        kindList.add(new SuggestKind(Suggest.KIND_OPTIMIZE, MyApp.get().getString(R.string.experience_optimize)));
        kindList.add(new SuggestKind(Suggest.KIND_DEBUNK, MyApp.get().getString(R.string.just_debunk)));
        info.setKindList(kindList);
        return info;
    }

    public static String getStatusShow(int status) {
        SuggestInfo info = getInstance();
        List<SuggestStatus> statusList = info.getStatusList();
        // 不要全部
        for (int i = 1; i < statusList.size(); i++) {
            SuggestStatus s = statusList.get(i);
            if (s.getStatus() == status) {
                return s.getShow();
            }
        }
        return "";
    }

    public static String getKindShow(int kind) {
        SuggestInfo info = getInstance();
        List<SuggestKind> kindList = info.getKindList();
        // 不要全部
        for (int i = 1; i < kindList.size(); i++) {
            SuggestKind s = kindList.get(i);
            if (s.getKind() == kind) {
                return s.getShow();
            }
        }
        return "";
    }

    public static int getKindIndex(int kind) {
        SuggestInfo info = getInstance();
        List<SuggestKind> kindList = info.getKindList();
        // 不要全部
        for (int i = 1; i < kindList.size(); i++) {
            SuggestKind s = kindList.get(i);
            if (s.getKind() == kind) {
                return i;
            }
        }
        return 0;
    }

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
