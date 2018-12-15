package com.jiangzg.lovenote_admin.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.RelativeLayout;

import com.jiangzg.base.common.ConstantUtils;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.base.component.FragmentTrans;
import com.jiangzg.base.view.ToastUtils;
import com.jiangzg.lovenote_admin.R;
import com.jiangzg.lovenote_admin.base.BaseActivity;
import com.jiangzg.lovenote_admin.base.MyApp;
import com.jiangzg.lovenote_admin.domain.OssInfo;
import com.jiangzg.lovenote_admin.domain.Result;
import com.jiangzg.lovenote_admin.fragment.BroadcastFragment;
import com.jiangzg.lovenote_admin.fragment.CoupleFragment;
import com.jiangzg.lovenote_admin.fragment.NoticeFragment;
import com.jiangzg.lovenote_admin.fragment.UserFragment;
import com.jiangzg.lovenote_admin.fragment.VersionFragment;
import com.jiangzg.lovenote_admin.helper.API;
import com.jiangzg.lovenote_admin.helper.OssHelper;
import com.jiangzg.lovenote_admin.helper.RetrofitHelper;
import com.jiangzg.lovenote_admin.helper.SPHelper;
import com.jiangzg.lovenote_admin.helper.ViewHelper;

import butterknife.BindView;
import retrofit2.Call;

public class HomeActivity extends BaseActivity<HomeActivity> {

    @BindView(R.id.dl)
    DrawerLayout dl;
    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.rlContent)
    RelativeLayout rlContent;
    @BindView(R.id.nv)
    NavigationView nv;

    public static void goActivity(Activity from) {
        Intent intent = new Intent(from, HomeActivity.class);
        // intent.putExtra();
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_home;
    }

    @Override
    protected void initView(Intent intent, Bundle state) {
        ViewHelper.initTopBar(mActivity, tb, getString(R.string.app_name), false);
        ViewHelper.initDrawerLayout(mActivity, dl, tb);
        // listener
        nv.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menuUser: // user
                        UserFragment userFragment = UserFragment.newFragment();
                        FragmentTrans.replace(mActivity.mFragmentManager, userFragment, R.id.rlContent);
                        break;
                    case R.id.menuCouple: // couple
                        CoupleFragment coupleFragment = CoupleFragment.newFragment();
                        FragmentTrans.replace(mActivity.mFragmentManager, coupleFragment, R.id.rlContent);
                        break;
                    case R.id.menuVersion: // version
                        VersionFragment versionFragment = VersionFragment.newFragment();
                        FragmentTrans.replace(mActivity.mFragmentManager, versionFragment, R.id.rlContent);
                        break;
                    case R.id.menuNotice: // notice
                        NoticeFragment noticeFragment = NoticeFragment.newFragment();
                        FragmentTrans.replace(mActivity.mFragmentManager, noticeFragment, R.id.rlContent);
                        break;
                    case R.id.menuBroadcast: // broadcast
                        BroadcastFragment broadcastFragment = BroadcastFragment.newFragment();
                        FragmentTrans.replace(mActivity.mFragmentManager, broadcastFragment, R.id.rlContent);
                        break;
                }
                tb.setTitle(item.getTitle());
                dl.closeDrawer(GravityCompat.START);
                return true;
            }
        });
    }

    @Override
    protected void initData(Intent intent, Bundle state) {
        ossInfoUpdate();
        // init
        UserFragment userFragment = UserFragment.newFragment();
        FragmentTrans.replace(mActivity.mFragmentManager, userFragment, R.id.rlContent);
    }

    @Override
    protected void onFinish(Bundle state) {
    }

    @Override
    public void onBackPressed() {
        if (dl.isDrawerOpen(GravityCompat.START)) {
            //DrawerLayout打开时，返回键不会退出
            dl.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    // 更新oss信息
    private static void ossInfoUpdate() {
        Call<Result> call = new RetrofitHelper().call(API.class).ossGet();
        RetrofitHelper.enqueue(call, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                ToastUtils.show("oss更新成功！");
                OssInfo ossInfo = data.getOssInfo();
                // 刷新ossInfo
                SPHelper.setOssInfo(ossInfo);
                // 刷新ossClient
                OssHelper.refreshOssClient();
            }

            @Override
            public void onFailure(int code, final String message, Result.Data data) {
                ToastUtils.show("oss更新失败：" + message);
                MyApp.get().getHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ossInfoUpdate(); // 重复更新
                    }
                }, ConstantUtils.SEC * 5);
            }
        });
    }

}
