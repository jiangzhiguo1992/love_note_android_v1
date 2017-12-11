package com.android.depend.utils;

import android.app.Activity;

import com.android.base.component.activity.ActivityStack;
import com.android.base.function.PermUtils;
import com.tbruyelle.rxpermissions.RxPermissions;

import rx.Observable;
import rx.functions.Action0;
import rx.functions.Action1;

/**
 * Created by Jiangzg on 2016/11/18.
 * 权限验证框架管理
 */
public class RxPermUtils {

    public interface PermissionListener {
        /* 同意使用权限 */
        void onAgree();
    }

    /**
     * 请求权限,返回结果一起处理
     */
    public static void request(final PermissionListener listener, final String... permissions) {
        Activity activity = ActivityStack.getStack().lastElement();
        if (activity == null) return;
        if (PermUtils.isPermissionOK(activity, permissions)) return;
        RxPermissions rxPermissions = new RxPermissions(activity);
        Observable<Boolean> request = rxPermissions.request(permissions);
        request.subscribe(new Action1<Boolean>() {
            @Override
            public void call(final Boolean aBoolean) {
                if (aBoolean) { // 同意使用权限
                    if (listener != null) {
                        listener.onAgree();
                    }
                } else {
                    LogUtils.e("拒绝使用权限");
                }
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                LogUtils.e("请求权限抛出异常");
            }
        }, new Action0() {
            @Override
            public void call() {
                LogUtils.d("请求权限完成");
            }
        });
    }

}
