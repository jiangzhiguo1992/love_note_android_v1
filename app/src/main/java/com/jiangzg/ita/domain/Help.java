package com.jiangzg.ita.domain;

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

    private int contentType;
    private String title;
    private String desc;
    private List<Content> contentList;
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

    public List<Content> getContentList() {
        return contentList;
    }

    public void setContentList(List<Content> contentList) {
        this.contentList = contentList;
    }

    public List<Help> getSubList() {
        return subList;
    }

    public void setSubList(List<Help> subList) {
        this.subList = subList;
    }

    public static class Content {
        private String question;
        private String answer;

        public Content(String question, String answer) {
            this.question = question;
            this.answer = answer;
        }

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
    }
}
