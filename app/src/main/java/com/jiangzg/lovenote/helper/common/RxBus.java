package com.jiangzg.lovenote.helper.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;

/**
 * Created by JiangZhiGuo on 2016-11-24.
 * describe RxJava实现的Bus管理类
 */
public class RxBus {

    public static final int EVENT_MAP_SELECT = 101; // 选择
    public static final int EVENT_VIDEO_SELECT = 102;
    public static final int EVENT_ALBUM_SELECT = 103;
    public static final int EVENT_DIARY_SELECT = 105;
    public static final int EVENT_AWARD_RULE_SELECT = 106;
    public static final int EVENT_TRAVEL_SELECT = 107;
    public static final int EVENT_GIFT_SELECT = 108;
    public static final int EVENT_FOOD_SELECT = 109;
    public static final int EVENT_PROMISE_SELECT = 110;
    public static final int EVENT_MOVIE_SELECT = 111;
    public static final int EVENT_PAY_WX_RESULT = 310;
    public static final int EVENT_SUGGEST_LIST_REFRESH = 1000; // 建议
    public static final int EVENT_SUGGEST_LIST_ITEM_DELETE = 1001;
    public static final int EVENT_SUGGEST_LIST_ITEM_REFRESH = 1002;
    public static final int EVENT_SUGGEST_DETAIL_REFRESH = 1003;
    public static final int EVENT_USER_REFRESH = 2000; // 用户
    public static final int EVENT_COUPLE_REFRESH = 3000; // 配对
    public static final int EVENT_WALL_PAPER_REFRESH = 3100; // 墙纸
    public static final int EVENT_LOCK_REFRESH = 4010; // 密码锁
    public static final int EVENT_CUSTOM_REFRESH = 4020; // 功能定制
    public static final int EVENT_SHY_LIST_REFRESH = 4030; // 羞羞
    public static final int EVENT_SHY_LIST_ITEM_DELETE = 4031;
    public static final int EVENT_MENSES_ = 4040; // 羞羞
    public static final int EVENT_SLEEP_LIST_ITEM_DELETE = 4050; // 睡眠
    public static final int EVENT_AUDIO_LIST_REFRESH = 4060; // 音频
    public static final int EVENT_AUDIO_LIST_ITEM_DELETE = 4061;
    public static final int EVENT_VIDEO_LIST_REFRESH = 4070; // 视频
    public static final int EVENT_VIDEO_LIST_ITEM_DELETE = 4071;
    public static final int EVENT_ALBUM_LIST_REFRESH = 4080; // 相册
    public static final int EVENT_ALBUM_LIST_ITEM_DELETE = 4081;
    public static final int EVENT_ALBUM_LIST_ITEM_REFRESH = 4082;
    public static final int EVENT_ALBUM_DETAIL_REFRESH = 4083;
    public static final int EVENT_PICTURE_LIST_REFRESH = 4090; // 照片
    public static final int EVENT_PICTURE_LIST_ITEM_REFRESH = 4091;
    public static final int EVENT_PICTURE_LIST_ITEM_DELETE = 4092;
    public static final int EVENT_SOUVENIR_LIST_REFRESH = 4110; // 纪念日
    public static final int EVENT_SOUVENIR_LIST_ITEM_DELETE = 4111;
    public static final int EVENT_SOUVENIR_LIST_ITEM_REFRESH = 4112;
    public static final int EVENT_SOUVENIR_DETAIL_REFRESH = 4113;
    public static final int EVENT_WORD_LIST_ITEM_DELETE = 4120; // 留言
    public static final int EVENT_DIARY_LIST_REFRESH = 4130; // 日记
    public static final int EVENT_DIARY_LIST_ITEM_DELETE = 4131;
    public static final int EVENT_DIARY_LIST_ITEM_REFRESH = 4132;
    public static final int EVENT_DIARY_DETAIL_REFRESH = 4133;
    public static final int EVENT_AWARD_LIST_REFRESH = 4140; // 约定
    public static final int EVENT_AWARD_LIST_ITEM_DELETE = 4141;
    public static final int EVENT_AWARD_RULE_LIST_REFRESH = 4142;
    public static final int EVENT_AWARD_RULE_LIST_ITEM_DELETE = 4143;
    public static final int EVENT_DREAM_LIST_REFRESH = 4150; // 梦境
    public static final int EVENT_DREAM_LIST_ITEM_DELETE = 4151;
    public static final int EVENT_DREAM_LIST_ITEM_REFRESH = 4152;
    public static final int EVENT_DREAM_DETAIL_REFRESH = 4153;
    public static final int EVENT_GIFT_LIST_REFRESH = 4160; // 礼物
    public static final int EVENT_GIFT_LIST_ITEM_DELETE = 4161;
    public static final int EVENT_GIFT_LIST_ITEM_REFRESH = 4162;
    public static final int EVENT_FOOD_LIST_REFRESH = 4170; // 美食
    public static final int EVENT_FOOD_LIST_ITEM_DELETE = 4171;
    public static final int EVENT_FOOD_LIST_ITEM_REFRESH = 4172;
    public static final int EVENT_TRAVEL_LIST_REFRESH = 4180; // 游记
    public static final int EVENT_TRAVEL_LIST_ITEM_DELETE = 4181;
    public static final int EVENT_TRAVEL_LIST_ITEM_REFRESH = 4182;
    public static final int EVENT_TRAVEL_DETAIL_REFRESH = 4183;
    public static final int EVENT_TRAVEL_EDIT_ADD_PLACE = 4184;
    public static final int EVENT_ANGRY_LIST_REFRESH = 4190; // 生气
    public static final int EVENT_ANGRY_LIST_ITEM_DELETE = 4191;
    public static final int EVENT_ANGRY_LIST_ITEM_REFRESH = 4192;
    public static final int EVENT_PROMISE_LIST_REFRESH = 4200; // 承诺
    public static final int EVENT_PROMISE_LIST_ITEM_DELETE = 4201;
    public static final int EVENT_PROMISE_LIST_ITEM_REFRESH = 4202;
    public static final int EVENT_PROMISE_DETAIL_REFRESH = 4203;
    public static final int EVENT_MOVIE_LIST_REFRESH = 4210; // 电影
    public static final int EVENT_MOVIE_LIST_ITEM_DELETE = 4211;
    public static final int EVENT_MOVIE_LIST_ITEM_REFRESH = 4212;
    public static final int EVENT_POST_GO_TOP = 5100;
    public static final int EVENT_POST_SEARCH_ALL = 5101;
    public static final int EVENT_POST_SEARCH_OFFICIAL = 5102;
    public static final int EVENT_POST_SEARCH_WELL = 5103;
    public static final int EVENT_POST_LIST_REFRESH = 5200;
    public static final int EVENT_POST_LIST_ITEM_DELETE = 5201;
    public static final int EVENT_POST_LIST_ITEM_REFRESH = 5202;
    public static final int EVENT_POST_DETAIL_REFRESH = 5203;
    public static final int EVENT_POST_COMMENT_LIST_REFRESH = 5300;
    public static final int EVENT_POST_COMMENT_LIST_ITEM_DELETE = 5301;
    public static final int EVENT_POST_COMMENT_LIST_ITEM_REFRESH = 5302;
    public static final int EVENT_POST_COMMENT_DETAIL_REFRESH = 5303;
    public static final int EVENT_VIP_INFO_REFRESH = 6100;
    public static final int EVENT_COIN_INFO_REFRESH = 6200;

    // object是订阅的类型 ,List<Subject>里时候所有订阅此频道的订阅者
    private static HashMap<Object, List<Subject>> maps = new HashMap<>();

    /* 注意:这个是即时发送消息的，没有注册这么一说 (可用于线程间的操作)*/
    public static <T> Observable<T> post(final T send, Subscriber<? super T> subscriber) {
        Observable<T> observable = Observable.create(subscriber1 -> {
            // 这里可以做一些耗时操作
            subscriber1.onNext(send); // 发送事件
            subscriber1.onCompleted(); // 完成事件
        });
        observable.subscribeOn(Schedulers.io()) // 执行线程
                .onBackpressureBuffer() // 解决连续发送数据过快时的异常，toList也行
                .observeOn(AndroidSchedulers.mainThread()) // 回调线程
                .subscribe(subscriber); // 回调内容
        return observable;
    }

    /* 发送频道消息(已注册的频道里的观察者才会收到) */
    public static <T> void post(Event<T> event) {
        int id = event.getChannel();
        T data = event.getData();
        if (id != 0 && data != null) {
            next(id, data);
        }
    }

    /* 注册频道 */
    public static <T> Observable<T> register(int eventId, Action1<? super T> onNext) {
        Observable<T> observable = createObservable(eventId); // 获取观察者
        // 执行线程和回调内容必须连起来,否则回调线程会不正确
        observable.subscribeOn(Schedulers.immediate()) // 执行线程(当前线程)
                .onBackpressureBuffer() // 解决连续发送数据过快时的异常，toList也行
                .observeOn(AndroidSchedulers.mainThread()) // 回调线程
                .subscribe(onNext); // 回调内容
        return observable;
    }

    public static <T> Observable<T> register(int eventId, Action1<? super T> onNext,
                                             final Action1<Throwable> onError,
                                             final Action0 onCompleted) {
        Observable<T> observable = createObservable(eventId); // 获取观察者
        // Rx最好连着点出来,不连着点，下面全是Bug
        observable.subscribeOn(Schedulers.immediate()) // 当前线程
                // 解决连续发送数据过快时的异常，toList也行
                .onBackpressureBuffer()
                // 接受线程和事件处理必须连起来,否则回调线程会不正确
                .observeOn(AndroidSchedulers.mainThread()).subscribe(onNext, onError, onCompleted);
        return observable;
    }

    /* 注销频道里的单个观察者 */
    public static void unregister(int eventId, Observable observable) {
        if (observable == null) return;
        removeObservable(eventId, observable); // 移除此event绑定的观察者
        observable = null;
    }

    /* 获取观察者 */
    @SuppressWarnings("unchecked")
    private static <T> Observable<T> createObservable(int tag) {
        List<Subject> subjects = maps.get(tag);
        if (subjects == null) { // 这个tag没有订阅者
            subjects = new ArrayList<>();
            maps.put(tag, subjects); // 创建这个tag的订阅者集合
        }
        Subject<T, T> subject = PublishSubject.create(); // 获取观察者
        subjects.add(subject); // 向这个tag下的订阅者集合里添加订阅者
        return subject;
    }

    /* 观察者发送消息给订阅者 */
    @SuppressWarnings("unchecked")
    private static <T> void next(int tag, T t) {
        List<Subject> subjects = maps.get(tag);
        if (subjects != null && !subjects.isEmpty()) {
            for (Subject s : subjects) { // 向所有订阅tag的对象发送消息
                s.onNext(t);
            }
        }
    }

    /* 移除观察者 */
    @SuppressWarnings("unchecked")
    private static void removeObservable(int tag, Observable observable) {
        List<Subject> subjects = maps.get(tag);
        if (subjects != null) { // 这个tag的订阅者集合不为空
            subjects.remove((Subject) observable);
            if (subjects.isEmpty()) {  // 这个tag的订阅者没有时，去掉tag
                maps.remove(tag);
            }
        }
    }

    /**
     * Created by JiangZhiGuo on 2016-11-25.
     * describe 用来RxBus传输的实体类
     */
    public static class Event<T> implements Serializable {

        private int channel; // 订阅的频道
        private T data; // 传输的数据

        public Event(int channel, T data) {
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

            Event<?> event = (Event<?>) o;

            return channel == event.channel && (data != null ? data.equals(event.data) : event.data == null);
        }

        @Override
        public int hashCode() {
            int result = channel;
            result = 31 * result + (data != null ? data.hashCode() : 0);
            return result;
        }
    }
}
