package com.jiangzg.ita.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

import com.jiangzg.base.common.ConstantUtils;
import com.jiangzg.base.time.DateUtils;
import com.jiangzg.base.view.BarUtils;
import com.jiangzg.ita.R;
import com.jiangzg.ita.base.BaseActivity;
import com.jiangzg.ita.base.MyApp;
import com.jiangzg.ita.utils.PrefUtils;
import com.jiangzg.ita.view.GImageView;
import com.jiangzg.ita.view.GMultiLoveUpLayout;

import butterknife.BindView;

/**
 * Created by JiangZhiGuo on 2016/06/01
 * describe 启动界面
 */
public class WelcomeActivity extends BaseActivity<WelcomeActivity> {

    private static final long TransPageMillis = (long) (ConstantUtils.SEC * 1); // todo 修改

    @BindView(R.id.iv1)
    GImageView iv1;
    @BindView(R.id.iv2)
    GImageView iv2;
    @BindView(R.id.iv3)
    GImageView iv3;
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
        // todo logo要换
        // todo 开屏页本地获取并加载
        //ivBg.setImageResource();

        iv1.setUri(Uri.parse("https://i-ta.oss-cn-beijing.aliyuncs.com/ita-couple/cid-1/avatar/IMG_20171001_192104.jpg?Expires=1521697260&OSSAccessKeyId=TMP.AQEtWlX6JVaz3QJC_I1kLao0VkDfKpgU6C4bAyWKiIwnnjLbP7Q0uBDB1rx9MC4CFQDPUf4rgdXPSMeNgiAuH4rBRkPSMQIVAKMCLIF-3sTPEergscZuq59imKIf&Signature=44GjCtSU0T1kZRec1N9epAKcrmM%3D"));
        iv1.setClickFull(true);
        iv2.setUri(Uri.parse("https://i-ta.oss-cn-beijing.aliyuncs.com/ita-couple/cid-1/avatar/IMG_20171001_201829.jpg?Expires=1521697270&OSSAccessKeyId=TMP.AQEtWlX6JVaz3QJC_I1kLao0VkDfKpgU6C4bAyWKiIwnnjLbP7Q0uBDB1rx9MC4CFQDPUf4rgdXPSMeNgiAuH4rBRkPSMQIVAKMCLIF-3sTPEergscZuq59imKIf&Signature=wHKKvFS%2FhZrC1kMr%2B4felTRFu70%3D"));
        iv3.setUri(Uri.parse("https://i-ta.oss-cn-beijing.aliyuncs.com/ita-couple/cid-1/avatar/IMG_20171001_201953_1.jpg?Expires=1521693743&OSSAccessKeyId=TMP.AQEtWlX6JVaz3QJC_I1kLao0VkDfKpgU6C4bAyWKiIwnnjLbP7Q0uBDB1rx9MC4CFQDPUf4rgdXPSMeNgiAuH4rBRkPSMQIVAKMCLIF-3sTPEergscZuq59imKIf&Signature=c7B%2B01CzQ2ArRYjBG%2BrKBzVgJyw%3D"));
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        // todo ...非网络性init操作
        checkUser();
    }

    @Override
    protected void onStart() {
        super.onStart();
        //mlul.startUp(300); // todo 删除注释
    }

    @Override
    protected void onStop() {
        super.onStop();
        //mlul.cancelUp();
        finish(); // 记得关闭欢迎页
    }

    // 检查用户
    private void checkUser() {
        if (PrefUtils.noLogin()) {
            // 没有登录
            MyApp.get().getHandler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    //LoginActivity.goActivity(mActivity);
                    //HomeActivity.goActivity(mActivity); // todo 修改
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
            //            PrefUtils.clearUser();
            //            LoginActivity.goActivity(mActivity);
            //        } else {
            //            PrefUtils.setUser(user);
            //        }
            //        if (!PrefUtils.noCouple(couple)) {
            //             PrefUtils.setCouple(couple);
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
