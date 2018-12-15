package com.jiangzg.lovenote.model.engine;

import android.os.Parcel;
import android.os.Parcelable;

import com.jiangzg.lovenote.base.BaseObj;

import java.util.List;

/**
 * Created by JZG on 2018/12/15.
 */
public class Help extends BaseObj implements Parcelable {

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

    protected Help(Parcel in) {
        super(in);
        index = in.readInt();
        title = in.readString();
        desc = in.readString();
        subList = in.createTypedArrayList(Help.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(index);
        dest.writeString(title);
        dest.writeString(desc);
        dest.writeTypedList(subList);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Help> CREATOR = new Creator<Help>() {
        @Override
        public Help createFromParcel(Parcel in) {
            return new Help(in);
        }

        @Override
        public Help[] newArray(int size) {
            return new Help[size];
        }
    };

}
