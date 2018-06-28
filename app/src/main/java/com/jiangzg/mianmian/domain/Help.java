package com.jiangzg.mianmian.domain;

import java.util.List;

/**
 * Created by JZG on 2018/3/12.
 * 帮助文档
 */
public class Help extends BaseObj {

    public static final int INDEX_ALL = 0;
    public static final int INDEX_USER_INFO_SET = 1;
    public static final int INDEX_COUPLE_PAIR = 2;
    public static final int INDEX_COUPLE_HOME = 3;
    public static final int INDEX_SUGGEST_HOME = 4;
    public static final int INDEX_SUGGEST_DETAIL = 5;
    public static final int INDEX_SUGGEST_ADD = 6;
    public static final int INDEX_SUGGEST_MINE = 7;
    public static final int INDEX_SUGGEST_FOLLOW = 8;
    public static final int INDEX_WALL_PAPER_ADD = 9;
    public static final int INDEX_COUPLE_INFO = 10;
    public static final int INDEX_BOOK_HOME = 11;
    public static final int INDEX_DIARY_LIST = 12;
    public static final int INDEX_DIARY_EDIT = 13;
    public static final int INDEX_DIARY_DETAIL = 14;
    public static final int INDEX_WORD_LIST = 15;
    public static final int INDEX_WHISPER_LIST = 16;
    public static final int INDEX_ALBUM_LIST = 17;
    public static final int INDEX_ALBUM_EDIT = 18;
    public static final int INDEX_COUPLE_PLACE = 19;
    public static final int INDEX_COUPLE_WEATHER = 20;
    public static final int INDEX_NOTICE_LIST = 21;
    public static final int INDEX_PICTURE_EDIT = 22;
    public static final int INDEX_PICTURE_LIST = 23;
    public static final int INDEX_DREAM_LIST = 24;
    public static final int INDEX_DREAM_DETAIL = 25;
    public static final int INDEX_DREAM_EDIT = 26;
    public static final int INDEX_GIFT_LIST = 27;
    public static final int INDEX_GIFT_EDIT = 28;
    public static final int INDEX_PROMISE_LIST = 29;
    public static final int INDEX_PROMISE_DETAIL = 30;
    public static final int INDEX_PROMISE_EDIT = 31;
    public static final int INDEX_ANGRY_LIST = 32;
    public static final int INDEX_ANGRY_EDIT = 33;

    private int index;
    private String title;
    private String desc;
    private List<HelpContent> contentList;
    private List<Help> subList;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
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
