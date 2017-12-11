package com.android.depend.domain;

import java.io.Serializable;

/**
 * Created by JiangZhiGuo on 2016-11-25.
 * describe 用来RxBus传输的实体类
 */
public class RxEvent<T> implements Serializable {

    private int channel; // 订阅的频道
    private T data; // 传输的数据

    public RxEvent(int channel, T data) {
        this.channel = channel;
        this.data = data;
    }

    public int getChannel() {
        return channel;
    }

    public void setChannel(int channel) {
        this.channel = channel;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RxEvent<?> rxEvent = (RxEvent<?>) o;

        if (channel != rxEvent.channel) return false;
        return data != null ? data.equals(rxEvent.data) : rxEvent.data == null;

    }

    @Override
    public int hashCode() {
        int result = channel;
        result = 31 * result + (data != null ? data.hashCode() : 0);
        return result;
    }
}
