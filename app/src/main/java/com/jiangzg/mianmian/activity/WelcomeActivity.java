package com.jiangzg.mianmian.activity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.jiangzg.base.common.ConstantUtils;
import com.jiangzg.base.common.FileUtils;
import com.jiangzg.base.time.DateUtils;
import com.jiangzg.base.view.BarUtils;
import com.jiangzg.mianmian.R;
import com.jiangzg.mianmian.activity.user.LoginActivity;
import com.jiangzg.mianmian.base.BaseActivity;
import com.jiangzg.mianmian.base.MyApp;
import com.jiangzg.mianmian.domain.Entry;
import com.jiangzg.mianmian.domain.Result;
import com.jiangzg.mianmian.helper.API;
import com.jiangzg.mianmian.helper.ApiHelper;
import com.jiangzg.mianmian.helper.CheckHelper;
import com.jiangzg.mianmian.helper.ImgResHelper;
import com.jiangzg.mianmian.helper.RetrofitHelper;
import com.jiangzg.mianmian.view.GImageNativeView;

import java.io.File;

import butterknife.BindView;
import retrofit2.Call;

/**
 * Created by JiangZhiGuo on 2016/06/01
 * describe 启动界面
 */
public class WelcomeActivity extends BaseActivity<WelcomeActivity> {

    private static final long TransPageMillis = (long) (ConstantUtils.SEC * 2);

    @BindView(R.id.ivBg)
    GImageNativeView ivBg;

    private Call<Result> call;
    private boolean exits;

    @Override
    protected int getView(Intent intent) {
        BarUtils.setStatusBarTrans(mActivity, true);
        BarUtils.setNavigationBarTrans(mActivity, true);
        return R.layout.activity_welcome;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        exits = false;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        // wallPaper
        File wallPaper = ImgResHelper.getWallPaperRandom();
        if (!FileUtils.isFileEmpty(wallPaper)) {
            ivBg.setDataFile(wallPaper);
            startAnim();
        }
        // TODO ...非网络性init操作
        checkUser();
    }

    private void startAnim() {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(ivBg, "scaleX", 1f, 1.2F, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(ivBg, "scaleY", 1f, 1.2F, 1f);
        AnimatorSet set = new AnimatorSet();
        set.setDuration(10000);
        set.playTogether(scaleX, scaleY);
        set.setInterpolator(new AccelerateDecelerateInterpolator());
        set.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish(); // 记得关闭欢迎页
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        exits = true;
        RetrofitHelper.cancel(call);
        MyApp.get().getHandler().removeCallbacks(checkUser);
    }

    // 检查用户
    private void checkUser() {
        if (CheckHelper.noLogin()) {
            // 没有登录
            MyApp.get().getHandler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    LoginActivity.goActivity(mActivity);
                }
            }, TransPageMillis);
        } else {
            // 有token TODO 上传本地异常，然后再删除
            final long startTime = DateUtils.getCurrentLong();
            Entry entry = ApiHelper.getEntryBody();
            call = new RetrofitHelper().call(API.class).entryPush(entry);
            RetrofitHelper.enqueue(call, null, new RetrofitHelper.CallBack() {
                @Override
                public void onResponse(int code, String message, Result.Data data) {
                    ApiHelper.onEntryFinish(startTime, TransPageMillis, mActivity, code, data);
                }

                @Override
                public void onFailure(String errMsg) {
                    // 一直请求
                    MyApp.get().getHandler().postDelayed(checkUser, 2000);
                }
            });
        }
    }


    private Runnable checkUser = new Runnable() {
        @Override
        public void run() {
            if (exits) return;
            WelcomeActivity.this.checkUser();
        }
    };

}
