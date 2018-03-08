package com.jiangzg.ita.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import com.jiangzg.base.common.ConstantUtils;
import com.jiangzg.base.time.DateUtils;
import com.jiangzg.base.view.BarUtils;
import com.jiangzg.ita.R;
import com.jiangzg.ita.activity.user.LoginActivity;
import com.jiangzg.ita.base.BaseActivity;
import com.jiangzg.ita.base.MyApp;
import com.jiangzg.ita.utils.UserPreference;

import butterknife.BindView;

/**
 * Created by JiangZhiGuo on 2016/06/01
 * describe 启动界面
 */
public class WelcomeActivity extends BaseActivity<WelcomeActivity> {

    private static final long TransPageMillis = (long) (ConstantUtils.SEC * 1); // todo 修改

    @BindView(R.id.ivBg)
    ImageView ivBg;

    @Override
    protected int getView(Intent intent) {
        BarUtils.setStatusBarTrans(mActivity, true);
        BarUtils.setNavigationBarTrans(mActivity, true);
        // todo logo要换
        return R.layout.activity_welcome;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        // todo 开屏页本地获取并加载
        //ivBg.setImageResource();
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        // todo ...非网络性init操作
        checkUser();
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish(); // 记得关闭欢迎页
    }

    // 检查用户
    private void checkUser() {
        if (UserPreference.noLogin()) {
            // 没有登录
            MyApp.get().getHandler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    //LoginActivity.goActivity(mActivity);
                    HomeActivity.goActivity(mActivity); // todo 修改
                }
            }, TransPageMillis);
        } else {
            // 有token
            final long startTime = DateUtils.getCurrentLong();
            // todo 登录api调用 + 本地数据存储
            //Entry entry = Entry.getEntry();
            //Call<Result> call = new RetroManager().call(API.class).entry(entry);
            //RetroManager.enqueue(call, null, new RetroManager.CallBack() {
            //    @Override
            //    public void onResponse(int code, Result.Data data) {
            //        User user = data.getUser();
            //        Couple couple = data.getCouple();
            //        Version version = data.getVersion();
            //        if (user == null) {
            //            UserPreference.clearUser();
            //            LoginActivity.goActivity(mActivity);
            //        } else {
            //            UserPreference.setUser(user);
            //        }
            //        if (!UserPreference.noCouple(couple)) {
            //             UserPreference.setCouple(couple);
            //        }
            //        if (version != null) {
            //
            //        }
            //        goHome(startTime);
            //    }
            //
            //    @Override
            //    public void onFailure() {
            //        LogUtils.e("---->");
            //    }
            //});
            goHome(startTime);
        }
    }

    // 跳转home页面
    private void goHome(long startTime) {
        long endTime = DateUtils.getCurrentLong();
        long between = endTime - startTime;
        if (between >= TransPageMillis) {
            // 间隔时间太大
            HomeActivity.goActivity(mActivity);
        } else {
            // 间隔时间太小
            MyApp.get().getHandler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    HomeActivity.goActivity(mActivity);
                }
            }, TransPageMillis - between);
        }
    }

}
