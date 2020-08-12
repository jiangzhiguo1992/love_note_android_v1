package com.jiangzg.lovenote.model.engine;

import rx.Observable;

/**
 * Created by JZG on 2019/1/13.
 * RxRegister
 */
public class RxRegister {

    private int event;
    private Observable ob;

    public RxRegister() {
    }

    public RxRegister(int event, Observable ob) {
        this.event = event;
        this.ob = ob;
    }

    public int getEvent() {
        return event;
    }

    public void setEvent(int event) {
        this.event = event;
    }

    public Observable getOb() {
        return ob;
    }

    public void setOb(Observable ob) {
        this.ob = ob;
    }
}
