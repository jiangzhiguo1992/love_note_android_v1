package com.jiangzg.mianmian.activity.book;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.mianmian.R;
import com.jiangzg.mianmian.activity.couple.CouplePairActivity;
import com.jiangzg.mianmian.activity.settings.HelpActivity;
import com.jiangzg.mianmian.base.BaseActivity;
import com.jiangzg.mianmian.domain.Couple;
import com.jiangzg.mianmian.domain.Help;
import com.jiangzg.mianmian.domain.Lock;
import com.jiangzg.mianmian.domain.Result;
import com.jiangzg.mianmian.domain.RxEvent;
import com.jiangzg.mianmian.helper.API;
import com.jiangzg.mianmian.helper.ConsHelper;
import com.jiangzg.mianmian.helper.RetrofitHelper;
import com.jiangzg.mianmian.helper.RxBus;
import com.jiangzg.mianmian.helper.SPHelper;
import com.jiangzg.mianmian.helper.ViewHelper;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;

public class LockActivity extends BaseActivity<LockActivity> {

    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.srl)
    SwipeRefreshLayout srl;
    @BindView(R.id.llContent)
    LinearLayout llContent;
    @BindView(R.id.ivLockClose)
    ImageView ivLockClose;
    @BindView(R.id.ivLockOpen)
    ImageView ivLockOpen;
    @BindView(R.id.btnToggleLock)
    Button btnToggleLock;
    @BindView(R.id.btnPwd)
    Button btnPwd;

    private Lock lock;
    private Call<Result> callGet;
    private Call<Result> callToggle;

    public static void goActivity(Fragment from) {
        if (Couple.isBreak(SPHelper.getCouple())) {
            // 无效配对
            CouplePairActivity.goActivity(from);
            return;
        }
        Intent intent = new Intent(from.getActivity(), LockActivity.class);
        // intent.putExtra();
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_lock;
    }

    @Override
    protected void initView(Intent intent, Bundle state) {
        ViewHelper.initTopBar(mActivity, tb, getString(R.string.pwd_lock), false);
        srl.setEnabled(false);
    }

    @Override
    protected void initData(Intent intent, Bundle state) {
        refreshData();
    }

    @Override
    protected void onFinish(Bundle state) {
        RetrofitHelper.cancel(callGet);
        RetrofitHelper.cancel(callToggle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.help, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuHelp: // 帮助
                HelpActivity.goActivity(mActivity, Help.INDEX_BOOK_LOCK);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick({R.id.btnToggleLock, R.id.btnPwd})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnToggleLock: // 开锁/关锁
                toggleLock();
                break;
            case R.id.btnPwd: // 设置/修改密码
                // TODO
                break;
        }
    }

    private void refreshData() {
        if (!srl.isRefreshing()) {
            srl.setRefreshing(true);
        }
        callGet = new RetrofitHelper().call(API.class).bookLockGet();
        RetrofitHelper.enqueue(callGet, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                srl.setRefreshing(false);
                lock = data.getLock();
                refreshView();
                // event
                RxEvent<Lock> event = new RxEvent<>(ConsHelper.EVENT_LOCK_REFRESH, lock);
                RxBus.post(event);
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                srl.setRefreshing(false);
            }
        });
    }

    private void refreshView() {
        llContent.setVisibility(View.VISIBLE);
        if (lock == null) {
            ivLockOpen.setVisibility(View.VISIBLE);
            ivLockClose.setVisibility(View.GONE);
            btnToggleLock.setVisibility(View.GONE);
            btnPwd.setText(R.string.set_password);
            return;
        }
        btnToggleLock.setVisibility(View.VISIBLE);
        btnPwd.setText(R.string.modify_password);
        if (lock.isLock()) {
            // 状态：关
            ivLockClose.setVisibility(View.VISIBLE);
            ivLockOpen.setVisibility(View.GONE);
            btnPwd.setText(R.string.open_lock);
        } else {
            // 状态：开
            ivLockOpen.setVisibility(View.VISIBLE);
            ivLockClose.setVisibility(View.GONE);
            btnPwd.setText(R.string.close_lock);
        }
    }

    private void toggleLock() {
        if (lock == null) {
            srl.setRefreshing(false);
            return;
        }
        if (!srl.isRefreshing()) {
            srl.setRefreshing(true);
        }
        callToggle = new RetrofitHelper().call(API.class).bookLockToggle();
        RetrofitHelper.enqueue(callToggle, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                srl.setRefreshing(false);
                lock = data.getLock();
                refreshView();
                // event
                RxEvent<Lock> event = new RxEvent<>(ConsHelper.EVENT_LOCK_REFRESH, lock);
                RxBus.post(event);
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                srl.setRefreshing(false);
            }
        });
    }

}
