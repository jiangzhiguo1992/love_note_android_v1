package com.jiangzg.lovenote.controller.activity.note;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.afollestad.materialdialogs.MaterialDialog;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.common.TimeUnit;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.base.view.ToastUtils;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.controller.activity.base.BaseActivity;
import com.jiangzg.lovenote.controller.activity.couple.CouplePairActivity;
import com.jiangzg.lovenote.controller.activity.settings.HelpActivity;
import com.jiangzg.lovenote.helper.common.ApiHelper;
import com.jiangzg.lovenote.helper.common.RxBus;
import com.jiangzg.lovenote.helper.common.SPHelper;
import com.jiangzg.lovenote.helper.common.UserHelper;
import com.jiangzg.lovenote.helper.system.RetrofitHelper;
import com.jiangzg.lovenote.helper.view.ViewHelper;
import com.jiangzg.lovenote.main.MyApp;
import com.jiangzg.lovenote.model.api.API;
import com.jiangzg.lovenote.model.api.Result;
import com.jiangzg.lovenote.model.entity.Lock;
import com.jiangzg.lovenote.model.entity.Sms;
import com.jiangzg.lovenote.model.entity.User;

import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTextChanged;
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

    @BindView(R.id.tilPwd)
    TextInputLayout tilPwd;
    @BindView(R.id.etPwd)
    TextInputEditText etPwd;
    @BindView(R.id.llCode)
    LinearLayout llCode;
    @BindView(R.id.etCode)
    TextInputEditText etCode;
    @BindView(R.id.btnSendCode)
    Button btnSendCode;
    @BindView(R.id.llOperate)
    LinearLayout llOperate;
    @BindView(R.id.btnCancel)
    Button btnCancel;
    @BindView(R.id.btnOk)
    Button btnOk;

    @BindView(R.id.btnToggleLock)
    Button btnToggleLock;
    @BindView(R.id.btnPwd)
    Button btnPwd;

    private Lock lock;
    private Call<Result> callGet;
    private Call<Result> callToggle;
    private Call<Result> callAddPwd;
    private Call<Result> callModifyPwd;
    private Call<Result> callTa;
    private Call<Result> callSms;
    private boolean open;
    private int countDownGo = -1;
    private Runnable countDownTask;

    public static void goActivity(Fragment from) {
        if (UserHelper.isCoupleBreak(SPHelper.getCouple())) {
            // 无效配对
            CouplePairActivity.goActivity(from);
            return;
        }
        Intent intent = new Intent(from.getActivity(), LockActivity.class);
        // intent.putExtra();
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    public static void goActivity(Context from) {
        if (UserHelper.isCoupleBreak(SPHelper.getCouple())) {
            // 无效配对
            CouplePairActivity.goActivity(from);
            return;
        }
        Intent intent = new Intent(from, LockActivity.class);
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
        ViewHelper.initTopBar(mActivity, tb, getString(R.string.pwd_lock), true);
        srl.setEnabled(false);
    }

    @Override
    protected void initData(Intent intent, Bundle state) {
        open = true; // 默认是要开锁
        refreshData();
    }

    @Override
    protected void onFinish(Bundle state) {
        RetrofitHelper.cancel(callGet);
        RetrofitHelper.cancel(callToggle);
        RetrofitHelper.cancel(callAddPwd);
        RetrofitHelper.cancel(callModifyPwd);
        RetrofitHelper.cancel(callTa);
        RetrofitHelper.cancel(callSms);
        stopCountDownTask();
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
                HelpActivity.goActivity(mActivity, HelpActivity.INDEX_NOTE_LOCK);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnTextChanged({R.id.etPwd, R.id.etCode})
    public void afterTextChanged(Editable s) {
        onInputChange();
    }

    @OnClick({R.id.btnToggleLock, R.id.btnPwd, R.id.btnCancel, R.id.btnOk, R.id.btnSendCode})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnToggleLock: // 开锁/关锁
                open = true;
                toggleLock();
                break;
            case R.id.btnPwd: // 设置/修改密码
                open = false;
                showOperaView();
                break;
            case R.id.btnCancel: // 取消
                refreshView();
                break;
            case R.id.btnOk: // 完成
                push();
                break;
            case R.id.btnSendCode: // 验证码
                getTaData();
                break;
        }
    }

    private void refreshData() {
        if (!srl.isRefreshing()) {
            srl.setRefreshing(true);
        }
        callGet = new RetrofitHelper().call(API.class).noteLockGet();
        RetrofitHelper.enqueue(callGet, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                srl.setRefreshing(false);
                lock = data.getLock();
                refreshView();
                // event
                RxBus.post(new RxBus.Event<>(RxBus.EVENT_LOCK_REFRESH, lock));
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                srl.setRefreshing(false);
            }
        });
    }

    private void refreshView() {
        stopCountDownTask(); // 清空倒计时
        llContent.setVisibility(View.VISIBLE);
        tilPwd.setVisibility(View.GONE);
        llCode.setVisibility(View.GONE);
        llOperate.setVisibility(View.GONE);
        btnPwd.setVisibility(View.VISIBLE);
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
            btnToggleLock.setText(R.string.open_lock);
        } else {
            // 状态：开
            ivLockOpen.setVisibility(View.VISIBLE);
            ivLockClose.setVisibility(View.GONE);
            btnToggleLock.setText(R.string.close_lock);
        }
    }

    private void toggleLock() {
        if (lock == null) return;
        if (lock.isLock()) {
            // 需要开锁视图
            showOperaView();
        } else {
            // 直接关锁
            toggleLockData("");
        }
    }

    private void toggleLockData(String pwd) {
        if (lock == null) {
            srl.setRefreshing(false);
            return;
        }
        if (!srl.isRefreshing()) {
            srl.setRefreshing(true);
        }
        Lock body = ApiHelper.getLockBody(pwd);
        callToggle = new RetrofitHelper().call(API.class).noteLockToggle(body);
        RetrofitHelper.enqueue(callToggle, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                srl.setRefreshing(false);
                lock = data.getLock();
                refreshView();
                // event
                RxBus.post(new RxBus.Event<>(RxBus.EVENT_LOCK_REFRESH, lock));
                // finish
                if (lock != null && !lock.isLock()) {
                    mActivity.finish();
                }
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                srl.setRefreshing(false);
            }
        });
    }

    private void onInputChange() {
        boolean code = true;
        if (lock != null && !open) {
            // 修改密码
            String c = etCode.getText().toString().trim();
            code = !StringUtils.isEmpty(c);
        }
        boolean pwd = etPwd.getText().toString().length() == SPHelper.getLimit().getNoteLockLength();
        btnOk.setEnabled(code && pwd);
        if (countDownGo >= 0) {
            btnSendCode.setEnabled(false);
        } else {
            btnSendCode.setEnabled(true);
        }
    }

    private void showOperaView() {
        tilPwd.setVisibility(View.VISIBLE);
        if (lock == null || open) { // 设置密码/开锁
            llCode.setVisibility(View.GONE);
        } else { // 修改密码
            llCode.setVisibility(View.VISIBLE);
        }
        llOperate.setVisibility(View.VISIBLE);
        btnToggleLock.setVisibility(View.GONE);
        btnPwd.setVisibility(View.GONE);
        // etPwd
        String format = getString(R.string.please_input_pwd_no_over_holder_text);
        String hint = String.format(Locale.getDefault(), format, SPHelper.getLimit().getNoteLockLength());
        tilPwd.setHint(hint);
        etPwd.setText("");
    }

    private void push() {
        String pwd = etPwd.getText().toString();
        if (lock == null) { // 设置密码
            addPwd(pwd);
        } else if (!open) { // 修改密码
            modifyPwd(pwd);
        } else { // 开锁
            toggleLockData(pwd);
        }
    }

    private void addPwd(String pwd) {
        if (!srl.isRefreshing()) {
            srl.setRefreshing(true);
        }
        Lock body = ApiHelper.getLockBody(pwd);
        callAddPwd = new RetrofitHelper().call(API.class).noteLockAdd(body);
        MaterialDialog loading = getLoading(getString(R.string.are_send_validate_code), true);
        RetrofitHelper.enqueue(callAddPwd, loading, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                srl.setRefreshing(false);
                lock = data.getLock();
                refreshView();
                // event
                RxBus.post(new RxBus.Event<>(RxBus.EVENT_LOCK_REFRESH, lock));
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                srl.setRefreshing(false);
            }
        });
    }

    private void modifyPwd(String pwd) {
        if (!srl.isRefreshing()) {
            srl.setRefreshing(true);
        }
        String code = etCode.getText().toString().trim();
        Lock body = ApiHelper.getLockBody(pwd);
        callModifyPwd = new RetrofitHelper().call(API.class).noteLockUpdatePwd(code, body);
        MaterialDialog loading = getLoading(getString(R.string.are_send_validate_code), true);
        RetrofitHelper.enqueue(callModifyPwd, loading, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                srl.setRefreshing(false);
                lock = data.getLock();
                refreshView();
                // event
                RxBus.post(new RxBus.Event<>(RxBus.EVENT_LOCK_REFRESH, lock));
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                srl.setRefreshing(false);
            }
        });
    }

    private void getTaData() {
        callTa = new RetrofitHelper().call(API.class).userGetTa();
        RetrofitHelper.enqueue(callTa, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                User ta = data.getUser();
                SPHelper.setTa(ta);
                sendCode();
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
            }
        });
    }

    private void sendCode() {
        btnSendCode.setEnabled(false);
        // 发送验证码
        User ta = SPHelper.getTa();
        if (ta == null || StringUtils.isEmpty(ta.getPhone())) {
            ToastUtils.show(getString(R.string.un_know_ta_phone));
            return;
        }
        Sms body = ApiHelper.getSmsLockBody(ta.getPhone());
        callSms = new RetrofitHelper().call(API.class).smsSend(body);
        MaterialDialog loading = getLoading(getString(R.string.are_send_validate_code), true);
        RetrofitHelper.enqueue(callSms, loading, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                countDownGo = 0;
                MyApp.get().getHandler().post(getCountDownTask());
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                btnSendCode.setEnabled(true);
            }
        });
    }

    private Runnable getCountDownTask() {
        final int countDownSec = SPHelper.getLimit().getSmsBetweenSec();
        if (countDownTask == null) {
            countDownTask = new Runnable() {
                @SuppressLint("SetTextI18n")
                @Override
                public void run() {
                    if (countDownGo < countDownSec) {
                        ++countDownGo;
                        btnSendCode.setText(String.valueOf(countDownSec - countDownGo) + "s");
                        MyApp.get().getHandler().postDelayed(this, TimeUnit.SEC);
                    } else {
                        stopCountDownTask();
                    }
                }
            };
        }
        return countDownTask;
    }

    private void stopCountDownTask() {
        countDownGo = -1;
        btnSendCode.setText(R.string.send_validate_code);
        onInputChange();
        if (countDownTask != null) {
            MyApp.get().getHandler().removeCallbacks(countDownTask);
            countDownTask = null;
        }
    }

}
