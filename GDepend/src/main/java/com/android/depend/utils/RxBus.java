package com.android.depend.utils;

import com.android.depend.domain.RxEvent;

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

    // object是订阅的类型 ,List<Subject>里时候所有订阅此频道的订阅者
    private static HashMap<Object, List<Subject>> maps = new HashMap<>();

    /* 注意:这个是即时发送消息的，没有注册这么一说 (可用于线程间的操作)*/
    public static <T> Observable<T> post(final T send, Subscriber<? super T> subscriber) {
        Observable<T> observable = Observable.create(new Observable.OnSubscribe<T>() {
            @Override
            public void call(Subscriber<? super T> subscriber) {
                // TODO: 2017/4/26 这里可以做一些耗时操作
                subscriber.onNext(send); // 发送事件
                subscriber.onCompleted(); // 完成事件
            }
        });
        observable.subscribeOn(Schedulers.io()) // 执行线程
                .onBackpressureBuffer() // 解决连续发送数据过快时的异常，toList也行
                .observeOn(AndroidSchedulers.mainThread()) // 回调线程
                .subscribe(subscriber); // 回调内容
        return observable;
    }

    /* 发送频道消息(已注册的频道里的观察者才会收到) */
    public static <T> void post(RxEvent<T> rxEvent) {
        int id = rxEvent.getChannel();
        T data = rxEvent.getData();
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

}
