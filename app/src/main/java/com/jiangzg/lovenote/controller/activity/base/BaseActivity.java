package com.jiangzg.lovenote.controller.activity.base;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.transition.Fade;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;

import com.afollestad.materialdialogs.MaterialDialog;
import com.jiangzg.base.application.AppUtils;
import com.jiangzg.base.common.DateUtils;
import com.jiangzg.base.component.ActivityStack;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.base.system.InputUtils;
import com.jiangzg.base.system.PermUtils;
import com.jiangzg.base.view.BarUtils;
import com.jiangzg.base.view.ScreenUtils;
import com.jiangzg.base.view.ToastUtils;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.controller.activity.main.HomeActivity;
import com.jiangzg.lovenote.helper.common.RxBus;
import com.jiangzg.lovenote.helper.system.RetrofitHelper;
import com.jiangzg.lovenote.helper.view.DialogHelper;
import com.jiangzg.lovenote.helper.view.ThemeHelper;
import com.jiangzg.lovenote.model.api.Result;
import com.jiangzg.lovenote.model.engine.RxRegister;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import rx.Observable;

/**
 * Created by JiangZhiGuo on 2016-12-2.
 * describe Activity的基类
 */
public abstract class BaseActivity<T> extends AppCompatActivity {

    // activityFrom
    public static final int ACT_LIST_FROM_BROWSE = 0;
    public static final int ACT_LIST_FROM_SELECT = 1;
    public static final int ACT_EDIT_FROM_ADD = 0;
    public static final int ACT_EDIT_FROM_UPDATE = 1;
    public static final int ACT_DETAIL_FROM_ID = 0;
    public static final int ACT_DETAIL_FROM_OBJ = 1;
    // requestCode
    public static final int REQUEST_APP_INFO = 1001;
    public static final int REQUEST_DEVICE_INFO = 1002;
    public static final int REQUEST_PICTURE = 1003;
    public static final int REQUEST_AUDIO = 1004;
    public static final int REQUEST_VIDEO = 1005;
    public static final int REQUEST_CAMERA = 1006;
    public static final int REQUEST_CROP = 1007;
    public static final int REQUEST_INSTALL = 1008;
    public static final int REQUEST_CONTEXT_ALERT = 1009;
    public static final int REQUEST_SCAN = 1010;
    public static final int REQUEST_CONTACT = 1011;
    public static final int REQUEST_LOCATION = 1012;
    public static final int REQUEST_NOTE_PICTURE = 1013;

    public BaseActivity mActivity;
    public FragmentManager mFragmentManager;
    //public View mRootView;
    public int mRootViewId;
    private Unbinder mUnBinder;
    private MaterialDialog mLoading;
    private Long mLastExitTime = 0L; //最后一次退出时间
    private boolean isLoad = false; // 是否加载过数据，主要用于换肤
    private List<Call<Result>> apiList = new ArrayList<>(); // api队列
    private List<RxRegister> busList = new ArrayList<>(); // bus队列

    /* activity跳转demo */
    private static void goActivity(Activity from) {
        Intent intent = new Intent(from, BaseActivity.class);
        // intent.putExtra();
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    /* 初始layout(setContent之前调用) */
    protected abstract int getView(Intent intent);

    /* 实例化View */
    protected abstract void initView(Intent intent, Bundle state);

    /* 初始Data */
    protected abstract void initData(Intent intent, Bundle state);

    /* 注销 */
    protected abstract void onFinish(Bundle state);

    public void pushApi(Call<Result> api) {
        if (api == null) return;
        apiList.add(api);
    }

    public void pushBus(int event, Observable bus) {
        if (bus == null) return;
        busList.add(new RxRegister(event, bus));
    }

    public MaterialDialog getLoading(boolean cancelable) {
        if (mLoading == null) {
            MaterialDialog.Builder builder = DialogHelper.getBuild(mActivity)
                    .cancelable(cancelable)
                    .canceledOnTouchOutside(false)
                    .content(R.string.please_wait)
                    .progress(true, 0)
                    .progressIndeterminateStyle(false);
            mLoading = builder.build();
            DialogHelper.setAnim(mLoading);
        }
        mLoading.setContent(R.string.please_wait);
        return mLoading;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mActivity = this;
        InputUtils.initActivity(this); // 软键盘
        ScreenUtils.requestPortrait(this); // 竖屏
        BarUtils.requestNoTitle(this); // noTitle
        initTransAnim(this); //  过渡动画
        this.setTheme(ThemeHelper.getTheme());
        super.onCreate(savedInstanceState);
        mFragmentManager = getSupportFragmentManager();
        mRootViewId = getView(getIntent());
        if (mRootViewId != 0) {
            setContentView(mRootViewId);
        }
    }

    /* setContentView()或addContentView()后调用,view只是加载出来，没有实例化.
       为了页面的加载速度，不要在setContentView前做过多的操作 */
    @Override
    public void onContentChanged() {
        super.onContentChanged();
        // 每次setContentView之后都要bind一下
        mUnBinder = ButterKnife.bind(this);
        // 二次setContentView之后控件是以前view的，所以要重新实例化一次
        initView(getIntent(), null);
        // 二次setContentView的话，可以不用获取数据 只加载数据
        if (!isLoad) {
            isLoad = true;
            initData(getIntent(), null);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        PermUtils.onRequestPermissionsResult(requestCode, permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /*
     * DecorView在这里才会有params,viewGroup在这里才能add
     * Window是WindowManager最顶层的视图，PhoneWindow是Window的唯一实现类
     * DecorView是window下的子视图,是所有应用窗口(Activity界面)的根View
     */
    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        // 控制DecorView的大小来控制activity的大小，可做窗口activity
        //mRootView = getWindow().getDecorView();
        // setFinishOnTouchOutside(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isFinishing()) {
            onFinish(null);
            // 取消网络操作
            for (Call<Result> api : apiList) {
                RetrofitHelper.cancel(api);
            }
            // 注销bus
            for (RxRegister bus : busList) {
                if (bus == null) continue;
                RxBus.unregister(bus.getEvent(), bus.getOb());
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //if (mUnBinder != null) {
        //    mUnBinder.unbind();
        //    mUnBinder = null;
        //}
    }

    /* 触摸事件 */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        View focus = this.getCurrentFocus();
        if (null != focus) { // 点击屏幕空白区域隐藏软键盘
            return InputUtils.hideSoftInput(focus);
        }
        return super.onTouchEvent(event);
    }

    /* 手机返回键 */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            onBackPressed();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /* 菜单事件 */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: // 返回键
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Stack<Activity> stack = ActivityStack.getStack();
        if (stack.size() <= 1 || this.getCls() == HomeActivity.class) {
            long nowTime = DateUtils.getCurrentLong();
            if (nowTime - mLastExitTime > 2000) { // 第一次按
                ToastUtils.show(getString(R.string.press_again_exit));
            } else { // 返回键连按两次
                AppUtils.appExit();
                //super.onBackPressed();
            }
            mLastExitTime = nowTime;
        } else {
            super.onBackPressed();
        }
    }

    /**
     * @return 获取当前类
     */
    @SuppressWarnings("unchecked")
    public Class<T> getCls() {
        Type type = this.getClass().getGenericSuperclass();
        return (Class<T>) (((ParameterizedType) (type)).getActualTypeArguments()[0]);
    }

    /*
     * activity过渡动画初始化, 要在setContentView之前调用
     * 这里设置的是5.0以上的过渡动画,需要ActivityTrans中的跳转方式才会起效
     * 5.0以下的过渡动画已在themes里添加
     */
    private void initTransAnim(Activity activity) {
        Window window = activity.getWindow();
        window.requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        // 压栈
        window.setEnterTransition(new Fade(0)); // 下一个activity进场
        window.setExitTransition(new Fade(0)); // 当前activity向后时退场
        // 弹栈
        //window.setReenterTransition(new Fade(0)); // 上一个activity进场
        //window.setReturnTransition(new Fade(0)); // 当前activity向前时退场
        // fresco动画
        //getWindow().setSharedElementEnterTransition(DraweeTransition.createTransitionSet(ScalingUtils.ScaleType.CENTER_CROP, ScalingUtils.ScaleType.FIT_CENTER)); // 进入
        //getWindow().setSharedElementReturnTransition(DraweeTransition.createTransitionSet(ScalingUtils.ScaleType.FIT_CENTER, ScalingUtils.ScaleType.CENTER_CROP)); // 返回
    }

}
