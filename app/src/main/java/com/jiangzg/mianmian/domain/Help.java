package com.jiangzg.mianmian.domain;

import java.util.List;

/**
 * Created by JZG on 2018/3/12.
 * 帮助文档
 */
public class Help extends BaseObj {

    public static final int INDEX_ALL = 0;
    public static final int INDEX_USER_INFO_SET = 10;
    public static final int INDEX_NOTICE_LIST = 21;
    public static final int INDEX_SUGGEST_HOME = 31;
    public static final int INDEX_SUGGEST_MINE = 32;
    public static final int INDEX_SUGGEST_FOLLOW = 33;
    public static final int INDEX_SUGGEST_DETAIL = 34;
    public static final int INDEX_SUGGEST_ADD = 35;
    public static final int INDEX_COUPLE_HOME = 100;
    public static final int INDEX_COUPLE_PAIR = 110;
    public static final int INDEX_COUPLE_INFO = 120;
    public static final int INDEX_WALL_PAPER_ADD = 121;
    public static final int INDEX_COUPLE_PLACE = 122;
    public static final int INDEX_COUPLE_WEATHER = 123;
    public static final int INDEX_BOOK_HOME = 200;
    public static final int INDEX_MENSES = 207;
    public static final int INDEX_SHY = 208;
    public static final int INDEX_SLEEP = 209;
    public static final int INDEX_WORD_LIST = 210;
    public static final int INDEX_WHISPER_LIST = 211;
    public static final int INDEX_DIARY_LIST = 212;
    public static final int INDEX_DIARY_EDIT = 213;
    public static final int INDEX_DIARY_DETAIL = 214;
    public static final int INDEX_ALBUM_LIST = 215;
    public static final int INDEX_ALBUM_EDIT = 216;
    public static final int INDEX_PICTURE_EDIT = 217;
    public static final int INDEX_PICTURE_LIST = 218;
    public static final int INDEX_AUDIO_LIST = 220;
    public static final int INDEX_AUDIO_EDIT = 221;
    public static final int INDEX_VIDEO_LIST = 222;
    public static final int INDEX_VIDEO_EDIT = 223;
    public static final int INDEX_FOOD_LIST = 224;
    public static final int INDEX_FOOD_EDIT = 225;
    public static final int INDEX_TRAVEL_LIST = 226;
    public static final int INDEX_TRAVEL_DETAIL = 227;
    public static final int INDEX_TRAVEL_EDIT = 228;
    public static final int INDEX_TRAVEL_PLACE_EDIT = 229;
    public static final int INDEX_GIFT_LIST = 230;
    public static final int INDEX_GIFT_EDIT = 231;
    public static final int INDEX_PROMISE_LIST = 232;
    public static final int INDEX_PROMISE_DETAIL = 233;
    public static final int INDEX_PROMISE_EDIT = 234;
    public static final int INDEX_ANGRY_LIST = 235;
    public static final int INDEX_ANGRY_EDIT = 236;
    public static final int INDEX_ANGRY_DETAIL = 237;
    public static final int INDEX_DREAM_LIST = 238;
    public static final int INDEX_DREAM_DETAIL = 239;
    public static final int INDEX_DREAM_EDIT = 240;
    public static final int INDEX_AWARD_LIST = 241;
    public static final int INDEX_AWARD_EDIT = 242;
    public static final int INDEX_AWARD_RULE_LIST = 243;
    public static final int INDEX_AWARD_RULE_EDIT = 244;

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
