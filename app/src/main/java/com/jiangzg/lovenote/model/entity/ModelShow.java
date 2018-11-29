package com.jiangzg.lovenote.model.entity;

/**
 * Created by JZG on 2018/11/7.
 */
public class ModelShow {

    private boolean couple;
    private boolean couplePlace;
    private boolean coupleWeather;
    private boolean note;
    private boolean topic;
    private boolean more;
    private boolean moreVip;
    private boolean moreCoin;
    private boolean moreMatch;
    private boolean moreFeature;

    public boolean isCouplePlace() {
        return couplePlace;
    }

    public void setCouplePlace(boolean couplePlace) {
        this.couplePlace = couplePlace;
    }

    public boolean isCoupleWeather() {
        return coupleWeather;
    }

    public void setCoupleWeather(boolean coupleWeather) {
        this.coupleWeather = coupleWeather;
    }

    public boolean isMoreFeature() {
        return moreFeature;
    }

    public void setMoreFeature(boolean moreFeature) {
        this.moreFeature = moreFeature;
    }

    public boolean isCouple() {
        return couple;
    }

    public void setCouple(boolean couple) {
        this.couple = couple;
    }

    public boolean isNote() {
        return note;
    }

    public void setNote(boolean note) {
        this.note = note;
    }

    public boolean isTopic() {
        return topic;
    }

    public void setTopic(boolean topic) {
        this.topic = topic;
    }

    public boolean isMore() {
        return more;
    }

    public void setMore(boolean more) {
        this.more = more;
    }

    public boolean isMoreVip() {
        return moreVip;
    }

    public void setMoreVip(boolean moreVip) {
        this.moreVip = moreVip;
    }

    public boolean isMoreCoin() {
        return moreCoin;
    }

    public void setMoreCoin(boolean moreCoin) {
        this.moreCoin = moreCoin;
    }

    public boolean isMoreMatch() {
        return moreMatch;
    }

    public void setMoreMatch(boolean moreMatch) {
        this.moreMatch = moreMatch;
    }

    public ModelShow() {
    }

}
