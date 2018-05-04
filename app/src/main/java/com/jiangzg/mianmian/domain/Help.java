package com.jiangzg.mianmian.domain;

import java.util.List;

/**
 * Created by JZG on 2018/3/12.
 * 帮助文档
 */
public class Help extends BaseObj {

    public static final int TYPE_ALL = 0;
    public static final int TYPE_USER_INFO_SET = 1;
    public static final int TYPE_COUPLE_PAIR = 2;
    public static final int TYPE_COUPLE_HOME = 3;
    public static final int TYPE_SUGGEST_HOME = 4;
    public static final int TYPE_SUGGEST_DETAIL = 5;
    public static final int TYPE_SUGGEST_ADD = 6;
    public static final int TYPE_SUGGEST_MINE = 7;
    public static final int TYPE_SUGGEST_FOLLOW = 8;
    public static final int TYPE_WALL_PAPER_ADD = 9;
    public static final int TYPE_COUPLE_INFO = 10;
    public static final int TYPE_BOOK_HOME = 11;
    public static final int TYPE_DIARY_LIST = 12;
    public static final int TYPE_DIARY_EDIT = 13;
    public static final int TYPE_DIARY_DETAIL = 14;
    public static final int TYPE_WORD_LIST = 15;
    public static final int TYPE_WHISPER_LIST = 16;
    public static final int TYPE_ALBUM_LIST = 17;
    public static final int TYPE_ALBUM_EDIT = 18;
    public static final int TYPE_COUPLE_PLACE = 19;
    public static final int TYPE_COUPLE_WEATHER = 20;

    private int contentType;
    private String title;
    private String desc;
    private List<HelpContent> contentList;
    private List<Help> subList;

    public int getContentType() {
        return contentType;
    }

    public void setContentType(int contentType) {
        this.contentType = contentType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public List<HelpContent> getContentList() {
        return contentList;
    }

    public void setContentList(List<HelpContent> contentList) {
        this.contentList = contentList;
    }

    public List<Help> getSubList() {
        return subList;
    }

    public void setSubList(List<Help> subList) {
        this.subList = subList;
    }

    public static class HelpContent {
        private String question;
        private String answer;

        public String getQuestion() {
            return question;
        }

        public void setQuestion(String question) {
            this.question = question;
        }

        public String getAnswer() {
            return answer;
        }

        public void setAnswer(String answer) {
            this.answer = answer;
        }

        public HelpContent() {
        }
    }

    public Help() {
    }

}
