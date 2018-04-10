package com.jiangzg.ita.activity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;

import com.jiangzg.base.common.ConstantUtils;
import com.jiangzg.base.time.DateUtils;
import com.jiangzg.base.view.BarUtils;
import com.jiangzg.ita.R;
import com.jiangzg.ita.activity.user.LoginActivity;
import com.jiangzg.ita.base.BaseActivity;
import com.jiangzg.ita.base.MyApp;
import com.jiangzg.ita.domain.Entry;
import com.jiangzg.ita.domain.Result;
import com.jiangzg.ita.helper.ApiHelper;
import com.jiangzg.ita.helper.CheckHelper;
import com.jiangzg.ita.helper.API;
import com.jiangzg.ita.helper.RetrofitHelper;

import butterknife.BindView;
import retrofit2.Call;

/**
 * Created by JiangZhiGuo on 2016/06/01
 * describe 启动界面
 */
public class WelcomeActivity extends BaseActivity<WelcomeActivity> {

    private static final long TransPageMillis = (long) (ConstantUtils.SEC * 2);

    @BindView(R.id.ivBg)
    ImageView ivBg;

    @Override
    protected int getView(Intent intent) {
        BarUtils.setStatusBarTrans(mActivity, true);
        BarUtils.setNavigationBarTrans(mActivity, true);
        return R.layout.activity_welcome;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        // todo 开屏页本地获取并加载
        //ivBg.setImageResource();
        startAnim();
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        // todo ...非网络性init操作
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
            // 有token
            final long startTime = DateUtils.getCurrentLong();
            Entry entry = ApiHelper.getEntryBody();
            Call<Result> call = new RetrofitHelper().call(API.class).entryPush(entry);
            RetrofitHelper.enqueue(call, null, new RetrofitHelper.CallBack() {
                @Override
                public void onResponse(int code, String message, Result.Data data) {
                    ApiHelper.onEntryFinish(startTime, TransPageMillis, mActivity, code, data);
                }

                @Override
                public void onFailure() {
                }
            });
            // todo 失败处理
        }
    }

}
