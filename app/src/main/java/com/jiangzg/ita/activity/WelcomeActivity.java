package com.jiangzg.ita.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import com.jiangzg.base.common.ConstantUtils;
import com.jiangzg.base.view.BarUtils;
import com.jiangzg.ita.R;
import com.jiangzg.ita.activity.user.LoginActivity;
import com.jiangzg.ita.base.BaseActivity;
import com.jiangzg.ita.base.MyApp;
import com.jiangzg.ita.domain.Entry;
import com.jiangzg.ita.domain.Result;
import com.jiangzg.ita.helper.ApiHelper;
import com.jiangzg.ita.helper.PrefHelper;
import com.jiangzg.ita.third.API;
import com.jiangzg.ita.third.RetrofitHelper;
import com.jiangzg.ita.view.GMultiLoveUpLayout;

import butterknife.BindView;
import retrofit2.Call;

/**
 * Created by JiangZhiGuo on 2016/06/01
 * describe 启动界面
 */
public class WelcomeActivity extends BaseActivity<WelcomeActivity> {

    private static final long TransPageMillis = (long) (ConstantUtils.SEC * 3);

    @BindView(R.id.ivBg)
    ImageView ivBg;
    @BindView(R.id.mlul)
    GMultiLoveUpLayout mlul;

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
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        // todo ...非网络性init操作
        checkUser();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mlul.startUp(300);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mlul.cancelUp();
        finish(); // 记得关闭欢迎页
    }

    // 检查用户
    private void checkUser() {
        if (PrefHelper.noLogin()) {
            // 没有登录
            MyApp.get().getHandler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    LoginActivity.goActivity(mActivity);
                }
            }, TransPageMillis);
        } else {
            // 有token
            Entry entry = Entry.getEntry();
            Call<Result> call = new RetrofitHelper().call(API.class).entryPush(entry);
            RetrofitHelper.enqueueDelay(call, TransPageMillis, new RetrofitHelper.CallBack() {
                @Override
                public void onResponse(int code, String message, Result.Data data) {
                    ApiHelper.onEntryFinish(mActivity, data);
                }

                @Override
                public void onFailure() {
                }
            });
        }
    }

}
