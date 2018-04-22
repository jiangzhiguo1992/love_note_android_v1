package com.jiangzg.mianmian.activity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.jiangzg.base.common.ConstantUtils;
import com.jiangzg.base.component.ActivityStack;
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
import com.jiangzg.mianmian.helper.DialogHelper;
import com.jiangzg.mianmian.helper.RetrofitHelper;

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
        // TODO 开屏页本地获取并加载
        //ivBg.setImageResource();
        startAnim();
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
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
            // TODO 上传本地异常，然后再删除
            final long startTime = DateUtils.getCurrentLong();
            Entry entry = ApiHelper.getEntryBody();
            Call<Result> call = new RetrofitHelper().call(API.class).entryPush(entry);
            RetrofitHelper.enqueue(call, null, new RetrofitHelper.CallBack() {
                @Override
                public void onResponse(int code, String message, Result.Data data) {
                    ApiHelper.onEntryFinish(startTime, TransPageMillis, mActivity, code, data);
                }

                @Override
                public void onFailure(String errMsg) {
                    // TODO 这个去掉 换成递归获取
                    MaterialDialog dialog = DialogHelper.getBuild(mActivity)
                            .cancelable(true)
                            .canceledOnTouchOutside(false)
                            .title(errMsg)
                            .content(R.string.app_start_err_detail_please_ask_official_contact_method_qq)
                            .negativeText(R.string.i_know)
                            .dismissListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialog) {
                                    ActivityStack.finishAllActivity();
                                }
                            })
                            .build();
                    DialogHelper.showWithAnim(dialog);
                }
            });
        }
    }

}
